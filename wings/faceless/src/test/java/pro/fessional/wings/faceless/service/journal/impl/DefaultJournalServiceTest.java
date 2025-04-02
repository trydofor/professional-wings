package pro.fessional.wings.faceless.service.journal.impl;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.faceless.database.helper.TransactionHelper;
import pro.fessional.wings.faceless.service.journal.JournalService.Journal;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author trydofor
 * @since 2024-06-14
 */
@SpringBootTest(properties = {
    "logging.level.org.springframework.jdbc=DEBUG",
    "logging.level.org.springframework.transaction=DEBUG",
    "wings.faceless.journal.alive=1"
})
@Slf4j
@DependsOnDatabaseInitialization
class DefaultJournalServiceTest {

    @Setter(onMethod_ = { @Autowired })
    protected DefaultJournalService journalService;

    @Setter(onMethod_ = { @Autowired })
    protected JdbcTemplate jdbcTemplate;

    @Setter(onMethod_ = { @Autowired })
    protected PlatformTransactionManager platformTransactionManager;

    enum CallLevel {
        L1,
        L2,
        L3,
    }

    @TmsLink("C12151")
    @Test
    void commit() {
        Assertions.assertSame(platformTransactionManager, TransactionHelper.manager());

        final AtomicReference<Journal> journalRef = new AtomicReference<>();
        {
            // default required new
            TransactionStatus status = TransactionHelper.begin();
            journalService.commit(CallLevel.L1, j1 -> {
                journalService.commit(CallLevel.L2, j2 -> {
                    journalService.commit(CallLevel.L3, journalRef::set);
                });
            });
            TransactionHelper.rollback(status);

            Journal jn = journalRef.get();
            log.info("journal ={}", jn);
            Assertions.assertNotNull(jn);
            Assertions.assertTrue(jn.getEventName().endsWith(CallLevel.L1.name()));

            Long id = jdbcTemplate.queryForObject("SELECT id FROM sys_commit_journal WHERE id = ?", Long.class, jn.getId());
            Assertions.assertEquals(jn.getId(), id);
            Assertions.assertEquals(0, jn.getParentId());
        }

        {
            journalService.setPropagation(Propagation.REQUIRED);
            try {
                TransactionStatus status = TransactionHelper.begin();
                journalService.commit(CallLevel.L1, j1 -> {
                    journalService.commit(CallLevel.L1, j2 -> {
                        journalService.commit(CallLevel.L3, journalRef::set);
                    });
                });
                TransactionHelper.rollback(status);
                Journal jn = journalRef.get();
                List<Long> id = jdbcTemplate.queryForList("SELECT id FROM sys_commit_journal WHERE id = ?", Long.class, jn.getId());
                Assertions.assertTrue(id.isEmpty());
                Assertions.assertEquals(0, jn.getParentId());
            }
            finally {
                journalService.setPropagation(Propagation.REQUIRES_NEW);
            }
        }

        {
            journalService.commit(CallLevel.L1, j1 -> {
                journalService.commit(CallLevel.L1, j2 -> {
                    Sleep.ignoreInterrupt(2500);
                    journalService.commit(CallLevel.L3, journalRef::set);
                });
            });

            Journal jn = journalRef.get();
            Assertions.assertTrue(jn.getParentId() != 0);
        }
    }
}