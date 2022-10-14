package pro.fessional.wings.faceless.flywave

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
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
import pro.fessional.wings.faceless.flywave.SchemaJournalManagerTest.Companion.HEAD
import pro.fessional.wings.faceless.flywave.SchemaJournalManagerTest.Companion.TAIL
import pro.fessional.wings.faceless.flywave.SchemaJournalManagerTest.Companion.TFMT
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner

/**
 * 包括了分表，跟踪表的综合测试
 * @author trydofor
 * @since 2019-06-20
 */

@SpringBootTest(
    properties = ["debug = true",
        "wings.faceless.flywave.sql.format-trace=$TFMT",
        "wings.faceless.flywave.ver.schema-version-table=win_schema_version",
        "wings.faceless.flywave.ver.schema-journal-table=win_schema_journal",
        "wings.faceless.flywave.ver.journal-insert=" +
                "CREATE TABLE `$HEAD{{TABLE_NAME}}$TAIL` ( " +
                "    `_id` BIGINT(20) NOT NULL AUTO_INCREMENT, " +
                "    `_dt` DATETIME(3) NOT NULL DEFAULT '1000-01-01 00:00:00', " +
                "    `_tp` CHAR(1) NOT NULL DEFAULT 'Z', " +
                "    {{TABLE_BONE}}, " +
                "    PRIMARY KEY (`_id`), " +
                "    KEY `RAW_TABLE_PK` ({{TABLE_PKEY}}) " +
                ") ENGINE=INNODB DEFAULT CHARSET=UTF8MB4",
        "wings.faceless.flywave.ver.trigger-insert=" +
                "CREATE TRIGGER `ai__{{TABLE_NAME}}` AFTER INSERT ON `{{TABLE_NAME}}` " +
                "FOR EACH ROW BEGIN " +
                "  IF (@DISABLE_FLYWAVE IS NULL) THEN  " +
                "    INSERT INTO `$HEAD{{TABLE_NAME}}$TAIL` SELECT NULL, NOW(3), 'C', t.* FROM `{{TABLE_NAME}}` t " +
                "    WHERE t.id = NEW.id ; " +
                "  END IF;  " +
                "END",
        "wings.faceless.flywave.ver.journal-update=" +
                "CREATE TABLE `$HEAD{{TABLE_NAME}}$TAIL` ( " +
                "    `_id` BIGINT(20) NOT NULL AUTO_INCREMENT, " +
                "    `_dt` DATETIME(3) NOT NULL DEFAULT '1000-01-01 00:00:00', " +
                "    `_tp` CHAR(1) NOT NULL DEFAULT 'Z', " +
                "    {{TABLE_BONE}}, " +
                "    PRIMARY KEY (`_id`), " +
                "    KEY `RAW_TABLE_PK` ({{TABLE_PKEY}}) " +
                ") ENGINE=INNODB DEFAULT CHARSET=UTF8MB4",
        "wings.faceless.flywave.ver.trigger-update=" +
                "CREATE TRIGGER `au__{{TABLE_NAME}}` AFTER UPDATE ON `{{TABLE_NAME}}` " +
                "FOR EACH ROW BEGIN " +
                "  IF (@DISABLE_FLYWAVE IS NULL) THEN  " +
                "    INSERT INTO `$HEAD{{TABLE_NAME}}$TAIL` SELECT NULL, NOW(3), 'U', t.* FROM `{{TABLE_NAME}}` t " +
                "    WHERE t.id = NEW.id ; " +
                "  END IF;  " +
                "END",
        "wings.faceless.flywave.ver.journal-delete=" +
                "CREATE TABLE `$HEAD{{TABLE_NAME}}$TAIL` ( " +
                "    `_id` BIGINT(20) NOT NULL AUTO_INCREMENT, " +
                "    `_dt` DATETIME(3) NOT NULL DEFAULT '1000-01-01 00:00:00', " +
                "    `_tp` CHAR(1) NOT NULL DEFAULT 'Z', " +
                "    {{TABLE_BONE}}, " +
                "    PRIMARY KEY (`_id`), " +
                "    KEY `RAW_TABLE_PK` ({{TABLE_PKEY}}) " +
                ") ENGINE=INNODB DEFAULT CHARSET=UTF8MB4",
        "wings.faceless.flywave.ver.trigger-delete=" +
                "CREATE TRIGGER `bd__{{TABLE_NAME}}` BEFORE DELETE ON `{{TABLE_NAME}}` " +
                "FOR EACH ROW BEGIN " +
                "  IF (@DISABLE_FLYWAVE IS NULL) THEN  " +
                "    INSERT INTO `$HEAD{{TABLE_NAME}}$TAIL` SELECT NULL, NOW(3), 'D', t.* FROM `{{TABLE_NAME}}` t " +
                "    WHERE t.id = OLD.id ; " +
                "  END IF;  " +
                "END"
    ]
)
@TestMethodOrder(MethodName::class)
class SchemaJournalManagerTest {

