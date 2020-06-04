package pro.fessional.wings.faceless.enums;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.wings.faceless.enums.standard.StandardLanguage;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2019-09-17
 */
public class EnumUtil {

    @Nullable
    public static <T extends ConstantEnum> T idOrNull(T[] es, Long id) {
        if (es == null || id == null) return null;
        for (T e : es) {
            if (id.equals(e.getId())) return e;
        }
        return null;
    }

    @NotNull
    public static <T extends ConstantEnum> T idOrThrow(T[] es, Long id) {
        T t = idOrNull(es, id);
        if (t == null) {
            throw new IllegalArgumentException("can not found Enum by id=" + id);
        } else {
            return t;
        }
    }

    @NotNull
    public static <T extends ConstantEnum> T idOrThrowInfo(T[] es, Long id, String tipInfo) {
        T t = idOrNull(es, id);
        if (t == null) {
            throw new IllegalArgumentException("can not found Enum by id=" + id + "[提示：" + tipInfo + "]");
        } else {
            return t;
        }
    }

    @NotNull
    public static <T extends ConstantEnum> T idOrElse(T[] es, Long id, T el) {
        T t = idOrNull(es, id);
        return t == null ? el : t;
    }

    // ///////////////

    @Nullable
    public static <T extends Enum<?>> T nameOrNull(T[] es, String name) {
        if (es == null || name == null) return null;
        for (T e : es) {
            if (e.name().equalsIgnoreCase(name)) return e;
        }
        return null;
    }

    @NotNull
    public static <T extends Enum<?>> T nameOrThrow(T[] es, String name) {
        T t = nameOrNull(es, name);
        if (t == null) {
            throw new IllegalArgumentException("can not found Enum by name=" + name);
        } else {
            return t;
        }
    }

    @NotNull
    public static <T extends Enum<?>> T nameOrThrowInfo(T[] es, String name, String tipInfo) {
        T t = nameOrNull(es, name);
        if (t == null) {
            throw new IllegalArgumentException("can not found Enum by name=" + name + "[提示：" + tipInfo + "]");
        } else {
            return t;
        }
    }

    @NotNull
    public static <T extends Enum<?>> T nameOrElse(T[] es, String name, T el) {
        T t = nameOrNull(es, name);
        return t == null ? el : t;
    }

    // ///////////////

    @Nullable
    public static <T extends CodeEnum> T codeOrNull(T[] es, String code) {
        if (es == null || code == null) return null;
        for (T e : es) {
            if (e.getCode().equalsIgnoreCase(code)) return e;
        }
        return null;
    }

    @NotNull
    public static <T extends CodeEnum> T codeOrThrow(T[] es, String code) {
        T t = codeOrNull(es, code);
        if (t == null) {
            throw new IllegalArgumentException("can not found Enum by code=" + code);
        } else {
            return t;
        }
    }

    @NotNull
    public static <T extends CodeEnum> T codeOrElse(T[] es, String name, T el) {
        T t = codeOrNull(es, name);
        return t == null ? el : t;
    }

    // ///////////////

    @SafeVarargs
    public static <T extends CodeEnum> boolean codeIn(String code, T... es) {
        if (code == null || es == null) return false;
        for (T e : es) {
            if (e.getCode().equalsIgnoreCase(code)) return true;
        }
        return false;
    }

    @SafeVarargs
    public static <T extends Enum<?>> boolean nameIn(String name, T... es) {
        if (name == null || es == null) return false;
        for (T e : es) {
            if (e.name().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    @SafeVarargs
    public static <T extends ConstantEnum> boolean idIn(Long id, T... es) {
        if (id == null || es == null) return false;
        for (T e : es) {
            if (id.equals(e.getId())) return true;
        }
        return false;
    }

    // ///////////////

    @Nullable
    public static StandardLanguage langOrNull(String str) {
        if (str == null) return null;
        if (str.indexOf('-') >= 0) {
            str = str.replace('-', '_');
        }
        str = str.toLowerCase();
        StandardLanguage winLang = null;
        int maxScore = 0;
        for (StandardLanguage e : StandardLanguage.values()) {
            String code = e.getCode();
            if (code.equalsIgnoreCase(str)) return e;
            //
            int scr = e.similar(str);
            if (scr > maxScore) {
                maxScore = scr;
                winLang = e;
            }
        }
        return winLang;
    }

    @NotNull
    public static StandardLanguage langOrThrow(String str) {
        StandardLanguage t = langOrNull(str);
        if (t == null) {
            throw new IllegalArgumentException("can not found StandardLanguage by str=" + str);
        } else {
            return t;
        }
    }

    @NotNull
    public static StandardLanguage langOrElse(String str, StandardLanguage el) {
        StandardLanguage t = langOrNull(str);
        return t == null ? el : t;
    }


    @Nullable
    public static StandardLanguage langOrNull(Locale lcl) {
        if (lcl == null) return null;
        for (StandardLanguage lang : StandardLanguage.values()) {
            if (lang.getLocale().equals(lcl)) return lang;
        }
        return langOrNull(lcl.getLanguage() + "_" + lcl.getCountry());
    }

    @NotNull
    public static StandardLanguage langOrThrow(Locale lcl) {
        StandardLanguage t = langOrNull(lcl);
        if (t == null) {
            throw new IllegalArgumentException("can not found StandardLanguage by lcl=" + lcl);
        } else {
            return t;
        }
    }

    @NotNull
    public static StandardLanguage langOrElse(Locale lcl, StandardLanguage el) {
        StandardLanguage t = langOrNull(lcl);
        return t == null ? el : t;
    }
}
