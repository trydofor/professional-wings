package pro.fessional.wings.faceless.enums;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2020-06-11
 */
public interface StandardLanguageEnum extends ConstantEnum, StandardI18nEnum {
    /**
     * 转变为Locale
     */
    Locale toLocale();
}
