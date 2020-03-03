package pro.fessional.wings.faceless.flywave

import org.slf4j.LoggerFactory
import pro.fessional.wings.faceless.flywave.util.TemplateUtil
import java.util.LinkedList
import java.util.SortedMap

/**
 * @author trydofor
 * @since 2019-06-06
 */
class SqlSegmentProcessor(
        commentSingle: String = "",
        commentMultiple: String = "",
        delimiterDefault: String = "",
        delimiterCommand: String = ""
) {

    enum class DbsType {
        Plain, Shard
    }

    enum class ErrType {
        Skip, Stop
    }

    class Segment(
            val dbsType: DbsType, // 数据源类型
            val lineBgn: Int, // 开始行，含
            val lineEnd: Int, // 结束行，含
            val tblName: String, // 主表
            val tblIdx2: SortedMap<Int, Int>, // 要替换的启止坐标位置
            val sqlText: String,  // SQL文本
            val errType: ErrType = ErrType.Stop, // 错误处理
            val tblRegx: Regex? // 应用表正则
    ) {
        /**
         * 返回应用到指定
         */
        fun applyTbl(tables: List<String>?): Set<String> {
            if (tables == null || tables.isEmpty() || tblIdx2.isEmpty() || tblName.isEmpty()) return emptySet()

            // 包括分表和日志表
            val tblApply = hashSetOf(tblName) // 保证当前执行
            if (tblRegx == null) {
                tblApply.addAll(tables.filter { hasType(tblName, it) > TYPE_PLAIN })
            } else {
                tblApply.addAll(tables.filter { tblRegx.matches(it) })
            }
            return tblApply
        }

        /**
         * 是否是 Plain 数据源
         */
        fun isPlain() = dbsType == DbsType.Plain
    }

    private val logger = LoggerFactory.getLogger(SqlSegmentProcessor::class.java)

    private val singleComment: String
    private val blockComment1: String
    private val blockComment2: String
    private val delimiterDefault: String
    private val delimiterCommand: String

    init {
        // wings.flywave.sql.comment-single="--"
        if (commentSingle.isBlank()) {
            this.singleComment = "--"
        } else {
            this.singleComment = commentSingle.trim()
        }
        logger.debug("[init] use single-comment= {}", this.singleComment)

        // wings.flywave.sql.comment-multiple="/*   */"
        if (commentMultiple.isBlank()) {
            this.blockComment1 = "/*"
            this.blockComment2 = "*/"
        } else {
            val spt = commentMultiple.split("[ \t]+".toRegex(), 2)
            this.blockComment1 = spt[0].trim()
            this.blockComment2 = spt[1].trim()
        }
        logger.debug("[init] use multiple-comment={} {}", this.blockComment1, this.blockComment2)

        // wings.flywave.sql.delimiter-default=";"
        if (delimiterDefault.isBlank()) {
            this.delimiterDefault = ";"
        } else {
            this.delimiterDefault = delimiterDefault.trim()
        }
        logger.debug("[init] use delimiter={}", this.delimiterDefault)

        // wings.flywave.sql.delimiter-command="DELIMITER"
        if (delimiterCommand.isBlank()) {
            this.delimiterCommand = "DELIMITER"
        } else {
            this.delimiterCommand = delimiterCommand.trim()
        }
        logger.debug("[init] use delimiter command={}", this.delimiterCommand)
    }

    /**
     * 解析SQL文本，变成可以执行和替换的SQL片段
     * @param statementParser 语法解析器
     * @param text SQL文本
     */
    fun parse(statementParser: SqlStatementParser, text: String): List<Segment> {
        if (text.isBlank()) {
            return emptyList()
        }

        logger.debug("[parse] parse sql start")
        var lineBgn = -1
        var dbsAnot = 0
        var tblName = ""
        var tbApply = ""
        var errType = ErrType.Stop
        var inComment = false
        var lineCur = 0
        var delimiter = delimiterDefault

        val result = LinkedList<Segment>()
        val lines = text.lines()
        val total = lines.size
        val builder = StringBuilder()

        for (line in lines) {
            lineCur += 1

            if (line.isBlank()) {
                continue
            }

            if (line.startsWith(singleComment)) {
                val mt = parseCmd(line)
                if (mt.isNotEmpty()) {
                    val (tbl, pln, apl, ers) = mt
                    logger.debug("[parse] got annotation, line={} , tblName={}, dbsType={}, apply={}, error={}", lineCur, tbl, pln, apl, ers)

                    if (tbl.isNotEmpty()) {
                        tblName = tbl
                    }
                    if (pln.contains("shard", true)) {
                        dbsAnot = 1
                    } else if (line.contains("plain", true)) {
                        dbsAnot = -1
                    }
                    if (apl.isNotEmpty()) {
                        tbApply = apl
                    }
                    if (ers.contains("skip", true)) {
                        errType = ErrType.Skip
                    } else if (ers.contains("stop", true)) {
                        errType = ErrType.Stop
                    }
                }
                continue
            }
            if (line.startsWith(blockComment1)) {
                inComment = true
                continue
            }
            if (line.endsWith(blockComment2)) {
                inComment = false
                continue
            }
            if (inComment) {
                continue
            }

            if (line.startsWith(delimiterCommand, true)) {
                delimiter = line.substringAfter(delimiterCommand).trim()
                logger.debug("[parse] got delimiter command, delimiter={}", delimiter)
                continue
            }

            if (lineBgn < 0) {
                lineBgn = lineCur
            }

            if (line.endsWith(delimiter, true) || lineCur == total) {
                builder.append(line.substringBeforeLast(delimiter))
                val sql = builder.toString().trim()
                if (sql.isNotEmpty()) {
                    if (tblName.isEmpty()) {
                        logger.debug("[parse] use statementParser to get tableName and shard/plain")
                        when (val st = statementParser.parseTypeAndTable(sql)) {
                            is SqlStatementParser.SqlType.Plain -> {
                                tblName = st.table
                                if (dbsAnot == 0) dbsAnot = -1
                            }
                            is SqlStatementParser.SqlType.Shard -> {
                                tblName = st.table
                                if (dbsAnot == 0) dbsAnot = 1
                            }
                            SqlStatementParser.SqlType.Other ->
                                logger.warn("[parse] unsupported type, use shard datasource to run, sql=$sql")
                        }
                    }
                    val tblIdx2 = TemplateUtil.parse(sql, tblName)
                    val dbsType = if (dbsAnot < 0) DbsType.Plain else DbsType.Shard
                    val tblRegx = if (tbApply.isEmpty()) null else tbApply.toRegex(RegexOption.IGNORE_CASE)
                    logger.debug("[parse] got a segment line from={}, to={}, tableName={}, dbsType={}, errType={}, tblRegx={}", lineBgn, lineCur, tblName, dbsType, errType, tbApply)
                    result.add(Segment(dbsType, lineBgn, lineCur, tblName, tblIdx2, sql, errType, tblRegx))
                }
                // reset for next
                lineBgn = -1
                dbsAnot = 0
                tblName = ""
                tbApply = ""
                errType = ErrType.Stop
                builder.clear()
            } else {
                builder.append(line).append("\n")
            }
        }
        logger.debug("[parse] parse sql done")
        return result
    }


    /**
     * 使用新表名，合并sql片段。
     * @see SqlStatementParser.parseTokenIndices
     * @param segment 解析过的sql片段
     * @param newTbl 新表
     */
    fun merge(segment: Segment, newTbl: String) = TemplateUtil.merge(segment.sqlText, segment.tblIdx2, newTbl)

    companion object {
        const val TYPE_OTHER = -1
        const val TYPE_PLAIN = 0
        const val TYPE_TRACE = 1
        const val TYPE_SHARD = 2

        /**
         * 判断两表关系，忽略大小写
         * @param table 主表
         * @param other 其他表
         * @return
         * -1:没有关系
         * 0:自己
         * 1:trace表
         * 2:shard表
         */
        fun hasType(table: String, other: String): Int {
            val pos = other.indexOf(table, 0, true)
            if (pos < 0) return TYPE_OTHER

            val len = pos + table.length
            if (len == other.length) return TYPE_PLAIN

            val c = other[len]
            if (c == '$') return TYPE_TRACE

            var typ = TYPE_OTHER
            if (c == '_') {
                for (i in len + 1 until other.length) {
                    if (other[i] in '0'..'9') {
                        typ = TYPE_SHARD
                    } else {
                        return TYPE_OTHER
                    }
                }
            }

            return typ
        }

        // -- wgs_order@plain apply@ctr_clerk[_0-0]* error@skip
        private val cmdReg = "([^@ \t]+)?@([^@ \t]+)".toRegex()

        fun parseCmd(line: String): Array<String> {
            var emt = true
            var tbl = ""
            var dbs = ""
            var apl = ""
            var ers = ""
            for (mr in cmdReg.findAll(line)) {
                val (k, v) = mr.destructured
                if(v.equals("plain", true) || v.equals("shard", true)){
                    tbl = k
                    dbs = v
                    emt = false
                } else if (k.equals("apply", true)){
                    apl = v
                    emt = false
                } else if (k.equals("error", true)){
                    ers = v
                    emt = false
                }
            }

            return if (emt) emptyArray() else arrayOf(tbl, dbs, apl, ers)
        }
    }
}