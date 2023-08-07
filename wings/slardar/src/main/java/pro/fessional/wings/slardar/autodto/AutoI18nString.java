package pro.fessional.wings.slardar.autodto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * Auto I18n Convert for I18nString and CharSequence
 * `I18nString` - auto convert, use @JsonI18nString to disable.
 * `CharSequence` - If contains i18nCode, use @JsonI18nString to enable.
 * </pre>
 *
 * @author trydofor
 * @since 2019-09-19
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
//@JacksonAnnotationsInside
//@JsonSerialize(using = I18nStringSerializer.class)
public @interface AutoI18nString {
    /**
     * enable 1i8n serials
     *
     * @return true if enable
     */
    boolean value() default true;
}
