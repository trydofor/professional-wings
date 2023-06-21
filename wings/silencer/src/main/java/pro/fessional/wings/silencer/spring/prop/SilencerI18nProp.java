package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Set default language and timezone for the app, as well as i18n messages.
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
     * in the format `en_US`, `zh_CN`. Default system language.
     * Corresponds to `user.language`, `user.country` of the system variable
     *
     * @see #Key$locale
     */
    private String locale = "";
    public static final String Key$locale = Key + ".locale";

    /**
     * such as `UTC`, `GMT+8,` `Asia/Shanghai`. Default system timezone.
     * corresponding to `user.timezone` of the system variable
     *
     * @see #Key$zoneid
     */
    private String zoneid = "";
    public static final String Key$zoneid = Key + ".zoneid";

    /**
     * The default resource configuration, in comma-separated AntPath format.
     *
     * @see #Key$bundle
     */
    private String bundle = "classpath*:/wings-i18n/**/*.properties";
    public static final String Key$bundle = Key + ".bundle";
}
