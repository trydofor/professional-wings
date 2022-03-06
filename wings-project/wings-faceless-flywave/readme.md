# 2.1.虚空假面④时间结界(flywave)

在时空中创造一个泡状遮罩，将所有位于其中的单位定住。

![faceless_void_chronosphere](faceless_void_chronosphere.png)

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

不适用场景及风险提示，**数据十分重要**，重要操作需要专业人士double check

 * 线上数据很热或非常大，以至于没有一刀切的升级的计划，flywave无法胜任。
 * 涉及线上表rename等，建议专业人士介入，采用pt-online-schema-change思路。

实际项目中，schema结构的变更十分频繁，需要控制好local/develop/product的版本和节奏。
`flyway`是个不错的选择，但用它有点牛刀杀鸡，所以新造个轮子叫`flywave`，可以，

 * 根据 `/wings-flywave/master/**/*.sql` 数据库和数据的统一管理。
 * 根据 `/wings-flywave/branch/**/*.sql` sql的分支管理。
 * 根据 `sys_schema_version`表，控制数据库版本，升级和降级。
 * 根据 `sys_schema_journal`表，完成自动记录数据变更。
 * 实际扫描`/**/*.sql`，因此次级目录表序表意为上，结构层次不影响版本排序

flywave的sql文件都受git管理，所以，如无必须，勿搞复杂分支，单线实践最佳。
这里的branch，目标是sql管理，而非数据库和数据管理。
就是说，数据库中只有master一条线，而本地sql可以有多条线。

实际项目经验中，数据库只存储数据，不存业务逻辑。所以，必须使用基本的SQL和数据库功能。
这些功能包括，表，索引，触发器。不包括，视图，存储过程，外键约束及个别特性。

`flywave`体系下，有一下约定和概念，理解这些约定，有利于清晰业务架构。

JDBC数据源(DataSource)，分为两种，他们会存在于`DataSourceContext`中，

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


`$`是命名中的特殊字符，定义`跟踪表`，但dollar在很多环境中需要转义，如shell，regexp。
sql的书写规则详见[数据库约定](../wings-faceless-flywave/src/main/resources/wings-flywave/readme.md)

数据库中尽量不要`nullable`，约定默认值代替，如`convention.EmptyValue`类

关于`/wings-flywave/`目录，有以下几种实践操作。

 * 存在`fulldump/{db}/`目录，用来管理当前主要db的最新schema
 * 存在`master/00-init/`目录，最开始的初始化脚本
 * 存在`master/01-##/`目录，用来按里程碑或年月管理变更脚本
 * 存在`branch/##/##/`目录，可以以git-flow的命名实践，内建如下
   - feature 功能组件，可以作为选择项，按需添加
   - support 支撑型，如临时数据维护，调查
   - somefix 包括patch, bugfix, hotfix

## 2.1.2.数据的版本管理(journal)

`逻辑删除`已成为行业标配，但如果删除数据只在调查时使用，则不是最优解。

 * 破坏查询索引。每个查询都要`is_deleted=0`，且90%以上数据为true。
 * 破坏唯一约束。可以重复逻辑删除，所以无法简单有效的对数据unique。

`逻辑删除`在wings中存在的唯一目的是解决数据的`溯源`问题，否则应该直接删除。
它也叫`标记删除`，类似java的GC，在引用计数为0时，会被(立即或批处理)删除。


任何数据变动，都应该有`commit_id`，记录下事件信息（人，事件，业务信息等）。
最新的数据留在`本表`，旧数据通过`trigger`插入`跟踪表`。跟踪表也称`$log`表，
因最初的命名规则是，本表+`$log`后缀，但后因某些工具缺陷，误把`$`当做变量处理，
尽管其是mysql官方合法字符，flywave在210版后调整为双下划线`__`后缀。

`journal`通过`sys_schema_journal`生成`跟踪表`和`触发器`。

 * 根据`log_insert`创建 `after insert` 触发器。
 * 根据`log_update`创建 `after update` 触发器。
 * 根据`log_delete`创建 `before delete` 触发器。

通过配置文件指定模板来定义DDL，默认设置参考`wings-flywave.properties`。
默认分表有自己的`log表`(`TABLE_#$log`)，一个table的触发器共用一个log表。
模板中，预定义以下DDL变量，避开spring变量替换，使用胡子`{{}}`表示法，名字全大写。

 * `{{PLAIN_NAME}}` 目标表的`本表`名字
 * `{{TABLE_NAME}}` 目标表名字，可能是本表，分表，跟踪表
 * `{{TABLE_BONE}}` 目标表字段(至少包含名字，类型，注释)，不含索引和约束
 * `{{TABLE_PKEY}}` 目标表的主键中字段名，用来创建原主键的普通索引。

