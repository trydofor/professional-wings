package pro.fessional.wings.faceless.flywave

import io.qameta.allure.TmsLink
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization
import org.springframework.boot.test.context.SpringBootTest
import javax.sql.DataSource

@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName::class)
@DependsOnDatabaseInitialization
class WingsShardingTest {
    val log: Logger = LoggerFactory.getLogger(SqlSegmentParserTest::class.java)

    @Autowired
    lateinit var datasource: DataSource

    @Test
    @TmsLink("C12068")
    fun test1DropTable() {
        val statement = datasource.connection.prepareStatement("""
            drop table if exists `wg_order`;
        """.trimIndent())

        val result = statement.executeUpdate()
        log.info("=================== dropTable=$result")
    }

    @Test
    @TmsLink("C12069")
    fun test2CreateTable() {
        val statement = datasource.connection.prepareStatement("""
            create table `wg_order`
            (
              `id`         bigint(20)   not null comment 'PK',
              `create_dt`  datetime(3)     not null default now(3) comment 'created datetime',
              `modify_dt`  datetime(3)     not null default '1000-01-01' on update now(3) comment 'modified datetime',
              `commit_id`  bigint(20)   not null comment 'commit id',
              primary key (`id`)
            ) engine = innodb
              default charset = utf8mb4 comment ='202/test order';
        """.trimIndent())

        val result = statement.executeUpdate()
        log.info("=================== createTable=$result")
    }

    @Test
    @TmsLink("C12070")
    fun test3InsertDate() {
        val statement = datasource.connection.prepareStatement("""
            insert into `wg_order` (`id`,`commit_id`) values
            (1, 1),
            (2, 1)
        """.trimIndent())

        val result = statement.executeUpdate()
        log.info("=================== insertDate=$result")
    }

    @Test
    @TmsLink("C12071")
    fun test4AlterTable() {
        val statement = datasource.connection.prepareStatement("""
            alter table `wg_order`
            drop column `commit_id`;
        """.trimIndent())

        val result = statement.executeUpdate()
        log.info("=================== alterTable=$result")
    }


    @Test
    @TmsLink("C12072")
    @Disabled("no sharding datasource configed")
    fun trigger() {
        val sts1 = datasource.connection.prepareStatement("""
            create table `wg_order${"$"}log`
            (
              `id`         bigint(20)   not null comment 'PK',
              `create_dt`  datetime(3)     not null comment 'created datetime',
              `modify_dt`  datetime(3)     not null comment 'modified datetime',
              `commit_id`  bigint(20)   not null comment 'commit id',
              `_du` int(11) null,
              `_dt` datetime(3) default now(3),
              `_id` int(11) not null auto_increment,
              primary key (`_id`)
            ) engine = innodb
              default charset = utf8mb4 comment ='test order';
        """.trimIndent())

        val rst1 = sts1.executeUpdate()
        log.info("=================== trigger=$rst1")

        val sts2 = datasource.connection.prepareStatement("""
        create trigger `wg_order${"$"}log_bu` before update on `wg_order_0`
        for each row begin
          insert into `wg_order${"$"}log` select *, 1 from `wg_order_0` where id = old.id;
        end
        """.trimIndent())

        val rst2 = sts2.executeUpdate()
        log.info("=================== trigger=$rst2")
    }
}
