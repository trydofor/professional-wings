package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring-wings-enabled-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(FacelessEnabledProp.Key)
public class FacelessEnabledProp {

    public static final String Key = "spring.wings.faceless.enabled";

    /**
     * 是否注入lingthid
     *
     * @see #Key$lightid
     */
    private boolean lightid = true;
    public static final String Key$lightid = Key + ".lightid";

    /**
     * 是否注入journal
     *
     * @see #Key$journal
     */
    private boolean journal = true;
    public static final String Key$journal = Key + ".journal";

    /**
     * 是否注入StandardI18nService
     *
     * @see #Key$enumi18n
     */
    private boolean enumi18n = true;
    public static final String Key$enumi18n = Key + ".enumi18n";
}
