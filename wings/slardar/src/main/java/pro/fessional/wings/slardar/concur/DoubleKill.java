package pro.fessional.wings.slardar.concur;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * Note: When executing async and using with AOP annotations such as @Cacheable,
 * make sure that DK is executed first. Otherwise, the result of the async execution
 * cannot be processed correctly. If you can not guarantee the first execution,
 * do not use at the same time.
 *
 * Repeated interception within the JVM will throw a no-stack exception to stop the execution,
 * the caller needs to `catch` their own.
 * - DoubleKillException will `throw` except those in sync execution.
 *
 * </pre>
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
     * static key, just make sure it is unique within the method. higher priority than `expression` key
     */
    String value() default "";

    /**
     * Used in the same way as `key` of `@Cacheable`, empty by default, with all arguments.
     * If a static-key exists, the expression is omitted.
     * Beans can be obtained using `@beanName`.
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
     * Whether to use spring SecurityContextHolder.context.authentication.principal in keys
     */
    boolean principal() default true;

    /**
     * Whether to execute the method async, default sync.
     * If async, the execution progresses with the ReturnOrException.
     * Use spring @Async thread pool by default.
     */
    boolean async() default false;

    /**
     * The seconds of the execution message remain in the ProgressContext. default 300s
     */
    int progress() default 300;
}
