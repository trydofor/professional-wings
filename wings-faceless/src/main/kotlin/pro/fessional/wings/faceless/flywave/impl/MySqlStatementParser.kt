package pro.fessional.wings.faceless.flywave.impl

import org.slf4j.LoggerFactory
import pro.fessional.wings.faceless.flywave.SqlStatementParser
import java.util.Optional
import java.util.function.Function
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

    private val plainRegex = linkedSetOf(
            ddlAlterTable.toFunction()
            , ddlCreateIndex.toFunction()
            , ddlCreateTable.toFunction()
            , ddlCreateTrigger.toFunction()
            , ddlDropIndex.toFunction()
            , ddlDropTable.toFunction()
            , ddlDropTrigger.toFunction()
            , ddlTruncateTable.toFunction()
    )

    private val shardRegex = linkedSetOf(
            dmlDelete.toFunction()
            , dmlInsert.toFunction()
            , dmlReplace.toFunction()
            , dmlUpdate.toFunction()
    )

    /**
     * append shard sql pattern
     */
    fun addShard(pattern: Pattern) = shardRegex.add(pattern.toFunction())

    /**
     * append plain sql pattern
     */
    fun addPlain(pattern: Pattern) = plainRegex.add(pattern.toFunction())

    /**
     * append shard sql function
     */
    fun addShard(function: Function<String, Optional<String>>) = shardRegex.add(function)

    /**
     * append plain sql function
     */
    fun addPlain(function: Function<String, Optional<String>>) = plainRegex.add(function)

    private fun Pattern.toFunction(): Function<String, Optional<String>> {
        return Function { sql ->
            val m = this.matcher(sql)
            if (m.find()) {
                return@Function if (m.groupCount() > 0) {
                    Optional.of(trimName(m.group(1)))
                } else {
                    Optional.of("")
                }
            } else {
                return@Function Optional.empty<String>()
            }
        }
    }

    override fun parseTypeAndTable(sql: String): SqlStatementParser.SqlType {
        for (fnc in plainRegex) {
            val m = fnc.apply(sql)
            if (m.isPresent) {
                return SqlStatementParser.SqlType.Plain(m.get())
            }
        }
        for (fnc in shardRegex) {
            val m = fnc.apply(sql).orElse("")
            if (m.isNotEmpty()) {
                return SqlStatementParser.SqlType.Shard(m)
            }
        }

        // 备用方案，一般不会到达。

        logger.warn("unmatched sql type, return Other. sql=$sql")
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