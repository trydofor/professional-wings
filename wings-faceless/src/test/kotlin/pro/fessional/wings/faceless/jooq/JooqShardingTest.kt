package pro.fessional.wings.faceless.jooq

import org.apache.shardingsphere.api.hint.HintManager
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.event.annotation.BeforeTestClass
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.faceless.WingsTestHelper
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
@ActiveProfiles("shard")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class JooqShardingTest {

    @Autowired
    lateinit var schemaRevisionManager: SchemaRevisionManager

    @Autowired
    lateinit var schemaShardingManager: SchemaShardingManager

    @Autowired
    lateinit var lightIdService: LightIdService

    @Autowired
    lateinit var dsl: DSLContext

    @Autowired
    lateinit var dao: Tst中文也分表Dao

    @Autowired
    lateinit var wingsTestHelper: WingsTestHelper

    @Test
    fun `test0🦁清表重置`() {
        wingsTestHelper.cleanAndInit()
    }

    @Test
    fun `test1🦁清表重置`() {
        schemaRevisionManager.publishRevision(20190521_01, 0)
    }

    @Test
    fun `test3🦁分五张表`() {
        schemaShardingManager.publishShard("tst_中文也分表", 5)
    }

    val id by lazy { lightIdService.getId(Tst中文也分表Table::class.java) }

    @Test
    fun `test4🦁插入🦁查日志`() {
        val rd = Tst中文也分表(id,
                LocalDateTime.now(),
                EmptyValue.DATE_TIME,
                EmptyValue.DATE_TIME,
                0,
                EmptyValue.VARCHAR,
                EmptyValue.VARCHAR
        )
        // insert into `tst_中文也分表` (`id`, `create_dt`, `modify_dt`, `commit_id`, `login_info`, `other_info`) values (?, ?, ?, ?, ?, ?)
        dao.insert(rd)

        wingsTestHelper.note("""
                ==== 检查 sql 日志 ====
                [OK] insert into `tst_中文也分表_0` (`ID`, `CREATE_DT`, `MODIFY_DT`, `COMMIT_ID`, `LOGIN_INFO`, `OTHER_INFO`) values (?, ?, ?, ?, ?, ?)
                [NG] insert into `TST_中文也分表` as `t1` (`ID`, `CREATE_DT`, `MODIFY_DT`, `COMMIT_ID`, `LOGIN_INFO`, `OTHER_INFO`) values (?, ?, ?, ?, ?, ?)
                """.trimIndent())
//        dsl.newRecord(Tst中文也分表Table.TST_中文也分表, rd).insert()
    }

    @Test
    fun `test5🦁更新🦁查日志`() {
        val tp = Tst中文也分表Table.Tst中文也分表
        // update `tst_中文也分表` set `modify_dt` = ?, `login_info` = ? where `id` <= ?
        val rp = dsl.update(tp)
                .set(tp.ModifyDt, LocalDateTime.now())
                .set(tp.LoginInfo, "update 5")
                .where(tp.Id.eq(id))
                .execute()
        wingsTestHelper.note("plain updated= $rp")
        wingsTestHelper.note("update `tst_中文也分表_1` set `modify_dt` = ?, `login_info` = ? where `id` = ?")

        val tw = dao.tableForWriter
        val rw = dsl.update(tw)
                .set(tw.ModifyDt, LocalDateTime.now())
                .set(tw.LoginInfo, "update 5")
                .where(tw.Id.eq(id))
                .execute()
        wingsTestHelper.note("write updated= $rw")
        wingsTestHelper.note("update `tst_中文也分表_1` set `modify_dt` = ?, `login_info` = ? where `id` = ?")

        val tr = dao.aliasForReader
        val rr = dsl.update(tr)
                .set(tr.ModifyDt, LocalDateTime.now())
                .set(tr.LoginInfo, "update 5")
                .where(tr.Id.eq(id))
                .execute()
        wingsTestHelper.note("read  updated= $rr")
        wingsTestHelper.note("update `tst_中文也分表_1` as `y8` set `y8`.`modify_dt` = ?, `y8`.`login_info` = ? where `y8`.`id` = ?")


        wingsTestHelper.note("""
                ==== 检查 sql 日志 ====
                [OK] update `TST_中文也分表` set `MODIFY_DT` = ?, `LOGIN_INFO` = ? where `ID` <= ?
                [OK] update `TST_中文也分表` as `t1` set `t1`.`MODIFY_DT` = ?, `t1`.`LOGIN_INFO` = ? where `t1`.`ID` <= ?
                [NG] update `TST_中文也分表` set `TST_中文也分表`.`MODIFY_DT` = ?, `TST_中文也分表`.`LOGIN_INFO` = ? where `TST_中文也分表`.`ID` <= ?
                """.trimIndent())
    }

    @Test
    fun `test6🦁查询🦁查日志`() = HintManager.getInstance().use {
        it.setMasterRouteOnly()

        val ta = Tst中文也分表Table.asY8
        val ra = dsl.select(ta.Id)
                .from(ta)
                .where(ta.Id.le(id))
                .limit(DSL.inline(1)) // RC3
                .getSQL()
//                .fetchOne().into(Long::class.java)
        wingsTestHelper.note("alias select", ra)
        wingsTestHelper.note("select `y8`.`id` from `tst_中文也分表` as `y8` where `y8`.`id` <= ?")

        val tp = Tst中文也分表Table.Tst中文也分表
        val rp = dsl.select(tp.Id)
                .from(tp)
                .where(tp.Id.le(id))
//                .limit(1) // https://github.com/apache/incubator-shardingsphere/issues/3330
                .getSQL()
//                .fetchOne().into(Long::class.java)
        wingsTestHelper.note("plain select", rp)
        wingsTestHelper.note("select `id` from `tst_中文也分表` where `id` <= ?")

        val da = dao.aliasForReader
        val rd = dao.fetch(da.Id.eq(id))
        wingsTestHelper.note("dao select= $rd")
        wingsTestHelper.note("select `y8`.`id`, `y8`.`create_dt`, ... from `tst_中文也分表` as `y8` where `y8`.`id` = ?")

        wingsTestHelper.note("""
                ==== 检查 sql 日志 ====
                [OK] select `ID` from `TST_中文也分表` where `ID` <= ? limit ?
                [OK] select `t1`.`ID` from `TST_中文也分表` as `t1` where `t1`.`ID` <= ? limit ?
                [NG] select `TST_中文也分表`.`ID` from `TST_中文也分表` where `TST_中文也分表`.`ID` <= ? limit ?
                """.trimIndent())
    }

    @Test
    fun `test7🦁删除🦁查日志`() {
        val tp = Tst中文也分表Table.Tst中文也分表
        val rp = dsl.delete(tp)
                .where(tp.Id.eq(id)) // Inline strategy cannot support range sharding.
                .and(tp.CommitId.isNotNull)
                .getSQL()
//                .execute()
        wingsTestHelper.note("plain delete= $rp")
        wingsTestHelper.note("delete from `tst_中文也分表` where (`id` <= ? and `commit_id` is not null)")

        val dw = dao.tableForWriter
        val rw = dao.delete(dw.Id.eq(id))
        wingsTestHelper.note("dao delete= $rw")
        wingsTestHelper.note("delete from `tst_中文也分表_3` where `id` = ? ")

        wingsTestHelper.note("""
                ==== 检查 sql 日志 ====
                [OK] delete from `TST_中文也分表` where `ID` <= ?
                [NG] delete from `TST_中文也分表` where `TST_中文也分表`.`ID` <= ?
                [NG] delete `t1` from `TST_中文也分表` as `t1` where `t1`.`ID` <= ?
                """.trimIndent())
    }
}