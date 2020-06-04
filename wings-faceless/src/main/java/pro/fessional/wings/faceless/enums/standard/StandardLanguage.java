package pro.fessional.wings.faceless.enums.standard;

import pro.fessional.wings.faceless.enums.StandardI18nEnum;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2019-09-17
 */
public enum StandardLanguage implements StandardI18nEnum {

    AR_AE("ar_AE", "阿拉伯联合酋长国"),
    DE_DE("de_DE", "德语"),
    EN_US("en_US", "美国英语"),
    ES_ES("es_ES", "西班牙语"),
    FR_FR("fr_FR", "法语"),
    IT_IT("it_IT", "意大利语"),
    JA_JP("ja_JP", "日语"),
    KO_KR("ko_KR", "韩语"),
    RU_RU("ru_RU", "俄语"),
    TH_TH("th_TH", "泰国语"),
    ZH_CN("zh_CN", "简体中文"),
    ZH_HK("zh_HK", "繁体中文");

    private final String code;
    private final String name;
    private final String ikey;
    private final Locale locale;
    private final String[] score;

    StandardLanguage(String code, String name) {
        this.code = code;
        this.name = name;
        this.ikey = getPrefix() + "." + code;
        this.locale = Locale.forLanguageTag(code);
        this.score = new String[]{code.substring(0, 2).toLowerCase(),
                                  code.substring(3).toLowerCase(),
                                  code.toLowerCase()
        };
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getI18nKey() {
        return ikey;
    }

    @Override
    public String getName() {
        return name;
    }

    public Locale getLocale() {
        return locale;
    }

    public int similar(String str) {
        if (str == null) return 0;
        int scr = 0;
        for (int i = 0; i < score.length; i++) {
            if (str.contains(score[i])) scr += i + 1;
        }

        return scr;
    }

    @Override
    public String getPrefix() {
        return "ctr_standard_lang.name";
    }
}
