package pro.fessional.wings.faceless.spring.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.CommitJournalModify;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.impl.CommitJournalModifyJdbc;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.journal.impl.DefaultJournalService;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.faceless.spring.prop.FacelessEnabledProp;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = FacelessEnabledProp.Key$journal, havingValue = "true")
public class FacelessJournalConfiguration {

    @Bean
    @ConditionalOnMissingBean(JournalService.class)
    public JournalService journalService(
            LightIdService lightIdService,
            BlockIdProvider blockIdProvider,
            CommitJournalModify journalModify
    ) {
        return new DefaultJournalService(lightIdService, blockIdProvider, journalModify);
    }


    @Bean
    @ConditionalOnMissingBean(CommitJournalModify.class)
    public CommitJournalModify commitJournalModify(JdbcTemplate jdbcTemplate) {
        return new CommitJournalModifyJdbc(jdbcTemplate);
    }
}
