package pro.fessional.wings.warlock.spring.boot;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@ComponentScan({"pro.fessional.wings.warlock.controller",
                "pro.fessional.wings.warlock.spring.bean"})
@ConfigurationPropertiesScan("pro.fessional.wings.warlock.spring.prop")
public class WingsAutoConfiguration {
}
