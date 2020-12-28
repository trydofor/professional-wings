package pro.fessional.wings.faceless.flywave


import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.MethodOrderer.MethodName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pro.fessional.wings.faceless.WingsTestHelper
import pro.fessional.wings.faceless.WingsTestHelper.testcaseNotice
import pro.fessional.wings.faceless.flywave.SchemaFulldumpManager.Companion.groupedRegexp
import pro.fessional.wings.faceless.flywave.SchemaFulldumpManager.Companion.groupedTable
import java.io.File
import javax.sql.DataSource

/**
 * @author trydofor
 * @since 2019-06-20
 */

@SpringBootTest
@ActiveProfiles("init")
@TestMethodOrder(MethodName::class)
@Disabled("手动执行，避免污染Git提交文件")
@Tag("init")
class SchemaFulldumpManagerTest {

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var schemaFulldumpManager: SchemaFulldumpManager

    val fold = "./src/test/resources/wings-flywave/fulldump"

    @Autowired
    lateinit var wingsTestHelper: WingsTestHelper

    @Test
    fun `test0🦁清表重置`() {
        wingsTestHelper.cleanAndInit()
    }

    @Test
    fun `test1🦁DumpDdl🦁查文件`() {
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
        testcaseNotice("检查文件 $file")
    }

    @Test
    fun `test2🦁DumpRec🦁查文件`() {
        File(fold).mkdirs()
        val recs = schemaFulldumpManager.dumpRec(dataSource, groupedTable(true,
                "SYS_LIGHT_SEQUENCE",
                "-- schema",
                "sys_schema_journal",
                "sys_schema_version")
        )
        val file = "$fold/record.sql"
        schemaFulldumpManager.saveFile(file, recs)
        testcaseNotice("检查文件 $file")
    }
}