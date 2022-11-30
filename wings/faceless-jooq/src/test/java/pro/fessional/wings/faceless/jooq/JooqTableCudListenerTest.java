package pro.fessional.wings.faceless.jooq;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pro.fessional.wings.faceless.WingsTestHelper;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.database.WingsTableCudHandler.Cud;
import pro.fessional.wings.faceless.database.autogen.tables.Tst中文也分表Table;
import pro.fessional.wings.faceless.database.autogen.tables.daos.Tst中文也分表Dao;
import pro.fessional.wings.faceless.database.autogen.tables.pojos.Tst中文也分表;
import pro.fessional.wings.faceless.database.autogen.tables.records.Tst中文也分表Record;
import pro.fessional.wings.faceless.database.jooq.listener.TableCudListener;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.service.WingsTableCudHandlerTest;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import static java.util.Collections.singletonList;
import static pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V2;
import static pro.fessional.wings.faceless.WingsTestHelper.testcaseNotice;
import static pro.fessional.wings.faceless.enums.autogen.StandardLanguage.ZH_CN;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_MASTER;

/**
 * @author trydofor
 * @since 2019-09-27
 */

@TestMethodOrder(MethodOrderer.MethodName.class)
@ActiveProfiles("init")
@SpringBootTest(properties = {
        "debug = true",
        "wings.faceless.jooq.cud.table[tst_中文也分表]=id,login_info",
        "spring.wings.faceless.jooq.enabled.listen-table-cud=true"
})
@Tag("init")
@Slf4j
public class JooqTableCudListenerTest {

    private static final AtomicReference<String> LastSql = new AtomicReference<>();
    public static BiConsumer<Long, String> SlowSql = (c, s) -> {
        log.warn("SLOW-SQL,cost={}, sql={}", c, s);
        LastSql.set(s);
    };

    @Setter(onMethod_ = {@Autowired})
    private Tst中文也分表Dao testDao;

