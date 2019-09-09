package pro.fessional.wings.faceless.sample

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.faceless.flywave.SchemaJournalManager
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager
import pro.fessional.wings.faceless.flywave.SchemaShardingManager
import pro.fessional.wings.faceless.spring.conf.WingsFlywaveVerProperties
import pro.fessional.wings.faceless.util.FlywaveRevisionSqlScanner

/**
 * @author trydofor
 * @since 2019-06-22
 */
@RunWith(SpringRunner::class)
@ActiveProfiles("shard")
@SpringBootTest(properties = ["debug = true"])
class WingsFlywaveShardJournalSample {

    @Autowired
    lateinit var schemaShardingManager: SchemaShardingManager

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Autowired
    lateinit var schemaJournalManager: SchemaJournalManager

    @Autowired
    lateinit var wingsFlywaveVerProperties: WingsFlywaveVerProperties

    @Test
    fun revisionShardJournal() {

        // 初始
        val sqls = FlywaveRevisionSqlScanner.scan(SchemaRevisionManager.REVISIONSQL_PATH)
        schemaRevisionManager.checkAndInitSql(sqls, 0)

        // 升级
        schemaRevisionManager.publishRevision(SchemaRevisionManager.INIT2ND_REVISION, 0)
        schemaRevisionManager.publishRevision(20190521_01, 0)

        // 单库强升
        schemaRevisionManager.forceApplyBreak(20190521_02, 2, true, "master")

        // 分表
        val table = "tst_中文也分表"

        schemaShardingManager.publishShard(table, 5)
        schemaShardingManager.shardingData(table, true)

        // 跟踪
        val ddls = SchemaJournalManager.JournalDdl(
                wingsFlywaveVerProperties.journalUpdate,
                wingsFlywaveVerProperties.triggerUpdate,
                wingsFlywaveVerProperties.journalDelete,
                wingsFlywaveVerProperties.triggerDelete
        )
        schemaJournalManager.checkAndInitDdl(table, ddls, 0)

        // 开启关闭
        schemaJournalManager.publishUpdate(table, false, 0)
        schemaJournalManager.publishUpdate(table, true, 0)
        schemaJournalManager.publishUpdate(table, false, 0)

        schemaJournalManager.publishDelete(table, false, 0)
        schemaJournalManager.publishDelete(table, true, 0)
        schemaJournalManager.publishDelete(table, false, 0)

        // 降级
        schemaRevisionManager.publishRevision(SchemaRevisionManager.INIT1ST_REVISION, 0)
    }
}