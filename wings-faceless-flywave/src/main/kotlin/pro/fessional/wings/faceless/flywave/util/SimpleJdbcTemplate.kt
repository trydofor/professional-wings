package pro.fessional.wings.faceless.flywave.util

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.JdbcUtils
import java.sql.DatabaseMetaData
import java.sql.ResultSet
import java.util.concurrent.ConcurrentHashMap
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
        return if (args.isEmpty()) {
            jdbcTmpl.queryForObject(sql, Int::class.java)!!
        } else {
            jdbcTmpl.queryForObject(sql, Int::class.java, args)
        }
    }

    /**
     * 处理每一条数据
     * @param handler org.springframework.jdbc.core.RowCallbackHandler
     */
    fun query(sql: String, vararg args: Any?, handler: (ResultSet) -> Unit) {
        if (args.isEmpty()) {
            jdbcTmpl.query(sql, handler)
        } else {
            // [TYPE_INFERENCE_CANDIDATE_WITH_SAM_AND_VARARG]
            // Please use spread operator to pass an array as vararg.
            // It will be an error in 1.5.
            @Suppress("TYPE_INFERENCE_CANDIDATE_WITH_SAM_AND_VARARG")
            jdbcTmpl.query(sql, handler, args)
        }
    }

    fun update(sql: String, vararg args: Any?): Int {
        return if (args.isEmpty()) {
            jdbcTmpl.update(sql)
        } else {
            jdbcTmpl.update(sql, args)
        }
    }

    fun execute(sql: String) {
        jdbcTmpl.execute(sql)
    }

    companion object {
        private val templates = ConcurrentHashMap<DataSource, JdbcTemplate>()
    }
}
