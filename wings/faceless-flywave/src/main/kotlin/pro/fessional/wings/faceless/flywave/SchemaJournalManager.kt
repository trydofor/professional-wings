package pro.fessional.wings.faceless.flywave

import org.slf4j.LoggerFactory
import org.slf4j.event.Level.ERROR
import org.slf4j.event.Level.INFO
import org.slf4j.event.Level.WARN
import pro.fessional.mirana.data.Null
import pro.fessional.wings.faceless.flywave.SqlSegmentProcessor.Companion.TYPE_PLAIN
import pro.fessional.wings.faceless.flywave.SqlSegmentProcessor.Companion.TYPE_SHARD
import pro.fessional.wings.faceless.flywave.SqlSegmentProcessor.Companion.hasType
import pro.fessional.wings.faceless.flywave.impl.DefaultInteractiveManager
import pro.fessional.wings.faceless.flywave.util.SimpleJdbcTemplate
import pro.fessional.wings.faceless.flywave.util.TemplateUtil
import java.util.LinkedList
import java.util.concurrent.atomic.AtomicReference
import java.util.function.BiConsumer
import java.util.function.Function
import javax.sql.DataSource

/**
 * 控制`$schemaJournalTable`中的`log_update`和`log_delete`，
 * 进而实现自动的Trigger创建和删除。
 *
 * @author trydofor
 * @since 2019-06-13
 */
