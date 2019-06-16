package pro.fessional.wings.faceless.flywave

import org.junit.Test

import org.junit.runner.RunWith
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
class SchemaShardingManagerTest {

    @Autowired
    lateinit var schemaShardingManager: SchemaShardingManager

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Test
    fun manageSharding() {
        schemaShardingManager.publishShard("SYS_COMMIT_JOURNAL", 2)
        schemaShardingManager.publishShard("SYS_COMMIT_JOURNAL", 0)
    }

    @Test
    fun moveShardData() {

        val sqls = FlywaveRevisionSqlScanner.scan(SchemaRevisionManager.REVISIONSQL_PATH)
        schemaRevisionManager.checkAndInitSql(sqls, 0)
        schemaRevisionManager.publishRevision(2019052001, 0)
        schemaRevisionManager.publishRevision(2019052101, 0)
        schemaRevisionManager.forceApplyBreak(2019052102, 2, true, "master")

        schemaShardingManager.publishShard("TST_中文也分表", 5)
        schemaShardingManager.shardingData("TST_中文也分表", true)
    }
}