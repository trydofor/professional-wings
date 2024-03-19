package pro.fessional.wings.faceless.database.jooq;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.jooq.Field;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pro.fessional.wings.faceless.app.database.autogen.tables.TstShardingTable;
import pro.fessional.wings.faceless.app.database.autogen.tables.daos.TstShardingDao;
import pro.fessional.wings.faceless.app.database.autogen.tables.pojos.TstSharding;
import pro.fessional.wings.faceless.app.database.autogen.tables.records.TstShardingRecord;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.WingsRevision;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;
import pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.fessional.wings.faceless.enums.autogen.StandardLanguage.ZH_CN;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V90_22_0601_02_TestRecord;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_MASTER;
import static pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper.testcaseNotice;


/**
 * @author trydofor
 * @since 2020-05-31
 */

@SpringBootTest
@DependsOnDatabaseInitialization
@ActiveProfiles("init")
@TestMethodOrder(MethodOrderer.MethodName.class)
@Tag("init")
public class WingsJooqDaoAliasImplTest {

    @Setter(onMethod_ = {@Autowired})
    private TestingDatabaseHelper testingDatabaseHelper;

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod_ = {@Autowired})
    private TstShardingDao tstShardingDao;

    private final TstShardingTable tbl = TstShardingTable.TstSharding;
    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @TmsLink("C12081")
    public void test0DropAndInit() {
        testingDatabaseHelper.cleanTable();
        var sqls = FlywaveRevisionScanner.scan(REVISION_PATH_MASTER, WingsRevision.V01_19_0521_01_EnumI18n.classpath());
        schemaRevisionManager.checkAndInitSql(sqls, 0, true);
        schemaRevisionManager.publishRevision(V90_22_0601_02_TestRecord.revision(), -1);
    }

    @Test
    @TmsLink("C12082")
    public void test1BatchLoadSeeLog() {
        if (WingsJooqEnv.daoBatchMysql) {
            testcaseNotice("Skip the inefficient SQL and use mysql `replace into` syntax, see batchMerge");
            return;
        }

        final var rds = Arrays.asList(
                new TstShardingRecord(301L, now, now, now, 9L, "batch load 301", "", ZH_CN),
                new TstShardingRecord(302L, now, now, now, 9L, "batch load 302", "", ZH_CN),
                new TstShardingRecord(303L, now, now, now, 9L, "batch load 303", "", ZH_CN)
        );
        testcaseNotice("batch load, check log, ignore, 301-303, use `from dual where exists` check, then insert");
        tstShardingDao.batchLoad(rds, true);
        testcaseNotice("batch load, check log, replace, 301-303, use on duplicate key update");
        tstShardingDao.batchLoad(rds, false);
    }

    @Test
    @TmsLink("C12083")
    public void test2BatchInsertSeeLog() {
        final var rds = Arrays.asList(
                new TstShardingRecord(304L, now, now, now, 9L, "batch load 304", "", ZH_CN),
                new TstShardingRecord(305L, now, now, now, 9L, "batch load 305", "", ZH_CN),
                new TstShardingRecord(306L, now, now, now, 9L, "batch load 306", "", ZH_CN)
        );
        testcaseNotice("batch Insert, check log, 304-306, in 2 batch");
        final var rs = tstShardingDao.batchInsert(rds, 2);
        assertArrayEquals(new int[]{1, 1, 1}, rs);
    }

    @Test
    @TmsLink("C12084")
    public void test3BatchMergeSeeLog() {
        final var rds = Arrays.asList(
                new TstShardingRecord(307L, now, now, now, 9L, "batch load 307", "", ZH_CN),
                new TstShardingRecord(308L, now, now, now, 9L, "batch load 308", "", ZH_CN),
                new TstShardingRecord(309L, now, now, now, 9L, "batch load 309", "", ZH_CN)
        );
        testcaseNotice("batch Insert, check log, ignore, 307-309, in 2 batch, insert ignore");
        final var rs1 = tstShardingDao.batchInsert(rds, 2, true);
        assertArrayEquals(new int[]{1, 1, 1}, rs1);

        testcaseNotice("batch Insert, check log, replace, 307-309, in 2 batch, replace into", "BUG https://github.com/apache/shardingsphere/issues/8226\n");
        final var rs2 = tstShardingDao.batchInsert(rds, 2, false);
        assertArrayEquals(new int[]{1, 1, 1}, rs2);

        testcaseNotice("batch Merge, check log, on dupkey, 307-309, in 2 batch, duplicate");
        testcaseNotice("insert into `tst_sharding` (`id`, .., `other_info`) values (?,..., ?) on duplicate key update `login_info` = ?, `other_info` = ?");
        final var rs3 = tstShardingDao.batchMerge(tbl, rds, 2, tbl.LoginInfo, tbl.OtherInfo);
        assertArrayEquals(new int[]{1, 1, 1}, rs3);
    }

    @Test
    @TmsLink("C12085")
    public void test4BatchStoreSeeLog() {
        final var rds = Arrays.asList(
                new TstShardingRecord(310L, now, now, now, 9L, "batch load 310", "", ZH_CN),
                new TstShardingRecord(311L, now, now, now, 9L, "batch load 311", "", ZH_CN),
                new TstShardingRecord(312L, now, now, now, 9L, "batch load 312", "merge", ZH_CN)
        );
        testcaseNotice("batch Insert, check log, ignore, 307-309, in 2 batch");
        final var rs = tstShardingDao.batchStore(rds, 2);
        assertArrayEquals(new int[]{1, 1, 1}, rs);
    }

    @Test
    @TmsLink("C12086")
    public void test5BatchUpdateSeeLog() {
        final var rds = Arrays.asList(
                new TstShardingRecord(309L, now, now, now, 9L, "batch load 309", "update", ZH_CN),
                new TstShardingRecord(310L, now, now, now, 9L, "batch load 310", "update", ZH_CN),
                new TstShardingRecord(311L, now, now, now, 9L, "batch load 311", "update", ZH_CN)
        );
        testcaseNotice("batch Update, check log, 307-309, in 2 batch");
        final var rs1 = tstShardingDao.batchUpdate(rds, 2);
        assertArrayEquals(new int[]{1, 1, 1}, rs1);

        final var rs2 = tstShardingDao.batchUpdate(tbl, new Field[]{tbl.Id}, rds, 2, tbl.LoginInfo, tbl.OtherInfo);
        assertArrayEquals(new int[]{1, 1, 1}, rs2);
    }

    @Test
    @TmsLink("C12087")
    public void test6SingleMergeSeeLog() {
        testcaseNotice("insert into `tst_sharding` (`id`, .., `other_info`) values (?,..., ?) on duplicate key update `login_info` = ?, `other_info` = ?");
        TstSharding pojo = new TstSharding(312L, now, now, now, 9L, "batch load 312", "update-bymerge", ZH_CN);
        final var rs = tstShardingDao.mergeInto(tbl, pojo, tbl.LoginInfo, tbl.OtherInfo);
        assertEquals(2, rs);
    }

    @Test
    @TmsLink("C12088")
    public void test7BatchMergeSeeLog() {
        final var rds = Arrays.asList(
                new TstShardingRecord(313L, now, now, now, 9L, "batch 313-merge", "update-merge", ZH_CN),
                new TstShardingRecord(310L, now, now, now, 9L, "batch 310-merge", "update-merge", ZH_CN),
                new TstShardingRecord(311L, now, now, now, 9L, "batch 311-merge", "update-merge", ZH_CN)
        );
        testcaseNotice("313 insert, 310,311 update");
        final var rs = tstShardingDao.batchMerge(tbl, new Field[]{tbl.Id}, rds, 2, tbl.LoginInfo, tbl.OtherInfo);
        assertArrayEquals(new int[]{1, 1, 1}, rs);
    }

    @Test
    @TmsLink("C12089")
    public void test8LogicDeleteSeeLog() {
        tstShardingDao.fetchById(1L);
        tstShardingDao.fetchOneById(1L);
        tstShardingDao.count();
        final TstShardingTable tbl = tstShardingDao.getTable();
        tstShardingDao.count(tbl, tbl.getOnlyLive());
    }
}
