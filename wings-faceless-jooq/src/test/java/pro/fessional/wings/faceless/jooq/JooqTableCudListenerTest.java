package pro.fessional.wings.faceless.jooq;

import lombok.Setter;
import lombok.val;
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
import pro.fessional.wings.faceless.database.WingsTableCudHandler;
import pro.fessional.wings.faceless.database.autogen.tables.Tst中文也分表Table;
import pro.fessional.wings.faceless.database.autogen.tables.daos.Tst中文也分表Dao;
import pro.fessional.wings.faceless.database.autogen.tables.pojos.Tst中文也分表;
import pro.fessional.wings.faceless.database.autogen.tables.records.Tst中文也分表Record;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.service.WingsTableCudHandlerTest;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.SortedMap;

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
        "spring.wings.faceless.jooq.enabled.listen-table-cud=true"
})
@Tag("init")
public class JooqTableCudListenerTest {

    @Setter(onMethod_ = {@Autowired})
    private Tst中文也分表Dao testDao;

    @Setter(onMethod_ = {@Autowired})
    private WingsTestHelper wingsTestHelper;

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager revisionManager;

    @Setter(onMethod_ = {@Autowired})
    private WingsTableCudHandlerTest wingsTableCudHandlerTest;

    @Test
    public void test0Init() {
        wingsTestHelper.cleanTable();
        final SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls = FlywaveRevisionScanner.scan(REVISION_PATH_MASTER);
        revisionManager.checkAndInitSql(sqls, 0, true);
        revisionManager.publishRevision(REVISION_TEST_V2, -1);
    }

    @Test
    public void test1Create() {
        testcaseNotice("单个插入");
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

        wingsTableCudHandlerTest.reset();
        testDao.insertInto(pojo, true);
        Assertions.assertEquals(WingsTableCudHandler.Cud.Create, wingsTableCudHandlerTest.getCud());
        Assertions.assertEquals("tst_中文也分表", wingsTableCudHandlerTest.getTable());
        Assertions.assertEquals(1, wingsTableCudHandlerTest.getCount());

        wingsTableCudHandlerTest.reset();
        testDao.insertInto(pojo, false);
        Assertions.assertEquals(WingsTableCudHandler.Cud.Create, wingsTableCudHandlerTest.getCud());
        Assertions.assertEquals("tst_中文也分表", wingsTableCudHandlerTest.getTable());
        Assertions.assertEquals(1, wingsTableCudHandlerTest.getCount());

        final Tst中文也分表Table t = testDao.getTable();
        final long c1 = testDao.count(t.Id.eq(301L));
        Assertions.assertEquals(1L, c1);

        testcaseNotice("批量插入");
        val rds = Arrays.asList(
                new Tst中文也分表Record(302L, now, now, now, 9L, "login-info-303", "", ZH_CN),
                new Tst中文也分表Record(303L, now, now, now, 9L, "login-info-304", "", ZH_CN)
        );

//        TableCudListener.WarnVisit =true;
        wingsTableCudHandlerTest.reset();
        testDao.batchInsert(rds,10);
        Assertions.assertEquals(WingsTableCudHandler.Cud.Create, wingsTableCudHandlerTest.getCud());
        Assertions.assertEquals("tst_中文也分表", wingsTableCudHandlerTest.getTable());
        Assertions.assertEquals(1, wingsTableCudHandlerTest.getCount());

        wingsTableCudHandlerTest.reset();
        testDao.batchInsert(rds,10, true);
        Assertions.assertEquals(WingsTableCudHandler.Cud.Create, wingsTableCudHandlerTest.getCud());
        Assertions.assertEquals("tst_中文也分表", wingsTableCudHandlerTest.getTable());
        Assertions.assertEquals(1, wingsTableCudHandlerTest.getCount());

        wingsTableCudHandlerTest.reset();
        testDao.batchInsert(rds,10, false);
        Assertions.assertEquals(WingsTableCudHandler.Cud.Create, wingsTableCudHandlerTest.getCud());
        Assertions.assertEquals("tst_中文也分表", wingsTableCudHandlerTest.getTable());
        Assertions.assertEquals(1, wingsTableCudHandlerTest.getCount());

        final long c2 = testDao.count(t.Id.ge(302L).and(t.Id.le(303L)));
        Assertions.assertEquals(2L, c2);
    }

    @Test
    public void test2Update() {
        testcaseNotice("单个更新");
        final Tst中文也分表Table t = testDao.getTable();
        Tst中文也分表 pojo = new Tst中文也分表();
        pojo.setId(301L);
        pojo.setCommitId(-301L);
        wingsTableCudHandlerTest.reset();
        testDao.update(pojo,true);
        Assertions.assertEquals(WingsTableCudHandler.Cud.Update, wingsTableCudHandlerTest.getCud());
        Assertions.assertEquals("tst_中文也分表", wingsTableCudHandlerTest.getTable());
        Assertions.assertEquals(1, wingsTableCudHandlerTest.getCount());

        final long c1 = testDao.count(t.CommitId.eq(-301L));
        Assertions.assertEquals(1L, c1);

        testcaseNotice("批量更新");
        wingsTableCudHandlerTest.reset();
        testDao.ctx()
               .update(t)
               .set(t.CommitId, -302L)
               .where(t.Id.in(302L,303L).and(t.Id.ge(302L).and(t.Id.le(303L))))
               .execute();
        Assertions.assertEquals(WingsTableCudHandler.Cud.Update, wingsTableCudHandlerTest.getCud());
        Assertions.assertEquals("tst_中文也分表", wingsTableCudHandlerTest.getTable());
        Assertions.assertEquals(1, wingsTableCudHandlerTest.getCount());

        final long c2 = testDao.count(t.CommitId.eq(-302L));
        Assertions.assertEquals(2L, c2);
    }

    @Test
    public void test4Delete() {
        testcaseNotice("单个删除");
        wingsTableCudHandlerTest.reset();
        final Tst中文也分表Table t = testDao.getTable();
        testDao.ctx()
               .delete(t)
               .where(t.Id.eq(301L))
               .execute();
        Assertions.assertEquals(WingsTableCudHandler.Cud.Delete, wingsTableCudHandlerTest.getCud());
        Assertions.assertEquals("tst_中文也分表", wingsTableCudHandlerTest.getTable());
        Assertions.assertEquals(1, wingsTableCudHandlerTest.getCount());

        wingsTableCudHandlerTest.reset();
        testDao.ctx()
               .delete(t)
               .where(t.Id.ge(302L).and(t.Id.le(304L)))
               .execute();
        Assertions.assertEquals(WingsTableCudHandler.Cud.Delete, wingsTableCudHandlerTest.getCud());
        Assertions.assertEquals("tst_中文也分表", wingsTableCudHandlerTest.getTable());
        Assertions.assertEquals(1, wingsTableCudHandlerTest.getCount());
        final long c1 = testDao.count(t.Id.ge(301L).and(t.Id.le(304L)));
        Assertions.assertEquals(0L, c1);
        Assertions.assertEquals(1, wingsTableCudHandlerTest.getCount());
    }
}
