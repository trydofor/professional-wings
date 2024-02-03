package pro.fessional.wings.faceless.sample

import io.qameta.allure.TmsLink
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager
import pro.fessional.wings.faceless.flywave.WingsRevision
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner

/**
 * @author trydofor
 * @since 2019-06-22
 */
@SpringBootTest
@Disabled("Manually, tested by SchemaRevisionMangerTest")
class TestWingsFlywaveInitDatabaseSample {

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Test
    @TmsLink("C12073")
    fun forceUpdateSql() {
        val sqls = FlywaveRevisionScanner.scanMaster()
        schemaRevisionManager.forceUpdateSql(sqls[WingsRevision.V00_19_0512_01_Schema.revision()]!!, 0)
        schemaRevisionManager.forceUpdateSql(sqls[WingsRevision.V90_22_0601_01_TestSchema.revision()]!!, 0)
    }
}
