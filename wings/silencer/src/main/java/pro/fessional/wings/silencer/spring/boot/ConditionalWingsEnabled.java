package pro.fessional.wings.silencer.spring.boot;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * enhanced `@Conditional(WingsEnabledCondition.class)` to dynamically
 * disable `@Configuration`, `@Bean` and `@Component` by properties.
 *
 * `true` only if `this && and1 && and2 && !not1 && !not2`
 *
 * the key priority from high to low
 * - qualified-key = `prefix` + `ClassName` + `methodName`?
 * - absolute-key = `abs()`
 * - relative-key = `prefix` + `key()`
 * - default = `value()`
 * </pre>
 *
 * @author trydofor
 * @see WingsEnabledCondition
 * @since 2023-11-17
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(WingsEnabledCondition.class)
public @interface ConditionalWingsEnabled {

    /**
     * absolute-key, without prefix, priority lower then qualified-key
     */
    String abs() default "";

    /**
     * relative-key, with prefix, priority lower then absolute-key
     */
    String key() default "";

    /**
     * default value, the lowest priority, if no property found
     */
    boolean value() default true;

    /**
     * narrow this and others properties (or annotation).
     * true only if `this && and1 && and2 && !not1 && !not2`
     */
    Class<?>[] and() default {};

    /**
     * narrow this and not others properties (or annotation).
     * true only if `this && and1 && and2 && !not1 && !not2`
     */
    Class<?>[] not() default {};
}
