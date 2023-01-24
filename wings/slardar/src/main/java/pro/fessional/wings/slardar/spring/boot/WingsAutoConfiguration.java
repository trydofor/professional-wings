package pro.fessional.wings.slardar.spring.boot;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@ComponentScan("pro.fessional.wings.slardar.spring.bean")
@ConfigurationPropertiesScan("pro.fessional.wings.slardar.spring.prop")
@AutoConfiguration
@ConditionalOnProperty(name = SlardarEnabledProp.Key$autoconf, havingValue = "true")
public class WingsAutoConfiguration {
}
