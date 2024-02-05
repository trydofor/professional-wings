package pro.fessional.wings.warlock.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.warlock.spring.bean.WarlockAutoRunConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockAwesomeConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockJournalConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockLockBeanConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockTableChangeConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockWatchingConfiguration;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration
@ConditionalWingsEnabled
@ConfigurationPropertiesScan(basePackageClasses = WarlockEnabledProp.class)
@Import({
        WarlockAutoRunConfiguration.class,
        WarlockAwesomeConfiguration.class,
        WarlockJournalConfiguration.class,
        WarlockLockBeanConfiguration.class,
        WarlockTableChangeConfiguration.class,
        WarlockWatchingConfiguration.class,
})
public class WarlockAutoConfiguration {
}
