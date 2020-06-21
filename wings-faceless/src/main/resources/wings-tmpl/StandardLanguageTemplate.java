package pro.fessional.wings.faceless.enums.tmpl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.i18n.LocaleResolver;
import pro.fessional.wings.faceless.enums.StandardLanguageEnum;

import java.util.Locale;

/**
 * language + "_" + country，使用`_`分隔，zh_CN，在解析中，也支持zh-CN
 *
 * @author trydofor
 * @see Locale#toString()
 * @since 2019-09-17
 */
public enum StandardLanguageTemplate implements StandardLanguageEnum {

    SUPER(1010100, "ConstantEnumTemplate", "性别", "性别");

    public static final boolean useIdAsKey = false;

    private final int id;
    private final String code;
    private final String desc;
    private final String info;

    private final String ukey;
    private final String rkey;
    private final Locale locl;

    StandardLanguageTemplate(int id, String code, String desc, String info) {
        this.id = id;
        this.code = code;
        this.desc = desc;
        this.info = info;
        this.ukey = useIdAsKey ? "id" + id : code;
        this.rkey = "sys_constant_enum.desc." + ukey;
        this.locl = LocaleResolver.locale(code);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public @NotNull String getType() {
        return "{sys_constant_enum.type}";
    }

    @Override
    public @NotNull String getInfo() {
        return info;
    }

    public String getDesc() {
        return desc;
    }

    //

    @Override
    public Locale toLocale() {
        return locl;
    }

    //
    @Override
    public @NotNull String getBase() {
        return "sys_constant_enum";
    }

    @Override
    public @NotNull String getKind() {
        return "desc";
    }

    @Override
    public @NotNull String getUkey() {
        return ukey;
    }

    //
    @Override
    public @NotNull String getCode() {
        return code;
    }

    @Override
    public @NotNull String getHint() {
        return desc;
    }

    @Override
    public @NotNull String getI18nCode() {
        return rkey;
    }
}
