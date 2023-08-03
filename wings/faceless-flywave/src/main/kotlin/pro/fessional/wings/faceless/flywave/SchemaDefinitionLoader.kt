package pro.fessional.wings.faceless.flywave

import javax.sql.DataSource

/**
 * Provides information about the table structure of the current database. Used for data sharding and triggers.
 *
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
     * list all tables of current datasource
     *
     * @param dataSource current datasource
     */
    fun showTables(dataSource: DataSource): List<String>

    /**
     * List all DDLs that can create the current table with fields, constraints, indexes and triggers.
     * Foreign Key should not be considered, some normalization is no longer good in modern software development.
     *
     * @param dataSource current datasource
     * @param table the plain table name, do NOT include any quote.
     */
    fun showFullDdl(dataSource: DataSource, table: String): List<String>

    /**
     * Whether two tables have the same skeleton, including field (NAME, TYPE, COMMENT, POSITION), index and trigger.
     *
     * @param dataSource current datasource
     * @param table the plain table name, do NOT include any quote.
     * @param other other plain table name, do NOT include any quote.
     * @return diff info, empty mean the same
     */
    fun diffBoneSame(dataSource: DataSource, table: String, other: String, types: Int = TYPE_TBL or TYPE_IDX or TYPE_TRG): String

    /**
     * Whether two tables have the same struct, including field (NAME, TYPE, COMMENT, POSITION, NULLABLE, DEFAULT), index and trigger.
     *
     * @param dataSource current datasource
     * @param table the plain table name, do NOT include any quote.
     * @param other other plain table name, do NOT include any quote.
     * @return diff info, empty mean the same
     */
    fun diffFullSame(dataSource: DataSource, table: String, other: String, types: Int = TYPE_TBL or TYPE_IDX or TYPE_TRG): String

    /**
     * A DDL section that lists at least the `Name`, `Type`, and `Comment` of field in the table.
     * Used to populate the `TABLE_BONE` environment variable. Fields are comma-separated in SQL syntax
     * (no comma at the end of the line)
     *
     * ```sql
     *   `LOGIN_INFO` text COMMENT 'login info',
     *   `OTHER_INFO` text COMMENT 'other info'
     * ```
     *
     * @param dataSource current datasource
     * @param table the plain table name, do NOT include any quote.
     */
    fun showBoneCol(dataSource: DataSource, table: String): List<String>

    /**
     * Get the field name of primary key in the table.
     *
     * @param table the plain table name, do NOT include any quote.
     */
    fun showPkeyCol(dataSource: DataSource, table: String): List<String>

    data class Trg(val name: String, val timing: String, val action: String, val event: String, val table: String)

    /**
     * Get the name and `EVENT` content of trigger in the table.
     *
     * @param table the plain table name, do NOT include any quote.
     */
    fun showBoneTrg(dataSource: DataSource, table: String): List<Trg>

    /**
     * Create trigger DDL via trigger definition
     *
     * @param trg trigger definition
     * @param drop drop or create
     */
    fun makeDdlTrg(trg: Trg, drop: Boolean): String
}
