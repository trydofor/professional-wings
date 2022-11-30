package pro.fessional.wings.slardar.spring.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.webmvc.SimpleResponse;

/**
 * wings-doublekill-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@ConfigurationProperties(SlardarDoubleKillProp.Key)
public class SlardarDoubleKillProp extends SimpleResponse {
    public static final String Key = "wings.slardar.double-kill";
}