对于删除的数据，无法优雅的设置`commit_id`，此时若是需要journal，则要先更新再删除。
目前，在不使用sql标记或解析的情况下，提供了2种方法，手工类优先于自动拦截。

 * 通过工具类`JournalHelp`，手动执行`delete##`。
 * 自动对`delete from ## where id=? and commit_id=?`格式进行拦截。

自动拦截`spring.wings.faceless.jooq.enabled.journal-delete`默认关闭。
因为违反`静态高于动态，编译时高于运行时`团队规则，且性能和限制不好控制。

可设置session级变量`DISABLE_FLYWAVE`使trigger失效，如数据恢复等无trigger情况。
* disable - `SET @DISABLE_FLYWAVE = 1;`时，trigger无效。
* enable - 当session结束时，trigger自动恢复有效。
* enable - `SET @DISABLE_FLYWAVE = NULL;`。

参考资料
* https://dev.mysql.com/doc/refman/8.0/en/trigger-syntax.html

## 2.1.4.测试用例

`kotlin`中的测试用例，主要是场景演示。需要单个执行，确保成功。
统一执行时，springboot为了有效使用资源，不会全部重新初始化`context`，
这样会使有些`ApplicationListener`得不到触发，可能导致部分TestCase失败。

## 2.1.3.注解指令

flywave提供了以下有特殊功能的`sql注释`，称为`注解指令`

 * 格式为 `特征前缀` + `本表`? + `数据源`? + `目标表`? + `错误处理`? + `确认语句`?
   - `特征前缀` = `^\s*-{2,}\s+`，即，单行注释 + `空格`
   - `本表` = `[^@ \t]+`，即，合法表名
   - `数据源` = `@plain`|`@shard`，固定值
   - `目标表` = `\s+apply@[^@ \t]+`，即，固定值，正则
   - `错误处理` = `\s+error@(skip|stop)`，即，出错时停止还是继续
   - `确认语句` = `\s+ask@[^@ \t]+`，即，确认语句，比如危险
   - `触发器影响` = `@trigger`，即，是否影响trigger
 * 指定了`本表`的SQL，不会尝试解析。
 * 指定的`本表`在SQL语句中不存在时，不影响SQL执行，只是忽略`跟踪表`替换。
 * `目标表` 不区分大小写，全匹配。其中内定以下简写
    - 空，默认适配全部，本表+分表+跟踪表
    - `apply@nut` 只适配本表和分表 `[_0-9]*`
    - `apply@log` 只适配跟踪表 `__[a-z]*`
    - 注意，目标表不是if语句，不作为条件检查
 * `错误处理` 默认`stop`以抛异常结束，`skip`表示忽略异常继续执行。
 * `确认语句` 默认std.out输出，在std.in等待确认输入
 * 注解的表达式为 `([^@ \t]+)?@([^@ \t]+)`

``` sql
-- ask@drop-database
DROP TABLE sys_schema_version;
-- @shard 强制使用shard数据源，自动解析本表为 sys_light_sequence
DROP TABLE IF EXISTS `sys_light_sequence`;
-- @plain 强制使用原始数据源，自动解析本表为sys_commit_journal
DROP TABLE IF EXISTS `sys_commit_journal`;
-- wgs_order@plain 强制使用原始数据源，并直接指定本表为wgs_order，因为语法中没有本表。
DROP TRIGGER IF EXISTS `wgs_order$bd`;
-- apply@win_admin[_0-0]* error@skip 可以解析本表，应用分表，忽略错误
ALTER TABLE `win_admin` DROP INDEX ix_login_name;
-- apply@nut error@skip 等效于上一句
ALTER TABLE `win_admin` DROP INDEX ix_login_name;
-- apply@log error@skip 只适应于跟踪表
ALTER TABLE `win_admin` DROP INDEX ix_login_name;
```

关于注释，只解析和忽略整行的，不处理行尾或行中的注释。
因需求简单，未使用语法分析，只属于正则和字符串替换方式进行。
单双引号内括起来的字符串内容会被忽略，不会被替换的。

推荐使用单行注释`--`，对应多行注释`/* */`不可置于行中。

## 2.1.5.常见问题

