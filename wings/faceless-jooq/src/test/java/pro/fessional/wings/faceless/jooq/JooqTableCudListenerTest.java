package pro.fessional.wings.faceless.jooq;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
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
import pro.fessional.wings.faceless.app.service.TestTableCudHandler;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.database.WingsTableCudHandler.Cud;
import pro.fessional.wings.faceless.database.jooq.listener.TableCudListener;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;
import pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import static java.util.Collections.singletonList;
import static pro.fessional.wings.faceless.enums.autogen.StandardLanguage.ZH_CN;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V90_22_0601_02_TestRecord;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_MASTER;
import static pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper.testcaseNotice;

/**
 * @author trydofor
 * @since 2019-09-27
 */

@SuppressWarnings("CanBeFinal")
@SpringBootTest(properties = {
        "wings.faceless.jooq.cud.table[tst_sharding]=id,login_info",
        "wings.faceless.jooq.conf.listen-cud=true"
})
@DependsOnDatabaseInitialization
@TestMethodOrder(MethodOrderer.MethodName.class)
@ActiveProfiles("init")
@Tag("init")
@Slf4j
public class JooqTableCudListenerTest {

    private static final AtomicReference<String> LastSql = new AtomicReference<>();
    public static BiConsumer<Long, String> SlowSql = (c, s) -> {
        log.warn("SLOW-SQL,cost={}, sql={}", c, s);
        LastSql.set(s);
    };

    @Setter(onMethod_ = {@Autowired})
    private TstShardingDao testDao;

    @Setter(onMethod_ = {@Autowired})
    private TestingDatabaseHelper testingDatabaseHelper;

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod_ = {@Autowired})
    private TestTableCudHandler testTableCudHandler;

    @Test
    @TmsLink("C12104")
    public void test0Init() {
        testingDatabaseHelper.cleanTable();
        var sqls = FlywaveRevisionScanner.scan(REVISION_PATH_MASTER);
        schemaRevisionManager.checkAndInitSql(sqls, 0, true);
        schemaRevisionManager.publishRevision(V90_22_0601_02_TestRecord.revision(), -1);
    }

    @Test
    @TmsLink("C12105")
    public void test1Create() {

        final LocalDateTime now = LocalDateTime.now();
        TstSharding pojo = new TstSharding();
        pojo.setId(301L);
        pojo.setCommitId(-1L);
        pojo.setCreateDt(now);
        pojo.setModifyDt(EmptyValue.DATE_TIME);
        pojo.setDeleteDt(EmptyValue.DATE_TIME);
        pojo.setLanguage(ZH_CN);
        pojo.setLoginInfo("login-info-301");
        pojo.setOtherInfo("other-info-301");

        testcaseNotice("single insert normal");
        assertCud(false, Cud.Create, singletonList(singletonList(301L)), () -> testDao.insert(pojo),
                "insert into");


        testcaseNotice("single insert ignore");
        assertCud(false, Cud.Create, singletonList(singletonList(301L)), () -> testDao.insertInto(pojo, true),
                "insert ignore into");

        testcaseNotice("single insert replace");
        assertCud(false, Cud.Update, singletonList(singletonList(301L)), () -> testDao.insertInto(pojo, false),
                "duplicate key update");

        final TstShardingTable t = testDao.getTable();
        final long c1 = testDao.count(t, t.Id.eq(301L));
        Assertions.assertEquals(1L, c1);

        final var rds = Arrays.asList(
                new TstShardingRecord(302L, now, now, now, 9L, "login-info-302", "", ZH_CN),
                new TstShardingRecord(303L, now, now, now, 9L, "login-info-303", "", ZH_CN),
                new TstShardingRecord(304L, now, now, now, 9L, "login-info-304", "", ZH_CN)
        );

        testcaseNotice("batch insert normal");
        assertCud(false, Cud.Create, Arrays.asList(singletonList(302L), singletonList(303L), singletonList(304L)), () -> testDao.batchInsert(rds, 10),
                "insert into");

        testcaseNotice("batch insert ignore");
        assertCud(false, null, Collections.emptyList(), () -> testDao.batchInsert(rds, 10, true),
                "insert ignore into");

        testcaseNotice("batch insert replace");
        assertCud(false, null, Collections.emptyList(), () -> testDao.batchInsert(rds, 10, false),
                "duplicate key update");

        final long c2 = testDao.count(t, t.Id.ge(302L).and(t.Id.le(303L)));
        Assertions.assertEquals(2L, c2);
    }

    @Test
    @TmsLink("C12106")
    public void test2Update() {

        final TstShardingTable t = testDao.getTable();

        TstSharding pojo = new TstSharding();
        pojo.setId(301L);
        pojo.setCommitId(-301L);

        testcaseNotice("single update");
        assertCud(true, Cud.Update, singletonList(singletonList(301L)), () -> testDao.update(pojo, true),
                "update");

        final long c1 = testDao.count(t, t.CommitId.eq(-301L));
        Assertions.assertTrue(StringUtils.containsIgnoreCase(LastSql.get(), "select count"));
        Assertions.assertEquals(1L, c1);

        testcaseNotice("batch update");
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
    @TmsLink("C12107")
    public void test4Delete() {
        final TstShardingTable t = testDao.getTable();
        testcaseNotice("single delete");
        assertCud(false, Cud.Delete, singletonList(singletonList(301L)), () -> testDao
                        .ctx()
                        .delete(t)
                        .where(t.Id.eq(301L))
                        .execute(),
                "delete from"
        );

        testcaseNotice("batch delete");
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
        testTableCudHandler.reset();
        TableCudListener.WarnVisit = wv;
        run.run();
        final String sql = LastSql.get();
        Assertions.assertTrue(StringUtils.containsIgnoreCase(sql, sqlPart));

        TableCudListener.WarnVisit = false;
        final List<Cud> d = testTableCudHandler.getCud();
        final List<String> t = testTableCudHandler.getTable();
        List<Map<String, List<?>>> f = testTableCudHandler.getField();

        if (cud == null) {
            Assertions.assertTrue(d.isEmpty());
            Assertions.assertTrue(t.isEmpty());
            Assertions.assertTrue(f.isEmpty());
        }
        else {
            for (int i = 0, l = ids.size(); i < l; i++) {
                Assertions.assertEquals(cud, d.get(i));
                Assertions.assertEquals("tst_sharding", t.get(i));
                Assertions.assertEquals(ids.get(i), f.get(i).get("id"));
            }
            Assertions.assertEquals(ids.size(), f.size());
        }
    }
}
