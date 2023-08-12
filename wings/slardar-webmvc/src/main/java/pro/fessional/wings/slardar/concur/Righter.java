package pro.fessional.wings.slardar.concur;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author trydofor
 * @since 2021-10-19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Righter {

    /**
     * Whether to force audit.
     * Write method suggests true, audit fails if header is missing.
     * Read method suggests false to provide data.
     */
    boolean value() default true;
}
