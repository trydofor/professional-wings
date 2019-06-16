package pro.fessional.wings.faceless.flywave

import org.junit.Test
import org.junit.runner.RunWith
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
open class SchemaRevisionMangerTest {

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Test
    fun initFlywave() {
        val sqls = FlywaveRevisionSqlScanner.scan(SchemaRevisionManager.REVISIONSQL_PATH)
        schemaRevisionManager.checkAndInitSql(sqls, 0)
        schemaRevisionManager.publishRevision(INIT2ND_REVISION, 0)
    }

    @Test
    fun getDatabaseVersion() {
        val databaseVersion = schemaRevisionManager.currentRevision()
        for ((t, u) in databaseVersion) {
            println("$t -> $u")
        }
    }

    @Test
    fun checkAndSave() {
        val sqls = FlywaveRevisionSqlScanner.scan(SchemaRevisionManager.REVISIONSQL_PATH)
        schemaRevisionManager.checkAndInitSql(sqls, 0)
    }

    @Test
    fun resetZero() {
        schemaRevisionManager.publishRevision(INIT1ST_REVISION, 0)
        schemaRevisionManager.forceApplyBreak(INIT1ST_REVISION, 0, false)
    }

    @Test
    fun publishVersion() {
        schemaRevisionManager.publishRevision(INIT1ST_REVISION, -1)
        schemaRevisionManager.publishRevision(INIT2ND_REVISION, -1)
        schemaRevisionManager.publishRevision(INIT1ST_REVISION, -1)
    }

    private val test3rdRevision = 2019061501L
    @Test
    fun forceUpdateSql() {
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
    fun forceApplyBreak() {
        schemaRevisionManager.forceApplyBreak(test3rdRevision, -3, true)
        schemaRevisionManager.forceApplyBreak(test3rdRevision, -4, false)
    }

}