package pro.fessional.wings.faceless.flywave.util

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.JdbcUtils
import java.sql.DatabaseMetaData
import java.sql.ResultSet
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import javax.sql.DataSource

/**
 * flywave 有可能分离出去，尽量与spring解藕
 * @author trydofor
 * @since 2019-06-16
 */
class SimpleJdbcTemplate(val dataSource: DataSource, val name: String = "unnamed") {

    private val jdbcTmpl: JdbcTemplate

    init {
        this.jdbcTmpl = templates.computeIfAbsent(dataSource) { JdbcTemplate(dataSource) }
    }

    fun jdbcUrl() = JdbcUtils.extractDatabaseMetaData<String>(dataSource) {
        DatabaseMetaData::class.java.getMethod("getURL").invoke(it) as String
    }

    fun count(sql: String, vararg args: Any?): Int {
        val count = AtomicInteger(0)
        if (args.isEmpty()) {
            jdbcTmpl.query(sql) {
                count.set(it.getInt(1))
            }
        } else {
            jdbcTmpl.query(sql, { rs -> count.set(rs.getInt(1)) }, *args)
        }
        return count.get()
    }

    /**
     * 处理每一条数据
     * @param handler RowCallbackHandler
     */
    fun query(sql: String, vararg args: Any?, handler: (ResultSet) -> Unit) {
        if (args.isEmpty()) {
            jdbcTmpl.query(sql, handler)
        } else {
            jdbcTmpl.query(sql, handler, *args)
        }
    }

    fun update(sql: String, vararg args: Any?): Int {
        return if (args.isEmpty()) {
            jdbcTmpl.update(sql)
        } else {
            jdbcTmpl.update(sql, *args)
        }
    }

    fun execute(sql: String) {
        jdbcTmpl.execute(sql)
    }

    companion object {
        private val templates = ConcurrentHashMap<DataSource, JdbcTemplate>()
    }
}