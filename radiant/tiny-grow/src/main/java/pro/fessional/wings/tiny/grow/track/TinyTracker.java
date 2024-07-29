package pro.fessional.wings.tiny.grow.track;

import org.intellij.lang.annotations.Language;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * data tracking by AOP
 *
 * @author trydofor
 * @since 2024-07-24
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TinyTracker {

    /**
     * track key instead of AOP auto
     */
    String key() default "";

    /**
     * track ref instead of AOP auto
     */
    String ref() default "";

    /**
     * <pre>
     * The name of this AOP object's method that will mix the `TinyTracking` after return/throw,
     * its parameters is the `TinyTracking` prepended to the parameters of the AOP method.
     *
     * * saveOrder(long, Order) - the AOP method
     * * saveOrder(TinyTracking, long, Order) - same as AOP method if mix is empty
     * * saveOrderMix(TinyTracking, long, Order) - if mix is saveOrderMix
     * </pre>
     *
     * @see TinyTracking
     */
    String mix() default "";

    /**
     * omit the property if it is instance of
     */
    Class<?>[] omitClass() default {};

    /**
     * omit the property if its name equals
     */
    String[] omitEqual() default {};

    /**
     * omit the property if its name match regex
     */
    @Language("regexp")
    String[] omitRegex() default {};
}
