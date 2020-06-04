package pro.fessional.wings.faceless.database.common;

import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.faceless.WingsTestHelper
import pro.fessional.wings.faceless.database.autogen.tables.daos.Tst中文也分表Dao
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

    @Test
    fun `test0🦁清表重置`() {
        wingsTestHelper.cleanAndInit()
        schemaRevisionManager.publishRevision(20190521_02, 0)
    }

    val now = LocalDateTime.now()

    @Test
    fun `test1🦁批量Load🦁查日志`() {
        if(WingsJooqEnv.daoBatchMysql){
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
        dao.batchInsert(rds, 2)
    }

    @Test
    fun `test3🦁分批批量Merge🦁查日志`() {
        val rds = listOf(
                Tst中文也分表Record(307, now, now, now, 9, "批量加载307", ""),
                Tst中文也分表Record(308, now, now, now, 9, "批量加载308", ""),
                Tst中文也分表Record(309, now, now, now, 9, "批量加载309", "")
        )
        wingsTestHelper.note("批量Insert，查看日志,ignore, 307-309，分2批次， insert ignore")
        dao.batchInsert(rds, 2, true)
        wingsTestHelper.note("批量Insert，查看日志,merge, 307-309，分2批，replace into")
        dao.batchInsert(rds, 2, false)
    }

    @Test
    fun `test4🦁分批批量Store🦁查日志`() {
        val rds = listOf(
                Tst中文也分表Record(310, now, now, now, 9, "批量加载310", ""),
                Tst中文也分表Record(311, now, now, now, 9, "批量加载311", ""),
                Tst中文也分表Record(312, now, now, now, 9, "批量加载312", "merge")
        )
        wingsTestHelper.note("批量Insert，查看日志,ignore, 307-309，分2批插入")
        dao.batchStore(rds, 2)
    }

    @Test
    fun `test5🦁分批批量Update🦁查日志`() {
        val rds = listOf(
                Tst中文也分表Record(309, now, now, now, 9, "批量加载309", "update"),
                Tst中文也分表Record(310, now, now, now, 9, "批量加载310", "update"),
                Tst中文也分表Record(311, now, now, now, 9, "批量加载311", "update")
        )
        wingsTestHelper.note("批量Insert，查看日志,ignore, 307-309，分2批更新")
        dao.batchUpdate(rds, 2)
    }

    @Test
    fun `test6🦁单独Merge🦁查日志`() {
        dao.insertInto(Tst中文也分表Record(312, now, now, now, 9, "批量加载312", "update"), true)
        dao.insertInto(Tst中文也分表Record(312, now, now, now, 9, "批量加载312", "update"), false)
    }
}