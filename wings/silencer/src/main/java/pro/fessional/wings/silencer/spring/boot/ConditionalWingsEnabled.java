package pro.fessional.wings.silencer.spring.boot;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * use `@Conditional(WingsEnabledCondition.class)` to dynamically
 * enable or disable `@Configuration`, `@Bean` and `@Component` by properties.
 * <p>
 * true only if `this && and1 && and2 && !not1 && !not2`
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
     * A prefix that should be applied to the property.
     * inherit from the EnclosingClass non-empty prefix
     */
    String prefix() default "";

    /**
     * without prefix, absolute key of properties
     */
    String absKey() default "";

    /**
     * with prefix, customize key instead of ClassName-qualified
     */
    String key() default "";

    /**
     * default value, if no property found
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
