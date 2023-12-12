package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.CommitJournalModify;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.warlock.service.other.TerminalJournalService;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class WarlockJournalConfiguration {

    private static final Log log = LogFactory.getLog(WarlockJournalConfiguration.class);

    /**
     * terminal journal instead of the simple
     */
    @Bean
    @ConditionalWingsEnabled
    public TerminalJournalService terminalJournalService(
            @SuppressWarnings("all") LightIdService lightIdService,
            @SuppressWarnings("all") BlockIdProvider blockIdProvider,
            @SuppressWarnings("all") CommitJournalModify journalModify
    ) {
        log.info("WarlockShadow spring-bean terminalJournalService Overriding");
        return new TerminalJournalService(lightIdService, blockIdProvider, journalModify);
    }
}
