package pro.fessional.wings.silencer.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.bean.SilencerAutoLogConfiguration;
import pro.fessional.wings.silencer.spring.bean.SilencerEncryptConfiguration;
import pro.fessional.wings.silencer.spring.bean.SilencerInspectConfiguration;
import pro.fessional.wings.silencer.spring.bean.SilencerModeWiredConfiguration;
import pro.fessional.wings.silencer.spring.bean.SilencerTweakConfiguration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration
@ConditionalWingsEnabled
@Import({
        SilencerAutoLogConfiguration.class,
        SilencerEncryptConfiguration.class,
        SilencerInspectConfiguration.class,
        SilencerModeWiredConfiguration.class,
        SilencerTweakConfiguration.class,
})
public class SilencerCurseAutoConfiguration {
}
