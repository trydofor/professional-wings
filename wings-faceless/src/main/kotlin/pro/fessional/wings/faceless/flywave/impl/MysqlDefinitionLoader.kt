package pro.fessional.wings.faceless.flywave.impl

import pro.fessional.wings.faceless.flywave.SchemaDefinitionLoader
import pro.fessional.wings.faceless.flywave.SchemaDefinitionLoader.Companion.TYPE_IDX
import pro.fessional.wings.faceless.flywave.SchemaDefinitionLoader.Companion.TYPE_TBL
import pro.fessional.wings.faceless.flywave.SchemaDefinitionLoader.Companion.TYPE_TRG
import pro.fessional.wings.faceless.flywave.util.SimpleJdbcTemplate
import java.lang.StringBuilder
import java.util.LinkedList
import javax.sql.DataSource

/**
 * @author trydofor
 * @since 2019-06-13
 */
class MysqlDefinitionLoader : SchemaDefinitionLoader {

    override fun showTables(dataSource: DataSource): List<String> {
        val list = LinkedList<String>()
        SimpleJdbcTemplate(dataSource).query("SHOW TABLES") {
            list.add(it.getString(1))
        }
        return list
    }

    override fun showFullDdl(dataSource: DataSource, table: String): List<String> {
        val rst = LinkedList<String>()
        val tmpl = SimpleJdbcTemplate(dataSource)
        // Table , Create Table
        tmpl.query("SHOW CREATE TABLE $table") {
            rst.add(it.getString(2)) // Create Table
        }

        // triggers
        tmpl.query("""
            SELECT
                TRIGGER_NAME,
                ACTION_TIMING,
                EVENT_MANIPULATION,
                ACTION_STATEMENT
            FROM
                INFORMATION_SCHEMA.TRIGGERS
            WHERE
                EVENT_OBJECT_SCHEMA = database()
                AND EVENT_OBJECT_TABLE=?
        """, table) {
            val n = it.getString("TRIGGER_NAME")
            val t = it.getString("ACTION_TIMING")
            val e = it.getString("EVENT_MANIPULATION")
            val s = it.getString("ACTION_STATEMENT")
            rst.add("""
                |CREATE TRIGGER `$n` $t $e ON `$table`
                |FOR EACH ROW $s
            """.trimMargin())
        }

        return rst
    }

