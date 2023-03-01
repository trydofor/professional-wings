package pro.fessional.wings.tiny.mail.spring.boot;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@ComponentScan("pro.fessional.wings.tiny.mail.spring.bean")
@ConfigurationPropertiesScan("pro.fessional.wings.tiny.mail.spring.prop")
@AutoConfiguration
@ConditionalOnProperty(name = TinyMailEnabledProp.Key$autoconf, havingValue = "true")
public class WingsAutoConfiguration {
}
