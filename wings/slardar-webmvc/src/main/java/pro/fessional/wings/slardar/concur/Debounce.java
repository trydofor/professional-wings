package pro.fessional.wings.slardar.concur;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Execute first and debounce later, the same session can have only one executing call in the debouncing time.
 *
 * @author trydofor
 * @since 2022-05-29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Debounce {

    /**
     * Whether to wait and reuse the previous request result or return it directly.
     */
    boolean reuse() default false;

    /**
     * Interval of debounce waiting in ms
     */
    long waiting() default 500;

    /**
     * Whether the combination key contains the sessionId
     */
    boolean session() default true;

    /**
     * Whether the combination key contains method
     */
    boolean method() default true;

    /**
     * Whether the combination key contains querystring
     */
    boolean query() default true;

    /**
     * Header names contained in the combination key
     */
    String[] header() default {};

    /**
     * Whether the combination key contains the md5sum or length of the body.
     * If request support wings reuse stream, then use md5, otherwise take length
     */
    boolean body() default false;
}
