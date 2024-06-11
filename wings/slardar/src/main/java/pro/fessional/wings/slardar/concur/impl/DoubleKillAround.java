package pro.fessional.wings.slardar.concur.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.lock.ArrayKey;
import pro.fessional.mirana.lock.JvmStaticGlobalLock;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.slardar.concur.DoubleKill;
import pro.fessional.wings.slardar.concur.DoubleKillException;
import pro.fessional.wings.slardar.concur.ProgressContext;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.security.DefaultUserId;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;

/**
 * Use in single JVM and the key without serialization/deserialization
 *
 * @author trydofor
 * @since 2021-03-09
 */
@Aspect
@Order(WingsOrdered.Lv3Service)
@Slf4j
public class DoubleKillAround {

    private final Evaluator evaluator = new Evaluator();

    @Setter(onMethod_ = { @Autowired(required = false) })
    protected BeanFactory beanFactory;

    @Setter(onMethod_ = { @Autowired, @Qualifier(AsyncAnnotationBeanPostProcessor.DEFAULT_TASK_EXECUTOR_BEAN_NAME) })
    protected Executor asyncExecutor;

    @Around("@annotation(pro.fessional.wings.slardar.concur.DoubleKill)")
    public Object doubleKill(ProceedingJoinPoint joinPoint) throws Throwable {
        final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        final DoubleKill doubleKill = method.getAnnotation(DoubleKill.class);

        final Long uid = doubleKill.principal() ? TerminalContext.get(false).getUserId() : DefaultUserId.Guest;
        final Object[] args = joinPoint.getArgs();

        final String keyStr = doubleKill.value();
        final ArrayKey arrKey;
        if (StringUtils.hasText(keyStr)) {
            arrKey = new ArrayKey(method, uid, keyStr);
        }
        else {
            final String spelKey = doubleKill.expression();
            if (StringUtils.hasText(spelKey)) {
                final Root root = new Root(method, args, joinPoint.getTarget());
                final EvaluationContext ctx = evaluator.createContext(root, beanFactory);
                final AnnotatedElementKey methodKey = new AnnotatedElementKey(root.method, root.targetClass);
                final Object key = evaluator.key(spelKey, methodKey, ctx);
                if (doubleKill.checkNull()) {
                    if (key == null) {
                        IllegalStateException ex = new IllegalStateException("expression result is null, SpEL=" + spelKey);
                        log.error("please check the expression. use the static key or expressionCheck=false to skip checking", ex);
                        throw ex;
                    }
                    else if (key instanceof String str) {
                        if (str.isEmpty()) {
                            IllegalStateException ex = new IllegalStateException("expression result is empty, SpEL=" + spelKey);
                            log.error("please check the expression. use the static key or expressionCheck=false to skip checking", ex);
                            throw ex;
                        }
                        else if (str.contains("null")) {
                            IllegalStateException ex = new IllegalStateException("expression result contains 'null', SpEL=" + spelKey + ", result=" + str);
                            log.error("please check the expression. use the static key or expressionCheck=false to skip checking", ex);
                            throw ex;
                        }
                    }
                }
                arrKey = new ArrayKey(method, uid, key);
            }
            else {
                if (args == null || args.length == 0) {
                    arrKey = new ArrayKey(method, uid);
                }
                else {
                    arrKey = new ArrayKey(method, uid, args);
                }
            }
        }

        final long now = ThreadNow.millis();
        final Lock lock = JvmStaticGlobalLock.get(arrKey);
        final int ttl = doubleKill.progress();
        if (lock.tryLock()) {
            try {
                final ProgressContext.Bar bar = ProgressContext.gen(arrKey, now, ttl);
                if (doubleKill.async()) {

                    asyncExecutor.execute(() -> {
                        try {
                            syncProceed(joinPoint, bar);
                        }
                        catch (Throwable e) {
                            // ignore in async, get it from bar
                        }
                    });
                    throw new DoubleKillException(bar.getKey(), bar.getStarted(), now);
                }
                else {
                    return syncProceed(joinPoint, bar);
                }
            }
            finally {
                lock.unlock();
            }
        }
        else {
            final ProgressContext.Bar bar = ProgressContext.get(arrKey);
            if (bar == null) {
                throw new DoubleKillException("", 0); // Never here, Defensive
            }
            else {
                throw new DoubleKillException(bar.getKey(), bar.getStarted(), now);
            }
        }
    }

    private Object syncProceed(ProceedingJoinPoint joinPoint, ProgressContext.Bar bar) throws Throwable {
        try {
            final Object r = joinPoint.proceed();
            bar.ok(r);
            return r;
        }
        catch (Throwable e) {
            bar.fail(e);
            throw e;
        }
    }

    // //////
    @Getter
    public static class Root {
        private final Method method;
        private final Object[] args;
        private final Object target;
        private final Class<?> targetClass;

        public Root(Method method, Object[] args, Object target) {
            this.method = method;
            this.args = args;
            this.target = target;
            this.targetClass = target.getClass();
        }

        public String getMethodName() {
            return method.getName();
        }

    }

    public static class Evaluator extends CachedExpressionEvaluator {

        private final Map<ExpressionKey, Expression> keyCache = new ConcurrentHashMap<>(64);

        public EvaluationContext createContext(Root root, @Nullable BeanFactory beanFactory) {

            final MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(root, root.method, root.args, getParameterNameDiscoverer());
            if (beanFactory != null) {
                context.setBeanResolver(new BeanFactoryResolver(beanFactory));
            }
            return context;
        }

        @Nullable
        public Object key(String keyExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
            return getExpression(this.keyCache, methodKey, keyExpression)
                .getValue(evalContext);
        }
    }
}