    companion object {
        const val HEAD = ""
        const val TAIL = "__"
        const val TFMT = SqlSegmentProcessor.TRACE_SU2_LINE

        // OK
//        const val HEAD = ""
//        const val TAIL = "\$log"
//        const val TFMT = ""

        // OK
//        const val HEAD = ""
//        const val TAIL = "__log"
//        const val TFMT = SqlSegmentProcessor.TRACE_SU2_LINE


        // OK
//        const val HEAD = "_"
//        const val TAIL = ""
//        const val TFMT = SqlSegmentProcessor.TRACE_PRE_LINE
    }

    @Autowired
    lateinit var schemaDefinitionLoader: SchemaDefinitionLoader

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

    private fun traceTable(tbl: String): String {
        return "$HEAD$tbl$TAIL"
    }

    @Test
    fun `test0🦁清表`() {
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
            "tst_中文也分表",
            "tst_中文也分表_postfix",
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
        wingsTestHelper.assertHas(WingsTestHelper.Type.Table, traceTable("tst_中文也分表"))
        wingsTestHelper.assertHas(WingsTestHelper.Type.Trigger, "ai__tst_中文也分表")

        jdbcTemplate.execute(
            """
            INSERT INTO `tst_中文也分表_1`
            (`id`, `create_dt`, `modify_dt`, `delete_dt`, `commit_id`, `login_info`, `other_info`)
            VALUES (1,NOW(3),NOW(3),'1000-01-01',0,'赵四','老张');
        """
        )
        val del = jdbcTemplate.update("DELETE FROM `${traceTable("tst_中文也分表_1")}` WHERE id = 1")
        assertEquals(1, del, "如果失败，单独运行整个类，消除分表干扰")
        breakpointDebug("清楚数据🐵，因为trace表不会删除有数据表")

        schemaJournalManager.publishInsert("tst_中文也分表", false, 0)
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, traceTable("tst_中文也分表"))
        wingsTestHelper.assertNot(WingsTestHelper.Type.Trigger, "ai__tst_中文也分表")
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
        wingsTestHelper.assertHas(WingsTestHelper.Type.Table, traceTable("tst_中文也分表"))
        wingsTestHelper.assertHas(WingsTestHelper.Type.Trigger, "au__tst_中文也分表")

        jdbcTemplate.execute("UPDATE `tst_中文也分表_1` SET login_info='赵思', commit_id=1 WHERE id = 1")
        breakpointDebug("更新数据🐵，查询数据库各表及数据")

        val del = jdbcTemplate.update("DELETE FROM `${traceTable("tst_中文也分表_1")}` WHERE id = 1")

        assertEquals(1, del, "如果失败，单独运行整个类，消除分表干扰")
        breakpointDebug("清楚数据🐵，因为trace表不会删除有数据表")

