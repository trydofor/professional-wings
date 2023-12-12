package pro.fessional.wings.silencer.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.bean.SilencerConfiguration;
import pro.fessional.wings.silencer.spring.bean.SilencerRunnerConfiguration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.silencer.spring.prop.SilencerEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration
@ConditionalWingsEnabled
@ConfigurationPropertiesScan(basePackageClasses = SilencerEnabledProp.class)
@Import({
        SilencerConfiguration.class,
        SilencerRunnerConfiguration.class,
})
public class SilencerAutoConfiguration {
}
