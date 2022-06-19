package pro.fessional.wings.faceless.flywave

import javax.sql.DataSource

/**
 * 提供当前数据库的结构信息。
 * 用来做分表和触发器
 * @author trydofor
 * @since 2019-06-12
 */
interface SchemaDefinitionLoader {

    companion object {
        const val TYPE_TBL = 1
        const val TYPE_IDX = 2
        const val TYPE_TRG = 4
    }

    /**
     * 列出当前datasource中的所有表
     * @param dataSource 当前数据源
     */
    fun showTables(dataSource: DataSource): List<String>

    /**
     * 列出，可以创建当前表（字段，约束，索引，触发器）的所有DDL
     * 不应该不考虑外键约束，范式已经符合现代软件开发节奏。
     * @param dataSource 当前数据源
     * @param table 目标表名，不要包含`` ` ``
     */
    fun showFullDdl(dataSource: DataSource, table: String): List<String>

    /**
     * 检测两个表的是否骨架相同，包括字段，索引，触发器
     * @param dataSource 当前数据源
     * @param table 目标表名，不要包含`` ` ``
     * @param other 其他表名，不要包含`` ` ``
     * @return 不一致的信息，空表示完全一致
     */
    fun diffBoneSame(dataSource: DataSource, table: String, other: String, types: Int = TYPE_TBL or TYPE_IDX or TYPE_TRG): String

    /**
     * 检测两个表的是否完全相同，包括字段，索引，触发器
     * @param dataSource 当前数据源
     * @param table 目标表名，不要包含`` ` ``
     * @param other 其他表名，不要包含`` ` ``
     * @return 不一致的信息，空表示完全一致
     */
    fun diffFullSame(dataSource: DataSource, table: String, other: String, types: Int = TYPE_TBL or TYPE_IDX or TYPE_TRG): String

    /**
     * 至少列出当前表的`字段名`，`类型`，`注释`三项的DDL部分
     * 用来填充 `TABLE_BONE` 环境变量。各字段逗号分隔后，符合SQL语法（末行无逗号）
     * ```sql
     *   `LOGIN_INFO` text COMMENT 'login info',
     *   `OTHER_INFO` text COMMENT 'other info'
     * ```
     * @param dataSource 当前数据源
     * @param table 目标表名。
     */
    fun showBoneCol(dataSource: DataSource, table: String): List<String>

    /**
     * 获得当前表的主键字段名
     * @param table 目标表名。
     */
    fun showPkeyCol(dataSource: DataSource, table: String): List<String>

    data class Trg(val name: String, val timing: String, val action: String, val event: String, val table: String)

    /**
     * 获得当前表触发器的名字和`EVENT`内容
     * @param table 目标表名。
     */
    fun showBoneTrg(dataSource: DataSource, table: String): List<Trg>

    /**
     * 根据 Trg定义，创建trigger ddl
     * @param trg 定义
     * @param drop 是drop还是create
     */
    fun makeDdlTrg(trg: Trg, drop: Boolean): String
}
