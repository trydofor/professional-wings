package pro.fessional.wings.faceless.flywave

/**
 * Parsing out the plain table, datasource types and replacing boundaries
 *
 * @author trydofor
 * @since 2019-06-11
 */
interface SqlStatementParser {

    sealed class SqlType {
        data class Plain(val table: String, val rename: String = "") : SqlType()
        data class Shard(val table: String) : SqlType()
        object Other : SqlType()
    }

    /**
     * Parser the type of datasource and plain table name.
     */
    fun parseTypeAndTable(sql: String): SqlType

    /**
     * For non-standard names (e.g. non-ASCII, keyword), escape them to safe word.
     *
     * @param str name
     */
    fun safeName(str: String): String

    /**
     * Convert the value to the sql literal form
     * @param obj value
     */
    fun safeValue(obj: Any?): String

    /**
     * trim the name to plain style, e.g. remove the escape character, white char.
     * @param str name
     */
    fun trimName(str: String): String
}
