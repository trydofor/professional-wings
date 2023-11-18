package pro.fessional.wings.faceless.jooq;

import io.qameta.allure.TmsLink;
import lombok.Data;
import lombok.Setter;
import lombok.val;
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
import pro.fessional.wings.faceless.helper.WingsTestHelper;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import static pro.fessional.wings.faceless.helper.WingsTestHelper.REVISION_TEST_V2;
import static pro.fessional.wings.faceless.helper.WingsTestHelper.testcaseNotice;

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
    private WingsTestHelper wingsTestHelper;

    @Setter(onMethod_ = {@Autowired})
    private TstShardingDao dao;

    @Test
    @TmsLink("C12098")
    public void test0Init() {
        wingsTestHelper.cleanTable();
        val sqls = FlywaveRevisionScanner.scanMaster();
        schemaRevisionManager.checkAndInitSql(sqls, 0, false);
        schemaRevisionManager.publishRevision(REVISION_TEST_V2, 0);
    }

    @Test
    @TmsLink("C12099")
    public void test1Exist() {
        final boolean b = dao.notTableExist();
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
        DSLContext ctx = dao.ctx();
        TstShardingTable t = dao.getTable();
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
        DSLContext ctx = dao.ctx();
        TstShardingTable t = dao.getTable();
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

        final TstShardingRecord rd = dao.newRecord(vo);
        final Field<?>[] fld = dao.getTable().fields();
        final Object[] arr = rd.intoArray();

        Assertions.assertNotNull(rd.getLoginInfo());
        Assertions.assertEquals(fld.length, arr.length, "Sfm bug https://github.com/arnaudroger/SimpleFlatMapper/issues/764");
    }

    @Test
    @TmsLink("C12103")
    public void test2Array() {
        TstShardingTable t = dao.getTable();
        Condition c = t.Id.eq(105L);
        final TstShardingRecord rd = dao.ctx()
                                        .selectFrom(t)
                                        .where(c)
                                        .fetchOne();
        Assertions.assertNotNull(rd);
        Assertions.assertNotNull(rd.getLoginInfo());
        final Object[] arr = rd.intoArray();
        final Field<?>[] fld = dao.getTable().fields();
        Assertions.assertEquals(fld.length, arr.length, "Sfm bug https://github.com/arnaudroger/SimpleFlatMapper/issues/764");
    }
}
