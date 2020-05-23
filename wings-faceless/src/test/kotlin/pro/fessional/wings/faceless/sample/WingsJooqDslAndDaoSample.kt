package pro.fessional.wings.faceless.sample

import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.OrderField
import org.junit.FixMethodOrder
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.faceless.database.autogen.tables.Tst中文也分表Table
import pro.fessional.wings.faceless.database.autogen.tables.daos.Tst中文也分表Dao
import pro.fessional.wings.faceless.database.autogen.tables.pojos.Tst中文也分表
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager
import pro.fessional.wings.faceless.service.journal.JournalService
import pro.fessional.wings.faceless.util.FlywaveRevisionSqlScanner
import java.time.LocalDateTime

/**
 * @author trydofor
 * @since 2019-06-20
 */

@RunWith(SpringRunner::class)
@SpringBootTest(properties = ["debug = true", "logging.level.org.jooq.tools.LoggerListener=DEBUG"])
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Ignore("手动执行，以有JooqShardingTest覆盖测试 ")
class WingsJooqDslAndDaoSample {

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Autowired
    lateinit var dao: Tst中文也分表Dao

    @Autowired
    lateinit var dsl: DSLContext


    @Test
    fun test1Init() {
        val sqls = FlywaveRevisionSqlScanner.scan(SchemaRevisionManager.REVISIONSQL_PATH)
        schemaRevisionManager.checkAndInitSql(sqls, 0)
        schemaRevisionManager.publishRevision(20190521_01, 0)
    }

    @Test
    fun test2Dao() {

        val a = dao.aliasForReader
        val c = a.Id.eq(1L).and(a.CommitId.eq(2L))

        // select count(*) from `tst_中文也分表` as `y8` where (`y8`.`id` = ? and `y8`.`commit_id` = ?)
        val i = dao.count(c)
        // select `y8`.`id`, `y8`.`create_dt`, `y8`.`modify_dt`, `y8`.`commit_id`, `y8`.`login_info`, `y8`.`other_info`
        // from `tst_中文也分表` as `y8` where (`y8`.`id` = ? and `y8`.`commit_id` = ?) limit ?
        val fetch = dao.fetch(0, 10, c)
        println("============count $i, fetch'size=${fetch.size}")

        val t = dao.tableForWriter
        val setter = hashMapOf<Any, Any>()
        setter.put(t.LoginInfo, "info")
        setter.put(t.CommitId, t.Id.add(1L))
        // update `tst_中文也分表` set `commit_id` = (`id` + ?), `login_info` = ? where `id` = ?
        val u1 = dao.update(setter, t.Id.eq(2L))
        println("============update $u1")

        val po = Tst中文也分表()
        po.commitId = 2L
        po.loginInfo = "info"
        // update `tst_中文也分表` set `commit_id` = ?, `login_info` = ? where `id` = ?
        val u2 = dao.update(po, t.Id.eq(2L))
        println("============update $u2")
    }

    @Test
    fun test3Dsl() {
        val nullCond: Condition? = null
        val nullField: Field<Long>? = null
        val nullOrder: OrderField<Long>? = null
        val emptyOrder = Array<OrderField<Long>?>(0) { null }
        val t = Tst中文也分表Table.Tst中文也分表
        val sql = dsl
                .select(t.Id, nullField) // null safe
                .from(t)
                .where(nullCond)  // null safe
                .orderBy(*emptyOrder) // empty safe
//                .orderBy(t.Id, nullOrder) // IllegalArgumentException: Field not supported : null
//                .orderBy(nullOrder) // IllegalArgumentException: Field not supported : null
                .getSQL()
        print(sql)
    }

    @Test
    fun test4Journal() {
        val journal = JournalService.Journal()
        val now = LocalDateTime.now()
        journal.commitDt = now
        journal.commitId = 1L

        val s1 = HashMap<Any, Any>()
        val t = Tst中文也分表Table.Tst中文也分表
        journal.create(t, s1);
        println(s1)

        val s2 = HashMap<Any, Any>()
        journal.modify(t, s2);
        println(s2)
        val s3 = HashMap<Any, Any>()
        journal.delete(t, s3);
        println(s3)

        val s4 = HashMap<Any, Any>()
        val ob = Tst中文也分表()
        val start1 = System.currentTimeMillis()
        for (i in 1..10000) {
            journal.create(t, s4);
        }
        val start2 = System.currentTimeMillis()
        for (i in 1..10000) {
            journal.create(ob)
        }
        val start3 = System.currentTimeMillis()

        println("cost1=${start2 - start1}, cost2=${start3 - start2}")
    }

    @Test
    fun test5DeleteDt() {
        val c1 = dao.count()
        println(c1)
        val c2 = dao.count(dao.onlyDiedData())
        println(c2)
        val t = Tst中文也分表Table.Tst中文也分表

        val c3 = dsl.selectCount().from(t).where(t.onlyLiveData).execute()
        println(c3)
    }
}