    override fun diffAllSame(dataSource: DataSource, table: String, other: String, types: Int): String {

        val diff = StringBuilder()
        val tmpl = SimpleJdbcTemplate(dataSource)

        // 对比列
        if (types and TYPE_TBL != 0) {
            tmpl.query("""
            SELECT
                COLUMN_NAME, COLUMN_TYPE, COLUMN_COMMENT, ORDINAL_POSITION, IS_NULLABLE, COLUMN_DEFAULT,
                GROUP_CONCAT(TABLE_NAME) as TBL_NAME,
                COUNT(1) SAME_COUNT
            FROM
                INFORMATION_SCHEMA.COLUMNS
            WHERE
                TABLE_SCHEMA = database()
                AND TABLE_NAME IN (?, ?)
            GROUP BY COLUMN_NAME, COLUMN_TYPE, COLUMN_COMMENT, ORDINAL_POSITION, IS_NULLABLE, COLUMN_DEFAULT
            HAVING SAME_COUNT != 2
            """, table, other) {
                diff.append("\nCOL@")
                diff.append(it.getString("TBL_NAME")).append(".")
                diff.append(it.getString("COLUMN_NAME")).append("=")
                diff.append(it.getString("COLUMN_TYPE")).append(",")
                diff.append(it.getString("COLUMN_COMMENT")).append(",")
                diff.append(it.getString("ORDINAL_POSITION")).append(",")
                var nullable = it.getString("IS_NULLABLE")
                if (it.wasNull()) nullable = "<NULL>"
                diff.append(nullable).append(",")
                diff.append(it.getString("COLUMN_DEFAULT"))
            }
        }

        // 对比索引
        if (types and TYPE_IDX != 0) {
            tmpl.query("""
            SELECT
                INDEX_NAME, NON_UNIQUE, SEQ_IN_INDEX, COLUMN_NAME, INDEX_TYPE,
                GROUP_CONCAT(TABLE_NAME) as TBL_NAME,
                COUNT(1) SAME_COUNT
            FROM
                INFORMATION_SCHEMA.STATISTICS
            WHERE
                TABLE_SCHEMA = database()
                AND TABLE_NAME IN (?, ?)
            GROUP BY INDEX_NAME, NON_UNIQUE, SEQ_IN_INDEX, COLUMN_NAME, INDEX_TYPE
            HAVING SAME_COUNT != 2
            """, table, other) {
                diff.append("\nIDX@")
                diff.append(it.getString("TBL_NAME")).append(".")
                diff.append(it.getString("INDEX_NAME")).append("=")
                diff.append(it.getString("NON_UNIQUE")).append(",")
                diff.append(it.getString("SEQ_IN_INDEX")).append(",")
                diff.append(it.getString("COLUMN_NAME")).append(",")
                diff.append(it.getString("INDEX_TYPE"))
            }
        }

        // 对比触发器
        if (types and TYPE_TRG != 0) {
            tmpl.query("""
            SELECT
                TRIGGER_NAME, ACTION_TIMING, EVENT_MANIPULATION, ACTION_STATEMENT,
                GROUP_CONCAT(EVENT_OBJECT_TABLE) as TBL_NAME,
                COUNT(1) SAME_COUNT
            FROM
                INFORMATION_SCHEMA.TRIGGERS
            WHERE
                EVENT_OBJECT_SCHEMA = database()
                AND EVENT_OBJECT_TABLE IN (?, ?)
            GROUP BY TRIGGER_NAME, ACTION_TIMING, EVENT_MANIPULATION, ACTION_STATEMENT
            HAVING SAME_COUNT != 2
            """, table, other) {
                diff.append("\nTRG@")
                diff.append(it.getString("TBL_NAME")).append(".")
                diff.append(it.getString("TRIGGER_NAME")).append("=")
                diff.append(it.getString("ACTION_TIMING")).append(",")
                diff.append(it.getString("EVENT_MANIPULATION")).append(",")
                diff.append(it.getString("ACTION_STATEMENT"))
            }
        }
        return diff.toString()
    }

    override fun showBoneCol(dataSource: DataSource, table: String): List<String> {
        val rst = LinkedList<String>()
        SimpleJdbcTemplate(dataSource).query("""
        SELECT
            COLUMN_NAME,
            COLUMN_TYPE,
            COLUMN_COMMENT,
            ORDINAL_POSITION
        FROM
            INFORMATION_SCHEMA.COLUMNS
        WHERE
            TABLE_SCHEMA = database()
            AND TABLE_NAME = ?
            ORDER BY ORDINAL_POSITION ASC
        """, table) {
            val n = it.getString("COLUMN_NAME")
            val t = it.getString("COLUMN_TYPE")
            val c = it.getString("COLUMN_COMMENT").replace("'", "\\'")
            rst.add("`$n` $t COMMENT '$c'") // `EVENT_NAME` varchar(100) NOT NULL COMMENT '事件名称'
        }

        return rst
    }

    override fun showPkeyCol(dataSource: DataSource, table: String): List<String> {
        val rst = LinkedList<String>()
        SimpleJdbcTemplate(dataSource).query("""
        SHOW KEYS FROM $table WHERE KEY_NAME = 'PRIMARY'
        """) {
            rst.add(it.getString("COLUMN_NAME"))
        }

        return rst
    }

    override fun showBoneTrg(dataSource: DataSource, table: String): Map<String, String> {
        val rst = HashMap<String, String>()
        SimpleJdbcTemplate(dataSource).query("""
            SELECT
                TRIGGER_NAME,
                ACTION_STATEMENT
            FROM
                INFORMATION_SCHEMA.TRIGGERS
            WHERE
                EVENT_OBJECT_SCHEMA = database()
                AND EVENT_OBJECT_TABLE = ?
            """, table) {
            rst.put(it.getString("TRIGGER_NAME"), it.getString("ACTION_STATEMENT"))
        }

        return rst
    }
}