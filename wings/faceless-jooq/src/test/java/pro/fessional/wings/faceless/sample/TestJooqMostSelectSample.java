package pro.fessional.wings.faceless.sample;

import io.qameta.allure.TmsLink;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOrderByStep;
import org.jooq.TableOnConditionStep;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;
import pro.fessional.wings.faceless.converter.WingsEnumConverters;
import pro.fessional.wings.faceless.app.database.autogen.tables.TstShardingTable;
import pro.fessional.wings.faceless.app.database.autogen.tables.daos.TstShardingDao;
import pro.fessional.wings.faceless.app.database.autogen.tables.pojos.TstSharding;
import pro.fessional.wings.faceless.app.database.autogen.tables.records.TstShardingRecord;
import pro.fessional.wings.faceless.database.helper.RowMapperHelper;
import pro.fessional.wings.faceless.database.jooq.WingsJooqUtil;
import pro.fessional.wings.faceless.database.jooq.converter.JooqConsEnumConverter;
import pro.fessional.wings.faceless.database.jooq.helper.PageJooqHelper;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jooq.Operator.AND;
import static org.jooq.Operator.OR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.fessional.wings.faceless.helper.WingsTestHelper.REVISION_TEST_V2;
import static pro.fessional.wings.faceless.helper.WingsTestHelper.testcaseNotice;

/**
 * <pre>
 * Jooq programming is more powerful than the mybatis series (mybatis plus), see
 * <a href="https://www.jooq.org/doc/latest/manual/sql-execution/fetching/">fetching</a> and
 * <a href="https://www.jooq.org/doc/3.12/manual/sql-building/plain-sql-templating/">plain-sql-templating</a>
 * </pre>
 *
 * @author trydofor
 * @since 2020-08-14
 */

