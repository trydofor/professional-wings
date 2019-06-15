package pro.fessional.wings.oracle.flywave

import javax.sql.DataSource

/**
 * 提供当前数据库的结构信息。
 * 用来做分表和触发器
 * @author trydofor
 * @since 2019-06-12
 */
interface SchemaDefinitionLoader {

    /**
     * 列出当前datasource中的所有表
     * @param dataSource 当前数据源
     */
    fun showTables(dataSource: DataSource): List<String>

    /**
     * 列出，可以创建当前表（字段，约束，索引，触发器）的所有DDL
     * 不应该不考虑外键约束，范式已经符合现代软件开发节奏。
     * @param dataSource 当前数据源
     * @param table 目标表名
     */
    fun showFullDdl(dataSource: DataSource, table: String): List<String>

    /**
     * 只列出当前表的`字段名`，`类型`，`注释`三项的DDL部分
     * 用来填充 `TABLE_BODY` 环境变量。各字段逗号分隔（末行无逗号）
     * ``` sql
     *   `LOGIN_INFO` text COMMENT 'login info',
     *   `OTHER_INFO` text COMMENT 'other info'
     * ```
     * @param dataSource 当前数据源
     * @param table 目标表名
     */
    fun showBodyDdl(dataSource: DataSource, table: String): String
}