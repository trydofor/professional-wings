package pro.fessional.wings.warlock.spring.boot;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@ComponentScan("pro.fessional.wings.slardar.spring.bean")
@ConfigurationPropertiesScan("pro.fessional.wings.slardar.spring.prop")
public class WingsComponentScanner {
}
