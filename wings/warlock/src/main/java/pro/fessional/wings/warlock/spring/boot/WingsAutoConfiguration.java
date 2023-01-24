package pro.fessional.wings.warlock.spring.boot;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@ComponentScan("pro.fessional.wings.warlock.spring.bean")
@ConfigurationPropertiesScan("pro.fessional.wings.warlock.spring.prop")
@AutoConfiguration
@ConditionalOnProperty(name = WarlockEnabledProp.Key$autoconf, havingValue = "true")
public class WingsAutoConfiguration {
}
