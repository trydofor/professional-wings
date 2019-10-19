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

    @Test
    fun test1Init() {
        val sqls = FlywaveRevisionSqlScanner.scan(SchemaRevisionManager.REVISIONSQL_PATH)
        schemaRevisionManager.checkAndInitSql(sqls, 0)
        schemaRevisionManager.publishRevision(20190521_01, 0)
    }

    @Test
    fun test3Shard() {
        schemaShardingManager.publishShard("tst_中文也分表", 5)
    }

    val id by lazy { lightIdService.getId(Tst中文也分表Table::class.java) }

    @Test
    fun test4Insert() {
        //OK insert into `TST_中文也分表` (`ID`, `CREATE_DT`, `MODIFY_DT`, `COMMIT_ID`, `LOGIN_INFO`, `OTHER_INFO`) values (?, ?, ?, ?, ?, ?)
        //NG insert into `TST_中文也分表` as `t1` (`ID`, `CREATE_DT`, `MODIFY_DT`, `COMMIT_ID`, `LOGIN_INFO`, `OTHER_INFO`) values (?, ?, ?, ?, ?, ?)
        val rd = Tst中文也分表(id,
                LocalDateTime.now(),
                EmptyValue.DATE_TIME,
                0,
                EmptyValue.VARCHAR,
                EmptyValue.VARCHAR
        )
        // insert into `tst_中文也分表` (`id`, `create_dt`, `modify_dt`, `commit_id`, `login_info`, `other_info`) values (?, ?, ?, ?, ?, ?)
        dao.insert(rd)
//        dsl.newRecord(Tst中文也分表Table.TST_中文也分表, rd).insert()
    }

    @Test
    fun test5Update() {
        //NG update `TST_中文也分表` set `TST_中文也分表`.`MODIFY_DT` = ?, `TST_中文也分表`.`LOGIN_INFO` = ? where `TST_中文也分表`.`ID` <= ?
        //OK update `TST_中文也分表` as `t1` set `t1`.`MODIFY_DT` = ?, `t1`.`LOGIN_INFO` = ? where `t1`.`ID` <= ?
        val t = Tst中文也分表Table.Tst中文也分表
        // update `tst_中文也分表` set `modify_dt` = ?, `login_info` = ? where `id` <= ?
        val r = dsl.update(t)
                .set(t.ModifyDt, LocalDateTime.now())
                .set(t.LoginInfo, "update 5")
                .where(t.Id.le(id))
                .execute()
        println("============updated $r records")
    }

    @Test
    fun test6Select() = HintManager.getInstance().use {
        it.setMasterRouteOnly()
        //NG select `TST_中文也分表`.`ID` from `TST_中文也分表` where `TST_中文也分表`.`ID` <= ? limit ?
        //OK select `t1`.`ID` from `TST_中文也分表` as `t1` where `t1`.`ID` <= ? limit ?
        val f1 = Tst中文也分表Table.asY8
        // select `y8`.`id` from `tst_中文也分表` as `y8` where `y8`.`id` <= ?
        val r1 = dsl.select(f1.Id)
                .from(f1)
                .where(f1.Id.le(id))
                .limit(DSL.inline(1)) // RC3
                .getSQL()
//                .fetchOne().into(Long::class.java)
        println("============select id=$r1")

        // select `id` from `tst_中文也分表` where `id` <= ?
        val t = Tst中文也分表Table.Tst中文也分表
        val r2 = dsl.select(t.Id)
                .from(t)
                .where(t.Id.le(id))
//                .limit(1) // https://github.com/apache/incubator-shardingsphere/issues/3330
                .getSQL()
//                .fetchOne().into(Long::class.java)
        println("============select id=$r2")
    }

    @Test
    fun test7Delete() {
        //NG delete from `TST_中文也分表` where `TST_中文也分表`.`ID` <= ?
        //NG delete `t1` from `TST_中文也分表` as `t1` where `t1`.`ID` <= ?
        val t = Tst中文也分表Table.Tst中文也分表
        // delete from `tst_中文也分表` where (`id` <= ? and `commit_id` is not null)
        val r = dsl.delete(t)
                .where(t.Id.le(id))
                .and(t.CommitId.isNotNull)
//                .getSQL()
                .execute()
        println("============deleted $r records")
    }
}