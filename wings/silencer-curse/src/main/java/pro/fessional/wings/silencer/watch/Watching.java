package pro.fessional.wings.silencer.watch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AOP-based, stopwatch timing of methods
 *
 * @author trydofor
 * @see pro.fessional.mirana.time.StopWatch
 * @since 2022-11-21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Watching {
    /**
     * watch name. when empty, AOP automatically set
     */
    String value() default "";

    /**
     * milliseconds of the threshold, -1 means turn off, 0 is automatically config
     */
    long threshold() default 0;
}
