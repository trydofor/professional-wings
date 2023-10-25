package pro.fessional.wings.faceless.flywave

import io.qameta.allure.TmsLink
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.MethodOrderer.MethodName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pro.fessional.wings.faceless.flywave.SchemaFulldumpManager.Companion.groupedRegexp
import pro.fessional.wings.faceless.flywave.SchemaFulldumpManager.Companion.groupedTable
import pro.fessional.wings.faceless.helper.WingsTestHelper
import pro.fessional.wings.faceless.helper.WingsTestHelper.testcaseNotice
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner
import java.io.File
import javax.sql.DataSource

/**
 * @author trydofor
 * @since 2019-06-20
 */

@SpringBootTest
@TestMethodOrder(MethodName::class)
@Disabled("Export table structure and data to avoid polluting Git workspace")
class SchemaFulldumpManagerTest {

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Autowired
    lateinit var schemaFulldumpManager: SchemaFulldumpManager

    val fold = "./src/test/resources/wings-flywave/fulldump"

    @Autowired
    lateinit var wingsTestHelper: WingsTestHelper

    @Test
    @TmsLink("C12036")
    fun test0CleanTables() {
        wingsTestHelper.cleanTable()
        schemaRevisionManager.checkAndInitSql(FlywaveRevisionScanner.scanMaster(), 0, true)
    }

    @Test
    @TmsLink("C12037")
    fun test1DumpDdlSeeFile() {
        File(fold).mkdirs()
        val dlls = schemaFulldumpManager.dumpDdl(dataSource, groupedRegexp(false,
                "SYS_LIGHT_SEQUENCE",
                "-- schema",
                "sys_schema_.*",
                "sys_commit_.*",
                "-- wings",
                "WG_.*"
        )
        )
        val file = "$fold/schema.sql"
        schemaFulldumpManager.saveFile(file, dlls)
        testcaseNotice("Check File $file")
    }

    @Test
    @TmsLink("C12038")
    fun test2DumpRecSeeFile() {
        File(fold).mkdirs()
        val recs = schemaFulldumpManager.dumpRec(dataSource, groupedTable(true,
                "SYS_LIGHT_SEQUENCE",
                "-- schema",
                "sys_schema_journal",
                "sys_schema_version")
        )
        val file = "$fold/record.sql"
        schemaFulldumpManager.saveFile(file, recs)
        testcaseNotice("Check File $file")
    }
}
