package pro.fessional.wings.faceless.flywave

import org.slf4j.LoggerFactory
import pro.fessional.wings.faceless.flywave.SqlSegmentProcessor.Companion.TYPE_PLAIN
import pro.fessional.wings.faceless.flywave.SqlSegmentProcessor.Companion.TYPE_SHARD
import pro.fessional.wings.faceless.flywave.util.SimpleJdbcTemplate
import pro.fessional.wings.faceless.flywave.util.TemplateUtil
import java.util.LinkedList
import java.util.concurrent.atomic.AtomicReference
import javax.sql.DataSource

/**
 * 控制`SYS_SCHEMA_JOURNAL`中的`LOG_UPDATE`和`LOG_DELETE`，
 * 进而实现自动的Trigger创建和删除。
 *
 * @author trydofor
 * @since 2019-06-13
 */
class SchemaJournalManager(
        private val flywaveDataSources: FlywaveDataSources,
        private val sqlStatementParser: SqlStatementParser,
        private val sqlSegmentProcessor: SqlSegmentProcessor,
        private val schemaDefinitionLoader: SchemaDefinitionLoader
) {
    data class JournalDdl(
            var updTbl: String = "",
            var updTrg: String = "",
            var delTbl: String = "",
            var delTrg: String = ""
    )

    companion object {
        const val PLAIN_NAME = "{{PLAIN_NAME}}"
        const val TABLE_NAME = "{{TABLE_NAME}}"
        const val TABLE_BONE = "{{TABLE_BONE}}"
        const val TABLE_PKEY = "{{TABLE_PKEY}}"
    }

    private val logger = LoggerFactory.getLogger(SchemaJournalManager::class.java)

    /**
     * 根据DDL模板应用跟踪表和触发器
     * 跟踪表，如果存在，没有数据，则重建。
     * 跟踪表，如果存在，且有数据，结构相同时，忽略，否则报错。
     * 触发器，如果触发器存在，删除重建。
     * 如果跟踪表和触发器都不存在，新建。
     * @param table 主表
     * @param enable 允许或禁止
     * @param commitId 提交ID，参见Journal
     */
    fun publishUpdate(table: String, enable: Boolean, commitId: Long) =
            publishJournal(table, enable, commitId, "update")

    /**
     * 根据DDL模板应用跟踪表和触发器
     * 跟踪表，如果存在，没有数据，则重建。
     * 跟踪表，如果存在，且有数据，结构相同时，忽略，否则报错。
     * 触发器，如果触发器存在，删除重建。
     * 如果跟踪表和触发器都不存在，新建。
     * @param table 主表
     * @param enable 允许或禁止
     * @param commitId 提交ID，参见Journal
     */
    fun publishDelete(table: String, enable: Boolean, commitId: Long) =
            publishJournal(table, enable, commitId, "delete")

    /**
     * 对比本地和数据库中的SQL。
     * 当不存在时，则把本地保存到数据库。
     * 当存在但内容不一致，已APPLY则log error，否则更新
     * @param table 主表
     * @param ddl ddl模板
     * @param commitId 提交ID，参见Journal
     */
    fun checkAndInitDdl(table: String, ddl: JournalDdl, commitId: Long) {
        logger.info("start check journal table={}", table)
        val selectSql = """
                SELECT DDL_UPDTBL, DDL_UPDTRG, DDL_DELTBL, DDL_DELTRG, LOG_UPDATE, LOG_DELETE
                FROM SYS_SCHEMA_JOURNAL
                WHERE TABLE_NAME = ?
                """.trimIndent()
        val insertSql = """
                INSERT INTO SYS_SCHEMA_JOURNAL
                (TABLE_NAME, COMMIT_ID, DDL_UPDTBL, DDL_UPDTRG, DDL_DELTBL, DDL_DELTRG)
                VALUES (?, ?, ?, ?, ?, ?)
                """.trimIndent()

        for ((plainName, plainDs) in flywaveDataSources.plains()) {
            logger.info("ready to check journal, table={} on db={}", table, plainName)
            val tmpl = SimpleJdbcTemplate(plainDs, plainName)
            val dbVal = HashMap<String, String>()
            tmpl.query(selectSql, table) {
                dbVal["DDL_UPDTBL"] = it.getString("DDL_UPDTBL")
                dbVal["DDL_UPDTRG"] = it.getString("DDL_UPDTRG")
                dbVal["DDL_DELTBL"] = it.getString("DDL_DELTBL")
                dbVal["DDL_DELTRG"] = it.getString("DDL_DELTRG")
                dbVal["LOG_UPDATE"] = it.getString("LOG_UPDATE")
                dbVal["LOG_DELETE"] = it.getString("LOG_DELETE")
            }

            if (dbVal.isEmpty()) {
                logger.info("insert journal ddl, table=$table, db=$plainName")
                val rst = tmpl.update(insertSql, table, commitId, ddl.updTbl, ddl.updTrg, ddl.delTbl, ddl.delTrg)
                if (rst != 1) {
                    throw IllegalStateException("failed to insert journal ddl, table=$table, db=$plainName")
                }
                continue
            }

            // check
            val updSql = StringBuilder()
            val updVal = LinkedList<Any>()
            val updNot = notApply(dbVal["LOG_UPDATE"])
            val delNot = notApply(dbVal["LOG_DELETE"])

            if (ddl.updTbl != dbVal["DDL_UPDTBL"]) {
                if (updNot) {
                    updSql.append("DDL_UPDTBL = ?, ")
                    updVal.add(ddl.updTbl)
                    logger.warn("diff ddl-upd-tbl, update it. table={}, db={}", table, plainName)
                } else {
                    logger.error("skip diff ddl-upd-tbl but applied, should manually disable it first. table={}, db={}", table, plainName)
                    continue
                }
            }

            if (ddl.updTrg != dbVal["DDL_UPDTRG"]) {
                if (updNot) {
                    updSql.append("DDL_UPDTRG = ?, ")
                    updVal.add(ddl.updTrg)
                    logger.warn("diff ddl-upd-trg, update it. table={}, db={}", table, plainName)
                } else {
                    logger.error("skip diff ddl-upd-trg but applied, should manually disable it first. table={}, db={}", table, plainName)
                    continue
                }
            }

            if (ddl.delTbl != dbVal["DDL_DELTBL"]) {
                if (delNot) {
                    updSql.append("DDL_DELTBL = ?, ")
                    updVal.add(ddl.delTbl)
                    logger.warn("diff ddl-del-tbl, update it. table={}, db={}", table, plainName)
                } else {
                    logger.error("skip diff ddl-del-tbl but applied, should manually disable it first. table={}, db={}", table, plainName)
                    continue
                }
            }
            if (ddl.delTrg != dbVal["DDL_DELTRG"]) {
                if (delNot) {
                    updSql.append("DDL_DELTRG = ?, ")
                    updVal.add(ddl.delTrg)
                    logger.warn("diff ddl-del-trg, update it. table={}, db={}", table, plainName)
                } else {
                    logger.error("skip diff ddl-del-trg but applied, should manually disable it first. table={}, db={}", table, plainName)
                    continue
                }
            }

            // update
            if (updSql.isNotEmpty()) {
                logger.info("update diff journal to database table={}, db={}", table, plainName)
                updVal.add(commitId)
                updVal.add(table)
                val rst = tmpl.update("""
                        UPDATE SYS_SCHEMA_JOURNAL SET
                            $updSql
                            MODIFY_DT = NOW(),
                            COMMIT_ID = ?
                        WHERE TABLE_NAME = ?
                        """.trimIndent(), *updVal.toArray())
                if (rst != 1) {
                    throw IllegalStateException("failed to update table=$table, db=$plainName")
                }
            } else {
                logger.info("skip all same journal, table={}, db={}", table, plainName)
            }
        }
        logger.info("done check journal table={}", table)
    }

    private fun publishJournal(table: String, enable: Boolean, commitId: Long, event: String) {
        logger.info("start publish {} table={}, enable={}", event, table, enable)

        val isUpdate = "update".equals(event, true)
        val selectSql = if (isUpdate) {
            """
            SELECT
                DDL_UPDTBL DDL_TBL,
                DDL_UPDTRG DDL_TRG,
                LOG_UPDATE APPLY_DT
            FROM SYS_SCHEMA_JOURNAL
            WHERE TABLE_NAME = ?
            """.trimIndent()
        } else {
            """
            SELECT
                DDL_DELTBL DDL_TBL,
                DDL_DELTRG DDL_TRG,
                LOG_DELETE APPLY_DT
            FROM SYS_SCHEMA_JOURNAL
            WHERE TABLE_NAME = ?
            """.trimIndent()
        }

        val updateSql = if (isUpdate) {
            """
            UPDATE SYS_SCHEMA_JOURNAL SET
                LOG_UPDATE = NOW(),
                COMMIT_ID = ?
            WHERE TABLE_NAME = ?
            """.trimIndent()
        } else {
            """
            UPDATE SYS_SCHEMA_JOURNAL SET
                LOG_DELETE = NOW(),
                COMMIT_ID = ?
            WHERE TABLE_NAME = ?
            """.trimIndent()
        }

        val model = HashMap<String, String>()
        for ((plainName, plainDs) in flywaveDataSources.plains()) {
            logger.info("ready to publish {} table={}, enable={}, db={}", event, table, enable, plainName)
            val tmpl = SimpleJdbcTemplate(plainDs, plainName)
            val vals = AtomicReference<Triple<String, String, String>>()
            tmpl.query(selectSql, table) {
                vals.set(Triple(it.getString("DDL_TBL"),
                        it.getString("DDL_TRG"),
                        it.getString("APPLY_DT")
                ))
            }

            if (vals.get() == null) {
                logger.error("skip template not found, table={}, db={}", table, plainName)
                continue
            }

            val (tmplTbl, tmplTrg, applyDt) = vals.get()
            if (tmplTbl.isBlank() || tmplTrg.isBlank()) {
                logger.error("skip blank template,apply={} table={}, db={}", table, plainName)
                continue
            }

            val tables = schemaDefinitionLoader.showTables(plainDs).map {
                it.toLowerCase() to it
            }.toMap()

            val staffs = tables.filter {
                val tp = sqlSegmentProcessor.hasType(table, it.value)
                tp == TYPE_SHARD || tp == TYPE_PLAIN
            }.toMap()

            logger.info("init model, applyDt={} table={}, enable={}, db={}", applyDt, table, enable, plainName)
            initModelOnce(table, plainDs, model)

            val trcStf = HashMap<String, String>()
            val trcDdl = HashMap<String, String>()
            val trgDdl = HashMap<String, String>()
            val drpTbl = HashMap<String, String>()

            val tmpTkn = "___temp_fw79"
            val tmpTrc = "$table$tmpTkn"

            // clean temp table
            for ((_, tblRaw) in tables) {
                if (tblRaw.contains(tmpTkn, true)) {
                    logger.info("drop temp table table={}, db={}", tblRaw, plainName)
                    tmpl.execute("DROP TABLE IF EXISTS ${sqlStatementParser.safeName(tblRaw)}")
                }
            }

            // 跟踪表，删除存在的，非空的
            for ((_, tblRaw) in staffs) {
                val ddlTbl = mergeDdl(tmplTbl, model, tblRaw)
                val ddlTrg = mergeDdl(tmplTrg, model, tblRaw)

                val curTac = parseTblName(ddlTbl)
                if (curTac.isBlank()) {
                    logger.warn("skip bad table={}, trace-table-ddl ={}", tblRaw, ddlTbl)
                    continue
                }

                // 检查跟踪表
                val safeCurTrc = sqlStatementParser.safeName(curTac)
                var notOld = true
                var drpOld = false
                if (tables.containsKey(curTac.toLowerCase())) {
                    logger.info("existed trace-table={}, table={}, db={}", curTac, tblRaw, plainName)
                    val cnt = tmpl.count("SELECT COUNT(1) FROM $safeCurTrc")
                    if (cnt == 0) {
                        drpOld = true
                        drpTbl["DROP TABLE IF EXISTS $safeCurTrc"] = tblRaw
                    } else {
                        logger.warn("lazy-check existed {} records trace-table={}, table={}, db={}", cnt, curTac, tblRaw, plainName)
                        trcStf[curTac] = tblRaw
                        notOld = false
                    }
                }

                if (notOld) {
                    trcDdl[ddlTbl] = tblRaw
                }

                // 检查触发器，删除同名或关联表的
                val furTrg = parseTrgName(ddlTrg) // 名字
                for ((trg, evt) in schemaDefinitionLoader.showBoneTrg(plainDs, tblRaw)) {
                    if (drpOld && TemplateUtil.isBoundary(evt, curTac, false)) {
                        logger.warn("drop trigger={}, dropped trace-table={}, table={}, db={}", curTac, trg, tblRaw, plainName)
                    } else if (furTrg.isNotEmpty() && furTrg.equals(trg, true)) {
                        logger.warn("drop trigger={}, existed same name, table={}, db={}", trg, tblRaw, plainName)
                    } else {
                        continue
                    }
                    tmpl.execute("DROP TRIGGER IF EXISTS ${sqlStatementParser.safeName(trg)}")
                }

                trgDdl[ddlTrg] = tblRaw
            }

            // 检测已存在的，所有跟踪表应该结构一致
            if (trcStf.isNotEmpty()) {
                val tmpDdl = mergeDdl(tmplTbl, model, tmpTrc)
                val tmpTbl = parseTblName(tmpDdl)
                val safeTmp = sqlStatementParser.safeName(tmpTbl)

                tmpl.execute(TemplateUtil.replace(tmpDdl, tmpTbl, tmpTrc))
                logger.info("create temp-trace-table={}, db={}", tmpTbl, plainName)
                try {
                    var isSame = true
                    for ((trc, stf) in trcStf) {
                        val df = schemaDefinitionLoader.diffAllSame(plainDs, tmpTbl, trc)
                        if (df.isNotEmpty()) {
                            isSame = false
                            logger.error("different trace-table={} of staff={}, error={}", trc, stf, df)
                        }
                    }
                    if (isSame) {
                        logger.info("existed traces all the same table={}, db={}", table, plainName)
                    } else {
                        logger.error("need manually check the different traces, table={}, db={}", table, plainName)
                        continue
                    }
                } finally {
                    // 如创建，则删除
                    tmpl.execute("DROP TABLE IF EXISTS $safeTmp")
                    logger.info("drop temp-trace-table={}, db={}", tmpTbl, plainName)
                }
            }

            for ((ddl, tbl) in drpTbl) {
                logger.warn("drop trace-table={}, empty existed, table={}, db={}", tbl, table, plainName)
                tmpl.execute(ddl)
            }

            if (enable) {
                logger.info("execute enable journal, plain-table={}, db={}", table, plainName)
                for ((ddl, tbl) in trcDdl) {
                    logger.info("execute trace-table ddl on table={}, db={}", tbl, plainName)
                    tmpl.execute(ddl)
                }
                for ((ddl, tbl) in trgDdl) {
                    logger.info("execute trigger ddl on table={}, db={}", tbl, plainName)
                    tmpl.execute(ddl)
                }
            } else {
                logger.info("execute disable journal, plain-table={}, db={}", table, plainName)
            }

            // 更新状态
            val rst = tmpl.update(updateSql, commitId, table)
            if (rst != 1) {
                throw IllegalStateException("update journal $rst records, table=$table, db=$plainName")
            }
        }

        logger.info("done publish {} table={}, enable={}", event, table, enable)
    }

    private fun initModelOnce(table: String, ds: DataSource, map: HashMap<String, String>) {
        if (map.isNotEmpty()) {
            return
        }

        map[PLAIN_NAME] = table
        val bone = schemaDefinitionLoader.showBoneCol(ds, table).joinToString(",\n")
        map[TABLE_BONE] = bone
        val keys = schemaDefinitionLoader.showPkeyCol(ds, table).joinToString()
        map[TABLE_PKEY] = keys

        logger.info("init model table={}, keys={}, bone={}", table, keys, bone)
    }

    private fun mergeDdl(ddl: String, map: HashMap<String, String>, table: String): String {
        val tkn = listOf(PLAIN_NAME, TABLE_NAME, TABLE_BONE, TABLE_PKEY)
        val idx = TemplateUtil.parse(ddl, tkn)

        map[TABLE_NAME] = table
        return TemplateUtil.merge(ddl, idx, map)
    }

    private fun notApply(str: String?): Boolean {
        if (str.isNullOrEmpty()) return true
        return str.startsWith("1000-01-01")
    }

    val trgNameRegex = """\s+TRIGGER\s+[`'"]*(\S+)[`'"]*"""
            .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))

    private fun parseTrgName(ddl: String): String {
        return trgNameRegex.find(ddl)?.groupValues?.get(1) ?: "" // 名字
    }

    private fun parseTblName(ddl: String) = when (val st = sqlStatementParser.parseTypeAndTable(ddl)) {
        is SqlStatementParser.SqlType.Plain -> st.table
        is SqlStatementParser.SqlType.Shard -> st.table
        else -> ""
    }
}
