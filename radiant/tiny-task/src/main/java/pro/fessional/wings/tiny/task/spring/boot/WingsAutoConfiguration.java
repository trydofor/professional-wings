package pro.fessional.wings.tiny.task.spring.boot;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@ComponentScan("pro.fessional.wings.tiny.task.spring.bean")
@ConfigurationPropertiesScan("pro.fessional.wings.tiny.task.spring.prop")
@AutoConfiguration
@ConditionalOnProperty(name = TinyTaskEnabledProp.Key$autoconf, havingValue = "true")
public class WingsAutoConfiguration {
}
