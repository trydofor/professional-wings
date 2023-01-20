package pro.fessional.wings.silencer.spring.boot;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@ComponentScan("pro.fessional.wings.silencer.spring.bean")
@ConfigurationPropertiesScan("pro.fessional.wings.silencer.spring.prop")
@AutoConfiguration
public class WingsAutoConfiguration {
}
