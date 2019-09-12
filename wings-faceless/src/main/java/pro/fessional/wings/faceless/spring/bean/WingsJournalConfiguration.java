package pro.fessional.wings.faceless.spring.bean;

import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.database.manual.single.insert.commitjournal.CommitJournalInsert;
import pro.fessional.wings.faceless.database.manual.single.insert.commitjournal.impl.CommitJournalInsertJdbc;
import pro.fessional.wings.faceless.database.manual.single.insert.commitjournal.impl.CommitJournalInsertJooq;
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
    @ConditionalOnMissingBean(JournalService.class)
    public JournalService journalServiceJ(
            LightIdService lightIdService,
            BlockIdProvider blockIdProvider,
            CommitJournalInsert journalInsert
    ) {
        return new DefaultJournalService(lightIdService, blockIdProvider, journalInsert);
    }


    @Bean
    @ConditionalOnMissingBean(CommitJournalInsert.class)
    @ConditionalOnProperty(prefix = "wings.journal.dao", name = "impl", havingValue = "jdbc")
    public CommitJournalInsert commitJournalInsertJdbc(JdbcTemplate tpl) {
        return new CommitJournalInsertJdbc(tpl);
    }

    @Bean
    @ConditionalOnMissingBean(CommitJournalInsert.class)
    @ConditionalOnProperty(prefix = "wings.journal.dao", name = "impl", havingValue = "jooq")
    public CommitJournalInsert commitJournalInsertJooq(DSLContext dsl) {
        return new CommitJournalInsertJooq(dsl);
    }
}
