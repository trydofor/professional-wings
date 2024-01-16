// HI-MEEPO
// RNA:USE /pro.fessional.wings.faceless.enums.templet/enum-package/
package pro.fessional.wings.faceless.enums.templet;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.i18n.LocaleResolver;
import pro.fessional.wings.faceless.enums.StandardLanguageEnum;

import javax.annotation.processing.Generated;
import java.util.Locale;

// RNA:USE /2019-09-17/now.date/

/**
 * language + `_` + country, `_` delimited, `zh_CN`. For parsing, `zh-CN` is also supported.
 *
 * @author trydofor
 * @see Locale#toString()
 * @since 2019-09-17
 */
// RNA:USE /StandardLanguageTemplate/enum-class/*
@Generated("wings faceless codegen")
public enum StandardLanguageTemplate implements StandardLanguageEnum {

    // RNA:EACH /1/enum-items/enum
    // RNA:USE /SUPER/enum.name/*
    // RNA:USE /1020100/enum.id/
    // RNA:USE /standard_language/enum.code/fd
    // RNA:USE /Standard Language/enum.hint/
    // RNA:USE /Template Path/enum.info/
    SUPER(1020100, "standard_language", "Standard Language", "Template Path"),
    // RNA:DONE enum
    ;
    // RNA:EACH /1/enum-items/enum
    public static final String $SUPER = "standard_language";
    // RNA:DONE enum
    // DNA:END fd

    // RNA:USE /false/enum-idkey/
    public static final boolean useIdAsKey = false;
    private final int id;
    private final String code;
    private final String hint;
    private final String info;

    private final String ukey;
    private final String rkey;
    private final Locale locl;

    // RNA:USE /standard_language/enum-type/*
    StandardLanguageTemplate(int id, String code, String hint, String info) {
        this.id = id;
        this.code = code;
        this.hint = hint;
        this.info = info;
        this.ukey = "standard_language." + (useIdAsKey ? "id." + id : code);
        this.rkey = "sys_constant_enum.hint." + ukey;
        this.locl = LocaleResolver.locale(code);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public @NotNull String getType() {
        return "standard_language";
    }

    @Override
    public @NotNull String getInfo() {
        return info;
    }

    @Override
    public Locale toLocale() {
        return locl;
    }

    @Override
    public @NotNull String getBase() {
        return "sys_constant_enum";
    }

    @Override
    public @NotNull String getKind() {
        return "hint";
    }

    @Override
    public @NotNull String getUkey() {
        return ukey;
    }

    @Override
    public @NotNull String getCode() {
        return code;
    }

    @Override
    public @NotNull String getHint() {
        return hint;
    }

    @Override
    public @NotNull String getI18nCode() {
        return rkey;
    }

    @Nullable
    public static StandardLanguageTemplate valueOf(int id) {
        for (StandardLanguageTemplate v : StandardLanguageTemplate.values()) {
            if (id == v.id) return v;
        }
        return null;
    }

    @Contract("_, !null -> !null")
    public static StandardLanguageTemplate idOf(Integer id, StandardLanguageTemplate elz) {
        if (id == null) return elz;
        final int i = id;
        for (StandardLanguageTemplate v : StandardLanguageTemplate.values()) {
            if (i == v.id) return v;
        }
        return elz;
    }

    @Contract("_, !null -> !null")
    public static StandardLanguageTemplate codeOf(String code, StandardLanguageTemplate elz) {
        if (code == null) return elz;
        for (StandardLanguageTemplate v : StandardLanguageTemplate.values()) {
            if (code.equalsIgnoreCase(v.code)) return v;
        }
        return elz;
    }

    @Contract("_, !null -> !null")
    public static StandardLanguageTemplate nameOf(String name, StandardLanguageTemplate elz) {
        if (name == null) return elz;
        for (StandardLanguageTemplate v : StandardLanguageTemplate.values()) {
            if (name.equalsIgnoreCase(v.name())) return v;
        }
        return elz;
    }
}
