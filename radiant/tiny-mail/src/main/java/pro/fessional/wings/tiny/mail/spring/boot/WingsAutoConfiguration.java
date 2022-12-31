package pro.fessional.wings.tiny.mail.spring.boot;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@ComponentScan("pro.fessional.wings.tiny.mail.spring.bean")
@ConfigurationPropertiesScan("pro.fessional.wings.tiny.mail.spring.prop")
public class WingsAutoConfiguration {
}
