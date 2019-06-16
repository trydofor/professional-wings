package pro.fessional.wings.faceless.flywave

import org.junit.Test

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.faceless.spring.conf.WingsFlywaveVerProperties

/**
 * @author trydofor
 * @since 2019-06-20
 */

@RunWith(SpringRunner::class)
@SpringBootTest
@ActiveProfiles("shard")
class SchemaJournalManagerTest {

    @Autowired
    lateinit var schemaJournalManager: SchemaJournalManager

    @Autowired
    lateinit var wingsFlywaveVerProperties: WingsFlywaveVerProperties


    @Test
    fun publishUpdate() {
        schemaJournalManager.publishUpdate("TST_中文也分表", true, 0)
        schemaJournalManager.publishUpdate("TST_中文也分表", false, 0)
    }

    @Test
    fun publishDelete() {
        schemaJournalManager.publishDelete("TST_中文也分表", true, 0)
        schemaJournalManager.publishDelete("TST_中文也分表", false, 0)
    }

    @Test
    fun checkAndInitDdl() {
        val ddls = SchemaJournalManager.JournalDdl(
                wingsFlywaveVerProperties.journalUpdate,
                wingsFlywaveVerProperties.triggerUpdate,
                wingsFlywaveVerProperties.journalDelete,
                wingsFlywaveVerProperties.triggerDelete
        )
        schemaJournalManager.checkAndInitDdl("TST_中文也分表", ddls, 0)
    }
}