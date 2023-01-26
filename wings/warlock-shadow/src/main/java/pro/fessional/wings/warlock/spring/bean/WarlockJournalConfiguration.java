package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.CommitJournalModify;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.faceless.spring.prop.FacelessEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.spring.consts.OrderedWarlockConst;
import pro.fessional.wings.warlock.service.other.TerminalJournalService;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = FacelessEnabledProp.Key$journal, havingValue = "true")
@AutoConfigureOrder(OrderedWarlockConst.TerminalJournalConfiguration)
public class WarlockJournalConfiguration {

    private static final Log log = LogFactory.getLog(WarlockJournalConfiguration.class);

    @Bean
    @ConditionalOnProperty(name = SlardarEnabledProp.Key$terminal, havingValue = "true")
    public TerminalJournalService terminalJournalService(
            @SuppressWarnings("all") LightIdService lightIdService,
            @SuppressWarnings("all") BlockIdProvider blockIdProvider,
            @SuppressWarnings("all") CommitJournalModify journalModify
    ) {
        log.info("WarlockShadow spring-bean terminalJournalService Overriding");
        return new TerminalJournalService(lightIdService, blockIdProvider, journalModify);
    }
}
