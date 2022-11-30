package pro.fessional.wings.faceless.flywave.impl

import org.slf4j.LoggerFactory
import org.slf4j.event.Level.ERROR
import org.slf4j.event.Level.INFO
import org.slf4j.event.Level.WARN
import pro.fessional.mirana.data.Null
import pro.fessional.wings.faceless.flywave.SchemaDefinitionLoader
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager.AskType
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager.Status
import pro.fessional.wings.faceless.flywave.SqlSegmentProcessor
import pro.fessional.wings.faceless.flywave.SqlSegmentProcessor.ErrType
import pro.fessional.wings.faceless.flywave.SqlStatementParser
import pro.fessional.wings.faceless.flywave.WingsRevision
import pro.fessional.wings.faceless.flywave.util.SimpleJdbcTemplate
import pro.fessional.wings.faceless.flywave.util.TemplateUtil
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner.commentInfo
import java.util.LinkedList
import java.util.SortedMap
import java.util.TreeMap
import java.util.concurrent.atomic.AtomicLong
import java.util.function.BiConsumer
import java.util.function.Function
import javax.sql.DataSource


/**
 * 根据数据库中表（含分表）的名字，进行版本管理。
 *
 * @author trydofor
 * @since 2019-06-05
 */
class DefaultRevisionManager(
    private val plainDataSources: Map<String, DataSource>,
    private val shardDataSource: DataSource?,
    private val sqlStatementParser: SqlStatementParser,
    private val sqlSegmentProcessor: SqlSegmentProcessor,
    private val schemaDefinitionLoader: SchemaDefinitionLoader,
    private val schemaVersionTable: String = "sys_schema_version"
) : SchemaRevisionManager {

    private val revi1st = WingsRevision.V00_19_0512_01_Schema.revision()
    private val log = LoggerFactory.getLogger(DefaultRevisionManager::class.java)
    private val unapplyMark = "1000-01-01"
    private val runningFlag = "17.521"
    private val runningMark = "$unapplyMark 00:00:$runningFlag"

    private val interactive = DefaultInteractiveManager<AskType>(log, plainDataSources, "🐝")
    private val dropReg = HashMap<String, Regex>()

    override fun logWay(func: BiConsumer<String, String>): BiConsumer<String, String> {
        return interactive.logWay(func)
    }

    override fun askWay(func: Function<String, Boolean>): Function<String, Boolean> {
        return interactive.askWay(func)
    }

    override fun needAsk(ask: AskType, yes: Boolean): Boolean? {
        return interactive.needAsk(ask, yes)
    }

    /**
     * 增加一个识别drop语句的表达式
     * @param regexp 正则表达式
     */
    fun addDropRegexp(regexp: String) {
        dropReg[regexp] = regexp.toRegex(RegexOption.IGNORE_CASE)
    }

    override fun currentRevision() = plainDataSources.map {
        val tmpl = SimpleJdbcTemplate(it.value, it.key)
        val revi = getRevision(tmpl)
        it.key to revi
    }.toMap()

    override fun statusRevisions() = plainDataSources.map {
        val tmpl = SimpleJdbcTemplate(it.value, it.key)
        val line = lineRevision(tmpl)
        it.key to line
    }.toMap()

    override fun publishRevision(revision: Long, commitId: Long) {
        val here = "publishRevision"
        if (revision < revi1st) {
            interactive.log(WARN, here, "skip the revision less than $revi1st")
            return
        }
        val selectUpto = """
                SELECT
                    revision,
                    upto_sql,
                    apply_dt
                FROM $schemaVersionTable
                WHERE revision > ?
                    AND revision <= ?
                ORDER BY revision ASC
                """
        val selectUndo = """
                SELECT
                    revision,
                    undo_sql,
                    apply_dt
                FROM $schemaVersionTable
                WHERE revision <= ?
                    AND revision >= ?
                ORDER BY revision DESC
                """

        val shardTmpl = shardDataSource?.let { SimpleJdbcTemplate(it, "sharding") }
        for ((plainName, plainDs) in plainDataSources) {
            val plainTmpl = SimpleJdbcTemplate(plainDs, plainName)
            val plainRevi = getRevision(plainTmpl)

            if (plainRevi < 0) {
                interactive.log(WARN, here, "skip a bad version,    db-revi=$plainRevi, to-revi=$revision, db=$plainName")
                continue
            }
            if (plainRevi == revision) {
                interactive.log(WARN, here, "skip the same version, db-revi=$plainRevi, to-revi=$revision, db=$plainName")
                continue
            }

            val isUptoSql = revision > plainRevi
            val reviQuery = if (isUptoSql) { // 升级
                interactive.log(INFO, here, "upgrade,   db-revi=$plainRevi, to-revi=$revision, db=$plainName")
                selectUpto
            } else { // 降级
                interactive.log(INFO, here, "downgrade, db-revi=$plainRevi, to-revi=$revision")
                selectUndo
            }

            val reviText = LinkedList<Triple<Long, String, String>>()

            plainTmpl.query(reviQuery, plainRevi, revision) {
                val tpl = Triple(it.getLong(1), it.getString(2), it.getString(3))
                reviText.add(tpl)
            }

            if (reviText.isEmpty()) {
                interactive.log(WARN, here, "skip the empty revision-sqls, db-revi=$plainRevi, to-revi=$revision, db=$plainName")
                continue
            }

            if (reviText.count { isRunning(it.third) } != 0) {
                interactive.log(
                    WARN,
                    here,
                    "skip running revision, need manually fix it [UPDATE $schemaVersionTable SET apply_dt = '1000-01-01 00:00:00' WHERE apply_dt = '1000-01-01 00:00:17'] db-revi=$plainRevi, to-revi=$revision, db=$plainName"
                )
                continue
            }

            // 检测和处理边界
            if (isUptoSql) { // 版本从低到高，重点不一致，或不存在
                if (reviText.last.first != revision) {
                    interactive.log(WARN, here, "skip the diff upgrade end point, db-revi=$plainRevi, to-revi=$revision, db=$plainName")
                    continue
                }
                // 检测apply情况，应该全都未APPLY
                if (reviText.count { isUnapply(it.third) } != reviText.size) {
                    interactive.log(WARN, here, "skip broken un-apply_dt upgrade, db-revi=$plainRevi, to-revi=$revision, db=$plainName")
                    continue
                }
            } else {  // 版本从高到低
                if (reviText.last.first != revision) {
                    interactive.log(WARN, here, "skip the diff downgrade end point, db-revi=$plainRevi, to-revi=$revision, db=$plainName")
                    continue
                }
                // 检测apply情况
                if (reviText.count { isUnapply(it.third) } != 0) {
                    interactive.log(WARN, here, "skip broken apply_dt-ed downgrade, db-revi=$plainRevi, to-revi=$revision, db=$plainName")
                    continue
                }

                // 去掉终点脚本，不需要执行
                reviText.removeLast()
            }

            // 检查部分执行
            val partUndo = LinkedList<Triple<Long, String, String>>()
            val partRedo = LinkedList<Triple<Long, String, String>>()
            plainTmpl.query(
                """
                SELECT
                    revision,
                    upto_sql,
                    undo_sql,
                    apply_dt
                FROM $schemaVersionTable
                WHERE apply_dt > '$unapplyMark 00:00:00' 
                    AND apply_dt <= '$unapplyMark 23:23:59'
                ORDER BY revision DESC
            """
            ) {
                val rev = it.getLong(1)
                val tplRedo = Triple(rev, it.getString(2), it.getString(4))
                val tplUndo = Triple(rev, it.getString(3), it.getString(4))
                interactive.log(WARN, here, "undo partly applied for revi=$rev, db=$plainName need undo it")
                partRedo.add(tplRedo)
                partUndo.add(tplUndo)
            }

            if (partRedo.size > 0) {
                partUndo.addAll(partRedo)
                reviText.addAll(0, partUndo)
            }

            val plainTbls = schemaDefinitionLoader.showTables(plainDs)
            for ((revi, text) in reviText) {
                interactive.log(INFO, here, "ready for revi=$revi, db=$plainName ")
                try {
                    applyRevisionSql(revi, text, isUptoSql, commitId, plainTmpl, shardTmpl, plainTbls)
                } catch (e: Exception) {
                    interactive.log(ERROR, here, "failed to exec sql revision, revi=$revi, db=$plainName", e)
                    throw e
                }
                interactive.log(INFO, here, "done for revi=$revi, db=$plainName")
            }

            // 后置检查
            interactive.log(INFO, here, "post check revi=$revision, db=$plainName")
            val newRevi = getRevision(plainTmpl)
            if (revision != newRevi) {
                val msg = "failed to post check schema revision, need $revision, but $newRevi, db=$plainName"
                interactive.log(ERROR, here, msg)
                throw IllegalStateException(msg)
            }
        }
    }


    override fun forceApplyBreak(revision: Long, commitId: Long, isUpto: Boolean, dataSource: String?) {
        val shardTmpl = shardDataSource?.let { SimpleJdbcTemplate(it, "sharding") }
        val reviQuery = if (isUpto) {
            """
            SELECT
                upto_sql,
                apply_dt
            FROM $schemaVersionTable
            WHERE revision = ?
            """
        } else {
            """
            SELECT
                undo_sql,
                apply_dt
            FROM $schemaVersionTable
            WHERE revision = ?
            """
        }
        val here = "forceApplyBreak"
        interactive.log(INFO, here, "begin revi=$revision, assigned db=$dataSource")

        for ((plainName, plainDs) in plainDataSources) {

            if (!(dataSource == null || plainName.equals(dataSource, true))) {
                interactive.log(INFO, here, "skip revi=$revision, on unmatched db=$plainName")
                continue
            }

            interactive.log(INFO, here, "apply revi=$revision, db=$plainName")
            val plainTmpl = SimpleJdbcTemplate(plainDs, plainName)
            val applySqls = LinkedList<Pair<String, String>>()

            try {
                plainTmpl.query(reviQuery, revision) {
                    applySqls.add(Pair(it.getString(1), it.getString(2)))
                }
            } catch (e: Exception) {
                assertNot1st(plainDs, e)
                interactive.log(WARN, here, "skip, un-init-ist, revi=$revision, isUpto=$isUpto, db=$plainName")
                continue
            }

            if (applySqls.size != 1) {
                interactive.log(WARN, here, "skip, find ${applySqls.size} sqls, revi=$revision, isUpto=$isUpto, db=$plainName")
                continue
            }

            val reviSql = applySqls.first
            val notAppd = isUnapply(reviSql.second)
            val msgAly = applyMessage(reviSql.second)

            if (isUpto && !notAppd) {
                interactive.log(ERROR, here, "skip, $msgAly upto, need force to undo first, revi=$revision, isUpto=$isUpto, db=$plainName")
                continue
            }

            if (!isUpto && notAppd) {
                interactive.log(ERROR, here, "skip, not $msgAly undo, revi=$revision, isUpto=$isUpto, db=$plainName")
                continue
            }

            interactive.log(INFO, here, "ready, revi=$revision, isUpto=$isUpto, db=$plainName")
            val plainTbls = schemaDefinitionLoader.showTables(plainDs)
            applyRevisionSql(revision, reviSql.first, isUpto, commitId, plainTmpl, shardTmpl, plainTbls)
            interactive.log(INFO, here, "done , revi=$revision, isUpto=$isUpto, db=$plainName")
        }

        interactive.log(INFO, here, "end, revi=$revision, assigned db=$dataSource")
    }

    override fun checkAndInitSql(sqls: SortedMap<Long, SchemaRevisionManager.RevisionSql>, commitId: Long, updateDiff: Boolean) {
        val here = "checkAndInitSql"
        if (sqls.isEmpty()) {
            interactive.log(WARN, here, "skip empty local sqls, should be removed")
            return
        }

        val selectSql = """
                    SELECT upto_sql, undo_sql, apply_dt, comments
                    FROM $schemaVersionTable
                    WHERE revision = ?
                    """
        val insertSql = """
                    INSERT INTO $schemaVersionTable
                    (revision, create_dt, commit_id, upto_sql, undo_sql, comments)
                    VALUES(?, NOW(3), ?, ?, ?, ?)
                    """

        for ((revi, entry) in sqls) {
            val undoSql = entry.undoText
            val uptoSql = entry.uptoText
            if (undoSql.isBlank() && uptoSql.isBlank()) {
                interactive.log(WARN, here, "skip an both empty sqls, revi=${entry.revision}, upto-path=${entry.uptoPath}, undo-path=${entry.undoPath}")
                continue
            }

            for ((plainName, plainDs) in plainDataSources) {
                interactive.log(INFO, here, "ready to check revi=$revi, on db=$plainName")
                val plainTmpl = SimpleJdbcTemplate(plainDs, plainName)
                val dbVal = HashMap<String, String>()

                try {
                    plainTmpl.query(selectSql, revi) {
                        dbVal["upto_sql"] = it.getString("upto_sql")
                        dbVal["undo_sql"] = it.getString("undo_sql")
                        dbVal["apply_dt"] = it.getString("apply_dt")
                        dbVal["comments"] = it.getString("comments")
                    }
                } catch (e: Exception) {
                    if (entry.uptoText.contains(schemaVersionTable, true)) {
                        assertNot1st(plainDs, e)
                        interactive.log(WARN, here, "try to init $schemaVersionTable , revision=$revi, on db=$plainName")
                        applyRevisionSql(revi, uptoSql, true, commitId, plainTmpl, null, emptyList(), false, entry.revision)
                        // for later update
                        dbVal["upto_sql"] = Null.Str
                        dbVal["undo_sql"] = Null.Str
                        dbVal["apply_dt"] = Null.Str
                        dbVal["comments"] = Null.Str
                    } else {
                        val help = """for un-init database, one of the following ways can auto init.
                                1.need $revi1st as the first revision
                                2.replace $revi1st to first revi by Scanner.Helper
                                3.the first revi less than $revi1st
                                4.branch create tables as in $revi1st
                            """.trimIndent()
                        interactive.log(ERROR, here, help)
                        throw e
                    }
                }

                val comments = commentInfo(entry.undoPath, entry.uptoPath)
                if (dbVal.isEmpty()) {
                    interactive.log(INFO, here, "insert for database not exist revi=$revi, db=$plainName")
                    val rst = plainTmpl.update(insertSql, revi, commitId, uptoSql, undoSql, comments)
                    if (rst != 1) {
                        throw IllegalStateException("failed to insert revi=$revi, db=$plainName")
                    }
                    continue
                }

                // check
                val updSql = StringBuilder()
                val updVal = LinkedList<Any>()
                val applyd = dbVal["apply_dt"]
                val notAly = isUnapply(applyd)
                val msgAly = applyMessage(applyd)

                // check undo
                val undoDbs = dbVal["undo_sql"]
                val undoBlk = undoDbs?.isBlank() != false
                if (undoSql != undoDbs) {
                    if (notAly || undoBlk || updateDiff) {
                        updSql.append("undo_sql = ?, ")
                        updVal.add(undoSql)
                        if (undoBlk) {
                            interactive.log(INFO, here, "empty undo-sql, update it. revi=$revi, db=$plainName")
                        } else {
                            interactive.log(WARN, here, "diff undo-sql $msgAly, update it. revi=$revi, db=$plainName")
                        }
                    } else {
                        interactive.log(WARN, here, "diff undo-sql $msgAly, ignore it. revi=$revi, db=$plainName")
                    }
                }

                // check upto
                val uptoDbs = dbVal["upto_sql"]
                val uptoBlk = uptoDbs?.isBlank() != false
                if (uptoSql != uptoDbs) {
                    if (notAly || uptoBlk || updateDiff) {
                        updSql.append("upto_sql = ?, ")
                        updVal.add(uptoSql)
                        if (uptoBlk) {
                            interactive.log(INFO, here, "empty upto-sql, update it. revi=$revi, db=$plainName")
                        } else {
                            interactive.log(WARN, here, "diff upto-sql $msgAly, update it. revi=$revi, db=$plainName")
                        }
                    } else {
                        interactive.log(WARN, here, "diff upto-sql $msgAly, ignore it. revi=$revi, db=$plainName")
                    }
                }

                // check comments
                if (comments != dbVal["comments"]) {
                    if (updateDiff) {
                        updSql.append("comments = ?, ")
                        updVal.add(comments)
                        interactive.log(INFO, here, "update comments. revi=$revi, db=$plainName")
                    } else {
                        interactive.log(WARN, here, "diff comments ignore it. revi=$revi, db=$plainName")
                    }
                }

                // update
                if (updSql.isNotEmpty()) {
                    // replace
                    val whereRevi: String
                    if (revi != entry.revision) {
                        interactive.log(INFO, here, "update diff to database refer-revi=$revi, entry-revi=${entry.revision}, applyDt=$applyd, db=$plainName")
                        updSql.append("revision = ?, ")
                        updVal.add(revi)
                        whereRevi = "revision in (?,?)"
                        updVal.add(commitId)
                        updVal.add(revi)
                        updVal.add(entry.revision)
                    } else {
                        interactive.log(INFO, here, "update diff to database revi=$revi, applyDt=$applyd, db=$plainName")
                        whereRevi = "revision = ?"
                        updVal.add(commitId)
                        updVal.add(revi)
                    }
                    val rst = plainTmpl.update(
                        """
                        UPDATE $schemaVersionTable SET
                            $updSql
                            modify_dt = NOW(3),
                            commit_id = ?
                        WHERE $whereRevi
                        """, *updVal.toArray()
                    )

                    if (rst != 1) {
                        throw IllegalStateException("failed to update revi=$revi, db=$plainName, affect $rst records")
                    }
                } else {
                    interactive.log(INFO, here, "skip all same  revi=$revi, applyDt=$applyd, db=$plainName")
                }
            }
        }
    }

    override fun forceUpdateSql(revision: SchemaRevisionManager.RevisionSql, commitId: Long) {
        forceUpdateSql(revision.revision, revision.uptoText, revision.undoText, commitId)
    }

    override fun forceUpdateSql(revision: Long, upto: String, undo: String, commitId: Long) {
        val insertSql = """
            INSERT INTO $schemaVersionTable
            (revision, create_dt, commit_id, upto_sql, undo_sql)
            VALUES(?, NOW(3), ?, ?, ?)
            """
        val updateSql = """
            UPDATE $schemaVersionTable SET
                upto_sql = ?,
                undo_sql = ?,
                modify_dt = NOW(3),
                commit_id = ?
            WHERE revision = ?
            """
        val here = "forceUpdateSql"
        for ((plainName, plainDs) in plainDataSources) {
            interactive.log(INFO, here, "ready force update revi=$revision, on db=$plainName")
            val tmpl = SimpleJdbcTemplate(plainDs, plainName)

            // 不要使用msyql的REPLACE INTO，使用标准SQL

            val cnt = tmpl.count("SELECT COUNT(1) FROM $schemaVersionTable WHERE revision= ?", revision)
            if (cnt == 0) {
                val rst = tmpl.update(insertSql, revision, commitId, upto, undo)
                interactive.log(INFO, here, "done force insert $rst records, revi=$revision, on db=$plainName")
            } else {
                val rst = tmpl.update(updateSql, upto, undo, commitId, revision)
                interactive.log(INFO, here, "done force update $rst records, revi=$revision, on db=$plainName")
            }
        }
    }

    override fun forceExecuteSql(text: String) {
        if (text.isEmpty()) return
        val shardTmpl = shardDataSource?.let { SimpleJdbcTemplate(it, "sharding") }
        val sqlSegs = sqlSegmentProcessor.parse(sqlStatementParser, text)

        val here = "forceExecuteSql"
        for ((plainName, plainDs) in plainDataSources) {
            interactive.log(INFO, here, "ready force execute sql on db=$plainName")
            val plainTmpl = SimpleJdbcTemplate(plainDs, plainName)
            val plainTbls = schemaDefinitionLoader.showTables(plainDs)

            for (seg in sqlSegs) {
                if (seg.sqlText.isBlank()) {
                    continue
                }
                // 不使用事务，出错时，根据日志进行回滚或数据清理
                if (seg.isPlain() || shardTmpl == null) {
                    interactive.log(INFO, here, "use plain to run sql-line from ${seg.lineBgn} to ${seg.lineEnd}, db=$plainName")
                    runSegment(plainTmpl, plainTbls, seg)
                } else {
                    interactive.log(INFO, here, "use shard to run sql-line from ${seg.lineBgn} to ${seg.lineEnd}")
                    runSegment(shardTmpl, emptyList(), seg)
                }
            }
        }
    }

    override fun forceExecuteSql(sqls: SortedMap<Long, SchemaRevisionManager.RevisionSql>, isUpto: Boolean) {
        val here = "forceExecuteSql"
        for (sql in sqls) {
            val txt = if (isUpto) sql.value.uptoText else sql.value.undoText
            if (txt.isEmpty()) {
                interactive.log(INFO, here, "skip empty for revision=${sql.key}")
            } else {
                interactive.log(INFO, here, "ready sql  for revision=${sql.key}")
                forceExecuteSql(txt)
            }
        }
    }

    //
    private fun applyRevisionSql(
        revi: Long, text: String, isUpto: Boolean, commitId: Long,
        plainTmpl: SimpleJdbcTemplate, shardTmpl: SimpleJdbcTemplate?, plainTbls: List<String>,
        check: Boolean = true, orig: Long = revi
    ) {
        val here = "applyRevisionSql"
        val plainName = plainTmpl.name

        if (!isUpto && interactive.needAsk(AskType.Undo)) {
            askSegment(revi, "apply undo sqls")
        }

        // 记录部分执行情况。
        if (check) {
            interactive.log(INFO, here, "parse revi-sql, revi=$revi, isUpto=$isUpto, mark as '$runningMark'")
            plainTmpl.update("UPDATE $schemaVersionTable SET apply_dt='$runningMark', commit_id=? WHERE revision=?", commitId, revi)
        } else {
            interactive.log(INFO, here, "parse revi-sql, revi=$revi, isUpto=$isUpto in abnormal mode")
        }

        for (seg in sqlSegmentProcessor.parse(sqlStatementParser, text)) {
            if (seg.sqlText.isBlank()) {
                continue
            }
            // 不使用事务，出错时，根据日志进行回滚或数据清理
            if (seg.isPlain() || shardTmpl == null) {
                interactive.log(INFO, here, "use plain to run revi=$revi, sql-line from ${seg.lineBgn} to ${seg.lineEnd}, db=$plainName")
                runSegment(plainTmpl, plainTbls, seg, revi)
            } else {
                interactive.log(INFO, here, "use shard to run revi=$revi, sql-line from ${seg.lineBgn} to ${seg.lineEnd}")
                runSegment(shardTmpl, emptyList(), seg, revi)
            }
        }

        // update apply datetime，避免时区问题，使用SQL语法
        val applyDt = if (isUpto) {
            "NOW(3)"
        } else {
            "'$unapplyMark'"
        }

        val cnt = try {
            if (revi == orig) {
                plainTmpl.update("UPDATE $schemaVersionTable SET apply_dt=$applyDt, commit_id=? WHERE revision=?", commitId, revi)
            } else {
                plainTmpl.update("UPDATE $schemaVersionTable SET apply_dt=$applyDt, commit_id=? WHERE revision in (?, ?)", commitId, revi, orig)
            }
        } catch (e: Exception) {
            assertNot1st(plainTmpl.dataSource, e)
            interactive.log(WARN, here, "skip un-init-1st, revi={}, applyDt=$revi, db=$plainName")
            return
        }
        // 执行了，必须一条，因为上面不会出现语法错误
        if (cnt == 1) {
            interactive.log(INFO, here, "update revi=$revi, applyDt=$applyDt, db=$plainName")
        } else {
            if (check) {
                throw IllegalStateException("update revi=$revi, but $cnt records affect, db=$plainName")
            } else {
                interactive.log(WARN, here, "update revi=$revi, $cnt records affect, db=$plainName in abnormal-mode")
            }
        }
    }


    private fun getRevision(tmpl: SimpleJdbcTemplate): Long {
        val here = "getRevision"
        return try {
            val rst = AtomicLong(0)
            tmpl.query("SELECT revision, apply_dt FROM $schemaVersionTable WHERE apply_dt >'$unapplyMark' order by revision desc limit 2") {
                val r = it.getLong(1)
                val d = it.getString(2)
                if (isRunning(d)) {
                    interactive.log(WARN, here, "find running revision=$r, db=${tmpl.name}")
                } else if (rst.get() < r) {
                    rst.set(r)
                }
            }
            val r = rst.get()
            interactive.log(INFO, here, "find applied revision=$r, db=${tmpl.name}")
            r
        } catch (e: Exception) {
            assertNot1st(tmpl.dataSource, e)
            interactive.log(WARN, here, "failed to get un-init-1st revision, return -1, db=${tmpl.name}")
            -1
        }
    }

    private fun lineRevision(tmpl: SimpleJdbcTemplate): SortedMap<Long, Status>? {
        val here = "lineRevision"
        val tree = TreeMap<Long, Status>()
        var last = -1L
        try {
            tmpl.query("SELECT revision, apply_dt FROM $schemaVersionTable order by revision") {
                val r = it.getLong(1)
                val d = it.getString(2)
                tree[r] = when {
                    isRunning(d) -> {
                        last = r
                        Status.Running
                    }
                    isUnapply(d) -> Status.Future
                    else -> {
                        last = r
                        Status.Applied
                    }
                }
            }
        } catch (e: Exception) {
            assertNot1st(tmpl.dataSource, e)
            interactive.log(WARN, here, "failed to get un-init-1st revision, return null, db=${tmpl.name}")
            return null
        }

        if (last > 0 && tree.isNotEmpty()) {
            for (entry in tree.entries) {
                if (entry.key < last && entry.value == Status.Future) {
                    entry.setValue(Status.Broken)
                }
            }
        }

        return tree
    }

    private fun runSegment(tmpl: SimpleJdbcTemplate, tables: List<String>, seg: SqlSegmentProcessor.Segment, revi: Long = 0) {
        val here = "runSegment"
        val dbName = tmpl.name
        val tblApply = seg.applyTbl(tables)

        val ask = if (seg.askText.isNotEmpty() && interactive.needAsk(AskType.Mark)) "强制确认：${seg.sqlText}" else Null.Str

        askSegment(revi, ask, dangerous(seg.sqlText))

        val erh = seg.errType
        if (tblApply.isEmpty()) {
            interactive.log(INFO, here, "run sql on direct table, db=$dbName")
            try {
                tmpl.execute(seg.sqlText)
            } catch (e: Exception) {
                interactive.log(WARN, here, "$erh an error", e)
                when (erh) {
                    ErrType.Skip -> {
                        // skip
                    }
                    else -> {
                        throw e
                    }
                }
            }
        } else {
            val tblName = seg.tblName
            for ((tbl, map) in tblApply) {
                if (tbl == tblName) {
                    interactive.log(INFO, here, "run sql on plain table=$tbl, db=$dbName")
                } else {
                    interactive.log(INFO, here, "run sql on shard/trace table=$tbl, db=$dbName")
                }

                val trgDef = if (seg.trgJour) {
                    schemaDefinitionLoader.showBoneTrg(tmpl.dataSource, tbl).filter {
                        it.event.contains(tbl, true)
                    }
                } else {
                    emptyList()
                }
                try {
                    for (trg in trgDef) {
                        interactive.log(WARN, here, "affect trigger=${trg.name}, table=$tbl, db=$dbName")
                        interactive.log(WARN, here, "recover trigger-ddl=" + schemaDefinitionLoader.makeDdlTrg(trg, false).replace('\n', ' '))
                        tmpl.execute(schemaDefinitionLoader.makeDdlTrg(trg, true))
                    }
                    //
                    val sql = sqlSegmentProcessor.merge(seg, map)
                    tmpl.execute(sql)
                    //
                    for (trg in trgDef) {
                        val ddl = schemaDefinitionLoader.makeDdlTrg(trg, false)
                        val dic = seg.dicName
                        if (dic.isEmpty()) {
                            tmpl.execute(ddl)
                        } else {
                            val ndl = TemplateUtil.replace(ddl, dic, bnd = false)
                            tmpl.execute(ndl)
                        }
                    }
                } catch (e: Exception) {
                    interactive.log(WARN, here, "$erh an error", e)
                    when (erh) {
                        ErrType.Skip -> {
                            // skip
                        }
                        else -> {
                            throw e
                        }
                    }
                }
            }
        }
    }

    private fun isUnapply(str: String?): Boolean {
        if (str.isNullOrEmpty()) return true
        return str.startsWith(unapplyMark)
    }

    private fun isRunning(str: String?): Boolean {
        if (str.isNullOrEmpty()) return false
        // 可能受到时区影响
        return str.startsWith(unapplyMark) && str.endsWith(runningFlag)
    }

    private fun applyMessage(str: String?): String = when {
        isRunning(str) -> "running"
        isUnapply(str) -> "unapply"
        else -> "applied"
    }

    private fun assertNot1st(ds: DataSource, er: Exception) {
        try {
            val tables = schemaDefinitionLoader.showTables(ds)
            if (tables.find { it.equals(schemaVersionTable, true) } != null) {
                log.error("exist $schemaVersionTable without any records, need manual fixed: drop empty table or insert records")
                throw er // 存在 $schemaVersionTable 表，报出原异常
            }
        } catch (e: Exception) {
            // 报出原异常
            throw er
        }
    }

    private fun dangerous(sql: String): String {
        if (!interactive.needAsk(AskType.Drop)) return Null.Str

        val txt = sql.trim()
        for ((k, reg) in dropReg) {
            if (reg.containsMatchIn(txt)) {
                return "dangerous sql, $k\n$txt"
            }
        }
        return Null.Str
    }

    private fun askSegment(revi: Long, vararg txt: String) {
        val ask = StringBuilder()
        for (s in txt) {
            if (s.isNotEmpty()) {
                ask.append(s)
                ask.append('\n')
            }
        }
        if (ask.isEmpty()) return

        if (revi > 0) {
            ask.append("revision=")
            ask.append(revi)
            ask.append("\n")
        }

        val pr = interactive.lastMessage.get() // 获取上一个信息，必须 interactive.log 之前
        interactive.log(WARN, "askSegment", ask.toString())

        if (pr != null) {
            ask.append("\n")
            ask.append(pr.first)
            ask.append("\t")
            ask.append(pr.second)
        }

        val msg = ask.trim().toString()

        interactive.ask(msg)
    }
}
