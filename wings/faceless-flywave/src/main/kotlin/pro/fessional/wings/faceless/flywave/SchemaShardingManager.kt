package pro.fessional.wings.faceless.flywave

import org.slf4j.LoggerFactory
import org.slf4j.event.Level.ERROR
import org.slf4j.event.Level.INFO
import pro.fessional.mirana.data.Null
import pro.fessional.wings.faceless.flywave.SqlSegmentProcessor.Companion.TYPE_SHARD
import pro.fessional.wings.faceless.flywave.SqlSegmentProcessor.Companion.hasType
import pro.fessional.wings.faceless.flywave.impl.DefaultInteractiveManager
import pro.fessional.wings.faceless.flywave.util.SimpleJdbcTemplate
import pro.fessional.wings.faceless.flywave.util.TemplateUtil
import java.util.LinkedList
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.BiConsumer
import java.util.function.Function
import javax.sql.DataSource
import kotlin.concurrent.thread

/**
 * Clone the plain table structure to auto create shard table, and auto sharing data.
 *
 * @author trydofor
 * @since 2019-06-06
 */
class SchemaShardingManager(
    private val plainDataSources: Map<String, DataSource>,
    private val shardDataSource: DataSource?,
    private val sqlStatementParser: SqlStatementParser,
    private val schemaDefinitionLoader: SchemaDefinitionLoader
) : InteractiveManager<SchemaShardingManager.AskType> {

    enum class AskType {
        DropTable, ManualCheck
    }

    private val log = LoggerFactory.getLogger(SchemaShardingManager::class.java)
    private val interactive = DefaultInteractiveManager<AskType>(log, plainDataSources, "🐵")

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
     * Check and shard table with table_0 to table_${NUMBER - 1}, total NUMBER tables.
     * A warning is displayed if the footer is not consecutive or if the total is greater than the NUMBER.
     * If shard table already exist, but none of them have records, delete them all and recreate.
     * If none exist, create new ones.
     * Otherwise, an error will throw. Manual intervention is required
     *
     * @param table plain table
     * @param number count of sharding, `0` means no sharding.
     */
    fun publishShard(table: String, number: Int) {
        val here = "publishShard"
        interactive.log(INFO, here, "start publishShard table=$table, number=$number")

        for ((plainName, plainDs) in plainDataSources) {
            interactive.log(INFO, here, "ready publishShard table=$table, db=$plainName")
            val allTables = schemaDefinitionLoader.showTables(plainDs)
            val shardAll = HashMap<String, Int>() // may different numbering styles, key-val can NOT swap

            val shardBgn = table.length + 1
            for (tbl in allTables) {
                if (hasType(table, tbl) == TYPE_SHARD) {
                    shardAll[tbl] = tbl.substring(shardBgn).toInt()
                }
            }

            // check consecutive
            val shardReb = HashMap<Int, String>()
            val shardNew = HashMap<Int, String>()
            for (i in 0 until number) {
                val tbl = table + "_" + i
                val old = shardAll.remove(tbl)
                if (old == null) {
                    shardNew[i] = tbl
                } else {
                    shardReb[i] = tbl
                }
            }

            val tmpl = SimpleJdbcTemplate(plainDs, plainName)
            // redundant table
            var hasError = false
            for ((tbl, _) in shardAll) {
                val cnt = tmpl.count("SELECT COUNT(1) FROM $tbl")
                val drop = "DROP TABLE " + sqlStatementParser.safeName(tbl)
                if (cnt == 0) {
                    interactive.log(INFO, here, "drop unused empty shard table=$table, db=$plainName")
                    if (interactive.needAsk(AskType.DropTable)) {
                        interactive.ask("continue?\ndrop unused empty shard table=$table")
                    }
                    tmpl.execute(drop)
                } else {
                    hasError = true
                    interactive.log(ERROR, here, "ignore drop table with $cnt records, table=$table, db=$plainName, sql=$drop")
                }
            }
            // recreate table
            for ((idx, tbl) in shardReb) {
                val cnt = tmpl.count("SELECT COUNT(1) FROM $tbl")
                val canDrop = if (cnt == 0) {
                    true
                } else {
                    // check full DDL (field detail, index, trigger)
                    val diff = schemaDefinitionLoader.diffFullSame(plainDs, table, tbl)
                    if (diff.isEmpty()) {
                        true
                    } else {
                        hasError = true
                        interactive.log(ERROR, here, "ignore existed diff shard=$tbl, db=$plainName , diff=$diff")
                        false
                    }
                }
                if (canDrop) {
                    val drop = "DROP TABLE " + sqlStatementParser.safeName(tbl)
                    interactive.log(INFO, here, "drop empty shard table then recreate it, table=$table, db=$plainName")
                    if (interactive.needAsk(AskType.DropTable)) {
                        interactive.ask("continue?\ndrop empty shard table then recreate it, table=$table")
                    }
                    tmpl.execute(drop)
                    shardNew[idx] = tbl
                }
            }

            if (hasError) {
                interactive.log(ERROR, here, "need manually handle above errors to continue, table=$table, db=$plainName")
                if (interactive.needAsk(AskType.ManualCheck)) {
                    interactive.ask("continue?\nskip above errors and continue next, table=$table")
                }
                continue
            }

            // create new table
            val ddls = schemaDefinitionLoader.showFullDdl(plainDs, table).map {
                it to TemplateUtil.parse(it, table)
            }

            for ((_, tbl) in shardNew) {
                interactive.log(INFO, here, "create shard table, table=$table, db=$plainName")
                for ((ddl, idx) in ddls) {
                    val sql = TemplateUtil.merge(ddl, idx, tbl)
                    interactive.log(INFO, here, "running db=$plainName, ddl=$sql")
                    tmpl.execute(sql)
                }
            }
        }
        interactive.log(INFO, here, "done publishShard table=$table, number=$number")
    }

    /**
     * Migrate small size data from the plain table to the shard datasource,
     * it is not recommended to use it for more than one million data.
     * When migrating data, it is recommended to disable triggers to avoid generating a lot of useless data.
     * The execution process uses 3 threads: select, insert and delete.
     * 2 blocking queue: insert, delete. default size 1024.
     * Execute without transaction.
     * If the target data is successfully inserted, then the source data is deleted.
     * If it fails, there may be dirty data, you need to  insert manually, according to the log processing
     *
     * @param table plain table
     * @param stopOnError Whether to stop when insertion or deletion fails, the default is not to stop, only record error.
     */
    fun shardingData(table: String, stopOnError: Boolean = false) {
        if (shardDataSource == null) {
            log.error("[shardingData]🐵 can NOT shard without sharding datasource, table={}", table)
            return
        }

        val pks = LinkedList<String>()
        val cls = LinkedList<String>()
        plainDataSources.values.iterator().next().connection.use { conn ->
            val cate = conn.catalog
            val schm = conn.schema
            val meta = conn.metaData
            meta.getPrimaryKeys(cate, schm, table).use { rs ->
                while (rs.next()) {
                    pks.add(rs.getString("COLUMN_NAME"))
                }
            }

            meta.getColumns(cate, schm, table, null).use { rs ->
                while (rs.next()) {
                    cls.add(rs.getString("COLUMN_NAME"))
                }
            }
        }

        if (pks.isEmpty()) {
            log.error("[shardingData]🐵 can NOT shard without pk, table={}", table)
            return
        }

        val selectCounter = AtomicInteger(0)
        val insertCounter = AtomicInteger(0)
        val deleteCounter = AtomicInteger(0)
        val insertQueue = LinkedBlockingQueue<Triple<String, Array<Any>, Array<Any>>>(1024)
        val deleteQueue = LinkedBlockingQueue<Pair<String, Array<Any>>>(1024)

        val safeTable = sqlStatementParser.safeName(table)
        val deleteStmt = StringBuilder("DELETE FROM ")
            .append(safeTable)
            .append(" WHERE ")
            .append(pks.joinToString { sqlStatementParser.safeName(it) + "=?" })
            .toString()


        // insert thread
        thread(name = "flywave-shard-insert") {
            // INSERT INTO TABLES VALUES
            // sql92 sql99 standard but sharding jdbc not well
            var triple = insertQueue.take()
            val insertStmt = StringBuilder("INSERT INTO ")
                .append(safeTable)
                .append("(")
                .append(cls.joinToString { sqlStatementParser.safeName(it) })
                .append(") VALUES (")
                .append((1..triple.third.size).joinToString { "?" })
                .append(")").toString()

            val shardTmpl = SimpleJdbcTemplate(shardDataSource, "sharding")

            try {
                while (true) {
                    val (dsName, keys, vals) = triple
                    if (keys.isEmpty()) {
                        break
                    }

                    val rst = try {
                        shardTmpl.update(insertStmt, *vals)
                    } catch (e: Exception) {
                        val err = "[shardingData]🐵 failed to insert records shard table=$table, values=${vals.joinToString()}"
                        if (stopOnError) {
                            throw IllegalStateException(err, e)
                        } else {
                            log.error(err, e)
                        }
                        continue
                    }

                    if (rst == 1) {
                        deleteQueue.offer(Pair(dsName, keys))
                        val cnt = insertCounter.incrementAndGet()
                        if (cnt % 100 == 0) {
                            log.info("[shardingData]🐵 insert {} records on shard table={}", cnt, table)
                        }
                    } else {
                        val err = "[shardingData]🐵 failed, insert $rst records, shard table=$table"
                        if (stopOnError) {
                            throw IllegalStateException(err)
                        } else {
                            log.error(err)
                        }
                    }
                    triple = insertQueue.take()
                }
            } finally {
                deleteQueue.offer(Pair(Null.Str, emptyArray()))
                log.info("[shardingData]🐵 finished, total insert {} records on table={}", insertCounter.get(), table)
            }
        }

        // delete thread
        val latch = CountDownLatch(1)
        val tmplMap = ConcurrentHashMap<String, SimpleJdbcTemplate>()
        thread(name = "flywave-shard-delete") {
            try {
                while (true) {
                    val (plainName, vals) = deleteQueue.take()
                    if (vals.isEmpty()) {
                        break
                    }

                    val rst = try {
                        tmplMap[plainName]!!.update(deleteStmt, *vals)
                    } catch (e: Exception) {
                        val err = "[shardingData]🐵 failed to delete records, table=$table, db=$plainName, pks=${vals.joinToString()}"
                        if (stopOnError) {
                            throw IllegalStateException(err, e)
                        } else {
                            log.error(err, e)
                        }
                        continue
                    }

                    val vls = vals.joinToString()
                    if (rst == 1) {
                        val cnt = deleteCounter.incrementAndGet()
                        log.info("[shardingData]🐵 delete {} records on table={}, db={}, pks={}", cnt, table, plainName, vls)
                    } else {
                        val err = "[shardingData]🐵 delete $rst records, table=$table, db=$plainName, pks=$vls"
                        if (stopOnError) {
                            throw IllegalStateException(err)
                        } else {
                            log.error(err)
                        }
                    }
                }
            } finally {
                log.info("[shardingData]🐵 finished, total delete {} records on table={}", deleteCounter.get(), table)
                latch.countDown()
            }
        }

        // main select thread
        for ((plainName, plainDs) in plainDataSources) {
            log.info("[shardingData]🐵 move data from plain db={}, table={}", plainName, table)
            val plainTmpl = SimpleJdbcTemplate(plainDs, plainName)
            tmplMap[plainName] = plainTmpl

            val count = plainTmpl.count("SELECT COUNT(1) FROM $table")
            log.info("[shardingData]🐵 find {} records on table={}, db={}", count, table, plainName)

            // select loop
            val lastCnt = selectCounter.get()
            plainTmpl.query("SELECT * FROM $safeTable") {
                val keys = Array<Any>(pks.size) {}
                for ((i, k) in pks.withIndex()) {
                    keys[i] = it.getObject(k)
                }
                val vals = Array<Any>(it.metaData.columnCount) {}
                for (i in vals.indices) {
                    vals[i] = it.getObject(i + 1)
                }
                insertQueue.offer(Triple(plainName, keys, vals))
                val cnt = selectCounter.incrementAndGet()
                if (cnt % 100 == 0) {
                    log.info("[shardingData]🐵 select {} records on table={}, db={}", cnt, table, plainName)
                }
            }
            log.info("[shardingData]🐵 finish one select. {} records on table={}, db={}", selectCounter.get() - lastCnt, table, plainName)
        }

        insertQueue.offer(Triple(Null.Str, emptyArray(), emptyArray()))
        log.info("[shardingData]🐵 finish all select. {} records on table={}, and wait for insert and delete done", selectCounter.get(), table)
        latch.await()
    }
}
