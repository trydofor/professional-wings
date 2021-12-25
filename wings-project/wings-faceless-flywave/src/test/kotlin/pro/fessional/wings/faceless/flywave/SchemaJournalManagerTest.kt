package pro.fessional.wings.faceless.flywave

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.MethodOrderer.MethodName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import pro.fessional.wings.faceless.WingsTestHelper
import pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V1
import pro.fessional.wings.faceless.WingsTestHelper.breakpointDebug
import pro.fessional.wings.faceless.WingsTestHelper.testcaseNotice
import pro.fessional.wings.faceless.util.FlywaveInteractiveTty
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner

/**
 * 包括了分表，跟踪表的综合测试
 * @author trydofor
 * @since 2019-06-20
 */

@SpringBootTest(
    properties = ["debug = true",
        "wings.faceless.flywave.ver.schema-version-table=win_schema_version",
        "wings.faceless.flywave.ver.schema-journal-table=win_schema_journal"
    ]
)
@TestMethodOrder(MethodName::class)
class SchemaJournalManagerTest {

    @Autowired
    lateinit var schemaJournalManager: SchemaJournalManager

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Autowired
    lateinit var shcemaShardingManager: SchemaShardingManager

    @Autowired
    lateinit var wingsTestHelper: WingsTestHelper

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    private val schemaPrefix = "win_schema_"

    @Test
    fun `test0🦁清表`() {
        schemaJournalManager.askWay(FlywaveInteractiveTty.askYes)
        schemaRevisionManager.askWay(FlywaveInteractiveTty.askYes)
        shcemaShardingManager.askWay(FlywaveInteractiveTty.askYes)

        wingsTestHelper.cleanTable()
        val sqls = FlywaveRevisionScanner
            .helper()
            .master()
            .modify("更名win_schema_version") { _, sql ->
                if (sql.revision == WingsRevision.V00_19_0512_01_Schema.revision()) {
                    sql.undoText = sql.undoText.replace("sys_schema_", schemaPrefix)
                    sql.uptoText = sql.uptoText.replace("sys_schema_", schemaPrefix)
                }
            }
            .scan()
        schemaRevisionManager.checkAndInitSql(sqls, 0, true)
        breakpointDebug("清楚所有表，发布 REVISION_1ST_SCHEMA 版，新建 flywave 版本表")
    }

    @Test
    fun `test1🦁建表`() {
        schemaRevisionManager.publishRevision(WingsRevision.V01_19_0520_01_IdLog.revision(), 0)
        wingsTestHelper.assertSame(
            WingsTestHelper.Type.Table,
            "sys_commit_journal",
            "sys_light_sequence",
            "${schemaPrefix}journal",
            "${schemaPrefix}version"
        )
        breakpointDebug("生成测试表💰，观察数据库所有表")
        schemaRevisionManager.publishRevision(REVISION_TEST_V1, 0)
        wingsTestHelper.assertSame(
            WingsTestHelper.Type.Table,
            "sys_commit_journal",
            "sys_light_sequence",
            "${schemaPrefix}journal",
            "${schemaPrefix}version",
            "tst_中文也分表"
        )
        testcaseNotice("可检查日志或debug观察，wing0和wing1表名")
    }

    @Test
    fun `test2🦁分表`() {
        schemaJournalManager.checkAndInitDdl("tst_中文也分表", 0)
        wingsTestHelper.assertNot(
            WingsTestHelper.Type.Table,
            "tst_中文也分表_0",
            "tst_中文也分表_1",
            "tst_中文也分表_2",
            "tst_中文也分表_3",
            "tst_中文也分表_4"
        )
        breakpointDebug("分表测试表💰，观察数据库所有表")
        shcemaShardingManager.publishShard("tst_中文也分表", 5)
        wingsTestHelper.assertHas(
            WingsTestHelper.Type.Table, "tst_中文也分表_0",
            "tst_中文也分表_1",
            "tst_中文也分表_2",
            "tst_中文也分表_3",
            "tst_中文也分表_4"
        )
        testcaseNotice("可检查日志或debug观察，wing_test，多出分表0-5")
    }

