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
 * 控制`sys_schema_journal`中的`log_update`和`log_delete`，
 * 进而实现自动的Trigger创建和删除。
 *
 * @author trydofor
 * @since 2019-06-13
 */
class SchemaJournalManager(
        private val flywaveDataSources: FlywaveDataSources,
        private val sqlStatementParser: SqlStatementParser,
        private val sqlSegmentProcessor: SqlSegmentProcessor,
        private val schemaDefinitionLoader: SchemaDefinitionLoader,
        private val journalDdl: JournalDdl
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
     * @param commitId 提交ID，参见Journal
     */
    fun checkAndInitDdl(table: String, commitId: Long) {
        logger.info("[checkAndInitDdl] start check journal table={}", table)
        val selectSql = """
                SELECT ddl_updtbl, ddl_updtrg, ddl_deltbl, ddl_deltrg, log_update, log_delete
                FROM sys_schema_journal
                WHERE table_name = ?
                """.trimIndent()
        val insertSql = """
                INSERT INTO sys_schema_journal
                (table_name, commit_id, ddl_updtbl, ddl_updtrg, ddl_deltbl, ddl_deltrg)
                VALUES (?, ?, ?, ?, ?, ?)
                """.trimIndent()

        for ((plainName, plainDs) in flywaveDataSources.plains()) {
            logger.info("[checkAndInitDdl] ready to check journal, table={} on db={}", table, plainName)
            val tmpl = SimpleJdbcTemplate(plainDs, plainName)
            val dbVal = HashMap<String, String>()
            tmpl.query(selectSql, table) {
                dbVal["ddl_updtbl"] = it.getString("ddl_updtbl")
                dbVal["ddl_updtrg"] = it.getString("ddl_updtrg")
                dbVal["ddl_deltbl"] = it.getString("ddl_deltbl")
                dbVal["ddl_deltrg"] = it.getString("ddl_deltrg")
                dbVal["log_update"] = it.getString("log_update")
                dbVal["log_delete"] = it.getString("log_delete")
            }

            if (dbVal.isEmpty()) {
                logger.info("[checkAndInitDdl] insert journal ddl, table=$table, db=$plainName")
                val rst = tmpl.update(insertSql, table, commitId, journalDdl.updTbl, journalDdl.updTrg, journalDdl.delTbl, journalDdl.delTrg)
                if (rst != 1) {
                    throw IllegalStateException("failed to insert journal ddl, table=$table, db=$plainName")
                }
                continue
            }

            // check
            val updSql = StringBuilder()
            val updVal = LinkedList<Any>()
            val updNot = notApply(dbVal["log_update"])
            val delNot = notApply(dbVal["log_delete"])

            if (journalDdl.updTbl != dbVal["ddl_updtbl"]) {
                if (updNot) {
                    updSql.append("ddl_updtbl = ?, ")
                    updVal.add(journalDdl.updTbl)
                    logger.warn("[checkAndInitDdl] diff ddl-upd-tbl, update it. table={}, db={}", table, plainName)
                } else {
                    logger.error("[checkAndInitDdl] skip diff ddl-upd-tbl but applied, should manually disable it first. table={}, db={}", table, plainName)
                    continue
                }
            }

            if (journalDdl.updTrg != dbVal["ddl_updtrg"]) {
                if (updNot) {
                    updSql.append("ddl_updtrg = ?, ")
                    updVal.add(journalDdl.updTrg)
                    logger.warn("[checkAndInitDdl] diff ddl-upd-trg, update it. table={}, db={}", table, plainName)
                } else {
                    logger.error("[checkAndInitDdl] skip diff ddl-upd-trg but applied, should manually disable it first. table={}, db={}", table, plainName)
                    continue
                }
            }

            if (journalDdl.delTbl != dbVal["ddl_deltbl"]) {
                if (delNot) {
                    updSql.append("ddl_deltbl = ?, ")
                    updVal.add(journalDdl.delTbl)
                    logger.warn("[checkAndInitDdl] diff ddl-del-tbl, update it. table={}, db={}", table, plainName)
                } else {
                    logger.error("[checkAndInitDdl] skip diff ddl-del-tbl but applied, should manually disable it first. table={}, db={}", table, plainName)
                    continue
                }
            }
            if (journalDdl.delTrg != dbVal["ddl_deltrg"]) {
                if (delNot) {
                    updSql.append("ddl_deltrg = ?, ")
                    updVal.add(journalDdl.delTrg)
                    logger.warn("[checkAndInitDdl] diff ddl-del-trg, update it. table={}, db={}", table, plainName)
                } else {
                    logger.error("[checkAndInitDdl] skip diff ddl-del-trg but applied, should manually disable it first. table={}, db={}", table, plainName)
                    continue
                }
            }

            // update
            if (updSql.isNotEmpty()) {
                logger.info("[checkAndInitDdl] update diff journal to database table={}, db={}", table, plainName)
                updVal.add(commitId)
                updVal.add(table)
                val rst = tmpl.update("""
                        UPDATE sys_schema_journal SET
                            $updSql
                            modify_dt = NOW(),
                            commit_id = ?
                        WHERE table_name = ?
                        """.trimIndent(), *updVal.toArray())
                if (rst != 1) {
                    throw IllegalStateException("failed to update table=$table, db=$plainName")
                }
            } else {
                logger.info("[checkAndInitDdl] skip all same journal, table={}, db={}", table, plainName)
            }
        }
        logger.info("[checkAndInitDdl] done check journal table={}", table)
    }

    private fun publishJournal(table: String, enable: Boolean, commitId: Long, event: String) {
        logger.info("[publishJournal] start publish {} table={}, enable={}", event, table, enable)

        val isUpdate = "update".equals(event, true)
        val selectSql = if (isUpdate) {
            """
            SELECT
                ddl_updtbl ddl_tbl,
                ddl_updtrg ddl_trg,
                log_update apply_dt
            FROM sys_schema_journal
            WHERE table_name = ?
            """.trimIndent()
        } else {
            """
            SELECT
                ddl_deltbl ddl_tbl,
                ddl_deltrg ddl_trg,
                log_delete apply_dt
            FROM sys_schema_journal
            WHERE table_name = ?
            """.trimIndent()
        }

        val updateSql = if (isUpdate) {
            """
            UPDATE sys_schema_journal SET
                log_update = NOW(),
                commit_id = ?
            WHERE table_name = ?
            """.trimIndent()
        } else {
            """
            UPDATE sys_schema_journal SET
                log_delete = NOW(),
                commit_id = ?
            WHERE table_name = ?
            """.trimIndent()
        }

        val model = HashMap<String, String>()
        for ((plainName, plainDs) in flywaveDataSources.plains()) {
            logger.info("[publishJournal] ready to publish {} table={}, enable={}, db={}", event, table, enable, plainName)
            val tmpl = SimpleJdbcTemplate(plainDs, plainName)
            val vals = AtomicReference<Triple<String, String, String>>()
            tmpl.query(selectSql, table) {
                vals.set(Triple(it.getString("ddl_tbl"),
                        it.getString("ddl_trg"),
                        it.getString("apply_dt")
                ))
            }

            if (vals.get() == null) {
                logger.error("[publishJournal] skip template not found, table={}, db={}", table, plainName)
                continue
            }

            val (tmplTbl, tmplTrg, applyDt) = vals.get()
            if (tmplTbl.isBlank() || tmplTrg.isBlank()) {
                logger.error("[publishJournal] skip blank template,apply={} table={}, db={}", table, plainName)
                continue
            }

            val tables = schemaDefinitionLoader.showTables(plainDs).map {
                it.toLowerCase() to it
            }.toMap()

            val staffs = tables.filter {
                val tp = sqlSegmentProcessor.hasType(table, it.value)
                tp == TYPE_SHARD || tp == TYPE_PLAIN
            }.toMap()

            logger.info("[publishJournal] init model, applyDt={} table={}, enable={}, db={}", applyDt, table, enable, plainName)
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
                    logger.info("[publishJournal] drop temp table table={}, db={}", tblRaw, plainName)
                    tmpl.execute("DROP TABLE IF EXISTS ${sqlStatementParser.safeName(tblRaw)}")
                }
            }

            // 跟踪表，删除存在的，非空的
            for ((_, tblRaw) in staffs) {
                val ddlTbl = mergeDdl(tmplTbl, model, tblRaw)
                val ddlTrg = mergeDdl(tmplTrg, model, tblRaw)

                val curTac = parseTblName(ddlTbl)
                if (curTac.isBlank()) {
                    logger.warn("[publishJournal] skip bad table={}, trace-table-ddl ={}", tblRaw, ddlTbl)
                    continue
                }

                // 检查跟踪表
                val safeCurTrc = sqlStatementParser.safeName(curTac)
                var notOld = true
                var drpOld = false
                if (tables.containsKey(curTac.toLowerCase())) {
                    logger.info("[publishJournal] existed trace-table={}, table={}, db={}", curTac, tblRaw, plainName)
                    val cnt = tmpl.count("SELECT COUNT(1) FROM $safeCurTrc")
                    if (cnt == 0) {
                        drpOld = true
                        drpTbl["DROP TABLE IF EXISTS $safeCurTrc"] = tblRaw
                    } else {
                        logger.warn("[publishJournal] lazy-check existed {} records trace-table={}, table={}, db={}", cnt, curTac, tblRaw, plainName)
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
                        logger.warn("[publishJournal] drop trigger={}, dropped trace-table={}, table={}, db={}", curTac, trg, tblRaw, plainName)
                    } else if (furTrg.isNotEmpty() && furTrg.equals(trg, true)) {
                        logger.warn("[publishJournal] drop trigger={}, existed same name, table={}, db={}", trg, tblRaw, plainName)
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
                logger.info("[publishJournal] create temp-trace-table={}, db={}", tmpTbl, plainName)
                try {
                    var isSame = true
                    for ((trc, stf) in trcStf) {
                        val df = schemaDefinitionLoader.diffAllSame(plainDs, tmpTbl, trc)
                        if (df.isNotEmpty()) {
                            isSame = false
                            logger.error("[publishJournal] different trace-table={} of staff={}, error={}", trc, stf, df)
                        }
                    }
                    if (isSame) {
                        logger.info("[publishJournal] existed traces all the same table={}, db={}", table, plainName)
                    } else {
                        logger.error("[publishJournal] need manually check the different traces, table={}, db={}", table, plainName)
                        continue
                    }
                } finally {
                    // 如创建，则删除
                    tmpl.execute("DROP TABLE IF EXISTS $safeTmp")
                    logger.info("[publishJournal] drop temp-trace-table={}, db={}", tmpTbl, plainName)
                }
            }

            for ((ddl, tbl) in drpTbl) {
                logger.warn("[publishJournal] drop trace-table={}, empty existed, table={}, db={}", tbl, table, plainName)
                tmpl.execute(ddl)
            }

            if (enable) {
                logger.info("[publishJournal] execute enable journal, plain-table={}, db={}", table, plainName)
                for ((ddl, tbl) in trcDdl) {
                    logger.info("[publishJournal] execute trace-table ddl on table={}, db={}", tbl, plainName)
                    tmpl.execute(ddl)
                }
                for ((ddl, tbl) in trgDdl) {
                    logger.info("[publishJournal] execute trigger ddl on table={}, db={}", tbl, plainName)
                    tmpl.execute(ddl)
                }
            } else {
                logger.info("[publishJournal] execute disable journal, plain-table={}, db={}", table, plainName)
            }

            // 更新状态
            val rst = tmpl.update(updateSql, commitId, table)
            if (rst != 1) {
                throw IllegalStateException("update journal $rst records, table=$table, db=$plainName")
            }
        }

        logger.info("[publishJournal] done publish {} table={}, enable={}", event, table, enable)
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

        logger.info("[initModelOnce] init model table={}, keys={}, bone={}", table, keys, bone)
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
