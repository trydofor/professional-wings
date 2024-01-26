package pro.fessional.wings.faceless.flywave

import io.qameta.allure.TmsLink
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization
import org.springframework.boot.test.context.SpringBootTest
import pro.fessional.wings.faceless.database.DataSourceContext
import pro.fessional.wings.faceless.flywave.util.SimpleJdbcTemplate

/**
 * @author trydofor
 * @since 2019-06-19
 */
@SpringBootTest
@DependsOnDatabaseInitialization
class SimpleJdbcTemplateTest {

    @Autowired
    lateinit var dataSources: DataSourceContext

    @Test
    @TmsLink("C12058")
    fun simpleJdbcTemplate() {
        val tmpl = SimpleJdbcTemplate(dataSources.current, "current")
        tmpl.execute("show tables")
    }
}