@SpringBootTest
@DependsOnDatabaseInitialization
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class TestJooqMostSelectSample {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod_ = {@Autowired})
    private TstShardingDao dao;

    @Test
    @TmsLink("C12120")
    public void test0Init() {
        val sqls = FlywaveRevisionScanner.scanMaster();
        schemaRevisionManager.checkAndInitSql(sqls, 0, false);
        schemaRevisionManager.publishRevision(REVISION_TEST_V2, 0);
    }

    @Test
    @TmsLink("C12121")
    public void test1SelectOnDemand() {
        DSLContext ctx = dao.ctx();
        TstShardingTable t = dao.getTable();
        Condition c = t.Id.gt(1L).and(t.Id.le(105L));

        testcaseNotice("1 field to List");
        List<Long> ones = ctx.select(t.Id)
                             .from(t)
                             .where(c)
                             .fetch()
                             .into(Long.class);

        testcaseNotice("1 field to object");
        Long one = ctx.select(t.Id)
                      .from(t)
                      .where(t.Id.lt(0L))
                      .fetchOneInto(Long.class);
        Assertions.assertNull(one);

        testcaseNotice("2 fields to Map");
        Map<Long, String> maps = ctx.select(t.Id, t.LoginInfo)
                                    .from(t)
                                    .where(c)
                                    .fetch()
                                    .intoMap(t.Id, t.LoginInfo);

        testcaseNotice("group Pojo to Map");
        Map<Long, List<TstSharding>> grps = ctx.selectFrom(t)
                                               .where(c)
                                               .fetch()
                                               .intoGroups(t.Id, dao.mapper());

        testcaseNotice("2 fields to 2D array");
        Object[][] arrs = ctx.select(t.Id, t.LoginInfo)
                             .from(t)
                             .where(c)
                             .fetch()
                             .intoArrays();
    }


    @Data
    public static class SameName {
        private Long id;
        private String loginInfo;
    }

    @Data
    public static class DiffName {
        private Long uid;
        private String str;
    }

    /**
     * auto generated by `wgmp` live template
     */
    @Mapper
    public interface Record2ToDiffName {

        @Mapping(target = "uid", expression = "java(source.value1())")
        @Mapping(target = "str", expression = "java(source.value2())")
        void mapping(@Nullable Record2<Long, String> source, @NotNull @MappingTarget DiffName target);

        Record2ToDiffName INSTANCE = Mappers.getMapper(Record2ToDiffName.class);

        @NotNull
        static DiffName into(@Nullable Record2<Long, String> source) {
            final DiffName target = new DiffName();
            INSTANCE.mapping(source, target);
            return target;
        }

        static void into(@Nullable Record2<Long, String> source, @NotNull DiffName target) {
            INSTANCE.mapping(source, target);
        }
    }

    @Test
    @TmsLink("C12122")
    public void test2InsertPojo() {
        DSLContext ctx = dao.ctx();
        TstShardingTable t = dao.getTable();
        Condition c = t.Id.gt(1L).and(t.Id.le(105L));

        testcaseNotice("Multiple fields (subsets of the same name) to List *Recommended*");
        List<SameName> sames = ctx.select(t.Id, t.LoginInfo)
                                  .from(t)
                                  .where(c)
                                  .fetch()
                                  .into(SameName.class);

        testcaseNotice("Multiple fields (with different names, using field aliases) to List  *Recommended*");
        List<DiffName> alias = ctx.select(t.Id.as("uid"), t.LoginInfo.as("str"))
                                  .from(t)
                                  .where(c)
                                  .fetch()
                                  .into(DiffName.class);

        testcaseNotice("Multiple fields (subsets of the same name) to List, use Mapstruct");
        List<DiffName> diffs = ctx.select(t.Id, t.LoginInfo)
                                  .from(t)
                                  .where(c)
                                  .fetch()
                                  .map(Record2ToDiffName::into);

        testcaseNotice("Multiple fields (subsets of the same name) to List, use lambda");
        List<DiffName> lambs = ctx.select(t.Id, t.LoginInfo)
                                  .from(t)
                                  .where(c)
                                  .fetch()
                                  .map(it -> {
                                      DiffName a = new DiffName();
                                      a.setUid(it.value1());
                                      a.setStr(it.value2());
                                      return a;
                                  });


        log.info("debug here to see");
    }

    @Test
    @TmsLink("C12123")
    public void test3MixingSql() {
        //////////////////////// description ////////////////////////
        testcaseNotice("where {0} is, for 0-base, a direct string replacement. If used incorrectly, this can lead to SQL Injection.");
        Param<Integer> count = DSL.val(3);
        Param<String> string = DSL.val("abc");
        DSL.field("replace(substr(quote(zeroblob(({0} + 1) / 2)), 3, {0}), '0', {1})", String.class, count, string);
        //                                     ^                  ^          ^                   ^^^^^  ^^^^^^
        //                                     |                  |          |                     |       |
        // argument "count" is repeated twice: \------------------+----------|---------------------/       |
        // argument "string" is used only once:                              \-----------------------------/

        testcaseNotice("the template support java and sql comment, placeholder and variable-binding");
        DSL.query(
                "SELECT /* In a comment, this is not a placeholder: {0}. And this is not a bind variable: ? */ title AS `title {1} ?` " +
                "-- Another comment without placeholders: {2} nor bind variables: ?" +
                "FROM book " +
                "WHERE title = 'In a string literal, this is not a placeholder: {3}. And this is not a bind variable: ?'"
        );

        //////////////////////// execution ////////////////////////
        DSLContext ctx = dao.ctx();
        TstShardingTable t = dao.getTable();

        testcaseNotice("from `tst_sharding` where (id >= ? AND id <= ?)"
                , "from `tst_sharding` where (id >= 1 AND id <= 105)");
        List<SameName> rc1 = ctx.selectFrom(t)
                                .where(
                                        "id >= ? AND id <= ?",     // The SQL string containing bind value placeholders ("?")
                                        1L,                               // The bind value at index 1
                                        105L                    // The bind value at index 2
                                )
                                .fetch()
                                .into(SameName.class);

        // Plain SQL using embeddable QueryPart placeholders (counting from zero).
        // The QueryPart "index" is substituted for the placeholder {0}, the QueryPart "title" for {1}
        testcaseNotice("from `tst_sharding` where (id >= ? AND id <= ?)"
                , "from `tst_sharding` where (id >= 2 AND id <= 105)");
        Param<Long> id1 = DSL.val(2L);
        Param<Long> id2 = DSL.val(105L);
        List<SameName> rc2 = ctx.selectFrom(t)
                                .where(
                                        "id >= {0} AND id <= {1}", // The SQL string containing QueryPart placeholders ("{N}")
                                        id1,                              // The QueryPart at index 0
                                        id2                            // The QueryPart at index 1
                                )
                                .fetch()
                                .into(SameName.class);

        log.info("rc2={}", rc2);
    }

    @Test
    @TmsLink("C12124")
    public void test3BindSql() {
        DSLContext ctx = dao.ctx();

        testcaseNotice("Binding by map or jackson pojo to map");
        Map<String, Object> bd1 = new HashMap<>();
        bd1.put("idMin", 3L);
        bd1.put("idMax", 105L);
        bd1.put("offset", 0);
        bd1.put("count", 10);
        List<SameName> bv1 = ctx.fetch("""
                                                SELECT id, login_info
                                                FROM tst_sharding
                                                WHERE id >=:idMin AND id <=:idMax
                                                ORDER BY login_info DESC,id
                                                LIMIT :offset, :count""",
                                        WingsJooqUtil.bindNamed(bd1))
                                .into(SameName.class);

        // Binding by array
        // SELECT id, login_info FROM tst_sharding WHERE id >=? AND id <=? ORDER BY login_info DESC,id LIMIT ?, ?
        // SELECT id, login_info FROM tst_sharding WHERE id >=4 AND id <=105 ORDER BY login_info DESC,id LIMIT 0, 10
        testcaseNotice("Binding by array");
        Object[] bd2 = {4L, 105L, 0, 10};
        List<SameName> bv2 = ctx.fetch("""
                                        SELECT id, login_info
                                        FROM tst_sharding
                                        WHERE id >={0} AND id <={1}
                                        ORDER BY login_info DESC, id
                                        LIMIT {2}, {3}""", bd2)
                                .into(SameName.class);

        // Binding by pojo
        SameName bd3 = new SameName();
        bd3.setId(5L);
        bd3.setLoginInfo("LOGIN_INFO-05");

        // Convert by record, Must have fields with the same name
        testcaseNotice("Binding by pojo, Convert by record, Must have fields with the same name");
        TstShardingRecord rc = dao.newRecord(bd3);
        rc.from(bd3);
        List<SameName> bv3 = ctx.fetch("""
                                        SELECT id, login_info
                                        FROM tst_sharding
                                        WHERE id = :id OR login_info=:loginInfo
                                        ORDER BY login_info DESC,id""", WingsJooqUtil.bindNamed(rc))
                                .into(SameName.class);

        log.info("");
    }

    @Test
    @TmsLink("C12125")
    public void test3DynamicSql() {
        // condition and `cond*`
        TstShardingTable t = dao.getTable();

        // by builder, null friendly
        Condition d1 = WingsJooqUtil.condition("1=1");
        Condition d2 = WingsJooqUtil.condition("2=2");
        Condition d3 = WingsJooqUtil.condition("3=3");
        Condition d4 = WingsJooqUtil.condition("4=4");
        Condition d5 = WingsJooqUtil.condition("5=5");

        // 1=1 and ((2=2 or 3=3) and (4=4 or 5=5))
        Condition p1 = d2.or(d3);
        Condition p2 = d4.or(d5);
        Condition p3 = p1.and(p2);
        Condition c0 = d1.and(p3);

        Condition c1 = WingsJooqUtil.condBuilder()
                                    .and(d1).and()
                                    .grp()
                                    .grp(d2).or(d3).end()
                                    .and()
                                    .grp(d4).or(d5).end()
                                    .end()
                                    .build();
        assertEquals(c0.toString(), c1.toString());


        testcaseNotice("by passed pojo and `AND`");
        SameName bd1 = new SameName();
        bd1.setId(105L);
        bd1.setLoginInfo("LOGIN_INFO-05");
        TstShardingRecord rc1 = dao.newRecord(bd1);

        // where (`id` = ? and `login_info` = ?)
        // (`id` = 105 and `login_info` = 'LOGIN_INFO-05')
        testcaseNotice("by Record and condChain `AND`");
        Condition cd1 = WingsJooqUtil.condChain(AND, rc1);
        List<TstSharding> rs1 = dao.fetch(dao.getTable(), cd1);

        //
        SameName bd2 = new SameName();
        bd2.setId(105L);
        bd2.setLoginInfo("LOGIN_INFO-06");
        TstShardingRecord rc2 = dao.newRecord(bd2);
        // where (`id` = ? or `login_info` = ?)
        // where (`id` = 105 or `login_info` = 'LOGIN_INFO-06')
        testcaseNotice("by Record and condChain `OR`");
        Condition cd2 = WingsJooqUtil.condChain(OR, rc2);
        List<TstSharding> rs2 = dao.fetch(dao.getTable(), cd2);

        // only id
        // where `id` = ?
        // where `id` = 105
        testcaseNotice("by Record and condChain single field");
        List<Condition> cds = WingsJooqUtil.condField(rc2, t.Id);
        List<TstSharding> rs3 = dao.fetch(t, DSL.condition(OR, cds));

        //
        testcaseNotice("by string-value map");
        Map<String, Object> map = new HashMap<>();
        map.put("id", Collections.singletonList(105L));// use `in()`
//        map.put("id", 105L);
        map.put("login_info", "LOGIN_INFO-05");

        // from `tst_sharding` where (id = ? and login_info = ?)
        Condition cd4 = WingsJooqUtil.condChain(map);
        List<TstSharding> rc4 = dao.fetch(t, cd4);

        // from `tst_sharding` as `y8` where (`y8`.`login_info` = ? and `y8`.`id` = ?)
        testcaseNotice("by string-value map, and alias");
        TstShardingTable a = dao.getAlias();
        Condition cd5 = WingsJooqUtil.condChain(map, true, a);
        List<TstSharding> rc5 = dao.fetch(a, cd5);

        // use dao.update() to update

        log.info("");
    }

    @Setter(onMethod_ = {@Autowired})
    private JdbcTemplate jdbcTemplate;

    @Test
    @TmsLink("C12126")
    public void test4JdbcTemplate() {
        // single field select
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM tst_sharding WHERE id > ?",
                Integer.class, 1);

        SameName sn1 = jdbcTemplate.queryForObject(
                "SELECT id, login_info FROM tst_sharding WHERE id = ?",
                RowMapperHelper.of(SameName.class), 105L);

        DiffName df1 = jdbcTemplate.queryForObject(
                "SELECT id AS uid, login_info AS str FROM tst_sharding WHERE id = ?",
                RowMapperHelper.of(DiffName.class), 105L);

        // lambda
        DiffName df2 = jdbcTemplate.queryForObject("SELECT id, login_info FROM tst_sharding WHERE id = ?",
                (rs, rowNum) -> {
                    DiffName a = new DiffName();
                    a.setUid(rs.getLong(1));
                    a.setStr(rs.getString(2));
                    return a;
                }, 105L);

        log.info("");
    }

    @Test
    @TmsLink("C12127")
    public void test5PaginateJooq() {
        DSLContext dsl = dao.ctx();
        TstShardingTable t = dao.getTable();
        TstShardingTable t1 = dao.getAlias();
        TstShardingTable t2 = dao.getAlias("t2");

        //
        testcaseNotice("use helperJooq, normal",
                "select count(*) from `tst_sharding` as `t1` where `t1`.`id` >= ?",
                "select `t1`.* from `tst_sharding` as `t1` where `t1`.`id` >= ? order by `id` asc limit ?");
        PageQuery page = new PageQuery().setSize(5).setPage(1).setSort("d");
        Map<String, Field<?>> order = new HashMap<>();
        order.put("d", t1.Id);
        PageResult<TstSharding> pr1 = PageJooqHelper.use(dao, page)
                                                    .count()
                                                    .from(t1)
                                                    .where(t1.Id.ge(1L))
                                                    .order(order)
                                                    .fetch(t1.Id, t1.CommitId)
                                                    .into(TstSharding.class);

        PageResult<TstSharding> pr2 = PageJooqHelper.use(dao.ctx(), page)
                                                    .count()
                                                    .from(t1)
                                                    .where(t1.Id.ge(1L))
                                                    .order(order)
                                                    .fetch(t1.Id, t1.CommitId)
                                                    .into(it -> {
                                                        TstSharding po = new TstSharding();
                                                        po.setId(it.get(t1.Id));
                                                        po.setCommitId(it.get(t1.CommitId));
                                                        return po;
                                                    });

        testcaseNotice("use helperJooq, simple",
                "cached total to ignore `select count` in db",
                "select * from `tst_sharding` limit ?");
        PageResult<TstSharding> pr3 = PageJooqHelper.use(dao, page, 10)
                                                    .count()
                                                    .from(t)
                                                    .whereTrue()
                                                    .orderNone()
                                                    .fetch()
                                                    .into(TstSharding.class);
        //
        testcaseNotice("use helperJooq wrap",
                "select count(*) as `c` from (select `t1`.* from `tst_sharding` as `t1` where `t1`.`id` >= ?) as `q`",
                "select `t1`.* from `tst_sharding` as `t1` where `t1`.`id` >= ? order by `id` asc limit ?");
        val qry4 = dsl.select(t1.asterisk()).from(t1).where(t1.Id.ge(1L));
        PageResult<TstSharding> pr4 = PageJooqHelper.use(dao, page)
                                                    .wrap(qry4, order)
                                                    .fetch()
                                                    .into(TstSharding.class);

        val qry5 = dsl.select(t1.Id, t1.CommitId).from(t1).where(t1.Id.ge(1L));
        PageResult<TstSharding> pr5 = PageJooqHelper.use(dao, page)
                                                    .wrap(qry5, order)
                                                    .fetch()
                                                    .into(it -> {
                                                        TstSharding po = new TstSharding();
                                                        po.setId(it.get(t1.Id));
                                                        po.setCommitId(it.get(t1.CommitId));
                                                        return po;
                                                    });
        /////////////////////

        testcaseNotice("wrap count",
                "select count(*) as `c` from (select `id` from `tst_sharding` where `id` > ?) as `q`",
                "select `id` from `tst_sharding` where `id` > ?");
        SelectConditionStep<Record1<Long>> qry1 = dsl.select(t.Id).from(t).where(t.Id.gt(1L));
        SelectOrderByStep<Record1<Long>> qry = dsl.select(t.Id).from(t).where(t.Id.gt(1L)).groupBy(t.Id);
        int cnt0 = dsl.fetchCount(qry);
        int cnt00 = dsl.fetchCount(qry1);
        List<TstSharding> lst0 = qry.fetch().into(TstSharding.class);

        testcaseNotice("single table count",
                "select count(*) from `tst_sharding` where `id` > ?");
        Integer cnt1 = dsl.selectCount()
                          .from(t)
                          .where(t.Id.gt(1L))
                          .fetchOptionalInto(Integer.class)
                          .orElse(0);
        List<TstSharding> lst1 = dsl.select()
                                    .from(t)
                                    .where(t.Id.gt(1L))
                                    .orderBy(t.Id.asc())
                                    .limit(0, 10)
                                    .fetch()
                                    .into(TstSharding.class);
        log.info("cnt1={}", cnt1);
        log.info("lst1={}", lst1.size());

        // DSL.countDistinct()
        testcaseNotice("joined table count",
                "select count(`t1`.`id`) from `tst_sharding` as `t1`, `tst_sharding` as `t2` where (`t1`.`id` = `t2`.`id` and `t1`.`id` > ?)");
        int cnt2 = dsl.select(DSL.count(t1.Id))
                      .from(t1, t2)
                      .where(t1.Id.eq(t2.Id).and(t1.Id.gt(1L)))
                      .fetchOptionalInto(Integer.class)
                      .orElse(0);
        log.info("cnt2={}", cnt2);

        testcaseNotice("left join",
                "select count(`t1`.`id`) from `tst_sharding` as `t1` left outer join `tst_sharding` as `t2` on `t1`.`id` = `t2`.`id` where `t1`.`id` > ?");
        TableOnConditionStep<Record> jt = t1.leftJoin(t2).on(t1.Id.eq(t2.Id));
        int cnt3 = dsl.select(DSL.count(t1.Id))
                      .from(jt)
                      .where(t1.Id.gt(1L))
                      .fetchOptionalInto(Integer.class)
                      .orElse(0);
        log.info("cnt3={}", cnt3);
    }

    @Test
    @TmsLink("C12128")
    public void test5PaginateJdbc() {
        //
        testcaseNotice("use helperJdbc wrap",
                "SELECT count(*) FROM (select `t1`.* from `tst_sharding` as `t1` where `t1`.`id` >= ?) WINGS_WRAP",
                "select `t1`.* from `tst_sharding` as `t1` where `t1`.`id` >= ? order by t1.Id ASC limit 5");

        PageQuery page = new PageQuery().setSize(5).setPage(1).setSort("d");
        Map<String, String> order = new HashMap<>();
        order.put("d", "t1.Id");
        PageResult<TstSharding> pr1 = PageJooqHelper.use(jdbcTemplate, page)
                                                    .wrap("select `t1`.* from `tst_sharding` as `t1` where `t1`.`id` >= ?")
                                                    .order(order)
                                                    .bind(1L)
                                                    .fetchInto(TstSharding.class, WingsEnumConverters.Id2Language);

        log.info("pr1={}", pr1.getData().size());

        testcaseNotice("use helperJdbc normal",
                "SELECT count(*) from `tst_sharding` where id >= ?",
                "SELECT id,login_info,other_info from `tst_sharding` where id >= ? order by id limit 5");

        PageResult<TstSharding> pr2 = PageJooqHelper.use(jdbcTemplate, page)
                                                    .count("count(*)")
                                                    .fromWhere("from `tst_sharding` where id >= ?")
                                                    .order("id")
                                                    .bind(1L)
                                                    .fetch("id,login_info,other_info")
                                                    .into(TstSharding.class, WingsEnumConverters.Id2Language);

        log.info("pr2={}", pr2.getData().size());
    }

    // Same name, auto convert
    @Data
    public static class EnumDto {
        private Long id;
        private StandardLanguage language;
    }

    @Test
    @TmsLink("C12129")
    public void test6MapperEnum() {
        final TstShardingTable t = dao.getTable();
        DataType<StandardLanguage> lang = SQLDataType.INTEGER.asConvertedDataType(JooqConsEnumConverter.of(StandardLanguage.class));
        final Field<StandardLanguage> langField = DSL.field(t.Language.getName(), lang);
        final List<EnumDto> sn = dao.ctx()
                                    .select(t.Id, langField)
                                    .from(t)
                                    .fetch()
                                    .into(EnumDto.class);
        log.info("sn={}", sn);

        // Global injected
        final List<EnumDto> sn2 = dao.ctx()
                                     .select(t.Id, t.Language)
                                     .from(t)
                                     .fetch()
                                     .into(EnumDto.class);
        log.info("sn2={}", sn2);
    }

    @Test
    @TmsLink("C12130")
    public void test7Function() {
        testcaseNotice("by DSL, get function of dialect",
                "select `id` from `tst_sharding` where (`modify_dt` > date_add(?, interval ? day) and substring(`other_info`, ?, ?) like ?)");

        final TstShardingTable t = dao.getTable();
        final String sql1 = dao
                .ctx()
                .select(t.Id)
                .from(t)
                .where(t.ModifyDt.gt(DSL.localDateTimeAdd(LocalDateTime.now(), 2, DatePart.DAY)))
                .and(DSL.substring(t.OtherInfo, 0, 3).like(""))
                .getSQL();
        log.info("sql1={}", sql1);

        testcaseNotice("by DSL, query tuple https://www.jooq.org/doc/3.14/manual/sql-building/column-expressions/row-value-expressions/",
                "select `id` from `tst_sharding` where (`id`, `login_info`) in ((?, ?), (?, ?))");

        final List<Row2<Long, String>> rw2 = new ArrayList<>();
        rw2.add(DSL.row(1L, "1"));
        rw2.add(DSL.row(2L, "2"));

        final String sql2 = dao
                .ctx()
                .select(t.Id)
                .from(t)
                .where(DSL.row(t.Id, t.LoginInfo).in(rw2))
                .getSQL();
        log.info("sql2={}", sql2);
    }
}
