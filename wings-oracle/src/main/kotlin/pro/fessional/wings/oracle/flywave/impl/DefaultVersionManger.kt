package pro.fessional.wings.oracle.flywave.impl

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import pro.fessional.wings.oracle.flywave.FlywaveDataSources
import pro.fessional.wings.oracle.flywave.SchemaDefinitionLoader
import pro.fessional.wings.oracle.flywave.SchemaVersionManger
import pro.fessional.wings.oracle.flywave.SchemaVersionManger.Companion.init1stRevision
import pro.fessional.wings.oracle.flywave.SqlSegmentProcessor
import pro.fessional.wings.oracle.flywave.SqlStatementParser
import pro.fessional.wings.oracle.sugar.funs.md5
import java.util.LinkedList
import java.util.SortedMap
import javax.sql.DataSource


/**
 * 根据数据库中表（含分表）的名字，进行版本管理。
 *
 * @author trydofor
 * @since 2019-06-05
 */
class DefaultVersionManger(
        private val flywaveDataSources: FlywaveDataSources,
        private val sqlSegmentProcessor: SqlSegmentProcessor,
        private val sqlStatementParser: SqlStatementParser,
        private val schemaDefinitionLoader: SchemaDefinitionLoader) : SchemaVersionManger {

    private val logger = LoggerFactory.getLogger(DefaultVersionManger::class.java)

    override fun showCurrentVersion() = flywaveDataSources.allPlain().map {
        val template = JdbcTemplate(it.value)
        val revi = getRevision(it.key, template)
        it.key to revi
    }.toMap()


    override fun publishRevision(revision: Long, commitId: Long) {
        if (revision < init1stRevision) {
            logger.warn("skip the revision less than {}", init1stRevision)
            return
        }

        val shardTmpl = JdbcTemplate(flywaveDataSources.shard)
        for ((plainName, plainDs) in flywaveDataSources.allPlain()) {
            val plainTmpl = JdbcTemplate(plainDs)
            val plainRevi = getRevision(plainName, plainTmpl)

            if (plainRevi < 0 || plainRevi == revision) {
                logger.warn("skip a same or bad version, db-revi={}, to-revi={}, db={}", plainRevi, revision, plainName)
                continue
            }

            val reviQuery: String
            val isUptoSql = revision > plainRevi
            if (isUptoSql) { // 升级
                logger.info("upgrade, db-revi={}, to-revi={}, db={}", plainRevi, revision, plainName)
                reviQuery = """
                SELECT
                    REVISION,
                    UPTO_SQL,
                    APPLY_DT
                FROM SYS_SCHEMA_VERSION
                WHERE REVISION > $plainRevi
                    AND REVISION <= $revision
                ORDER BY REVISION ASC
                """.trimIndent()
            } else { // 降级
                logger.info("downgrade, db-revi={}, to-revi={}", plainRevi, revision)
                reviQuery = """
                SELECT
                    REVISION,
                    UNDO_SQL,
                    APPLY_DT
                FROM SYS_SCHEMA_VERSION
                WHERE REVISION <= $plainRevi
                    AND REVISION >= $revision
                ORDER BY REVISION DESC
                """.trimIndent()
            }

            val reviText = LinkedList<Triple<Long, String, String>>()

            plainTmpl.query(reviQuery) {
                val tpl = Triple(it.getLong(1), it.getString(2), it.getString(3))
                reviText.add(tpl)
            }

            if (reviText.isEmpty()) {
                logger.warn("skip the empty revision-sqls, name={}, db-revi={}, to-revi={}", plainName, plainRevi, revision)
                continue
            }

            // 检测和处理边界
            if (isUptoSql) { // 版本从低到高
                if (reviText.last.first != revision) {
                    logger.warn("skip the different upgrade end point , name={}, db-revi={}, to-revi={}", plainName, plainRevi, revision)
                    continue
                }
                // 检测apply情况，应该全都未APPLY
                if (reviText.count { notApply(it.third) } != reviText.size) {
                    logger.warn("skip broken un-apply_dt upgrade , name={}, db-revi={}, to-revi={}", plainName, plainRevi, revision)
                    continue
                }
            } else {  // 版本从高到低
                if (reviText.last.first != revision) {
                    logger.warn("skip the different downgrade end point , name={}, db-revi={}, to-revi={}", plainName, plainRevi, revision)
                    continue
                }
                // 检测apply情况
                if (reviText.count { notApply(it.third) } != 0) {
                    logger.warn("skip broken apply_dt-ed downgrade , name={}, db-revi={}, to-revi={}", plainName, plainRevi, revision)
                    continue
                }

                // 去掉终点脚本，不需要执行
                reviText.removeLast()
            }

            val plainTbls = schemaDefinitionLoader.showTables(plainDs)
            for ((revi, text) in reviText) {
                logger.info("ready for name={} revi={}", plainName, revi)
                try {
                    applyRevisionSql(revi, text, isUptoSql, commitId, plainTmpl, shardTmpl, plainTbls)
                } catch (e: Exception) {
                    logger.error("failed to exec sql revision, name=$plainName, revi=$revi", e)
                    throw e
                }
                logger.info("done for name={}, revi={}", plainName, revi)
            }

            // 后置检查
            val newRevi = getRevision(plainName, plainTmpl)
            if (revision != newRevi) {
                val msg = "failed to post check schema revision, name=$plainName, need ${revision}, but $newRevi"
                logger.error(msg)
                throw IllegalStateException(msg)
            }
        }
    }


    override fun forceApplyBreak(revision: Long, commitId: Long, isUpto: Boolean) {
        val shardTmpl = JdbcTemplate(flywaveDataSources.shard)
        for ((plainName, plainDs) in flywaveDataSources.allPlain()) {
            val plainTmpl = JdbcTemplate(plainDs)
            val reviQuery = if (isUpto) { // 升级
                """
                SELECT
                    UPTO_SQL,
                    APPLY_DT
                FROM SYS_SCHEMA_VERSION
                WHERE REVISION = $revision
                """.trimIndent()
            } else { // 降级
                """
                SELECT
                    UNDO_SQL,
                    APPLY_DT
                FROM SYS_SCHEMA_VERSION
                WHERE REVISION = $revision
                """.trimIndent()
            }

            val applySqls = LinkedList<Pair<String, String>>()

            try {
                plainTmpl.query(reviQuery) {
                    applySqls.add(Pair(it.getString(1), it.getString(2)))
                }
            } catch (e: Exception) {
                assertNot1st(plainDs, e)
                logger.warn("skip, un-init-ist, revi={}, isUpto={}, db={}", applySqls.size, revision, isUpto, plainName)
                continue
            }

            if (applySqls.size != 1) {
                logger.warn("skip, find {} sqls, revi={}, isUpto={}, db={}", applySqls.size, revision, isUpto, plainName)
                continue
            }

            val reviSql = applySqls.first
            val notAppd = notApply(reviSql.second)
            if (isUpto && !notAppd) {
                logger.error("skip, applied upto, need force to undo first, revi={}, isUpto={}, db={}", revision, isUpto, plainName)
                continue
            }

            if (!isUpto && notAppd) {
                logger.error("skip, not applied undo, revi={}, isUpto={}, db={}", revision, isUpto, plainName)
                continue
            }

            logger.info("ready, revi={}, isUpto={}, db={}", revision, isUpto, plainName)
            val plainTbls = schemaDefinitionLoader.showTables(plainDs)
            applyRevisionSql(revision, reviSql.first, isUpto, commitId, plainTmpl, shardTmpl, plainTbls)
            logger.info("done, revi={}, isUpto={}, db={}", revision, isUpto, plainName)
        }
    }

    override fun checkAndInitSql(sqls: SortedMap<Long, SchemaVersionManger.RevisionSql>, commitId: Long) {
        if (sqls.isNullOrEmpty()) {
            logger.warn("skip empty local sqls")
        }
        for ((revi, entry) in sqls) {
            val undoSql = entry.undoText
            val uptoSql = entry.uptoText
            if (undoSql.isBlank() && uptoSql.isBlank()) {
                logger.warn("skip an both empty sqls, revi={}, upto-path={}, undo-path={}", entry.revision, entry.uptoPath, entry.undoPath)
                continue
            }
            val undoMd5 = undoSql.md5()
            val uptoMd5 = uptoSql.md5()

            for ((plainName, plainDs) in flywaveDataSources.allPlain()) {
                logger.info("ready to check revi={}, on db={}", revi, plainName)
                val plainTmpl = JdbcTemplate(plainDs)
                val map = HashMap<String, String>()

                var needInit1st = false
                try {
                    plainTmpl.query("""
                    SELECT UPTO_SQL, UPTO_MD5, UNDO_SQL, UNDO_MD5, APPLY_DT
                    FROM SYS_SCHEMA_VERSION
                    WHERE REVISION = $revi
                    """.trimIndent()) {
                        map["UPTO_SQL"] = it.getString("UPTO_SQL")
                        map["UPTO_MD5"] = it.getString("UPTO_MD5")
                        map["UNDO_SQL"] = it.getString("UNDO_SQL")
                        map["UNDO_MD5"] = it.getString("UNDO_MD5")
                        map["APPLY_DT"] = it.getString("APPLY_DT")
                    }
                } catch (e: Exception) {
                    if (revi == init1stRevision) {
                        assertNot1st(plainDs, e)
                        needInit1st = true
                        logger.warn("try to init first version, revi={}, on db={}", revi, plainName)
                    } else {
                        throw e
                    }
                }

                if (needInit1st) {
                    applyRevisionSql(revi, uptoSql, true, commitId, plainTmpl, null, emptyList())
                    map["UPTO_SQL"] = uptoSql
                    map["UPTO_MD5"] = uptoMd5
                    map["UNDO_SQL"] = undoSql
                    map["UNDO_MD5"] = undoMd5
                    map["APPLY_DT"] = ""
                }

                if (map.isEmpty()) {
                    logger.info("insert for database not exist revi={}, db={}", revi, plainName)
                    val rst = plainTmpl.update("""
                    INSERT INTO SYS_SCHEMA_VERSION
                    (REVISION, CREATE_DT, COMMIT_ID, UPTO_SQL, UPTO_MD5, UNDO_SQL, UNDO_MD5)
                    VALUES(?, NOW(), ?, ?, ?, ?, ?)
                    """.trimIndent(), revi, commitId, uptoSql, uptoMd5, undoSql, undoMd5)
                    if (rst != 1) {
                        throw IllegalStateException("failed to insert revi=$revi, db=$plainName")
                    }
                } else {
                    val updSql = StringBuilder()
                    val updVal = LinkedList<Any>()
                    val applyd = map["APPLY_DT"]
                    val notAly = notApply(applyd)

                    // check undo
                    val undoDbs = map["UNDO_SQL"] as String
                    if (undoSql == undoDbs) {
                        if (undoMd5 != map["UNDO_MD5"]) {
                            updSql.append("UNDO_MD5 = ?, ")
                            updVal.add(undoMd5)
                            logger.warn("same undo-sql but diff md5, update it. revi={}, db={}", revi, plainName)
                        }
                    } else {
                        if (notAly || undoDbs.isBlank()) {
                            updSql.append("UNDO_SQL = ?, ")
                            updVal.add(undoSql)
                            updSql.append("UNDO_MD5 = ?, ")
                            updVal.add(undoMd5)
                            logger.warn("diff undo-sql, update it. revi={}, db={}", revi, plainName)
                        } else {
                            logger.error("skip diff undo-sql but applied. revi={}, db={}", revi, plainName)
                            continue
                        }
                    }

                    // check upto
                    val uptoDbs = map["UPTO_SQL"] as String
                    if (uptoSql == uptoDbs) {
                        if (uptoMd5 != map["UPTO_MD5"]) {
                            updSql.append("UPTO_MD5 = ?, ")
                            updVal.add(uptoMd5)
                            logger.warn("same upto-sql but diff md5, update it, revi={}, db={}", revi, plainName)
                        }
                    } else {
                        if (notAly || uptoDbs.isBlank()) {
                            updSql.append("UPTO_SQL = ?, ")
                            updVal.add(uptoSql)
                            updSql.append("UPTO_MD5 = ?, ")
                            updVal.add(uptoMd5)
                            logger.warn("diff upto-sql, update it to revi={}, db={}", revi, plainName)
                        } else {
                            logger.error("skip diff upto-sql but applied revi={}, db={}", revi, plainName)
                            continue
                        }
                    }

                    if (updSql.isNotEmpty()) {
                        logger.info("update diff to database revi={}, applyDt={}, db={}", revi, applyd, plainName)
                        updVal.add(commitId)
                        updVal.add(revi)
                        val rst = plainTmpl.update("""
                        UPDATE SYS_SCHEMA_VERSION SET
                            ${updSql}
                            MODIFY_DT = NOW(),
                            COMMIT_ID = ?
                        WHERE REVISION = ?
                        """.trimIndent(), *updVal.toArray())

                        if (rst != 1) {
                            throw IllegalStateException("failed to update revi=$revi, db=$plainName")
                        }
                    } else {
                        logger.info("skip both same revi={}, applyDt={}, db={}", revi, applyd, plainName)
                    }
                }
            }
        }
    }


    override fun forceUpdateSql(revision: Long, upto: String, undo: String, commitId: Long) {
        for ((plainName, plainDs) in flywaveDataSources.allPlain()) {
            logger.info("ready force update revi={}, on db={}", revision, plainName)
            val tmpl = JdbcTemplate(plainDs)

            // 不要使用msyql的REPLACE INTO，使用标准SQL
            val ext = tmpl.queryForObject("SELECT COUNT(*) FROM SYS_SCHEMA_VERSION WHERE REVISION=$revision", Int::class.java)
            if (ext == 0) {
                val rst = tmpl.update("""
                    INSERT INTO SYS_SCHEMA_VERSION
                    (REVISION, CREATE_DT, COMMIT_ID, UPTO_SQL, UPTO_MD5, UNDO_SQL, UNDO_MD5)
                    VALUES(?, NOW(), ?, ?, ?, ?, ?)
                    """.trimIndent(), revision, commitId, upto, upto.md5(), undo, undo.md5())
                logger.info("done force insert {} records, revi={}, on db={}", rst, revision, plainName)
            } else {
                val rst = tmpl.update("""
                    UPDATE SYS_SCHEMA_VERSION SET
                        UPTO_SQL = ?,
                        UPTO_MD5 = ?,
                        UNDO_SQL = ?,
                        UNDO_MD5 = ?,
                        MODIFY_DT = NOW(),
                        COMMIT_ID = ?
                    WHERE REVISION = ?
                    """.trimIndent(), upto, upto.md5(), undo, undo.md5(), commitId, revision)
                logger.info("done force update {} records, revi={}, on db={}", rst, revision, plainName)
            }
        }
    }
    //

    private fun applyRevisionSql(revi: Long, text: String, applyNow: Boolean, commitId: Long, plainTmpl: JdbcTemplate, shardTmpl: JdbcTemplate?, plainTbls: List<String>) {
        val segments = sqlSegmentProcessor.parse(sqlStatementParser, revi, text)
        for (seg in segments) {
            // 不使用事务，出错时，根据日志进行回滚或数据清理
            if (seg.isPlain || shardTmpl == null || !flywaveDataSources.isSharding) {
                logger.info("use plain to run sql-line from {} to {}", seg.lineBgn, seg.lineEnd)
                runSegment(plainTmpl, plainTbls, seg)
            } else {
                logger.info("use shard to run sql-line from {} to {}", seg.lineBgn, seg.lineEnd)
                runSegment(shardTmpl, emptyList(), seg)
            }
        }
        // update apply datetime，避免时区问题，使用SQL语法
        val applyDt = if (applyNow) {
            "NOW()"
        } else {
            "'1000-01-01'"
        }
        val cnt = try {
            plainTmpl.update("UPDATE SYS_SCHEMA_VERSION SET APPLY_DT=$applyDt, COMMIT_ID=? WHERE REVISION=?", commitId, revi)
        } catch (e: Exception) {
            assertNot1st(plainTmpl.dataSource!!, e)
            logger.warn("skip un-init-1st, revi={}, applyDt={}", revi, applyDt)
            return
        }
        // 执行了，必须一条，因为上面不会出现语法错误
        if (cnt == 1) {
            logger.info("update revi={}, applyDt={}", revi, applyDt)
        } else {
            throw IllegalStateException("update revi=$revi, but $cnt records affect")
        }
    }


    private fun getRevision(name: String, tmpl: JdbcTemplate): Long = try {
        val revi = tmpl.queryForObject("SELECT MAX(REVISION) FROM SYS_SCHEMA_VERSION WHERE APPLY_DT >'1000-01-01'", Long::class.java)
        revi ?: 0
    } catch (e: Exception) {
        assertNot1st(tmpl.dataSource!!, e)
        logger.warn("failed to get un-init-1st revision, return -1, db={}", name)
        -1
    }

    private fun runSegment(tmpl: JdbcTemplate, tables: List<String>, seg: SqlSegmentProcessor.Segment) {
        if (seg.isBlack() || tables.isEmpty() || seg.tblName.isEmpty()) {
            logger.info("run sql on direct table")
            tmpl.execute(seg.sqlText)
        } else {
            val tblName = seg.tblName
            // 包括分表和日志表
            val tblReal = tables.filter { isShadowTable(tblName, it) }.toMutableSet()
            tblReal.add(tblName) // 保证当前执行

            for (tbl in tblReal) {
                if (tbl == tblName){
                    logger.info("run sql on direct table={}", tbl)
                }else {
                    logger.info("run sql on shadow table={}", tbl)
                }
                val sql = sqlSegmentProcessor.merge(seg, tbl)
                tmpl.execute(sql)
            }
        }
    }

    private fun isShadowTable(table: String, other: String): Boolean {
        val pos = other.indexOf(table, 0, true)
        if (pos < 0) return false

        val len = pos + table.length
        if (len == other.length) return false // 自己不算

        val c = other[len]
        if (c == '$') return true
        var cnt = 0
        if (c == '_') {
            for (i in len + 1 until other.length) {
                if (other[i] in '0'..'9') {
                    cnt++
                } else {
                    return false
                }
            }
        }
        return cnt > 0
    }

    private fun notApply(str: String?): Boolean {
        if (str.isNullOrEmpty()) return true
        return str.startsWith("1000-01-01")
    }

    private fun assertNot1st(ds: DataSource, er: Exception) {
        try {
            val tables = schemaDefinitionLoader.showTables(ds)
            if (tables.find { it.equals("SYS_SCHEMA_VERSION", true) } != null) {
                throw er // 存在 SYS_SCHEMA_VERSION 表，报出原异常
            }
        } catch (e: Exception) {
            // 报出原异常
            throw er
        }
    }
}