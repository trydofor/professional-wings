package pro.fessional.wings.warlock.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pro.fessional.wings.warlock.spring.bean.WarlockExceptionConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockHazelcastConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockJournalConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockJustAuthConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockOauthTicketConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockOtherBeanConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockSecurityBeanConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockSecurityConfConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockWatching2Configuration;
import pro.fessional.wings.warlock.spring.prop.WarlockApiAuthProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@AutoConfiguration
@AutoConfigureBefore(WarlockSecurityBeanConfiguration.class)
@EnableConfigurationProperties({WarlockApiAuthProp.class, WarlockUrlmapProp.class})
@ImportAutoConfiguration({
        WarlockExceptionConfiguration.class,
        WarlockHazelcastConfiguration.class,
        WarlockJournalConfiguration.class,
        WarlockJustAuthConfiguration.class,
        WarlockOauthTicketConfiguration.class,
        WarlockOtherBeanConfiguration.class,
        WarlockSecurityBeanConfiguration.class,
        WarlockSecurityConfConfiguration.class,
        WarlockWatching2Configuration.class,
})
public class WarlockShadowAutoConfiguration {
}
