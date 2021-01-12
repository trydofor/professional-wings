# 2.3.虚空假面②时间膨胀(shard)

对膨胀的数据增加一个状态，时间在此停止，分而治之，合而用之。

![faceless_void_time_dilation](./faceless_void_time_dilation.png)

使用ShardingJdbc完成数据的分表分库功能，平稳处理大数据量。

## 2.3.1.分表分库的不足

因为`ShardingJdbc`在执行SCHEMA变更时，存在一定的SQL解析问题(index,trigger)，
所以在做SCHEMA和`journal`功能时，使用普通数据源，使用`flywave`完成。
[参考SQL解析引擎](https://shardingsphere.apache.org/document/current/cn/features/sharding/principle/parse/)

业务开始可能不必须做分表考虑，当需要分表时，使用`flywave`工具生成表和迁移数据。
~~DQL,DML,DDL,TCL,DAL,DCL,GENERAL中，DDL和DCL使用真实数据源，其他使用sharding数据源。~~
默认下，DDL,DCL使用`plain数据源`，DML等使用`shard数据源`执行。
此外，可以手动指定数据源，高于默认规则，以完成定制的更新需求。

flywave提供了以下有特殊功能的`sql注释`，称为`注解指令`

 * 格式为 `特征前缀` + `本表`? + `数据源`? + `目标表`? + `错误处理`? + `确认语句`?
   - `特征前缀` = `^\s*-{2,}\s+`，即，单行注释` + `空格`
   - `本表` = `[^@ \t]+`，即，合法表名
   - `数据源` = `@plain`|`@shard`，固定值
   - `目标表` = `\s+apply@[^@ \t]+`，即，固定值，正则
   - `错误处理` = `\s+error@(skip|stop)`，即，出错时停止还是继续
   - `确认语句` = `\s+ask@[^@ \t]+`，即，确认语句，比如危险
 * 指定了`本表`的SQL，不会尝试解析。
 * 指定的`本表`在SQL语句中不存在时，不影响SQL执行，只是忽略`跟踪表`替换。
 * `目标表` 不区分大小写，全匹配。其中内定以下简写
    - 空，默认适配全部，本表+分表+跟踪表
    - `apply@nut` 只适配本表和分表 `[_0-9]*`
    - `apply@log` 只适配跟踪表 `\$\w+`
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
-- apply@ctr_clerk[_0-0]* error@skip 可以解析本表，应用分表，忽略错误
ALTER TABLE `win_admin` DROP INDEX ix_login_name;
-- apply@nut error@skip 等效于上一句
ALTER TABLE `win_admin` DROP INDEX ix_login_name;
-- apply@log error@skip 只适应于跟踪表
ALTER TABLE `win_admin` DROP INDEX ix_login_name;
```

关于注释，只解析和忽略整行的，不处理行尾或行中的注释。
因需求简单，未使用语法分析，只属于正则和字符串替换方式进行。
单双引号内括起来的字符串内容会被忽略，不会被替换的。

## 2.3.2.执行SQL中的事项

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