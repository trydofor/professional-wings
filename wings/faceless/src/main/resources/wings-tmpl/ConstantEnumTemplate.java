// HI-MEEPO
// RNA:USE /pro.fessional.wings.faceless.enums.templet/enum-package/
package pro.fessional.wings.faceless.enums.templet;


import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.faceless.enums.ConstantEnum;
import pro.fessional.wings.faceless.enums.StandardI18nEnum;

// RNA:USE /2019-09-17/now.date/

/**
 * @author trydofor
 * @since 2019-09-17
 */
// RNA:USE /ConstantEnumTemplate/enum-class/*
public enum ConstantEnumTemplate implements ConstantEnum, StandardI18nEnum {

    // RNA:EACH /1/enum-items/enum
    // RNA:USE /SUPER/enum.name/*
    // RNA:USE /1020100/enum.id/
    // RNA:USE /standard_language/enum.code/fd
    // RNA:USE /标准语言/enum.hint/
    // RNA:USE /模板路径/enum.info/
    SUPER(1020100, "standard_language", "标准语言", "模板路径"),
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

    ConstantEnumTemplate(int id, String code, String hint, String info) {
        this.id = id;
        this.code = code;
        this.hint = hint;
        this.info = info;
        this.ukey = useIdAsKey ? "id" + id : code;
        this.rkey = "sys_constant_enum.hint." + ukey;
    }

    @Override
    public int getId() {
        return id;
    }

    // RNA:USE /standard_language/enum-type/
    @Override
    public @NotNull String getType() {
        return "standard_language";
    }

    @Override
    public @NotNull String getInfo() {
        return info;
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
}
