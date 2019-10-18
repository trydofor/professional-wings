# 2.虚空假面(faceless)

支持MySql系的一套Sharding，并有表结构和数据变更的版本管理的基本套餐。

 * 轻量SCHEMA版本管理(fly-wave)
 * DATA版本管理和追踪(journal/$log)
 * 可切换的分表分库功能(ShardingJdbc/PlainDataSource)
 * 高效递增非连续的分布式主键(LightId)
 * jooq 自动生成代码

## 2.1.飞波(flywave)是一个实践

实际项目中，schema结构的变更十分频繁，需要控制好local/dev/product的版本和节奏。
`flyway`是个不错的选择，但用它有点牛刀杀鸡，所以新造个轮子叫`flywave`，可以，

 * 根据 `/wings-flywave/revision/*.sql` 完成数据库和数据的统一管理。
 * 根据 `sys_schema_version`表，控制数据库版本，升级和降级。
 * 根据 `sys_schema_journal`表，完成自动记录数据变更。

实际项目经验中，数据库只存储数据，不存业务逻辑。所以，必须使用基本的SQL和数据库功能。
这些功能包括，表，索引，触发器。不包括，视图，存储过程，外键约束及个别特性。

`flywave`体系下，有一下约定和概念，理解这些约定，有利于清晰业务架构。

JDBC数据源(DataSource)，分为两种，他们会存在于`FlywaveDataSources`中，

 * 分片数据源(shard)，具有分表分库功能，如`ShardingSphere`。
 * 普通数据源(Plain)，没有sharding功能，只在单个DB上执行。

当只有一个数据源，且没有sharding配置时，两者实际为同一个值。
`flywave`会根据后续的场景规则，自动或手动使用不同的数据源执行DDL和DML等。

数据表(Table)，语义上有三种表，用来表示用途和使用场景。

 * 普通表(plain)，普通的数据表，正常的命名，如英数下划线，甚至中文。
 * 分表(shard)，普通表+`_#`后缀，`#`为对N取模(0..N-1)（左侧无0填充）。
 * 跟踪表(trace)，普通表或分表+`$*`，其中`*`为任意命名字符，`$`视为分隔符。

这三种表，满足以下规定，并且跟普通表保持同步更新。

 * 普通表一定存在，即便存在分表时，也会存在一个普通表，用来保持原始表结构。
 * 分表，具有和普通表一样的表结构，索引和触发器，同步更新。
 * 跟踪表具有和本表(staff，对应的普通表和分表)相同的字段和类型，同步更新。
 * 跟踪表为触发器使用，包含了一些标记字段，建议以`_*`格式。
 * 三种表，一定会保持相同的表结构（名字，类型，前后关系），同步更新。


`$`是命名中的特殊字符，定义`跟踪表`。比如替换时。
sql的书写规则详见[数据库约定](/wings-faceless/src/main/resources/wings-flywave/readme.md)

数据库中尽量不要`nullable`，约定默认值代替，如`convention.EmptyValue`类

## 2.2.数据的版本管理(journal)

`逻辑删除`已成为行业标配，但如果删除数据只在调查时使用，则不是最优解。

 * 破坏查询索引。每个查询都要`is_deleted=0`，且90%以上数据为true。
 * 破坏唯一约束。可以重复逻辑删除，所以无法简单有效的对数据unique。

`逻辑删除`在wings中存在的唯一目的是解决数据的`溯源`问题，否则应该直接删除。
它也叫`标记删除`，类似java的GC，在引用计数为0时，会被(立即或批处理)删除。


任何数据变动，都应该有`commit_id`，记录下事件信息（人，事件，业务信息等）。
最新的数据留在`本表`，旧数据通过`trigger`插入`跟踪表`

`journal`通过`sys_schema_journal`生成`跟踪表`和`触发器`。

 * 根据`log_update`创建 `before update` 触发器。
 * 根据`log_delete`创建 `before delete` 触发器。
 
