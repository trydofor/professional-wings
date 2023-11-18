package pro.fessional.wings.warlock.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.warlock.spring.bean.WarlockExceptionConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockJournalConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockJustAuthConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockOauthTicketConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockOtherBeanConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockWatching2Configuration;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@AutoConfiguration
@ConditionalWingsEnabled
@Import({
        WarlockExceptionConfiguration.class,
        WarlockJournalConfiguration.class,
        WarlockJustAuthConfiguration.class,
        WarlockOauthTicketConfiguration.class,
        WarlockOtherBeanConfiguration.class,
        WarlockWatching2Configuration.class,
})
public class WarlockShadowAutoConfiguration {
}
