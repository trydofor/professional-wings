package pro.fessional.wings.faceless.service.lightid.impl

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.faceless.WingsTestHelper
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager
import pro.fessional.wings.faceless.service.lightid.LightIdService
import pro.fessional.wings.faceless.util.FlywaveRevisionSqlScanner
import java.util.concurrent.atomic.AtomicLong

/**
 * @author trydofor
 * @since 2019-06-04
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@ActiveProfiles("init")
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
        wingsTestHelper.cleanAndInit()
    }

    @Test
    fun `test1🦁获取ID`() {
        schemaRevisionManager.publishRevision(SchemaRevisionManager.INIT2ND_REVISION, 0)

        val seqName = "sys_commit_journal"
        val bgn = AtomicLong(0)
        val stp = AtomicLong(0)
        jdbcTemplate.query("SELECT next_val, step_val FROM sys_light_sequence WHERE seq_name='$seqName'") {
            bgn.set(it.getLong("next_val"))
            stp.set(it.getLong("step_val"))
        }

        for (i in 1 .. (stp.get() + 10)) {
            Assert.assertEquals(bgn.getAndIncrement(), lightIdService.getId(seqName, 0))
        }
    }
}