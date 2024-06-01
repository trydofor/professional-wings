package pro.fessional.wings.faceless.enums;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.i18n.LocaleResolver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author trydofor
 * @since 2020-06-11
 */
public class LanguageEnumUtil extends ConstantEnumUtil {

    private static final Map<Integer, Locale> LocaleMap = new HashMap<>();

    public static void register(StandardLanguageEnum en) {
        final Locale neu = en.toLocale();
        final Locale old = LocaleMap.put(en.getId(), neu);
        if (old != null && !old.equals(neu)) {
            throw new IllegalArgumentException("need only one Locale. old=" + old.toLanguageTag() + ", new=" + neu.toLanguageTag() + ", id=" + en.getId());
        }

    }

    public static void register(StandardLanguageEnum... enums) {
        for (StandardLanguageEnum en : enums) {
            register(en);
        }
    }

    @Nullable
    public static Locale localeOrNull(Integer locale) {
        if (locale == null) return null;
        return LocaleMap.get(locale);
    }

    @NotNull
    public static Locale localeOrThrow(Integer locale) {
        Locale t = localeOrNull(locale);
        if (t == null) {
            throw new IllegalArgumentException("can not found Locale by locale=" + locale);
        }
        else {
            return t;
        }
    }

    @NotNull
    public static Locale localeOrHint(Integer locale, String hint) {
        Locale t = localeOrNull(locale);
        if (t == null) {
            throw new IllegalArgumentException(hint);
        }
        else {
            return t;
        }
    }

    @NotNull
    public static Locale localeOrElse(Integer locale, Locale el) {
        Locale t = localeOrNull(locale);
        return t == null ? el : t;
    }

    @Nullable
    public static Integer localeOrNull(Locale locale) {
        if (locale == null) return null;
        for (Map.Entry<Integer, Locale> en : LocaleMap.entrySet()) {
            if (en.getValue().equals(locale)) return en.getKey();
        }
        return null;
    }

    @NotNull
    public static Integer localeOrThrow(Locale locale) {
        Integer t = localeOrNull(locale);
        if (t == null) {
            throw new IllegalArgumentException("can not found Locale by locale=" + locale);
        }
        else {
            return t;
        }
    }

    @NotNull
    public static Integer localeOrHint(Locale locale, String hint) {
        Integer t = localeOrNull(locale);
        if (t == null) {
            throw new IllegalArgumentException(hint);
        }
        else {
            return t;
        }
    }

    @NotNull
    public static Integer localeOrElse(Locale locale, Integer el) {
        Integer t = localeOrNull(locale);
        return t == null ? el : t;
    }

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
        }
        else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends StandardLanguageEnum> T localeOrThrow(Locale locale, T... es) {
        T t = localeOrNull(locale, es);
        if (t == null) {
            throw new IllegalArgumentException("can not found StandardLanguageEnum by locale=" + locale);
        }
        else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends StandardLanguageEnum> T localeOrHint(String locale, String hint, T... es) {
        T t = localeOrNull(locale, es);
        if (t == null) {
            throw new IllegalArgumentException(hint);
        }
        else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends StandardLanguageEnum> T localeOrHint(Locale locale, String hint, T... es) {
        T t = localeOrNull(locale, es);
        if (t == null) {
            throw new IllegalArgumentException(hint);
        }
        else {
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
