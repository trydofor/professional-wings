package pro.fessional.wings.silencer.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.bean.SilencerCurseConfiguration;
import pro.fessional.wings.silencer.spring.bean.SilencerEncryptConfiguration;
import pro.fessional.wings.silencer.spring.bean.SilencerTweakConfiguration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration
@ConditionalWingsEnabled
@Import({
        SilencerCurseConfiguration.class,
        SilencerEncryptConfiguration.class,
        SilencerTweakConfiguration.class,
})
public class SilencerCurseAutoConfiguration {
}
