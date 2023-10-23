package pro.fessional.wings.silencer.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import pro.fessional.wings.silencer.spring.bean.SilencerAutoLogConfiguration;
import pro.fessional.wings.silencer.spring.bean.SilencerEncryptConfiguration;
import pro.fessional.wings.silencer.spring.bean.SilencerInspectConfiguration;
import pro.fessional.wings.silencer.spring.bean.SilencerRuntimeConfiguration;
import pro.fessional.wings.silencer.spring.bean.SilencerTweakConfiguration;
import pro.fessional.wings.silencer.spring.prop.SilencerEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration
@ConditionalOnProperty(name = SilencerEnabledProp.Key$autoconf, havingValue = "true")
@ImportAutoConfiguration({
        SilencerAutoLogConfiguration.class,
        SilencerEncryptConfiguration.class,
        SilencerInspectConfiguration.class,
        SilencerRuntimeConfiguration.class,
        SilencerTweakConfiguration.class,
})
public class SilencerCurseAutoConfiguration {
}
