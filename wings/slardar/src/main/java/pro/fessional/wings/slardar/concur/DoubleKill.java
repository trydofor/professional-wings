package pro.fessional.wings.slardar.concur;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注意：异步执行时且同@Cacheable等AOP功能注解一起使用时，要保证DK最先被执行。
 * 否则异步执行的结果，不能被正确处理。若是不能保证最先执行，不要同时使用。
 * <p>
 * JVM内重复执行拦截，以抛出无栈异常终止执行，需要调用者自行catch。
 * - DoubleKillException 除同步执行中的调用，其他调用都throw。
 *
 * @author trydofor
 * @see DoubleKillException
 * @see org.springframework.cache.interceptor.CacheProxyFactoryBean
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
     * 使用方法同`@Cacheable`的`key`，默认空，使用全部参数。当有static-key时，expression无效。
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
     * can also be accessed by #name if that information is available.</li>
     * </ul>
     *
     * @return SpEL
     */
    String expression() default "";

    /**
     * 是否使用spring SecurityContextHolder.context.authentication.principal参与鉴别
     *
     * @return 是否参与
     */
    boolean principal() default true;

    /**
     * 是否异步执行该方法，默认同步。
     * 异步执行时，执行中都以ReturnOrException任务进度。
     * 默认使用spring @Async线程池
     *
     * @return 是否异步
     */
    boolean async() default false;

    /**
     * 执行信息在 ProgressContext 中存活的秒数
     *
     * @return 默认300秒
     */
    int progress() default 300;
}
