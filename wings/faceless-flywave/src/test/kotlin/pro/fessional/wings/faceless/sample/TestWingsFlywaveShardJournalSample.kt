package pro.fessional.wings.faceless.sample

import io.qameta.allure.TmsLink
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pro.fessional.wings.faceless.flywave.SchemaJournalManager
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager
import pro.fessional.wings.faceless.flywave.SchemaShardingManager
import pro.fessional.wings.faceless.flywave.WingsRevision
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner

/**
 * @author trydofor
 * @since 2019-06-22
 */
@SpringBootTest(properties = ["debug = true"])
@Disabled("Manually, tested by SchemaJournalManagerTest, SchemaShardingManagerTest")
class TestWingsFlywaveShardJournalSample {

    @Autowired
    lateinit var schemaShardingManager: SchemaShardingManager

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Autowired
    lateinit var schemaJournalManager: SchemaJournalManager

    @Test
    @TmsLink("C12074")
    fun revisionShardJournal() {
        // init
        val sqls = FlywaveRevisionScanner.scanMaster()
        schemaRevisionManager.checkAndInitSql(sqls, 0)

        // upgrade
        schemaRevisionManager.publishRevision(WingsRevision.V01_19_0520_01_IdLog.revision(), 0)
        schemaRevisionManager.publishRevision(WingsRevision.V90_22_0601_01_TestSchema.revision(), 0)

        // force upgrade in master database only
        schemaRevisionManager.forceApplyBreak(WingsRevision.V90_22_0601_02_TestRecord.revision(), 2, true, "master")

        // sharding
        val table = "tst_sharding"

        schemaShardingManager.publishShard(table, 5)
        // need sharding datasource, tested in shard testcase
//        schemaShardingManager.shardingData(table, true)

        // trace table
        schemaJournalManager.checkAndInitDdl(table, 0)

        // enable / disable
        schemaJournalManager.publishUpdate(table, false, 0)
        schemaJournalManager.publishUpdate(table, true, 0)
        schemaJournalManager.publishUpdate(table, false, 0)

        schemaJournalManager.publishDelete(table, false, 0)
        schemaJournalManager.publishDelete(table, true, 0)
        schemaJournalManager.publishDelete(table, false, 0)

        // downgrade
        schemaRevisionManager.publishRevision(WingsRevision.V00_19_0512_01_Schema.revision(), 0)
    }
}
