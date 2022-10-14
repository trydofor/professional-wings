package pro.fessional.wings.faceless.jooq;

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
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.WingsTestHelper;
import pro.fessional.wings.faceless.database.autogen.tables.Tst中文也分表Table;
import pro.fessional.wings.faceless.database.autogen.tables.daos.Tst中文也分表Dao;
import pro.fessional.wings.faceless.database.autogen.tables.records.Tst中文也分表Record;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.spring.prop.FacelessJooqEnabledProp;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import static pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V2;
import static pro.fessional.wings.faceless.WingsTestHelper.testcaseNotice;

/**
 * SimpleFlatMapper 比较不错，但有intoArray的bug
 * https://github.com/arnaudroger/SimpleFlatMapper/issues/764
 * <p>
 * SimpleFlatMapper 不支持 int.class, 仅Integer.class
 *
 * @author trydofor
 * @since 2020-08-14
 */

@SpringBootTest(properties = {
        "debug = true",
        "logging.level.org.jooq.tools.LoggerListener=debug"
})
@TestMethodOrder(MethodOrderer.MethodName.class)
public class JooqMapperCompatibleTest {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod_ = {@Autowired})
    private FacelessJooqEnabledProp facelessJooqEnabledProp;

    @Setter(onMethod_ = {@Autowired})
    private WingsTestHelper wingsTestHelper;

    @Setter(onMethod_ = {@Autowired})
    private Tst中文也分表Dao dao;

    @Test
    public void test0Init() {
        wingsTestHelper.cleanTable();
        val sqls = FlywaveRevisionScanner.scanMaster();
        schemaRevisionManager.checkAndInitSql(sqls, 0, false);
        schemaRevisionManager.publishRevision(REVISION_TEST_V2, 0);
    }

    // 同名
    @Data
    public static class SameName {
        private Long id;
        private String loginInfo;
    }

    @Test
    public void test1Lower() {
        DSLContext ctx = dao.ctx();
        Tst中文也分表Table t = dao.getTable();
        Condition c = t.Id.gt(1L).and(t.Id.le(105L));

        testcaseNotice("采用区分大小写的别名，jooq不支持，sfm支持");
        SameName vo1 = ctx.select(t.Id, t.LoginInfo.as("logininfo"))
                          .from(t)
                          .where(c)
                          .limit(1)
                          .fetchOneInto(SameName.class);

        Assertions.assertNotNull(vo1);
        Assertions.assertNull(vo1.getLoginInfo(), "Jooq区分大小写");
    }

    @Test
    public void test1Snake() {
        DSLContext ctx = dao.ctx();
        Tst中文也分表Table t = dao.getTable();
        Condition c = t.Id.gt(1L).and(t.Id.le(105L));

        testcaseNotice("采用下划线的别名，jooq和sfm都支持");
        SameName vo2 = ctx.select(t.Id, t.LoginInfo.as("login_info"))
                          .from(t)
                          .where(c)
                          .limit(1)
                          .fetchOneInto(SameName.class);

        Assertions.assertNotNull(vo2);
        Assertions.assertNotNull(vo2.getLoginInfo());
    }

    @Test
    public void test1Array() {

        testcaseNotice("sfm有bug");
        SameName vo = new SameName();
        vo.setId(101L);
        vo.setLoginInfo("login-info test");

        final Tst中文也分表Record rd = dao.newRecord(vo);
        final Field<?>[] fld = dao.getTable().fields();
        final Object[] arr = rd.intoArray();

        Assertions.assertNotNull(rd.getLoginInfo());
        Assertions.assertEquals(fld.length, arr.length, "Sfm bug https://github.com/arnaudroger/SimpleFlatMapper/issues/764");
    }

    @Test
    public void test2Array() {
        Tst中文也分表Table t = dao.getTable();
        Condition c = t.Id.eq(105L);
        final Tst中文也分表Record rd = dao.ctx()
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
