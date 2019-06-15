package pro.fessional.wings.oracle.flywave.impl

import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration
import org.apache.shardingsphere.core.constant.DatabaseType
import org.apache.shardingsphere.core.constant.SQLType
import org.apache.shardingsphere.core.metadata.table.ShardingTableMetaData
import org.apache.shardingsphere.core.parse.antlr.AntlrParsingEngine
import org.apache.shardingsphere.core.rule.ShardingRule
import org.slf4j.LoggerFactory
import pro.fessional.wings.oracle.flywave.SqlStatementParser
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.min

/**
 * @author trydofor
 * @since 2019-06-11
 */
class MySqlStatementParser : SqlStatementParser {

    private val logger = LoggerFactory.getLogger(MySqlStatementParser::class.java)

    private val options = Pattern.MULTILINE or Pattern.CASE_INSENSITIVE
    private val ddlAlterTable = "^ALTER\\s+TABLE\\s+([^(\\s]+)".toPattern(options)
    private val ddlCreateIndex = "^CREATE\\s+(?:UNIQUE|FULLTEXT|SPATIAL)?\\s*INDEX\\s+\\S+\\s+(?:\\S+\\s+)?ON\\s+([^(\\s]+)".toPattern(options)
    private val ddlCreateTable = "^CREATE\\s+(?:TEMPORARY\\s+)?TABLE\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?([^(\\s]+)".toPattern(options)
    private val ddlCreateTrigger = "^CREATE\\s+(?:DEFINER\\s*=\\s*\\S+\\s+)?TRIGGER\\s+(?:\\S+\\s+)*ON\\s+([^(\\s]+)".toPattern(options)
    private val ddlDropIndex = "^DROP\\s+INDEX\\s+\\S+\\s+ON\\s+([^(\\s]+)".toPattern(options)
    private val ddlDropTable = "^DROP\\s+(?:TEMPORARY\\s+)?TABLE\\s+(?:IF\\s+EXISTS\\s+)?([^(\\s]+)".toPattern(options)
    private val ddlDropTrigger = "^DROP\\s+TRIGGER\\s+(?:IF\\s+EXISTS\\s+)?\\S+\\s".toPattern(options) // 没有table
    private val ddlTruncateTable = "^TRUNCATE\\s+(?:TABLE\\s+)?([^(\\s]+)".toPattern(options)

    private val dmlDelete = "^DELETE\\s+(?:\\S+\\s)*FROM\\s+([^(\\s]+)".toPattern(options)
    private val dmlInsert = "^INSERT\\s+(?:LOW_PRIORITY|DELAYED|HIGH_PRIORITY)?\\s*(?:IGNORE)?\\s*(?:INTO)?\\s*([^(\\s]+)".toPattern(options)
    private val dmlReplace = "^REPLACE\\s+(?:LOW_PRIORITY|DELAYED)?\\s*(?:INTO)?\\s*([^(\\s]+)".toPattern(options)
    private val dmlUpdate = "^UPDATE\\s+(?:LOW_PRIORITY)?\\s*(?:IGNORE)\\s*([^(\\s]+)".toPattern(options)

    private val plainRegex = setOf(
            ddlAlterTable
            , ddlCreateIndex
            , ddlCreateTable
            , ddlCreateTrigger
            , ddlDropIndex
            , ddlDropTable
            , ddlDropTrigger
            , ddlTruncateTable)

    private val shardRegex = setOf(
            dmlDelete
            , dmlInsert
            , dmlReplace
            , dmlUpdate)

    private val emptyRule = ShardingRule(ShardingRuleConfiguration(), listOf("empty-data-source"))
    private val emptyMeta = ShardingTableMetaData(emptyMap())

