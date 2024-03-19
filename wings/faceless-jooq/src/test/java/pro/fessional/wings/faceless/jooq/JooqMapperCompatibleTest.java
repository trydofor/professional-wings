package pro.fessional.wings.faceless.jooq;

import io.qameta.allure.TmsLink;
import lombok.Data;
import lombok.Setter;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.app.database.autogen.tables.TstShardingTable;
import pro.fessional.wings.faceless.app.database.autogen.tables.daos.TstShardingDao;
import pro.fessional.wings.faceless.app.database.autogen.tables.records.TstShardingRecord;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.WingsRevision;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;
import pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper;

import static pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper.testcaseNotice;

/**
 * @author trydofor
 * @since 2020-08-14
 */

@SpringBootTest
@DependsOnDatabaseInitialization
@TestMethodOrder(MethodOrderer.MethodName.class)
public class JooqMapperCompatibleTest {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod_ = {@Autowired})
    private TestingDatabaseHelper testingDatabaseHelper;

    @Setter(onMethod_ = {@Autowired})
    private TstShardingDao tstShardingDao;

    @Test
    @TmsLink("C12098")
    public void test0Init() {
        testingDatabaseHelper.cleanTable();
        var sqls = FlywaveRevisionScanner.scanMaster();
        schemaRevisionManager.checkAndInitSql(sqls, 0, false);
        schemaRevisionManager.publishRevision(WingsRevision.V90_22_0601_02_TestRecord.revision(), 0);
    }

    @Test
    @TmsLink("C12099")
    public void test1Exist() {
        final boolean b = tstShardingDao.notTableExist();
        Assertions.assertFalse(b);
    }

    @Data
    public static class SameName {
        private Long id;
        private String loginInfo;
    }

    @Test
    @TmsLink("C12100")
    public void test1Lower() {
        DSLContext ctx = tstShardingDao.ctx();
        TstShardingTable t = tstShardingDao.getTable();
        Condition c = t.Id.gt(1L).and(t.Id.le(105L));

        testcaseNotice("Case-sensitive alias, not supported by jooq, supported by sfm");
        SameName vo1 = ctx.select(t.Id, t.LoginInfo.as("logininfo"))
                          .from(t)
                          .where(c)
                          .limit(1)
                          .fetchOneInto(SameName.class);

        Assertions.assertNotNull(vo1);
        Assertions.assertNull(vo1.getLoginInfo(), "Jooq is case-sensitive");
    }

    @Test
    @TmsLink("C12101")
    public void test1Snake() {
        DSLContext ctx = tstShardingDao.ctx();
        TstShardingTable t = tstShardingDao.getTable();
        Condition c = t.Id.gt(1L).and(t.Id.le(105L));

        testcaseNotice("Underscore alias, supported by both jooq and sfm");
        SameName vo2 = ctx.select(t.Id, t.LoginInfo.as("login_info"))
                          .from(t)
                          .where(c)
                          .limit(1)
                          .fetchOneInto(SameName.class);

        Assertions.assertNotNull(vo2);
        Assertions.assertNotNull(vo2.getLoginInfo());
    }

    @Test
    @TmsLink("C12102")
    public void test1Array() {

        testcaseNotice("sfm has bug");
        SameName vo = new SameName();
        vo.setId(101L);
        vo.setLoginInfo("login-info test");

        final TstShardingRecord rd = tstShardingDao.newRecord(vo);
        final Field<?>[] fld = tstShardingDao.getTable().fields();
        final Object[] arr = rd.intoArray();

        Assertions.assertNotNull(rd.getLoginInfo());
        Assertions.assertEquals(fld.length, arr.length, "Sfm bug https://github.com/arnaudroger/SimpleFlatMapper/issues/764");
    }

    @Test
    @TmsLink("C12103")
    public void test2Array() {
        TstShardingTable t = tstShardingDao.getTable();
        Condition c = t.Id.eq(105L);
        final TstShardingRecord rd = tstShardingDao.ctx()
                                                   .selectFrom(t)
                                                   .where(c)
                                                   .fetchOne();
        Assertions.assertNotNull(rd);
        Assertions.assertNotNull(rd.getLoginInfo());
        final Object[] arr = rd.intoArray();
        final Field<?>[] fld = tstShardingDao.getTable().fields();
        Assertions.assertEquals(fld.length, arr.length, "Sfm bug https://github.com/arnaudroger/SimpleFlatMapper/issues/764");
    }
}
