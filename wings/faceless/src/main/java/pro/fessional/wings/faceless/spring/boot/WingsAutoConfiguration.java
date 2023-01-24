package pro.fessional.wings.faceless.spring.boot;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import pro.fessional.wings.faceless.spring.prop.FacelessEnabledProp;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@ComponentScan({"pro.fessional.wings.faceless.spring.bean"})
@ConfigurationPropertiesScan("pro.fessional.wings.faceless.spring.prop")
@AutoConfiguration
@ConditionalOnProperty(name = FacelessEnabledProp.Key$autoconf, havingValue = "true")
public class WingsAutoConfiguration {
}
