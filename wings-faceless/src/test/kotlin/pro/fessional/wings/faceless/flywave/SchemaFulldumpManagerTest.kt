package pro.fessional.wings.faceless.flywave

import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.faceless.flywave.SchemaFulldumpManager.Companion.groupedRegexp
import pro.fessional.wings.faceless.flywave.SchemaFulldumpManager.Companion.groupedTable
import java.io.File
import javax.sql.DataSource

/**
 * @author trydofor
 * @since 2019-06-20
 */

@RunWith(SpringRunner::class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SchemaFulldumpManagerTest {

    @Autowired
    lateinit var dataSource: DataSource;

    @Autowired
    lateinit var schemaFulldumpManager: SchemaFulldumpManager;

    val fold = "./src/test/resources/wings-flywave/fulldump"

    @Test
    fun test1DumpDdl() {
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
        schemaFulldumpManager.saveFile("$fold/schema.sql", dlls)
    }

    @Test
    fun test2DumpRec() {
        File(fold).mkdirs()
        val recs = schemaFulldumpManager.dumpRec(dataSource, groupedTable(true,
                "SYS_LIGHT_SEQUENCE",
                "-- schema",
                "sys_schema_journal",
                "sys_schema_version")
        )
        schemaFulldumpManager.saveFile("$fold/record.sql", recs)
    }
}