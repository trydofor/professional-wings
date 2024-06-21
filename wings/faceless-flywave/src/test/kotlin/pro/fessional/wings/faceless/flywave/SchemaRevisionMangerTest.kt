package pro.fessional.wings.faceless.flywave

import io.qameta.allure.TmsLink
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.MethodOrderer.MethodName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization
import org.springframework.boot.test.context.SpringBootTest
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner
import pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper
import pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper.breakpointDebug

/**
 * Default profile, there are writer and reader datasource, use the writer only.
 * @author trydofor
 * @since 2019-06-05
 */
@SpringBootTest(
    properties = [
        "debug=true",
        "java.awt.headless=false",
        "wings.faceless.flywave.ver.schema-version-table=win_schema_version",
    ]
)
@DependsOnDatabaseInitialization
@TestMethodOrder(MethodName::class)
class SchemaRevisionMangerTest {

    val log: Logger = LoggerFactory.getLogger(SqlSegmentParserTest::class.java)

    private val revi1Schema: Long = WingsRevision.V00_19_0512_01_Schema.revision() + 1
    private val revi2IdLog: Long = WingsRevision.V01_19_0520_01_IdLog.revision()

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Autowired
    lateinit var testingDatabaseHelper: TestingDatabaseHelper

    private val schemaVersion = "win_schema_version"

    @Test
    @TmsLink("C12047")
    fun test0CleanTables() {
        testingDatabaseHelper.cleanTable()
        val sqls = FlywaveRevisionScanner.helper()
            .master()
            .replace(WingsRevision.V00_19_0512_01_Schema.revision(), revi1Schema, true)
            .modify(revi1Schema, "sys_schema_version", schemaVersion)
//                .modify("rename win_schema_version") { _, sql ->
//                    if (sql.revision == REVISION_1ST_SCHEMA) {
//                        sql.undoText = sql.undoText.replace("sys_schema_version", schemaVersion)
//                        sql.uptoText = sql.uptoText.replace("sys_schema_version", schemaVersion)
//                    }
//                }
            .scan()
        schemaRevisionManager.checkAndInitSql(sqls, 0, true)
        testingDatabaseHelper.assertHas(TestingDatabaseHelper.Type.Table, "win_schema_version")
        testingDatabaseHelper.assertNot(TestingDatabaseHelper.Type.Table, "sys_light_sequence")
    }

    private fun assertRevision(revi: Long) {
        val databaseVersion = schemaRevisionManager.currentRevision()
        for ((_, u) in databaseVersion) {
            assertEquals(revi, u)
        }
    }

    @Test
    @TmsLink("C12048")
    fun test1Publish520() {
        breakpointDebug("Publish to REVISION_2ND_IDLOGS💰")
        schemaRevisionManager.publishRevision(revi2IdLog, 0)
    }

    @Test
    @TmsLink("C12049")
    fun test2CurrentRevi() {
        breakpointDebug("Check current revision💰")
        assertRevision(revi2IdLog)
    }

    @Test
    @TmsLink("C12050")
    fun test2ReviLine() {
        breakpointDebug("Check current revision line💰")
        val databaseVersion = schemaRevisionManager.statusRevisions()
        for ((d, u) in databaseVersion) {
            if (u == null) {
                log.info("$d - -1")
            } else {
                log.info("$d -")
                for (entry in u.entries) {
                    log.info(" ${entry.key} : ${entry.value}")
                }
            }
        }
    }

