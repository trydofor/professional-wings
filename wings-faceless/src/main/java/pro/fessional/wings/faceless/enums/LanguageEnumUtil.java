package pro.fessional.wings.faceless.enums;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.i18n.LocaleResolver;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2020-06-11
 */
public class LanguageEnumUtil extends ConstantEnumUtil {

    @SafeVarargs
    @Nullable
    public static <T extends StandardLanguageEnum> T localeOrNull(String locale, T... es) {
        if (locale == null || es == null || es.length == 0) return null;
        return localeOrNull(LocaleResolver.locale(locale), es);
    }

    @SafeVarargs
    @Nullable
    public static <T extends StandardLanguageEnum> T localeOrNull(Locale locale, T... es) {
        if (locale == null || es == null || es.length == 0) return null;

        T rst = null;
        int max = 0;
        for (T e : es) {
            int cnt = 0;
            if (e.toLocale().getLanguage().equalsIgnoreCase(locale.getLanguage())) {
                cnt++;
            }
            if (e.toLocale().getCountry().equalsIgnoreCase(locale.getCountry())) {
                cnt++;
            }
            if (cnt > max) {
                max = cnt;
                rst = e;
            }
            if (max == 2) {
                break;
            }
        }

        return rst;
    }

    @SafeVarargs
    @NotNull
    public static <T extends StandardLanguageEnum> T localeOrThrow(String locale, T... es) {
        T t = localeOrNull(locale, es);
        if (t == null) {
            throw new IllegalArgumentException("can not found StandardLanguageEnum by locale=" + locale);
        } else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends StandardLanguageEnum> T localeOrThrow(Locale locale, T... es) {
        T t = localeOrNull(locale, es);
        if (t == null) {
            throw new IllegalArgumentException("can not found StandardLanguageEnum by locale=" + locale);
        } else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends StandardLanguageEnum> T localeOrHint(String locale, String hint, T... es) {
        T t = localeOrNull(locale, es);
        if (t == null) {
            throw new IllegalArgumentException(hint);
        } else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends StandardLanguageEnum> T localeOrHint(Locale locale, String hint, T... es) {
        T t = localeOrNull(locale, es);
        if (t == null) {
            throw new IllegalArgumentException(hint);
        } else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends StandardLanguageEnum> T localeOrElse(String locale, T el, T... es) {
        T t = localeOrNull(locale, es);
        return t == null ? el : t;
    }

    @SafeVarargs
    @NotNull
    public static <T extends StandardLanguageEnum> T localeOrElse(Locale locale, T el, T... es) {
        T t = localeOrNull(locale, es);
        return t == null ? el : t;
    }
}
