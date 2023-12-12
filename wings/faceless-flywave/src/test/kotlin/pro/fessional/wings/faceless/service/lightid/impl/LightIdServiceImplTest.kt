package pro.fessional.wings.faceless.service.lightid.impl


import io.qameta.allure.TmsLink
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import pro.fessional.mirana.id.LightIdBufferedProvider
import pro.fessional.mirana.id.LightIdUtil
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager
import pro.fessional.wings.faceless.flywave.WingsRevision
import pro.fessional.wings.faceless.helper.WingsTestHelper
import pro.fessional.wings.faceless.service.journal.JournalService
import pro.fessional.wings.faceless.service.lightid.LightIdService
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong


/**
 * @author trydofor
 * @since 2019-06-04
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName::class)
@DependsOnDatabaseInitialization
open class LightIdServiceImplTest {

    @Autowired
    lateinit var lightIdService: LightIdService

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var wingsTestHelper: WingsTestHelper

    @Autowired
    lateinit var journalService: JournalService

    @Autowired
    lateinit var lightIdBufferedProvider: LightIdBufferedProvider

    private val seqName = "sys_commit_journal"

    @Test
    @TmsLink("C12075")
    fun test0CleanTables() {
        wingsTestHelper.cleanTable()
        schemaRevisionManager.checkAndInitSql(FlywaveRevisionScanner.scanMaster(), 0, true)
    }

    @Test
    @TmsLink("C12076")
    fun test1FetchId() {
        schemaRevisionManager.publishRevision(WingsRevision.V01_19_0520_01_IdLog.revision(), 0)

        val bgn = AtomicLong(0)
        val stp = AtomicLong(0)
        jdbcTemplate.query("SELECT next_val, step_val FROM sys_light_sequence WHERE seq_name='$seqName'") {
            bgn.set(it.getLong("next_val"))
            stp.set(it.getLong("step_val"))
        }

        for (i in 1..(stp.get() + 10)) {
            assertEquals(bgn.getAndIncrement(), lightIdService.getId(seqName, 0))
        }
    }

    @Test
    @TmsLink("C12077")
    fun test2FetchId() {
        // consumer
        journalService.commit(this.javaClass) {
            print(it.commitDt)
        }

        // function
        journalService.submit(this.javaClass) { it.commitDt }
    }

    @Test
    @TmsLink("C12078")
    fun test3CompeteId() {
        val threadCnt = 100
        val loopCount = 5000
        val idCache = ConcurrentHashMap<Long, Long>()
        val service = Executors.newFixedThreadPool(threadCnt / 2)

        val latch = CountDownLatch(threadCnt)
        for (i in 0 until threadCnt) {
            service.submit {
                for (j in 0 until loopCount) {
                    val id = lightIdService.getId(seqName, 0)
                    idCache[id] = id
                }
                latch.countDown()
            }
        }
        latch.await()
        assertEquals(loopCount * threadCnt, idCache.size)
    }

    @Test
    @TmsLink("C12079")
    fun test4RangeId() {
        val rg = 999_000_000_000L
        val id = lightIdService.getId(seqName, 0)
        lightIdBufferedProvider.setGenerator { _, b, s -> LightIdUtil.toId(b, s + rg) }
        val id1 = lightIdService.getId(seqName, 0)
        assertEquals(rg + 1, id1 - id)
        lightIdBufferedProvider.generator = LightIdBufferedProvider.GENERATOR
        val id2 = lightIdService.getId(seqName, 0)
        assertEquals(2, id2 - id)
    }
}
