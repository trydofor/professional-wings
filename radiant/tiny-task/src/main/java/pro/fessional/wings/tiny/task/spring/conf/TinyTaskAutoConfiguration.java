package pro.fessional.wings.tiny.task.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import pro.fessional.wings.slardar.spring.conf.SlardarAutoConfiguration;
import pro.fessional.wings.tiny.task.spring.bean.TinyTaskConfiguration;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration(before = SlardarAutoConfiguration.class)
@ConditionalOnProperty(name = TinyTaskEnabledProp.Key$autoconf, havingValue = "true")
@ImportAutoConfiguration(TinyTaskConfiguration.class)
public class TinyTaskAutoConfiguration {
}
