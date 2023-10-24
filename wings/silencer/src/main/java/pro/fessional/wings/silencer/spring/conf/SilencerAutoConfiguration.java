package pro.fessional.wings.silencer.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import pro.fessional.wings.silencer.spring.bean.SilencerMessageConfiguration;
import pro.fessional.wings.silencer.spring.bean.SilencerRunnerConfiguration;
import pro.fessional.wings.silencer.spring.prop.SilencerEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(name = SilencerEnabledProp.Key$autoconf, havingValue = "true")
@ImportAutoConfiguration({
        SilencerMessageConfiguration.class,
        SilencerRunnerConfiguration.class,
})
public class SilencerAutoConfiguration {
}
