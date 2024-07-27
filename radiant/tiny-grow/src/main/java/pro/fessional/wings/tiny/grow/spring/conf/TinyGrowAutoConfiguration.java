package pro.fessional.wings.tiny.grow.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.tiny.grow.spring.bean.TinyTrackConfiguration;
import pro.fessional.wings.tiny.grow.spring.prop.TinyTrackExcludeProp;

/**
 * @author trydofor
 * @since 2024-07-27
 */
@AutoConfiguration
@ConditionalWingsEnabled
@ConfigurationPropertiesScan(basePackageClasses = TinyTrackExcludeProp.class)
@Import(TinyTrackConfiguration.class)
public class TinyGrowAutoConfiguration {
}
