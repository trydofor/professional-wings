package pro.fessional.wings.faceless.spring.boot;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@ComponentScan({"pro.fessional.wings.faceless.spring.bean"})
@ConfigurationPropertiesScan("pro.fessional.wings.faceless.spring.prop")
public class WingsAutoConfiguration {
}
