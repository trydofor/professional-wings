package pro.fessional.wings.faceless.database.common;

import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.faceless.WingsTestHelper
import pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V2
import pro.fessional.wings.faceless.database.autogen.tables.Tst中文也分表Table
import pro.fessional.wings.faceless.database.autogen.tables.daos.Tst中文也分表Dao
import pro.fessional.wings.faceless.database.autogen.tables.pojos.Tst中文也分表
import pro.fessional.wings.faceless.database.autogen.tables.records.Tst中文也分表Record
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager
import java.time.LocalDateTime


/**
 * @author trydofor
 * @since 2020-05-31
 */
@RunWith(SpringRunner::class)
@SpringBootTest(properties = ["debug = true", "logging.level.org.jooq.tools.LoggerListener=DEBUG"])
@ActiveProfiles("init")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class WingsJooqDaoImplTest {

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Autowired
    lateinit var wingsTestHelper: WingsTestHelper

    @Autowired
    lateinit var dao: Tst中文也分表Dao

    val tbl = Tst中文也分表Table.Tst中文也分表
    val now = LocalDateTime.now()

    @Test
    fun `test0🦁清表重置`() {
        wingsTestHelper.cleanAndInit()
        schemaRevisionManager.publishRevision(REVISION_TEST_V2, 0)
    }

    @Test
    fun `test1🦁批量Load🦁查日志`() {
        if (WingsJooqEnv.daoBatchMysql) {
            wingsTestHelper.note("跳过低效的SQL，使用mysql replace into 语法，见 batchMerge")
            return
        }

        val rds = listOf(
                Tst中文也分表Record(301, now, now, now, 9, "批量加载301", ""),
                Tst中文也分表Record(302, now, now, now, 9, "批量加载302", ""),
                Tst中文也分表Record(303, now, now, now, 9, "批量加载303", "")
        )
        wingsTestHelper.note("批量Load，查看日志，ignore, 301-303，使用了from dual where exists先查再插")
        dao.batchLoad(rds, true)
        wingsTestHelper.note("批量Load，查看日志，replace, 301-303，使用了on duplicate key update")
        dao.batchLoad(rds, false)
    }

    @Test
    fun `test2🦁分批批量Insert🦁查日志`() {
        val rds = listOf(
                Tst中文也分表Record(304, now, now, now, 9, "批量加载304", ""),
                Tst中文也分表Record(305, now, now, now, 9, "批量加载305", ""),
                Tst中文也分表Record(306, now, now, now, 9, "批量加载306", "")
        )
        wingsTestHelper.note("批量Insert，查看日志, 304-306，分2批插入")
        val rs = dao.batchInsert(rds, 2)
        Assert.assertArrayEquals(intArrayOf(1, 1, 1), rs)
    }

    @Test
    fun `test3🦁分批批量Merge🦁查日志`() {
        val rds = listOf(
                Tst中文也分表Record(307, now, now, now, 9, "批量加载307", ""),
                Tst中文也分表Record(308, now, now, now, 9, "批量加载308", ""),
                Tst中文也分表Record(309, now, now, now, 9, "批量加载309", "")
        )
        wingsTestHelper.note("批量Insert，查看日志,ignore, 307-309，分2批次， insert ignore")
        val rs1 = dao.batchInsert(rds, 2, true)
        Assert.assertArrayEquals(intArrayOf(1, 1, 1), rs1)

        wingsTestHelper.note("批量Insert，查看日志,replace, 307-309，分2批，replace into")
        val rs2 = dao.batchInsert(rds, 2, false)
        Assert.assertArrayEquals(intArrayOf(1, 1, 1), rs2)

        wingsTestHelper.note("批量Merge，查看日志,on dupkey, 307-309，分2批，duplicate")
        wingsTestHelper.note("insert into `tst_中文也分表` (`id`, .., `other_info`) values (?,..., ?) on duplicate key update `login_info` = ?, `other_info` = ?")
        val rs3 = dao.batchMerge(rds, 2, tbl.LoginInfo, tbl.OtherInfo)
        Assert.assertArrayEquals(intArrayOf(1, 1, 1), rs3)
    }

    @Test
    fun `test4🦁分批批量Store🦁查日志`() {
        val rds = listOf(
                Tst中文也分表Record(310, now, now, now, 9, "批量加载310", ""),
                Tst中文也分表Record(311, now, now, now, 9, "批量加载311", ""),
                Tst中文也分表Record(312, now, now, now, 9, "批量加载312", "merge")
        )
        wingsTestHelper.note("批量Insert，查看日志,ignore, 307-309，分2批插入")
        val rs = dao.batchStore(rds, 2)
        Assert.assertArrayEquals(intArrayOf(1, 1, 1), rs)
    }

    @Test
    fun `test5🦁分批批量Update🦁查日志`() {
        val rds = listOf(
                Tst中文也分表Record(309, now, now, now, 9, "批量加载309", "update"),
                Tst中文也分表Record(310, now, now, now, 9, "批量加载310", "update"),
                Tst中文也分表Record(311, now, now, now, 9, "批量加载311", "update")
        )
        wingsTestHelper.note("批量Update，查看日志 307-309，分2批更新")
        val rs1 = dao.batchUpdate(rds, 2)
        Assert.assertArrayEquals(intArrayOf(1, 1, 1), rs1)

        val rs2 = dao.batchUpdate(arrayOf(tbl.Id), rds, 2, tbl.LoginInfo, tbl.OtherInfo);
        Assert.assertArrayEquals(intArrayOf(1, 1, 1), rs2)
    }

    @Test
    fun `test6🦁单独Merge🦁查日志`() {
        wingsTestHelper.note("insert into `tst_中文也分表` (`id`, .., `other_info`) values (?,..., ?) on duplicate key update `login_info` = ?, `other_info` = ?")
        val rs = dao.mergeInto(Tst中文也分表(312, now, now, now, 9, "批量加载312", "update-bymerge"), tbl.LoginInfo, tbl.OtherInfo)
        Assert.assertEquals(2, rs)
    }

    @Test
    fun `test7🦁分批Merge🦁查日志`() {
        val rds = listOf(
                Tst中文也分表Record(313, now, now, now, 9, "批量合并313-merge", "update-merge"),
                Tst中文也分表Record(310, now, now, now, 9, "批量合并310-merge", "update-merge"),
                Tst中文也分表Record(311, now, now, now, 9, "批量合并311-merge", "update-merge")
        )
        wingsTestHelper.note("313 insert, 310,311 update")
        val rs = dao.batchMerge(arrayOf(tbl.Id), rds, 2, tbl.LoginInfo, tbl.OtherInfo)
        Assert.assertArrayEquals(intArrayOf(1, 1, 1), rs)
    }
}