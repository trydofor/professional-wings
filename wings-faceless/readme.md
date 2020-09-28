# 2.虚空假面(faceless)

`Void`，J8脸, `public static void main`  
他是来自超维视界的一名访客，一个时间之外的境域。

![faceless_void](./faceless_void_full.png)

支持MySql系(mysql及分支,h2)的一套Sharding，并有表结构和数据变更的版本管理的基本套餐。

 * 可切换的分表分库功能(ShardingJdbc/PlainDataSource)
 * 高效递增非连续的分布式主键(LightId)
 * 从数据库自动生成enum和i18n代码, constantEnum,i18nEnum
 * 轻量SCHEMA版本管理(fly-wave @flywave)
 * DATA版本管理和追踪(journal/$log @flywave)
 * 全备份dump现有数据库的表结构和记录(@flywave)
 * 从数据库自动生成jooq代码，pojo, table, dao (@jooq)

## 2.1.飞波(flywave)是一个实践

工程实际中，我们响应变化，应当变更，都有成熟的工具，源代码由git管理，任务由jira管理，
那么静态的表结构变更，运行时的数据变更，我们怎么管理和记录，跟踪和调查，分支和回滚呢？

  * 项目从零开始，需求逐渐浮现，如果管理每周迭代中的字段新增，废弃和修改
  * local, develop, product的代码应该对应哪个schema和data
  * 线上一个订单数据错误，由哪个even引起，同一even都更新了哪些条数据
  * sql搞错where，发现时已晚，如何确认受影响数据，快速恢复到更新前
  * 项目一点点变大，从单库单表，平滑的过度到，读写分离，分表分库
  * 需要离线功能，同样代码可以跑本地h2database，云端mysql

具体内容，移步到子工程[时间结界(flywave)](../wings-faceless-flywave/readme.md)

## 2.2.强类型(jooq)数据库操作

推荐使用SqlMapping，因为ORM太重了，工程内使用Jooq和JdbcTemplate

MyBatis虽是大部分项目的首选，固有其优秀之处，但开发人员的懒惰或约束力量的不足，
使得项目不高效，偶尔很难维护，项目中容易蔓生出以下问题。

 * 经常被 `select *`，带有大量无用信息。
 * 很容易写出复杂的大SQL，使得服务难以拆分。
 * 字符串及弱类型，IDE的眷顾有限。

使用Jooq，强类型，编程高于配置，并且SQL友好，又恰好有限制能力的能力。

具体内容，移步到子工程[时间漫游(jooq)](../wings-faceless-jooq/readme.md)

## 2.3.分表分库功能(ShardingSphere)

有时候需要保留分表分库的能力，但当前还不需要分。所以，

 * 在只有一个`DataSource`并且没有分表配置时，暴露普通数据源，
 * 有分表分库需要时，暴露`Sharding`数据源。
 * 所有普通数据源在`FacelessDataSources`中获得。

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

## 2.5.数据库Enum类和多国语

schema版本2019_0521_01，定义了enum和i18n表，分别定义业务级枚举code，如状态，
可以使用`ConstantEnumGenerate`自动生成java类，保持db和java代码的一致。

i18n可以使用CombinableMessageSource动态添加，处理service内消息的多国语。

## 2.6.事件服务EventService

单进程的异步和解耦，guava的EventBus和Spring的Event都可以胜任。
为单Jvm，多Jvm提高一个基于数据库的Event服务，主要用来


## 2.9.H2本地库

在不方便提供mysql数据库的时候，如演示或本地数据库应用，可以使用H2，配置如下。
```
jdbc:h2:~/wings-init
;USER=trydofor;PASSWORD=moilioncircle
;MODE=MySQL;CASE_INSENSITIVE_IDENTIFIERS=TRUE
;AUTO_RECONNECT=TRUE;AUTO_SERVER=TRUE
```
其中，H2对mysql做了部分兼容，分表分库可以，trigger不支持，参考配置，

 * wings-conf/shardingsphere-datasource-79.properties
 * wings-conf/shardingsphere-datasource-79@init.properties

[H2官方文档](http://h2database.com/html/features.html)


## 2.10.常见问题

### 01.项目无法启动

去掉了默认的datasource配置，使用无法启动的方式来提醒必须配置数据库。

### 02.本地创建mysql docker

```bash
sudo tee /Users/trydofor/Docker/mysql/conf/moilioncircle.cnf << EOF
[mysqld]
innodb_file_per_table       = 1
innodb_ft_min_token_size    = 1
ft_min_word_len             = 1
character-set-server        = UTF8MB4
max_allowed_packet          = 1024M
skip_ssl
EOF
# 启动docker
docker run -d \
 --name mysql \
 --restart=unless-stopped \
 -e MYSQL_ALLOW_EMPTY_PASSWORD=yes \
 -v /Users/trydofor/Docker/mysql/conf:/etc/mysql/conf.d \
 -v /Users/trydofor/Docker/mysql/data:/var/lib/mysql \
 -p 3306:3306 \
mysql:5.7
```

### 03.如何自定义journalService

使用高优先级注入`journalService`，参考 example工程的 `SecurityJournalService`

### 04.日时零值和时区问题

执行环境和数据库要在同一时区，否则jooq和jdbc在以下过程会自动转换时区，引发问题。

如果服务器和执行环境时区不一致，可以通过以下方式协调。

* 通过wings的参数设置时区 `wings.i18n.zoneid=Asia/Shanghai`
* java的启动参数， `-Duser.timezone=Asia/Shanghai`
* mysql的jdbc的url参数， `serverTimezone=Asia/Shanghai`
* java的代码参数， `TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));`

引发问题的原因，目前断定为jdbc和timestamp历史问题

* jooq，使用timestamp作为localDatetime的值，设置preparedStatement。
* jdbc，setTimestamp(int parameterIndex, Timestamp x), 
  the JDBC driver uses the time zone of the virtual machine 
  to calculate the date and time of the timestamp in that time zone.

通过以下SQL可以在mysql端调查数据库执行过程

```sql
-- 查看 系统，程序，会话时区
SELECT @@system_time_zone,  @@global.time_zone, @@session.time_zone;
-- @@system_time_zone, @@global.time_zone, @@session.time_zone
-- UTC, Asia/Shanghai, Asia/Shanghai

-- mysql 执行日志(UTC)
select `id` from `win_user` where `delete_dt` <= '0999-12-31 16:00:00.0';
-- jooq 绑定日志(GMT+8)
select `id` from `win_user` where `delete_dt` <= '1000-01-01 00:00:00.0';

-- 打开，日志，blob要
SET GLOBAL log_output = 'TABLE';SET GLOBAL general_log = 'ON';
SELECT * from mysql.general_log ORDER BY event_time DESC;
-- 关闭，清除
SET GLOBAL log_output = 'TABLE'; SET GLOBAL general_log = 'OFF';
truncate table mysql.general_log;
```
