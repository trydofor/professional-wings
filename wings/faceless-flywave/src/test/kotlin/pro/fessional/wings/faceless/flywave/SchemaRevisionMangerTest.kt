package pro.fessional.wings.faceless.flywave

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.MethodOrderer.MethodName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pro.fessional.wings.faceless.WingsTestHelper
import pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V1
import pro.fessional.wings.faceless.WingsTestHelper.breakpointDebug
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner

/**
 * Default profile, there are writer and reader datasource, use the writer only.
 * @author trydofor
 * @since 2019-06-05
 */
@SpringBootTest(
    properties = [
        "debug = true",
        "wings.faceless.flywave.ver.schema-version-table=win_schema_version",
    ]
)
@TestMethodOrder(MethodName::class)
open class SchemaRevisionMangerTest {

    private val revi1Schema: Long = WingsRevision.V00_19_0512_01_Schema.revision()
    private val revi2IdLog: Long = WingsRevision.V01_19_0520_01_IdLog.revision()

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Autowired
    lateinit var wingsTestHelper: WingsTestHelper

    private val schemaVersion = "win_schema_version"

    @Test
    fun test0CleanTables() {
        wingsTestHelper.cleanTable()
        val sqls = FlywaveRevisionScanner.helper()
            .master()
            .replace(revi1Schema, revi1Schema + 1, true)
            .modify(revi1Schema + 1, "sys_schema_version", schemaVersion)
//                .modify("rename win_schema_version") { _, sql ->
//                    if (sql.revision == REVISION_1ST_SCHEMA) {
//                        sql.undoText = sql.undoText.replace("sys_schema_version", schemaVersion)
//                        sql.uptoText = sql.uptoText.replace("sys_schema_version", schemaVersion)
//                    }
//                }
            .scan()
        schemaRevisionManager.checkAndInitSql(sqls, 0, true)
    }

    @Test
    fun test1Publish520() {
        breakpointDebug("Publish to REVISION_2ND_IDLOGS💰")
        schemaRevisionManager.publishRevision(revi2IdLog, 0)
    }

    @Test
    fun test2CurrentRevi() {
        breakpointDebug("Check current revision💰")
        val databaseVersion = schemaRevisionManager.currentRevision()
        for ((_, u) in databaseVersion) {
            assertEquals(revi2IdLog, u)
        }
    }

    @Test
    fun test2ReviLine() {
        breakpointDebug("Check current revision line💰")
        val databaseVersion = schemaRevisionManager.statusRevisions()
        for ((d, u) in databaseVersion) {
            if (u == null) {
                println("$d - -1")
            } else {
                println("$d -")
                for (entry in u.entries) {
                    println(" ${entry.key} : ${entry.value}")
                }
            }
        }
    }

    @Test
    fun test3DownThenUp() {
        breakpointDebug("Downgrade to 1st💰")
        schemaRevisionManager.publishRevision(revi1Schema, -1)
        breakpointDebug("Upgrade to 2st💰")
        schemaRevisionManager.publishRevision(revi2IdLog, -1)
        breakpointDebug("Again downgrade to 1st💰")
        schemaRevisionManager.publishRevision(revi1Schema, -1)
    }

    private val test3rdRevision = 20190615_01L

    @Test
    fun test4Force615() {
        breakpointDebug("Force to add 615💰, but do NOT publish")
        schemaRevisionManager.forceUpdateSql(
            test3rdRevision, """
            CREATE TABLE `test_temp`(
              `SEQ_NAME` varchar(100) NOT NULL COMMENT 'sequence name'
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4 COMMENT ='test_temp';

            CREATE TABLE `test_temp_0`(
              `SEQ_NAME` varchar(100) NOT NULL COMMENT 'sequence name'
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4 COMMENT ='test_temp';

            CREATE TABLE `test_temp_1`(
              `SEQ_NAME` varchar(100) NOT NULL COMMENT 'sequence name'
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4 COMMENT ='test_temp';
            """.trimIndent(),

            "DROP TABLE IF EXISTS `test_temp`",
            -2
        )
    }

    @Test
    fun test5ForceBreak() {
        breakpointDebug("Publish 615💰")
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, "test_temp", "test_temp_0", "test_temp_1")
        schemaRevisionManager.forceApplyBreak(test3rdRevision, -3, true)
        wingsTestHelper.assertHas(WingsTestHelper.Type.Table, "test_temp", "test_temp_0", "test_temp_1")
        breakpointDebug("Cancel 615💰")
        schemaRevisionManager.forceApplyBreak(test3rdRevision, -4, false)
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, "test_temp", "test_temp_0", "test_temp_1")
    }


    @Test
    fun test6Republish520() {
        breakpointDebug("Publish 520💰")
        schemaRevisionManager.publishRevision(revi2IdLog, 0)
        breakpointDebug("Downgrade 520💰")
        schemaRevisionManager.forceApplyBreak(revi2IdLog, 0, false)
        breakpointDebug("Re-publish 520💰")
        schemaRevisionManager.publishRevision(revi2IdLog, 0)
    }

    @Test
    fun test7ForceExecSql() {
        breakpointDebug("Force to execute the Sql💰")
        schemaRevisionManager.forceExecuteSql(
            """
            CREATE TABLE `test_temp_x`(
              `SEQ_NAME` varchar(100) NOT NULL COMMENT 'sequence name'
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4 COMMENT ='test_temp';

            DROP TABLE IF EXISTS `test_temp_x`;
            """.trimIndent()
        )
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, "test_temp_x")
    }

    @Test
    fun test8PublishBranch() {
        breakpointDebug("scan branch feature/01-enum-i18n💰")
        val sqls = FlywaveRevisionScanner.scanBranch("feature/01-enum-i18n")
        schemaRevisionManager.checkAndInitSql(sqls, 0, true)
        breakpointDebug("publish branch feature/01-enum-i18n💰")
        schemaRevisionManager.publishRevision(WingsRevision.V01_19_0521_01_EnumI18n.revision(), 0)
    }

    @Test
    fun test9MaintainBreak() {
        breakpointDebug("Prepare a breakpoint revision to mock a failure💰")
        schemaRevisionManager.forceExecuteSql(
            """
            UPDATE `$schemaVersion` SET `apply_dt` = '1000-01-01 00:00:17' WHERE `revision` = '$REVISION_TEST_V1';
            """.trimIndent()
        )
        schemaRevisionManager.publishRevision(REVISION_TEST_V1, 0)
        breakpointDebug("Can't execute due to broken version, see logs💰")
        schemaRevisionManager.forceExecuteSql(
            """
            UPDATE `$schemaVersion` SET `apply_dt` = '1000-01-01 00:00:00' WHERE `revision` = '$REVISION_TEST_V1';
            """.trimIndent()
        )
        breakpointDebug("Fix breakpoint, and downgrade💰")
        schemaRevisionManager.publishRevision(REVISION_TEST_V1, 0)
    }
}
