package pro.fessional.wings.faceless.flywave

import org.slf4j.LoggerFactory
import pro.fessional.wings.faceless.flywave.util.SimpleJdbcTemplate
import java.io.File
import java.util.LinkedList
import javax.sql.DataSource

/**
 * 进行表结构dump
 *
 * @author trydofor
 * @since 2019-12-15
 */
typealias FilterSorter = (List<String>) -> List<String>

class SchemaFulldumpManager(
        private val sqlStatementParser: SqlStatementParser,
        private val schemaDefinitionLoader: SchemaDefinitionLoader
) {
    private val logger = LoggerFactory.getLogger(SchemaFulldumpManager::class.java)

    companion object {
        val prefix = "--"
        /**
         * 满足正则的会被移除，按ascii自燃顺序排序
         */
        fun excludeRegexp(vararg regex: String): FilterSorter {
            return { tables ->
                val excludes = regex.map { it.toRegex(RegexOption.IGNORE_CASE) }
                tables.filter { excludes.find { tt -> tt.matches(it) } == null }.sorted()
            }
        }

        /**
         * 不满足正则的会被移除，按ascii自燃顺序排序
         */
        fun includeRegexp(vararg regex: String): FilterSorter {
            return { tables ->
                val excludes = regex.map { it.toRegex(RegexOption.IGNORE_CASE) }
                tables.filter { excludes.find { tt -> tt.matches(it) } != null }.sorted()
            }
        }

        /**
         * 按字符串相等（不区分大小写）过滤和排序。其中，`--` 开头表示分组分割线
         * @param only true表示只包匹配的，false把未匹配的放最后，按ascii自燃顺序排序
         */
        fun groupedTable(only: Boolean, vararg table: String): FilterSorter {
            return { tables ->
                val spec = ArrayList<String>(tables.size + table.size)
                val temp = LinkedList(tables)
                for (t in table) {
                    if (t.startsWith(prefix)) {
                        spec.add(t)
                        continue
                    }
                    val iter = temp.iterator()
                    while (iter.hasNext()) {
                        val s = iter.next()
                        if (s.equals(t, true)) {
                            iter.remove()
                            spec.add(s)
                            break
                        }
                    }
                }
                if (!only && temp.isNotEmpty()) {
                    spec.add(prefix)
                    spec.addAll(temp.sorted())
                }
                spec
            }
        }

        val excludeShadow: FilterSorter = excludeRegexp(""".*_\d+$""", """.*\$\w+$""")
    }

    enum class SqlType {
        ddlTable,
        ddlTrigger,
        dmlInsert,
        sqlUnknown,
        strComment,
    }

    data class SqlString(
            val table: String,
            val sqlType: SqlType,
            val sqlText: String
    )

    fun saveFile(path: String, sqls: List<SqlString>) = File(path).bufferedWriter().use { buf ->
        val trgs = ArrayList<SqlString>()
        for (sql in sqls) {
            when (sql.sqlType) {
                SqlType.strComment -> {
                    buf.write(sql.sqlText)
                }
                SqlType.ddlTrigger -> {
                    trgs.add(sql)
                }
                else -> {
                    buf.write( "$prefix ${sql.table} ${sql.sqlType}\n${sql.sqlText};")
                }
            }
            buf.write("\n\n")
        }

        if (trgs.isNotEmpty()) {
            buf.write("DELIMITER \$\$\n\n")
            for (sql in trgs) {
                buf.write("$prefix ${sql.table} ${sql.sqlType}\n${sql.sqlText} \$\$\n\n")
            }
            buf.write("DELIMITER ;\n")
        }
    }

    /**
     * dump 指定数据库的DDL (table,index和trigger)
     * 如果元素以`--`开头，表示注释
     *
     * @param database 数据库
     * @param filterSorter 过滤并排序
     * @return table_name :sql的map
     */
    fun dumpDdl(database: DataSource, filterSorter: FilterSorter = excludeShadow): List<SqlString> {

        val tables = schemaDefinitionLoader.showTables(database).let(filterSorter)
        val result = ArrayList<SqlString>()
        val ddlTableReg = """CREATE\s+TABLE\s+""".toRegex(RegexOption.IGNORE_CASE)
        val ddlTriggerReg = """CREATE\s+TRIGGER\s+""".toRegex(RegexOption.IGNORE_CASE)
        for (table in tables) {
            if (table.startsWith(prefix)) {
                logger.info("[dumpDdl] insert comment, {}", table)
                result.add(SqlString(table, SqlType.strComment, table))
                continue
            }

            logger.info("[dumpDdl] dump ddls for table={}", table)
            val ddls = schemaDefinitionLoader.showFullDdl(database, table)
            for (ddl in ddls) {
                when {
                    ddlTableReg.containsMatchIn(ddl) -> {
                        result.add(SqlString(table, SqlType.ddlTable, ddl))
                    }
                    ddlTriggerReg.containsMatchIn(ddl) -> {
                        result.add(SqlString(table, SqlType.ddlTrigger, ddl))
                    }
                    else -> {
                        result.add(SqlString(table, SqlType.sqlUnknown, ddl))
                    }
                }
            }
        }

        return result
    }

    /**
     * dump指定数据库的数据，INSERT语句
     * 如果元素以`--`开头，表示注释
     *
     * @param database 数据库
     * @param filterSorter 过滤并排序
     * @return table_name:sql的map
     */
    fun dumpRec(database: DataSource, filterSorter: FilterSorter = excludeShadow): List<SqlString> {

        val result = ArrayList<SqlString>()
        val tmpl = SimpleJdbcTemplate(database)

        val tables = schemaDefinitionLoader.showTables(database).let(filterSorter)
        val builder = StringBuilder()

        for (table in tables) {
            if (table.startsWith(prefix)) {
                logger.info("[dumpRec] insert comment, {}", table)
                result.add(SqlString(table, SqlType.strComment, table))
                continue
            }

            logger.info("[dumpRec] dump record for table={}", table)
            val tbl = sqlStatementParser.safeName(table)
            var ist = true
            tmpl.query("select * from $tbl") { rs ->
                val meta = rs.metaData
                val cnt = meta.columnCount

                if (ist) {
                    builder.append("INSERT INTO ").append(tbl)
                    (1..cnt).joinTo(builder, ",", "(", ")") {
                        sqlStatementParser.safeName(meta.getColumnName(it))
                    }
                    builder.append(" VALUES \n")
                    ist = false
                }

                (1..cnt).joinTo(builder, ",", "(", ")") {
                    sqlStatementParser.safeValue(rs.getObject(it))
                }

                builder.append(",\n")
            }
            result.add(SqlString(table, SqlType.dmlInsert, builder.substring(0, builder.length - 2)))
            builder.clear()
        }

        return result
    }
}