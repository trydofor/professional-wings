package pro.fessional.wings.tiny.task.schedule.exec;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.support.AopUtils;
import pro.fessional.wings.slardar.jackson.JacksonHelper;
import pro.fessional.wings.tiny.task.schedule.help.TaskerHelper;

import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author trydofor
 * @since 2022-12-12
 */
@Getter
@Slf4j
public class TaskerExec {

    @NotNull
    protected final Class<?> beanClass;
    @NotNull
    protected final Object beanObject;
    @NotNull
    protected final Method beanMethod;
    protected final Class<?> paraClass;

    @NotNull
    @Setter
    protected Function<Object, String> paraEncoder = JacksonHelper::string;
    @NotNull
    @Setter
    protected BiFunction<String, Class<?>, Object> paraDecoder = JacksonHelper::object;

    public TaskerExec(@NotNull Object beanObject, @NotNull Method beanMethod) {
        this(AopUtils.getTargetClass(beanObject), beanObject, beanMethod);
    }

    public TaskerExec(@NotNull Class<?> beanClass, @NotNull Object beanObject, @NotNull Method beanMethod) {
        final Class<?>[] pt = beanMethod.getParameterTypes();
        if (pt.length == 0) {
            paraClass = null;
        }
        else if (pt.length == 1) {
            paraClass = pt[0];
        }
        else {
            throw new IllegalArgumentException("must 0 or 1 param, method=" + beanMethod.getName());
        }
        this.beanClass = beanClass;
        this.beanObject = beanObject;
        this.beanMethod = beanMethod;
    }

    public Object decodePara(String para) {
        if (para == null) return null;
        return paraDecoder.apply(para, paraClass);
    }

    public String encodePara(Object para) {
        if (para == null) return null;
        return paraEncoder.apply(para);
    }

    /**
     * The format is name:Class, which matches name first, then Class.
     */
    public boolean accept(String token) {
        return TaskerHelper.acceptToken(beanClass, beanMethod, token);
    }

    /**
     * Whether the bean is accepted
     */
    public boolean accept(Object bean) {
        return TaskerHelper.acceptBean(beanClass, beanObject, bean);
    }

    @SneakyThrows
    public Object invoke(Object arg, boolean encodedString) {
        if (paraClass == null) {
            return beanMethod.invoke(beanObject);
        }
        else {
            if (encodedString) {
                arg = decodePara((String) arg);
            }
            return beanMethod.invoke(beanObject, arg);
        }
    }
}
