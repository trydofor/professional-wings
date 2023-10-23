package pro.fessional.wings.warlock.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pro.fessional.wings.warlock.spring.bean.WarlockAutoRunConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockLockBeanConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockTableChangeConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockWatchingConfiguration;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration
@ConditionalOnProperty(name = WarlockEnabledProp.Key$autoconf, havingValue = "true")
@EnableConfigurationProperties(WarlockEnabledProp.class)
@ImportAutoConfiguration({
        WarlockAutoRunConfiguration.class,
        WarlockLockBeanConfiguration.class,
        WarlockTableChangeConfiguration.class,
        WarlockWatchingConfiguration.class,
})
public class WarlockAutoConfiguration {
}
