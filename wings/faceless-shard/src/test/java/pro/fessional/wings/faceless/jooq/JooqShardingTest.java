package pro.fessional.wings.faceless.jooq;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.app.database.autogen.tables.TstShardingTable;
import pro.fessional.wings.faceless.app.database.autogen.tables.daos.TstShardingDao;
import pro.fessional.wings.faceless.app.database.autogen.tables.pojos.TstSharding;
import pro.fessional.wings.faceless.app.database.autogen.tables.records.TstShardingRecord;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.SchemaShardingManager;
import pro.fessional.wings.faceless.helper.WingsTestHelper;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.SortedMap;

import static pro.fessional.wings.faceless.enums.autogen.StandardLanguage.ZH_CN;
import static pro.fessional.wings.faceless.helper.WingsTestHelper.REVISION_TEST_V1;
import static pro.fessional.wings.faceless.helper.WingsTestHelper.testcaseNotice;

/**
 * @author trydofor
 * @since 2019-06-20
 */


@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
@SpringBootTest
@DependsOnDatabaseInitialization
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class JooqShardingTest {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod_ = {@Autowired})
    private SchemaShardingManager schemaShardingManager;

    @Setter(onMethod_ = {@Autowired})
    private TstShardingDao dao;

    @Setter(onMethod_ = {@Autowired})
    private WingsTestHelper wingsTestHelper;

    @Test
    @TmsLink("C12135")
    public void test0CleanTables() {
        wingsTestHelper.cleanTable();
        final SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls = FlywaveRevisionScanner.scanMaster();
        schemaRevisionManager.checkAndInitSql(sqls, 0, true);
    }

    @Test
    @TmsLink("C12136")
    public void test1PublishTest() {
        schemaRevisionManager.publishRevision(REVISION_TEST_V1, 0);
    }

    @Test
    @TmsLink("C12137")
    public void test3SplitTable5() {
        schemaShardingManager.publishShard("tst_sharding", 5);
    }

    private Long id = 1L;//lightIdService.getId(TstShardingTable.class);

    @Test
    @TmsLink("C12138")
    public void test4InsertSeeLog() {
        var rd = new TstSharding(id,
                LocalDateTime.now(),
                EmptyValue.DATE_TIME,
                EmptyValue.DATE_TIME,
                0L,
                EmptyValue.VARCHAR,
                EmptyValue.VARCHAR,
                ZH_CN.getId()
        );
        // insert into `tst_sharding` (`id`, `create_dt`, `modify_dt`, `commit_id`, `login_info`, `other_info`) values (?, ?, ?, ?, ?, ?)
        dao.insert(rd);

        testcaseNotice(
                "==== check sql log ====",
                "[OK] insert into `tst_sharding_0` (`ID`, `CREATE_DT`, `MODIFY_DT`, `COMMIT_ID`, `LOGIN_INFO`, `OTHER_INFO`) values (?, ?, ?, ?, ?, ?)",
                "[NG] insert into `tst_sharding` as `t1` (`ID`, `CREATE_DT`, `MODIFY_DT`, `COMMIT_ID`, `LOGIN_INFO`, `OTHER_INFO`) values (?, ?, ?, ?, ?, ?)"
        );
//        dsl.newRecord(TstShardingTable.TstSharding, rd).insert()
    }

    @Test
    @TmsLink("C12139")
    public void test5UpdateSeeLog() {
        var tp = TstShardingTable.TstSharding;
        // update `tst_sharding` set `modify_dt` = ?, `login_info` = ? where `id` <= ?
        var rp = dao.ctx().update(tp)
                    .set(tp.ModifyDt, LocalDateTime.now())
                    .set(tp.LoginInfo, "update 5")
                    .where(tp.Id.eq(id))
                    .execute();
        testcaseNotice("plain updated= $rp");
        testcaseNotice("update `tst_sharding_1` set `modify_dt` = ?, `login_info` = ? where `id` = ?");

        var tw = dao.getTable();
        var rw = dao.ctx().update(tw)
                    .set(tw.ModifyDt, LocalDateTime.now())
                    .set(tw.LoginInfo, "update 5")
                    .where(tw.Id.eq(id))
                    .execute();
        testcaseNotice("write updated= $rw");
        testcaseNotice("update `tst_sharding_1` set `modify_dt` = ?, `login_info` = ? where `id` = ?");

        var tr = dao.getAlias();
        var rr = dao.ctx().update(tr)
                    .set(tr.ModifyDt, LocalDateTime.now())
                    .set(tr.LoginInfo, "update 5")
                    .where(tr.Id.eq(id))
                    .execute();
        testcaseNotice("read  updated= $rr");
        testcaseNotice("update `tst_sharding_1` as `y8` set `y8`.`modify_dt` = ?, `y8`.`login_info` = ? where `y8`.`id` = ?");


        testcaseNotice(
                "==== check sql log ====",
                "[OK] update `tst_sharding` set `MODIFY_DT` = ?, `LOGIN_INFO` = ? where `ID` <= ?",
                "[OK] update `tst_sharding` as `t1` set `t1`.`MODIFY_DT` = ?, `t1`.`LOGIN_INFO` = ? where `t1`.`ID` <= ?",
                "[NG] update `tst_sharding` set `tst_sharding`.`MODIFY_DT` = ?, `tst_sharding`.`LOGIN_INFO` = ? where `tst_sharding`.`ID` <= ?"
        );
    }

    @Test
    @TmsLink("C12140")
    public void test6SelectSeeLog() {
        try (HintManager it = HintManager.getInstance()) {
            it.setWriteRouteOnly();
            var ta = TstShardingTable.asP1;
            var ra = dao.ctx().select(ta.Id)
                        .from(ta)
                        .where(ta.Id.le(id))
                        .limit(DSL.inline(1)) // RC3
                        .getSQL();
//                .fetchOne().into(Long::class.java)
            testcaseNotice("alias select", ra);
            testcaseNotice("select `y8`.`id` from `tst_sharding` as `y8` where `y8`.`id` <= ?");

            var tp = TstShardingTable.TstSharding;
            var rp = dao.ctx().select(tp.Id)
                        .from(tp)
                        .where(tp.Id.le(id))
//                .limit(1) // https://github.com/apache/incubator-shardingsphere/issues/3330
                        .getSQL();
//                .fetchOne().into(Long::class.java)
            testcaseNotice("plain select", rp);
            testcaseNotice("select `id` from `tst_sharding` where `id` <= ?");

            var da = dao.getAlias();
            var rd = dao.fetch(da, da.Id.eq(id));
            testcaseNotice("dao select= $rd");
            testcaseNotice("select `y8`.`id`, `y8`.`create_dt`, ... from `tst_sharding` as `y8` where `y8`.`id` = ?");

            testcaseNotice(
                    "==== check sql log ====",
                    "[OK] select `ID` from `tst_sharding` where `ID` <= ? limit ?",
                    "[OK] select `t1`.`ID` from `tst_sharding` as `t1` where `t1`.`ID` <= ? limit ?",
                    "[NG] select `tst_sharding`.`ID` from `tst_sharding` where `tst_sharding`.`ID` <= ? limit ?"
            );
        }
    }

    @Test
    @TmsLink("C12141")
    public void test7DeleteSeeLog() {
        var tp = TstShardingTable.TstSharding;
        var rp = dao.ctx().delete(tp)
                    .where(tp.Id.eq(id)) // Inline strategy cannot support range sharding.
                    .and(tp.CommitId.isNotNull())
                    .getSQL();
//                .execute()
        testcaseNotice("plain delete= $rp");
        testcaseNotice("delete from `tst_sharding` where (`id` <= ? and `commit_id` is not null)");

        var dw = dao.getTable();
        var rw = dao.delete(dw, dw.Id.eq(id));
        testcaseNotice("dao delete= $rw");
        testcaseNotice("delete from `tst_sharding_3` where `id` = ? ");

        testcaseNotice(
                "==== check sql log ====",
                "[OK] delete from `tst_sharding` where `ID` <= ?",
                "[NG] delete from `tst_sharding` where `tst_sharding`.`ID` <= ?",
                "[NG] delete `t1` from `tst_sharding` as `t1` where `t1`.`ID` <= ?"
        );
    }

    private LocalDateTime now = LocalDateTime.now();
    private TstShardingTable tbl = TstShardingTable.TstSharding;

    @Test
    @TmsLink("C12142")
    public void test8BatchSeeLog() {
        var rds = Arrays.asList(
                new TstShardingRecord(119L, now, now, now, 9L, "Batch merge 119", "test8", ZH_CN.getId()),
                new TstShardingRecord(308L, now, now, now, 9L, "Batch merge 308", "test8", ZH_CN.getId()),
                new TstShardingRecord(309L, now, now, now, 9L, "Batch merge 309", "test8", ZH_CN.getId())
        );
        testcaseNotice("Batch Insert, check log, ignore in 2 batch, 119 ignore; 308, 309 insert");
        var rs1 = dao.batchInsert(rds, 2, true);
        Assertions.assertArrayEquals(new int[]{1, 1, 1}, rs1);

        testcaseNotice("select first, then insert 310, or update 308, 309");
        var rs3 = dao.batchMerge(tbl, new Field[]{tbl.Id}, Arrays.asList(
                new TstShardingRecord(310L, now, now, now, 9L, "Batch merge 310", "Other 310", ZH_CN.getId()),
                new TstShardingRecord(308L, now, now, now, 9L, "Batch merge 308", "Other 308", ZH_CN.getId()),
                new TstShardingRecord(309L, now, now, now, 9L, "Batch merge 309", "Other 309", ZH_CN.getId())
        ), 2, tbl.LoginInfo, tbl.OtherInfo);
        Assertions.assertArrayEquals(new int[]{1, 1, 1}, rs3);
    }

    @Test
    @TmsLink("C12143")
    public void test9BatchSeeLog() {
        var rds = Arrays.asList(
                new TstShardingRecord(119L, now, now, now, 9L, "Batch load 307", "test9", ZH_CN.getId()),
                new TstShardingRecord(318L, now, now, now, 9L, "Batch load 318", "test9", ZH_CN.getId()),
                new TstShardingRecord(319L, now, now, now, 9L, "Batch load 319", "test9", ZH_CN.getId())
        );
        testcaseNotice("Batch Insert, check log, replace 119, new318,319, in 2 batch, replace into");
        try {
            var rs2 = dao.batchInsert(rds, 2, false);
            log.info(Arrays.toString(rs2));
            Assertions.assertArrayEquals(new int[]{2, 1, 1}, rs2);
        }
        catch (Exception e) {
            testcaseNotice("Sharding unsupported, replace into https://github.com/apache/shardingsphere/issues/5330");
            log.info("Sharding unsupported", e);
        }

        testcaseNotice("Batch Merge, check log, new 320, on dupkey 318,319, in 2 batch, duplicate");
        testcaseNotice("insert into `tst_sharding` (`id`, .., `other_info`) values (?,..., ?) on duplicate key update `login_info` = ?, `other_info` = ?");
        try {
            var rs3 = dao.batchMerge(tbl, Arrays.asList(
                    new TstShardingRecord(320L, now, now, now, 9L, "Batch merge 320", "Other 320", ZH_CN.getId()),
                    new TstShardingRecord(318L, now, now, now, 9L, "Batch merge 318", "Other 318", ZH_CN.getId()),
                    new TstShardingRecord(319L, now, now, now, 9L, "Batch merge 319", "Other 319", ZH_CN.getId())
            ), 2, tbl.LoginInfo, tbl.OtherInfo);
            log.info(Arrays.toString(rs3));
            Assertions.assertArrayEquals(new int[]{1, 2, 2}, rs3);
        }
        catch (Exception e) {
            testcaseNotice("Sharding unsupported, on duplicate key update https://github.com/apache/shardingsphere/issues/5210");
            testcaseNotice("Sharding unsupported, https://github.com/apache/shardingsphere/pull/5423");
            log.info("Sharding unsupported", e);
        }
    }
}