### 01.控制flywave时，spring找不到bean `SchemaRevisionManager`

在2.2.6后续中，默认关闭了spring.wings.faceless.flywave.enabled.module=false
初始化的时候需要打开，例如在test中增加临时打开
`@SpringBootTest(properties = "spring.wings.faceless.flywave.enabled.module=true")`

### 02.版本更新，异常说缺少字段branches

在2.2.7版后，对sys_schema_version增加了分支支持，之前的版本需要手动维护。
执行`branch/somefix/v227-fix`的`2019_0512_02`即可。

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
 * undo 语句确认 `wings.faceless.flywave.ver.ask-undo=true`
 * drop 类语句确认 `wings.faceless.flywave.ver.ask-drop=true`
 * drop 类语句定义 `wings.faceless.flywave.ver.drop-reg[drop-table]`

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
	AND INDEX_NAME NOT IN ('PRIMARY','RAW_TABLE_PK')
	AND TABLE_NAME RLIKE '%$%';
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
    reverse(substring(reverse(table_name),length(substring_index(table_name,'__',-1))+1)) as tbl,
    group_concat(SUBSTRING_INDEX(table_name,'__',-1)) as log
FROM INFORMATION_SCHEMA.TABLES
WHERE table_schema = DATABASE()
    AND table_name RLIKE '__'
    GROUP BY tbl;

SELECT
   table_name,
   CONCAT('DROP TABLE IF EXISTS ',table_name,';')
FROM INFORMATION_SCHEMA.TABLES
WHERE table_schema = DATABASE()
  AND table_name RLIKE '\\$|__';

-- 仅分表
SELECT 
    reverse(substring(reverse(table_name),length(substring_index(table_name,'_',-1))+1)) as tbl,
    group_concat(SUBSTRING_INDEX(table_name,'_',-1)) as num
FROM INFORMATION_SCHEMA.TABLES
WHERE table_schema = DATABASE()
    AND table_name RLIKE '_[0-9]+$'
    group by tbl;

SELECT
   table_name,
   CONCAT('DROP TABLE IF EXISTS ',table_name,';')
FROM INFORMATION_SCHEMA.TABLES
WHERE table_schema = DATABASE()
  AND table_name RLIKE '_[0-9]+$';

-- 仅主表
SELECT table_name
FROM INFORMATION_SCHEMA.TABLES
WHERE table_schema = DATABASE()
    AND table_name NOT REGEXP '_[0-9]+$'
    AND table_name NOT RLIKE '\\$|__';
```

### 07.如果使用flywave管理老工程

对于老工程，需要保留原来的表结构和数据，可能无法使用wings的命名，分作以下情况。

* 不能用`sys_schema_*`表，可以通过wings-flywave-79.properties配置设置对于表，并手工创建同结构表。
* 希望用`sys_schema_*`表，也希望版本连续，可通过replace方法把1ST_SCHEMA改名为新名字。
* 不希望rename的，可以使用branch分支管理初始化脚本，使用forceExecuteSql方法执行

以上方法，推荐使用最后一种，做好手工初始化后，后续通过flywave管理数据库版本。

除了初始版本，会在checkAndInit时执行外，其他版本必须显示的publish或execute

### 08.如果获得或删除所有trigger

```sql
SELECT
	EVENT_OBJECT_TABLE,
	TRIGGER_NAME,
	ACTION_TIMING,
	EVENT_MANIPULATION,
	ACTION_STATEMENT
FROM
	INFORMATION_SCHEMA.TRIGGERS
WHERE
  EVENT_OBJECT_SCHEMA = database();

-- 获取创建trigger的SQL;
-- DELIMITER $$
SELECT
   TRIGGER_NAME,
   CONCAT('DROP TRIGGER IF EXISTS ',TRIGGER_NAME,';'),
   CONCAT('CREATE TRIGGER `', TRIGGER_NAME, '` ',
          ACTION_TIMING, ' ', EVENT_MANIPULATION, ' ON `', EVENT_OBJECT_TABLE, '` FOR EACH ROW ',
          ACTION_STATEMENT, '$$')
FROM
   INFORMATION_SCHEMA.TRIGGERS
WHERE
   EVENT_OBJECT_SCHEMA = database();

-- 符合flywave命名规则的
SELECT
   TRIGGER_NAME,
   concat('DROP TRIGGER IF EXISTS ',TRIGGER_NAME,';')
FROM
   INFORMATION_SCHEMA.TRIGGERS
