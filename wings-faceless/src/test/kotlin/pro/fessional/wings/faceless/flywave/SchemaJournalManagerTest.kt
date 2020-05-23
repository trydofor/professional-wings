package pro.fessional.wings.faceless.flywave

import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.faceless.WingsTestHelper

/**
 * @author trydofor
 * @since 2019-06-20
 */

@RunWith(SpringRunner::class)
@SpringBootTest
@ActiveProfiles("shard")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SchemaJournalManagerTest {

    @Autowired
    lateinit var schemaJournalManager: SchemaJournalManager

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Autowired
    lateinit var wingsTestHelper: WingsTestHelper

    @Test
    fun `test0🦁清表重置`() {
        wingsTestHelper.cleanAndInit()
    }

    @Test
    fun `test1🦁分表发布`() {
        schemaRevisionManager.publishRevision(20190520_01, 0)
        wingsTestHelper.assertSame(WingsTestHelper.Type.Table,"sys_commit_journal",
                "sys_light_sequence",
                "sys_schema_journal",
                "sys_schema_version"
        )
        schemaRevisionManager.publishRevision(20190521_01, 0)
        wingsTestHelper.assertSame(WingsTestHelper.Type.Table,"sys_commit_journal",
                "sys_light_sequence",
                "sys_schema_journal",
                "sys_schema_version",
                "tst_中文也分表")
        wingsTestHelper.note("可检查日志或debug观察，wing0和wing1编号")
    }

    @Test
    fun `test2🦁检查分表状态`() {
        schemaJournalManager.checkAndInitDdl("tst_中文也分表", 0)
        wingsTestHelper.note("没有变化，无错即可")
    }

    @Test
    fun `test3🦁增减BU触发器`() {
        schemaJournalManager.publishUpdate("tst_中文也分表", true, 0)
        wingsTestHelper.assertHas(WingsTestHelper.Type.Table, "tst_中文也分表\$upd")
        wingsTestHelper.assertHas(WingsTestHelper.Type.Trigger,"tst_中文也分表\$bu")

        schemaJournalManager.publishUpdate("tst_中文也分表", false, 0)
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, "tst_中文也分表\$upd")
        wingsTestHelper.assertNot(WingsTestHelper.Type.Trigger,"tst_中文也分表\$bu")
        wingsTestHelper.note("检查日志和数据库变化，最好debug进行，wing0和wing1，同步更新表结构")
    }

    @Test
    fun `test4🦁增减BD触发器`() {
        schemaJournalManager.publishDelete("tst_中文也分表", true, 0)
        wingsTestHelper.assertHas(WingsTestHelper.Type.Table, "tst_中文也分表\$del")
        wingsTestHelper.assertHas(WingsTestHelper.Type.Trigger,"tst_中文也分表\$bd")
        schemaJournalManager.publishDelete("tst_中文也分表", false, 0)
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, "tst_中文也分表\$del")
        wingsTestHelper.assertNot(WingsTestHelper.Type.Trigger,"tst_中文也分表\$bd")

        wingsTestHelper.note("检查日志和数据库变化，最好debug进行，wing0和wing1，同步更新表结构")
    }
}