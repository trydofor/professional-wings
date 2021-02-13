package pro.fessional.wings.silencer.spring.boot;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@Configuration
@ComponentScan("pro.fessional.wings.silencer.spring.bean")
@ConfigurationPropertiesScan("pro.fessional.wings.silencer.spring.prop")
public class WingsComponentScanner {
}
