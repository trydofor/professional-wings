# 2.2.虚空假面①时间漫游(jooq)

冲到目标位置，并取消最近2.0秒内受到的任何伤害。
虚空假面在时间漫游过程中为无敌状态。

![faceless_void_time_walk](faceless_void_time_walk.png)

 * 从数据库自动生成jooq代码，pojo, table, dao
 * 通过jooq的强类型，保证数据层面的变更和重构稳定

## 2.2.1.强类型(jooq)数据库操作

推荐使用SqlMapping，因为ORM太重了，工程内使用Jooq和JdbcTemplate

MyBatis虽是大部分项目的首选，固有其优秀之处，但开发人员的懒惰或约束力量的不足，
使得项目不高效，偶尔很难维护，项目中容易蔓生出以下问题。

 * 经常被 `select *`，带有大量无用信息。
 * 很容易写出复杂的大SQL，使得服务难以拆分。
 * 字符串及弱类型，IDE的眷顾有限。

使用Jooq，强类型，编程高于配置，并且SQL友好，又恰好有限制能力的能力。

自动生成jooq代码，使用`WingsCodeGenerator`以编程的方式进行（不用maven）。
自动生成的代码在 `database/autogen/`，手动编写的代码在`database/manual/`下。

当自动生成代码时碰到wings或jooq断版功能导致编译错误，无法在当前工程生产代码时，
需要建立一个新的小工程，依赖wings新版，然后执行代码生成类即可。

自动生成的`*Dao`，有大量可直接使用的数据库操作方法，免去很多手写代码量。

 * `getAlias` 获得select用的别名表，`Table as t1`
 * `getTable` 获得modify用的不使用别名的表 `Table`
 * 使用preparedStatement的batch批量插入和更新大量数据
 * 使用mysql特效，`insert ignore`和`replace into`处理重复数据
 * 使用`on duplicate key update`或`select+insert+update`部分更新唯一记录。

值得注意的是，在Dao中使用alias表和本表时，必须保持同源，否则报语法错误。

``` kotlin
val da = dao.alias
// val rd = dao.fetch(da.Id.eq(id)) 别名和本表不同源，语法错误
// select * from win_user where `y8`.`id` = ?

val rd = dao.fetch(da, da.Id.eq(id))
```

在复杂数据操作必须手写代码时，遵循以下约定，

 * 任何对数据库的操作，都应该在`database`包内进行。
 * DSLContext和DataSource不应该离开database层。
 * `single/`包，表示单表，可含简单的条件子查询，一个包名一个表。
 * `couple/`包， 表示多表，一般为join查询或子查询，包名以主表命名。
 * `select|modify`分别对应数据库操作。
 * 也可以`select|insert|update|delete`分类，只是autoware时比较多
 * 数据传递以Dto结尾，放到最临近使用的位子。
 * Dto以静态内类形似存在，用lombok做@Value或@Data。
 * `forUpdate`这种带锁操作，方法名以`Lock`结尾。
 * 类名以`表名`+`Insert|Modify`。
 * `Record`等同于`Dao`不应该在外部使用，应该用`Pojo`或`Dto`
 * 主要 Dao，完成 dsl，等相关操作即可

JdbcTemplate用于功能性或复杂的数据库操作，以自动注入Bean。
参考 `JdbcTemplateConfiguration`的注入。

命名上，接口直接命名，不需要前后缀，Dto放在接口之内。
实现类，放到`impl/`包内，用后缀表示实现方式不同。

 * `Jooq`，Jooq实现
 * `Jdbc`，JdbcTemplate实现
 * `Impl`，混合实现。
 
如`LightId`在读写分离时，需要强制master，可使用注解`MasterRouteOnly`。

## 2.2.2.JOOQ与ShardingSphere的兼容问题

`flywave`对jooq的`Dao`包装，提供了`reader`和`writer`表，跟踪表。
因此，强烈建议，使用`Dao`完成基础的CRUD操作，参见`JooqShardingTest.kt`。
使用dsl构造复杂的sql时，要考虑读写分离。更复杂的sql建议使用jdbcTemplate。

