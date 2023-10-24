package pro.fessional.wings.warlock.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import pro.fessional.wings.faceless.spring.conf.FacelessJooqAutoConfiguration;
import pro.fessional.wings.slardar.spring.conf.SlardarAutoConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockAutoRunConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockLockBeanConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockTableChangeConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockWatchingConfiguration;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration(before = {FacelessJooqAutoConfiguration.class, SlardarAutoConfiguration.class})
@ConditionalOnProperty(name = WarlockEnabledProp.Key$autoconf, havingValue = "true")
@ImportAutoConfiguration({
        WarlockAutoRunConfiguration.class,
        WarlockLockBeanConfiguration.class,
        WarlockTableChangeConfiguration.class,
        WarlockWatchingConfiguration.class,
})
public class WarlockAutoConfiguration {
}
