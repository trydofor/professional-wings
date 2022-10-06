package pro.fessional.wings.slardar.autodto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对I18nString和CharSequence进行国际化转换
 * `I18nString`类型会自动转换，使用@JsonI18nString关闭
 * `CharSequence`如果内部是i18nCode，使用@JsonI18nString开启。
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
