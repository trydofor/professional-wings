package pro.fessional.wings.slardar.concur.impl;

import lombok.Setter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.flow.DoubleKillException;
import pro.fessional.mirana.lock.JvmStaticGlobalLock;
import pro.fessional.wings.slardar.concur.DoubleKill;
import pro.fessional.wings.slardar.security.SecurityContextUtil;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

/**
 * @author trydofor
 * @since 2021-03-09
 */
@Aspect
public class DoubleKillAround {

    private final Evaluator evaluator = new Evaluator();

    @Setter(onMethod_ = {@Autowired(required = false)})
    private BeanFactory beanFactory;

    @Around("@annotation(doubleKill)")
    public Object doubleKill(ProceedingJoinPoint joinPoint, DoubleKill doubleKill) throws Throwable {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        final Object principal;

        if (doubleKill.principal()) {
            final Object p = SecurityContextUtil.getPrincipal();
            if (p == null) {
                principal = Boolean.TRUE;
            } else {
                principal = p;
            }
        } else {
            principal = Boolean.FALSE;
        }
        final Object[] args = joinPoint.getArgs();

        final String keyStr = doubleKill.value();
        final Object[] uniqKey;
        if (StringUtils.hasText(keyStr)) {
            uniqKey = new Object[]{method, principal, keyStr};
        } else {
            final String spelKey = doubleKill.expression();
            if (StringUtils.hasText(spelKey)) {
                final Root root = new Root(method, args, joinPoint.getTarget());
                final EvaluationContext ctx = evaluator.createContext(root, beanFactory);
                final AnnotatedElementKey methodKey = new AnnotatedElementKey(root.method, root.targetClass);
                final Object key = evaluator.key(spelKey, methodKey, ctx);
                uniqKey = new Object[]{method, principal, key};
            } else {
                if (args == null || args.length == 0) {
                    uniqKey = new Object[]{method, principal};
                } else {
                    final int ln = 2;
                    uniqKey = new Object[args.length + ln];
                    uniqKey[0] = method;
                    uniqKey[1] = principal;
                    System.arraycopy(args, 0, uniqKey, ln, args.length);
                }
            }
        }

        final Lock lock = JvmStaticGlobalLock.get(uniqKey);
        if (lock.tryLock()) {
            try {
                return joinPoint.proceed();
            } finally {
                lock.unlock();
            }
        } else {
            throw new DoubleKillException();
        }
    }

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

        public Method getMethod() {
            return method;
        }

        public String getMethodName() {
            return method.getName();
        }

        public Object[] getArgs() {
            return args;
        }

        public Object getTarget() {
            return target;
        }

        public Class<?> getTargetClass() {
            return targetClass;
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
            return getExpression(this.keyCache, methodKey, keyExpression).getValue(evalContext);
        }
    }
}
