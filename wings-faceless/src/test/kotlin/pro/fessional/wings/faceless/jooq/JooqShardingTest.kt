package pro.fessional.wings.faceless.jooq

import org.apache.shardingsphere.api.hint.HintManager
import org.jooq.DSLContext
import org.junit.Test
import org.junit.runner.RunWith
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
import pro.fessional.wings.faceless.jooqgen.WingsCodeGenerator
import pro.fessional.wings.faceless.service.lightid.LightIdService
import pro.fessional.wings.faceless.util.FlywaveRevisionSqlScanner
import java.time.LocalDateTime

/**
 * @author trydofor
 * @since 2019-06-20
 */

@RunWith(SpringRunner::class)
@SpringBootTest(properties = ["debug = true","logging.level.org.jooq.tools.LoggerListener=DEBUG"])
@ActiveProfiles("shard")
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
        schemaRevisionManager.publishRevision(2019052101, 0)
    }

    @Test
    fun test2Code() {
        WingsCodeGenerator.builder()
                .jdbcDriver("com.mysql.cj.jdbc.Driver")
                .jdbcUrl("jdbc:mysql://127.0.0.1/wings_0")
                .jdbcUser("trydofor")
                .jdbcPassword("moilioncircle")
                .databaseSchema("wings_0")
                .databaseIncludes("TST_中文也分表")
                .databaseVersionProvider("")
                .targetPackage("pro.fessional.wings.faceless.database.autogen")
                .targetDirectory("src/test/java/")
                .forceRegenerate()
                .buildAndGenerate()
    }

    @Test
    fun test3Shard() {
        schemaShardingManager.publishShard("TST_中文也分表", 5)
    }

    val id by lazy { lightIdService.getId(Tst中文也分表Table::class.java, 0) }

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
        dao.insert(rd)
//        dsl.newRecord(Tst中文也分表Table.TST_中文也分表, rd).insert()
    }

    @Test
    fun test5Update() {
        //NG update `TST_中文也分表` set `TST_中文也分表`.`MODIFY_DT` = ?, `TST_中文也分表`.`LOGIN_INFO` = ? where `TST_中文也分表`.`ID` <= ?
        //OK update `TST_中文也分表` as `t1` set `t1`.`MODIFY_DT` = ?, `t1`.`LOGIN_INFO` = ? where `t1`.`ID` <= ?
        val t = Tst中文也分表Table.TST_中文也分表
        val r = dsl.update(t)
                .set(t.MODIFY_DT, LocalDateTime.now())
                .set(t.LOGIN_INFO, "update 5")
                .where(t.ID.le(id))
                .execute()
        println("============updated $r records")
    }

    @Test
    fun test6Select() = HintManager.getInstance().use {
        it.setMasterRouteOnly()
        //NG select `TST_中文也分表`.`ID` from `TST_中文也分表` where `TST_中文也分表`.`ID` <= ? limit ?
        //OK select `t1`.`ID` from `TST_中文也分表` as `t1` where `t1`.`ID` <= ? limit ?
        val f1 = Tst中文也分表Table.AS_F1
        val r1 = dsl.select(f1.ID)
                .from(f1)
                .where(f1.ID.le(id))
                .limit(1)
                .fetchOne().into(Long::class.java)
        println("============select id=$r1")

        val t = Tst中文也分表Table.TST_中文也分表
        val r2 = dsl.select(t.ID)
                .from(t)
                .where(t.ID.le(id))
                .limit(1)
                .fetchOne().into(Long::class.java)
        println("============select id=$r2")
    }

    @Test
    fun test7Delete() {
        //NG delete from `TST_中文也分表` where `TST_中文也分表`.`ID` <= ?
        //NG delete `t1` from `TST_中文也分表` as `t1` where `t1`.`ID` <= ?
        val t = Tst中文也分表Table.TST_中文也分表
        val r = dsl.delete(t)
                .where(t.ID.le(id))
                .and(t.COMMIT_ID.isNotNull)
//                .getSQL()
                .execute()
        println("============deleted $r records")
    }
}