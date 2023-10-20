package pro.fessional.wings.faceless.flywave.impl

import pro.fessional.wings.faceless.database.helper.DatabaseChecker
import pro.fessional.wings.faceless.flywave.SchemaDefinitionLoader
import pro.fessional.wings.faceless.flywave.SchemaDefinitionLoader.Companion.TYPE_IDX
import pro.fessional.wings.faceless.flywave.SchemaDefinitionLoader.Companion.TYPE_TBL
import pro.fessional.wings.faceless.flywave.SchemaDefinitionLoader.Companion.TYPE_TRG
import pro.fessional.wings.faceless.flywave.SchemaDefinitionLoader.Trg
import pro.fessional.wings.faceless.flywave.util.SimpleJdbcTemplate
import java.util.LinkedList
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource

/**
 * @author trydofor
 * @since 2019-06-13
 */
class MysqlDefinitionLoader : SchemaDefinitionLoader {

    private val h2database: ConcurrentHashMap<DataSource, Boolean> = ConcurrentHashMap()

    fun isH2Database(dataSource: DataSource) = h2database.computeIfAbsent(dataSource) {
        DatabaseChecker.isH2(dataSource)
    }

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
        if (isH2Database(dataSource)) {
            tmpl.query("SCRIPT NODATA NOPASSWORDS NOSETTINGS TABLE $table") {
                val s = it.getString(1)
                if (s.startsWith("CREATE CACHED TABLE ", ignoreCase = true) ||
                    s.startsWith("CREATE TABLE ", ignoreCase = true) ||
                    s.startsWith("CREATE MEMORY TABLE ", ignoreCase = true)
                ) {
                    rst.add(s) // Create Table
                }
            }
            return rst
        }

        // Table , Create Table
        tmpl.query("SHOW CREATE TABLE $table") {
            rst.add(it.getString(2)) // Create Table
        }

        // triggers
        tmpl.query(
            """
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
        """, table
        ) {
            val n = it.getString("TRIGGER_NAME")
            val t = it.getString("ACTION_TIMING")
            val e = it.getString("EVENT_MANIPULATION")
            val s = it.getString("ACTION_STATEMENT")
            rst.add("CREATE TRIGGER `$n` $t $e ON `$table` FOR EACH ROW $s")
        }

