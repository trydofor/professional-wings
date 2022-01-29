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

## 2.3.2.执行SQL中的事项

[DDL - Data Definition Statements](https://dev.mysql.com/doc/refman/5.7/en/sql-syntax-data-definition.html)

 * `ALTER TABLE`
 * `CREATE INDEX`
 * `CREATE TABLE`
 * `CREATE TRIGGER`
 * `DROP INDEX`
 * `DROP TABLE` 只可以一次一个table
 * `DROP TRIGGER` 需要手动指定本表，系统根据本表和分表命名规则执行
 * `TRUNCATE TABLE`
 
 其中，系统自带的`journal`的trigger外，手写删除会出现数据不一致。
  
[DML - Data Manipulation Statements](https://dev.mysql.com/doc/refman/5.7/en/sql-syntax-data-manipulation.html)

 * `DELETE`
 * `INSERT`
 * `REPLACE`
 * `UPDATE`

以上类型之外的SQL，请使用注解，强制指定`数据源`和`本表名`，跳过SQL自动解析。
理论上，不应该使用flywave执行DDL,DCL和DML之外的SQL，不属于版本管理范围。

## 2.3.9.常见问题

### 01. No implementation class load from SPI with type `null`

命名中避免使用中文，边界测试时，发现shardingsphere可以直接中文表名，但对其他命名会截断错误。
如`sharding-algorithms.[中文也分表-inline]`，会截断错误，使其`.type=null`，从而报错，
No implementation class load from SPI `org.apache.shardingsphere.sharding.spi.ShardingAlgorithm` with type `null`


### 02. INSERT INTO ... ON DUPLICATE KEY UPDATE can not support update for sharding column.

then append sharding column and value to WHERE Clause

https://github.com/apache/shardingsphere/issues/14025

此功能大概在 5.1.0发布
