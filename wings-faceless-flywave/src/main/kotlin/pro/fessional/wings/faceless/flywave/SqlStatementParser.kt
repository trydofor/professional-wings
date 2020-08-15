package pro.fessional.wings.faceless.flywave

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
     * 对非标准名字（非ASCII命名），进行转义
     * @param str 名字
     */
    fun safeName(str: String): String

    /**
     * 对sql类型，变成sql字面量
     * @param obj 值
     */
    fun safeValue(obj: Any?): String

    /**
     * 去掉转义字符，还原本名
     * @param str 名字
     */
    fun trimName(str: String): String
}