package pro.fessional.wings.faceless.flywave

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pro.fessional.wings.faceless.database.DataSourceContext
import pro.fessional.wings.faceless.flywave.util.SimpleJdbcTemplate

/**
 * @author trydofor
 * @since 2019-06-19
 */
@SpringBootTest
class SimpleJdbcTemplateTest {

    @Autowired
    lateinit var dataSources: DataSourceContext

    @Test
    fun metadata() {
        val tmpl = SimpleJdbcTemplate(dataSources.current, "first")
        tmpl.execute("show tables")
    }
}
