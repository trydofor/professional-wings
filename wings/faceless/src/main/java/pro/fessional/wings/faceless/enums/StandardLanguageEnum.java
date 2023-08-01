package pro.fessional.wings.faceless.enums;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2020-06-11
 */
public interface StandardLanguageEnum extends ConstantEnum, StandardI18nEnum {
    /**
     * Get or Convert to Locale
     */
    Locale toLocale();
}
