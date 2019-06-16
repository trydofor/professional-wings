package pro.fessional.wings.faceless.flywave.impl

import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration
import org.apache.shardingsphere.core.constant.DatabaseType
import org.apache.shardingsphere.core.constant.SQLType
import org.apache.shardingsphere.core.metadata.table.ShardingTableMetaData
import org.apache.shardingsphere.core.parse.antlr.AntlrParsingEngine
import org.apache.shardingsphere.core.rule.ShardingRule
import org.slf4j.LoggerFactory
import pro.fessional.wings.faceless.flywave.SqlStatementParser
import java.util.regex.Pattern

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
                    val table = trimName(m.group(1))
                    SqlStatementParser.SqlType.Plain(table)
                } else {
                    SqlStatementParser.SqlType.Plain("")
                }
            }
        }
        for (ddl in shardRegex) {
            val m = ddl.matcher(sql)
            if (m.find() && m.groupCount() > 0) {
                val table = trimName(m.group(1))
                return SqlStatementParser.SqlType.Shard(table)
            }
        }

        try {
            val parser = AntlrParsingEngine(DatabaseType.MySQL, sql, emptyRule, emptyMeta)
            val rst = parser.parse()
            val table = trimName(rst.tables.singleTableName)
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

    override fun safeName(str: String): String {
        val hasBad = str.any {
            when (it) {
                in '0'..'9' -> false
                in 'a'..'z' -> false
                in 'A'..'Z' -> false
                '$', '_' -> false
                else -> true
            }
        }

        if (!hasBad) {
            return str
        }

        val i1 = str.indexOf('`')
        if (i1 >= 0) {
            val i2 = str.lastIndexOf('`')
            return if (i1 == 0 && i2 == str.length - 1) {
                str
            } else {
                "`${str.replaceAfter("`", "")}`"
            }
        }
        return "`$str`"
    }

    override fun trimName(str: String) = if (str.contains('`')) {
        str.replace("`", "")
    } else {
        str
    }

}