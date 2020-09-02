# 2.1.虚空假面④时间结界(flywave)

在时空中创造一个泡状遮罩，将所有位于其中的单位定住。

![faceless_void](./faceless_void_chronosphere.png)

## 2.1.1.飞波(flywave)是一个实践

工程实际中，我们响应变化，应对变更，都有成熟的工具，源代码由git管理，任务由jira管理，
那么静态的表结构变更，运行时的数据变更，我们怎么管理和记录，跟踪和调查，分支和回滚呢？

  * 项目从零开始，需求逐渐浮现，如果管理每周迭代中的字段新增，废弃和修改
  * local, develop, product的代码应该对应哪个schema和data
  * 线上一个订单数据错误，由哪个even引起，同一even都更新了哪些条数据
  * sql搞错where，发现时已晚，如何确认受影响数据，快速恢复到更新前
  * 项目一点点变大，从单库单表，平滑的过度到，读写分离，分表分库
  * 需要离线功能，同样代码可以跑本地h2database，云端mysql

如果你的项目遇到了以上的数据库及数据的问题，flywave的思想（不一定是本工程）适合你。

实际项目中，schema结构的变更十分频繁，需要控制好local/develop/product的版本和节奏。
`flyway`是个不错的选择，但用它有点牛刀杀鸡，所以新造个轮子叫`flywave`，可以，

 * 根据 `/wings-flywave/master/**/*.sql` 数据库和数据的统一管理。
 * 根据 `/wings-flywave/branch/**/*.sql` sql的分支管理。
 * 根据 `sys_schema_version`表，控制数据库版本，升级和降级。
 * 根据 `sys_schema_journal`表，完成自动记录数据变更。

flywave的sql文件都受git管理，所以，如无必须，勿搞复杂分支，单线实践最佳。
这里的branch，目标是sql管理，而非数据库和数据管理。
就是说，数据库中只有master一条线，而本地sql可以有多条线。

实际项目经验中，数据库只存储数据，不存业务逻辑。所以，必须使用基本的SQL和数据库功能。
这些功能包括，表，索引，触发器。不包括，视图，存储过程，外键约束及个别特性。

`flywave`体系下，有一下约定和概念，理解这些约定，有利于清晰业务架构。

JDBC数据源(DataSource)，分为两种，他们会存在于`FacelessDataSources`中，

 * 分片数据源(Shard)，具有分表分库功能，如`ShardingSphere`。
 * 普通数据源(Plain)，没有sharding功能，只在单个DB上执行。

当只有一个数据源，且没有sharding配置时，两者实际为同一个值。
`flywave`会根据后续的场景规则，自动或手动使用不同的数据源执行DDL和DML等。

数据表(Table)，语义上有三种表，用来表示用途和使用场景。

 * 本表(plain)，也叫普通表，正常的命名，如英数下划线，甚至中文。
 * 分表(shard)，本表+`_#`后缀，`#`为对N取模(0..N-1)（左侧无0填充）。
 * 跟踪表(trace)，也叫log表，影子表。本表或分表+`\$\w+`，`$`视为分隔符。
 * 以`nut`表示，本表+分表；以`log`表示，跟踪表。

这三种表，满足以下规定，并且跟本表保持同步更新。

 * 本表一定存在，即便存在分表时，也会存在一个本表，用来保持原始表结构。
 * 分表，具有和本表一样的表结构，索引和触发器，同步更新。
 * 跟踪表具有和本表(staff，对应的本表和分表)相同的字段和类型，同步更新。
 * 跟踪表为触发器使用，包含了一些标记字段，建议以`_*`格式。
 * 三种表，一定会保持相同的表结构（名字，类型，前后关系），同步更新。


`$`是命名中的特殊字符，定义`跟踪表`。比如替换时。
sql的书写规则详见[数据库约定](../wings-faceless-flywave/src/main/resources/wings-flywave/readme.md)

数据库中尽量不要`nullable`，约定默认值代替，如`convention.EmptyValue`类

关于`/wings-flywave/`目录，有以下几种实践操作。

 * 存在`fulldump/{db}/`目录，用来管理当前主要db的最新schema
 * 存在`master/init/`目录，最开始的初始化脚本
 * 存在`master/{m##}/`目录，用来按里程碑管理变更脚本
 * 存在`branch/{m##}/`目录，那git-flow的命名实践

## 2.1.2.数据的版本管理(journal)

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
默认分表有自己的`更新表`(`TABLE_#$upd`)，但共享同一个`删除表`(`TABLE$del`)。
模板中，预定义以下DDL变量，避开spring变量替换，使用胡子`{{}}`表示法，名字全大写。

 * `{{PLAIN_NAME}}` 目标表的`本表`名字
 * `{{TABLE_NAME}}` 目标表名字，可能是本表，分表，跟踪表
 * `{{TABLE_BONE}}` 目标表字段(至少包含名字，类型，注释)，不含索引和约束
 * `{{TABLE_PKEY}}` 目标表的主键中字段名，用来创建原主键的普通索引。

