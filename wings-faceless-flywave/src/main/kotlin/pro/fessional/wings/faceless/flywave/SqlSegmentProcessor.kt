package pro.fessional.wings.faceless.flywave

import org.slf4j.LoggerFactory
import pro.fessional.mirana.data.Null
import pro.fessional.wings.faceless.flywave.util.TemplateUtil
import java.util.LinkedList
import java.util.SortedMap

/**
 * @author trydofor
 * @since 2019-06-06
 */
class SqlSegmentProcessor(
        commentSingle: String = "--",
        commentMultiple: String = "/*   */",
        delimiterDefault: String = ";",
        delimiterCommand: String = "DELIMITER"
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
            val askText: String, // 确认语句
            val tblRegx: Regex? // 应用表正则
    ) {
        /**
         * 返回应用到指定
         */
        fun applyTbl(tables: List<String>?): Set<String> {
            if (tables == null || tables.isEmpty() || tblIdx2.isEmpty() || tblName.isEmpty()) return emptySet()

            return if (tblRegx == null) {
                tables.filter {
                    hasType(tblName, it) >= TYPE_PLAIN
                }
            } else {
                val ptn = tblRegx.pattern
                when {
                    ptn.equals("nut", true) -> tables.filter {
                        val tp = hasType(tblName, it)
                        tp == TYPE_PLAIN || tp == TYPE_SHARD
                    }
                    ptn.equals("log", true) -> tables.filter {
                        hasType(tblName, it) == TYPE_TRACE
                    }
                    else -> tables.filter {
                        tblRegx.matches(it)
                    }
                }
            }.toSet()
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
        // wings.faceless.flywave.sql.comment-single="--"
        if (commentSingle.isBlank()) {
            this.singleComment = "--"
        } else {
            this.singleComment = commentSingle.trim()
        }
        logger.debug("[init] use single-comment= {}", this.singleComment)

        // wings.faceless.flywave.sql.comment-multiple="/*   */"
        if (commentMultiple.isBlank()) {
            this.blockComment1 = "/*"
            this.blockComment2 = "*/"
        } else {
            val spt = commentMultiple.split("[ \t]+".toRegex(), 2)
            this.blockComment1 = spt[0].trim()
            this.blockComment2 = spt[1].trim()
        }
        logger.debug("[init] use multiple-comment={} {}", this.blockComment1, this.blockComment2)

        // wings.faceless.flywave.sql.delimiter-default=";"
        if (delimiterDefault.isBlank()) {
            this.delimiterDefault = ";"
        } else {
            this.delimiterDefault = delimiterDefault.trim()
        }
        logger.debug("[init] use delimiter={}", this.delimiterDefault)

        // wings.faceless.flywave.sql.delimiter-command="DELIMITER"
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
        var tblName = Null.Str
        var tbApply = Null.Str
        var errType = ErrType.Stop
        var askText = Null.Str
        var inComment = false
        var lineCur = 0
        var delimiter = delimiterDefault

        val result = LinkedList<Segment>()
        val lines = text.lines()
        val total = lines.size
        val builder = StringBuilder()
        val comment = StringBuilder()

        for (line in lines) {
            lineCur += 1
            val ln = line.trim()

            if (ln.isBlank()) {
                continue
            }

            val sic = ln.startsWith(singleComment)
            if (sic || (!inComment && comment.isNotEmpty())) {
                val mt = if (sic) {
                    parseCmd(ln, singleComment)
                } else {
                    val ts = comment.toString().trim()
                    logger.debug("[parse] multi-comment {}", ts)
                    comment.setLength(0)
                    parseCmd(ts, blockComment1)
                }
                if (mt.isNotEmpty()) {
                    val (tbl, pln, apl, ers, ask) = mt
                    logger.debug("[parse] got annotation, line={} , tblName={}, dbsType={}, apply={}, error={}", lineCur, tbl, pln, apl, ers)

                    if (tbl.isNotEmpty()) {
                        tblName = tbl
                    }
                    if (pln.contains("shard", true)) {
                        dbsAnot = 1
                    } else if (ln.contains("plain", true)) {
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
                    if (ask.isNotEmpty()) {
                        askText = ask
                    }
                }

                if (sic) {
                    continue
                }
            }

            val m1 = ln.startsWith(blockComment1)
            val m2 = ln.endsWith(blockComment2)
            if (m1 && m2) {
                comment.append(ln)
                inComment = false
                continue
            }
            if (m1) {
                inComment = true
                comment.append(ln)
                continue
            }
            if (m2) {
                comment.append('\n').append(ln)
                inComment = false
                continue
            }
            if (inComment) {
                comment.append('\n').append(ln)
                continue
            }

            if (ln.startsWith(delimiterCommand, true)) {
                delimiter = ln.substringAfter(delimiterCommand).trim()
                logger.debug("[parse] got delimiter command, delimiter={}", delimiter)
                continue
            }

            if (lineBgn < 0) {
                lineBgn = lineCur
            }

            val den = ln.endsWith(delimiter, true)
            if (den || lineCur == total) {
                if(den) {
                    builder.append(ln, 0, ln.length - delimiter.length)
                }else{
                    builder.append(ln)
                }
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
                    result.add(Segment(dbsType, lineBgn, lineCur, tblName, tblIdx2, sql, errType, askText, tblRegx))
                }
                // reset for next
                lineBgn = -1
                dbsAnot = 0
                tblName = Null.Str
                tbApply = Null.Str
                errType = ErrType.Stop
                askText = Null.Str
                builder.setLength(0)
            } else {
                builder.append(ln).append("\n")
            }
        }
        logger.debug("[parse] parse sql done")
        return result
    }


    /**
     * 使用新表名，合并sql片段。
     * @param segment 解析过的sql片段
     * @param newTbl 新表
     */
    fun merge(segment: Segment, newTbl: String) = TemplateUtil.merge(segment.sqlText, segment.tblIdx2, newTbl)

    companion object {
        const val TYPE_OTHER = -1
        const val TYPE_PLAIN = 0
        const val TYPE_TRACE = 1
        const val TYPE_SHARD = 2

        private val regShard = "_[0-9]+".toRegex()
        private val regTrace = "(_[0-9]+)?\\\$\\w+".toRegex()

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
            if (pos != 0) return TYPE_OTHER

            val suf = other.substring(table.length)
            return when {
                suf.isEmpty() -> TYPE_PLAIN
                regShard.matches(suf) -> {
                    TYPE_SHARD
                }
                regTrace.matches(suf) -> {
                    TYPE_TRACE
                }
                else -> {
                    TYPE_OTHER
                }
            }
        }

        // -- wgs_order@plain apply@ctr_clerk[_0-0]* error@skip ask@danger
        private val cmdReg = """([^@\s]+)?\s*@\s*([^@\s]+)""".toRegex(RegexOption.MULTILINE)

        fun parseCmd(line: String, head: String): Array<String> {
            var emt = true
            var tbl = Null.Str
            var dbs = Null.Str
            var apl = Null.Str
            var ers = Null.Str
            var ask = Null.Str
            for (mr in cmdReg.findAll(line.substringAfter(head))) {
                val (ko, vo) = mr.destructured
                val k = ko.trim()
                val v = vo.trim()
                if (v.equals("plain", true) || v.equals("shard", true)) {
                    tbl = k
                    dbs = v
                    emt = false
                } else if (k.equals("apply", true)) {
                    apl = v
                    emt = false
                } else if (k.equals("error", true)) {
                    ers = v
                    emt = false
                } else if (k.equals("ask", true)) {
                    ask = v
                    emt = false
                }
            }

            return if (emt) emptyArray() else arrayOf(tbl, dbs, apl, ers, ask)
        }
    }
}
