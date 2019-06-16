package pro.fessional.wings.faceless.flywave.util

import org.springframework.jdbc.core.JdbcTemplate
import java.sql.ResultSet
import java.util.concurrent.atomic.AtomicInteger
import javax.sql.DataSource

/**
 * flywave 有可能分离出去，尽量与spring解藕
 * @author trydofor
 * @since 2019-06-16
 */
class SimpleJdbcTemplate(val dataSource: DataSource, val name: String = "unnamed") {
    // 未来可能不使用任何依赖
    private val jdbcTmpl: JdbcTemplate = JdbcTemplate(dataSource)

    fun count(sql: String, vararg args: Any?): Int {
        val count = AtomicInteger(0)
        jdbcTmpl.query(sql, args) {
            count.set(it.getInt(1))
        }
        return count.get()
    }

    fun query(sql: String, vararg args: Any?, handler: (ResultSet) -> Unit) {
        jdbcTmpl.query(sql, args, handler)
    }

    fun update(sql: String, vararg args: Any?): Int {
        return jdbcTmpl.update(sql, *args)
    }

    fun execute(sql: String) {
        jdbcTmpl.execute(sql)
    }
}