WHERE
   EVENT_OBJECT_SCHEMA = DATABASE()
  AND TRIGGER_NAME RLIKE '^(bi|ai|bu|au|bd|ad)__';
```

### 09.获取log表的数据量

```sql
SELECT
    table_schema,
    concat('delete from ',table_name,' where _dt < \'2020-07-01\';'),
    CEILING(data_length / 1024 / 1024) AS data_mb,
    CEILING(index_length / 1024 / 1024) AS index_mb,
    CEILING((data_length + index_length) / 1024 / 1024) AS all_mb,
    table_rows
FROM
    information_schema.tables
WHERE
    table_name RLIKE '\\$|__'
    and table_schema = DATABASE()
ORDER BY table_schema , all_mb DESC;
```

### 10.手动修复历史log模板

```sql
ALTER TABLE `{{TABLE_NAME}}__`
   MODIFY COLUMN `_id` BIGINT(20) NOT NULL AUTO_INCREMENT FIRST,
   ADD COLUMN `_dt` DATETIME(3) NOT NULL DEFAULT '1000-01-01 00:00:00' AFTER `_id`,
   ADD COLUMN `_tp` CHAR(1) NOT NULL DEFAULT 'Z' AFTER `_dt`;

DELIMITER $$
CREATE TRIGGER `ai__{{TABLE_NAME}}` AFTER INSERT ON `{{TABLE_NAME}}`
   FOR EACH ROW BEGIN
   IF (@DISABLE_FLYWAVE IS NULL) THEN
      INSERT INTO `{{TABLE_NAME}}__` SELECT NULL, NOW(3), 'C', t.* FROM `{{TABLE_NAME}}` t
      WHERE t.id = NEW.id ;
   END IF;
END
$$

CREATE TRIGGER `au__{{TABLE_NAME}}` AFTER UPDATE ON `{{TABLE_NAME}}`
   FOR EACH ROW BEGIN
   IF (@DISABLE_FLYWAVE IS NULL) THEN
      INSERT INTO `{{TABLE_NAME}}__` SELECT NULL, NOW(3), 'U', t.* FROM `{{TABLE_NAME}}` t
      WHERE t.id = NEW.id ;
   END IF;
END
$$

CREATE TRIGGER `bd__{{TABLE_NAME}}` BEFORE DELETE ON `{{TABLE_NAME}}`
   FOR EACH ROW BEGIN
   IF (@DISABLE_FLYWAVE IS NULL) THEN
      INSERT INTO `{{TABLE_NAME}}__` SELECT NULL, NOW(3), 'D', t.* FROM `{{TABLE_NAME}}` t
      WHERE t.id = OLD.id ;
   END IF;
END
$$
DELIMITER ;
```

### 11.根据log表，局部恢复数据

使用动态SQL，从log表获得最新数据，并REPLACE INTO的主表，
期间需要关闭 Trigger @DISABLE_FLYWAVE = 1;

为了避免业务干扰，可把log的max_id写入临时表，或固化的sql

```sql
-- SET @group_concat_max_len = @@global.max_allowed_packet;
SET @tabl = 'win_user_basis';
SET @cols = (
SELECT CONCAT('`',GROUP_CONCAT(COLUMN_NAME SEPARATOR '`, `'), '`') 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = database() AND TABLE_NAME = @tabl
ORDER BY ORDINAL_POSITION
);
SET @restoreSql = CONCAT(
-- 'REPLACE INTO ', @tabl,
' SELECT ', @cols,' FROM ', @tabl,'__ WHERE (_id,id) IN (',
' SELECT max(_id), id FROM ', @tabl,'__ ',
' WHERE _tp in (\'D\')'
' GROUP BY id',
')');

SELECT @restoreSql;
-- 

SET @DISABLE_FLYWAVE = 1;
PREPARE stmt FROM @restoreSql;
EXECUTE stmt;
SET @DISABLE_FLYWAVE = NULL;
```

### 12.如何手工生成日志表和trigger

使用flywave，可以有更好的提示，记录。但也可以通过手工sql来完成.

```sql
-- 生成log表
SET @tabl = 'owt_lading_main';
SET @cols = (
SELECT
	GROUP_CONCAT(CONCAT('`',COLUMN_NAME, '` ', COLUMN_TYPE,' COMMENT \'', replace(COLUMN_COMMENT,'\'','\\\''),'\''))
FROM
	INFORMATION_SCHEMA.COLUMNS
WHERE
	TABLE_SCHEMA = database()
	AND TABLE_NAME = @tabl
	ORDER BY ORDINAL_POSITION
);
SET @prik = (
SELECT
	GROUP_CONCAT(CONCAT('`',COLUMN_NAME, '`'))
FROM
	INFORMATION_SCHEMA.COLUMNS
WHERE
	TABLE_SCHEMA = database()
	AND TABLE_NAME = @tabl
    AND COLUMN_KEY = 'PRI'
	ORDER BY ORDINAL_POSITION
);

