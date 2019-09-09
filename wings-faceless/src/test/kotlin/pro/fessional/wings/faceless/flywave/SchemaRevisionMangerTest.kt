package pro.fessional.wings.faceless.flywave

import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager.Companion.INIT1ST_REVISION
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager.Companion.INIT2ND_REVISION
import pro.fessional.wings.faceless.util.FlywaveRevisionSqlScanner

/**
 * @author trydofor
 * @since 2019-06-05
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
open class SchemaRevisionMangerTest {

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Test
    fun test1InitFlywave() {
        val sqls = FlywaveRevisionSqlScanner.scan(SchemaRevisionManager.REVISIONSQL_PATH)
        schemaRevisionManager.checkAndInitSql(sqls, 0)
        schemaRevisionManager.publishRevision(INIT2ND_REVISION, 0)
    }

    @Test
    fun test2DatabaseVersion() {
        val databaseVersion = schemaRevisionManager.currentRevision()
        for ((t, u) in databaseVersion) {
            println("$t -> $u")
        }
    }

    @Test
    fun test3PublishVersion() {
        schemaRevisionManager.publishRevision(INIT1ST_REVISION, -1)
        schemaRevisionManager.publishRevision(INIT2ND_REVISION, -1)
        schemaRevisionManager.publishRevision(INIT1ST_REVISION, -1)
    }

    private val test3rdRevision = 20190615_01L
    @Test
    fun test4ForceUpdateSql() {
        schemaRevisionManager.forceUpdateSql(test3rdRevision, """
            CREATE TABLE `TEST_TEMP`(
              `SEQ_NAME` varchar(100) NOT NULL COMMENT '序列名'
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4 COMMENT ='TEST_TEMP';

            CREATE TABLE `TEST_TEMP_0`(
              `SEQ_NAME` varchar(100) NOT NULL COMMENT '序列名'
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4 COMMENT ='TEST_TEMP';

            CREATE TABLE `TEST_TEMP_1`(
              `SEQ_NAME` varchar(100) NOT NULL COMMENT '序列名'
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4 COMMENT ='TEST_TEMP';
            """.trimIndent(), "DROP TABLE IF EXISTS `TEST_TEMP`", -2)
    }

    @Test
    fun test5ForceApplyBreak() {
        schemaRevisionManager.forceApplyBreak(test3rdRevision, -3, true)
        schemaRevisionManager.forceApplyBreak(test3rdRevision, -4, false)
    }


    @Test
    fun test6ResetThen2nd() {
        schemaRevisionManager.publishRevision(INIT1ST_REVISION, 0)
        schemaRevisionManager.forceApplyBreak(INIT1ST_REVISION, 0, false)
        schemaRevisionManager.publishRevision(INIT2ND_REVISION, 0)

    }
}