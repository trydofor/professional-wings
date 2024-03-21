package pro.fessional.wings.faceless.sample;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
import pro.fessional.wings.faceless.spring.prop.FacelessJooqConfProp;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;
import pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper;
import pro.fessional.wings.testing.silencer.TestingLoggerAssert;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static pro.fessional.wings.faceless.convention.EmptyValue.DATE_TIME;
import static pro.fessional.wings.faceless.enums.autogen.StandardLanguage.ZH_CN;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V90_22_0601_01_TestSchema;
import static pro.fessional.wings.faceless.service.journal.JournalService.Journal;
import static pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper.testcaseNotice;

/**
 * @author trydofor
 * @since 2019-06-20
 */


@SpringBootTest(properties = {
        "logging.level.root=DEBUG", // AssertionLogger
        "wings.faceless.jooq.conf.auto-qualify=true",
        "wings.faceless.jooq.conf.render-table=ALWAYS",
//        "wings.faceless.jooq.conf.auto-qualify=false",
//        "wings.faceless.jooq.conf.render-table=WHEN_MULTIPLE_TABLES",
})
@DependsOnDatabaseInitialization
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class TestJooqDslAndDaoSample {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod_ = {@Autowired})
    private TestingDatabaseHelper testingDatabaseHelper;

    @Setter(onMethod_ = {@Autowired})
    private TstShardingDao tstShardingDao;

    @Setter(onMethod_ = {@Autowired})
    private FacelessJooqConfProp prop;

    @Test
    @TmsLink("C12112")
    public void test0Init() {
        testingDatabaseHelper.cleanTable();
        var sqls = FlywaveRevisionScanner.scanMaster();
        schemaRevisionManager.checkAndInitSql(sqls, 0, true);
        schemaRevisionManager.publishRevision(V90_22_0601_01_TestSchema.revision(), 0);
    }

    @Test
    @TmsLink("C12113")
    public void test1Dao() {
        final TestingLoggerAssert al = TestingLoggerAssert.install();
        final Pattern alias = prop.isAutoQualify()
                              ? Pattern.compile("from `tst_sharding` as `(\\w+)` where \\(`\\1`.`id` > \\? and `\\1`.`commit_id` < \\?\\)")
                              : Pattern.compile("from `tst_sharding` as `(\\w+)` where \\(`id` > \\? and `commit_id` < \\?\\)");
        al.rule("alias-count", event -> alias.matcher(event.getFormattedMessage()).find());
        al.rule("table-select", event -> event.getFormattedMessage().contains("from `tst_sharding` where (`id` > ? and `commit_id` < ?)"));
        al.rule("table-update1", event -> event.getFormattedMessage().contains("update `tst_sharding` set `commit_id` = (`id` + ?), `login_info` = ? where `id` = ?"));
        al.rule("table-update2", event -> event.getFormattedMessage().contains("update `tst_sharding` set `commit_id` = ?, `login_info` = ? where `id` = ?"));
        al.start();

        testcaseNotice("Use alias");
        final var a = tstShardingDao.getAlias();
        final var c = a.Id.gt(1L).and(a.CommitId.lt(200L));

//        testcaseNotice("select count(*) from `tst_sharding` as `y8` where (`y8`.`id` = ? and `y8`.`commit_id` = ?)");
        final var i = tstShardingDao.count(a, c);
//        testcaseNotice("select * from `tst_sharding` as `y8` where (`y8`.`id` = ? and `y8`.`commit_id` = ?) limit ?");
        final var ft1 = tstShardingDao.fetch(a, 0, 2, c, a.Id.desc());
        log.info("============count {}, ft2'size={}", i, ft1.size());
//        testcaseNotice("select `id`, `commit_id` from `tst_sharding` where (`id` > ? and `commit_id` < ?) order by `id` desc limit ? offset ?");
        final var ft2 = tstShardingDao.fetch(0, 2, (t, w) -> w
                .where(t.Id.gt(1L).and(t.CommitId.lt(200L)))
                .query(t.Id, t.CommitId, t.Id.desc()));
        log.info("============count {}, ft2'size={}", i, ft2.size());

        // table
        testcaseNotice("Use table");
        final var t = tstShardingDao.getTable();
        final var setter = new LinkedHashMap<>();
        setter.put(t.CommitId, t.Id.add(1L));
        setter.put(t.LoginInfo, "info");
//        testcaseNotice("update `tst_sharding` set `commit_id` = (`id` + ?), `login_info` = ? where `id` = ?");
        final var u1 = tstShardingDao.update(t, setter, t.Id.eq(2L));
        log.info("============update {}", u1);

        final var po = new TstSharding();
        po.setCommitId(2L);
        po.setLoginInfo("info");
//        testcaseNotice("update `tst_sharding` set `commit_id` = ?, `login_info` = ? where `id` = ?");
        final var u2 = tstShardingDao.update(t, po, t.Id.eq(2L));
        log.info("============update {}", u2);

        al.stop();
        al.assertCount(1);
        al.uninstall();
    }

    @Test
    @TmsLink("C12114")
    public void test2Dsl() {
        testcaseNotice("Get Dsl by dao.ctx()");
        Condition nullCond = null;
        Field<Long> nullField = null;
//        final var nullOrder: OrderField<Long>? = null
        final var emptyOrder = new OrderField[]{};
        final var t = TstShardingTable.TstSharding;
        DSLContext dsl = tstShardingDao.ctx();
        final var sql = dsl.select(t.Id, nullField) // null safe
                           .from(t)
                           .where(nullCond)  // null safe
                           .orderBy(emptyOrder) // empty safe
//                .orderBy(t.Id, nullOrder) // IllegalArgumentException: Field not supported : null
//                .orderBy(nullOrder) // IllegalArgumentException: Field not supported : null
                           .getSQL();
        log.info(sql);
        Assertions.assertTrue(sql.contains("select `id` from `tst_sharding`"));

        testcaseNotice("plain sql delete");
        int rc = dsl.execute("DELETE FROM tst_sharding WHERE id < ?", 1L);

        // https://github.com/trydofor/professional-wings/issues/172

        final var t1 = TstShardingTable.TstSharding.as("t1");
        final var t2 = TstShardingTable.TstSharding.as("t2");
        String j1 = dsl
                .select(t1.Id, t2.CommitId)
                .from(t1, t2)
                .where(t1.Id.eq(t2.CommitId))
                .getSQL();

        String j2 = dsl
                .select(t1.Id)
                .from(t1)
                .where(t1.CommitId.in(dsl.select(t2.CommitId).from(t2).where(t2.Id.eq(t1.Id))))
                .getSQL();

        String j3 = dsl
                .select(t1.Id, t2.Id)
                .from(t1)
                .join(t2)
                .on(t1.Id.eq(t2.Id).and(t1.CommitId.eq(t2.CommitId)))
                .where(t1.Id.eq(1L))
                .getSQL();

        log.info(j1);
        log.info(j2);
        log.info(j3);
    }

    @Test
    @TmsLink("C12115")
    public void test3Journal() {
        testcaseNotice("Journal Feature");

        final var now = LocalDateTime.now();
        final var journal = new Journal(1L, now, "", "", "", "");

        final var s1 = new HashMap<>();
        final var t = TstShardingTable.TstSharding;
        JournalJooqHelper.create(journal, t, s1);
        log.info("map1={}", s1);

        final var s2 = new HashMap<>();
        JournalJooqHelper.modify(journal, t, s2);
        log.info("map2={}", s2);
        final var s3 = new HashMap<>();
        JournalJooqHelper.delete(journal, t, s3);
        log.info("map3={}", s3);

        final var s4 = new HashMap<>();
        final var ob = new TstSharding();
        final var start1 = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            JournalJooqHelper.create(journal, t, s4);
        }
        final var start2 = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            journal.create(ob);
        }
        final var start3 = System.currentTimeMillis();
        log.info("cost1={}, cost2={}", start2 - start1, start3 - start2);
    }

    @Test
    @TmsLink("C12116")
    public void test4DeleteDt() {
        testcaseNotice("Logic delete");
        final var c1 = tstShardingDao.count();
        log.info("count1={}", c1);
        final var c2 = tstShardingDao.count(TstShardingTable::getOnlyDied);
        log.info("count2={}", c2);
    }

    @Test
    @TmsLink("C12117")
    public void test4Shadow() {
        testcaseNotice("Shadow table");
        TstShardingTable upd = tstShardingDao.newTable("", "_postfix");
        final var c1 = tstShardingDao.count(upd, null);
        log.info("count1={}", c1);
    }

    @Test
    @TmsLink("C12118")
    public void test5DiffDao() {
        testcaseNotice("Diff Dao");
        TstSharding po = new TstSharding();
        final long id = 20221024L;
        final TstShardingTable t = tstShardingDao.getTable();
        final LocalDateTime now = LocalDateTime.of(2022, 10, 24, 12, 34, 56);

        po.setId(id);
        po.setCreateDt(now);
        po.setModifyDt(DATE_TIME);
        po.setDeleteDt(DATE_TIME);
        po.setCommitId(id);
        po.setLoginInfo("login by diff insert");
        po.setOtherInfo("other by diff insert");
        po.setLanguage(ZH_CN);

        final JournalDiff d0 = tstShardingDao.diffInsert(po);
        log.warn("diffInsert0={}", d0);
        Assertions.assertNotNull(d0);
        Assertions.assertEquals(1, d0.getCount());
        Assertions.assertEquals(t.getName(), d0.getTable());
        final List<String> fds = Arrays.stream(t.fields()).map(Field::getName).collect(Collectors.toList());
        Assertions.assertEquals(fds, d0.getColumn());
        Assertions.assertTrue(d0.getValue1().isEmpty());
        Assertions.assertEquals(Arrays.asList(id, now, DATE_TIME, DATE_TIME, id, "login by diff insert", "other by diff insert", ZH_CN), d0.getValue2());

        po.setId(id + 1);
        final JournalDiff d1 = tstShardingDao.diffInsert(po);
        log.warn("diffInsert1={}", d1);
        Assertions.assertNotNull(d1);
        Assertions.assertEquals(Arrays.asList(id + 1, now, DATE_TIME, DATE_TIME, id, "login by diff insert", "other by diff insert", ZH_CN), d1.getValue2());

        Map<Field<?>, Object> setter = new LinkedHashMap<>();
        setter.put(t.CommitId, t.CommitId.add(1));
        setter.put(t.LoginInfo, "login by diff update");

        final JournalDiff d2 = tstShardingDao.diffUpdate(t, setter, t.Id.ge(id));
        log.warn("diffUpdate2={}", d2);
        Assertions.assertNotNull(d2);
        Assertions.assertEquals(2, d2.getCount());
        Assertions.assertEquals(t.getName(), d2.getTable());
        Assertions.assertEquals(Arrays.asList(t.CommitId.getName(), t.LoginInfo.getName()), d2.getColumn());
        Assertions.assertEquals(Arrays.asList(id, "login by diff insert", id, "login by diff insert"), d2.getValue1());
        Assertions.assertEquals(Arrays.asList(id + 1, "login by diff update", id + 1, "login by diff update"), d2.getValue2());


        final JournalDiff d3 = tstShardingDao.diffDelete(t, t.Id.ge(id));
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
        final TstShardingTable t = tstShardingDao.getTable();
        final DSLContext dsl = tstShardingDao.ctx();
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
        final JournalDiff d0 = JournalDiffHelper.diffInsert(t, query, () -> tstShardingDao.insert(po));
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
