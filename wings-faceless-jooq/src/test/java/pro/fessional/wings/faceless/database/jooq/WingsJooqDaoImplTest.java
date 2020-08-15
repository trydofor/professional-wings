package pro.fessional.wings.faceless.database.jooq;

import lombok.Setter;
import lombok.val;
import org.jooq.Field;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.wings.faceless.WingsTestHelper;
import pro.fessional.wings.faceless.database.autogen.tables.Tst中文也分表Table;
import pro.fessional.wings.faceless.database.autogen.tables.daos.Tst中文也分表Dao;
import pro.fessional.wings.faceless.database.autogen.tables.pojos.Tst中文也分表;
import pro.fessional.wings.faceless.database.autogen.tables.records.Tst中文也分表Record;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;

import java.time.LocalDateTime;
import java.util.Arrays;

import static pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V2;
import static pro.fessional.wings.faceless.WingsTestHelper.testcaseNotice;


/**
 * @author trydofor
 * @since 2020-05-31
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"debug = true", "logging.level.org.jooq.tools.LoggerListener=DEBUG"})
@ActiveProfiles("init")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WingsJooqDaoImplTest {

    @Setter(onMethod = @__({@Autowired}))
    private SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod = @__({@Autowired}))
    private WingsTestHelper wingsTestHelper;

    @Setter(onMethod = @__({@Autowired}))
    private Tst中文也分表Dao dao;

    private final Tst中文也分表Table tbl = Tst中文也分表Table.Tst中文也分表;
    private final LocalDateTime now = LocalDateTime.now();

    @Test
    public void test0𓃬清表重置() {
        wingsTestHelper.cleanAndInit();
        schemaRevisionManager.publishRevision(REVISION_TEST_V2, 0);
    }

    @Test
    public void test1𓃬批量Load𓃬查日志() {
        if (WingsJooqEnv.daoBatchMysql) {
            testcaseNotice("跳过低效的SQL，使用mysql replace into 语法，见 batchMerge");
            return;
        }

        val rds = Arrays.asList(
                new Tst中文也分表Record(301L, now, now, now, 9L, "批量加载301", ""),
                new Tst中文也分表Record(302L, now, now, now, 9L, "批量加载302", ""),
                new Tst中文也分表Record(303L, now, now, now, 9L, "批量加载303", "")
        );
        testcaseNotice("批量Load，查看日志，ignore, 301-303，使用了from dual where exists先查再插");
        dao.batchLoad(rds, true);
        testcaseNotice("批量Load，查看日志，replace, 301-303，使用了on duplicate key update");
        dao.batchLoad(rds, false);
    }

    @Test
    public void test2𓃬分批批量Insert𓃬查日志() {
        val rds = Arrays.asList(
                new Tst中文也分表Record(304L, now, now, now, 9L, "批量加载304", ""),
                new Tst中文也分表Record(305L, now, now, now, 9L, "批量加载305", ""),
                new Tst中文也分表Record(306L, now, now, now, 9L, "批量加载306", "")
        );
        testcaseNotice("批量Insert，查看日志, 304-306，分2批插入");
        val rs = dao.batchInsert(rds, 2);
        Assert.assertArrayEquals(new int[]{1, 1, 1}, rs);
    }

    @Test
    public void test3𓃬分批批量Merge𓃬查日志() {
        val rds = Arrays.asList(
                new Tst中文也分表Record(307L, now, now, now, 9L, "批量加载307", ""),
                new Tst中文也分表Record(308L, now, now, now, 9L, "批量加载308", ""),
                new Tst中文也分表Record(309L, now, now, now, 9L, "批量加载309", "")
        );
        testcaseNotice("批量Insert，查看日志,ignore, 307-309，分2批次， insert ignore");
        val rs1 = dao.batchInsert(rds, 2, true);
        Assert.assertArrayEquals(new int[]{1, 1, 1}, rs1);

        testcaseNotice("批量Insert，查看日志,replace, 307-309，分2批，replace into");
        val rs2 = dao.batchInsert(rds, 2, false);
        Assert.assertArrayEquals(new int[]{1, 1, 1}, rs2);

        testcaseNotice("批量Merge，查看日志,on dupkey, 307-309，分2批，duplicate");
        testcaseNotice("insert into `tst_中文也分表` (`id`, .., `other_info`) values (?,..., ?) on duplicate key update `login_info` = ?, `other_info` = ?");
        val rs3 = dao.batchMerge(rds, 2, tbl.LoginInfo, tbl.OtherInfo);
        Assert.assertArrayEquals(new int[]{1, 1, 1}, rs3);
    }

    @Test
    public void test4𓃬分批批量Store𓃬查日志() {
        val rds = Arrays.asList(
                new Tst中文也分表Record(310L, now, now, now, 9L, "批量加载310", ""),
                new Tst中文也分表Record(311L, now, now, now, 9L, "批量加载311", ""),
                new Tst中文也分表Record(312L, now, now, now, 9L, "批量加载312", "merge")
        );
        testcaseNotice("批量Insert，查看日志,ignore, 307-309，分2批插入");
        val rs = dao.batchStore(rds, 2);
        Assert.assertArrayEquals(new int[]{1, 1, 1}, rs);
    }

    @Test
    public void test5𓃬分批批量Update𓃬查日志() {
        val rds = Arrays.asList(
                new Tst中文也分表Record(309L, now, now, now, 9L, "批量加载309", "update"),
                new Tst中文也分表Record(310L, now, now, now, 9L, "批量加载310", "update"),
                new Tst中文也分表Record(311L, now, now, now, 9L, "批量加载311", "update")
        );
        testcaseNotice("批量Update，查看日志 307-309，分2批更新");
        val rs1 = dao.batchUpdate(rds, 2);
        Assert.assertArrayEquals(new int[]{1, 1, 1}, rs1);

        val rs2 = dao.batchUpdate(new Field[]{tbl.Id}, rds, 2, tbl.LoginInfo, tbl.OtherInfo);
        Assert.assertArrayEquals(new int[]{1, 1, 1}, rs2);
    }

    @Test
    public void test6𓃬单独Merge𓃬查日志() {
        testcaseNotice("insert into `tst_中文也分表` (`id`, .., `other_info`) values (?,..., ?) on duplicate key update `login_info` = ?, `other_info` = ?");
        Tst中文也分表 pojo = new Tst中文也分表(312L, now, now, now, 9L, "批量加载312", "update-bymerge");
        val rs = dao.mergeInto(pojo, tbl.LoginInfo, tbl.OtherInfo);
        Assert.assertEquals(2, rs);
    }

    @Test
    public void test7𓃬分批Merge𓃬查日志() {
        val rds = Arrays.asList(
                new Tst中文也分表Record(313L, now, now, now, 9L, "批量合并313-merge", "update-merge"),
                new Tst中文也分表Record(310L, now, now, now, 9L, "批量合并310-merge", "update-merge"),
                new Tst中文也分表Record(311L, now, now, now, 9L, "批量合并311-merge", "update-merge")
        );
        testcaseNotice("313 insert, 310,311 update");
        val rs = dao.batchMerge(new Field[]{tbl.Id}, rds, 2, tbl.LoginInfo, tbl.OtherInfo);
        Assert.assertArrayEquals(new int[]{1, 1, 1}, rs);
    }

    @Test
    public void test8𓃬逻辑删除𓃬查日志() {
        dao.fetchById(1L);
        dao.fetchOneById(1L);
        dao.count();
        dao.count(dao.getTable().onlyLiveData);
    }
}