package pro.fessional.wings.faceless.sample;

import lombok.Data;
import lombok.Setter;
import lombok.val;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.SelectConditionStep;
import org.jooq.TableOnConditionStep;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;
import pro.fessional.wings.faceless.database.WingsPageHelper;
import pro.fessional.wings.faceless.database.autogen.tables.Tst中文也分表Table;
import pro.fessional.wings.faceless.database.autogen.tables.daos.Tst中文也分表Dao;
import pro.fessional.wings.faceless.database.autogen.tables.pojos.Tst中文也分表;
import pro.fessional.wings.faceless.database.autogen.tables.records.Tst中文也分表Record;
import pro.fessional.wings.faceless.database.jooq.WingsJooqUtil;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jooq.Operator.AND;
import static org.jooq.Operator.OR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V2;
import static pro.fessional.wings.faceless.WingsTestHelper.testcaseNotice;

/**
 * Jooq的编程能力十分强大，远高于 mybatis系列(mybatis plus)
 * https://www.jooq.org/doc/latest/manual/sql-execution/fetching/
 * https://www.jooq.org/doc/3.12/manual/sql-building/plain-sql-templating/
 *
 * @author trydofor
 * @since 2020-08-14
 */

@SpringBootTest(properties = {"debug = true", "logging.level.org.jooq.tools.LoggerListener=DEBUG"})
@TestMethodOrder(MethodOrderer.MethodName.class)
public class JooqMostSelectSample {

