package pro.fessional.wings.faceless.jooq;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pro.fessional.wings.faceless.WingsTestHelper;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.database.autogen.tables.Tst中文也分表Table;
import pro.fessional.wings.faceless.database.autogen.tables.daos.Tst中文也分表Dao;
import pro.fessional.wings.faceless.database.autogen.tables.pojos.Tst中文也分表;
import pro.fessional.wings.faceless.database.autogen.tables.records.Tst中文也分表Record;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.SchemaShardingManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.SortedMap;

import static pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V1;
import static pro.fessional.wings.faceless.WingsTestHelper.testcaseNotice;
import static pro.fessional.wings.faceless.enums.autogen.StandardLanguage.ZH_CN;

/**
 * @author trydofor
 * @since 2019-06-20
 */


@SpringBootTest(properties = {"debug = true", "logging.level.org.jooq.tools.LoggerListener=DEBUG"})
@ActiveProfiles("shard")
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class JooqShardingTest {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod_ = {@Autowired})
    private SchemaShardingManager schemaShardingManager;

    @Setter(onMethod_ = {@Autowired})
    private Tst中文也分表Dao dao;

    @Setter(onMethod_ = {@Autowired})
    private WingsTestHelper wingsTestHelper;

    @Test
    public void test0𓃬清表重置() {
        wingsTestHelper.cleanTable();
        final SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls = FlywaveRevisionScanner.scanMaster();
        schemaRevisionManager.checkAndInitSql(sqls, 0, true);
    }

    @Test
    public void test1𓃬发测试版() {
        schemaRevisionManager.publishRevision(REVISION_TEST_V1, 0);
    }

    @Test
    public void test3𓃬分五张表() {
        schemaShardingManager.publishShard("tst_中文也分表", 5);
    }

    private Long id = 1L;//lightIdService.getId(Tst中文也分表Table.class);

    @Test
    public void test4𓃬插入𓃬查日志() {
        val rd = new Tst中文也分表(id,
                LocalDateTime.now(),
                EmptyValue.DATE_TIME,
                EmptyValue.DATE_TIME,
                0L,
                EmptyValue.VARCHAR,
                EmptyValue.VARCHAR,
                ZH_CN.getId()
        );
        // insert into `tst_中文也分表` (`id`, `create_dt`, `modify_dt`, `commit_id`, `login_info`, `other_info`) values (?, ?, ?, ?, ?, ?)
        dao.insert(rd);

        testcaseNotice(
                "==== 检查 sql 日志 ====",
                "[OK] insert into `tst_中文也分表_0` (`ID`, `CREATE_DT`, `MODIFY_DT`, `COMMIT_ID`, `LOGIN_INFO`, `OTHER_INFO`) values (?, ?, ?, ?, ?, ?)",
                "[NG] insert into `TST_中文也分表` as `t1` (`ID`, `CREATE_DT`, `MODIFY_DT`, `COMMIT_ID`, `LOGIN_INFO`, `OTHER_INFO`) values (?, ?, ?, ?, ?, ?)"
        );
//        dsl.newRecord(Tst中文也分表Table.TST_中文也分表, rd).insert()
    }

    @Test
    public void test5𓃬更新𓃬查日志() {
        val tp = Tst中文也分表Table.Tst中文也分表;
        // update `tst_中文也分表` set `modify_dt` = ?, `login_info` = ? where `id` <= ?
        val rp = dao.ctx().update(tp)
                    .set(tp.ModifyDt, LocalDateTime.now())
                    .set(tp.LoginInfo, "update 5")
                    .where(tp.Id.eq(id))
                    .execute();
        testcaseNotice("plain updated= $rp");
        testcaseNotice("update `tst_中文也分表_1` set `modify_dt` = ?, `login_info` = ? where `id` = ?");

        val tw = dao.getTable();
        val rw = dao.ctx().update(tw)
                    .set(tw.ModifyDt, LocalDateTime.now())
                    .set(tw.LoginInfo, "update 5")
                    .where(tw.Id.eq(id))
                    .execute();
        testcaseNotice("write updated= $rw");
        testcaseNotice("update `tst_中文也分表_1` set `modify_dt` = ?, `login_info` = ? where `id` = ?");

        val tr = dao.getAlias();
        val rr = dao.ctx().update(tr)
                    .set(tr.ModifyDt, LocalDateTime.now())
                    .set(tr.LoginInfo, "update 5")
                    .where(tr.Id.eq(id))
                    .execute();
        testcaseNotice("read  updated= $rr");
        testcaseNotice("update `tst_中文也分表_1` as `y8` set `y8`.`modify_dt` = ?, `y8`.`login_info` = ? where `y8`.`id` = ?");


        testcaseNotice(
                "==== 检查 sql 日志 ====",
                "[OK] update `TST_中文也分表` set `MODIFY_DT` = ?, `LOGIN_INFO` = ? where `ID` <= ?",
                "[OK] update `TST_中文也分表` as `t1` set `t1`.`MODIFY_DT` = ?, `t1`.`LOGIN_INFO` = ? where `t1`.`ID` <= ?",
                "[NG] update `TST_中文也分表` set `TST_中文也分表`.`MODIFY_DT` = ?, `TST_中文也分表`.`LOGIN_INFO` = ? where `TST_中文也分表`.`ID` <= ?"
        );
    }

    @Test
    public void test6𓃬查询𓃬查日志() {
        try (HintManager it = HintManager.getInstance()) {
            it.setWriteRouteOnly();
            val ta = Tst中文也分表Table.asY8;
            val ra = dao.ctx().select(ta.Id)
                        .from(ta)
                        .where(ta.Id.le(id))
                        .limit(DSL.inline(1)) // RC3
                        .getSQL();
//                .fetchOne().into(Long::class.java)
            testcaseNotice("alias select", ra);
            testcaseNotice("select `y8`.`id` from `tst_中文也分表` as `y8` where `y8`.`id` <= ?");

            val tp = Tst中文也分表Table.Tst中文也分表;
            val rp = dao.ctx().select(tp.Id)
                        .from(tp)
                        .where(tp.Id.le(id))
//                .limit(1) // https://github.com/apache/incubator-shardingsphere/issues/3330
                        .getSQL();
//                .fetchOne().into(Long::class.java)
            testcaseNotice("plain select", rp);
            testcaseNotice("select `id` from `tst_中文也分表` where `id` <= ?");

            val da = dao.getAlias();
            val rd = dao.fetch(da, da.Id.eq(id));
            testcaseNotice("dao select= $rd");
            testcaseNotice("select `y8`.`id`, `y8`.`create_dt`, ... from `tst_中文也分表` as `y8` where `y8`.`id` = ?");

            testcaseNotice(
                    "==== 检查 sql 日志 ====",
                    "[OK] select `ID` from `TST_中文也分表` where `ID` <= ? limit ?",
                    "[OK] select `t1`.`ID` from `TST_中文也分表` as `t1` where `t1`.`ID` <= ? limit ?",
                    "[NG] select `TST_中文也分表`.`ID` from `TST_中文也分表` where `TST_中文也分表`.`ID` <= ? limit ?"
            );
        }
    }

    @Test
    public void test7𓃬删除𓃬查日志() {
        val tp = Tst中文也分表Table.Tst中文也分表;
        val rp = dao.ctx().delete(tp)
                    .where(tp.Id.eq(id)) // Inline strategy cannot support range sharding.
                    .and(tp.CommitId.isNotNull())
                    .getSQL();
//                .execute()
        testcaseNotice("plain delete= $rp");
        testcaseNotice("delete from `tst_中文也分表` where (`id` <= ? and `commit_id` is not null)");

        val dw = dao.getTable();
        val rw = dao.delete(dw, dw.Id.eq(id));
        testcaseNotice("dao delete= $rw");
        testcaseNotice("delete from `tst_中文也分表_3` where `id` = ? ");

        testcaseNotice(
                "==== 检查 sql 日志 ====",
                "[OK] delete from `TST_中文也分表` where `ID` <= ?",
                "[NG] delete from `TST_中文也分表` where `TST_中文也分表`.`ID` <= ?",
                "[NG] delete `t1` from `TST_中文也分表` as `t1` where `t1`.`ID` <= ?"
        );
    }

    private LocalDateTime now = LocalDateTime.now();
    private Tst中文也分表Table tbl = Tst中文也分表Table.Tst中文也分表;

    @Test
    public void test8𓃬批量𓃬查日志() {
        val rds = Arrays.asList(
                new Tst中文也分表Record(119L, now, now, now, 9L, "批量合并119", "test8", ZH_CN.getId()),
                new Tst中文也分表Record(308L, now, now, now, 9L, "批量合并308", "test8", ZH_CN.getId()),
                new Tst中文也分表Record(309L, now, now, now, 9L, "批量合并309", "test8", ZH_CN.getId())
        );
        testcaseNotice("批量Insert，查看日志,ignore, 分2批次， 119 ignore; 308，309 insert");
        val rs1 = dao.batchInsert(rds, 2, true);
        Assertions.assertArrayEquals(new int[]{1, 1, 1}, rs1);

        testcaseNotice("先select在insert 310，或update 308，309");
        val rs3 = dao.batchMerge(tbl, new Field[]{tbl.Id}, Arrays.asList(
                new Tst中文也分表Record(310L, now, now, now, 9L, "批量合并310", "其他310", ZH_CN.getId()),
                new Tst中文也分表Record(308L, now, now, now, 9L, "批量合并308", "其他308", ZH_CN.getId()),
                new Tst中文也分表Record(309L, now, now, now, 9L, "批量合并309", "其他309", ZH_CN.getId())
        ), 2, tbl.LoginInfo, tbl.OtherInfo);
        Assertions.assertArrayEquals(new int[]{1, 1, 1}, rs3);
    }

    @Test
    public void test9𓃬批量𓃬有bug() {
        val rds = Arrays.asList(
                new Tst中文也分表Record(119L, now, now, now, 9L, "批量加载307", "test9", ZH_CN.getId()),
                new Tst中文也分表Record(318L, now, now, now, 9L, "批量加载318", "test9", ZH_CN.getId()),
                new Tst中文也分表Record(319L, now, now, now, 9L, "批量加载319", "test9", ZH_CN.getId())
        );
        testcaseNotice("批量Insert，查看日志,replace, 307-309，分2批，replace into");
        try {
            val rs2 = dao.batchInsert(rds, 2, false);
            log.info("{}", Arrays.toString(rs2));
            //assertArrayEquals(intArrayOf(2, 2, 2), rs2)
        } catch (Exception e) {
            testcaseNotice("Sharding 不支持，replace into https://github.com/apache/shardingsphere/issues/5330");
            e.printStackTrace();
        }

        testcaseNotice("批量Merge，查看日志,on dupkey, 307-309，分2批，duplicate");
        testcaseNotice("insert into `tst_中文也分表` (`id`, .., `other_info`) values (?,..., ?) on duplicate key update `login_info` = ?, `other_info` = ?");
        try {
            val rs3 = dao.batchMerge(tbl, Arrays.asList(
                    new Tst中文也分表Record(320L, now, now, now, 9L, "批量合并320", "其他320", ZH_CN.getId()),
                    new Tst中文也分表Record(318L, now, now, now, 9L, "批量合并318", "其他318", ZH_CN.getId()),
                    new Tst中文也分表Record(319L, now, now, now, 9L, "批量合并319", "其他319", ZH_CN.getId())
            ), 2, tbl.LoginInfo, tbl.OtherInfo);
            log.info("{}", Arrays.toString(rs3));
            //assertArrayEquals(intArrayOf(1, 1, 1), rs3)
        } catch (Exception e) {
            testcaseNotice("Sharding 不支持，on duplicate key update https://github.com/apache/shardingsphere/issues/5210");
            testcaseNotice("Sharding 不支持，https://github.com/apache/shardingsphere/pull/5423");
            e.printStackTrace();
        }
    }
}
