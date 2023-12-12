package pro.fessional.wings.tiny.task.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.tiny.task.spring.bean.TinyTaskConfiguration;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration
@ConditionalWingsEnabled
@ConfigurationPropertiesScan(basePackageClasses = TinyTaskEnabledProp.class)
@Import(TinyTaskConfiguration.class)
public class TinyTaskAutoConfiguration {
}
