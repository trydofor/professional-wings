package pro.fessional.wings.faceless.spring.bean;

import org.jooq.DSLContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.database.autogen.tables.daos.SysCommitJournalDao;
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
@ConditionalOnProperty(name = "spring.wings.journal.enabled", havingValue = "true")
public class WingsJournalConfiguration {

    @Bean
    @ConditionalOnMissingBean(SysCommitJournalDao.class)
    public SysCommitJournalDao sysCommitJournalDao(org.jooq.Configuration configuration) {
        return new SysCommitJournalDao(configuration);
    }

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
    public CommitJournalModify commitJournalModify(
            @Value("${wings.journal.dao.impl}") String implType,
            ObjectProvider<JdbcTemplate> jdbcTemplate,
            ObjectProvider<DSLContext> dslContext
    ) {
        if (implType.equalsIgnoreCase("jdbc")) {
            return new CommitJournalModifyJdbc(jdbcTemplate.getIfAvailable());
        } else {
            return new CommitJournalModifyJooq(dslContext.getIfAvailable());
        }
    }
}
