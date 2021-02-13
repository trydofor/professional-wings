package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wings-i18n-79.properties
 *
 * @author trydofor
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties("wings.silencer.i18n")
public class SilencerI18nProp {

    /**
     * 系统默认语言，空表示系统默认: en_US, zh_CN，
     * system properties: user.language, user.country
     */
    private String locale = "";

    /**
     * 系统时区，默认使用系统: UTC, GMT+8, Asia/Shanghai,
     * system properties: user.timezone
     */
    private String zoneid = "";
    /**
     * 默认的resource配置，逗号分隔，
     */
    private String bundle = "classpath*:/wings-i18n/**/*.properties";
}
