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
 * @since 2019-06-20
 */

@RunWith(SpringRunner::class)
@SpringBootTest
@ActiveProfiles("shard")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SchemaJournalManagerTest {

    @Autowired
    lateinit var schemaJournalManager: SchemaJournalManager

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Test
    fun test1InitSharding(){
        val sqls = FlywaveRevisionSqlScanner.scan(SchemaRevisionManager.REVISIONSQL_PATH)
        schemaRevisionManager.checkAndInitSql(sqls, 0)
        schemaRevisionManager.publishRevision(20190520_01, 0)
        schemaRevisionManager.publishRevision(20190521_01, 0)
    }

    @Test
    fun test2PublishUpdate() {
        schemaJournalManager.publishUpdate("tst_中文也分表", true, 0)
        schemaJournalManager.publishUpdate("tst_中文也分表", false, 0)
    }

    @Test
    fun test3PublishDelete() {
        schemaJournalManager.publishDelete("tst_中文也分表", true, 0)
        schemaJournalManager.publishDelete("tst_中文也分表", false, 0)
    }
}