package pro.fessional.wings.faceless.spring.bean;

import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.CommitJournalModify;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.impl.CommitJournalModifyJdbc;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.impl.CommitJournalModifyJooq;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.journal.impl.DefaultJournalService;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdService;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.wings.journal", name = "enabled", havingValue = "true")
public class WingsJournalConfiguration {

    @Bean
    @Order
    @ConditionalOnMissingBean(JournalService.class)
    public JournalService journalServiceJ(
            LightIdService lightIdService,
            BlockIdProvider blockIdProvider,
            CommitJournalModify journalModify
    ) {
        return new DefaultJournalService(lightIdService, blockIdProvider, journalModify);
    }


    @Bean
    @Order
    @ConditionalOnMissingBean(CommitJournalModify.class)
    @ConditionalOnProperty(prefix = "wings.journal.dao", name = "impl", havingValue = "jdbc")
    public CommitJournalModify commitJournalInsertJdbc(JdbcTemplate tpl) {
        return new CommitJournalModifyJdbc(tpl);
    }

    @Bean
    @Order
    @ConditionalOnMissingBean(CommitJournalModify.class)
    @ConditionalOnProperty(prefix = "wings.journal.dao", name = "impl", havingValue = "jooq")
    public CommitJournalModify commitJournalInsertJooq(DSLContext dsl) {
        return new CommitJournalModifyJooq(dsl);
    }
}
