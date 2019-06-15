package pro.fessional.wings.oracle.flywave

import org.slf4j.LoggerFactory
import java.util.LinkedList

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
            val revision: Long, // 版本号
            val isPlain: Boolean, // 是否使用真实数据源
            val lineBgn: Int, // 开始行，含
            val lineEnd: Int, // 结束行，含
            val tblName: String, // 主表
            val tblIdx2: IntArray, // 要替换的启止坐标位置
            val sqlText: String // SQL文本
    ){
        /**
         * 完全不需要考虑影子表，直接执行
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
        logger.info("use single-comment= {}", this.singleComment)

        // wings.flywave.sql.comment-multiple="/*   */"
        if (commentMultiple.isBlank()) {
            this.blockComment1 = "/*"
            this.blockComment2 = "*/"
        } else {
            val spt = commentMultiple.split("[ \t]+".toRegex(), 2)
            this.blockComment1 = spt[0].trim()
            this.blockComment2 = spt[1].trim()
        }
        logger.info("use multiple-comment={} {}", this.blockComment1, this.blockComment2)

        // wings.flywave.sql.delimiter-default=";"
        if (delimiterDefault.isBlank()) {
            this.delimiterDefault = ";"
        } else {
            this.delimiterDefault = delimiterDefault.trim()
        }
        logger.info("use delimiter={}", this.delimiterDefault)

        // wings.flywave.sql.delimiter-command="DELIMITER"
        if (delimiterCommand.isBlank()) {
            this.delimiterCommand = "DELIMITER"
        } else {
            this.delimiterCommand = delimiterCommand.trim()
        }
        logger.info("use delimiter command={}", this.delimiterCommand)
    }

    fun parse(statementParser: SqlStatementParser, revi: Long, text: String): List<Segment> {
        logger.debug("parsing revi={}", revi)
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
                if (tblName.isEmpty()) {
                    logger.debug("use statementParser to get tableName and shard/plain")
                    when (val sqlType = statementParser.parseTypeAndTable(sql)) {
                        is SqlStatementParser.SqlType.Plain -> {
                            tblName = sqlType.table
                            if (annotate == 0) annotate = -1
                        }
                        is SqlStatementParser.SqlType.Shard -> {
                            tblName = sqlType.table
                            if (annotate == 0) annotate = 1
                        }
                        SqlStatementParser.SqlType.Other ->
                            logger.warn("unsupported type, use shard datasource to run, revi=$revi, sql=$sql")
                    }
                }
                val isPlain = annotate < 0
                logger.debug("got a segment line from={}, to={}, tableName={}, plain={}", lineBgn, lineCur, tblName, isPlain)
                val tblIdx2 = statementParser.parseTableReplace(tblName, sql)
                result.add(Segment(revi, isPlain, lineBgn, lineCur, tblName, tblIdx2, sql))

                // reset for next
                lineBgn = -1
                annotate = 0
                tblName = ""
                builder.clear()
            } else {
                builder.append(line).append("\n")
            }
        }
        logger.debug("parsed revi={}", revi)
        return result
    }


    fun merge(segment: Segment, newTbl: String): String {
        val idx2 = segment.tblIdx2
        if (idx2.isEmpty()) return segment.sqlText

        val sql = segment.sqlText
        val len = sql.length
        val buff = StringBuilder(len * 3 / 2)
        var off = 0
        for (i in 0 until idx2.size step 2) {
            val idx = idx2[i]
            if (idx > off) {
                buff.append(sql, off, idx)
            }
            buff.append(newTbl)
            off = idx2[i + 1]
        }

        if (off in 1 until len) {
            buff.append(sql, off, len)
        }
        return buff.toString()
    }
}