对于删除的数据，无法优雅的设置`commit_id`，此时若是需要journal，则要先更新再删除。
目前，在不使用sql标记或解析的情况下，提供了2种方法，手工类优先于自动拦截。

 * 通过工具类`JournalHelp`，手动执行`delete##`。
 * 自动对`delete from ## where id=? and commit_id=?`格式进行拦截。

自动拦截`spring.wings.trigger.journal-delete.enabled`默认关闭。
因为违反`静态高于动态，编译时高于运行时`团队规则，且性能和限制不好控制。

## 2.1.3.测试用例

`kotlin`中的测试用例，主要是场景演示。需要单个执行，确保成功。
统一执行时，springboot为了有效使用资源，不会全部重新初始化`context`，
这样会使有些`ApplicationListener`得不到触发，可能导致部分TestCase失败。

## 2.1.4.常见问题

### 01.控制flywave时，spring找不到bean `SchemaRevisionManager`

在2.2.6后续中，默认关闭了spring.wings.flywave.enabled=false
初始化的时候需要打开，例如在test中增加临时打开
`@SpringBootTest(properties = "spring.wings.flywave.enabled=true")`

### 02.版本更新，异常说缺少字段branches

在2.2.7版后，对sys_schema_version增加了分支支持，之前的版本需要手动维护。
执行`branch/hotfixes/v2.2.7-fix`的`2019_0512_02`即可。

### 03.哪些测试或例子适合了解flywave

 * SchemaJournalManagerTest - 包含了shard和track的例子测试
 * SchemaRevisionMangerTest - 基本的版本管理测试
 * SchemaShardingManagerTest - shard和数据迁移测试

 * WingsFlywaveInitDatabaseSample 管理数据库版本例子
 * ConstantEnumGenSample - enum类生成例子
 * JooqJavaCodeGenSample - jooq类生成例子
 * WingsSchemaDumper - schema和数据dump例子
 * WingsSchemaJournal - track表控制例子

### 04.flywave中确认危险语句

 * 带有`ask@*`注解的sql，强制确认
 * undo 语句确认 `wings.flywave.ver.ask-undo=true`
 * drop 类语句确认 `wings.flywave.ver.ask-drop=true`
 * drop 类语句定义 `wings.flywave.ver.drop-reg[0]`

如果UnitTest中控制台中无响应，需要在IDE中打开 console，如在Idea中
`-Deditable.java.test.console=true` ('Help' > 'Edit Custom VM Options...')

### 05.影子表不需要增加index

如果已有索引更新到了影子库，并影响了写入性能，或唯一索引，通过以下sql查看。

```sql
SELECT DISTINCT CONCAT('DROP INDEX ',INDEX_NAME,' ON ',TABLE_NAME, ';')
FROM
	INFORMATION_SCHEMA.STATISTICS
WHERE
	TABLE_SCHEMA = DATABASE()
	AND INDEX_NAME NOT IN ('PRIMARY','PLAIN_PK')
	AND TABLE_NAME LIKE '%$%';
```

对于通过，`apply@` 语句指定更新表。比如，以下更新本表和分表，不更新跟踪表
```sql
-- @plain apply@nut error@skip
ALTER TABLE `win_user`
  DROP INDEX `ft_auth_set`,
  DROP INDEX `ft_role_set`;
```

### 06.如何select所有本表，分表，影子表

```sql
-- 仅影子表
SELECT 
    reverse(substring(reverse(table_name),length(substring_index(table_name,'$',-1))+1)) as tbl,
    group_concat(SUBSTRING_INDEX(table_name,'$',-1)) as log
FROM INFORMATION_SCHEMA.TABLES
WHERE table_schema = DATABASE()
    AND table_name like '%$%'
    group by tbl;

-- 仅分表
SELECT 
    reverse(substring(reverse(table_name),length(substring_index(table_name,'_',-1))+1)) as tbl,
    group_concat(SUBSTRING_INDEX(table_name,'_',-1)) as num
FROM INFORMATION_SCHEMA.TABLES
WHERE table_schema = DATABASE()
    AND table_name REGEXP '_[0-9]+$'
    group by tbl;

-- 仅主表
SELECT table_name
FROM INFORMATION_SCHEMA.TABLES
WHERE table_schema = DATABASE()
    AND table_name NOT REGEXP '_[0-9]+$'
    AND table_name NOT LIKE '%$%';
```