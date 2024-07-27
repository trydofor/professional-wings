package pro.fessional.wings.tiny.grow.track;

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
     * The name of this AOP object's method that will mix the `Tracking` after return/throw,
     * its parameters is the `Tracking` prepended to the parameters of the AOP method.
     *
     * * saveOrder(long, Order) - the AOP method
     * * saveOrder(Tracking, long, Order) - same as AOP method if mix is empty
     * * saveOrderMix(Tracking, long, Order) - if mix is saveOrderMix
     * </pre>
     * @see TinyTrackService.Tracking
     */
    String mix() default "";
}
