package pro.fessional.wings.faceless.sample

import org.apache.shardingsphere.api.hint.HintManager
import org.jooq.DSLContext
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.faceless.convention.EmptyValue
import pro.fessional.wings.faceless.database.autogen.tables.Tst中文也分表Table
import pro.fessional.wings.faceless.database.autogen.tables.daos.Tst中文也分表Dao
import pro.fessional.wings.faceless.database.autogen.tables.pojos.Tst中文也分表
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager
import pro.fessional.wings.faceless.flywave.SchemaShardingManager
import pro.fessional.wings.faceless.service.lightid.LightIdService
import pro.fessional.wings.faceless.util.FlywaveRevisionSqlScanner
import java.time.LocalDateTime

/**
 * @author trydofor
 * @since 2019-06-20
 */

@RunWith(SpringRunner::class)
@SpringBootTest(properties = ["debug = true", "logging.level.org.jooq.tools.LoggerListener=DEBUG"])
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class WingsJooqDaoSample {

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Autowired
    lateinit var dao: Tst中文也分表Dao

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
}