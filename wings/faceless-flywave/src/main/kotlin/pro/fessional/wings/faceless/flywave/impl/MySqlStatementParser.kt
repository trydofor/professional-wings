package pro.fessional.wings.faceless.flywave.impl

import org.slf4j.LoggerFactory
import pro.fessional.mirana.bits.Bytes
import pro.fessional.mirana.data.Null
import pro.fessional.mirana.time.DateFormatter
import pro.fessional.wings.faceless.flywave.SqlStatementParser
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Optional
import java.util.function.Function
import java.util.regex.Pattern

/**
 * @author trydofor
 * @since 2019-06-11
 */
class MySqlStatementParser : SqlStatementParser {

    private val log = LoggerFactory.getLogger(MySqlStatementParser::class.java)

    private val options = Pattern.MULTILINE or Pattern.CASE_INSENSITIVE
    private val ddlRenameTable = "^ALTER\\s+TABLE\\s+([^(\\s]+)\\s+RENAME\\s+TO\\s+([^(\\s]+)".toPattern(options)
    private val ddlAlterTable = "^ALTER\\s+TABLE\\s+([^(\\s]+)".toPattern(options)
    private val ddlCreateIndex = "^CREATE\\s+(?:UNIQUE\\s+|FULLTEXT\\s+|SPATIAL\\s+)?INDEX\\s+\\S+\\s+(?:\\S+\\s+)?ON\\s+([^(\\s]+)".toPattern(options)
    private val ddlCreateTable = "^CREATE\\s+(?:TEMPORARY\\s+)?TABLE\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?([^(\\s]+)".toPattern(options)
    private val ddlCreateTrigger = "^CREATE\\s+(?:DEFINER\\s*=\\s*\\S+\\s+)?TRIGGER\\s+(?:\\S+\\s+)*ON\\s+([^(\\s]+)".toPattern(options)
    private val ddlDropIndex = "^DROP\\s+INDEX\\s+\\S+\\s+ON\\s+([^(\\s]+)".toPattern(options)
    private val ddlDropTable = "^DROP\\s+(?:TEMPORARY\\s+)?TABLE\\s+(?:IF\\s+EXISTS\\s+)?([^(\\s]+)".toPattern(options)
    private val ddlDropTrigger = "^DROP\\s+TRIGGER\\s+(?:IF\\s+EXISTS\\s+)?\\S+\\s".toPattern(options) // without table
    private val ddlTruncateTable = "^TRUNCATE\\s+(?:TABLE\\s+)?([^(\\s]+)".toPattern(options)

    private val dmlDelete = "^DELETE\\s+(?:\\S+\\s)*FROM\\s+([^(\\s]+)".toPattern(options)
    private val dmlInsert = "^INSERT\\s+(?:LOW_PRIORITY\\s+|DELAYED\\s+|HIGH_PRIORITY\\s+)?(?:IGNORE\\s+)?(?:INTO\\s+)?([^(\\s]+)".toPattern(options)
    private val dmlReplace = "^REPLACE\\s+(?:LOW_PRIORITY|DELAYED)?\\s*(?:INTO\\s+)?([^(\\s]+)".toPattern(options)
    private val dmlUpdate = "^UPDATE\\s+(?:LOW_PRIORITY\\s+)?(?:IGNORE\\s+)?([^(\\s]+)".toPattern(options)

    private val plainRename = linkedSetOf(
        ddlRenameTable
    )

    private val plainRegex = linkedSetOf(
        ddlAlterTable.toFunction(),
        ddlCreateIndex.toFunction(),
        ddlCreateTable.toFunction(),
        ddlCreateTrigger.toFunction(),
        ddlDropIndex.toFunction(),
        ddlDropTable.toFunction(),
        ddlDropTrigger.toFunction(),
        ddlTruncateTable.toFunction()
    )

    private val shardRegex = linkedSetOf(
        dmlDelete.toFunction(),
        dmlInsert.toFunction(),
        dmlReplace.toFunction(),
        dmlUpdate.toFunction()
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
                    Optional.of(Null.Str)
                }
            } else {
                return@Function Optional.empty<String>()
            }
        }
    }

    override fun parseTypeAndTable(sql: String): SqlStatementParser.SqlType {
        if(sql.startsWith("SELECT ",true)) {
            return SqlStatementParser.SqlType.Other
        }

        for (ptn in plainRename) {
            val m = ptn.matcher(sql)
            if (m.find()) {
                val tbl = trimName(m.group(1))
                val ren = trimName(m.group(2))
                return SqlStatementParser.SqlType.Plain(tbl, ren)
            }
        }
        for (fnc in plainRegex) {
            val m = fnc.apply(sql)
            if (m.isPresent) {
                return SqlStatementParser.SqlType.Plain(m.get())
            }
        }
        for (fnc in shardRegex) {
            val m = fnc.apply(sql).orElse(Null.Str)
            if (m.isNotEmpty()) {
                return SqlStatementParser.SqlType.Shard(m)
            }
        }

        // plain B, never here.
        log.warn("unmatched sql type, return Other. sql=[$sql]")
        return SqlStatementParser.SqlType.Other
    }

    override fun safeName(str: String): String {
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

    // https://dev.mysql.com/doc/refman/8.0/en/string-literals.html#character-escape-sequences
    override fun safeValue(obj: Any?): String {

        when (obj) {
            null -> return "NULL"
            is Boolean -> return if (obj) "1" else "0"
            is Number -> return obj.toString()
            is LocalDate -> return "'${DateFormatter.date10(obj)}'"
            is LocalDateTime -> return "'${DateFormatter.full23(obj)}'"
            is LocalTime -> return "'${DateFormatter.time12(obj)}'"
            is java.sql.Time -> return "'$obj'"
            is java.sql.Date -> return "'$obj'"
            is java.sql.Timestamp -> return "'${DateFormatter.full23(obj.toLocalDateTime())}'"
            is java.util.Date -> return "'${DateFormatter.full23(obj)}'"
            is ByteArray -> return "0x${Bytes.hex(obj)}"
            is java.sql.Blob -> return "0x${Bytes.hex(obj.getBytes(0, obj.length().toInt()))}"
        }

        val str = if (obj is java.sql.Clob) {
            obj.characterStream.readText()
        } else {
            obj.toString()
        }

        /**
        \0	An ASCII NUL (X'00') character
        \'	A single quote (') character
        \"	A double quote (") character
        \b	A backspace character
        \n	A newline (linefeed) character
        \r	A carriage return character
        \t	A tab character
        \Z	ASCII 26 (Control+Z); see note following the table
        \\	A backslash (\) character
        \%	A % character; see note following the table
        \_	A _ character; see note following the table
         */
        val sb = StringBuilder(str.length + 10)
        sb.append('\'')
        val c00 = 0x00.toChar()
        val c26 = 0x26.toChar()
        for (c in str) {
            when (c) {
                c00 -> sb.append("\\0")
                '\'' -> sb.append("\\'")
                '"' -> sb.append("\\\"")
                '\b' -> sb.append("\\b")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                c26 -> sb.append("\\Z")
                '\\' -> sb.append("\\\\")
                else -> sb.append(c)
            }
        }
        sb.append('\'')
        return sb.toString()
    }

    override fun trimName(str: String) = if (str.contains('`')) {
        str.replace("`", "")
    } else {
        str
    }
}
