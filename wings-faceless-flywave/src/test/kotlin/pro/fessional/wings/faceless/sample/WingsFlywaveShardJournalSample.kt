package pro.fessional.wings.faceless.sample

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V1
import pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V2
import pro.fessional.wings.faceless.flywave.SchemaJournalManager
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager
import pro.fessional.wings.faceless.flywave.SchemaShardingManager
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_1ST_SCHEMA
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_2ND_IDLOGS

/**
 * @author trydofor
 * @since 2019-06-22
 */
@ActiveProfiles("shard")
@SpringBootTest(properties = ["debug = true"])
@Disabled("手动执行，以有SchemaJournalManagerTest，SchemaShardingManagerTest覆盖测试 ")
class WingsFlywaveShardJournalSample {

    @Autowired
    lateinit var schemaShardingManager: SchemaShardingManager

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Autowired
    lateinit var schemaJournalManager: SchemaJournalManager

    @Test
    fun revisionShardJournal() {

        // 初始
        val sqls = FlywaveRevisionScanner.scanMaster()
        schemaRevisionManager.checkAndInitSql(sqls, 0)

        // 升级
        schemaRevisionManager.publishRevision(REVISION_2ND_IDLOGS, 0)
        schemaRevisionManager.publishRevision(REVISION_TEST_V1, 0)

        // 单库强升
        schemaRevisionManager.forceApplyBreak(REVISION_TEST_V2, 2, true, "master")

        // 分表
        val table = "tst_中文也分表"

        schemaShardingManager.publishShard(table, 5)
        schemaShardingManager.shardingData(table, true)

        // 跟踪
        schemaJournalManager.checkAndInitDdl(table, 0)

        // 开启关闭
        schemaJournalManager.publishUpdate(table, false, 0)
        schemaJournalManager.publishUpdate(table, true, 0)
        schemaJournalManager.publishUpdate(table, false, 0)

        schemaJournalManager.publishDelete(table, false, 0)
        schemaJournalManager.publishDelete(table, true, 0)
        schemaJournalManager.publishDelete(table, false, 0)

        // 降级
        schemaRevisionManager.publishRevision(REVISION_1ST_SCHEMA, 0)
    }
}