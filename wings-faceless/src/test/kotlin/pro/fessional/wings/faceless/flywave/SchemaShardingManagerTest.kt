package pro.fessional.wings.faceless.flywave

import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.faceless.util.FlywaveRevisionSqlScanner

/**
 * @author trydofor
 * @since 2019-06-17
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@ActiveProfiles("shard")
//@Ignore("手动执行，使用shard配置")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SchemaShardingManagerTest {

    @Autowired
    lateinit var schemaShardingManager: SchemaShardingManager

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Test
    fun test1InitSharding() {
        val sqls = FlywaveRevisionSqlScanner.scan(SchemaRevisionManager.REVISIONSQL_PATH)
        schemaRevisionManager.checkAndInitSql(sqls, 0)
        schemaRevisionManager.publishRevision(20190520_01, 0)
        schemaRevisionManager.publishRevision(20190521_01, 0)
        schemaRevisionManager.forceApplyBreak(20190521_02, 2, true, "master")
    }

    @Test
    fun test2ManageSharding() {
        schemaShardingManager.publishShard("sys_schema_journal", 2)
        schemaShardingManager.publishShard("sys_schema_journal", 0)
    }

    @Test
    fun test3MoveShardData() {
        schemaShardingManager.publishShard("tst_中文也分表", 5)
        schemaShardingManager.shardingData("tst_中文也分表", true)
    }
}