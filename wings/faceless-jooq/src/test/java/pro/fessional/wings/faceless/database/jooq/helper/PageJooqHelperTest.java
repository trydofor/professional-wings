package pro.fessional.wings.faceless.database.jooq.helper;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.jooq.Field;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.wings.faceless.app.database.autogen.tables.daos.TstNormalTableDao;
import pro.fessional.wings.faceless.app.database.autogen.tables.daos.TstShardingDao;
import pro.fessional.wings.faceless.app.database.autogen.tables.pojos.TstSharding;
import pro.fessional.wings.testing.silencer.TestingLoggerAssert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2025-05-05
 */
@SpringBootTest
@DependsOnDatabaseInitialization
class PageJooqHelperTest {

    @Setter(onMethod_ = { @Autowired })
    private TstShardingDao tstShardingDao;

    @Setter(onMethod_ = { @Autowired })
    private TstNormalTableDao tstNormalDao;

    @Test
    @TmsLink("C12152")
    void testUse() {
        final TestingLoggerAssert al = TestingLoggerAssert.install();
        al.rule("page1", event -> event.getFormattedMessage().contains("select count(*) from `tst_sharding` as `d` where `d`.`id` = ?"));
        al.rule("page2", event -> event.getFormattedMessage().contains("select count(*) from (select `d`.`id`, `d`.`login_info` from `tst_sharding` as `d` where `d`.`id` = ?) as `alias_"));
        al.rule("page3", event -> event.getFormattedMessage().contains("select count(*) from (select `d`.`id`, `d`.`login_info` from `tst_sharding` as `d` left outer join `tst_normal_table` as `a` on (`a`.`id` = `d`.`id` and `a`.`id` = ?) where `d`.`id` = ?) as `alias_"));
        al.start();

        // page1
        // select count(*) from `tst_sharding` as `d` where `d`.`id` = ?
        var pq = new PageQuery(1, 10);
        var t = tstShardingDao.getAlias();
        var helper = PageJooqHelper.use(tstShardingDao, pq);
        helper
            .count()
            .from(t)
            .where(t.Id.eq(1L))
            .order(t.Id.desc())
            .fetch(t.Id, t.LoginInfo)
            .into(TstSharding.class);


        // page2
        // select count(*) from (select `d`.`id`, `d`.`login_info` from `tst_sharding` as `d` where `d`.`id` = ?) as `alias_71769483`
        final var s2 = tstShardingDao
            .ctx()
            .select(t.Id, t.LoginInfo)
            .from(t)
            .where(t.Id.eq(1L));

        Map<String, Field<?>> order = new HashMap<>();
        order.put("id", t.Id);
        PageJooqHelper
            .use(tstShardingDao.ctx(), pq)
            .wrap(s2, order)
            .fetch()
            .into(TstSharding.class);
        // page3
        // select count(*) from (select `d`.`id`, `d`.`login_info` from `tst_sharding` as `d`
        // left outer join `tst_normal_table` as `a` on (`a`.`id` = `d`.`id` and `a`.`id` = ?) where `d`.`id` = ?) as `alias_63974807`
        var n = tstNormalDao.getAlias();
        final var s3 = tstShardingDao
            .ctx()
            .select(t.Id, t.LoginInfo)
            .from(t)
            .leftOuterJoin(n)
            .on(n.Id.eq(t.Id))
            .and(n.Id.eq(1L))
            .where(t.Id.eq(1L));

        PageJooqHelper
            .use(tstShardingDao.ctx(), pq)
            .wrap(s3, order)
            .fetch()
            .into(TstSharding.class);
        // check sql
        al.stop();
        al.assertCount(1);
        al.uninstall();
    }
}