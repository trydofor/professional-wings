package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wings-i18n-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(SilencerI18nProp.Key)
public class SilencerI18nProp {

    public static final String Key = "wings.silencer.i18n";

    /**
     * 系统默认语言，空表示系统默认: en_US, zh_CN，
     * system properties: user.language, user.country
     *
     * @see #Key$locale
     */
    private String locale = "";
    public static final String Key$locale = Key + ".locale";

    /**
     * 系统时区，默认使用系统: UTC, GMT+8, Asia/Shanghai,
     * system properties: user.timezone
     *
     * @see #Key$zoneid
     */
    private String zoneid = "";
    public static final String Key$zoneid = Key + ".zoneid";
    /**
     * 默认的resource配置，逗号分隔，
     *
     * @see #Key$bundle
     */
    private String bundle = "classpath*:/wings-i18n/**/*.properties";
    public static final String Key$bundle = Key + ".bundle";
}
