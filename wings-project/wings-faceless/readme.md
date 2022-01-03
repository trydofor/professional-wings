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

## 2.3.数据膨胀后的分表分库(shard)

有时候需要保留分表分库的能力，但当前还不需要分。所以，

 * 在只有一个`DataSource`并且没有分表配置时，暴露普通数据源，
 * 有分表分库需要时，暴露`Sharding`数据源。
 * 所有普通数据源在`FacelessDataSources`中获得。

用ShardingSphere实现，具体内容，移步到子工程[时间膨胀(shard)](../wings-faceless-shard/readme.md)

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
为单Jvm，多Jvm提高一个基于数据库的Event服务，主要用来。

 * the event should extend ApplicationEvent
 * the publisher should inject an ApplicationEventPublisher object
 * the listener should implement the ApplicationListener interface
 * @EventListener 和 @TransactionalEventListener 
 
对应线程池直接传递上下文，可使用

https://github.com/alibaba/transmittable-thread-local


## 2.9.数据库知识

mysql体系指mysql分支如(Percona,MariaDB)或兼容mysql协议的数据库，wings使用mysql-5.7.x（8.0未测试）。
原则上DB不应该封装（自定义function或procedure）业务逻辑，但可以使用db提供的功能，简化工作实现业务目标。
[mysql 5.7 官方文档](https://dev.mysql.com/doc/refman/5.7/en/)

### 2.9.1.MySql非通常用法

#### 01.FIND_IN_SET

FIND_IN_SET(str,strlist)，比like和match更精准的查找，strlist以逗号分隔，str中不能有逗号。
返回strlist中1-base的坐标。0表示没找到活strlist为空。NULL如果str或strlist为NULL。

``` sql
SELECT FIND_IN_SET('b','a,b,c,d')
-- 2，多数场景还是作为where条件，如下
WHERE FIND_IN_SET(role, role_set);
```


#### 02.GROUP_CONCAT

```
GROUP_CONCAT([DISTINCT] expr [,expr ...]
    [ORDER BY {unsigned_integer | col_name | expr}
        [ASC | DESC] [,col_name ...]]
    [SEPARATOR str_val]
)

SELECT 
    GROUP_CONCAT(CONCAT_WS(', ', contactLastName, contactFirstName)
        SEPARATOR ';')
FROM customers;
```

#### 03.全文检索，MATCH AGAINST

需要建立full text index，注意汉字分词或用插件或在java中分好

#### 04.替换和忽略 REPLACE IGNORE

`replace into`和`insert ignore`

#### 06.慎用Json数据类型

As of MySQL 5.7.8, MySQL supports a native JSON data type defined by RFC 7159  
新的操作符`->`和`->>`，需要注意词法分析框架的兼容性，所以在java中处理更为妥当。

#### 07.性能分析explain和BENCHMARK

``` sql
-- 单个express重复执行，注意，select只能返回唯一值
SELECT BENCHMARK(1000000,(
    SELECT count(author_name) FROM git_log_jetplus
));
-- 查看索引使用情况
explain 
    SELECT author_name FROM git_log_jetplus;
```

#### 08.分页limit和FOUND_ROWS()记录总数

```mysql
-- 先增加SQL_CALC_FOUND_ROWS选项，
SELECT SQL_CALC_FOUND_ROWS * FROM tbl_name WHERE id > 100 LIMIT 10;
-- 然后获取
SELECT FOUND_ROWS();
```
#### 09.自增主键AUTO_INCREMENT和LAST_INSERT_ID()

项目中避免使用自增主键，特事特办的时候，可以如上获得。  
注意value多值插入时，只返回第一个。

#### 10.字符串/字段链接 CONCAT和CONCAT_WS

```sql
-- 注意对null的处理
SELECT CONCAT('My', NULL, 'QL');
-- NULL, returns NULL if any argument is NULL.
SELECT CONCAT_WS(',','First name',NULL,'Last Name');
-- 'First name,Last Name', skip any NULL values
```

#### 11.时区转换CONVERT_TZ

转换类操作，应该在write时，此方法应在临时性读取时使用。  
注意闰秒(leap second) `:59:60` or `:59:61`都以`:59:59`返回
```
SELECT  CONVERT_TZ('2007-03-11 2:00:00','America/New_york','Asia/Shanghai') AS time_cn
```

#### 12.格式化输出FORMAT,DATE_FORMAT

```sql
-- '#,###,###.##'
SELECT FORMAT(12332.123456, 4);
-- '12,332.1235'
SELECT FORMAT(12332.1,4);
-- '12,332.1000'
```
#### 13.全局悲观锁GET_LOCK

此功能在做跨jvm全局悲观锁时可用。
``` sql
-- 一条语句，无阻塞获得锁
SELECT IF(IS_FREE_LOCK('10')=1, GET_LOCK('10',10), -1);
-- 检测锁，1 if the lock is free
SELECT IS_FREE_LOCK('lock1');
-- 阻塞10秒，1 if successfully, 0 timed out
SELECT GET_LOCK('lock1',10);
-- 释放锁，或session中断
SELECT RELEASE_LOCK('lock1');
-- RELEASE_ALL_LOCKS()
```

#### 14.正则匹配REGEXP和RLIKE

注意，mysql是基于byte-wise的，不是char，所以多字节字符有可能不正常。
``` sql
-- 1为匹配，0为不匹配
SELECT 'Michael!' NOT REGEXP '.*';
```

#### 15.VarChar和Text类型

* VarChar 有长度限制，可以设置默认值，这符合wings NOT NULL的规范。
* TEXT可认为无限制，不可设置默认值，不符合wings规范。
* MySQL has hard limit of 4096 columns
* maximum row size limit of 65535 bytes

### 2.9.2.本地(文件/内存)数据库H2

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
lower_case_table_names      = 1
innodb_file_per_table       = 1
innodb_ft_min_token_size    = 1
ft_min_word_len             = 1
character-set-server        = UTF8MB4
max_allowed_packet          = 1024M
default-time-zone           = '+8:00'
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

可以通过以下sql创建高权限用户，建议使用wings-mysql-user.sh独立权限的用户
```sql
CREATE USER 'trydofor'@'%' IDENTIFIED BY 'moilioncircle';
GRANT ALL ON *.*  TO 'trydofor'@'%'; -- 通常写法
-- GRANT ALL  ON `%`.*  TO 'trydofor'@'%'; -- 上述有语法错误时
SHOW GRANTS FOR 'trydofor'@'%';
-- DROP USER 'trydofor'@'%';
FLUSH PRIVILEGES;
```

### 03.如何自定义journalService

使用高优先级注入`journalService`，参考 example工程的 `SecurityJournalService`

### 04.日时零值和时区问题

执行环境和数据库要在同一时区，否则jooq和jdbc在以下过程会自动转换时区，引发问题。

如果服务器和执行环境时区不一致，可以通过以下方式协调。

* 通过wings的参数设置时区 `wings.silencer.i18n.zoneid=Asia/Shanghai`
* java的启动参数， `-Duser.timezone=Asia/Shanghai`
* mysql的jdbc的url参数， `serverTimezone=Asia/Shanghai`
* java的代码参数， `TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));`

在数据库和java配置上，需要统一时区和dev状态下的用户名和密码，一般团队一致。

```properties
wings.silencer.i18n.zoneid=Asia/Shanghai
# 常用jdbc参数
spring.datasource.url=jdbc:mysql://localhost:3306/wings_example\
  ?autoReconnect=true&useSSL=false\
  &useUnicode=true&characterEncoding=UTF-8\
  &serverTimezone=Asia/Shanghai
```

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

The session time zone setting affects display and storage of time values that are zone-sensitive. 
This includes the values displayed by functions such as NOW() or CURTIME(), 
and values stored in and retrieved from TIMESTAMP columns. Values for TIMESTAMP columns are converted 
from the session time zone to UTC for storage, and from UTC to the session time zone for retrieval.


在mysql中，尽量使用NOW(fsp)，因为其短小明确有缓存，如无必须不可使用SYSDATE(fsp)，参考

* https://dev.mysql.com/doc/refman/5.7/en/time-zone-support.html#time-zone-variables
* https://dev.mysql.com/doc/refman/5.7/en/date-and-time-functions.html#function_now
