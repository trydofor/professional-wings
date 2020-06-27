package pro.fessional.wings.faceless.flywave.impl

import org.slf4j.LoggerFactory
import pro.fessional.wings.faceless.flywave.FlywaveDataSources
import pro.fessional.wings.faceless.flywave.SchemaDefinitionLoader
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager
import pro.fessional.wings.faceless.flywave.SqlSegmentProcessor
import pro.fessional.wings.faceless.flywave.SqlStatementParser
import pro.fessional.wings.faceless.flywave.util.SimpleJdbcTemplate
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_1ST_SCHEMA
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner.commentInfo
import java.util.LinkedList
import java.util.SortedMap
import java.util.concurrent.atomic.AtomicLong
import javax.sql.DataSource


/**
 * 根据数据库中表（含分表）的名字，进行版本管理。
 *
 * @author trydofor
 * @since 2019-06-05
 */
class DefaultRevisionManager(
        private val flywaveDataSources: FlywaveDataSources,
        private val sqlStatementParser: SqlStatementParser,
        private val sqlSegmentProcessor: SqlSegmentProcessor,
        private val schemaDefinitionLoader: SchemaDefinitionLoader) : SchemaRevisionManager {

    private val logger = LoggerFactory.getLogger(DefaultRevisionManager::class.java)
    private val unapplyMark = "1000-01-01"
    private val runningFlag = "17"
    private val runningMark = "$unapplyMark 00:00:$runningFlag"

    override fun currentRevision() = flywaveDataSources.plains().map {
        val tmpl = SimpleJdbcTemplate(it.value, it.key)
        val revi = getRevision(tmpl)
        it.key to revi
    }.toMap()


    override fun publishRevision(revision: Long, commitId: Long) {
        if (revision < REVISION_1ST_SCHEMA) {
            logger.warn("[publishRevision]🐝 skip the revision less than {}", REVISION_1ST_SCHEMA)
            return
        }
        val selectUpto = """
                SELECT
                    revision,
                    upto_sql,
                    apply_dt
                FROM sys_schema_version
                WHERE revision > ?
                    AND revision <= ?
                ORDER BY revision ASC
                """
        val selectUndo = """
                SELECT
                    revision,
                    undo_sql,
                    apply_dt
                FROM sys_schema_version
                WHERE revision <= ?
                    AND revision >= ?
                ORDER BY revision DESC
                """

        val shardTmpl = flywaveDataSources.shard?.let { SimpleJdbcTemplate(it, "sharding") }
        for ((plainName, plainDs) in flywaveDataSources.plains()) {
            val plainTmpl = SimpleJdbcTemplate(plainDs, plainName)
            val plainRevi = getRevision(plainTmpl)

            if (plainRevi < 0) {
                logger.warn("[publishRevision]🐝 skip a bad version, db-revi={}, to-revi={}, db={}", plainRevi, revision, plainName)
                continue
            }
            if (plainRevi == revision) {
                logger.warn("[publishRevision]🐝 skip the same version, db-revi={}, to-revi={}, db={}", plainRevi, revision, plainName)
                continue
            }

            val isUptoSql = revision > plainRevi
            val reviQuery = if (isUptoSql) { // 升级
                logger.info("[publishRevision]🐝 upgrade, db-revi={}, to-revi={}, db={}", plainRevi, revision, plainName)
                selectUpto
            } else { // 降级
                logger.info("[publishRevision]🐝 downgrade, db-revi={}, to-revi={}", plainRevi, revision)
                selectUndo
            }

            val reviText = LinkedList<Triple<Long, String, String>>()

            plainTmpl.query(reviQuery, plainRevi, revision) {
                val tpl = Triple(it.getLong(1), it.getString(2), it.getString(3))
                reviText.add(tpl)
            }

            if (reviText.isEmpty()) {
                logger.warn("[publishRevision]🐝 skip the empty revision-sqls, name={}, db-revi={}, to-revi={}", plainName, plainRevi, revision)
                continue
            }

            if (reviText.count { isRunning(it.third) } != 0) {
                logger.warn("[publishRevision]🐝 skip running revision, need manually fix it [UPDATE sys_schema_version SET apply_dt = '1000-01-01 00:00:00' WHERE apply_dt = '1000-01-01 00:00:17'] , name={}, db-revi={}, to-revi={}", plainName, plainRevi, revision)
                continue
            }

            // 检测和处理边界
            if (isUptoSql) { // 版本从低到高，重点不一致，或不存在
                if (reviText.last.first != revision) {
                    logger.warn("[publishRevision]🐝 skip the different upgrade end point , name={}, db-revi={}, to-revi={}", plainName, plainRevi, revision)
                    continue
                }
                // 检测apply情况，应该全都未APPLY
                if (reviText.count { isUnapply(it.third) } != reviText.size) {
                    logger.warn("[publishRevision]🐝 skip broken un-apply_dt upgrade , name={}, db-revi={}, to-revi={}", plainName, plainRevi, revision)
                    continue
                }
            } else {  // 版本从高到低
                if (reviText.last.first != revision) {
                    logger.warn("[publishRevision]🐝 skip the different downgrade end point , name={}, db-revi={}, to-revi={}", plainName, plainRevi, revision)
                    continue
                }
                // 检测apply情况
                if (reviText.count { isUnapply(it.third) } != 0) {
                    logger.warn("[publishRevision]🐝 skip broken apply_dt-ed downgrade , name={}, db-revi={}, to-revi={}", plainName, plainRevi, revision)
                    continue
                }

                // 去掉终点脚本，不需要执行
                reviText.removeLast()
            }

            // 检查部分执行
            val partUndo = LinkedList<Triple<Long, String, String>>()
            val partRedo = LinkedList<Triple<Long, String, String>>()
            plainTmpl.query("""
                SELECT
                    revision,
                    upto_sql,
                    undo_sql,
                    apply_dt
                FROM sys_schema_version
                WHERE apply_dt > '$unapplyMark 00:00:00' 
                    AND apply_dt <= '$unapplyMark 23:23:59'
                ORDER BY revision DESC
            """) {
                val rev = it.getLong(1)
                val tplRedo = Triple(rev, it.getString(2), it.getString(4))
                val tplUndo = Triple(rev, it.getString(3), it.getString(4))
                logger.warn("[publishRevision]🐝 undo partly applied for name={} revi={} need undo it", plainName, rev)
                partRedo.add(tplRedo)
                partUndo.add(tplUndo)
            }

            if (partRedo.size > 0) {
                partUndo.addAll(partRedo)
                reviText.addAll(0, partUndo)
            }

            val plainTbls = schemaDefinitionLoader.showTables(plainDs)
            for ((revi, text) in reviText) {
                logger.info("[publishRevision]🐝 ready for name={} revi={}", plainName, revi)
                try {
                    applyRevisionSql(revi, text, isUptoSql, commitId, plainTmpl, shardTmpl, plainTbls)
                } catch (e: Exception) {
                    logger.error("[publishRevision]🐝 failed to exec sql revision, name=$plainName, revi=$revi", e)
                    throw e
                }
                logger.info("[publishRevision]🐝 done for name={}, revi={}", plainName, revi)
            }

            // 后置检查
            val newRevi = getRevision(plainTmpl)
            if (revision != newRevi) {
                val msg = "[publishRevision]🐝 failed to post check schema revision, name=$plainName, need ${revision}, but $newRevi"
                logger.error(msg)
                throw IllegalStateException(msg)
            }
        }
    }


    override fun forceApplyBreak(revision: Long, commitId: Long, isUpto: Boolean, dataSource: String?) {
        val shardTmpl = flywaveDataSources.shard?.let { SimpleJdbcTemplate(it, "sharding") }
        val reviQuery = if (isUpto) {
            """
            SELECT
                upto_sql,
                apply_dt
            FROM sys_schema_version
            WHERE revision = ?
            """
        } else {
            """
            SELECT
                undo_sql,
                apply_dt
            FROM sys_schema_version
            WHERE revision = ?
            """
        }
        logger.info("[forceApplyBreak]🐝 begin data-source={}", dataSource)

        for ((plainName, plainDs) in flywaveDataSources.plains()) {

            if (!(dataSource == null || plainName.equals(dataSource, true))) {
                logger.info("[forceApplyBreak]🐝 skip data-source={}", plainName)
                continue
            }

            logger.info("[forceApplyBreak]🐝 apply data-source={}", plainName)
            val plainTmpl = SimpleJdbcTemplate(plainDs, plainName)
            val applySqls = LinkedList<Pair<String, String>>()

            try {
                plainTmpl.query(reviQuery, revision) {
                    applySqls.add(Pair(it.getString(1), it.getString(2)))
                }
            } catch (e: Exception) {
                assertNot1st(plainDs, e)
                logger.warn("[forceApplyBreak]🐝 skip, un-init-ist, revi={}, isUpto={}, db={}", applySqls.size, revision, isUpto, plainName)
                continue
            }

            if (applySqls.size != 1) {
                logger.warn("[forceApplyBreak]🐝 skip, find {} sqls, revi={}, isUpto={}, db={}", applySqls.size, revision, isUpto, plainName)
                continue
            }

            val reviSql = applySqls.first
            val notAppd = isUnapply(reviSql.second)
            val msgAly = applyMessage(reviSql.second)

            if (isUpto && !notAppd) {
                logger.error("[forceApplyBreak]🐝 skip, $msgAly upto, need force to undo first, revi={}, isUpto={}, db={}", revision, isUpto, plainName)
                continue
            }

            if (!isUpto && notAppd) {
                logger.error("[forceApplyBreak]🐝 skip, not $msgAly undo, revi={}, isUpto={}, db={}", revision, isUpto, plainName)
                continue
            }

            logger.info("[forceApplyBreak]🐝 ready, revi={}, isUpto={}, db={}", revision, isUpto, plainName)
            val plainTbls = schemaDefinitionLoader.showTables(plainDs)
            applyRevisionSql(revision, reviSql.first, isUpto, commitId, plainTmpl, shardTmpl, plainTbls)
            logger.info("[forceApplyBreak]🐝 done, revi={}, isUpto={}, db={}", revision, isUpto, plainName)
        }
        logger.info("[forceApplyBreak]🐝 end")
    }

    override fun checkAndInitSql(sqls: SortedMap<Long, SchemaRevisionManager.RevisionSql>, commitId: Long, updateDiff: Boolean) {
        if (sqls.isNullOrEmpty()) {
            logger.warn("[checkAndInitSql]🐝 skip empty local sqls")
            return
        }

        val selectSql = """
                    SELECT upto_sql, undo_sql, apply_dt, comments
                    FROM sys_schema_version
                    WHERE revision = ?
                    """
        val insertSql = """
                    INSERT INTO sys_schema_version
                    (revision, create_dt, commit_id, upto_sql, undo_sql, comments)
                    VALUES(?, NOW(), ?, ?, ?, ?)
                    """

        for ((revi, entry) in sqls) {
            val undoSql = entry.undoText
            val uptoSql = entry.uptoText
            if (undoSql.isBlank() && uptoSql.isBlank()) {
                logger.warn("[checkAndInitSql]🐝 skip an both empty sqls, revi={}, upto-path={}, undo-path={}", entry.revision, entry.uptoPath, entry.undoPath)
                continue
            }

            for ((plainName, plainDs) in flywaveDataSources.plains()) {
                logger.info("[checkAndInitSql]🐝 ready to check revi={}, on db={}", revi, plainName)
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
                    if (revi <= REVISION_1ST_SCHEMA) {
                        assertNot1st(plainDs, e)
                        logger.warn("[checkAndInitSql]🐝 try to init first version, revi={}, on db={}", revi, plainName)
                        applyRevisionSql(revi, uptoSql, true, commitId, plainTmpl, null, emptyList())
                        dbVal["upto_sql"] = ""
                        dbVal["undo_sql"] = ""
                        dbVal["apply_dt"] = ""
                        dbVal["comments"] = ""
                    } else {
                        val regex = "sys_schema_version.*exist".toRegex(setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
                        if (e.message?.contains(regex) == true) {
                            logger.error("[checkAndInitSql]🐝 you may need revision=$REVISION_1ST_SCHEMA, for un-init database")
                        }
                        throw e
                    }
                }

                val comments = commentInfo(entry.undoPath, entry.uptoPath)
                if (dbVal.isEmpty()) {
                    logger.info("[checkAndInitSql]🐝 insert for database not exist revi={}, db={}", revi, plainName)
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
                            logger.info("[checkAndInitSql]🐝 empty undo-sql, update it. revi={}, db={}", revi, plainName)
                        } else {
                            logger.warn("[checkAndInitSql]🐝 diff undo-sql $msgAly, update it. revi={}, db={}", revi, plainName)
                        }
                    } else {
                        logger.warn("[checkAndInitSql]🐝 diff undo-sql $msgAly, ignore it. revi={}, db={}", revi, plainName)
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
                            logger.info("[checkAndInitSql]🐝 empty upto-sql, update it. revi={}, db={}", revi, plainName)
                        } else {
                            logger.warn("[checkAndInitSql]🐝 diff upto-sql $msgAly, update it. revi={}, db={}", revi, plainName)
                        }
                    } else {
                        logger.warn("[checkAndInitSql]🐝 diff upto-sql $msgAly, ignore it. revi={}, db={}", revi, plainName)
                    }
                }

                // check comments
                if (comments != dbVal["comments"]) {
                    if (updateDiff) {
                        updSql.append("comments = ?, ")
                        updVal.add(comments)
                        logger.info("[checkAndInitSql]🐝 update comments. revi={}, db={}", revi, plainName)
                    } else {
                        logger.warn("[checkAndInitSql]🐝 diff comments ignore it. revi={}, db={}", revi, plainName)
                    }
                }

                // update
                if (updSql.isNotEmpty()) {
                    logger.info("[checkAndInitSql]🐝 update diff to database revi={}, applyDt={}, db={}", revi, applyd, plainName)
                    updVal.add(commitId)
                    updVal.add(revi)
                    val rst = plainTmpl.update("""
                        UPDATE sys_schema_version SET
                            $updSql
                            modify_dt = NOW(),
                            commit_id = ?
                        WHERE revision = ?
                        """, *updVal.toArray())

                    if (rst != 1) {
                        throw IllegalStateException("failed to update revi=$revi, db=$plainName")
                    }
                } else {
                    logger.info("[checkAndInitSql]🐝 skip all same revi={}, applyDt={}, db={}", revi, applyd, plainName)
                }
            }
        }
    }

    override fun forceUpdateSql(revision: SchemaRevisionManager.RevisionSql, commitId: Long) {
        forceUpdateSql(revision.revision, revision.uptoText, revision.undoText, commitId)
    }

    override fun forceUpdateSql(revision: Long, upto: String, undo: String, commitId: Long) {
        val insertSql = """
            INSERT INTO sys_schema_version
            (revision, create_dt, commit_id, upto_sql, undo_sql)
            VALUES(?, NOW(), ?, ?, ?)
            """
        val updateSql = """
            UPDATE sys_schema_version SET
                upto_sql = ?,
                undo_sql = ?,
                modify_dt = NOW(),
                commit_id = ?
            WHERE revision = ?
            """

        for ((plainName, plainDs) in flywaveDataSources.plains()) {
            logger.info("[forceUpdateSql]🐝 ready force update revi={}, on db={}", revision, plainName)
            val tmpl = SimpleJdbcTemplate(plainDs, plainName)

            // 不要使用msyql的REPLACE INTO，使用标准SQL

            val cnt = tmpl.count("SELECT COUNT(1) FROM sys_schema_version WHERE revision= ?", revision)
            if (cnt == 0) {
                val rst = tmpl.update(insertSql, revision, commitId, upto, undo)
                logger.info("[forceUpdateSql]🐝 done force insert {} records, revi={}, on db={}", rst, revision, plainName)
            } else {
                val rst = tmpl.update(updateSql, upto, undo, commitId, revision)
                logger.info("[forceUpdateSql]🐝 done force update {} records, revi={}, on db={}", rst, revision, plainName)
            }
        }
    }

    override fun forceExecuteSql(text: String) {
        val shardTmpl = flywaveDataSources.shard?.let { SimpleJdbcTemplate(it, "sharding") }
        val sqlSegs = sqlSegmentProcessor.parse(sqlStatementParser, text)

        for ((plainName, plainDs) in flywaveDataSources.plains()) {
            logger.info("[forceExecuteSql]🐝 ready force execute sql on db={}", plainName)
            val plainTmpl = SimpleJdbcTemplate(plainDs, plainName)
            val plainTbls = schemaDefinitionLoader.showTables(plainDs)

            for (seg in sqlSegs) {
                if (seg.sqlText.isBlank()) {
                    continue
                }
                // 不使用事务，出错时，根据日志进行回滚或数据清理
                if (seg.isPlain() || shardTmpl == null) {
                    logger.info("[forceExecuteSql]🐝 use plain to run sql-line from {} to {}, db={}", seg.lineBgn, seg.lineEnd, plainName)
                    runSegment(plainTmpl, plainTbls, seg)
                } else {
                    logger.info("[forceExecuteSql]🐝 use shard to run sql-line from {} to {}", seg.lineBgn, seg.lineEnd)
                    runSegment(shardTmpl, emptyList(), seg)
                }
            }
        }
    }

    //
    private fun applyRevisionSql(revi: Long, text: String, isUpto: Boolean, commitId: Long, plainTmpl: SimpleJdbcTemplate, shardTmpl: SimpleJdbcTemplate?, plainTbls: List<String>) {
        logger.info("[applyRevisionSql]🐝 parse revi-sql, revi={}, isUpto={}, mark as '$runningMark'", revi, isUpto)

        val plainName = plainTmpl.name

        // 记录部分执行情况。
        if (revi > REVISION_1ST_SCHEMA) {
            plainTmpl.update("UPDATE sys_schema_version SET apply_dt='$runningMark', commit_id=? WHERE revision=?", commitId, revi)
        }

        for (seg in sqlSegmentProcessor.parse(sqlStatementParser, text)) {
            if (seg.sqlText.isBlank()) {
                continue
            }
            // 不使用事务，出错时，根据日志进行回滚或数据清理
            if (seg.isPlain() || shardTmpl == null) {
                logger.info("[applyRevisionSql]🐝 use plain to run revi={}, sql-line from {} to {}, db={}", revi, seg.lineBgn, seg.lineEnd, plainName)
                runSegment(plainTmpl, plainTbls, seg)
            } else {
                logger.info("[applyRevisionSql]🐝 use shard to run revi={}, sql-line from {} to {}", revi, seg.lineBgn, seg.lineEnd)
                runSegment(shardTmpl, emptyList(), seg)
            }
        }

        // update apply datetime，避免时区问题，使用SQL语法
        val applyDt = if (isUpto) {
            "NOW()"
        } else {
            "'$unapplyMark'"
        }
        val cnt = try {
            plainTmpl.update("UPDATE sys_schema_version SET apply_dt=$applyDt, commit_id=? WHERE revision=?", commitId, revi)
        } catch (e: Exception) {
            assertNot1st(plainTmpl.dataSource, e)
            logger.warn("[applyRevisionSql]🐝 skip un-init-1st, revi={}, applyDt={}, db={}", revi, applyDt, plainName)
            return
        }
        // 执行了，必须一条，因为上面不会出现语法错误
        if (cnt == 1) {
            logger.info("[applyRevisionSql]🐝 update revi={}, applyDt={}, db={}", revi, applyDt, plainName)
        } else {
            throw IllegalStateException("update revi=$revi, but $cnt records affect, db=$plainName")
        }
    }


    private fun getRevision(tmpl: SimpleJdbcTemplate): Long = try {
        val rst = AtomicLong(0)
        tmpl.query("SELECT revision, apply_dt FROM sys_schema_version WHERE apply_dt >'$unapplyMark' order by revision desc limit 2") {
            val r = it.getLong(1)
            val d = it.getString(2)
            if (isRunning(d)) {
                logger.warn("[getRevision]🐝 find running revision={}, db={}", r, tmpl.name)
            } else if (rst.get() < r) {
                rst.set(r)
            }
        }
        val v = rst.get()
        logger.info("[getRevision]🐝 find applied revision={}, db={}", v, tmpl.name)
        v
    } catch (e: Exception) {
        assertNot1st(tmpl.dataSource, e)
        logger.warn("[getRevision]🐝 failed to get un-init-1st revision, return -1, db={}", tmpl.name)
        -1
    }

    private fun runSegment(tmpl: SimpleJdbcTemplate, tables: List<String>, seg: SqlSegmentProcessor.Segment) {
        val dbName = tmpl.name
        val tblApply = seg.applyTbl(tables)
        val errh = seg.errType
        if (tblApply.isEmpty()) {
            logger.info("[runSegment]🐝 run sql on direct table, db={}", dbName)
            try {
                tmpl.execute(seg.sqlText)
            } catch (e: Exception) {
                if (errh == SqlSegmentProcessor.ErrType.Skip) {
                    logger.warn("skip an error by $errh", e)
                } else {
                    logger.error("stop an error by $errh", e)
                    throw e
                }
            }
        } else {
            val tblName = seg.tblName
            for (tbl in tblApply) {
                if (tbl == tblName) {
                    logger.info("[runSegment]🐝 run sql on plain table={}, db={}", tbl, dbName)
                } else {
                    logger.info("[runSegment]🐝 run sql on shard/trace table={}, db={}", tbl, dbName)
                }

                try {
                    val sql = sqlSegmentProcessor.merge(seg, tbl)
                    tmpl.execute(sql)
                } catch (e: Exception) {
                    if (errh == SqlSegmentProcessor.ErrType.Skip) {
                        logger.warn("skip an error by $errh", e)
                    } else {
                        logger.error("stop an error by $errh", e)
                        throw e
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
            if (tables.find { it.equals("sys_schema_version", true) } != null) {
                throw er // 存在 sys_schema_version 表，报出原异常
            }
        } catch (e: Exception) {
            // 报出原异常
            throw er
        }
    }
}