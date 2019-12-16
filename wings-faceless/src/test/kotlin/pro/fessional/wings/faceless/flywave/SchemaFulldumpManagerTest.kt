package pro.fessional.wings.faceless.flywave

import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
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
    lateinit var sqlStatementParser: SqlStatementParser;

    @Autowired
    lateinit var schemaDefinitionLoader: SchemaDefinitionLoader;


    val fold = File("./src/main/resources/wings-flywave/fulldump")

    @Test
    fun test1DumpDdl() {
        val manager = SchemaFulldumpManager(sqlStatementParser, schemaDefinitionLoader);
        manager.dumpDdl(fold, dataSource)
    }

    @Test
    fun test2DumpRec() {
        val manager = SchemaFulldumpManager(sqlStatementParser, schemaDefinitionLoader);
        manager.dumpRec(fold, dataSource, "SYS_LIGHT_SEQUENCE", "sys_schema_journal", "sys_schema_version")
    }

}