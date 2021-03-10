package pro.fessional.wings.slardar.concur;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JVM内重复执行拦截，抛出 DoubleKillException终止执行
 * 需要注意的是
 *
 * @author trydofor
 * @see pro.fessional.mirana.flow.DoubleKillException
 * @since 2021-03-09
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DoubleKill {

    /**
     * static key，保证方法内唯一即可。优先级高于expression key
     *
     * @return key
     */
    String value() default "";

    /**
     * 是否使用spring SecurityContextHolder.context.authentication.principal参与鉴别
     *
     * @return 是否参与
     */
    boolean principal() default true;

    /**
     * 使用方法同`@Cacheable`的`key`，默认空，表示不使用。当有static-key时，expression无效。
     * 可以使用`@beanName`获得Bean
     * <p>
     * Spring Expression Language (SpEL) expression for computing the key dynamically
     * <ul>
     * <li>{@code #root.method}, {@code #root.target} for
     * references to the {@link java.lang.reflect.Method method}, target object respectively.</li>
     * <li>Shortcuts for the method name ({@code #root.methodName}) and target class
     * ({@code #root.targetClass}) are also available.
     * <li>Method arguments can be accessed by index. For instance the 1st argument
     * can be accessed via {@code #root.args[0]}, {@code #p0} or {@code #a0}. Arguments
     * can also be accessed by name if that information is available.</li>
     * </ul>
     *
     * @return SpEL
     */
    String expression() default "";
}