class SchemaJournalManager(
    private val plainDataSources: Map<String, DataSource>,
    private val sqlStatementParser: SqlStatementParser,
    private val schemaDefinitionLoader: SchemaDefinitionLoader,
    private val journalDdl: JournalDdl,
    private val schemaJournalTable: String = "sys_schema_journal"
) : InteractiveManager<SchemaJournalManager.AskType> {

    enum class AskType {
        DropTable, DropTrigger, ManualCheck
    }

    data class JournalDdl(
        var insTbl: String = Null.Str,
        var insTrg: String = Null.Str,
        var updTbl: String = Null.Str,
        var updTrg: String = Null.Str,
        var delTbl: String = Null.Str,
        var delTrg: String = Null.Str
    )

    companion object {
        const val PLAIN_NAME = "{{PLAIN_NAME}}"
        const val TABLE_NAME = "{{TABLE_NAME}}"
        const val TABLE_BONE = "{{TABLE_BONE}}"
        const val TABLE_PKEY = "{{TABLE_PKEY}}"
    }

    private val log = LoggerFactory.getLogger(SchemaJournalManager::class.java)
    private val interactive = DefaultInteractiveManager<AskType>(log, plainDataSources, "🐶")

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
     * 根据DDL模板应用跟踪表和触发器
     * 跟踪表，如果存在，没有数据，则重建。
     * 跟踪表，如果存在，且有数据，结构相同时，忽略，否则报错。
     * 触发器，如果触发器存在，删除重建。
     * 如果跟踪表和触发器都不存在，新建。
     * @param table 主表
     * @param enable 允许或禁止
     * @param commitId 提交ID，参见Journal
     */
    fun publishInsert(table: String, enable: Boolean, commitId: Long) =
        publishJournal(table, enable, commitId, "insert")

    /**
     * 检查所有trigger，可以选择是否删除
     * @param table 主表
     * @param drop 是否询问删除，默认false
     */
    fun manageTriggers(table: String, drop: Boolean = false) {
        val here = "manageTriggers"
        interactive.log(INFO, here, "start check triggers on table=$table")
        for ((plainName, plainDs) in plainDataSources) {
            interactive.log(INFO, here, "ready to check triggers, table=$table on db=$plainName")
            val tgs = schemaDefinitionLoader.showBoneTrg(plainDs, table)
            interactive.log(INFO, here, "find ${tgs.size} triggers, table=$table on db=$plainName")
            val tmpl = SimpleJdbcTemplate(plainDs, plainName)
            for (trg in tgs) {
                val msg = "${trg.name} ${trg.timing} ${trg.action}\n${trg.event}"
                interactive.log(INFO, here, msg)
                if (drop && interactive.ask("drop trigger?\n$msg", false)) {
                    tmpl.execute(schemaDefinitionLoader.makeDdlTrg(trg, true))
                }
            }
        }
    }

    /**
     * 对比本地和数据库中的SQL。
     * 当不存在时，则把本地保存到数据库。
     * 当存在但内容不一致，已APPLY则log error，否则更新
     * @param table 主表
     * @param commitId 提交ID，参见Journal
     */
    fun checkAndInitDdl(table: String, commitId: Long) {
        val here = "checkAndInitDdl"
        interactive.log(INFO, here, "start check journal table=$table")
        val selectSql = """
                SELECT ddl_instbl, ddl_instrg, ddl_updtbl, ddl_updtrg, ddl_deltbl, ddl_deltrg, log_insert, log_update, log_delete
                FROM $schemaJournalTable
                WHERE table_name = ?
                """.trimIndent()
        val insertSql = """
                INSERT INTO $schemaJournalTable
                (table_name, commit_id, ddl_instbl, ddl_instrg, ddl_updtbl, ddl_updtrg, ddl_deltbl, ddl_deltrg)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()

        for ((plainName, plainDs) in plainDataSources) {
            interactive.log(INFO, here, "ready to check journal, table=$table on db=$plainName")
            val tables = schemaDefinitionLoader.showTables(plainDs).associateBy {
                it.lowercase()
            }

            if (!tables.containsKey(table.lowercase())) {
                throw IllegalArgumentException("table not existed. table=$table")
            }

            val tmpl = SimpleJdbcTemplate(plainDs, plainName)
            val dbVal = HashMap<String, String>()
            tmpl.query(selectSql, table) {
                dbVal["ddl_instbl"] = it.getString("ddl_instbl")
                dbVal["ddl_instrg"] = it.getString("ddl_instrg")
                dbVal["ddl_updtbl"] = it.getString("ddl_updtbl")
                dbVal["ddl_updtrg"] = it.getString("ddl_updtrg")
                dbVal["ddl_deltbl"] = it.getString("ddl_deltbl")
                dbVal["ddl_deltrg"] = it.getString("ddl_deltrg")
                dbVal["log_insert"] = it.getString("log_insert")
                dbVal["log_update"] = it.getString("log_update")
                dbVal["log_delete"] = it.getString("log_delete")
            }

            if (dbVal.isEmpty()) {
                interactive.log(INFO, here, "insert journal ddl, table=$table, db=$plainName")
                val rst = tmpl.update(insertSql, table, commitId, journalDdl.insTbl, journalDdl.insTrg, journalDdl.updTbl, journalDdl.updTrg, journalDdl.delTbl, journalDdl.delTrg)
                if (rst != 1) {
                    throw IllegalStateException("failed to insert journal ddl, table=$table, db=$plainName")
                }
                continue
            }

            // check
            val updSql = StringBuilder()
            val updVal = LinkedList<Any>()
            val badDif = StringBuilder()
            val insNot = notApply(dbVal["log_insert"])
            if (journalDdl.insTbl != dbVal["ddl_instbl"]) {
                if (insNot) {
                    interactive.log(WARN, here, "diff ddl-ins-tbl, update it. table=$table, db=$plainName")
                } else {
                    badDif.append("\ninsert-tracer")
                    interactive.log(WARN, here, "diff applied ddl-ins-tbl, should manually disable it first. table=$table, db=$plainName")
                    interactive.log(INFO, here, dbVal["ddl_instbl"] ?: "")
                    interactive.log(INFO, here, journalDdl.insTbl)
                    if (interactive.needAsk(AskType.ManualCheck)) {
                        interactive.ask("continue?\nupdate diff applied insert-tracer. table=$table")
                    }
                }
                updSql.append("ddl_instbl = ?, ")
                updVal.add(journalDdl.insTbl)
            }
            if (journalDdl.insTrg != dbVal["ddl_instrg"]) {
                if (insNot) {
                    interactive.log(WARN, here, "diff ddl-ins-trg, update it. table=$table, db=$plainName")
                } else {
                    badDif.append("\ninsert-trigger")
                    interactive.log(WARN, here, "diff applied ddl-ins-trg, should manually disable it first. table=$table, db=$plainName")
                    interactive.log(INFO, here, dbVal["ddl_instrg"] ?: "")
                    interactive.log(INFO, here, journalDdl.insTrg)
                    if (interactive.needAsk(AskType.ManualCheck)) {
                        interactive.ask("continue?\nupdate diff applied insert-trigger. table=$table")
                    }
                }
                updSql.append("ddl_instrg = ?, ")
                updVal.add(journalDdl.insTrg)
            }

            val updNot = notApply(dbVal["log_update"])
            if (journalDdl.updTbl != dbVal["ddl_updtbl"]) {
                if (updNot) {
                    interactive.log(WARN, here, "diff ddl-upd-tbl, update it. table=$table, db=$plainName")
                } else {
                    badDif.append("\nupdate-tracer")
                    interactive.log(WARN, here, "diff applied ddl-upd-tbl, should manually disable it first. table=$table, db=$plainName")
                    interactive.log(INFO, here, dbVal["ddl_updtbl"] ?: "")
                    interactive.log(INFO, here, journalDdl.updTbl)
                    if (interactive.needAsk(AskType.ManualCheck)) {
                        interactive.ask("continue?\nupdate diff applied update-tracer. table=$table")
                    }
                }
                updSql.append("ddl_updtbl = ?, ")
                updVal.add(journalDdl.updTbl)
            }

            if (journalDdl.updTrg != dbVal["ddl_updtrg"]) {
                if (updNot) {
                    interactive.log(WARN, here, "diff ddl-upd-trg, update it. table=$table, db=$plainName")
                } else {
                    badDif.append("\nupdate-trigger")
                    interactive.log(WARN, here, "diff applied ddl-upd-trg, should manually disable it first. table=$table, db=$plainName")
                    interactive.log(INFO, here, dbVal["ddl_updtrg"] ?: "")
                    interactive.log(INFO, here, journalDdl.updTrg)
                    if (interactive.needAsk(AskType.ManualCheck)) {
                        interactive.ask("continue?\nupdate diff applied update-trigger. table=$table")
                    }
                }
                updSql.append("ddl_updtrg = ?, ")
                updVal.add(journalDdl.updTrg)
            }

            val delNot = notApply(dbVal["log_delete"])
            if (journalDdl.delTbl != dbVal["ddl_deltbl"]) {
                if (delNot) {
                    interactive.log(WARN, here, "diff ddl-del-tbl, update it. table=$table, db=$plainName")
                } else {
                    badDif.append("\ndelete-tracer")
                    interactive.log(WARN, here, "diff applied ddl-del-tbl, should manually disable it first. table=$table, db=$plainName")
                    interactive.log(INFO, here, dbVal["ddl_deltbl"] ?: "")
                    interactive.log(INFO, here, journalDdl.delTbl)
                    if (interactive.needAsk(AskType.ManualCheck)) {
                        interactive.ask("continue?\nupdate diff applied delete-tracer. table=$table")
                    }
                }
                updSql.append("ddl_deltbl = ?, ")
                updVal.add(journalDdl.delTbl)
            }
            if (journalDdl.delTrg != dbVal["ddl_deltrg"]) {
                if (delNot) {
                    interactive.log(WARN, here, "diff ddl-del-trg, update it. table=$table, db=$plainName")
                } else {
                    badDif.append("\ndelete-trigger")
                    interactive.log(WARN, here, "diff applied ddl-del-trg, should manually disable it first. table=$table, db=$plainName")
                    interactive.log(INFO, here, dbVal["ddl_deltrg"] ?: "")
                    interactive.log(INFO, here, journalDdl.delTrg)
                    if (interactive.needAsk(AskType.ManualCheck)) {
                        interactive.ask("continue?\nupdate diff applied delete-trigger. table=$table")
                    }
                }
                updSql.append("ddl_deltrg = ?, ")
                updVal.add(journalDdl.delTrg)
            }

            // update
            if (updSql.isNotEmpty()) {
                if (badDif.isNotEmpty() && interactive.needAsk(AskType.ManualCheck)) {
                    interactive.ask("continue?\ntable=$table $badDif")
                }

                updVal.add(commitId)
                updVal.add(table)
                val sql = """
                        UPDATE $schemaJournalTable SET
                            $updSql
                            modify_dt = NOW(3),
                            commit_id = ?
                        WHERE table_name = ?
                        """.trimIndent()
                interactive.log(INFO, here, "update diff journal to database table=$table, db=$plainName")
                interactive.log(INFO, here, sql)
                val rst = tmpl.update(
                    sql, *updVal.toArray()
                )
                if (rst != 1) {
                    throw IllegalStateException("failed to update table=$table, db=$plainName")
                }
            } else {
                interactive.log(INFO, here, "skip all same journal, table=$table, db=$plainName")
            }
        }
        interactive.log(INFO, here, "done check journal table=$table")
    }

    private fun publishJournal(table: String, enable: Boolean, commitId: Long, event: String) {
        val here = "publishJournal"
        interactive.log(INFO, here, "start publish $event table=$table, enable=$enable")

        val isInsert = "insert".equals(event, true)
        val isUpdate = "update".equals(event, true)
        val isDelete = "delete".equals(event, true)
        val selectSql = when {
            isInsert -> {
                """
                SELECT
                    ddl_instbl ddl_tbl,
                    ddl_instrg ddl_trg,
                    log_insert apply_dt
                FROM $schemaJournalTable
                WHERE table_name = ?
                """.trimIndent()
            }
            isUpdate -> {
                """
                SELECT
                    ddl_updtbl ddl_tbl,
                    ddl_updtrg ddl_trg,
                    log_update apply_dt
                FROM $schemaJournalTable
                WHERE table_name = ?
                """.trimIndent()
            }
            isDelete -> {
                """
                SELECT
                    ddl_deltbl ddl_tbl,
                    ddl_deltrg ddl_trg,
                    log_delete apply_dt
                FROM $schemaJournalTable
                WHERE table_name = ?
                """.trimIndent()
            }
            else -> {
                throw RuntimeException("unsupported event $event")
            }
        }

        val logDate = if (enable) {
            "NOW(3)"
        } else {
            "'1000-01-01 00:00:00.000'"
        }
        val updateSql = if (isInsert) {
            """
            UPDATE $schemaJournalTable SET
                log_insert = $logDate,
                commit_id = ?
            WHERE table_name = ?
            """.trimIndent()
        } else if (isUpdate) {
            """
            UPDATE $schemaJournalTable SET
                log_update = $logDate,
                commit_id = ?
            WHERE table_name = ?
            """.trimIndent()
        } else if (isDelete) {
            """
            UPDATE $schemaJournalTable SET
                log_delete = $logDate,
                commit_id = ?
            WHERE table_name = ?
            """.trimIndent()
        } else {
            throw RuntimeException("unsupported event $event")
        }

        val model = HashMap<String, String>()
        for ((plainName, plainDs) in plainDataSources) {
            interactive.log(INFO, here, "ready to publish $event table=$table, enable=$enable, db=$plainName")
            val tmpl = SimpleJdbcTemplate(plainDs, plainName)
            val olds = AtomicReference<Triple<String, String, String>>()
            tmpl.query(selectSql, table) {
                olds.set(
                    Triple(
                        it.getString("ddl_tbl"),
                        it.getString("ddl_trg"),
                        it.getString("apply_dt")
                    )
                )
            }

            if (olds.get() == null) {
                interactive.log(WARN, here, "skip template not found, table=$table, db=$plainName")
                continue
            }

            val (tmplTbl, tmplTrg, applyDt) = olds.get()
            if (tmplTbl.isBlank() || tmplTrg.isBlank()) {
                interactive.log(WARN, here, "skip blank template,table=$table, db=$plainName")
                continue
            }

            val tables = schemaDefinitionLoader.showTables(plainDs).associateBy {
                it.lowercase()
            }

            val staffs = tables.filter {
                val tp = hasType(table, it.value)
                tp == TYPE_SHARD || tp == TYPE_PLAIN
            }.toMap()

            interactive.log(INFO, here, "init model, applyDt=$applyDt table=$table, enable=$enable, db=$plainName")
            initModelOnce(table, plainDs, model)

            val trcChk = HashMap<String, String>()
            val trcDdl = HashMap<String, String>()
            val trgDdl = HashMap<String, String>()
            val drpTbl = HashMap<String, String>()

            val tmpTkn = "___temp_fw79"

            // clean temp table
            for ((_, tblRaw) in tables) {
                if (tblRaw.contains(tmpTkn, true)) {
                    interactive.log(INFO, here, "remove temp table table=$tblRaw, db=$plainName")
                    tmpl.execute("DROP TABLE IF EXISTS ${sqlStatementParser.safeName(tblRaw)}")
                }
            }

            // 跟踪表，删除存在的，非空的
            for ((_, tblRaw) in staffs) {
                val ddlTbl = mergeDdl(tmplTbl, model, tblRaw)
                val ddlTrg = mergeDdl(tmplTrg, model, tblRaw)

                val curTac = parseTblName(ddlTbl)
                if (curTac.isBlank()) {
                    interactive.log(WARN, here, "skip bad table=$tblRaw, trace-table-ddl =$ddlTbl")
                    continue
                }

                // 检查触发器
                val furTrg = parseTrgName(ddlTrg) // 新trigger名字
                var refTrc = false // 有引用
                for (trg in schemaDefinitionLoader.showBoneTrg(plainDs, tblRaw)) {
                    // 删除同名
                    if (trg.name.equals(furTrg, true)) {
                        interactive.log(WARN, here, "drop trigger=${trg.name}, existed same name, table=$tblRaw, db=$plainName")
                        if (interactive.needAsk(AskType.DropTrigger)) {
                            interactive.ask("continue?\ndrop trigger=${trg.name}, existed same name")
                        }
                        tmpl.execute(schemaDefinitionLoader.makeDdlTrg(trg, true))
                    } else {
                        // 保留trigger使用的trac表
                        if (TemplateUtil.isBoundary(trg.event, curTac, false)) {
                            interactive.log(INFO, here, "trigger=${trg.name}, with same trace-table=$curTac, db=$plainName")
                            refTrc = true
                        }
                    }
                }

                // 检查跟踪表
                var newTrc = true
                if (tables.containsKey(curTac.lowercase())) {
                    interactive.log(INFO, here, "existed trace-table=$curTac, table=$tblRaw, db=$plainName")
                    val safeCurTrc = sqlStatementParser.safeName(curTac)
                    val cnt = tmpl.count("SELECT COUNT(1) FROM $safeCurTrc")
                    if (cnt == 0 && !refTrc) {
                        drpTbl["DROP TABLE IF EXISTS $safeCurTrc"] = curTac
                    } else {
                        interactive.log(WARN, here, "lazy-check existed $cnt records trace-table=$curTac, table=$tblRaw, db=$plainName")
                        trcChk[curTac] = tblRaw
                        newTrc = false
                    }
                }
                if (newTrc) {
                    trcDdl[ddlTbl] = tblRaw
                }
                trgDdl[ddlTrg] = tblRaw
            }

            // 检测已存在的，所有跟踪表应该结构一致
            if (trcChk.isNotEmpty()) {
                val tmpTrc = "$table$tmpTkn"
                val tmpDdl = mergeDdl(tmplTbl, model, tmpTrc)
                val tmpTbl = parseTblName(tmpDdl)
                val safeTmp = sqlStatementParser.safeName(tmpTbl)
                //val tmpRpl = TemplateUtil.replace(tmpDdl, tmpTbl, tmpTrc)

                tmpl.execute(tmpDdl)
                interactive.log(INFO, here, "create temp-trace-table=$tmpTbl, db=$plainName")
                try {
                    val diffTbl = HashSet<String>()
                    for ((trc, stf) in trcChk) {
                        val df = schemaDefinitionLoader.diffBoneSame(plainDs, tmpTbl, trc)
                        if (df.isNotEmpty()) {
                            diffTbl.add(trc)
                            interactive.log(ERROR, here, "different trace-table=$trc of staff=$stf, error=$df")
                        } else {
                            interactive.log(INFO, here, "same bone column trace-table=$trc, temp-trace=$tmpTbl")
                        }
                    }
                    if (diffTbl.isEmpty()) {
                        interactive.log(INFO, here, "existed tracers all the same table=$table, db=$plainName")
                    } else {
                        interactive.log(ERROR, here, "need manually check different tracers. table=$table, db=$plainName, tracers=${diffTbl.joinToString(",")}")
                        if (interactive.needAsk(AskType.ManualCheck)) {
                            interactive.ask("continue?\ndifferent tracers tracers:\n${diffTbl.joinToString("\n")}\ntable=$table")
                        }
                        continue
                    }
                } finally {
                    // 如创建，则删除
                    tmpl.execute("DROP TABLE IF EXISTS $safeTmp")
                    interactive.log(INFO, here, "remove temp-trace-table=$tmpTbl, db=$plainName")
                }
            }

            for ((ddl, tbl) in drpTbl) {
                interactive.log(WARN, here, "drop trace-table=$tbl, empty existed, table=$table, db=$plainName")
                if (interactive.needAsk(AskType.DropTable)) {
                    interactive.ask("continue?\ndrop tracer=$tbl\nddl=$ddl")
                }
                tmpl.execute(ddl)
            }

            if (enable) {
                interactive.log(INFO, here, "execute enable journal, plain-table=table=$table, db=$plainName")
                for ((ddl, tbl) in trcDdl) {
                    interactive.log(INFO, here, "execute trace-table ddl on table=$tbl, db=$plainName")
                    tmpl.execute(ddl)
                }
                for ((ddl, tbl) in trgDdl) {
                    interactive.log(INFO, here, "execute trigger ddl on table=$tbl, db=$plainName")
                    tmpl.execute(ddl)
                }
            } else {
                interactive.log(INFO, here, "execute disable journal, plain-table=table=$table, db=$plainName")
            }

            // 更新状态
            val rst = tmpl.update(updateSql, commitId, table)
            if (rst != 1) {
                throw IllegalStateException("update journal $rst records, table=$table, db=$plainName")
            }
        }

        interactive.log(INFO, here, "done publish $event table=$table, enable=$enable")
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

        interactive.log(INFO, "initModelOnce", "init model table=$table, keys=$keys")
    }

    private fun mergeDdl(ddl: String, map: HashMap<String, String>, table: String): String {
        val tkn = listOf(PLAIN_NAME, TABLE_NAME, TABLE_BONE, TABLE_PKEY)
        val idx = TemplateUtil.parse(ddl, tkn, "'", false)

        map[TABLE_NAME] = table
        return TemplateUtil.merge(ddl, idx, map)
    }

    private fun notApply(str: String?): Boolean {
        if (str.isNullOrEmpty()) return true
        return str.startsWith("1000-01-01")
    }

    private val trgNameRegex = """\s+TRIGGER\s+[`'"]*([^`'"]+)[`'"]*"""
        .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))

    private fun parseTrgName(ddl: String): String {
        return trgNameRegex.find(ddl)?.groupValues?.get(1) ?: Null.Str // 名字
    }

    private fun parseTblName(ddl: String) = when (val st = sqlStatementParser.parseTypeAndTable(ddl)) {
        is SqlStatementParser.SqlType.Plain -> st.table
        is SqlStatementParser.SqlType.Shard -> st.table
        else -> Null.Str
    }
}
