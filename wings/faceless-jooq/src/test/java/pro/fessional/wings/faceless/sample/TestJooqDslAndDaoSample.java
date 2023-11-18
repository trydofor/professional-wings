package pro.fessional.wings.faceless.sample;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.SelectConditionStep;
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
import pro.fessional.wings.faceless.database.jooq.helper.JournalDiffHelper;
import pro.fessional.wings.faceless.database.jooq.helper.JournalJooqHelper;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.service.journal.JournalDiff;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pro.fessional.wings.faceless.convention.EmptyValue.DATE_TIME;
import static pro.fessional.wings.faceless.enums.autogen.StandardLanguage.ZH_CN;
import static pro.fessional.wings.faceless.helper.WingsTestHelper.REVISION_TEST_V1;
import static pro.fessional.wings.faceless.helper.WingsTestHelper.testcaseNotice;
import static pro.fessional.wings.faceless.service.journal.JournalService.Journal;

/**
 * @author trydofor
 * @since 2019-06-20
 */


@SpringBootTest
@DependsOnDatabaseInitialization
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class TestJooqDslAndDaoSample {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod_ = {@Autowired})
    private TstShardingDao dao;

    @Test
    @TmsLink("C12112")
    public void test0Init() {
        val sqls = FlywaveRevisionScanner.scanMaster();
        schemaRevisionManager.checkAndInitSql(sqls, 0, true);
        schemaRevisionManager.publishRevision(REVISION_TEST_V1, 0);
    }

    @Test
    @TmsLink("C12113")
    public void test1Dao() {

        testcaseNotice("Use alias");
        val a = dao.getAlias();
        val c = a.Id.gt(1L).and(a.CommitId.lt(200L));

        testcaseNotice("select count(*) from `tst_sharding` as `y8` where (`y8`.`id` = ? and `y8`.`commit_id` = ?)");
        val i = dao.count(a, c);
        testcaseNotice("select * from `tst_sharding` as `y8` where (`y8`.`id` = ? and `y8`.`commit_id` = ?) limit ?");
        val ft1 = dao.fetch(a, 0, 2, c, a.Id.desc());
        log.info("============count {}, ft2'size={}", i, ft1.size());
        testcaseNotice("select id, commit_id  from `tst_sharding` as `y8` where (`y8`.`id` = ? and `y8`.`commit_id` = ?) limit ?");
        val ft2 = dao.fetch(0, 2, (t, w) -> w
                .where(t.Id.gt(1L).and(t.CommitId.lt(200L)))
                .query(t.Id, t.CommitId, t.Id.desc()));
        log.info("============count {}, ft2'size={}", i, ft2.size());

        // table
        testcaseNotice("Use table");
        val t = dao.getTable();
        val setter = new HashMap<>();
        setter.put(t.LoginInfo, "info");
        setter.put(t.CommitId, t.Id.add(1L));
        testcaseNotice("update `tst_sharding` set `commit_id` = (`id` + ?), `login_info` = ? where `id` = ?");
        val u1 = dao.update(t, setter, t.Id.eq(2L));
        log.info("============update {}", u1);

        val po = new TstSharding();
        po.setCommitId(2L);
        po.setLoginInfo("info");
        testcaseNotice("update `tst_sharding` set `commit_id` = ?, `login_info` = ? where `id` = ?");
        val u2 = dao.update(t, po, t.Id.eq(2L));
        log.info("============update {}", u2);
    }

    @Test
    @TmsLink("C12114")
    public void test2Dsl() {
        testcaseNotice("Get Dsl by dao.ctx()");
        Condition nullCond = null;
        Field<Long> nullField = null;
//        val nullOrder: OrderField<Long>? = null
        val emptyOrder = new OrderField[]{};
        val t = TstShardingTable.TstSharding;
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
        int rc = dsl.execute("DELETE FROM tst_sharding WHERE id < ?", 1L);
    }

    @Test
    @TmsLink("C12115")
    public void test3Journal() {
        testcaseNotice("Journal Feature");

        val now = LocalDateTime.now();
        val journal = new Journal(1L, now, "", "", "", "");

        val s1 = new HashMap<>();
        val t = TstShardingTable.TstSharding;
        JournalJooqHelper.create(journal, t, s1);
        log.info("map1={}", s1);

        val s2 = new HashMap<>();
        JournalJooqHelper.modify(journal, t, s2);
        log.info("map2={}", s2);
        val s3 = new HashMap<>();
        JournalJooqHelper.delete(journal, t, s3);
        log.info("map3={}", s3);

        val s4 = new HashMap<>();
        val ob = new TstSharding();
        val start1 = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            JournalJooqHelper.create(journal, t, s4);
        }
        val start2 = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            journal.create(ob);
        }
        val start3 = System.currentTimeMillis();
        log.info("cost1={}, cost2={}", start2 - start1, start3 - start2);
    }

    @Test
    @TmsLink("C12116")
    public void test4DeleteDt() {
        testcaseNotice("Logic delete");
        val c1 = dao.count();
        log.info("count1={}", c1);
        val c2 = dao.count(TstShardingTable::getOnlyDied);
        log.info("count2={}", c2);
    }

    @Test
    @TmsLink("C12117")
    public void test4Shadow() {
        testcaseNotice("Shadow table");
        TstShardingTable upd = dao.newTable("", "_postfix");
        val c1 = dao.count(upd, null);
        log.info("count1={}", c1);
    }

    @Test
    @TmsLink("C12118")
    public void test5DiffDao() {
        testcaseNotice("Diff Dao");
        TstSharding po = new TstSharding();
        final long id = 20221024L;
        final TstShardingTable t = dao.getTable();
        final LocalDateTime now = LocalDateTime.of(2022, 10, 24, 12, 34, 56);

        po.setId(id);
        po.setCreateDt(now);
        po.setModifyDt(DATE_TIME);
        po.setDeleteDt(DATE_TIME);
        po.setCommitId(id);
        po.setLoginInfo("login by diff insert");
        po.setOtherInfo("other by diff insert");
        po.setLanguage(ZH_CN);

        final JournalDiff d0 = dao.diffInsert(po);
        log.warn("diffInsert0={}", d0);
        Assertions.assertNotNull(d0);
        Assertions.assertEquals(1, d0.getCount());
        Assertions.assertEquals(t.getName(), d0.getTable());
        final List<String> fds = Arrays.stream(t.fields()).map(Field::getName).collect(Collectors.toList());
        Assertions.assertEquals(fds, d0.getColumn());
        Assertions.assertTrue(d0.getValue1().isEmpty());
        Assertions.assertEquals(Arrays.asList(id, now, DATE_TIME, DATE_TIME, id, "login by diff insert", "other by diff insert", ZH_CN), d0.getValue2());

        po.setId(id + 1);
        final JournalDiff d1 = dao.diffInsert(po);
        log.warn("diffInsert1={}", d1);
        Assertions.assertNotNull(d1);
        Assertions.assertEquals(Arrays.asList(id + 1, now, DATE_TIME, DATE_TIME, id, "login by diff insert", "other by diff insert", ZH_CN), d1.getValue2());

        Map<Field<?>, Object> setter = new LinkedHashMap<>();
        setter.put(t.CommitId, t.CommitId.add(1));
        setter.put(t.LoginInfo, "login by diff update");

        final JournalDiff d2 = dao.diffUpdate(t, setter, t.Id.ge(id));
        log.warn("diffUpdate2={}", d2);
        Assertions.assertNotNull(d2);
        Assertions.assertEquals(2, d2.getCount());
        Assertions.assertEquals(t.getName(), d2.getTable());
        Assertions.assertEquals(Arrays.asList(t.CommitId.getName(), t.LoginInfo.getName()), d2.getColumn());
        Assertions.assertEquals(Arrays.asList(id, "login by diff insert", id, "login by diff insert"), d2.getValue1());
        Assertions.assertEquals(Arrays.asList(id + 1, "login by diff update", id + 1, "login by diff update"), d2.getValue2());


        final JournalDiff d3 = dao.diffDelete(t, t.Id.ge(id));
        log.warn("diffDelete3={}", d3);
        JournalDiffHelper.tidy(d3, t.Language); // withDefault
        Assertions.assertNotNull(d3);
        Assertions.assertEquals(2, d3.getCount());
        Assertions.assertEquals(t.getName(), d3.getTable());
        Assertions.assertEquals(Arrays.asList(t.Id.getName(), t.LoginInfo.getName(), t.OtherInfo.getName()), d3.getColumn());
        Assertions.assertEquals(Arrays.asList(
                id, "login by diff update", "other by diff insert",
                id + 1, "login by diff update", "other by diff insert"
        ), d3.getValue1());
        Assertions.assertTrue(d3.getValue2().isEmpty());
    }

    @Test
    @TmsLink("C12119")
    public void test5DiffDsl() {
        testcaseNotice("Diff Dsl");
        TstSharding po = new TstSharding();
        final long id = 20221024L;
        final TstShardingTable t = dao.getTable();
        final DSLContext dsl = dao.ctx();
        final LocalDateTime now = LocalDateTime.of(2022, 10, 24, 12, 34, 56);

        po.setId(id);
        po.setCreateDt(now);
        po.setModifyDt(DATE_TIME);
        po.setDeleteDt(DATE_TIME);
        po.setCommitId(id);
        po.setLoginInfo("login by diff insert");
        po.setOtherInfo("other by diff insert");
        po.setLanguage(ZH_CN);

        final SelectConditionStep<TstShardingRecord> query = dsl.selectFrom(t).where(t.Id.eq(id));
        final JournalDiff d0 = JournalDiffHelper.diffInsert(t, query, () -> dao.insert(po));
        log.warn("diffInsert0={}", d0);
        Assertions.assertNotNull(d0);
        Assertions.assertEquals(1, d0.getCount());
        Assertions.assertEquals(t.getName(), d0.getTable());
        final List<String> fds = Arrays.stream(t.fields()).map(Field::getName).collect(Collectors.toList());
        Assertions.assertEquals(fds, d0.getColumn());
        Assertions.assertTrue(d0.getValue1().isEmpty());
        Assertions.assertEquals(Arrays.asList(id, now, DATE_TIME, DATE_TIME, id, "login by diff insert", "other by diff insert", ZH_CN), d0.getValue2());

        final JournalDiff d2 = JournalDiffHelper.diffUpdate(t, query, () ->
                dsl.update(t)
                   .set(t.CommitId, t.CommitId.add(1))
                   .set(t.LoginInfo, "login by diff update")
                   .set(t.ModifyDt, now)
                   .where(t.Id.eq(id))
                   .execute());
        log.warn("diffUpdate2={}", d2);
        Assertions.assertNotNull(d2);
        Assertions.assertEquals(1, d2.getCount());
        Assertions.assertEquals(t.getName(), d2.getTable());
        Assertions.assertEquals(fds, d2.getColumn());
        Assertions.assertEquals(Arrays.asList(id, now, DATE_TIME, DATE_TIME, id, "login by diff insert", "other by diff insert", ZH_CN), d2.getValue1());
        Assertions.assertEquals(Arrays.asList(id, now, now, DATE_TIME, id + 1, "login by diff update", "other by diff insert", ZH_CN), d2.getValue2());
        JournalDiffHelper.tidy(d2);
        Assertions.assertEquals(List.of(t.LoginInfo.getName()), d2.getColumn());
        Assertions.assertEquals(List.of("login by diff insert"), d2.getValue1());
        Assertions.assertEquals(List.of("login by diff update"), d2.getValue2());


        final JournalDiff d3 = JournalDiffHelper.diffDelete(t, query, () -> dsl.delete(t).where(t.Id.ge(id)));
        log.warn("diffDelete3={}", d3);
        JournalDiffHelper.tidy(d3, t.Language); // withDefault
        Assertions.assertNotNull(d3);
        Assertions.assertEquals(1, d3.getCount());
        Assertions.assertEquals(t.getName(), d3.getTable());
        Assertions.assertEquals(Arrays.asList(t.Id.getName(), t.LoginInfo.getName(), t.OtherInfo.getName()), d3.getColumn());
        Assertions.assertEquals(Arrays.asList(id, "login by diff update", "other by diff insert"), d3.getValue1());
        Assertions.assertTrue(d3.getValue2().isEmpty());
    }
}
