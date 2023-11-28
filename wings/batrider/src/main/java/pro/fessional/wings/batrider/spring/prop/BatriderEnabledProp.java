package pro.fessional.wings.batrider.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.silencer.spring.boot.WingsEnabledCondition;

/**
 * wings-enabled-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(BatriderEnabledProp.Key)
public class BatriderEnabledProp {

    public static final String Key = WingsEnabledCondition.Prefix + ".batrider";

}