    @Test
    @TmsLink("C12051")
    fun test3DownThenUp() {
        breakpointDebug("Downgrade to 1st💰")
        schemaRevisionManager.publishRevision(revi1Schema, -1)
        assertRevision(revi1Schema)

        breakpointDebug("Bumping to 2nd💰")
        schemaRevisionManager.bumpingRevision(revi2IdLog, -1)
        testingDatabaseHelper.assertNot(TestingDatabaseHelper.Type.Table, "sys_light_sequence")
        assertRevision(revi2IdLog)
        breakpointDebug("Bumping to 1st💰")
        schemaRevisionManager.bumpingRevision(revi1Schema, -1)
        assertRevision(revi1Schema)

        breakpointDebug("Upgrade to 2nd💰")
        schemaRevisionManager.publishRevision(revi2IdLog, -1)
        testingDatabaseHelper.assertHas(TestingDatabaseHelper.Type.Table, "sys_light_sequence")
        assertRevision(revi2IdLog)

        breakpointDebug("Again downgrade to 1st💰")
        schemaRevisionManager.publishRevision(revi1Schema, -1)
        testingDatabaseHelper.assertNot(TestingDatabaseHelper.Type.Table, "sys_light_sequence")
        assertRevision(revi1Schema)
    }

    private val test3rdRevision = 20190615_01L

    @Test
    @TmsLink("C12052")
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
    @TmsLink("C12053")
    fun test5ForceBreak() {
        breakpointDebug("Publish 615💰")
        testingDatabaseHelper.assertNot(TestingDatabaseHelper.Type.Table, "test_temp", "test_temp_0", "test_temp_1")
        schemaRevisionManager.forceApplyBreak(test3rdRevision, -3, true)
        testingDatabaseHelper.assertHas(TestingDatabaseHelper.Type.Table, "test_temp", "test_temp_0", "test_temp_1")
        breakpointDebug("Cancel 615💰")
        schemaRevisionManager.forceApplyBreak(test3rdRevision, -4, false)
        testingDatabaseHelper.assertNot(TestingDatabaseHelper.Type.Table, "test_temp", "test_temp_0", "test_temp_1")
    }


    @Test
    @TmsLink("C12054")
    fun test6Republish520() {
        breakpointDebug("Publish 520💰")
        schemaRevisionManager.publishRevision(revi2IdLog, 0)
        breakpointDebug("Downgrade 520💰")
        schemaRevisionManager.forceApplyBreak(revi2IdLog, 0, false)
        breakpointDebug("Re-publish 520💰")
        schemaRevisionManager.publishRevision(revi2IdLog, 0)
    }

    @Test
    @TmsLink("C12055")
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
        testingDatabaseHelper.assertNot(TestingDatabaseHelper.Type.Table, "test_temp_x")
    }

    @Test
    @TmsLink("C12056")
    fun test8PublishBranch() {
        breakpointDebug("scan branch feature/01-enum-i18n💰")
        val sqls = FlywaveRevisionScanner.scanBranch("feature/01-enum-i18n")
        schemaRevisionManager.checkAndInitSql(sqls, 0, true)
        breakpointDebug("publish branch feature/01-enum-i18n💰")
        val revi = WingsRevision.V01_19_0521_01_EnumI18n.revision()
        schemaRevisionManager.publishRevision(revi, 0)
        assertRevision(revi)
    }

    @Test
    @TmsLink("C12057")
    fun test9MaintainBreak() {
        breakpointDebug("Prepare a breakpoint revision to mock a failure💰")
        val revision = WingsRevision.V90_22_0601_01_TestSchema.revision()
        schemaRevisionManager.forceExecuteSql(
            """
            UPDATE `$schemaVersion` SET `apply_dt` = '1000-01-01 00:00:17' WHERE `revision` = '$revision';
            """.trimIndent()
        )
        schemaRevisionManager.publishRevision(revision, 0)
        breakpointDebug("Can't execute due to broken version, see logs💰")
        schemaRevisionManager.forceExecuteSql(
            """
            UPDATE `$schemaVersion` SET `apply_dt` = '1000-01-01 00:00:00' WHERE `revision` = '$revision';
            """.trimIndent()
        )
        breakpointDebug("Fix breakpoint, and downgrade💰")
        schemaRevisionManager.publishRevision(revision, 0)
    }
}