SET @tracerSql = CONCAT(
'CREATE TABLE ', @tabl, '__ (',
' `_id` BIGINT(20) NOT NULL AUTO_INCREMENT, ',
' `_dt` DATETIME(3) NOT NULL DEFAULT \'1000-01-01 00:00:00\', ',
' `_tp` CHAR(1) NOT NULL DEFAULT \'Z\', ',
@cols, ', '
' PRIMARY KEY (`_id`), ',
' KEY `RAW_TABLE_PK` (', @prik, ')',
 ') ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;');

SELECT @tracerSql;

PREPARE stmt FROM @tracerSql;
EXECUTE stmt;

-- 生成trigger sql
SET @tabl = 'win_user_basis';
SET @triggerSql = CONCAT(
   'DELIMITER $$\n',
   'CREATE TRIGGER `ai__', @tabl, '` AFTER INSERT ON `', @tabl,'` FOR EACH ROW BEGIN',
   ' IF (@DISABLE_FLYWAVE IS NULL) THEN',
   ' INSERT INTO `',@tabl ,'__` SELECT NULL, NOW(3), \'C\', t.* FROM `',@tabl ,'` t WHERE t.id = NEW.id ;',
   ' END IF;',
   'END$$\n',
   'CREATE TRIGGER `au__', @tabl, '` AFTER UPDATE ON `', @tabl,'` FOR EACH ROW BEGIN',
   ' IF (@DISABLE_FLYWAVE IS NULL) THEN',
   ' INSERT INTO `',@tabl ,'__` SELECT NULL, NOW(3), \'U\', t.* FROM `',@tabl ,'` t WHERE t.id = NEW.id ;',
   ' END IF;',
   'END$$\n',
   'CREATE TRIGGER `bd__', @tabl, '` BEFORE DELETE ON `', @tabl,'` FOR EACH ROW BEGIN',
   ' IF (@DISABLE_FLYWAVE IS NULL) THEN',
   ' INSERT INTO `',@tabl ,'__` SELECT NULL, NOW(3), \'D\', t.* FROM `',@tabl ,'` t WHERE t.id = OLD.id ;',
   ' END IF;',
   'END$$\n'
   );

select @triggerSql;
```

### 13.工具或DB不支持`$`命名怎么办

从210开始，wings以双下划线命名，取代dollar命名。

英数美刀下划线(`[0-9,a-z,A-Z$_]`)都是mysql官方无需转义的合法的[命名字符](https://dev.mysql.com/doc/refman/5.7/en/identifiers.html)
但某些不完备的云DB或工具，未做好处理，属于其功能缺陷。

若无法更换DB或工具，可以修改wings的默认约定及实现。
此选项，为隐藏功能，通过基本测试，通常情况下不建议使用。

* 变更后缀的分隔符，如`__`，两个下划线。
* 使用前缀，如`_`，以下划线开头。

每个方案，都会影响flywave的配表及脚本，需要逐一修改。
使用前缀时，还会影响表名排序，视觉上无法直观的看到主表是否有跟踪表。

简单的方式是修改`wings.faceless.flywave.ver.format-`配置。

原理是修改 SqlSegmentProcessor.setXXXFormat方法，设置表达式，
要求表达式必须精准，避免误判主表，分表，跟踪表的关系。
以`XXX`表示主表，`#`表示字母，SqlSegmentProcessor 默认提供了3种表达式

* TRACE_DOLLAR - 以dollar`$`后缀分隔，如`XXX$#`
* TRACE_SU2_LINE - 以双下划线`__`后缀分隔，如`XXX__#`
* TRACE_PRE_LINE - 以单下划线`_`前缀分隔，如`_XXX`或`_#_XXX`

### 14.kotlin编译失败，可能的盘错点

* kotlin-maven-plugin 插件，要同时编译java和kotlin
* kotlin-stdlib-jdk8 这是最新的stdlib
* mvn profile中的maven.compiler.target 优先与pom.xml
* JAVA_HOME是否指定正确的jdk版本

