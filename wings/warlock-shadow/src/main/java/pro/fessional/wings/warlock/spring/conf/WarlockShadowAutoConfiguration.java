package pro.fessional.wings.warlock.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import pro.fessional.wings.slardar.spring.conf.SlardarSprintAutoConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockExceptionConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockHazelcastConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockJournalConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockJustAuthConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockOauthTicketConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockOtherBeanConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockSecurityBeanConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockSecurityConfConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockWatching2Configuration;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@AutoConfiguration(before = {SlardarSprintAutoConfiguration.class, WarlockAutoConfiguration.class, SecurityAutoConfiguration.class})
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
