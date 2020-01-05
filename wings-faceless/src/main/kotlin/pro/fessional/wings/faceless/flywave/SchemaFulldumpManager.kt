package pro.fessional.wings.faceless.flywave

import org.slf4j.LoggerFactory
import pro.fessional.mirana.best.ArgsAssert
import pro.fessional.mirana.time.DateFormatter
import pro.fessional.wings.faceless.flywave.util.SimpleJdbcTemplate
import java.io.File
import java.time.LocalDateTime
import javax.sql.DataSource

/**
 * 进行表结构dump
 *
 * @author trydofor
 * @since 2019-12-15
 */
class SchemaFulldumpManager(
        private val sqlStatementParser: SqlStatementParser,
        private val schemaDefinitionLoader: SchemaDefinitionLoader
) {
    private val logger = LoggerFactory.getLogger(SchemaFulldumpManager::class.java)


    /**
     * dump 指定数据库的DDL (table,index和trigger)
     * 会在fold下生成 schema.sql, argddl.txt
     *
     * @param fold 制定目录
     * @param database 数据库
     * @param exclude 不包括的表，正则表达式
     */
    fun dumpDdl(fold: File, database: DataSource, vararg exclude: String = arrayOf(".*_\\d+$", ".*\\$\\w+$")) {
        ArgsAssert.isTrue(fold.isDirectory, "fold must be a directory, %s", fold)

        val argddl = StringBuilder("-- ")
        argddl.append(DateFormatter.full19(LocalDateTime.now()))
        argddl.append("\n-- exclude")
        for (s in exclude) {
            argddl.append("\n").append(s)
        }
        argddl.append("\n\n-- tables")
        val schema = StringBuilder()

        val excludeRegex = exclude.map { it.toRegex(RegexOption.IGNORE_CASE) }
        val tables = schemaDefinitionLoader.showTables(database).sorted()
        for (table in tables) {

            if (excludeRegex.find { it.matches(table) } != null) {
                logger.info("[dumpDdl] skip the excluded ddl table={}", table)
                continue
            }

            logger.info("[dumpDdl] dump ddls for table={}", table)
            argddl.append("\n").append(table)
            val ddls = schemaDefinitionLoader.showFullDdl(database, table)
            ddls.joinTo(schema, ";\n\n")
            schema.append(";\n\n")
        }

        File(fold, "schema.sql").writeText(schema.toString())
        File(fold, "argddl.txt").writeText(argddl.toString())
    }

    /**
     * dump指定数据库的数据，INSERT语句
     * 会在fold下生成 record.sql, argrec.txt
     *
     * @param fold 制定目录
     * @param database 数据库
     * @param exclude 包括的表，正则表达式
     */
    fun dumpRec(fold: File, database: DataSource, vararg include: String) {
        ArgsAssert.isTrue(fold.isDirectory, "fold must be a directory, %s", fold)

        val argrec = StringBuilder("-- ")
        argrec.append(DateFormatter.full19(LocalDateTime.now()))
        argrec.append("\n-- include")
        for (s in include) {
            argrec.append("\n").append(s)
        }
        argrec.append("\n\n-- tables")

        val recode = StringBuilder()
        val tmpl = SimpleJdbcTemplate(database)
        val includeRegex = include.map { it.toRegex(RegexOption.IGNORE_CASE) }

        val tables = schemaDefinitionLoader.showTables(database).sorted()
        for (table in tables) {

            if (includeRegex.find { it.matches(table) } == null) {
                logger.info("[dumpRec] skip the not include rec table={}", table)
                continue
            }

            logger.info("[dumpRec] dump record for table={}", table)
            argrec.append("\n").append(table)
            recode.append("\n")

            val tbl = sqlStatementParser.safeName(table)
            var ist = true
            tmpl.query("select * from $tbl") { rs ->
                val meta = rs.metaData
                val cnt = meta.columnCount

                if (ist) {
                    recode.append("INSERT INTO ").append(tbl)
                    (1..cnt).map {
                        sqlStatementParser.safeName(meta.getColumnName(it))
                    }.joinTo(recode, ",", "(", ")")
                    recode.append(" VALUES \n")
                    ist = false
                }

                (1..cnt).map {
                    sqlStatementParser.safeValue(rs.getObject(it))
                }.joinTo(recode, ",", "(", ")")
                recode.append(",\n")
            }

            recode.replace(recode.length - 2, recode.length - 1, ";")
        }

        File(fold, "record.sql").writeText(recode.toString())
        File(fold, "argrec.txt").writeText(argrec.toString())
    }
}