    @Setter(onMethod = @__({@Autowired}))
    private SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod = @__({@Autowired}))
    private Tst中文也分表Dao dao;

    @Test
    public void test0Init() {
        val sqls = FlywaveRevisionScanner.scanMaster();
        schemaRevisionManager.checkAndInitSql(sqls, 0, false);
        schemaRevisionManager.publishRevision(REVISION_TEST_V2, 0);
    }

    @Test
    public void test1按字段选择() {
        DSLContext ctx = dao.ctx();
        Tst中文也分表Table t = dao.getTable();
        Condition c = t.Id.gt(1L).and(t.Id.le(105L));

        testcaseNotice("1个字段，到List");
        List<Long> ones = ctx
                .select()
                .from(t)
                .where(c)
                .fetch()
                .getValues(t.Id);

        testcaseNotice("2个字段，到Map");
        Map<Long, String> maps = ctx
                .selectFrom(t)
                .where(c)
                .fetch()
                .intoMap(t.Id, t.LoginInfo);

        testcaseNotice("分组Pojo到Map");
        Map<Long, List<Tst中文也分表>> grps = ctx
                .selectFrom(t)
                .where(c)
                .fetch()
                .intoGroups(t.Id, dao.mapper());

        testcaseNotice("多个字段到2维数组");
        Object[][] arrs = ctx
                .select(t.Id, t.LoginInfo)
                .from(t)
                .where(c)
                .fetch()
                .intoArrays();
    }


    // 同名
    @Data
    public static class SameName {
        private Long id;
        private String loginInfo;
    }

    // 不同名
    @Data
    public static class DiffName {
        private Long uid;
        private String str;
    }


    // 使用 wgmp live template
    @Mapper
    public interface Record22DiffName {

        Record22DiffName INSTANCE = Mappers.getMapper(Record22DiffName.class);

        static DiffName into(Record2<Long, String> a) {
            return INSTANCE._into(a);
        }

        static void fill(Record2<Long, String> a, DiffName b) {
            INSTANCE._fill(a, b);
        }

        @Mapping(target = "uid", expression = "java(a.value1())")
        @Mapping(target = "str", expression = "java(a.value2())")
        DiffName _into(Record2<Long, String> a);

        @Mapping(target = "uid", expression = "java(a.value1())")
        @Mapping(target = "str", expression = "java(a.value2())")
        void _fill(Record2<Long, String> a, @MappingTarget DiffName b);
    }

    @Test
    public void test2存入新Pojo() {
        DSLContext ctx = dao.ctx();
        Tst中文也分表Table t = dao.getTable();
        Condition c = t.Id.gt(1L).and(t.Id.le(105L));

        testcaseNotice("多个字段(同名子集)到List  *推荐使用*");
        List<SameName> sames = ctx
                .select(t.Id, t.LoginInfo)
                .from(t)
                .where(c)
                .fetch()
                .into(SameName.class);

        testcaseNotice("多个字段(不同名，使用字段别名)到List  *推荐使用*");
        List<DiffName> alias = ctx
                .select(t.Id.as("uid"), t.LoginInfo.as("str"))
                .from(t)
                .where(c)
                .fetch()
                .into(DiffName.class);

        testcaseNotice("多个字段(同名子集)到List，使用Mapstruct");
        List<DiffName> diffs = ctx
                .select(t.Id, t.LoginInfo)
                .from(t)
                .where(c)
                .fetch()
                .map(Record22DiffName::into);

        testcaseNotice("多个字段(同名子集)到List，使用 lambda");
        List<DiffName> lambs = ctx
                .select(t.Id, t.LoginInfo)
                .from(t)
                .where(c)
                .fetch()
                .map(it -> {
                    DiffName a = new DiffName();
                    a.setUid(it.value1());
                    a.setStr(it.value2());
                    return a;
                });


        System.out.println("debug here to see");
    }

    @Test
    public void test3混合SQL() {
        //////////////////////// 说明部分 ////////////////////////
        testcaseNotice("其中的 {0}是，0-base的，直接字符串替换的。使用不当会构成sql注入");
        Param<Integer> count = DSL.val(3);
        Param<String> string = DSL.val("abc");
        DSL.field("replace(substr(quote(zeroblob(({0} + 1) / 2)), 3, {0}), '0', {1})", String.class, count, string);
        //                                     ^                  ^          ^                   ^^^^^  ^^^^^^
        //                                     |                  |          |                     |       |
        // argument "count" is repeated twice: \------------------+----------|---------------------/       |
        // argument "string" is used only once:                              \-----------------------------/

        testcaseNotice("模板中支持，java和sql注释，placeholder和variable-binding");
        DSL.query(
                "SELECT /* In a comment, this is not a placeholder: {0}. And this is not a bind variable: ? */ title AS `title {1} ?` " +
                        "-- Another comment without placeholders: {2} nor bind variables: ?" +
                        "FROM book " +
                        "WHERE title = 'In a string literal, this is not a placeholder: {3}. And this is not a bind variable: ?'"
        );

        //////////////////////// 执行部分 ////////////////////////
        DSLContext ctx = dao.ctx();
        Tst中文也分表Table t = dao.getTable();

        testcaseNotice("from `tst_中文也分表` where (id >= ? AND id <= ?)"
                , "from `tst_中文也分表` where (id >= 1 AND id <= 105)");
        List<SameName> rc1 = ctx
                .selectFrom(t)
                .where(
                        "id >= ? AND id <= ?",     // The SQL string containing bind value placeholders ("?")
                        1L,                               // The bind value at index 1
                        105L                    // The bind value at index 2
                )
                .fetch()
                .into(SameName.class);

        // Plain SQL using embeddable QueryPart placeholders (counting from zero).
        // The QueryPart "index" is substituted for the placeholder {0}, the QueryPart "title" for {1}
        testcaseNotice("from `tst_中文也分表` where (id >= ? AND id <= ?)"
                , "from `tst_中文也分表` where (id >= 2 AND id <= 105)");
        Param<Long> id1 = DSL.val(2L);
        Param<Long> id2 = DSL.val(105L);
        List<SameName> rc2 = ctx
                .selectFrom(t)
                .where(
                        "id >= {0} AND id <= {1}", // The SQL string containing QueryPart placeholders ("{N}")
                        id1,                              // The QueryPart at index 0
                        id2                            // The QueryPart at index 1
                )
                .fetch()
                .into(SameName.class);

        System.out.println("");
    }

    @Test
    public void test4绑定SQL() {
        DSLContext ctx = dao.ctx();

        testcaseNotice("按map绑定，或者通过 jackson pojo to map");
        Map<String, Object> bd1 = new HashMap<>();
        bd1.put("idMin", 3L);
        bd1.put("idMax", 105L);
        bd1.put("offset", 0);
        bd1.put("count", 10);
        List<SameName> bv1 = ctx
                .fetch("SELECT id, login_info\n" +
                                "FROM tst_中文也分表\n" +
                                "WHERE id >=:idMin AND id <=:idMax\n" +
                                "ORDER BY login_info DESC,id\n" +
                                "LIMIT :offset, :count",
                        WingsJooqUtil.bindNamed(bd1))
                .into(SameName.class);

        // 按数组绑定
        // SELECT id, login_info FROM tst_中文也分表 WHERE id >=? AND id <=? ORDER BY login_info DESC,id LIMIT ?, ?
        // SELECT id, login_info FROM tst_中文也分表 WHERE id >=4 AND id <=105 ORDER BY login_info DESC,id LIMIT 0, 10
        testcaseNotice("按数组绑定");
        Object[] bd2 = {4L, 105L, 0, 10};
        List<SameName> bv2 = ctx
                .fetch("SELECT id, login_info\n" +
                        "FROM tst_中文也分表\n" +
                        "WHERE id >={0} AND id <={1}\n" +
                        "ORDER BY login_info DESC, id\n" +
                        "LIMIT {2}, {3}", bd2)
                .into(SameName.class);

        // 按pojo绑定
        SameName bd3 = new SameName();
        bd3.setId(5L);
        bd3.setLoginInfo("LOGIN_INFO-05");

        // 通过record转一下，必须字段同名
        testcaseNotice("按pojo绑定, 通过record转一下，必须字段同名");
        Tst中文也分表Record rc = dao.newRecord(bd3);
        rc.from(bd3);
        List<SameName> bv3 = ctx
                .fetch("SELECT id, login_info\n" +
                        "FROM tst_中文也分表\n" +
                        "WHERE id = :id OR login_info=:loginInfo\n" +
                        "ORDER BY login_info DESC,id", WingsJooqUtil.bindNamed(rc))
                .into(SameName.class);

        System.out.println("");
    }

    @Test
    public void test5动态SQL() {
        // 条件构造，多参加 condition和cond*方法
        Tst中文也分表Table t = dao.getTable();

        // 通过builder建立，对null友好
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


        testcaseNotice("通过页面过来的pojo构造and条码");
        SameName bd1 = new SameName();
        bd1.setId(105L);
        bd1.setLoginInfo("LOGIN_INFO-05");
        Tst中文也分表Record rc1 = dao.newRecord(bd1);

        // where (`id` = ? and `login_info` = ?)
        // (`id` = 105 and `login_info` = 'LOGIN_INFO-05')
        testcaseNotice("通过Record和condChain AND");
        Condition cd1 = WingsJooqUtil.condChain(AND, rc1);
        List<Tst中文也分表> rs1 = dao.fetch(cd1);

        // 通过页面过来的pojo构造Or条码
        SameName bd2 = new SameName();
        bd2.setId(105L);
        bd2.setLoginInfo("LOGIN_INFO-06");
        Tst中文也分表Record rc2 = dao.newRecord(bd2);
        // where (`id` = ? or `login_info` = ?)
        // where (`id` = 105 or `login_info` = 'LOGIN_INFO-06')
        testcaseNotice("通过Record和condChain OR");
        Condition cd2 = WingsJooqUtil.condChain(OR, rc2);
        List<Tst中文也分表> rs2 = dao.fetch(cd2);

        // 只取id
        // where `id` = ?
        // where `id` = 105
        testcaseNotice("通过Record和condChain 单字段");
        List<Condition> cds = WingsJooqUtil.condField(rc2, t.Id);
        List<Tst中文也分表> rs3 = dao.fetch(DSL.condition(OR, cds));

        // 通过字符串map构造条件，如用户数据隔离
        testcaseNotice("通过字符串map构造条件，如用户数据隔离");
        Map<String, Object> map = new HashMap<>();
        map.put("id", Collections.singletonList(105L));// 当做 in ()处理
//        map.put("id", 105L);
        map.put("login_info", "LOGIN_INFO-05");

        // from `tst_中文也分表` where (id = ? and login_info = ?)
        Condition cd4 = WingsJooqUtil.condChain(map);
        List<Tst中文也分表> rc4 = dao.fetch(cd4);

        // from `tst_中文也分表` as `y8` where (`y8`.`login_info` = ? and `y8`.`id` = ?)
        testcaseNotice("通过字符串map构造条件，别名");
        Tst中文也分表Table a = dao.getAlias();
        Condition cd5 = WingsJooqUtil.condChain(map, true, a);
        List<Tst中文也分表> rc5 = dao.fetch(a, cd5);

        // 更新字段，可以直接使用dao.update()

        System.out.println("");
    }

    @Setter(onMethod = @__({@Autowired}))
    private JdbcTemplate jdbcTemplate;

    @Test
    public void test5JdbcTemplate() {
        // 单字段查询
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT count(1) FROM tst_中文也分表 WHERE id > ?",
                Integer.class, 1);

        // BeanPropertyRowMapper.new
        SameName sn1 = jdbcTemplate.queryForObject(
                "SELECT id, login_info FROM tst_中文也分表 WHERE id = ?",
                new BeanPropertyRowMapper<>(SameName.class), 105L);

        // BeanPropertyRowMapper.newInstance
        DiffName df1 = jdbcTemplate.queryForObject(
                "SELECT id AS uid, login_info AS str FROM tst_中文也分表 WHERE id = ?",
                BeanPropertyRowMapper.newInstance(DiffName.class), 105L);

        // lambda
        DiffName df2 = jdbcTemplate.queryForObject("SELECT id, login_info FROM tst_中文也分表 WHERE id = ?",
                (rs, rowNum) -> {
                    DiffName a = new DiffName();
                    a.setUid(rs.getLong(1));
                    a.setStr(rs.getString(2));
                    return a;
                }, 105L);

        System.out.println("");
    }

    @Test
    public void test6PageJooq() {
        DSLContext dsl = dao.ctx();
        Tst中文也分表Table t = dao.getTable();
        Tst中文也分表Table t1 = dao.getAlias("t1");
        Tst中文也分表Table t2 = dao.getAlias("t2");

        //
        testcaseNotice("使用helperJooq正常",
                "select count(*) from `tst_中文也分表` as `t1` where `t1`.`id` >= ?",
                "select `t1`.* from `tst_中文也分表` as `t1` where `t1`.`id` >= ? order by `id` asc limit ?");
        PageQuery page = new PageQuery().setPageSize(5).setPageNumber(1).setSortBy("d");
        Map<String, Field<?>> order = new HashMap<>();
        order.put("d", t.Id);
        PageResult<Tst中文也分表> pr1 = WingsPageHelper.use(dao, page)
                                                  .count()
                                                  .from(t1)
                                                  .where(t1.Id.ge(1L))
                                                  .order(order)
                                                  .fetch(t1.asterisk())
                                                  .into(Tst中文也分表.class);

        testcaseNotice("使用helperJooq简化",
                "缓存的total，使页面不执行count操作",
                "select * from `tst_中文也分表` limit ?");
        PageResult<Tst中文也分表> pr2 = WingsPageHelper.use(dao, page, 10)
                                                  .count()
                                                  .from(t)
                                                  .whereTrue()
                                                  .orderNone()
                                                  .fetch()
                                                  .into(Tst中文也分表.class);
        //
        testcaseNotice("使用helperJooq包装",
                "select count(*) as `c` from (select `t1`.* from `tst_中文也分表` as `t1` where `t1`.`id` >= ?) as `q`",
                "select `t1`.* from `tst_中文也分表` as `t1` where `t1`.`id` >= ? order by `id` asc limit ?");
        SelectConditionStep<Record> qry1 = dsl.select(t1.asterisk()).from(t1).where(t1.Id.ge(1L));
        PageResult<Tst中文也分表> pr3 = WingsPageHelper.use(dao, page)
                                                  .wrap(qry1, order)
                                                  .fetch()
                                                  .into(Tst中文也分表.class);
        /////////////////////

        // 包装count
        testcaseNotice("包装count",
                "select count(*) as `c` from (select `id` from `tst_中文也分表` where `id` > ?) as `q`",
                "select `id` from `tst_中文也分表` where `id` > ?");
        SelectConditionStep<Record1<Long>> qry = dsl.select(t.Id).from(t).where(t.Id.gt(1L));
        int cnt0 = dsl.fetchCount(qry);
        List<Tst中文也分表> lst0 = qry.fetch().into(Tst中文也分表.class);

        // 单表count
        testcaseNotice("单表count",
                "select count(*) from `tst_中文也分表` where `id` > ?");
        int cnt1 = dsl.selectCount()
                      .from(t)
                      .where(t.Id.gt(1L))
                      .fetchOne()
                      .into(int.class);
        List<Tst中文也分表> lst1 = dsl.select()
                                 .from(t)
                                 .where(t.Id.gt(1L))
                                 .orderBy(t.Id.asc())
                                 .limit(0, 10)
                                 .fetch()
                                 .into(Tst中文也分表.class);
        System.out.println(cnt1);
        System.out.println(lst1.size());

        // 联表count
        // DSL.countDistinct()
        testcaseNotice("内联count",
                "select count(`t1`.`id`) from `tst_中文也分表` as `t1`, `tst_中文也分表` as `t2` where (`t1`.`id` = `t2`.`id` and `t1`.`id` > ?)");
        int cnt2 = dsl.select(DSL.count(t1.Id))
                      .from(t1, t2)
                      .where(t1.Id.eq(t2.Id).and(t1.Id.gt(1L)))
                      .fetchOne()
                      .into(int.class);
        System.out.println(cnt2);

        testcaseNotice("左联查询",
                "select count(`t1`.`id`) from `tst_中文也分表` as `t1` left outer join `tst_中文也分表` as `t2` on `t1`.`id` = `t2`.`id` where `t1`.`id` > ?");
        TableOnConditionStep<Record> jt = t1.leftJoin(t2).on(t1.Id.eq(t2.Id));
        int cnt3 = dsl.select(DSL.count(t1.Id))
                      .from(jt)
                      .where(t1.Id.gt(1L))
                      .fetchOne()
                      .into(int.class);
        System.out.println(cnt3);
    }

    @Test
    public void test7PageJdbc() {
        //
        testcaseNotice("使用helperJdbc包装",
                "SELECT count(*) FROM (select `t1`.* from `tst_中文也分表` as `t1` where `t1`.`id` >= ?) WINGS_WRAP",
                "select `t1`.* from `tst_中文也分表` as `t1` where `t1`.`id` >= ? order by t1.Id ASC limit 5");

        PageQuery page = new PageQuery().setPageSize(5).setPageNumber(1).setSortBy("d");
        Map<String, String> order = new HashMap<>();
        order.put("d", "t1.Id");
        PageResult<Tst中文也分表> pr1 = WingsPageHelper.use(jdbcTemplate, page)
                                                  .wrap("select `t1`.* from `tst_中文也分表` as `t1` where `t1`.`id` >= ?")
                                                  .order(order)
                                                  .bind(1L)
                                                  .fetchInto(Tst中文也分表.class);

        System.out.println(pr1.getData().size());

        testcaseNotice("使用helperJdbc正常",
                "SELECT count(*) from `tst_中文也分表` where id >= ?",
                "SELECT id,login_info,other_info from `tst_中文也分表` where id >= ? order by id limit 5");

        PageResult<Tst中文也分表> pr2 = WingsPageHelper.use(jdbcTemplate, page)
                                                  .count("count(*)")
                                                  .fromWhere("from `tst_中文也分表` where id >= ?")
                                                  .order("id")
                                                  .bind(1L)
                                                  .fetch("id,login_info,other_info")
                                                  .into(Tst中文也分表.class);

        System.out.println(pr2.getData().size());
    }
}
