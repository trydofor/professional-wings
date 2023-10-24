package pro.fessional.wings.batrider.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import pro.fessional.wings.batrider.spring.bean.BatriderServcombConfiguration;
import pro.fessional.wings.batrider.spring.prop.BatriderEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration
@ConditionalOnProperty(name = BatriderEnabledProp.Key$autoconf, havingValue = "true")
@ImportAutoConfiguration(BatriderServcombConfiguration.class)
public class BatriderAutoConfiguration {
}