    @Test
    fun `test4🦁AI触发器`() {
        if (wingsTestHelper.isH2) {
            testcaseNotice("h2 database skip")
            return
        }

        breakpointDebug("分表触发器💰，观察数据库所有表")
        schemaJournalManager.publishInsert("tst_中文也分表", true, 0)
        wingsTestHelper.assertHas(WingsTestHelper.Type.Table, "tst_中文也分表\$log")
        wingsTestHelper.assertHas(WingsTestHelper.Type.Trigger, "tst_中文也分表\$ai")

        jdbcTemplate.execute(
            """
            INSERT INTO `tst_中文也分表_1`
            (`id`, `create_dt`, `modify_dt`, `delete_dt`, `commit_id`, `login_info`, `other_info`)
            VALUES (1,NOW(3),NOW(3),'1000-01-01',0,'赵四','老张');
        """
        )
        val del = jdbcTemplate.update("DELETE FROM `tst_中文也分表_1\$log` WHERE id = 1")
        assertEquals(1, del, "如果失败，单独运行整个类，消除分表干扰")
        breakpointDebug("清楚数据🐵，因为trace表不会删除有数据表")

        schemaJournalManager.publishInsert("tst_中文也分表", false, 0)
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, "tst_中文也分表\$log")
        wingsTestHelper.assertNot(WingsTestHelper.Type.Trigger, "tst_中文也分表\$ai")
        testcaseNotice("检查日志和数据库变化，最好debug进行，wing0和wing1，同步更新表结构")
    }

    @Test
    fun `test4🦁AU触发器`() {
        if (wingsTestHelper.isH2) {
            testcaseNotice("h2 database skip")
            return
        }

        breakpointDebug("分表触发器💰，观察数据库所有表")
        schemaJournalManager.publishUpdate("tst_中文也分表", true, 0)
        wingsTestHelper.assertHas(WingsTestHelper.Type.Table, "tst_中文也分表\$log")
        wingsTestHelper.assertHas(WingsTestHelper.Type.Trigger, "tst_中文也分表\$au")

        jdbcTemplate.execute("UPDATE `tst_中文也分表_1` SET login_info='赵思', commit_id=1 WHERE id = 1")
        breakpointDebug("更新数据🐵，查询数据库各表及数据")

        val del = jdbcTemplate.update("DELETE FROM `tst_中文也分表_1\$log` WHERE id = 1")

        assertEquals(1, del, "如果失败，单独运行整个类，消除分表干扰")
        breakpointDebug("清楚数据🐵，因为trace表不会删除有数据表")

        schemaJournalManager.publishUpdate("tst_中文也分表", false, 0)
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, "tst_中文也分表\$log")
        wingsTestHelper.assertNot(WingsTestHelper.Type.Trigger, "tst_中文也分表\$au")
        testcaseNotice("检查日志和数据库变化，最好debug进行，wing0和wing1，同步更新表结构")
    }

    @Test
    fun `test5🦁BD触发器`() {
        if (wingsTestHelper.isH2) {
            testcaseNotice("h2 database skip")
            return
        }
        breakpointDebug("分表触发器💰，观察数据库所有表")
        schemaJournalManager.publishDelete("tst_中文也分表", true, 0)
        wingsTestHelper.assertHas(WingsTestHelper.Type.Table, "tst_中文也分表\$log")
        wingsTestHelper.assertHas(WingsTestHelper.Type.Trigger, "tst_中文也分表\$bd")

        jdbcTemplate.execute("DELETE FROM `tst_中文也分表_1` WHERE id = 1")
        breakpointDebug("删除数据🐵，查询数据库各表及数据")

        val del = jdbcTemplate.update("DELETE FROM `tst_中文也分表_1\$log` WHERE id = 1")

        assertEquals(1, del)
        breakpointDebug("清楚数据🐵，因为trace表不会删除有数据表")

        schemaJournalManager.publishDelete("tst_中文也分表", false, 0)
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, "tst_中文也分表\$log")
        wingsTestHelper.assertNot(WingsTestHelper.Type.Trigger, "tst_中文也分表\$bd")
        testcaseNotice("检查日志和数据库变化，最好debug进行，wing0和wing1，同步更新表结构")
    }

    @Test
    fun `test6🦁全触发器`() {
        if (wingsTestHelper.isH2) {
            testcaseNotice("h2 database skip")
            return
        }

        breakpointDebug("分表触发器💰，观察数据库所有表")
        schemaJournalManager.publishInsert("tst_中文也分表", true, 0)
        schemaJournalManager.publishUpdate("tst_中文也分表", true, 0)
        schemaJournalManager.publishDelete("tst_中文也分表", true, 0)
        wingsTestHelper.assertHas(WingsTestHelper.Type.Table, "tst_中文也分表\$log")
        wingsTestHelper.assertHas(WingsTestHelper.Type.Trigger, "tst_中文也分表\$ai")
        wingsTestHelper.assertHas(WingsTestHelper.Type.Trigger, "tst_中文也分表\$au")
        wingsTestHelper.assertHas(WingsTestHelper.Type.Trigger, "tst_中文也分表\$bd")

        jdbcTemplate.execute(
            """
            INSERT INTO `tst_中文也分表_2`
            (`id`, `create_dt`, `modify_dt`, `delete_dt`, `commit_id`, `login_info`, `other_info`)
            VALUES (1,NOW(3),NOW(3),'1000-01-01',0,'赵四','老张');
        """
        )
        jdbcTemplate.execute("UPDATE `tst_中文也分表_2` SET login_info='赵思', commit_id=1 WHERE id = 1")
        jdbcTemplate.execute("DELETE FROM `tst_中文也分表_2` WHERE id = 1")
        breakpointDebug("删除数据🐵，查询数据库各表及数据")

        val tps = jdbcTemplate.queryForList("SELECT _tp FROM `tst_中文也分表_2\$log` WHERE id = 1 ORDER BY _id", String::class.java)

        assertEquals(listOf("C", "U", "D"), tps)
        breakpointDebug("清楚数据🐵，因为trace表不会删除有数据表")

        schemaJournalManager.publishInsert("tst_中文也分表", false, 0)
        schemaJournalManager.publishUpdate("tst_中文也分表", false, 0)
        schemaJournalManager.publishDelete("tst_中文也分表", false, 0)
        wingsTestHelper.assertNot(WingsTestHelper.Type.Trigger, "tst_中文也分表\$ai")
        wingsTestHelper.assertNot(WingsTestHelper.Type.Trigger, "tst_中文也分表\$au")
        wingsTestHelper.assertNot(WingsTestHelper.Type.Trigger, "tst_中文也分表\$bd")

        testcaseNotice("检查日志和数据库变化，最好debug进行，wing0和wing1，同步更新表结构")
    }
}