通过配置文件指定模板来定义DDL，默认设置参考`wings-flywave.properties`。
默认分表有自己的`更新表`(`TABLE_#$UPD`)，但共享同一个`删除表`(`TABLE$DEL`)。
模板中，预定义以下DDL变量，避开spring变量替换，使用胡子`{{}}`表示法，名字全大写。

 * `{{PLAIN_NAME}}` 目标表的`本表`名字
 * `{{TABLE_NAME}}` 目标表名字，可能是普通表，分表，跟踪表
 * `{{TABLE_BONE}}` 目标表字段(至少包含名字，类型，注释)，不含索引和约束
 * `{{TABLE_PKEY}}` 目标表的主键中字段名，用来创建原主键的普通索引。

对于删除的数据，无法优雅的设置`commit_id`，此时若是需要journal，则要先更新再删除。
目前，在不使用sql标记或解析的情况下，提供了2种方法，手工类优先于自动拦截。

 * 通过工具类`JournalHelp`，手动执行`delete##`。
 * 自动对`delete from ## where id=? and commit_id=?`格式进行拦截。

自动拦截`spring.wings.trigger.journal-delete.enabled`默认关闭。
因为违反`静态高于动态，编译时高于运行时`团队规则，且性能和限制不好控制。

## 2.3.分表分库功能(ShardingSphere)

有时候需要保留分表分库的能力，但当前还不需要分。所以，

 * 在只有一个`DataSource`并且没有分表配置时，暴露普通数据源，
 * 有分表分库需要时，暴露`Sharding`数据源。
 * 所有普通数据源在`FlywaveDataSources`中获得。

