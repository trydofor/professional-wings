package pro.fessional.wings.oracle.flywave

/**
 * 解析出主表和数据源类型，替换边界
 * @author trydofor
 * @since 2019-06-11
 */
interface SqlStatementParser {

    sealed class SqlType {
        data class Plain(val table: String) : SqlType()
        data class Shard(val table: String) : SqlType()
        object Other : SqlType()
    }

    /**
     * 解析出数据源类型和主表名称
     */
    fun parseTypeAndTable(sql: String): SqlType

    /**
     * 找到主表名称替换的边界索引
     */
    fun parseTableReplace(tbl: String, sql: String): IntArray
}