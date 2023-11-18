package pro.fessional.wings.warlock.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.warlock.spring.bean.WarlockAutoRunConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockLockBeanConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockTableChangeConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockWatchingConfiguration;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration
@ConditionalWingsEnabled
@Import({
        WarlockAutoRunConfiguration.class,
        WarlockLockBeanConfiguration.class,
        WarlockTableChangeConfiguration.class,
        WarlockWatchingConfiguration.class,
})
public class WarlockAutoConfiguration {
}
