package pro.fessional.wings.faceless.flywave

import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import javax.sql.DataSource

@RunWith(SpringRunner::class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
open class WingsShardingTests {

    @Autowired
    lateinit var datasource: DataSource

    @Test
    fun test1DropTable() {
        val statement = datasource.connection.prepareStatement("""
            DROP TABLE IF EXISTS `WG_ORDER`;
        """.trimIndent())

        val result = statement.executeUpdate()
        println("=================== dropTable=$result")
    }

    @Test
    fun test2CreateTable() {
        val statement = datasource.connection.prepareStatement("""
            CREATE TABLE `WG_ORDER`
            (
              `ID`         bigint(20)   NOT NULL COMMENT '主键',
              `CREATE_DT`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日时',
              `MODIFY_DT`  datetime     NOT NULL DEFAULT '1000-01-01' ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日时',
              `COMMIT_ID`  bigint(20)   NOT NULL COMMENT '提交ID',
              PRIMARY KEY (`ID`)
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4 COMMENT ='测试订单';
        """.trimIndent())

        val result = statement.executeUpdate()
        println("=================== createTable=$result")
    }

    @Test
    fun test3InsertDate() {
        val statement = datasource.connection.prepareStatement("""
            INSERT INTO `WG_ORDER` (`ID`,`COMMIT_ID`) VALUES
            (1, 1),
            (2, 1)
        """.trimIndent())

        val result = statement.executeUpdate()
        println("=================== insertDate=$result")
    }

    @Test
    fun test4AlterTable() {
        val statement = datasource.connection.prepareStatement("""
            ALTER TABLE `WG_ORDER`
            DROP COLUMN `COMMIT_ID`,
            ADD INDEX IDX_CREATE_DT (`CREATE_DT` ASC);
        """.trimIndent())

        val result = statement.executeUpdate()
        println("=================== alterTable=$result")
    }


    fun trigger() {
        val sts1 = datasource.connection.prepareStatement("""
            CREATE TABLE `WG_ORDER${"$"}LOG`
            (
              `ID`         bigint(20)   NOT NULL COMMENT '主键',
              `CREATE_DT`  datetime     NOT NULL COMMENT '创建日时',
              `MODIFY_DT`  datetime     NOT NULL COMMENT '修改日时',
              `COMMIT_ID`  bigint(20)   NOT NULL COMMENT '提交ID',
              `_du` INT(11) NULL,
              `_dt` DATETIME DEFAULT CURRENT_TIMESTAMP,
              `_id` INT(11) NOT NULL AUTO_INCREMENT,
              PRIMARY KEY (`_id`)
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4 COMMENT ='测试订单';
        """.trimIndent())

        val rst1 = sts1.executeUpdate()
        println("=================== trigger=$rst1")

        val sts2 = datasource.connection.prepareStatement("""
        CREATE TRIGGER `WG_ORDER${"$"}LOG_BU` BEFORE UPDATE ON `WG_ORDER_0`
        FOR EACH ROW BEGIN
          insert into `WG_ORDER${"$"}LOG` select *, 1 from `WG_ORDER_0` where id = OLD.id;
        END
        """.trimIndent())

        val rst2 = sts2.executeUpdate()
        println("=================== trigger=$rst2")
    }
}
