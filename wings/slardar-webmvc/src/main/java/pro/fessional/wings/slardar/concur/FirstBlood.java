package pro.fessional.wings.slardar.concur;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate RequestMapping that requires CAPTCHA support
 *
 * @author trydofor
 * @since 2021-03-10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FirstBlood {

    /**
     * The seconds between two requests triggering CAPTCHA, default 0, means CAPTCHA every time.
     */
    int first() default 0;


    /**
     * The seconds of CAPTCHA or disable the duration. In increments of 10s, no more than 1 day is recommended.
     */
    int blood() default 300;

    /**
     * Number of CAPTCHA retry, resend CAPTCHA when exceeded
     */
    int retry() default 1;


    /**
     * CAPTCHA scenarios, which are passed to the interceptor
     */
    String scene() default "";
}
