package pro.fessional.wings.faceless.flywave

import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.faceless.WingsTestHelper
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager.Companion.INIT1ST_REVISION
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager.Companion.INIT2ND_REVISION

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

    @Autowired
    lateinit var wingsTestHelper: WingsTestHelper

    @Test
    fun `test0🦁清表重置`() {
        wingsTestHelper.cleanAndInit()
    }

    @Test
    fun `test1🦁发布520版`() {
        schemaRevisionManager.publishRevision(INIT2ND_REVISION, 0)
    }

    @Test
    fun `test2🦁当前版本`() {
        val databaseVersion = schemaRevisionManager.currentRevision()
        for ((_, u) in databaseVersion) {
            Assert.assertEquals(INIT2ND_REVISION, u)
        }
    }

    @Test
    fun `test3🦁回滚再发`() {
        schemaRevisionManager.publishRevision(INIT1ST_REVISION, -1)
        schemaRevisionManager.publishRevision(INIT2ND_REVISION, -1)
        schemaRevisionManager.publishRevision(INIT1ST_REVISION, -1)
    }

    private val test3rdRevision = 20190615_01L

    @Test
    fun `test4🦁强加版本`() {
        schemaRevisionManager.forceUpdateSql(test3rdRevision, """
            CREATE TABLE `test_temp`(
              `SEQ_NAME` varchar(100) NOT NULL COMMENT '序列名'
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4 COMMENT ='test_temp';

            CREATE TABLE `test_temp_0`(
              `SEQ_NAME` varchar(100) NOT NULL COMMENT '序列名'
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4 COMMENT ='test_temp';

            CREATE TABLE `test_temp_1`(
              `SEQ_NAME` varchar(100) NOT NULL COMMENT '序列名'
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4 COMMENT ='test_temp';
            """.trimIndent(), "DROP TABLE IF EXISTS `test_temp`", -2)
    }

    @Test
    fun `test5🦁强发断版`() {
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, "test_temp", "test_temp_0", "test_temp_1")
        schemaRevisionManager.forceApplyBreak(test3rdRevision, -3, true)
        wingsTestHelper.assertHas(WingsTestHelper.Type.Table, "test_temp", "test_temp_0", "test_temp_1")
        schemaRevisionManager.forceApplyBreak(test3rdRevision, -4, false)
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, "test_temp", "test_temp_0", "test_temp_1")
    }


    @Test
    fun `test6🦁重置520版`() {
        schemaRevisionManager.publishRevision(INIT2ND_REVISION, 0)
        schemaRevisionManager.forceApplyBreak(INIT2ND_REVISION, 0, false)
        schemaRevisionManager.publishRevision(INIT2ND_REVISION, 0)
    }

    @Test
    fun `test7🦁强制执行Sql`() {
        schemaRevisionManager.forceExecuteSql("""
            CREATE TABLE `test_temp_x`(
              `SEQ_NAME` varchar(100) NOT NULL COMMENT '序列名'
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4 COMMENT ='test_temp';

            DROP TABLE IF EXISTS `test_temp_x`;
            """.trimIndent())
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, "test_temp_x")
    }

    @Test
    fun `test8🦁断版维护`() {
        schemaRevisionManager.publishRevision(INIT2ND_REVISION, 0)
        schemaRevisionManager.forceExecuteSql("""
            UPDATE `sys_schema_version` SET `apply_dt` = '1000-01-01 00:00:17' WHERE `revision` = '$INIT2ND_REVISION';
            """.trimIndent())
        schemaRevisionManager.publishRevision(20190521_01, 0)
    }

}