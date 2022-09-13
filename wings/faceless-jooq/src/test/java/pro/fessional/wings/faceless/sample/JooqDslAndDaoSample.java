package pro.fessional.wings.faceless.sample;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.OrderField;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.database.autogen.tables.Tst中文也分表Table;
import pro.fessional.wings.faceless.database.autogen.tables.daos.Tst中文也分表Dao;
import pro.fessional.wings.faceless.database.autogen.tables.pojos.Tst中文也分表;
import pro.fessional.wings.faceless.database.jooq.SelectOrderCondition;
import pro.fessional.wings.faceless.database.jooq.helper.JournalJooqHelp;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.time.LocalDateTime;
import java.util.HashMap;

import static pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V1;
import static pro.fessional.wings.faceless.WingsTestHelper.testcaseNotice;
import static pro.fessional.wings.faceless.service.journal.JournalService.Journal;

/**
 * @author trydofor
 * @since 2019-06-20
 */


@SpringBootTest(properties = {"debug = true", "logging.level.org.jooq.tools.LoggerListener=DEBUG"})
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled("手动执行，以有JooqShardingTest覆盖测试 ")
@Slf4j
public class JooqDslAndDaoSample {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod_ = {@Autowired})
    private Tst中文也分表Dao dao;

    @Test
    public void test0Init() {
        val sqls = FlywaveRevisionScanner.scanMaster();
        schemaRevisionManager.checkAndInitSql(sqls, 0, false);
        schemaRevisionManager.publishRevision(REVISION_TEST_V1, 0);
    }

    @Test
    public void test1Dao() {

        testcaseNotice("使用alias");
        val a = dao.getAlias();
        val c = a.Id.gt(1L).and(a.CommitId.lt(200L));

        testcaseNotice("select count(*) from `tst_中文也分表` as `y8` where (`y8`.`id` = ? and `y8`.`commit_id` = ?)");
        val i = dao.count(a, c);
        testcaseNotice("select * from `tst_中文也分表` as `y8` where (`y8`.`id` = ? and `y8`.`commit_id` = ?) limit ?");
        val ft1 = dao.fetch(a, 0, 2, c, a.Id.desc());
        log.info("============count {}, ft2'size={}", i, ft1.size());
        testcaseNotice("select id, commit_id  from `tst_中文也分表` as `y8` where (`y8`.`id` = ? and `y8`.`commit_id` = ?) limit ?");
        val ft2 = dao.fetch(0, 2, t -> SelectOrderCondition.of(
                t.Id.gt(1L).and(t.CommitId.lt(200L)),
                t.Id, t.CommitId, t.Id.desc()));
        log.info("============count {}, ft2'size={}", i, ft2.size());

        // table
        testcaseNotice("使用table");
        val t = dao.getTable();
        val setter = new HashMap<>();
        setter.put(t.LoginInfo, "info");
        setter.put(t.CommitId, t.Id.add(1L));
        testcaseNotice("update `tst_中文也分表` set `commit_id` = (`id` + ?), `login_info` = ? where `id` = ?");
        val u1 = dao.update(t, setter, t.Id.eq(2L));
        log.info("============update {}", u1);

        val po = new Tst中文也分表();
        po.setCommitId(2L);
        po.setLoginInfo("info");
        testcaseNotice("update `tst_中文也分表` set `commit_id` = ?, `login_info` = ? where `id` = ?");
        val u2 = dao.update(t, po, t.Id.eq(2L));
        log.info("============update {}", u2);
    }

    @Test
    public void test2Dsl() {
        testcaseNotice("通过dao.ctx()获得dsl能力");
        Condition nullCond = null;
        Field<Long> nullField = null;
//        val nullOrder: OrderField<Long>? = null
        val emptyOrder = new OrderField[]{null};
        val t = Tst中文也分表Table.Tst中文也分表;
        DSLContext dsl = dao.ctx();
        val sql = dsl.select(t.Id, nullField) // null safe
                     .from(t)
                     .where(nullCond)  // null safe
                     .orderBy(emptyOrder) // empty safe
//                .orderBy(t.Id, nullOrder) // IllegalArgumentException: Field not supported : null
//                .orderBy(nullOrder) // IllegalArgumentException: Field not supported : null
                     .getSQL();
        log.info(sql);

        testcaseNotice("plain sql delete");
        int rc = dsl.execute("DELETE FROM tst_中文也分表 WHERE id < ?", 1L);
    }

    @Test
    public void test3Journal() {
        testcaseNotice("日志功能");

        val now = LocalDateTime.now();
        val journal = new Journal(1L, now, "", "", "", "");

        val s1 = new HashMap<>();
        val t = Tst中文也分表Table.Tst中文也分表;
        JournalJooqHelp.create(journal, t, s1);
        log.info("map1={}", s1);

        val s2 = new HashMap<>();
        JournalJooqHelp.modify(journal, t, s2);
        log.info("map2={}", s2);
        val s3 = new HashMap<>();
        JournalJooqHelp.delete(journal, t, s3);
        log.info("map3={}", s3);

        val s4 = new HashMap<>();
        val ob = new Tst中文也分表();
        val start1 = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            JournalJooqHelp.create(journal, t, s4);
        }
        val start2 = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            journal.create(ob);
        }
        val start3 = System.currentTimeMillis();
        log.info("cost1={}, cost2={}", start2 - start1, start3 - start2);
    }

    @Test
    public void test4DeleteDt() {
        testcaseNotice("逻辑删除");
        val c1 = dao.count();
        log.info("count1={}", c1);
        val c2 = dao.count(it -> it.onlyDiedData);
        log.info("count2={}", c2);
    }

    @Test
    public void test4Shadow() {
        testcaseNotice("影子表");
        Tst中文也分表Table upd = dao.newTable("", "$upd");
        val c1 = dao.count(upd, null);
        log.info("count1={}", c1);
    }
}
