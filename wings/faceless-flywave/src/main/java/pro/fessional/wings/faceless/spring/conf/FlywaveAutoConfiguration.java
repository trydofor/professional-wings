package pro.fessional.wings.faceless.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pro.fessional.wings.faceless.spring.bean.WingsFlywaveConfiguration;
import pro.fessional.wings.faceless.spring.prop.FacelessEnabledProp;
import pro.fessional.wings.faceless.spring.prop.FlywaveEnabledProp;

/**
 * @author trydofor
 * @since 2019-06-01
 */

@AutoConfiguration
@ConditionalOnProperty(name = FacelessEnabledProp.Key$autoconf, havingValue = "true")
@EnableConfigurationProperties(FlywaveEnabledProp.class)
@ImportAutoConfiguration(WingsFlywaveConfiguration.class)
public class FlywaveAutoConfiguration {
}