jooq生成代码，默认使用`table.column`限定列名，而ShardingJdbc做当前版本不支持。
最优解决办法是使ShardingJdbc支持，当前最简单的办法是修改Jooq生成策略，参考以下Issue。

 * [JOOQ#8893 Add Settings.renderTable](https://github.com/jOOQ/jOOQ/issues/8893)
 * [JOOQ#9055 should NO table qualify if NO table alias](https://github.com/jOOQ/jOOQ/pull/9055)
 * [ShardingSphere#2859 `table.column` can not sharding](https://github.com/apache/incubator-shardingsphere/issues/2859)
 * [ShardingSphere#5330 replace into](https://github.com/apache/shardingsphere/issues/5330)
 * [ShardingSphere#5210 on duplicate key update](https://github.com/apache/shardingsphere/issues/5210)


在jooq`3.14.0`版本之前，使用`spring.wings.faceless.jooq.auto-qualify.enabled=true`，
完成限定名的自动处理，其规则是，`不存在alias时，不增加限定名`。

使用Jooq的主要原因之一是`限制的艺术`，避免写出比较复杂的SQL，所以约定如下，

 * 鼓励单表操作，放在`single`包内，使用`本名`(如，WinUserLoginTable)
 * 操作多表时，`别名`(如，WinUserLoginTable.asA2)优于`本名`
 * INSERT 使用`本名`，不可使用`别名`
 * DELETE 使用`本名`，不可使用`别名`
 * UPDATE 使用`别名`优先于`本名`
 * SELECT 单表时，用`本名`；多表时，`别名`优先于`本名`
 * **不要** 使用中文表名，例子代码只是极端测试。

JOOQ参考资料

 * [Jooq patch](https://github.com/trydofor/jOOQ/commit/0be23d2e90a1196def8916b9625fbe2ebffd4753)
 * [批量操作 record](https://www.jooq.org/doc/3.12/manual/sql-execution/crud-with-updatablerecords/batch-execution-for-crud/)
 * [批量操作 jdbc](https://www.jooq.org/doc/3.12/manual/sql-execution/batch-execution/)
 * [使用别名，支持分表](https://www.jooq.org/doc/3.12/manual/sql-building/table-expressions/aliased-tables/)
 * [SQL的执行](https://www.jooq.org/doc/3.12/manual/sql-execution/)

## 2.2.3.常见问题

### 01.使用jooq执行plain sql

在执行plain sql时，可以使用jdbcTemplate或jooq，jooq的好处是，会进行parse（性能），进行兼容性调整（如果需要），
所以，在运行时，不考虑兼容性，推荐用 jdbcTemplate，在需要语法分析或合并等场景，使用jooq。

 * https://www.jooq.org/doc/3.12/manual/sql-building/plain-sql/
 * https://www.jooq.org/doc/3.12/manual/sql-building/plain-sql-templating/
 * https://www.jooq.org/doc/3.12/manual/sql-building/queryparts/plain-sql-queryparts/
 * https://www.jooq.org/doc/3.12/manual/sql-building/bind-values/
 * https://www.jooq.org/doc/3.12/manual/sql-building/sql-parser/sql-parser-grammar/
 * https://blog.jooq.org/2020/03/05/using-java-13-text-blocks-for-plain-sql-with-jooq/
 * https://docs.oracle.com/cd/E13157_01/wlevs/docs30/jdbc_drivers/sqlescape.html

``` java
class SelectPlain {
public void test(){
// 其中的 {0}是，0-base的，直接字符串替换的。使用不当会构成sql注入
Field<Integer> count = val(3);
Field<String> string = val("abc");
field("replace(substr(quote(zeroblob(({0} + 1) / 2)), 3, {0}), '0', {1})", String.class, count, string);
//                                     ^                  ^          ^                   ^^^^^  ^^^^^^
//                                     |                  |          |                     |       |
// argument "count" is repeated twice: \------------------+----------|---------------------/       |
// argument "string" is used only once:                              \-----------------------------/

// Plain SQL using bind values. The value 5 is bound to the first variable, "Animal Farm" to the second variable:
create.selectFrom(BOOK).where(
    "BOOK.ID = ? AND TITLE = ?",     // The SQL string containing bind value placeholders ("?") 
    5,                               // The bind value at index 1
    "Animal Farm"                    // The bind value at index 2
).fetch();

// Plain SQL using embeddable QueryPart placeholders (counting from zero).
// The QueryPart "index" is substituted for the placeholder {0}, the QueryPart "title" for {1}
Field<Integer> id   = val(5);
Field<String> title = val("Animal Farm");
create.selectFrom(BOOK).where(
    "BOOK.ID = {0} AND TITLE = {1}", // The SQL string containing QueryPart placeholders ("{N}")
    id,                              // The QueryPart at index 0
    title                            // The QueryPart at index 1
).fetch();

// 模板中支持，java和sql注释，placeholder和variable-binding
query(
  "SELECT /* In a comment, this is not a placeholder: {0}. And this is not a bind variable: ? */ title AS `title {1} ?` " +
  "-- Another comment without placeholders: {2} nor bind variables: ?" +
  "FROM book " +
  "WHERE title = 'In a string literal, this is not a placeholder: {3}. And this is not a bind variable: ?'"
);
}}
```

Plain SQL templating specification
Templating with QueryPart placeholders (or bind value placeholders) requires a simple parsing logic to be applied to SQL strings. The jOOQ template parser behaves according to the following rules:

 * Single-line comments (starting with `--` in all databases (or #) in MySQL) are rendered without modification. Any bind variable or QueryPart placeholders in such comments are ignored.
 * Multi-line comments (starting with `/*` and ending with `*/` in all databases) are rendered without modification. Any bind variable or QueryPart placeholders in such comments are ignored.
 * String literals (starting and ending with `'` in all databases, where all databases support escaping of the quote character by duplication as such: `''`, or in MySQL by escaping as such: `\'` (if Settings.backslashEscaping is turned on)) are rendered without modification. Any bind variable or QueryPart placeholders in such comments are ignored.
 * Quoted names (starting and ending with `"` in most databases, with \` in MySQL, or with `[` and `]` in T-SQL databases) are rendered without modification. Any bind variable or QueryPart placeholders in such comments are ignored.
 * JDBC escape syntax (`{fn ...}`, `{d ...}`, `{t ...}`, `{ts ...}`) is rendered without modification. Any bind variable or QueryPart placeholders in such comments are ignored.
 * Bind variable placeholders (? or :name for named bind variables) are replaced by the matching bind value in case inlining is activated, e.g. through Settings.statementType == STATIC_STATEMENT.
 * QueryPart placeholders (`{number}`) are replaced by the matching QueryPart.
 * Keywords (`{identifier}`) are treated like keywords and rendered in the correct case according to Settings.renderKeywordStyle.

### 02.如何禁用Jooq功能

禁用jooq没有任何影响，不影响flywave，lightid，enum, i18n的使用。

运行时禁用，设置spring.wings 开关，把jooq disable,`spring.wings.faceless.jooq.enabled=false`
但如果有jooq自动生成的代码，是带有`@Repository`，需要禁止spring注入。
```
@ComponentScan(excludeFilters = 
@Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WingsJooqDaoImpl.class))  
```

### 03.常见的jooq查询操作

更多信息，参考`官方文档`和sample代码`pro.fessional.wings.faceless.sample`

https://www.jooq.org/doc/latest/manual/sql-execution/fetching/

```java
class SelectFetch {
// Fetching only book titles (the two calls are equivalent):
List<String> titles1 = create.select().from(BOOK).fetch().getValues(BOOK.TITLE);
List<String> titles2 = create.select().from(BOOK).fetch(BOOK.TITLE);
String[]     titles3 = create.select().from(BOOK).fetchArray(BOOK.TITLE);

// Fetching only book IDs, converted to Long
List<Long> ids1 = create.select().from(BOOK).fetch().getValues(BOOK.ID, Long.class);
List<Long> ids2 = create.select().from(BOOK).fetch(BOOK.ID, Long.class);
Long[]     ids3 = create.select().from(BOOK).fetchArray(BOOK.ID, Long.class);

// Fetching book IDs and mapping each ID to their records or titles
Map<Integer, BookRecord> map1 = create.selectFrom(BOOK).fetch().intoMap(BOOK.ID);
Map<Integer, BookRecord> map2 = create.selectFrom(BOOK).fetchMap(BOOK.ID);
Map<Integer, String>     map3 = create.selectFrom(BOOK).fetch().intoMap(BOOK.ID, BOOK.TITLE);
Map<Integer, String>     map4 = create.selectFrom(BOOK).fetchMap(BOOK.ID, BOOK.TITLE);

// Group by AUTHOR_ID and list all books written by any author:
Map<Integer, Result<BookRecord>> group1 = create.selectFrom(BOOK).fetch().intoGroups(BOOK.AUTHOR_ID);
Map<Integer, Result<BookRecord>> group2 = create.selectFrom(BOOK).fetchGroups(BOOK.AUTHOR_ID);
Map<Integer, List<String>>       group3 = create.selectFrom(BOOK).fetch().intoGroups(BOOK.AUTHOR_ID, BOOK.TITLE);
Map<Integer, List<String>>       group4 = create.selectFrom(BOOK).fetchGroups(BOOK.AUTHOR_ID, BOOK.TITLE);
}
```

### 04.分页查询jooq

分页查询中，count结果根据需要，可以进行缓存，或业务侧不要求必须精确，这样可以避免每次执行。
通过WingsPageHelper工具类，可以构造2类分页查询，count+select 和 wrap select，参考

 * JooqMostSelectSample #test6PageJooq, #test7PageJdbc
 * [count(*)和count(1)谁快](https://blog.jooq.org/2019/09/19/whats-faster-count-or-count1/)

在SpringMvc的`@RequestMapping`中构建`PageQuery`，可以把`@ModelAttribute`放在参数上，

 * pageNumber，大于等于1的整数
 * pageSize，大于等于1的整数
 * sortBy，字符串

参考Spring的`Pageable`和SQL的 order by，以下参数是效果相同的

 * PageQuery - pageSize=100&pageNumber=3&sortBy=id,-name
 * Pageable - size=100&page=3&sort=id,asc&sort=name,desc
 * SQL - order by id asc, name desc

### 05.如何转换sql语法

不同sql语法间可以如下转换，https://www.jooq.org/translate/ (需要翻墙)
