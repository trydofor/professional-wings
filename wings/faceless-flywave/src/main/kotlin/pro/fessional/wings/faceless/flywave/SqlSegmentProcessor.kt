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
        val dbsType: DbsType, // Datasource type
        val lineBgn: Int, // begin line(include)
        val lineEnd: Int, // end line(include)
        val tblName: String, // plain table
        val tblIdx2: SortedMap<Int, Pair<Int, String>>, // start and stop indexes to be replaced
        val sqlText: String,  // SQL text
        val errType: ErrType = ErrType.Stop, // error handle
        val askText: String, // confirm text
        val tblRegx: Regex?, // regexp of apply table
        val trgJour: Boolean, // whether affects trigger
        val dicName: Map<String, String> = emptyMap() // name replacing
    ) {
        /**
         * Filter out tables that can be applied as well as replacement keywords
         */
        fun applyTbl(tables: List<String>?): Map<String, Map<String, String>> {
            if (tables.isNullOrEmpty() || tblIdx2.isEmpty() || tblName.isEmpty()) return emptyMap()

            val tbls = if (tblRegx == null) {
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

            if (tbls.isEmpty()) return emptyMap()
            val tkns = tblIdx2.values.map { (_, v) -> v }.toSet()

            // table => ( token => replacement)
            return tbls.associateWith {
                tkns.associateWith { tk ->
                    it.replace(tblName, tk)
                }
            }
        }

        /**
         * Whether is plain datasource
         */
        fun isPlain() = dbsType == DbsType.Plain
    }

    private val log = LoggerFactory.getLogger(SqlSegmentProcessor::class.java)

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
        log.debug("[init] use single-comment= {}", this.singleComment)

        // wings.faceless.flywave.sql.comment-multiple="/*   */"
        if (commentMultiple.isBlank()) {
            this.blockComment1 = "/*"
            this.blockComment2 = "*/"
        } else {
            val spt = commentMultiple.split("[ \t]+".toRegex(), 2)
            this.blockComment1 = spt[0].trim()
            this.blockComment2 = spt[1].trim()
        }
        log.debug("[init] use multiple-comment={} {}", this.blockComment1, this.blockComment2)

        // wings.faceless.flywave.sql.delimiter-default=";"
        if (delimiterDefault.isBlank()) {
            this.delimiterDefault = ";"
        } else {
            this.delimiterDefault = delimiterDefault.trim()
        }
        log.debug("[init] use delimiter={}", this.delimiterDefault)

        // wings.faceless.flywave.sql.delimiter-command="DELIMITER"
        if (delimiterCommand.isBlank()) {
            this.delimiterCommand = "DELIMITER"
        } else {
            this.delimiterCommand = delimiterCommand.trim()
        }
        log.debug("[init] use delimiter command={}", this.delimiterCommand)
    }

    /**
     * Parse the SQL text to the sql segment that can be replaced and executed.
     *
     * @param statementParser simple statement parser
     * @param text SQL text
     */
    fun parse(statementParser: SqlStatementParser, text: String): List<Segment> {
        if (text.isBlank()) {
            return emptyList()
        }

        log.debug("[parse] parse sql start")
        var lineBgn = -1
        var dbsAnot = 0
        var tblName = Null.Str
        var tbApply = Null.Str
        var errType = ErrType.Stop
        var askText = Null.Str
        var trgJour = false
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
                    log.debug("[parse] multi-comment {}", ts)
                    comment.setLength(0)
                    parseCmd(ts, blockComment1)
                }

                if (mt != null) {
                    log.debug("[parse] got annotation, line={} , options={}", lineCur, mt)

                    if (mt.tbl.isNotEmpty()) {
                        tblName = mt.tbl
                    }
                    if (mt.dbs.contains("shard", true)) {
                        dbsAnot = 1
                    } else if (ln.contains("plain", true)) {
                        dbsAnot = -1
                    }
                    if (mt.apl.isNotEmpty()) {
                        tbApply = mt.apl
                    }
                    if (mt.ers.contains("skip", true)) {
                        errType = ErrType.Skip
                    } else if (mt.ers.contains("stop", true)) {
                        errType = ErrType.Stop
                    }
                    if (mt.ask.isNotEmpty()) {
                        askText = mt.ask
                    }
                    if (mt.trg) {
                        trgJour = true
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
                log.debug("[parse] got delimiter command, delimiter={}", delimiter)
                continue
            }

            if (lineBgn < 0) {
                lineBgn = lineCur
            }

            var idx = ln.lastIndexOf(delimiter, ignoreCase = true)
            var den = false
            if (idx >= 0) {
                val idl = ln.indexOf(delimiter, ignoreCase = true)
                val lst = if (idl < idx) {
                    ln.substring(idl + delimiter.length, idx).trim()
                } else {
                    ln.substring(idx + delimiter.length).trim()
                }

                if (lst.isEmpty()) {
                    den = true
                } else if (lst.startsWith(singleComment)) {
                    den = true
                    if (idx == idl) idx = ln.length
                } else if (lst.startsWith(blockComment1) && lst.indexOf(blockComment2, ignoreCase = true) == lst.length - blockComment2.length) {
                    den = true
                    if (idx == idl) idx = ln.length
                } else {
                    log.warn("find middle delimiter=$delimiter in line=$ln")
                    den = idx == ln.length - delimiter.length
                }
            }

            if (den || lineCur == total) {
                if (den) {
                    builder.append(ln, 0, idx)
                } else {
                    builder.append(ln)
                }
                val sql = builder.toString().trim()
                if (sql.isNotEmpty()) {
                    var rename = Null.Str
                    if (tblName.isEmpty()) {
                        log.debug("[parse] use statementParser to get tableName and shard/plain")
                        when (val st = statementParser.parseTypeAndTable(sql)) {
                            is SqlStatementParser.SqlType.Plain -> {
                                tblName = st.table
                                rename = st.rename
                                trgJour = true
                                if (dbsAnot == 0) dbsAnot = -1
                            }

                            is SqlStatementParser.SqlType.Shard -> {
                                tblName = st.table
                                if (dbsAnot == 0) dbsAnot = 1
                            }

                            SqlStatementParser.SqlType.Other ->
                                log.warn("[parse] unsupported type, use shard datasource to run, sql=$sql")
                        }
                    }

                    val dicName = if (rename.isBlank()) emptyMap() else mapOf(tblName to rename)
                    val tblList = if (rename.isBlank()) listOf(tblName) else listOf(tblName, rename)
                    val tblIdx2 = TemplateUtil.parse(sql, tblList)
                    val dbsType = if (dbsAnot < 0) DbsType.Plain else DbsType.Shard
                    val tblRegx = if (tbApply.isEmpty()) null else tbApply.toRegex(RegexOption.IGNORE_CASE)
                    log.debug("[parse] got a segment line from={}, to={}, tableName={}, dbsType={}, errType={}, tblRegx={}", lineBgn, lineCur, tblName, dbsType, errType, tbApply)
                    result.add(Segment(dbsType, lineBgn, lineCur, tblName, tblIdx2, sql, errType, askText, tblRegx, trgJour, dicName))
                }
                // reset for next
                lineBgn = -1
                dbsAnot = 0
                tblName = Null.Str
                tbApply = Null.Str
                errType = ErrType.Stop
                askText = Null.Str
                trgJour = false
                builder.setLength(0)
            } else {
                builder.append(ln).append("\n")
            }
        }
        log.debug("[parse] parse sql done")
        return result
    }


    /**
     * Merge the segment with new table.
     * @param segment parsed sql segment
     * @param newTbl new table
     */
    fun merge(segment: Segment, newTbl: Map<String, String>) = TemplateUtil.merge(segment.sqlText, segment.tblIdx2, newTbl)

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        const val TYPE_OTHER = -1
        const val TYPE_PLAIN = 0
        const val TYPE_TRACE = 1
        const val TYPE_SHARD = 2

        /**
         * Table placeholder, default `XXX`
         */
        const val PLAIN_TABLE = "XXX"

        /**
         * RegExp of shard table in `XXX_01`
         */
        const val SHARD_LINE_SEQ = "${PLAIN_TABLE}_[0-9]+"

        /**
         * RegExp of trace table in `XXX$log`
         */
        const val TRACE_DOLLAR = "${PLAIN_TABLE}(_[0-9]+)?\\\$[a-z]*"

        /**
         * RegExp of trace table in XXX__log
         */
        const val TRACE_SU2_LINE = "${PLAIN_TABLE}(_[0-9]+)?__+[a-z]*"

        /**
         * RegExp of trace table in _log_XXX
         */
        const val TRACE_PRE_LINE = "_+([a-z]+_+)?${PLAIN_TABLE}(_[0-9]+)?"

        private var regShard = SHARD_LINE_SEQ.toRegex(RegexOption.IGNORE_CASE)
        private var regTrace = TRACE_SU2_LINE.toRegex(RegexOption.IGNORE_CASE)

        /**
         * Set the RegExp of shard table, `XXX` is placeholder of plain table
         * @param reg Regexp of shard table
         * @see PLAIN_TABLE
         */
        @JvmStatic
        fun setShardFormat(reg: String) {
            assert(reg.contains(PLAIN_TABLE)) { "Regexp MUST contains $PLAIN_TABLE" }
            regShard = reg.toRegex(RegexOption.IGNORE_CASE)
        }

        /**
         * Set the RegExp of trace table, `XXX` is placeholder of plain table
         * @param reg Regexp of shard table
         * @see PLAIN_TABLE
         */
        @JvmStatic
        fun setTraceFormat(reg: String) {
            assert(reg.contains(PLAIN_TABLE)) { "Regexp MUST contains $PLAIN_TABLE" }
            regTrace = reg.toRegex(RegexOption.IGNORE_CASE)
        }

        /**
         * Determine the relationship type of `other` to `table` (case-insensitive)
         *
         * -1: no relation,
         * 0: table itself,
         * 1: other is trace,
         * 2: other is shard,
         *
         * @param table plain table
         * @param other other table
         * @return the type
         */
        fun hasType(table: String, other: String): Int {
            val pos = other.indexOf(table, 0, true)
            if (pos < 0) return TYPE_OTHER

            val suf = other.replace(table, PLAIN_TABLE)
            return when {
                suf == PLAIN_TABLE -> TYPE_PLAIN
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

        data class Opt(
            val tbl: String = Null.Str,
            val dbs: String = Null.Str,
            val apl: String = Null.Str,
            val ers: String = Null.Str,
            val ask: String = Null.Str,
            val trg: Boolean = false
        )

        fun parseCmd(line: String, head: String): Opt? {
            var emt = true
            var tbl = Null.Str
            var dbs = Null.Str
            var apl = Null.Str
            var ers = Null.Str
            var ask = Null.Str
            var trg = false
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
                } else if (v.equals("trigger", true)) {
                    trg = true
                    emt = false
                }
            }

            return if (emt) null else Opt(tbl, dbs, apl, ers, ask, trg)
        }
    }
}
