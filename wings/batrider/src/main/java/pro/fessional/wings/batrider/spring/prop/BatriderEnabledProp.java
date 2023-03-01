package pro.fessional.wings.batrider.spring.prop;

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
@ConfigurationProperties(BatriderEnabledProp.Key)
public class BatriderEnabledProp {

    public static final String Key = "spring.wings.batrider.enabled";

    /**
     * 是否启动自动配置
     *
     * @see #Key$autoconf
     */
    private boolean autoconf = true;
    public static final String Key$autoconf = Key + ".autoconf";

}
