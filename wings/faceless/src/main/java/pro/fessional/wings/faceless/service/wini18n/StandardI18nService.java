package pro.fessional.wings.faceless.service.wini18n;

import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.wings.faceless.enums.StandardI18nEnum;
import pro.fessional.wings.faceless.enums.StandardLanguageEnum;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2020-06-11
 */
public interface StandardI18nService {

    /**
     * Reload all and return the count.
     */
    int reloadAll();

    /**
     * Reload by base and return the count.
     */
    int reloadBase(String base);

    /**
     * Load i18n message form sys_standard_i18n
     *
     * @param base table or package
     * @param kind column or java field
     * @param ukey unique key eg. id.###, type.code, enum
     * @param lang locale
     * @return i18n message
     */
    @Nullable
    String load(String base, String kind, String ukey, Locale lang);

    /**
     * Load i18n message by I18nEnum (base, kind, ukey)
     *
     * @param enu  I18nEnum
     * @param lang locale
     * @return i18n message
     */
    @Nullable
    default String load(StandardI18nEnum enu, StandardLanguageEnum lang) {
        if (enu == null || lang == null) return null;
        return load(enu, lang.toLocale());
    }

    /**
     * Load i18n message by I18nEnum (base, kind, ukey)
     *
     * @param enu  I18nEnum
     * @param lang locale
     * @return i18n message
     */
    @Nullable
    default String load(StandardI18nEnum enu, Locale lang) {
        if (enu == null || lang == null) return null;
        return load(enu.getBase(), enu.getKind(), enu.getUkey(), lang);
    }

    /**
     * Load i18n message by Enum (package, className, name)
     *
     * @param enu  enum
     * @param lang locale
     * @return i18n message
     */
    @Nullable
    default String loadEnum(Enum<?> enu, StandardLanguageEnum lang) {
        if (enu == null || lang == null) return null;
        return loadEnum(enu, lang.toLocale());
    }

    /**
     * Load i18n message by Enum (package, className, name)
     *
     * @param enu  enum
     * @param lang locale
     * @return i18n message
     */
    @Nullable
    default String loadEnum(Enum<?> enu, Locale lang) {
        if (enu == null || lang == null) return null;
        final Class<?> clz = enu.getDeclaringClass();
        final String base = clz.getPackageName();
        final String kind = clz.getName().substring(base.length() + 1);
        return load(base, kind, enu.name(), lang);
    }

    /**
     * Load i18n message by CodeEnum (package, className, getCode)
     *
     * @param enu  CodeEnum
     * @param lang locale
     * @return i18n message
     */
    @Nullable
    default String loadCode(CodeEnum enu, StandardLanguageEnum lang) {
        if (enu == null || lang == null) return null;
        return loadCode(enu, lang.toLocale());
    }

    /**
     * Load i18n message by CodeEnum (package, className, getCode)
     *
     * @param enu  CodeEnum
     * @param lang locale
     * @return i18n message
     */
    @Nullable
    default String loadCode(CodeEnum enu, Locale lang) {
        if (enu == null || lang == null) return null;
        final Class<?> clz = enu.getClass();
        final String base = clz.getPackageName();
        final String kind = clz.getName().substring(base.length() + 1);
        return load(base, kind, enu.getCode(), lang);
    }
}
