package pro.fessional.wings.oracle.flywave

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.oracle.flywave.SchemaVersionManger.Companion.init1stRevision
import pro.fessional.wings.oracle.flywave.SchemaVersionManger.Companion.init2ndRevision
import pro.fessional.wings.oracle.util.FlywaveRevisionSqlScanner

/**
 * @author trydofor
 * @since 2019-06-05
 */
@RunWith(SpringRunner::class)
@SpringBootTest
open class SchemaVersionMangerTest {

    @Autowired
    lateinit var schemaVersionManger: SchemaVersionManger

    @Test
    fun initFlywave() {
        val sqls = FlywaveRevisionSqlScanner.scan(SchemaVersionManger.revisionSqlPath)
        schemaVersionManger.checkAndInitSql(sqls, 0)
        schemaVersionManger.publishRevision(init2ndRevision, 0)
    }

    @Test
    fun getDatabaseVersion() {
        val databaseVersion = schemaVersionManger.showCurrentVersion()
        for ((t, u) in databaseVersion) {
            println("$t -> $u")
        }
    }

    @Test
    fun checkAndSave() {
        val sqls = FlywaveRevisionSqlScanner.scan(SchemaVersionManger.revisionSqlPath)
        schemaVersionManger.checkAndInitSql(sqls, 0)
    }

    @Test
    fun resetZero() {
        schemaVersionManger.publishRevision(init1stRevision, 0)
        schemaVersionManger.forceApplyBreak(init1stRevision, 0, false)
    }

    @Test
    fun publishVersion() {
        schemaVersionManger.publishRevision(init1stRevision, -1)
        schemaVersionManger.publishRevision(init2ndRevision, -1)
        schemaVersionManger.publishRevision(init1stRevision, -1)
    }

    private val test3rdRevision = 2019061501L
    @Test
    fun forceUpdateSql() {
        schemaVersionManger.forceUpdateSql(test3rdRevision, """
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
        schemaVersionManger.forceApplyBreak(test3rdRevision, -3, true)
        schemaVersionManger.forceApplyBreak(test3rdRevision, -4, false)
    }

}