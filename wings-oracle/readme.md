# 1.神谕(oracle)

支持MySql系的一套Sharding，RPC，可对表结构和数据变更做版本管理的基本套餐。

 * 自动加载SpringBoot配置(wings-conf)
 * 轻量SCHEMA版本管理(fly-wave)
 * DATA版本管理和追踪(journal/$log)
 * 可切换的分表分库功能(ShardingJdbc/ActualDataSource)
 * 高效递增非连续的分布式主键(LightId)
 * jooq 自动生成代码
 
## 1.1.自动加载（wings-conf）

实际项目开发中，只有一个 `application.*`不利于分工和管理的，应该是，

 * shardingsphere-datasource.properties
 * shardingsphere-sharding.properties
 * logger-logback.properties

通过`EnvironmentPostProcessor`扫描`各路径`中`/wings-conf/*.*`，规则如下，

 1. Command line arguments. `--spring.config.location`
 2. Java System properties `spring.config.location`
 3. OS environment variables. `SPRING_CONFIG_LOCATION`
 4. default `classpath:/,classpath:/config/,file:./,file:./config/`

`各路径`指按照上述顺序，把路径拆分后，依次扫描，序号大的优先级高（默认值有关）。

目前只加载 `*.yml`, `*.yaml`, `*.properties`三种扩展名的配置文件。

[参考资料 docs.spring.io](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)

 - "23.5 Application Events and Listeners"
 - "24. Externalized Configuration"
 - "77.3 Change the Location of External Properties of an Application"
 - "76.3 Customize the Environment or ApplicationContext Before It Starts"


## 1.2.SCHEMA版本管理(fly-wave)

实际项目中，schema结构的变更十分频繁，需要控制好local，dev，product的版本和节奏。
`flyway`是个不错的选择，但其功能有点牛刀杀鸡了，所以造个轮子叫`flywave`。

根据 `/schema/version/` 和 `SYS_SCHEMA_VERSION` 进行版本管理。

sql的书写规则详见[数据库约定](/wings-oracle/src/main/resources/schema/readme.md)


## 1.3.DATA版本管理(journal)

`逻辑删除`好像被大量使用，已成为培训必修课，或者行业标配，但其不是最优解。

 * 破坏查询索引。每个查询都要`is_deleted=0`，且90%以上数据为true。
 * 破坏唯一约束。可以重复逻辑删除，所以无法简单有效的对数据unique。

任何数据变动，都需要通过`SYS_COMMIT_JOURNAL`产生`COMMIT_ID`，
记录下驱动力信息（人，事件，业务信息等），最新的数据留在`本表`，
旧数据通过`trigger`插入`历史表`（`本表`+`$LOG`）

`journal`通过`SYS_SCHEMA_MANAGER.LOG_UPDATE`和`SYS_SCHEMA_MANAGER.LOG_DELETE`，
生成`历史表`，和相应的`before update`和`before delete`触发器。

存在分表分库时，需要注意数据源，参见`分表分库`小节。

## 1.4.分表分库功能(ShardingJdbc)

有时候需要保留分表分库的能力，但当前还不需要分。所以，

 * 在只有一个`DataSource`并且没有分表配置时，暴露真实数据源，
 * 有分表分库需要时，暴露`Sharding`数据源。
 * 真实数据源`ActualDataSource`获得。

因为`ShardingJdbc`在执行SCHEMA变更时，存在一定的SQL解析问题(index,trigger)，
所以在做SCHEMA和`journal`功能时，使用真实数据源。

`journal`通过`SYS_SCHEMA_MANAGER.SHARD_AUTO`自动执行分表创建和更新。
`DDL`和`DCL`使用真实数据源，`DML`和`TCL`使用`Sharding`数据源。

[参考SQL解析引擎](https://shardingsphere.apache.org/document/current/cn/features/sharding/principle/parse/)

所以，存在分表分库时，SCHEMA的管理，需要DBA人工完成妥当。

分表(TABLE_#)存在时，`历史表`只有一个`TABLE$LOG`


## 1.5.分布式主键(LightId)

分布式主键有`snowflake`方案可选，但`LightId`支持CRC8做伪随机编码使用。
参考`pro.fessioinal.mirana`项目。


## 1.6.jooq做SqlMapping

轻量的MyBatis(Sql Mapping)能代替笨重的ORM，成为大部分项目的首选，固有其优秀之处。
但开发人员的懒惰或约束力量的不足，使得项目不高效，偶尔很难维护。

 * 经常被 `select *`，带有大量无用信息。
 * 很容易写出复杂的大SQL，使得服务难以拆分。
 * 字符串及弱类型，IDE的眷顾有限。

使用Jooq，强类型，编程高于配置，并且SQL友好，又恰好能力有限。

自动生成jooq代码，使用`WingsCodeGenerator`以编程的方式进行（不用maven）。
自动生成的代码在 `database/autogen/`，手动编写的代码在`database/manual/`下。

手动生成代码遵循一下约定，

 * 任何对数据库的操作，都应该在`database`包内进行
 * `simple` 表示单表，可含简单的条件子查询，一个包名一个表。
 * `couple` 表示多表，一般为join查询或子查询，包名以主表命名。
 * `insert|select|update|delete`分别对应数据库操作。
 * 数据传递以Dto结尾，放到最临近使用的位子。
 * Dto以静态内类形似存在，建议和方法同名，用lombok做@Value。
 * `forUpdate`这种带锁操作，方法名以`Lock`结尾。
 * 类名以`表名`+`Insert|Select|Update|Delete`。


## 1.7.spring规则

 * `/wings-conf` 放置拆分的配置文件，按字母顺序加载和覆盖。
 * `**/spring/boot/` boot有关的配置，如`spring.factories`
 * `**/spring/bean/` 和DI有关的bean。
 * `**/spring/conf/` 和properties有关的配置。
 
使用`idea`开发时，可能需要设置以下

 * 识别bean， `WingsOracleAutoComponentScan`加入`Context`
 * 识别配置，`Settings`/`Annotation Processors`/`Enable annotation processing`