    @Setter(onMethod_ = {@Autowired})
    private WingsTestHelper wingsTestHelper;

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod_ = {@Autowired})
    private WingsTableCudHandlerTest wingsTableCudHandlerTest;

    @Test
    public void test0Init() {
        wingsTestHelper.cleanTable();
        final SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls = FlywaveRevisionScanner.scan(REVISION_PATH_MASTER);
        schemaRevisionManager.checkAndInitSql(sqls, 0, true);
        schemaRevisionManager.publishRevision(REVISION_TEST_V2, -1);
    }

    @Test
    public void test1Create() {

        final LocalDateTime now = LocalDateTime.now();
        Tst中文也分表 pojo = new Tst中文也分表();
        pojo.setId(301L);
        pojo.setCommitId(-1L);
        pojo.setCreateDt(now);
        pojo.setModifyDt(EmptyValue.DATE_TIME);
        pojo.setDeleteDt(EmptyValue.DATE_TIME);
        pojo.setLanguage(ZH_CN);
        pojo.setLoginInfo("login-info-301");
        pojo.setOtherInfo("other-info-301");

        testcaseNotice("单个插入 normal");
        assertCud(false, Cud.Create, singletonList(singletonList(301L)), () -> testDao.insert(pojo),
                "insert into");


        testcaseNotice("单个插入 ignore");
        assertCud(false, Cud.Create, singletonList(singletonList(301L)), () -> testDao.insertInto(pojo, true),
                "insert ignore into");

        testcaseNotice("单个插入 replace");
        assertCud(false, Cud.Create, singletonList(singletonList(301L)), () -> testDao.insertInto(pojo, false),
                "duplicate key update");

        final Tst中文也分表Table t = testDao.getTable();
        final long c1 = testDao.count(t, t.Id.eq(301L));
        Assertions.assertEquals(1L, c1);

        val rds = Arrays.asList(
                new Tst中文也分表Record(302L, now, now, now, 9L, "login-info-302", "", ZH_CN),
                new Tst中文也分表Record(303L, now, now, now, 9L, "login-info-303", "", ZH_CN),
                new Tst中文也分表Record(304L, now, now, now, 9L, "login-info-304", "", ZH_CN)
        );

        testcaseNotice("批量插入 normal");
        assertCud(false, Cud.Create, Arrays.asList(singletonList(302L), singletonList(303L), singletonList(304L)), () -> testDao.batchInsert(rds, 10),
                "insert into");

        testcaseNotice("批量插入 ignore");
        assertCud(false, null, Collections.emptyList(), () -> testDao.batchInsert(rds, 10, true),
                "insert ignore into");

        testcaseNotice("批量插入 replace");
        assertCud(false, null, Collections.emptyList(), () -> testDao.batchInsert(rds, 10, false),
                "duplicate key update");

        final long c2 = testDao.count(t, t.Id.ge(302L).and(t.Id.le(303L)));
        Assertions.assertEquals(2L, c2);
    }

    @Test
    public void test2Update() {

        final Tst中文也分表Table t = testDao.getTable();
        Tst中文也分表 pojo = new Tst中文也分表();
        pojo.setId(301L);
        pojo.setCommitId(-301L);

        testcaseNotice("单个更新");
        assertCud(true, Cud.Update, singletonList(singletonList(301L)), () -> testDao.update(pojo, true),
                "update");

        final long c1 = testDao.count(t, t.CommitId.eq(-301L));
        Assertions.assertTrue(StringUtils.containsIgnoreCase(LastSql.get(), "select count"));
        Assertions.assertEquals(1L, c1);

        testcaseNotice("批量更新");
        assertCud(false, Cud.Update, singletonList(Arrays.asList(302L, 303L, 302L, 304L)), () -> testDao
                        .ctx()
                        .update(t)
                        .set(t.CommitId, -302L)
                        .where(t.Id.in(302L, 303L).or(t.Id.ge(302L).and(t.Id.le(304L))))
                        .execute(),
                "update");

        final long c2 = testDao.count(t, t.CommitId.eq(-302L));
        Assertions.assertEquals(3L, c2);
    }

    @Test
    public void test4Delete() {
        final Tst中文也分表Table t = testDao.getTable();
        testcaseNotice("单个删除");
        assertCud(false, Cud.Delete, singletonList(singletonList(301L)), () -> testDao
                .ctx()
                .delete(t)
                .where(t.Id.eq(301L))
                .execute(),
                "delete from"
        );

        testcaseNotice("范围删除");
        assertCud(false, Cud.Delete, singletonList(Arrays.asList(302L, 304L)), () -> testDao
                .ctx()
                .delete(t)
                .where(t.Id.ge(302L).and(t.Id.le(304L)))
                .execute(),
                "delete from"
        );


        final long c1 = testDao.count(t, t.Id.ge(301L).and(t.Id.le(304L)));
        Assertions.assertEquals(0L, c1);
    }

    private void assertCud(boolean wv, Cud cud, List<List<Long>> ids, Runnable run, String sqlPart) {
        wingsTableCudHandlerTest.reset();
        TableCudListener.WarnVisit = wv;
        run.run();
        final String sql = LastSql.get();
        Assertions.assertTrue(StringUtils.containsIgnoreCase(sql, sqlPart));

        TableCudListener.WarnVisit = false;
        final List<Cud> d = wingsTableCudHandlerTest.getCud();
        final List<String> t = wingsTableCudHandlerTest.getTable();
        List<Map<String, List<?>>> f = wingsTableCudHandlerTest.getField();

        if (cud == null) {
            Assertions.assertTrue(d.isEmpty());
            Assertions.assertTrue(t.isEmpty());
            Assertions.assertTrue(f.isEmpty());
        }
        else {
            for (int i = 0, l = ids.size(); i < l; i++) {
                Assertions.assertEquals(cud, d.get(i));
                Assertions.assertEquals("tst_中文也分表", t.get(i));
                Assertions.assertEquals(ids.get(i), f.get(i).get("id"));
            }
            Assertions.assertEquals(ids.size(), f.size());
        }
    }
}
