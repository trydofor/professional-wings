package pro.fessional.wings.oracle.flywave.impl

import org.springframework.jdbc.core.JdbcTemplate
import pro.fessional.wings.oracle.flywave.SchemaDefinitionLoader
import javax.sql.DataSource

/**
 * @author trydofor
 * @since 2019-06-13
 */
class MysqlDefinitionLoader : SchemaDefinitionLoader {

    override fun showTables(dataSource: DataSource): List<String> {
        val tmpl = JdbcTemplate(dataSource)
        return tmpl.queryForList("SHOW TABLES", String::class.java)
    }

    override fun showFullDdl(dataSource: DataSource, table: String): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showBodyDdl(dataSource: DataSource, table: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}