因为`ShardingJdbc`在执行SCHEMA变更时，存在一定的SQL解析问题(index,trigger)，
所以在做SCHEMA和`journal`功能时，使用普通数据源，使用`flywave`完成。
[参考SQL解析引擎](https://shardingsphere.apache.org/document/current/cn/features/sharding/principle/parse/)

业务开始可能不必须做分表考虑，当需要分表时，使用`flywave`工具生成表和迁移数据。
~~DQL,DML,DDL,TCL,DAL,DCL,GENERAL中，DDL和DCL使用真实数据源，其他使用sharding数据源。~~
默认下，DDL,DCL使用`plain数据源`，DML等使用`shard数据源`执行。
此外，可以手动指定数据源，高于默认规则，以完成定制的更新需求。

 * 格式为 `单行注释` + `空格` + [`本表`] + (`@plain`|`@shard`)
 * 指定了`本表`的SQL，不会尝试解析。
 * 指定`本表`在SQL语句中不存在时，不影响SQL执行，只是忽略`跟踪表`替换。

``` sql
-- @shard 强制使用shard数据源，自动解析本表为 sys_light_sequence
DROP TABLE IF EXISTS `sys_light_sequence`;
-- @plain 强制使用原始数据源，自动解析本表为sys_commit_journal
DROP TABLE IF EXISTS `sys_commit_journal`;
-- wgs_order@plain 强制使用原始数据源，并直接指定本表为wgs_order，因为语法中没有本表。
DROP TRIGGER IF EXISTS `wgs_order$bd`;
```

关于注释，只解析和忽略整行的，不处理行尾或行中的注释。
因需求简单，未使用语法分析，只属于正则和字符串替换方式进行。
单双引号内括起来的字符串内容会被忽略，不会被替换的。

[DDL - Data Definition Statements](https://dev.mysql.com/doc/refman/8.0/en/sql-syntax-data-definition.html)

 * `ALTER TABLE`
 * `CREATE INDEX`
 * `CREATE TABLE`
 * `CREATE TRIGGER`
 * `DROP INDEX`
 * `DROP TABLE` 只可以一次一个table
 * `DROP TRIGGER` 需要手动指定本表，系统根据本表和分表命名规则执行
 * `TRUNCATE TABLE`
 
 其中，系统自带的`journal`的trigger外，手写删除会出现数据不一致。
  
[DML - Data Manipulation Statements](https://dev.mysql.com/doc/refman/8.0/en/sql-syntax-data-manipulation.html)

 * `DELETE`
 * `INSERT`
 * `REPLACE`
 * `UPDATE`

以上类型之外的SQL，请使用注解，强制指定`数据源`和`本表名`，跳过SQL自动解析。
理论上，不应该使用flywave执行DDL,DCL和DML之外的SQL，不属于版本管理范围。

数据库运维应该使用其他工具链，如[godbart](https://gitee.com/trydofor/godbart)由专业DBA执行。


## 2.4.分布式主键(LightId)

分布式主键有`snowflake`方案可选，但`LightId`支持CRC8做伪随机编码使用。
参考[pro.fessioinal.mirana](https://gitee.com/trydofor/pro.fessional.mirana)项目。

实现了基于JDBC的LightId，在Jooq生成pojo时，自动继承`LightIdAware`，可以当作key使用。
具体细节参考`LightIdService`的实现。


## 2.5.数据库操作

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

JdbcTemplate用于功能性或复杂的数据库操作，以自动注入Bean。
参考 `JdbcTemplateConfiguration`的注入。

命名上，接口直接命名，不需要前后缀，Dto放在接口之内。
实现类，放到`impl/`包内，用后缀表示实现方式不同。

 * `Jooq`，Jooq实现
 * `Jdbc`，JdbcTemplate实现
 * `Impl`，混合实现。
 
如`LightId`在读写分离时，需要强制master，可使用注解`MasterRouteOnly`。

## 2.6.JOOQ与ShardingSphere的兼容问题

注意，jooq生成代码，默认使用`table.column`限定列名，而ShardingJdbc做当前版本不支持。
最优解决办法是使ShardingJdbc支持，当前最简单的办法是修改Jooq生成策略，参考以下Issue。


 * [JOOQ#9055 should NO table qualify if NO table alias](https://github.com/jOOQ/jOOQ/pull/9406)
 * [ShardingSphere#2859 `table.column` can not sharding](https://github.com/apache/incubator-shardingsphere/issues/2859)

使用Jooq的主要原因之一是`限制的艺术`，避免写出比较复杂的SQL，所以约定如下，

 * 鼓励单表操作，放在`single`包内，使用`本名`(如，TstDemoTable.TST_DEMO)
 * 操作多表时，**一定** 使用`别名`(如，TstDemoTable.AS_F1)
 * INSERT 使用`本名`，不可使用`别名`，在a9m时，使用`本名`
 * DELETE 使用`本名`，不可使用`别名`，在a9m时，使用`本名`
 * UPDATE 使用`别名`优先于`本名`，在a9m时，使用`本名`
 * SELECT 使用`别名`优先于`本名`，在a9m时，使用`本名`
 * **不要** 使用中文表名，例子代码只是极端测试。

使用patch版本的`jooq-a9m`(a9 mod)，可以都是有`本名`，参考pom中的私有库，或直接替换class，方法有三。

 * 私有库，`install`或`deploy` [jooq-a9m](https://github.com/trydofor/jOOQ) 
 * 静态替换，用`/test/resources/patch/*`到对应位置。
 * 动态替换，用`classloader`或`字节码修改术`搞黑科技，不推荐。

JOOQ参考资料

 * [Jooq patch](https://github.com/trydofor/jOOQ/commit/0be23d2e90a1196def8916b9625fbe2ebffd4753)
 * [批量操作 record](https://www.jooq.org/doc/3.12/manual/sql-execution/crud-with-updatablerecords/batch-execution-for-crud/)
 * [批量操作 jdbc](https://www.jooq.org/doc/3.12/manual/sql-execution/batch-execution/)
 * [使用别名，支持分表](https://www.jooq.org/doc/3.12/manual/sql-building/table-expressions/aliased-tables/)
 * [SQL的执行](https://www.jooq.org/doc/3.12/manual/sql-execution/)

## 2.7.测试用例

`kotlin`中的测试用例，主要是场景演示。需要单个执行，确保成功。
统一执行时，springboot为了有效使用资源，不会全部重新初始化`context`，
这样会使有些`ApplicationListener`得不到触发，可能导致部分TestCase失败。

发生部分失败部分成功时，重新执行失败部分，直到成功即可。