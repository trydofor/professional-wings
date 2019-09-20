package pro.fessional.wings.silencer.jackson;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author trydofor
 * @since 2019-09-19
 */

@Retention(RetentionPolicy.RUNTIME)
//@JacksonAnnotationsInside
//@JsonSerialize(using = I18nStringSerializer.class)
public @interface JsonI18nString {
    /**
     * enable 1i8n serials
     *
     * @return true if enable
     */
    boolean value() default true;
}
