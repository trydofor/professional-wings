package pro.fessional.wings.faceless.sample

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V1
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_1ST_SCHEMA

/**
 * @author trydofor
 * @since 2019-06-22
 */
@SpringBootTest
@ActiveProfiles("init")
@Disabled("手动执行，以有SchemaRevisionMangerTest覆盖测试 ")
@Tag("init")
class WingsFlywaveInitDatabaseSample {

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Test
    fun force(){
        val sqls = FlywaveRevisionScanner.scanMaster()
        schemaRevisionManager.forceUpdateSql(sqls[REVISION_1ST_SCHEMA]!!, 0)
        schemaRevisionManager.forceUpdateSql(sqls[REVISION_TEST_V1]!!, 0)
    }
}