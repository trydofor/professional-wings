package pro.fessional.wings.faceless.flywave

import org.slf4j.LoggerFactory
import pro.fessional.mirana.data.Null
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
    private val log = LoggerFactory.getLogger(SchemaFulldumpManager::class.java)

    companion object {
        const val prefix = "--"

        /**
         * 满足正则（全匹配matches，忽略大小写）的会被移除，按ascii自燃顺序排序
         */
        @JvmStatic
        fun excludeRegexp(vararg regex: String): FilterSorter {
            return { tables ->
                val excludes = regex.map { it.toRegex(RegexOption.IGNORE_CASE) }
                tables.filter { excludes.find { tt -> tt.matches(it) } == null }.sorted()
            }
        }

        /**
         * 不满足正则（全匹配matches，忽略大小写）的会被移除，按ascii自燃顺序排序
         */
        @JvmStatic
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
        @JvmStatic
        fun groupedTable(only: Boolean = true, vararg table: String): FilterSorter {
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

        /**
         * 按正则（全匹配matches，忽略大小写）过滤和排序。其中，`--` 开头表示分组分割线
         * @param only true表示只包匹配的，false把未匹配的放最后，按ascii自燃顺序排序
         */
        @JvmStatic
        fun groupedRegexp(only: Boolean = true, vararg regexp: String): FilterSorter {
            return { tables ->
                val regexSlots = LinkedHashMap<Regex, ArrayList<String>>(regexp.size)
                for (it in regexp) {
                    when {
                        it.startsWith(prefix) -> regexSlots[it.toRegex(RegexOption.LITERAL)] = arrayListOf(it.trim())
                        else -> regexSlots[it.toRegex(RegexOption.IGNORE_CASE)] = arrayListOf()
                    }
                }
                val temp = LinkedList(tables)
                val iter = temp.iterator()
                while (iter.hasNext()) {
                    val t = iter.next()
                    for ((r, v) in regexSlots) {
                        if (r.matches(t)) {
                            v.add(t)
                            iter.remove()
                            break
                        }
                    }
                }

                val grpd = regexSlots.values.map { it.sorted() }.flatten()
                if (!only && temp.isNotEmpty()) {
                    ArrayList<String>(grpd.size + temp.size + 1).apply {
                        addAll(grpd)
                        add(prefix)
                        addAll(temp.sorted())
                    }
                } else {
                    grpd
                }
            }
        }

        @JvmField
        val excludeShadow: FilterSorter = excludeRegexp(""".*_\d+$""", """.*\$\w+$""")
    }

    enum class SqlType {
        DdlTable,
        DdlTrigger,
        DmlInsert,
        SqlUnknown,
        StrComment,
    }

    data class SqlString(
            val table: String,
            val sqlType: SqlType,
            val sqlText: String
    )

    fun saveFile(path: String, sqls: List<SqlString>) = saveFile(File(path), sqls)

    fun saveFile(file: File, sqls: List<SqlString>) {
        file.parentFile.mkdirs()
        file.bufferedWriter().use { buf ->
            val trgs = ArrayList<SqlString>()
            for (sql in sqls) {
                when (sql.sqlType) {
                    SqlType.StrComment -> {
                        buf.write(sql.sqlText)
                        buf.write("\n\n")
                    }
                    SqlType.DdlTrigger -> {
                        trgs.add(sql)
                    }
                    else -> {
                        buf.write("$prefix ${sql.table} ${sql.sqlType}")
                        buf.write("\n")
                        buf.write("${sql.sqlText};")
                        buf.write("\n\n")
                    }
                }
            }

            if (trgs.isNotEmpty()) {
                buf.write("$prefix TRIGGER")
                buf.write("\n\n")
                buf.write("DELIMITER \$\$")
                buf.write("\n\n")
                for (sql in trgs) {
                    buf.write("$prefix ${sql.table} ${sql.sqlType}\n${sql.sqlText} \$\$\n\n")
                }
                buf.write("DELIMITER ;\n")
            }
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


        val result = ArrayList<SqlString>()
        appendRevision(result, SimpleJdbcTemplate(database))
        val ddlTableReg = """CREATE\s+TABLE\s+""".toRegex(RegexOption.IGNORE_CASE)
        val ddlTriggerReg = """CREATE\s+TRIGGER\s+""".toRegex(RegexOption.IGNORE_CASE)

        val tables = schemaDefinitionLoader.showTables(database)
        for (table in filterSorter(tables)) {
            if (table.startsWith(prefix)) {
                log.info("[dumpDdl] insert comment, {}", table)
                result.add(SqlString(table, SqlType.StrComment, table.trim()))
                continue
            }

            log.info("[dumpDdl] dump ddls for table={}", table)
            val ddls = schemaDefinitionLoader.showFullDdl(database, table)
            for (ddl in ddls) {
                val sql = ddl.trim()
                when {
                    ddlTableReg.containsMatchIn(sql) -> {
                        result.add(SqlString(table, SqlType.DdlTable, sql))
                    }
                    ddlTriggerReg.containsMatchIn(sql) -> {
                        result.add(SqlString(table, SqlType.DdlTrigger, sql))
                    }
                    else -> {
                        result.add(SqlString(table, SqlType.SqlUnknown, sql))
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
        appendRevision(result, tmpl)

        val tables = schemaDefinitionLoader.showTables(database)
        val builder = StringBuilder()
        for (table in filterSorter(tables)) {
            if (table.startsWith(prefix)) {
                log.info("[dumpRec] insert comment, {}", table)
                result.add(SqlString(table, SqlType.StrComment, table.trim()))
                continue
            }

            log.info("[dumpRec] dump record for table={}", table)
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

            if (builder.length > 2) {
                result.add(SqlString(table, SqlType.DmlInsert, builder.substring(0, builder.length - 2)))
                builder.clear()
            }
        }

        return result
    }

    private fun appendRevision(result: ArrayList<SqlString>, tmpl: SimpleJdbcTemplate) = try {
        tmpl.query("SELECT revision, apply_dt FROM sys_schema_version WHERE apply_dt >'1000-01-01' order by revision desc limit 1") {
            val sb = StringBuilder()
            sb.append(prefix)
            sb.append(" revision=")
            sb.append(it.getString("revision"))
            sb.append(", apply_dt=")
            sb.append(it.getString("apply_dt"))
            result.add(SqlString(Null.Str, SqlType.StrComment, sb.toString()))
        }
    } catch (e: Exception) {
        log.warn("[getRevision] failed to revision", e)
    }
}
