/* HI-MEEPO */
/* RNA:USE /pro.fessional.wings.faceless.enums.tmpl/enum-package/ */
package pro.fessional.wings.faceless.enums.tmpl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.i18n.LocaleResolver;
import pro.fessional.wings.faceless.enums.StandardLanguageEnum;

import java.util.Locale;

/* RNA:USE /2019-09-17/now.date/ */
/**
 * language + "_" + country，使用`_`分隔，zh_CN，在解析中，也支持zh-CN
 *
 * @author trydofor
 * @see Locale#toString()
 * @since 2019-09-17
 */
/* RNA:USE /StandardLanguageTemplate/enum-class/* */
public enum StandardLanguageTemplate implements StandardLanguageEnum {

    /* RNA:EACH /1/enum-items/enum */
    /* RNA:USE /SUPER/enum.name/* */
    /* RNA:USE /1020100/enum.id/ */
    /* RNA:USE /standard_language/enum.code/ */
    /* RNA:USE /标准语言/enum.desc/ */
    /* RNA:USE /模板路径/enum.info/ */
    SUPER(1020100, "standard_language", "标准语言", "模板路径"),
    /* RNA:DONE enum */
    ;
    /* RNA:EACH /1/enum-items/enum */
    public static final String $SUPER = SUPER.code;
    /* RNA:DONE enum */

    /* RNA:USE /false/enum-idkey/ */
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

    /* RNA:USE /standard_language/enum-type/ */
    @Override
    public @NotNull String getType() {
        return "standard_language";
    }

    @Override
    public @NotNull String getInfo() {
        return info;
    }

    public String getDesc() {
        return desc;
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
        return "desc";
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
        return desc;
    }

    @Override
    public @NotNull String getI18nCode() {
        return rkey;
    }
}
