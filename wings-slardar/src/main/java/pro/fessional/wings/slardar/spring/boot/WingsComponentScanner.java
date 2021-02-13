package pro.fessional.wings.slardar.spring.boot;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@Configuration
@ComponentScan("pro.fessional.wings.slardar.spring.bean")
@ConfigurationPropertiesScan("pro.fessional.wings.slardar.spring.prop")
public class WingsComponentScanner {
}
