package pro.fessional.wings.faceless.service.lightid.impl


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import pro.fessional.wings.faceless.WingsTestHelper
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager
import pro.fessional.wings.faceless.flywave.WingsRevision
import pro.fessional.wings.faceless.service.lightid.LightIdService
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner
import java.util.concurrent.atomic.AtomicLong

/**
 * @author trydofor
 * @since 2019-06-04
 */
@SpringBootTest
open class LightIdServiceImplTest {

    @Autowired
    lateinit var lightIdService: LightIdService

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var wingsTestHelper: WingsTestHelper

    @Test
    fun `test0🦁清表重置`() {
        wingsTestHelper.cleanTable()
        schemaRevisionManager.checkAndInitSql(FlywaveRevisionScanner.scanMaster(), 0, true)
    }

    @Test
    fun `test1🦁获取ID`() {
        schemaRevisionManager.publishRevision(WingsRevision.V01_19_0520_01_IdLog.revision(), 0)

        val seqName = "sys_commit_journal"
        val bgn = AtomicLong(0)
        val stp = AtomicLong(0)
        jdbcTemplate.query("SELECT next_val, step_val FROM sys_light_sequence WHERE seq_name='$seqName'") {
            bgn.set(it.getLong("next_val"))
            stp.set(it.getLong("step_val"))
        }

        for (i in 1 .. (stp.get() + 10)) {
            assertEquals(bgn.getAndIncrement(), lightIdService.getId(seqName, 0))
        }
    }
}
