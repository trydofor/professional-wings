package pro.fessional.wings.batrider.spring.boot;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@ComponentScan("pro.fessional.wings.batrider.spring.bean")
@ConfigurationPropertiesScan("pro.fessional.wings.batrider.spring.prop")
public class WingsAutoConfiguration {
}
