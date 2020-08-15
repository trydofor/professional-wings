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
import pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V1
import pro.fessional.wings.faceless.WingsTestHelper.breakpointDebug
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_1ST_SCHEMA
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_2ND_IDLOGS
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_3RD_ENU18N

/**
 * 默认profile，有writer和reader数据源，但只使用writer
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
        breakpointDebug("发布REVISION_2ND_IDLOGS💰")
        schemaRevisionManager.publishRevision(REVISION_2ND_IDLOGS, 0)
    }

    @Test
    fun `test2🦁当前版本`() {
        breakpointDebug("查看当前版本💰")
        val databaseVersion = schemaRevisionManager.currentRevision()
        for ((_, u) in databaseVersion) {
            Assert.assertEquals(REVISION_2ND_IDLOGS, u)
        }
    }

    @Test
    fun `test3🦁回滚再发`() {
        breakpointDebug("降级到1st版本💰")
        schemaRevisionManager.publishRevision(REVISION_1ST_SCHEMA, -1)
        breakpointDebug("升级到2st版本💰")
        schemaRevisionManager.publishRevision(REVISION_2ND_IDLOGS, -1)
        breakpointDebug("再次降级到1st版本💰")
        schemaRevisionManager.publishRevision(REVISION_1ST_SCHEMA, -1)
    }

    private val test3rdRevision = 20190615_01L

    @Test
    fun `test4🦁强加版本`() {
        breakpointDebug("强制增加版本615💰，但未执行")
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
            """.trimIndent(),

                "DROP TABLE IF EXISTS `test_temp`",
                -2)
    }

    @Test
    fun `test5🦁强发断版`() {
        breakpointDebug("发布615💰")
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, "test_temp", "test_temp_0", "test_temp_1")
        schemaRevisionManager.forceApplyBreak(test3rdRevision, -3, true)
        wingsTestHelper.assertHas(WingsTestHelper.Type.Table, "test_temp", "test_temp_0", "test_temp_1")
        breakpointDebug("取消615版💰")
        schemaRevisionManager.forceApplyBreak(test3rdRevision, -4, false)
        wingsTestHelper.assertNot(WingsTestHelper.Type.Table, "test_temp", "test_temp_0", "test_temp_1")
    }


    @Test
    fun `test6🦁重置520版`() {
        breakpointDebug("发布520💰")
        schemaRevisionManager.publishRevision(REVISION_2ND_IDLOGS, 0)
        breakpointDebug("降级520💰")
        schemaRevisionManager.forceApplyBreak(REVISION_2ND_IDLOGS, 0, false)
        breakpointDebug("重发520💰")
        schemaRevisionManager.publishRevision(REVISION_2ND_IDLOGS, 0)
    }

    @Test
    fun `test7🦁强制执行Sql`() {
        breakpointDebug("强制执行Sql💰")
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
    fun `test8🦁发布分支`() {
        breakpointDebug("扫描分支features/enum-i18n💰")
        val sqls = FlywaveRevisionScanner.scanBranch("features/enum-i18n")
        schemaRevisionManager.checkAndInitSql(sqls, 0, true)
        breakpointDebug("发布分支features/enum-i18n💰")
        schemaRevisionManager.publishRevision(REVISION_3RD_ENU18N, 0)
    }

    @Test
    fun `test9🦁断版维护`() {
        breakpointDebug("制作执行失败的断裂版本💰")
        schemaRevisionManager.forceExecuteSql("""
            UPDATE `sys_schema_version` SET `apply_dt` = '1000-01-01 00:00:17' WHERE `revision` = '$REVISION_2ND_IDLOGS';
            """.trimIndent())
        schemaRevisionManager.publishRevision(REVISION_2ND_IDLOGS, 0)
        breakpointDebug("因断裂版本不能执行，看日志💰")
        schemaRevisionManager.forceExecuteSql("""
            UPDATE `sys_schema_version` SET `apply_dt` = '1000-01-01 00:00:00' WHERE `revision` = '$REVISION_2ND_IDLOGS';
            """.trimIndent())
        breakpointDebug("修复断裂，降级版本💰")
        schemaRevisionManager.publishRevision(REVISION_TEST_V1, 0)
    }
}