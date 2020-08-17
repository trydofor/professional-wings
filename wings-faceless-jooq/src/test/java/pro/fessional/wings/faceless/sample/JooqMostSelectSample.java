package pro.fessional.wings.faceless.sample;

import lombok.Data;
import lombok.Setter;
import lombok.val;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Param;
import org.jooq.Record2;
import org.jooq.impl.DSL;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.wings.faceless.database.autogen.tables.Tst中文也分表Table;
import pro.fessional.wings.faceless.database.autogen.tables.daos.Tst中文也分表Dao;
import pro.fessional.wings.faceless.database.autogen.tables.pojos.Tst中文也分表;
import pro.fessional.wings.faceless.database.autogen.tables.records.Tst中文也分表Record;
import pro.fessional.wings.faceless.database.jooq.WingsJooqUtil;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jooq.Operator.AND;
import static org.jooq.Operator.OR;
import static pro.fessional.wings.faceless.WingsTestHelper.REVISION_TEST_V2;

/**
 * Jooq的编程能力十分强大，远高于 mybatis系列(mybatis plus)
 * https://www.jooq.org/doc/latest/manual/sql-execution/fetching/
 * https://www.jooq.org/doc/3.12/manual/sql-building/plain-sql-templating/
 *
 * @author trydofor
 * @since 2020-08-14
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"debug = true", "logging.level.org.jooq.tools.LoggerListener=DEBUG"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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

        // 1个字段，到List
        List<Long> ones = ctx
                .select()
                .from(t)
                .where(c)
                .fetch()
                .getValues(t.Id);

        // 2个字段，到Map
        Map<Long, String> maps = ctx
                .selectFrom(t)
                .where(c)
                .fetch()
                .intoMap(t.Id, t.LoginInfo);

        // 分组Pojo到Map
        Map<Long, List<Tst中文也分表>> grps = ctx
                .selectFrom(t)
                .where(c)
                .fetch()
                .intoGroups(t.Id, dao.mapper());


        // 多个字段到2维数组
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

    @Mapper
    public interface Record2Diff {
        Record2Diff INSTANCE = Mappers.getMapper(Record2Diff.class);

        @Mapping(target = "uid", expression = "java(rd.value1())")
        @Mapping(target = "str", expression = "java(rd.value2())")
        DiffName into(Record2<Long, String> rd);
    }

    @Test
    public void test2存入新Pojo() {
        DSLContext ctx = dao.ctx();
        Tst中文也分表Table t = dao.getTable();
        Condition c = t.Id.gt(1L).and(t.Id.le(105L));

        // 多个字段(同名子集)到List  *推荐使用*
        List<SameName> sames = ctx
                .select(t.Id, t.LoginInfo)
                .from(t)
                .where(c)
                .fetch()
                .into(SameName.class);

        // 多个字段(不同名，使用字段别名)到List  *推荐使用*
        List<DiffName> alias = ctx
                .select(t.Id.as("uid"), t.LoginInfo.as("str"))
                .from(t)
                .where(c)
                .fetch()
                .into(DiffName.class);

        // 多个字段(同名子集)到List，使用Mapstruct
        List<DiffName> diffs = ctx
                .select(t.Id, t.LoginInfo)
                .from(t)
                .where(c)
                .fetch()
                .map(Record2Diff.INSTANCE::into);

        // 多个字段(同名子集)到List，使用 lambda
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
        // 其中的 {0}是，0-base的，直接字符串替换的。使用不当会构成sql注入
        Param<Integer> count = DSL.val(3);
        Param<String> string = DSL.val("abc");
        DSL.field("replace(substr(quote(zeroblob(({0} + 1) / 2)), 3, {0}), '0', {1})", String.class, count, string);
        //                                     ^                  ^          ^                   ^^^^^  ^^^^^^
        //                                     |                  |          |                     |       |
        // argument "count" is repeated twice: \------------------+----------|---------------------/       |
        // argument "string" is used only once:                              \-----------------------------/

        // 模板中支持，java和sql注释，placeholder和variable-binding
        DSL.query(
                "SELECT /* In a comment, this is not a placeholder: {0}. And this is not a bind variable: ? */ title AS `title {1} ?` " +
                        "-- Another comment without placeholders: {2} nor bind variables: ?" +
                        "FROM book " +
                        "WHERE title = 'In a string literal, this is not a placeholder: {3}. And this is not a bind variable: ?'"
        );

        //////////////////////// 执行部分 ////////////////////////
        DSLContext ctx = dao.ctx();
        Tst中文也分表Table t = dao.getTable();

        // select `id`, `create_dt`, `modify_dt`, `delete_dt`, `commit_id`, `login_info`, `other_info` from `tst_中文也分表` where (id >= ? AND id <= ?)
        // select `id`, `create_dt`, `modify_dt`, `delete_dt`, `commit_id`, `login_info`, `other_info` from `tst_中文也分表` where (id >= 1 AND id <= 105)
        List<SameName> rc1 = ctx
                .selectFrom(t)
                .where(
                        "id >= ? AND id <= ?",     // The SQL string containing bind value placeholders ("?")
                        1L,                               // The bind value at index 1
                        105L                    // The bind value at index 2
                )
                .fetch()
                .into(SameName.class);

        //select `id`, `create_dt`, `modify_dt`, `delete_dt`, `commit_id`, `login_info`, `other_info` from `tst_中文也分表` where (id >= ? AND id <= ?)
        //select `id`, `create_dt`, `modify_dt`, `delete_dt`, `commit_id`, `login_info`, `other_info` from `tst_中文也分表` where (id >= 2 AND id <= 105)
        // Plain SQL using embeddable QueryPart placeholders (counting from zero).
        // The QueryPart "index" is substituted for the placeholder {0}, the QueryPart "title" for {1}
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

        // 按map绑定，或者通过 jackson pojo to map
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
        Object[] bd2 = {4L, 105L, 0, 10};
        List<SameName> bv2 = ctx
                .fetch("SELECT id, login_info\n" +
                        "FROM tst_中文也分表\n" +
                        "WHERE id >={0} AND id <={1}\n" +
                        "ORDER BY login_info DESC,id\n" +
                        "LIMIT {2}, {3}", bd2)
                .into(SameName.class);

        // 按pojo绑定
        SameName bd3 = new SameName();
        bd3.setId(5L);
        bd3.setLoginInfo("LOGIN_INFO-05");

        // 通过record转一下，必须字段同名
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
        Tst中文也分表Table t = dao.getTable();

        // 通过页面过来的pojo构造and条码
        SameName bd1 = new SameName();
        bd1.setId(105L);
        bd1.setLoginInfo("LOGIN_INFO-05");
        Tst中文也分表Record rc1 = dao.newRecord(bd1);

        // where (`id` = ? and `login_info` = ?)
        // (`id` = 105 and `login_info` = 'LOGIN_INFO-05')
        Condition cd1 = WingsJooqUtil.condChain(rc1, AND);
        List<Tst中文也分表> rs1 = dao.fetch(cd1);

        // 通过页面过来的pojo构造Or条码
        SameName bd2 = new SameName();
        bd2.setId(105L);
        bd2.setLoginInfo("LOGIN_INFO-06");
        Tst中文也分表Record rc2 = dao.newRecord(bd2);
        // where (`id` = ? or `login_info` = ?)
        // where (`id` = 105 or `login_info` = 'LOGIN_INFO-06')
        Condition cd2 = WingsJooqUtil.condChain(rc2, OR);
        List<Tst中文也分表> rs2 = dao.fetch(cd2);

        // 只取id
        // where `id` = ?
        // where `id` = 105
        List<Condition> cds = WingsJooqUtil.condField(rc2, t.Id);
        List<Tst中文也分表> rs3 = dao.fetch(DSL.condition(OR, cds));

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

        // BeanPropertyRowMapper
        SameName sn1 = jdbcTemplate.queryForObject(
                "SELECT id, login_info FROM tst_中文也分表 WHERE id = ?",
                new BeanPropertyRowMapper<>(SameName.class), 105L);

        // BeanPropertyRowMapper
        DiffName df1 = jdbcTemplate.queryForObject(
                "SELECT id as uid, login_info as str FROM tst_中文也分表 WHERE id = ?",
                new BeanPropertyRowMapper<>(DiffName.class), 105L);

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
}
