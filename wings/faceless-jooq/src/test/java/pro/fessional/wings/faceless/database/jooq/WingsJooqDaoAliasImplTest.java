package pro.fessional.wings.faceless.database.jooq;

import lombok.Setter;
import lombok.val;
import org.jooq.Field;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pro.fessional.wings.faceless.WingsTestHelper;
import pro.fessional.wings.faceless.database.autogen.tables.TstShardingTable;
import pro.fessional.wings.faceless.database.autogen.tables.daos.TstShardingDao;
import pro.fessional.wings.faceless.database.autogen.tables.pojos.TstSharding;
import pro.fessional.wings.faceless.database.autogen.tables.records.TstShardingRecord;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.WingsRevision;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V2;
import static pro.fessional.wings.faceless.WingsTestHelper.testcaseNotice;
import static pro.fessional.wings.faceless.enums.autogen.StandardLanguage.ZH_CN;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_MASTER;


/**
 * @author trydofor
 * @since 2020-05-31
 */

@SpringBootTest(properties = {"debug = true", "logging.level.org.jooq.tools.LoggerListener=DEBUG"})
@ActiveProfiles("init")
@TestMethodOrder(MethodOrderer.MethodName.class)
@Tag("init")
public class WingsJooqDaoAliasImplTest {

    @Setter(onMethod_ = {@Autowired})
    private WingsTestHelper wingsTestHelper;

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod_ = {@Autowired})
    private TstShardingDao dao;

    private final TstShardingTable tbl = TstShardingTable.TstSharding;
    private final LocalDateTime now = LocalDateTime.now();

    @Test
    public void test0DropAndInit() {
        wingsTestHelper.cleanTable();
        final SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls = FlywaveRevisionScanner.scan(REVISION_PATH_MASTER, WingsRevision.V01_19_0521_01_EnumI18n.classpath());
        schemaRevisionManager.checkAndInitSql(sqls, 0, true);
        schemaRevisionManager.publishRevision(REVISION_TEST_V2, -1);
    }

    @Test
    public void test1BatchLoadSeeLog() {
        if (WingsJooqEnv.daoBatchMysql) {
            testcaseNotice("跳过低效的SQL，使用mysql replace into 语法，见 batchMerge");
            return;
        }

        val rds = Arrays.asList(
                new TstShardingRecord(301L, now, now, now, 9L, "批量加载301", "", ZH_CN),
                new TstShardingRecord(302L, now, now, now, 9L, "批量加载302", "", ZH_CN),
                new TstShardingRecord(303L, now, now, now, 9L, "批量加载303", "", ZH_CN)
        );
        testcaseNotice("批量Load，查看日志，ignore, 301-303，使用了from dual where exists先查再插");
        dao.batchLoad(rds, true);
        testcaseNotice("批量Load，查看日志，replace, 301-303，使用了on duplicate key update");
        dao.batchLoad(rds, false);
    }

    @Test
    public void test2BatchInsertSeeLog() {
        val rds = Arrays.asList(
                new TstShardingRecord(304L, now, now, now, 9L, "批量加载304", "", ZH_CN),
                new TstShardingRecord(305L, now, now, now, 9L, "批量加载305", "", ZH_CN),
                new TstShardingRecord(306L, now, now, now, 9L, "批量加载306", "", ZH_CN)
        );
        testcaseNotice("批量Insert，查看日志, 304-306，分2批插入");
        val rs = dao.batchInsert(rds, 2);
        assertArrayEquals(new int[]{1, 1, 1}, rs);
    }

    @Test
    public void test3BatchMergeSeeLog() {
        val rds = Arrays.asList(
                new TstShardingRecord(307L, now, now, now, 9L, "批量加载307", "", ZH_CN),
                new TstShardingRecord(308L, now, now, now, 9L, "批量加载308", "", ZH_CN),
                new TstShardingRecord(309L, now, now, now, 9L, "批量加载309", "", ZH_CN)
        );
        testcaseNotice("批量Insert，查看日志,ignore, 307-309，分2批次， insert ignore");
        val rs1 = dao.batchInsert(rds, 2, true);
        assertArrayEquals(new int[]{1, 1, 1}, rs1);

        testcaseNotice("批量Insert，查看日志,replace, 307-309，分2批，replace into", "BUG https://github.com/apache/shardingsphere/issues/8226\n");
        val rs2 = dao.batchInsert(rds, 2, false);
        assertArrayEquals(new int[]{1, 1, 1}, rs2);

        testcaseNotice("批量Merge，查看日志,on dupkey, 307-309，分2批，duplicate");
        testcaseNotice("insert into `tst_sharding` (`id`, .., `other_info`) values (?,..., ?) on duplicate key update `login_info` = ?, `other_info` = ?");
        val rs3 = dao.batchMerge(tbl, rds, 2, tbl.LoginInfo, tbl.OtherInfo);
        assertArrayEquals(new int[]{1, 1, 1}, rs3);
    }

    @Test
    public void test4BatchStoreSeeLog() {
        val rds = Arrays.asList(
                new TstShardingRecord(310L, now, now, now, 9L, "批量加载310", "", ZH_CN),
                new TstShardingRecord(311L, now, now, now, 9L, "批量加载311", "", ZH_CN),
                new TstShardingRecord(312L, now, now, now, 9L, "批量加载312", "merge", ZH_CN)
        );
        testcaseNotice("批量Insert，查看日志,ignore, 307-309，分2批插入");
        val rs = dao.batchStore(rds, 2);
        assertArrayEquals(new int[]{1, 1, 1}, rs);
    }

    @Test
    public void test5BatchUpdateSeeLog() {
        val rds = Arrays.asList(
                new TstShardingRecord(309L, now, now, now, 9L, "批量加载309", "update", ZH_CN),
                new TstShardingRecord(310L, now, now, now, 9L, "批量加载310", "update", ZH_CN),
                new TstShardingRecord(311L, now, now, now, 9L, "批量加载311", "update", ZH_CN)
        );
        testcaseNotice("批量Update，查看日志 307-309，分2批更新");
        val rs1 = dao.batchUpdate(rds, 2);
        assertArrayEquals(new int[]{1, 1, 1}, rs1);

        val rs2 = dao.batchUpdate(tbl, new Field[]{tbl.Id}, rds, 2, tbl.LoginInfo, tbl.OtherInfo);
        assertArrayEquals(new int[]{1, 1, 1}, rs2);
    }

    @Test
    public void test6SingleMergeSeeLog() {
        testcaseNotice("insert into `tst_sharding` (`id`, .., `other_info`) values (?,..., ?) on duplicate key update `login_info` = ?, `other_info` = ?");
        TstSharding pojo = new TstSharding(312L, now, now, now, 9L, "批量加载312", "update-bymerge", ZH_CN);
        val rs = dao.mergeInto(tbl, pojo, tbl.LoginInfo, tbl.OtherInfo);
        assertEquals(2, rs);
    }

    @Test
    public void test7BatchMergeSeeLog() {
        val rds = Arrays.asList(
                new TstShardingRecord(313L, now, now, now, 9L, "批量合并313-merge", "update-merge", ZH_CN),
                new TstShardingRecord(310L, now, now, now, 9L, "批量合并310-merge", "update-merge", ZH_CN),
                new TstShardingRecord(311L, now, now, now, 9L, "批量合并311-merge", "update-merge", ZH_CN)
        );
        testcaseNotice("313 insert, 310,311 update");
        val rs = dao.batchMerge(tbl, new Field[]{tbl.Id}, rds, 2, tbl.LoginInfo, tbl.OtherInfo);
        assertArrayEquals(new int[]{1, 1, 1}, rs);
    }

    @Test
    public void test8LogicDeleteSeeLog() {
        dao.fetchById(1L);
        dao.fetchOneById(1L);
        dao.count();
        final TstShardingTable tbl = dao.getTable();
        dao.count(tbl, tbl.getOnlyLive());
    }
}