        schemaJournalManager.publishUpdate("tst_中文也分表", false, 0)
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, traceTable("tst_中文也分表"))
        wingsTestHelper.assertNot(WingsTestHelper.Type.Trigger, "au__tst_中文也分表")
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
        wingsTestHelper.assertHas(WingsTestHelper.Type.Table, traceTable("tst_中文也分表"))
        wingsTestHelper.assertHas(WingsTestHelper.Type.Trigger, "bd__tst_中文也分表")

        jdbcTemplate.execute("DELETE FROM `tst_中文也分表_1` WHERE id = 1")
        breakpointDebug("删除数据🐵，查询数据库各表及数据")

        val del = jdbcTemplate.update("DELETE FROM `${traceTable("tst_中文也分表_1")}` WHERE id = 1")

        assertEquals(1, del)
        breakpointDebug("清楚数据🐵，因为trace表不会删除有数据表")

        schemaJournalManager.publishDelete("tst_中文也分表", false, 0)
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, traceTable("tst_中文也分表"))
        wingsTestHelper.assertNot(WingsTestHelper.Type.Trigger, "bd__tst_中文也分表")
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
        wingsTestHelper.assertHas(WingsTestHelper.Type.Table, traceTable("tst_中文也分表"))
        wingsTestHelper.assertHas(WingsTestHelper.Type.Trigger, "ai__tst_中文也分表")
        wingsTestHelper.assertHas(WingsTestHelper.Type.Trigger, "au__tst_中文也分表")
        wingsTestHelper.assertHas(WingsTestHelper.Type.Trigger, "bd__tst_中文也分表")

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

        val tps = jdbcTemplate.queryForList("SELECT _tp FROM `${traceTable("tst_中文也分表_2")}` WHERE id = 1 ORDER BY _id", String::class.java)

        assertEquals(listOf("C", "U", "D"), tps)
        breakpointDebug("清楚数据🐵，因为trace表不会删除有数据表")

        schemaJournalManager.publishInsert("tst_中文也分表", false, 0)
        schemaJournalManager.publishUpdate("tst_中文也分表", false, 0)
        schemaJournalManager.publishDelete("tst_中文也分表", false, 0)
        wingsTestHelper.assertNot(WingsTestHelper.Type.Trigger, "ai__tst_中文也分表")
        wingsTestHelper.assertNot(WingsTestHelper.Type.Trigger, "au__tst_中文也分表")
        wingsTestHelper.assertNot(WingsTestHelper.Type.Trigger, "bd__tst_中文也分表")

        testcaseNotice("检查日志和数据库变化，最好debug进行，wing0和wing1，同步更新表结构")
    }

    @Test
    fun `test7🦁表结构变更`() {
        if (wingsTestHelper.isH2) {
            testcaseNotice("h2 database skip")
            return
        }

        breakpointDebug("分表触发器💰，观察数据库所有表")
        schemaJournalManager.publishInsert("tst_中文也分表", true, 0)
        schemaJournalManager.publishUpdate("tst_中文也分表", true, 0)
        schemaJournalManager.publishDelete("tst_中文也分表", true, 0)
        wingsTestHelper.assertHas(
            WingsTestHelper.Type.Table,
            "tst_中文也分表",
            "tst_中文也分表_0",
            "tst_中文也分表_1",
            "tst_中文也分表_2",
            "tst_中文也分表_3",
            "tst_中文也分表_4",
            traceTable("tst_中文也分表"),
            traceTable("tst_中文也分表_0"),
            traceTable("tst_中文也分表_1"),
            traceTable("tst_中文也分表_2"),
            traceTable("tst_中文也分表_3"),
            traceTable("tst_中文也分表_4")
        )

        schemaRevisionManager.forceExecuteSql(
            """
            ALTER TABLE `tst_中文也分表` 
            DROP COLUMN `other_info`,
            DROP COLUMN `login_info`;
        """.trimIndent()
        )

        assertHasColumn("tst_中文也分表", "id", "create_dt", "modify_dt", "delete_dt", "commit_id", "language")
        assertHasColumn(traceTable("tst_中文也分表"), "_id", "_dt", "_tp", "id", "create_dt", "modify_dt", "delete_dt", "commit_id", "language")

        assertNotColumn("tst_中文也分表", "other_info", "login_info")
        assertNotColumn(traceTable("tst_中文也分表"), "other_info", "login_info")


        assertSameColumn("tst_中文也分表", "tst_中文也分表_0")
        assertSameColumn("tst_中文也分表", "tst_中文也分表_1")
        assertSameColumn("tst_中文也分表", "tst_中文也分表_2")
        assertSameColumn("tst_中文也分表", "tst_中文也分表_3")
        assertSameColumn("tst_中文也分表", "tst_中文也分表_4")
        assertSameColumn(traceTable("tst_中文也分表"), traceTable("tst_中文也分表_0"))
        assertSameColumn(traceTable("tst_中文也分表"), traceTable("tst_中文也分表_1"))
        assertSameColumn(traceTable("tst_中文也分表"), traceTable("tst_中文也分表_2"))
        assertSameColumn(traceTable("tst_中文也分表"), traceTable("tst_中文也分表_3"))
        assertSameColumn(traceTable("tst_中文也分表"), traceTable("tst_中文也分表_4"))

        testcaseNotice("检查日志和数据库变化，最好debug进行，wing0和wing1，同步更新表结构")
    }


    private fun assertSameColumn(tbl1: String, tbl2: String) {
        val diff = schemaDefinitionLoader.diffFullSame(jdbcTemplate.dataSource!!, tbl1, tbl2, SchemaDefinitionLoader.TYPE_TBL)
        assertEquals("", diff, diff)
    }

    private fun assertNotColumn(tbl: String, vararg col: String) {
        val cols = schemaDefinitionLoader.showBoneCol(jdbcTemplate.dataSource!!, tbl).joinToString(",")
        for (s in col) {
            assertFalse(cols.contains("`$s`", true), cols)
        }
    }

    private fun assertHasColumn(tbl: String, vararg col: String) {
        val cols = schemaDefinitionLoader.showBoneCol(jdbcTemplate.dataSource!!, tbl).joinToString(",")
        for (s in col) {
            assertTrue(cols.contains("`$s`", true), cols)
        }
    }
}
