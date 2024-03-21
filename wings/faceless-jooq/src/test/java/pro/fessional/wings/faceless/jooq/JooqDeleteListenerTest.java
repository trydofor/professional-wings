package pro.fessional.wings.faceless.jooq;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import pro.fessional.wings.faceless.app.database.autogen.tables.TstShardingTable;
import pro.fessional.wings.faceless.app.database.autogen.tables.records.TstShardingRecord;
import pro.fessional.wings.faceless.database.jooq.helper.JournalJooqHelper;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;
import pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper;

import java.time.LocalDateTime;
import java.util.Arrays;

import static pro.fessional.wings.faceless.convention.EmptyValue.DATE_TIME;
import static pro.fessional.wings.faceless.enums.autogen.StandardLanguage.ZH_CN;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V90_22_0601_02_TestRecord;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_MASTER;
import static pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper.testcaseNotice;

/**
 * @author trydofor
 * @since 2019-09-27
 */

@SpringBootTest(properties = {"wings.faceless.jooq.conf.journal-delete=true"})
@DependsOnDatabaseInitialization
@TestMethodOrder(MethodOrderer.MethodName.class)
@ActiveProfiles("init")
@Tag("init")
@Slf4j
public class JooqDeleteListenerTest {

    @Setter(onMethod_ = {@Autowired})
    private DSLContext dsl;
    @Setter(onMethod_ = {@Autowired})
    private JdbcTemplate tmpl;

    // >>=>🦁🦁🦁
    @Setter(onMethod_ = {@Autowired})
    private TestingDatabaseHelper testingDatabaseHelper;

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Test
    @TmsLink("C12095")
    public void test0CleanTables() {
        testingDatabaseHelper.cleanTable();
        var sqls = FlywaveRevisionScanner.scan(REVISION_PATH_MASTER);
        schemaRevisionManager.checkAndInitSql(sqls, 0, true);
        schemaRevisionManager.publishRevision(V90_22_0601_02_TestRecord.revision(), -1);
    }
    //  🦁🦁🦁<=<<

    @Test
    @TmsLink("C12096")
    public void test2HelperSeeLog() {
        JournalJooqHelper.deleteByIds(dsl, TstShardingTable.TstSharding, 12L, 1L, 2L);
        JournalJooqHelper.deleteByIds(tmpl, "`tst_sharding`", 34L, 3L, 4L);
        testcaseNotice(
                "check logs, update before delete, as follows",
                "UPDATE `tst_sharding` SET commit_id=34, delete_dt=NOW(3)  WHERE id IN (3,4)",
                "DELETE FROM `tst_sharding`  WHERE id IN (3,4)"
        );
    }

    @Test
    @TmsLink("C12097")
    public void test3JooqDslSeeLog() {
        // handle
        dsl.execute("DELETE FROM `tst_sharding` WHERE ID =5 AND COMMIT_ID = 5");
        dsl.execute("DELETE FROM `tst_sharding` WHERE commit_id = 6 AND id = 6");
        dsl.execute("DELETE FROM `tst_sharding` WHERE commit_id = 7 AND id = ?", 7L);

        TstShardingTable t = TstShardingTable.TstSharding;
        dsl.deleteFrom(t).where(t.Id.eq(8L).and(t.CommitId.eq(8L))).execute();
        testcaseNotice(
                "check logs, id = (5,6,7,8) sql, and delete first, then update as follows",
                "DELETE FROM `tst_sharding` WHERE ID =5 AND COMMIT_ID = 5",
                "UPDATE `tst_sharding` SET COMMIT_ID = 5 ,delete_dt = NOW(3) WHERE ID =5"
        );

        // can not handle
        LocalDateTime now = LocalDateTime.now();
        dsl.batchDelete(
                new TstShardingRecord(9L, now, DATE_TIME, DATE_TIME, 9L, "", "", ZH_CN)
        ).execute();

        BatchBindStep batch = dsl.batch(
                dsl.deleteFrom(t).where(t.Id.eq((Long) null).and(t.CommitId.eq((Long) null)))
        );
        batch.bind(10L, 10L);
        batch.bind(11L, 11L);
        batch.bind(12L, 12L);
        batch.bind(13L, 13L);
        int[] rs = batch.execute();
        log.info(Arrays.toString(rs));
        testcaseNotice(
                "check logs, id >= 9 sql, only delete as follow",
                "delete from `tst_sharding` where `id` = ?"
        );
    }
}