        return rst
    }

    override fun diffBoneSame(dataSource: DataSource, table: String, other: String, types: Int) = diffTable(dataSource, table, other, types, true)
    override fun diffFullSame(dataSource: DataSource, table: String, other: String, types: Int) = diffTable(dataSource, table, other, types, false)

    private fun diffTable(dataSource: DataSource, table: String, other: String, types: Int, bone: Boolean): String {

        val diff = StringBuilder()
        val tmpl = SimpleJdbcTemplate(dataSource)

        if (isH2Database(dataSource)) {
            if (types and TYPE_TBL != 0) {
                val t1 = showBoneCol(dataSource, table).toSet()
                val t2 = showBoneCol(dataSource, other).toSet()

                val s12 = t1 - t2
                if (s12.isNotEmpty()) {
                    diff.append("\nCOL@")
                    diff.append(table).append(".")
                    diff.append(s12.joinToString(","))
                }

                val s21 = t2 - t1
                if (s21.isNotEmpty()) {
                    diff.append("\nCOL@")
                    diff.append(other).append(".")
                    diff.append(s21.joinToString(","))
                }
            }
            return diff.toString()
        }

        // diff column
        if (types and TYPE_TBL != 0) {
            val cols = if (bone) {
                "COLUMN_NAME, COLUMN_TYPE, COLUMN_COMMENT, ORDINAL_POSITION"
            } else {
                "COLUMN_NAME, COLUMN_TYPE, COLUMN_COMMENT, ORDINAL_POSITION, IS_NULLABLE, COLUMN_DEFAULT"
            }
            tmpl.query(
                """
            SELECT
                $cols ,
                GROUP_CONCAT(TABLE_NAME) as TBL_NAME,
                COUNT(1) SAME_COUNT
            FROM
                INFORMATION_SCHEMA.COLUMNS
            WHERE
                TABLE_SCHEMA = database()
                AND TABLE_NAME IN (?, ?)
            GROUP BY $cols
            HAVING SAME_COUNT != 2
            """, table, other
            ) {
                diff.append("\nCOL@")
                diff.append(it.getString("TBL_NAME")).append(".")
                diff.append(it.getString("COLUMN_NAME")).append("=")
                diff.append(it.getString("COLUMN_TYPE")).append(",")
                diff.append(it.getString("COLUMN_COMMENT")).append(",")
                diff.append(it.getString("ORDINAL_POSITION")).append(",")
                if (!bone) {
                    var nullable = it.getString("IS_NULLABLE")
                    if (it.wasNull()) nullable = "<NULL>"
                    diff.append(nullable).append(",")
                    diff.append(it.getString("COLUMN_DEFAULT"))
                }
            }
        }

        // diff index
        if (types and TYPE_IDX != 0) {
            tmpl.query(
                """
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
            """, table, other
            ) {
                diff.append("\nIDX@")
                diff.append(it.getString("TBL_NAME")).append(".")
                diff.append(it.getString("INDEX_NAME")).append("=")
                diff.append(it.getString("NON_UNIQUE")).append(",")
                diff.append(it.getString("SEQ_IN_INDEX")).append(",")
                diff.append(it.getString("COLUMN_NAME")).append(",")
                diff.append(it.getString("INDEX_TYPE"))
            }
        }

        // diff trigger
        if (types and TYPE_TRG != 0) {
            tmpl.query(
                """
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
            """, table, other
            ) {
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

        if (isH2Database(dataSource)) {
            SimpleJdbcTemplate(dataSource).query("SHOW COLUMNS FROM $table") {
                val n = it.getString("FIELD")
                val t = it.getString("TYPE")
                rst.add("`$n` $t COMMENT ''") // `EVENT_NAME` varchar(100) NOT NULL COMMENT 'event name'
            }
            return rst
        }

        SimpleJdbcTemplate(dataSource).query(
            """
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
        """, table
        ) {
            val n = it.getString("COLUMN_NAME")
            val t = it.getString("COLUMN_TYPE")
            val c = it.getString("COLUMN_COMMENT").replace("'", "\\'")
            rst.add("`$n` $t COMMENT '$c'") // `EVENT_NAME` varchar(100) NOT NULL COMMENT 'event name'
        }

        return rst
    }

    override fun showPkeyCol(dataSource: DataSource, table: String): List<String> {
        val rst = LinkedList<String>()

        if (isH2Database(dataSource)) {
            SimpleJdbcTemplate(dataSource).query("SHOW COLUMNS FROM $table") {
                val t = it.getString("KEY")
                if (t.isNotBlank()) {
                    rst.add(it.getString("FIELD"))
                }
            }
            return rst
        }

        SimpleJdbcTemplate(dataSource).query(
            """
        SHOW KEYS FROM $table WHERE KEY_NAME = 'PRIMARY'
        """
        ) {
            rst.add(it.getString("COLUMN_NAME"))
        }

        return rst
    }

    override fun showBoneTrg(dataSource: DataSource, table: String): List<Trg> {
        val rst = ArrayList<Trg>()
        if (isH2Database(dataSource)) {
            return rst
        }

        SimpleJdbcTemplate(dataSource).query(
            """
            SELECT
                TRIGGER_NAME,
                ACTION_TIMING,
                EVENT_MANIPULATION,
                ACTION_STATEMENT
            FROM
                INFORMATION_SCHEMA.TRIGGERS
            WHERE
                EVENT_OBJECT_SCHEMA = database()
                AND EVENT_OBJECT_TABLE = ?
            """, table
        ) {
            rst.add(
                Trg(
                    it.getString("TRIGGER_NAME"),
                    it.getString("ACTION_TIMING"),
                    it.getString("EVENT_MANIPULATION"),
                    it.getString("ACTION_STATEMENT"),
                    table
                )
            )
        }

        return rst
    }

    override fun makeDdlTrg(trg: Trg, drop: Boolean): String {
        return if (drop) {
            "DROP TRIGGER IF EXISTS ${trg.name}"
        } else {
            "CREATE TRIGGER ${trg.name} ${trg.timing} ${trg.action} ON ${trg.table} FOR EACH ROW ${trg.event}"
        }
    }
}