    override fun parseTypeAndTable(sql: String): SqlStatementParser.SqlType {
        for (ddl in plainRegex) {
            val m = ddl.matcher(sql)
            if (m.find()) {
                return if (m.groupCount() > 0) {
                    val table = trimTableName(m.group(1))
                    SqlStatementParser.SqlType.Plain(table)
                } else {
                    SqlStatementParser.SqlType.Plain("")
                }
            }
        }
        for (ddl in shardRegex) {
            val m = ddl.matcher(sql)
            if (m.find() && m.groupCount() > 0) {
                val table = trimTableName(m.group(1))
                return SqlStatementParser.SqlType.Shard(table)
            }
        }

        try {
            val parser = AntlrParsingEngine(DatabaseType.MySQL, sql, emptyRule, emptyMeta)
            val rst = parser.parse()
            val table = trimTableName(rst.tables.singleTableName)
            val type = rst.type
            return if (type == SQLType.DDL || type == SQLType.DAL || type == SQLType.DCL) {
                SqlStatementParser.SqlType.Plain(table)
            } else {
                SqlStatementParser.SqlType.Shard(table)
            }
        } catch (e: RuntimeException) {
            logger.warn("failed to use AntlrParsingEngine, sql=$sql", e)
        }

        return SqlStatementParser.SqlType.Other
    }

    private val emptyArray = IntArray(0)
    override fun parseTableReplace(tbl: String, sql: String): IntArray {
        if (tbl.isBlank() || sql.isBlank()) {
            return emptyArray
        }

        val mask = maskQuote(sql)
        val name = trimTableName(tbl)
        val idx = ArrayList<Int>()
        val len = name.length
        var off = 0
        while (true) {
            val i = mask.indexOf(name, off, true)
            if (i < 0) {
                break
            } else {
                val end = i + len
                val bgn = (i == 0 || (i > 0 && isBoundary(sql, i - 1)))
                if (bgn && isBoundary(sql, end)) {
                    idx.add(i)
                    idx.add(end)
                }
                off = end
            }
        }
        return idx.toIntArray()
    }

    private fun trimTableName(tbl: String?): String {
        if (tbl == null) return ""
        val pos1 = tbl.indexOf('`')
        val pos2 = tbl.lastIndexOf('`')
        if (pos1 < 0 && pos2 < 0) {
            return tbl.trim()
        }
        return tbl.replace("`", "").trim()
    }

    private fun maskQuote(sql: String): String {
        var off = 0
        val chs = sql.toCharArray()
        while (true) {
            val idx1 = sql.indexOf('\'', off)
            val idx2 = sql.indexOf('"', off)
            if (idx1 < 0 && idx2 < 0) {
                return if (off == 0) {
                    sql
                } else {
                    String(chs)
                }
            }

            val idx = if (idx1 < 0 || idx2 < 0) {
                max(idx1, idx2)
            } else {
                min(idx1, idx2)
            }

            val end = findQuoteEnd(sql, idx)
            if (end > idx) {
                off = end + 1
                chs.fill(' ', idx, off)
            } else {
                off = idx + 1
            }
        }
    }

    private fun findQuoteEnd(sql: String, idx: Int): Int {
        if (idx < 0 || idx >= sql.length - 1) return -1
        val chr = sql[idx]
        var off = idx + 1
        while (true) {
            val ix = sql.indexOf(chr, off)
            if (ix < 0) return -1
            var cnt = 0
            var qtc = ix - 1
            while (qtc > 0) {
                val c = sql[qtc]
                if (c == '\\') {
                    cnt++
                    qtc--
                } else {
                    break
                }
            }
            if (cnt % 2 == 0) {
                return ix
            }
            off = ix + 1
        }
    }

    private fun isBoundary(sql: String, idx: Int): Boolean {
        if (idx <= 0 || idx >= sql.length - 1) return true

        val c = sql[idx]
        return when {
            c in 'A'..'Z' -> false
            c in 'a'..'z' -> false
            c in '0'..'9' -> false
            c == '_' -> false

            // 非ascii命名
            c.toInt() > 127 && idx > 0 && sql[idx - 1].toInt() > 127 -> false
            else -> true
        }

    }
}