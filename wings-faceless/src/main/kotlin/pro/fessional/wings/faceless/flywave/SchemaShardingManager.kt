package pro.fessional.wings.faceless.flywave

import org.slf4j.LoggerFactory
import pro.fessional.wings.faceless.flywave.SqlSegmentProcessor.Companion.TYPE_SHARD
import pro.fessional.wings.faceless.flywave.SqlSegmentProcessor.Companion.hasType
import pro.fessional.wings.faceless.flywave.util.SimpleJdbcTemplate
import pro.fessional.wings.faceless.flywave.util.TemplateUtil
import java.lang.IllegalStateException
import java.lang.StringBuilder
import java.util.LinkedList
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * clone主表实现自动分表，及初始化时的数据导入。
 *
 * @author trydofor
 * @since 2019-06-06
 */
class SchemaShardingManager(
        private val flywaveDataSources: FlywaveDataSources,
        private val sqlStatementParser: SqlStatementParser,
        private val sqlSegmentProcessor: SqlSegmentProcessor,
        private val schemaDefinitionLoader: SchemaDefinitionLoader
) {
    private val logger = LoggerFactory.getLogger(SchemaShardingManager::class.java)

    /**
     * 检查并执行分表，分表为table_0, table_${number -1}，共number个表。。
     * 分表脚标不连续或数量高于number时，显示警告。
     * 如果已存在分表，但都没有记录，则全部删除重建。
     * 如果都不存在，新建。
     * 否则，报错。需要人工介入
     * @param table 主表表名
     * @param number 分表数量，0表示不分表。
     */
    fun publishShard(table: String, number: Int) {
        logger.info("[publishShard] start publishShard table={}, number={}", table, number)

        for ((plainName, plainDs) in flywaveDataSources.plains()) {
            logger.info("[publishShard] ready publishShard table={}, db={}", table, plainName)
            val allTables = schemaDefinitionLoader.showTables(plainDs)
            val shardAll = HashMap<String, Int>() // 可能存在不同的编号风格，key-val不能对调

            val shardBgn = table.length + 1
            for (tbl in allTables) {
                if (hasType(table, tbl) == TYPE_SHARD) {
                    shardAll[tbl] = tbl.substring(shardBgn).toInt()
                }
            }

            // 检查连续性
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
            // 多余的表
            var hasError = false
            for ((tbl, _) in shardAll) {
                val cnt = tmpl.count("SELECT COUNT(1) FROM $tbl")
                val drop = "DROP TABLE " + sqlStatementParser.safeName(tbl)
                if (cnt == 0) {
                    logger.info("[publishShard] drop unused empty shard table={}, db={}", table, plainName)
                    tmpl.execute(drop)
                } else {
                    hasError = true
                    logger.error("[publishShard] ignore drop table with {} records, table={}, db={}, sql={}", cnt, table, plainName, drop)
                }
            }
            // 重建的表
            for ((idx, tbl) in shardReb) {
                val cnt = tmpl.count("SELECT COUNT(1) FROM $tbl")
                val canDrop = if (cnt == 0) {
                    true
                } else {
                    // 检查全DDL（表结构，索引，触发器）是否一样
                    val diff = schemaDefinitionLoader.diffAllSame(plainDs, table, tbl)
                    if (diff.isEmpty()) {
                        true
                    } else {
                        hasError = true
                        logger.error("[publishShard] ignore existed diff shard {}, db={} , diff={}", tbl, plainName, diff)
                        false
                    }
                }
                if (canDrop) {
                    val drop = "DROP TABLE " + sqlStatementParser.safeName(tbl)
                    logger.info("[publishShard] drop empty shard table then recreate it, table={}, db={}", table, plainName)
                    tmpl.execute(drop)
                    shardNew[idx] = tbl
                }
            }

            if (hasError) {
                logger.error("[publishShard] need manually handle above errors to continue, table={}, db={}", table, plainName)
                continue
            }
            // 新建的表
            val ddls = schemaDefinitionLoader.showFullDdl(plainDs, table).map {
                it to TemplateUtil.parse(it, table)
            }

            for ((_, tbl) in shardNew) {
                logger.info("[publishShard] create shard table, table={}, db={}", table, plainName)
                for ((ddl, idx) in ddls) {
                    val sql = TemplateUtil.merge(ddl, idx, tbl)
                    logger.info("running db={}, ddl={}", plainName, sql)
                    tmpl.execute(sql)
                }
            }
        }
        logger.info("[publishShard] done publishShard table={}, number={}", table, number)
    }

    /**
     * 从原主表，按shard数据源，迁移数据，百万以上数据不建议使用。
     * 使用时，建议取消触发器，避免产生大量无用数据。
     * 执行过程中使用3线程:select, insert, delete。
     * 2阻塞队列：insert，delete。默认大小1024。
     * 执行没有事务。目标数据成功插入一条，则源数据删除该条。
     * 如果失败可能存在脏数据，需要人工加入，根据日志处理
     * @param table 主表表名
     * @param stopOnError 插入或删除失败时是否停止，默认不停止，只记录error。
     */
    fun shardingData(table: String, stopOnError: Boolean = false) {
        if (flywaveDataSources.shard == null) {
            logger.error("[shardingData] can NOT shard without sharding datasource, table={}", table)
            return
        }

        val pks = LinkedList<String>()
        val cls = LinkedList<String>()
        flywaveDataSources.plains().values.iterator().next().connection.use { conn ->
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
            logger.error("[shardingData] can NOT shard without pk, table={}", table)
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

            val shardTmpl = SimpleJdbcTemplate(flywaveDataSources.shard, "sharding")

            try {
                while (true) {
                    val (dsName, keys, vals) = triple
                    if (keys.isEmpty()) {
                        break
                    }

                    val rst = try {
                        shardTmpl.update(insertStmt, *vals)
                    } catch (e: Exception) {
                        val err = "[shardingData] failed to insert records shard table=$table, values=${vals.joinToString()}"
                        if (stopOnError) {
                            throw IllegalStateException(err, e)
                        } else {
                            logger.error(err, e)
                        }
                        continue
                    }

                    if (rst == 1) {
                        deleteQueue.offer(Pair(dsName, keys))
                        val cnt = insertCounter.incrementAndGet()
                        if (cnt % 100 == 0) {
                            logger.info("[shardingData] insert {} records on shard table={}", cnt, table)
                        }
                    } else {
                        val err = "[shardingData] failed, insert $rst records, shard table=$table"
                        if (stopOnError) {
                            throw IllegalStateException(err)
                        } else {
                            logger.error(err)
                        }
                    }
                    triple = insertQueue.take()
                }
            } finally {
                deleteQueue.offer(Pair("", emptyArray()))
                logger.info("[shardingData] finished, total insert {} records on table={}", insertCounter.get(), table)
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
                        val err = "[shardingData] failed to delete records, table=$table, db=$plainName, pks=${vals.joinToString()}"
                        if (stopOnError) {
                            throw IllegalStateException(err, e)
                        } else {
                            logger.error(err, e)
                        }
                        continue
                    }

                    val vls = vals.joinToString()
                    if (rst == 1) {
                        val cnt = deleteCounter.incrementAndGet()
                        logger.info("[shardingData] delete {} records on table={}, db={}, pks={}", cnt, table, plainName, vls)
                    } else {
                        val err = "[shardingData] delete $rst records, table=$table, db=$plainName, pks=$vls"
                        if (stopOnError) {
                            throw IllegalStateException(err)
                        } else {
                            logger.error(err)
                        }
                    }
                }
            } finally {
                logger.info("[shardingData] finished, total delete {} records on table={}", deleteCounter.get(), table)
                latch.countDown()
            }
        }

        // main select thread
        for ((plainName, plainDs) in flywaveDataSources.plains()) {
            logger.info("[shardingData] move data from plain db={}, table={}", plainName, table)
            val plainTmpl = SimpleJdbcTemplate(plainDs, plainName)
            tmplMap.put(plainName, plainTmpl)

            val count = plainTmpl.count("SELECT COUNT(1) FROM $table")
            logger.info("[shardingData] find {} records on table={}, db={}", count, table, plainName)

            // select loop
            val lastCnt = selectCounter.get()
            plainTmpl.query("SELECT * FROM $safeTable") {
                val keys = Array<Any>(pks.size) {}
                for ((i, k) in pks.withIndex()) {
                    keys[i] = it.getObject(k)
                }
                val vals = Array<Any>(it.metaData.columnCount) {}
                for (i in 0 until vals.size) {
                    vals[i] = it.getObject(i + 1)
                }
                insertQueue.offer(Triple(plainName, keys, vals))
                val cnt = selectCounter.incrementAndGet()
                if (cnt % 100 == 0) {
                    logger.info("[shardingData] select {} records on table={}, db={}", cnt, table, plainName)
                }
            }
            logger.info("[shardingData] finish one select. {} records on table={}, db={}", selectCounter.get() - lastCnt, table, plainName)
        }

        insertQueue.offer(Triple("", emptyArray(), emptyArray()))
        logger.info("[shardingData] finish all select. {} records on table={}, and wait for insert and delete done", selectCounter.get(), table)
        latch.await()
    }
}