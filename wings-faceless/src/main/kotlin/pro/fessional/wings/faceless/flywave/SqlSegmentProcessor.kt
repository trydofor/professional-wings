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

    class Segment(
            val isPlain: Boolean, // 是否使用真实数据源
            val lineBgn: Int, // 开始行，含
            val lineEnd: Int, // 结束行，含
            val tblName: String, // 主表
            val tblIdx2: SortedMap<Int, Int>, // 要替换的启止坐标位置
            val sqlText: String // SQL文本
    ) {
        /**
         * 完全不需要考虑跟踪表，直接执行
         */
        fun isBlack() = tblIdx2.isEmpty()
    }

    private val logger = LoggerFactory.getLogger(SqlSegmentProcessor::class.java)

    private val singleComment: String
    private val blockComment1: String
    private val blockComment2: String
    private val delimiterDefault: String
    private val delimiterCommand: String

    private val shardAnnotation = "@shard"
    private val plainAnnotation = "@plain"

    init {
        // wings.flywave.sql.comment-single="--"
        if (commentSingle.isBlank()) {
            this.singleComment = "--"
        } else {
            this.singleComment = commentSingle.trim()
        }
        logger.debug("use single-comment= {}", this.singleComment)

        // wings.flywave.sql.comment-multiple="/*   */"
        if (commentMultiple.isBlank()) {
            this.blockComment1 = "/*"
            this.blockComment2 = "*/"
        } else {
            val spt = commentMultiple.split("[ \t]+".toRegex(), 2)
            this.blockComment1 = spt[0].trim()
            this.blockComment2 = spt[1].trim()
        }
        logger.debug("use multiple-comment={} {}", this.blockComment1, this.blockComment2)

        // wings.flywave.sql.delimiter-default=";"
        if (delimiterDefault.isBlank()) {
            this.delimiterDefault = ";"
        } else {
            this.delimiterDefault = delimiterDefault.trim()
        }
        logger.debug("use delimiter={}", this.delimiterDefault)

        // wings.flywave.sql.delimiter-command="DELIMITER"
        if (delimiterCommand.isBlank()) {
            this.delimiterCommand = "DELIMITER"
        } else {
            this.delimiterCommand = delimiterCommand.trim()
        }
        logger.debug("use delimiter command={}", this.delimiterCommand)
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

        logger.debug("parse sql start")
        var lineBgn = -1
        var annotate = 0
        var tblName = ""
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
                if (line.contains(shardAnnotation, true)) {
                    annotate = 1
                } else if (line.contains(plainAnnotation, true)) {
                    annotate = -1
                }
                if (annotate != 0) {
                    tblName = line.substringBefore("@").substringAfterLast(singleComment).trim()
                    logger.debug("got annotation, line={} plain={}, tableName={}", lineCur, annotate < 0, tblName)
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
                logger.debug("got delimiter command, delimiter={}", delimiter)
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
                        logger.debug("use statementParser to get tableName and shard/plain")
                        when (val st = statementParser.parseTypeAndTable(sql)) {
                            is SqlStatementParser.SqlType.Plain -> {
                                tblName = st.table
                                if (annotate == 0) annotate = -1
                            }
                            is SqlStatementParser.SqlType.Shard -> {
                                tblName = st.table
                                if (annotate == 0) annotate = 1
                            }
                            SqlStatementParser.SqlType.Other ->
                                logger.warn("unsupported type, use shard datasource to run, sql=$sql")
                        }
                    }
                    val isPlain = annotate < 0
                    logger.debug("got a segment line from={}, to={}, tableName={}, plain={}", lineBgn, lineCur, tblName, isPlain)
                    val tblIdx2 = TemplateUtil.parse(sql, tblName)
                    result.add(Segment(isPlain, lineBgn, lineCur, tblName, tblIdx2, sql))
                }
                // reset for next
                lineBgn = -1
                annotate = 0
                tblName = ""
                builder.clear()
            } else {
                builder.append(line).append("\n")
            }
        }
        logger.debug("parse sql done")
        return result
    }


    /**
     * 使用新表名，合并sql片段。
     * @see SqlStatementParser.parseTokenIndices
     * @param segment 解析过的sql片段
     * @param newTbl 新表
     */
    fun merge(segment: Segment, newTbl: String) = TemplateUtil.merge(segment.sqlText, segment.tblIdx2, newTbl)


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

    companion object {
        const val TYPE_OTHER = -1
        const val TYPE_PLAIN = 0
        const val TYPE_TRACE = 1
        const val TYPE_SHARD = 2
    }
}