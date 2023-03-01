package pro.fessional.wings.batrider.spring.boot;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import pro.fessional.wings.batrider.spring.prop.BatriderEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@ComponentScan("pro.fessional.wings.batrider.spring.bean")
@ConfigurationPropertiesScan("pro.fessional.wings.batrider.spring.prop")
@AutoConfiguration
@ConditionalOnProperty(name = BatriderEnabledProp.Key$autoconf, havingValue = "true")
public class WingsAutoConfiguration {
}
