package pro.fessional.wings.faceless.flywave

import org.junit.Test

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.faceless.flywave.util.SimpleJdbcTemplate

/**
 * @author trydofor
 * @since 2019-06-19
 */
@RunWith(SpringRunner::class)
@SpringBootTest
class SimpleJdbcTemplateTest {

    @Autowired
    lateinit var flywaveDataSources: FlywaveDataSources

    @Test
    fun metadata() {
        val tmpl = SimpleJdbcTemplate(flywaveDataSources.plains().values.iterator().next(), "first")
        tmpl.execute("show tables")
    }
}