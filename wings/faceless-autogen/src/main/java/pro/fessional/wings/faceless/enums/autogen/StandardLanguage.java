package pro.fessional.wings.faceless.enums.autogen;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.i18n.LocaleResolver;
import pro.fessional.wings.faceless.enums.StandardLanguageEnum;

import java.util.Locale;

/**
 * language + "_" + country，使用`_`分隔，zh_CN，在解析中，也支持zh-CN
 *
 * @author trydofor
 * @see Locale#toString()
 * @since 2022-10-03
 */
public enum StandardLanguage implements StandardLanguageEnum {

    SUPER(1020100, "standard_language", "标准语言", "classpath:/wings-tmpl/StandardLanguageTemplate.java"),
    AR_AE(1020101, "ar_AE", "阿拉伯联合酋长国", ""),
    DE_DE(1020102, "de_DE", "德语", ""),
    EN_US(1020103, "en_US", "美国英语", ""),
    ES_ES(1020104, "es_ES", "西班牙语", ""),
    FR_FR(1020105, "fr_FR", "法语", ""),
    IT_IT(1020106, "it_IT", "意大利语", ""),
    JA_JP(1020107, "ja_JP", "日语", ""),
    KO_KR(1020108, "ko_KR", "韩语", ""),
    RU_RU(1020109, "ru_RU", "俄语", ""),
    TH_TH(1020110, "th_TH", "泰国语", ""),
    ZH_CN(1020111, "zh_CN", "简体中文", ""),
    ZH_HK(1020112, "zh_HK", "繁体中文", ""),
    ;
    public static final String $SUPER = "standard_language";
    public static final String $AR_AE = "ar_AE";
    public static final String $DE_DE = "de_DE";
    public static final String $EN_US = "en_US";
    public static final String $ES_ES = "es_ES";
    public static final String $FR_FR = "fr_FR";
    public static final String $IT_IT = "it_IT";
    public static final String $JA_JP = "ja_JP";
    public static final String $KO_KR = "ko_KR";
    public static final String $RU_RU = "ru_RU";
    public static final String $TH_TH = "th_TH";
    public static final String $ZH_CN = "zh_CN";
    public static final String $ZH_HK = "zh_HK";
    public static final boolean useIdAsKey = false;
    private final int id;
    private final String code;
    private final String hint;
    private final String info;

    private final String ukey;
    private final String rkey;
    private final Locale locl;

    StandardLanguage(int id, String code, String hint, String info) {
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
    public static StandardLanguage valueOf(int id) {
        for (StandardLanguage v : StandardLanguage.values()) {
            if (id == v.id) return v;
        }
        return null;
    }

    @Contract("_, !null -> !null")
    public static StandardLanguage idOf(Integer id, StandardLanguage elz) {
        if (id == null) return elz;
        final int i = id;
        for (StandardLanguage v : StandardLanguage.values()) {
            if (i == v.id) return v;
        }
        return elz;
    }

    @Contract("_, !null -> !null")
    public static StandardLanguage codeOf(String code, StandardLanguage elz) {
        if (code == null) return elz;
        for (StandardLanguage v : StandardLanguage.values()) {
            if (code.equalsIgnoreCase(v.code)) return v;
        }
        return elz;
    }

    @Contract("_, !null -> !null")
    public static StandardLanguage nameOf(String name, StandardLanguage elz) {
        if (name == null) return elz;
        for (StandardLanguage v : StandardLanguage.values()) {
            if (name.equalsIgnoreCase(v.name())) return v;
        }
        return elz;
    }
}
