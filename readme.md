# 0.专业大翅 (pro.fessional.wings)

[![Spring Boot](https://img.shields.io/badge/spring--boot-2.4.2-green)](https://spring.io/projects/spring-boot)
[![Java 8](https://img.shields.io/badge/java-8+-red)](https://spring.io/projects/spring-boot)
[![Kotlin 1.4](https://img.shields.io/badge/kotlin-1.4-red)](https://kotlinlang.org/docs/reference/)
[![Jooq](https://img.shields.io/badge/jooq-3.14-yellow)](https://www.jooq.org/download/)
[![Mysql](https://img.shields.io/badge/mysql-5.7+-blue)](https://dev.mysql.com/downloads/mysql/)
[![H2Database](https://img.shields.io/badge/h2db-1.4-blue)](http://h2database.com/html/main.html)

不是为吃货准备的伪装成吃货的项目，其核心价值是使团队快速实现业务目标，快速偿还技术债务，安全的面向程序和业务重构。

![wings ti6](./wings-ti6-champion.png)

Wings是springboot的一个脚手架，没有魔法和定制，主要有以下特点：

 * 提供了多语言多时区真实解决方案（动态语言包，时区，夏令时，闰秒）
 * 提供了数据库版本和数据版本管理（表变更变多了，数据维护多了）
 * 安排了一套油腻的约定和工程实践（枚举类，配置文件，模板等约定）
 * 解决了软件开发中最难的命名问题（允许使用中文命名，解决行业黑话）
 * 功能池很深，对功能有独到的理解（读3遍官方文档，debug部分源码）
 * 不懂代码的看文档，都看不懂别用（这是你的homework，及格线）
 
其目标是使小创业团队，平稳的实现从单应用，到分表分库，到服务化的过渡。
在任何项目阶段和规模下，安全快速的重构业务，变更数据模型及服务，管理版本及兼容性。
运行时的数据变化亦可追溯，复盘，恢复。对抗业务变化快，设计不足的技术债务。

## 0.1.项目技术

项目秉承以下价值观和团队规则

 * 静态优于动态，能编码的就不反射。
 * 强类型优于弱类型，能class就不map，能enum就不const
 * 编译时优于运行时，能在编译时解决的必须解决
 * IDE优于editor，IDE能提供语法分析，上下文解析
 * 命名规约中，可读性优先。不怕长，不怕怪异
 * 奥卡姆剃刀，能简单的实现，就不用搞复杂的
 * 防御性编程风格，默认输入数据不可信，必须验证。

由以下几个子工程构成，

 * [沉默术士/silencer](wings-silencer/readme.md) springboot的工程化装配，I18n等
 * [虚空假面/faceless](wings-faceless/readme.md) 数据层，分表分库，数据及库的版本管理
 * [鱼人守卫/slardar](wings-slardar/readme.md) Servlet体系的WebMvc基础约定和封装
 * [术士大叔/warlock](wings-warlock/readme.md) 综合以上的基础业务模块和功能脚手架
 * [演示例子/example](wings-example/readme.md) 集成以上的样板工程和例子

wings的版本号为`4段分隔`，前3段为spring-boot版本，第4段是changelist。
build是3位数字，第1位为大版本，意味着大调整，不兼容，后2位是小版本，意味着基本兼容或容易适配。

例如，`2.4.2.100-SNAPSHOT`，标识基于boot 2.4.2，是wings的`1##-SNAPSHOT`的系列。
因为wings使用了`revision`和`changelist`的CI占位属性，所以需要Maven 3.5.0 以上。

涉及技术和知识点

 * [Spring Boot](https://docs.spring.io/spring-boot/docs/2.4.2/reference/htmlsingle/)
 * [Apache ShardingSphere](https://shardingsphere.apache.org/index_zh.html)
 * [Jooq - 强类型 sql-mapping](https://www.jooq.org/)

## 0.2.编码风格

使用`IntelliJIdea`作为开发`IDE`，可使用`code style`和`live templates`。
`wings-idea-style.xml`在`Setting/Editor/Code Style`导入。

`wings-idea-live.xml`需要手动放到`$config/templates/`，没有则新建。

```
id_config=~/Library/ApplicationSupport/JetBrains/IntelliJIdea2020.2
# 通过复制，备份
cat $id_config/templates/wings.xml > wings-idea-live.xml
cat $id_config/codestyles/Wings-Idea.xml > wings-idea-style.xml
# 通过复制，还原
cat wings-idea-live.xml  > $id_config/templates/wings.xml
cat wings-idea-style.xml > $id_config/codestyles/Wings-Idea.xml
# 若重新导入工程，清除idea配置
find . -name '*.iml' -o -name '.idea' | tr '\n' '\0' | xargs -0 rm -r
```

关于live-template的使用，分为Insert和Surround，对应插入和编辑，一般
选择文本时，`Surround... ⌥⌘J`，无选择文本时，使用 `Insert... ⌘J`

 * WIN `%HOMEPATH%\.IntelliJIdea2019.2\config`
 * LIN `~/.IntelliJIdea2019.2/config`
 * MAC `~/Library/Preferences/IntelliJIdea2019.2`
 * MAC `~/Library/ApplicationSupport/JetBrains/IntelliJIdea2020.1`

参考资料
 * [sharing-live-templates](https://www.jetbrains.com/help/idea/sharing-live-templates.html)
 * [2020.1 and above versions](https://www.jetbrains.com/help/idea/tuning-the-ide.html#default-dirs)
 * [2019.3.x and below versions](https://www.jetbrains.com/help/idea/2019.3/tuning-the-ide.html#default-dirs)


安装以下插件
 * .ignore - 和版本管理中ignore有关的。
 * Any2dto -  支持jooq, sql查询直接生成dto，减少复制和赋值
 * CheckStyle - 代码质量
 * Error Prone Compiler - google出品（java8不好整）
 * GenerateAllSetter - alt-enter 生成全部 po.setXxx("")
 * Git Flow Integration - 集成了git-flow
 * GitToolBox - 自动 fetch
 * Grep Console - 控制台的日志分颜色显示和过滤
 * kotlin - 默认安装了
 * lombok - IntelliJ Lombok plugin
 * MapStruct Support - 静态强类型DTO转换，减少复制和赋值
 * Maven Helper - 帮助管理maven
 * Quick File Preview - 单击快速浏览文件
 * Rainbow Brackets - 彩虹括号
 * Request mapper - 快速查找 mapping
 * Statistic - 统计一下自己的代码
 * String Manipulation -  对字符串的各种操作和转换。
 * HTTP Client - 官方对`*.http`文件格式的支持

### 0.2.1.Java风格，遵循标准的java规范，但**可读性优先**。

 * `static final` 不必全大写。如`logger`比`LOG`可读性好。
 * `BIG_SNAKE`可使用`PascalNaming`，因为大写单词不如小写易读。
 * 全大写名词（缩写或专有）只首字母大写。`Json`,`Html`,`Id`。
 * 英文无法表达的业务词汇及行业黑话，不要用拼音，用中文。`落地配`。
 * 要求4-8字母的单词都记住。
 
### 0.2.2.Sql风格，`snake_case`，即全小写，下划线分割，小写词比大写容易识别。

 * 数据库，表名，字段名，全小写。
 * SQL关键词，内置词等建议`大写`，以区别。
 * `index`以`ix_`,`uq_`,`ft_`,`pk_`区分索引类型。
 * `trigger`以`_bu`,`_bd`表示触发的时机。
 
### 0.2.3.时间很神奇

系统内有2种时间`系统时间`和`本地时间`，数据库和 java 映射上，

 * `日期时间`，以`DATETIME`或`DATETIME(3)`和`LocalDateTime`存储。
 * `日期`，以`DATE`和`LocalDate`存储。
 * `时间`，以`TIME`或`TIME(3)`和`LocalTime`存储。
 * `时区`，以`VARCHAR(40)`或`INT(11)`存储。
 * 特别场景，以`BIGINT(20)`或`VARCHAR(20)`存储。

以跨境海淘场景为例，服务器群采用`UTC`时区（系统时间），中国用户`Asia/Shanghai`（用户时间）,
纽约NY商家`America/New_York`（数据时间），洛杉矶LA商家`America/Los_Angeles`（数据时间）。
为什么说`UTC-5`初级呢？因为同一经线上国家很多，又要考虑`夏令时`，所以需要city标志`zoneid`。

本地日时，必须有`时区`配合，又分为`用户时间`和`数据时间`，命名后缀如下，

 * `时区` - 以`_tz`或`_zid`为后缀，内容为`ZoneId`的字符串名字。
 * `日时` -系统/用户/数据，分别以`_dt`/`_udt`/`_ldt`结尾。
 * `日期` -系统/用户/数据，分别以`_dd`/`_udd`/`_ldd`结尾。
 * `时间` - 系统/用户/数据，分别以`_tm`/`_utm`/`_ltm`结尾。

举例，北京时间`2020-08-09 01:00:00`，中国用户C1，分表在NY和LA商家下单。

 * Sys_dt(UTC) = `2020-08-08 17:00:00`
 * C1_udt(Asia/Shanghai, UTC+8) = `2020-08-09 01:00:00`
 * NY_ldt(America/New_York, UTC-4) = `2020-08-08 13:00:00`
 * LA_ldt(America/Los_Angeles, UTC-7) = `2020-08-08 10:00:00`

哎，不对啊，记得纽约是`西五区`啊，应该`UTC-5`啊，现在是`夏令时`

系统时区，推荐为核心用户所在时区，要考虑UTC是否为最优解。

于是，以下场景时，我们会用到不同的时间，

 * 当跟踪系统日志时，我们使用`Sys_dt`，可以保证统一的时间线。
 * 当统计北美商家`上午`的营运报表时，我们使用`*_ldt`
 * 当追求用户体验，用户不关心时区时，用户看到的所有时间都是`C1_udt`
 * 有些行业惯例（航空，物流）使用本地时间，我们使用`*_ldt`

按数据的读写比例，处理时间存储时，要考虑。

 * 统计类业务，通常写入时转化，存入用户本地时间（和时区），读取时不转换。
 * 协作类业务，通常写入时，使用系统时间，读取时转换。

如果需要转换时间，需要在用户界面统一（如controller）处理。

### 0.2.4.属性文件风格

 * 尽量使用`properties`和列编辑，`yml`的缩进在传递与部分分享时会困扰。
 * 一组关联属性，放在一个`properties`，分成文件便于管理。
 * `spring-wings-enabled.properties`用于ConditionalOnProperty配置
    - 统一使用`spring.wings.**.enabled.*=true|false`格式。
    - 多模块时，模块本身为`spring.wings.**.enabled.module=true`
 * `spring-*`放置spring官方配置key。
 * `wings-*`放置wings配置key，
    - 带有工程或模块代号，如`wings.slardar.*`
    - 提供默认配置，使用`-79`序号
 * 推荐`kebab-caseae`命名，即`key`全小写，使用`-`分割。

### 0.2.5.Spring注入风格，在`silencer`和`faceless`有详细说明。

 * 优先使用`constructor`注入，用`lombok`的`@RequiredArgsConstructor`。
 * 次之使用`setter`注入，用`lombok`的`@Setter(onMethod = @__({@Autowired}))`
   或`kotlin`的`@Autowired lateinit var`。
 * 不要使用`Field`注入，坏处自己搜。

使用@Resource，@Inject和@Autowired，有细微差别，
 * Resource由CommonAnnotationBeanPostProcessor处理，查找顺序为①BeanName②BeanType③Qualifier
 * Autowired和Inject由AutowiredAnnotationBeanPostProcessor处理，查找顺序为①BeanType②Qualifier③BeanName 
 * 注入控制时，type优先用Autowired和Inject，name优先用Resource(细粒度，难控制)
 * 在spring体系下推荐@Autowired，考虑兼容性用Inject

### 0.2.6.Spring MVC中的 RequestMapping 约定

wings采用的Url命名主要是场景化的，命名为[RestHalf](./rest-half.md)，单独叙述。

 * 在方法上写全路径`@RequestMapping("/a/b/c.html")`
 * 在controller上写版本号`@RequestMapping("/v1")`
 * 不要相写相对路径，这样才可以通过URL直接搜索匹配。
 * 不要使用prefix拼接路径(view，url)，避免无意义的碎片。
 * 不管REST还是其他，url一定有扩展名，用来标识MIME和过滤

### 0.2.7.Spring Service 的接口和 DTO 约定

interface上使用annotation时，遵循以下规则，

* @Component类注解，不要放在接口上，放在具体实现上
* 功能约定类，放在接口上，如 @Transactional

Service定义为接口，Service中的DTO，定义为内类，作为锲约。
DTO间的转换和复制，使用工具类生成Helper静态对拷属性。
禁止使用反射，不仅是因为一点性能，主要是动态性，脱离了编译时检查。

直接单向输出的model对象，可以使用map，否则一定强类型的class。

```java
public interface TradeService {
    @Data
    class TradeInfo {
        private long orderId;
        private BigDecimal amountOrder;
        // others
    }
    /* docs */
    void transfer(@NotNull MoneyInfo ai, @NotNull TradeInfo ti, @NotNull Journal journal);
}
```

### 0.2.8.枚举类和code/const值

在service层，要求强类型，所以code/const都以enum传递。
通过自动java模板生成enum，通过*EnumUtil，转换。

在db层，以基本类型(int,varchar)读取和写入。

在用户层，以多国语形式显示枚举内容

### 0.2.9.maven管理的约定

 * 多模块有主工程（parent|packaging=pom）和子工程（module|packaging=jar）
 * 主工程在dependencyManagement定义lib，不管理具体dependency
 * 子工程自己管理dependency，不可以重新定义版本号

### 0.2.10.Api测试及文档约定

默认开启了swagger，访问路径为 /swagger-ui/index.html

使用swagger时，不可使用弱口令，在正式服上必须关闭。在3.0.0版本，通过设置以下属性即可。  
`springfox.documentation.enabled=false`，或通过profile来设置（不推荐）

推荐在每个工程test下建立idea支持的 `*.http` 接口描述和测试脚本，官方文档如下

 * https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html
 * https://www.jetbrains.com/help/idea/exploring-http-syntax.html
 * https://www.jetbrains.com/help/idea/http-response-handling-api-reference.html
 * https://www.jetbrains.com/help/idea/http-client-reference.html
 * https://www.jetbrains.com/help/idea/http-response-reference.html

使用建议如下

 * 使用`*.http`时，通常先从chrome中抓取 cURL 命令，复制过来即可。
 * 变量`{{variable_name}}`，来自`http-client*.env.json`，`client.global.`或系统自带
 * 处理Response. prepend it with `>` and enclose it in `{%` `%}`
 * 很长的请求折多个短行. Indent all query string lines but the first one.
 * HTTP Response Handler 的2个对象 client 和 response
 * https://www.jetbrains.com/help/idea/http-response-handling-examples.html
 
### 0.2.11.工程目录结构

文件命名，对外的内容使用Wings前缀，否则可实现项目前缀或特征代号。这样可以对外容易识别，对内避免冲突混淆。

#### 01. resources
```
src/main/resources
├── META-INF - spring 自动配置入口等
│   └── spring.factories - EnableAutoConfiguration入口
├── extra-conf/ - 非自动加载的其他配置
├── wings-conf/ - wings自动加载配置 xml|yml|yaml|properties
├── wings-flywave/ - flywave数据库版本管理，
│   ├── branch/* - 分支脚本，如维护，功能
│   └── master/* - 主线脚本，上线中
└── wings-i18n/ - wings自动加载 bundle
│   ├── base-validator_en.properties - 英文版
│   └── base-validator_ja.properties - 日文版
└── application.properties - spring 默认配置，用于覆盖wings
```

#### 02.database访问层

```
src/**/database/ - 数据访问层
├── autogen/ - 自动生成的代码，jooq，mybatis等
├── helper/ - 业务帮助类
│   └── RowMapperHelper.java
├── manual/ - 手动写的SQL
│   ├── couple/ - 表示多表，一般为join查询或子查询，包名以主表命名
│   │   ├── modify/ - 增，改，删
│   │   └── select/ - 查
│   └── single/ - 表示单表，可含简单的条件子查询，一个包名一个表。
│       ├── modify/ - 增，改，删
│       │   ├── commitjournal
│       │   │   ├── CommitJournalModify.java - 接口
│       │   │   └── impl/ 实现
│       │   │       └── CommitJournalModifyJdbc.java - Jdbc实现
│       └── select/ - 查
```

#### 03.spring有个目录

```
src/**/spring - spring有个配置
├── bean/ - 自动扫描，产生可被Autowired的Bean
│   └── WingsLightIdConfiguration.java - 内部用项目前缀，对外使用Wings前缀
├── boot/ - spring boot 配置用，不产生Bean
│   └── WingsAutoConfiguration.java - 兼容IDE和starter的配置入口
├── conf/ - 配置辅助类Configurer
├── help/ - 工具辅助类
└── prop/ - 属性类，自动生成spring-configuration-metadata.json
    └── FacelessEnabledProp.java - 开关类
```

需要注意的是，在`@Configuration`类中配置`@Bean`时，对bean的依赖遵循以下原则。
 * 优先使用Constructor注入+final
 * 使用Bean方法的参数。
 * 可使用Field注入。
 * 避免使用Setter注入，因为不能提前暴露依赖错误。

### 0.2.12.常见的命名约定

* 接口默认实现为`Default*`
* 适配器类为`*Adapter`

### 0.2.13.有关事件Event约定

* 内部Event，内部Publish，内部Listen
* 能内部Listen的，就不用外部的Subscribe。
* 能同步的就不用异步

## 0.3.技术选型

技术选型，遵循Unix哲学，主要回答，`为什么`和`为什么不？`

### 0.3.1.Spring Boot

事实标准，从业人员基数大，容易拉扯队伍。

### 0.3.2.ShardingSphere

分表分库，足以解决90%的`数据大`的问题。大部分公司面临的情况是`数据大`而不是`大数据`。
`大`主要指，单表超过`500万`，查询速度超过`10ms`的`OLTP`业务场景。

此时合适的解决方案，应该是读写分离，水平分表，优化数据结构，拆分业务场景。
不建议微服务，集群，甚至`大数据`。因为服务治理的难度容易拖垮团队。

选择`shardingjdbc`，个人认为其在实践场景，文档，代码及活跃度上高于竞品。

 * [mycat](http://www.mycat.io/)

### 0.3.3.jooq

在faceless中有详细介绍，主要原因是`限制的艺术`

 * jooq强类型，可以受到IDE加持
 * 不能写成过于复杂的SQL，有利于分库，分服务
 * 比mybatis有更多的语言特性

### 0.3.4.ServiceComb

阅读过部分源码，个人比较喜欢 ServiceComb 的哲学，而且力道刚刚好。

`dubbo`更多的是服务治理，中断又重启，虽社区呼声大，但时过境迁了。

`sofa`技术栈，有着金服实践，功能强大，社区活跃，仍在不断开源干货中。
如果团队够大，项目够复杂，管理和协作成本很高时，推荐使用。

 * [servicecomb](http://servicecomb.apache.org/)
 * [dubbo](http://dubbo.apache.org)
 * [sofa stack](https://www.sofastack.tech/)
 
### 0.3.5.kotlin

`kotlin`比`scala`更能胜任`更好的java`，主要考量的是团队成本，工程实践性价比。

### 0.3.6.webmvc

尽管`webflux`在模型和性能好于serverlet体系，当前更多的是阻塞IO，多线程场景。
所以，当前只考虑 webmvc，用thymeleaf模板引擎。

### 0.3.7.lombok

简化代码，开发时，需要自己在pom中引入。使用了Experimental功能，可能会突然编译不过去。
错误大概类似于 `cannot find symbol class __`，官方文档表示，

javac8+, you add an `_` after `onMethod`, `onParam`, or `onConstructor`.

``` java
//  @Getter(onMethod=@__({@Id, @Column(name="unique-id")})) //JDK7
//  @Setter(onParam=@__(@Max(10000))) //JDK7
@Getter(onMethod_={@Id, @Column(name="unique-id")}) //JDK8
@Setter(onParam_=@Max(10000)) //JDK8
```
在IDEA中，可通过以下正则进行全工程替换。

* 查找 `onMethod\s*=\s*@__\(/(.+)\)`
* 替换 `onMethod_ = $1`

### 0.3.8.git-flow

使用`git flow`管理工程

[git-flow-cheatsheet](http://danielkummer.github.io/git-flow-cheatsheet/)

### 0.3.9.guava&commons

以下3个是java程序员进阶必备的工具包，其中commons-lang3，spring-boot定义了版本。

 * guava - https://github.com/google/guava
 * commons-lang3 - https://commons.apache.org/proper/commons-lang/
 * commons-io - http://commons.apache.org/proper/commons-io/

## 0.5.常见问题

### 01.getHostName() took 5004 milliseconds
InetAddress.getLocalHost().getHostName() took 5004 milliseconds to respond. 
Please verify your network configuration (macOS machines may need to add entries to /etc/hosts)

``` bash
hostname
# 输出 trydofors-Hackintosh.local

cat /etc/hosts
# 在localhost后面，填上 trydofors-Hackintosh.local
127.0.0.1	    localhost trydofors-Hackintosh.local
```

### 02.工程中哪些参数是必须打开的

``` bash
# 找到所以开关文件
find . -name 'spring-wings-enabled.properties' \
| egrep -v -E 'target/|example/' 

./wings-slardar/src/main/resources/wings-conf/spring-wings-enabled.properties
./wings-faceless/src/main/resources/wings-conf/spring-wings-enabled.properties
./wings-silencer/src/main/resources/wings-conf/spring-wings-enabled.properties

# 找到所false的开关
find . -name 'spring-wings-enabled.properties' \
| egrep -v -E 'target/|example/' \
| xargs grep 'false'

# 以下2个需要在flywave和enum时开启
spring.wings.faceless.flywave.enabled.module=false
spring.wings.faceless.enabled.enumi18n=false
``` 

### 03.如何创建一个工程

``` bash
git clone https://gitee.com/trydofor/pro.fessional.wings.git
cd pro.fessional.wings
wings-example/wings-init-project.sh

# 如果不能执行bash，那么自行编译和执行
cd wings-example/src/test/java/
pro/fessional/wings/example/exec/Wings0InitProject.java
```

### 04.lib工程和boot工程的区别

Springboot的打包机制使boot.jar 不是普通的lib.jar
``` xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <executable>true</executable>
    </configuration>
</plugin>
```

lib工程的配置，跳过repackage，参考example之外的工程
``` xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>repackage</id>
            <goals>
                <goal>repackage</goal>
            </goals>
            <configuration>
                <skip>true</skip>
            </configuration>
        </execution>
    </executions>
</plugin>
```
所以，wings推荐的工程结构是，在parent工程pom.xml的`project/build/plugins`中，
对以下`plugin`的`configuration`设置，

 * spring-boot-maven-plugin executable=true
 * maven-deploy-plugin skip=true
 * maven-install-plugin skip=true

这样，为所以子模块，以boot工程提供默认的build（boot打包，不deploy，不install）。
在lib子模块中跳过boot打包，spring-boot-maven-plugin/repackage skip=true

### 05.jackson和fastjson

wings中和springboot一样，默认采用了jackson进行json和xml绑定。
不过wings的中对json的格式有特殊约定，比如日期格式，数字以字符串传递。
再与外部api交换数据时可能格式不匹配，这时需要用有background

 * 使用2套jackson配置
 * 使用jackson注解 @JsonRawValue
 * 使用fastjson(不推荐，需1.2.69+，SafeMode, 安全漏洞)

### 06.为什么是dota的英雄

有这样一个团队，她是做对日金融的，穿拖鞋裤衩上班，课间可以团dota，cs，跑跑卡丁车。
日本人组团爱上了瓜子，黄飞红，米线，火锅。团队只有一个要求，活干的漂亮，快，零缺陷。

我本人与dota有缘无分，现在也就连直播都不爱看了，只是心中有个地方，叫dota

 * TI6，她在西雅图，我在特拉华
 * TI9，她在奔驰馆，我在大虹桥

### 07.类型间Mapping比较

根据以下文章，推荐使用静态性的`MapStruct`和简单的`SimpleFlatMapper`。

 * [Quick Guide to MapStruct](https://www.baeldung.com/mapstruct)
 * [Mapping Collections with MapStruct](https://www.baeldung.com/java-mapstruct-mapping-collections)
 * [MapStruct 性能比较](https://www.baeldung.com/java-performance-mapping-frameworks)
 * [MapStruct ide&mvn支持](https://mapstruct.org/documentation/installation/)
 * [Jdbc-Performance](https://github.com/arnaudroger/SimpleFlatMapper/wiki/Jdbc-Performance-Local-Mysql)

在编码过程中，我们经常要处理各种O的转换，赋值，比如DTO，PO，VO，POJO。
同时我们又希望强类型，以便可以通过IDE提示提供效率，并把错误暴露在编译时。
这样就一定要避免弱类型(map,json)和反射（bean copy）,势必需要代码生成工具。

对于比较复杂的mapping，使用expression，qualifiedByName，spring注入。
自动生成的代码位于`target/generated-sources/annotations/`

在wings中，推荐使用列编辑和正则（分享视频有讲），对于使用MapStruct的时候，
可以使用wings提供的`wgmp`(live template)做`A2B`的into生成器。

 * 在业务层代码，推荐MapStruct或列编辑和正则（分享视频有讲）手工制品。
 * 在jdbc中推荐`SimpleFlatMapper`或手工RowMapper，避免使用`BeanPropertyRowMapper`。
 * 在jooq中推荐jooq自动生成的record，或SimpleFlatMapper。

纯wings中的converter以`-or`结尾(convertor)，以和其他框架的converter区分。  
包名以converter为准，类名以目的区分，通常纯wings的使用`-or`，其他用`-er`。

### 08.文件系统或对象存储

需要权限才能访问的文件资源，不可以放到CDN，需要自建对象存储或使用物理文件系统
当使用本地FS是，需要注意子文件或子目录的数量限制，一般控制在30k以下，理由。

* The ext2/ext3 filesystems have a hard limit of 31998 links.
* 数量过多时，ls读取巨慢，索引也会慢。

如果自建对象存储，推荐以下方案

 * https://docs.min.io/cn/ 推荐使用
 * https://github.com/happyfish100/fastdfs

### 09.客户端或服务器信息

收集用户画像，需要获得UA信息，可使用以下工具包

 * https://www.bitwalker.eu/software/user-agent-utils 浏览器（停止维护）
 * https://github.com/browscap/browscap/wiki/Using-Browscap 浏览器工具家族
 * https://github.com/blueconic/browscap-java 浏览器（推荐）

获取服务器运行信息，使用以下工具包

 * https://github.com/oshi/oshi 系统信息

### 10.缺少mirana和meepo依赖lib

因是非吃货的大翅项目，一些`-SNAPSHOT`依赖，需要自行编译并本地安装。
偶尔可以在`sonatype`上找到，需要自行添加`repository`，如`~/.m2/settings.xml`
```
<repository>
    <id>ossrh-snapshots</id>
    <url>>https://oss.sonatype.org/content/repositories/snapshots</url>
    <snapshots><enabled>true</enabled></snapshots>
    <releases><enabled>false</enabled></releases>
</repository>
```

### 11.调整springboot版本和依赖

wings工程，仅对spring-boot的标准生命周期进行了配置文件加载的hook，非强依赖于任何固定版本。
对于不想跟随wings一同升级spring及其依赖的，只把wings做dependency，而不parent和import即可。

wings随时跟进升级spring boot的最新版本，目的是为了测试sharding-jdbc和jooq的兼容性。
而在二进制兼容方面，wings编译的版本是java=1.8，kotlin=1.3。

对于maven继承ri依赖有parent和import两种，其重要区别在于property覆盖。

 * parent - you can also override individual dependencies by overriding a property in your own project
 * import - does not let you override individual dependencies by using properties, as explained above. 
   To achieve the same result, you need to add entries in the dependencyManagement 
   section of your project before the spring-boot-dependencies entry.
 * https://docs.spring.io/spring-boot/docs/2.4.2/maven-plugin/reference/htmlsingle/#using-parent-pom
 * https://docs.spring.io/spring-boot/docs/2.4.2/maven-plugin/reference/htmlsingle/#using-import

对于低于wings的spring-boot版本，一般来讲指定一下jooq版本就可以完全正常。

### 12.关于http密码安全

* 密码长度不可设置上限，一般要求8位以上
* 支持中文密码，标点，全角半角
* 不发送明文密码，密码初级散列策略为md5(pass+':'+pass).toUpperCase(Hex大写)
* js侧md5需要支持UTF8，如 https://github.com/emn178/js-md5

### 13.关于内网穿透，第三方集成调试

在Oauth，支付等第三方集成调试时，需要有公网ip或域名，然后把公网请求转发到开发机调试。

* 临时用 ssh - `ssh -R 9988:127.0.0.1:8080 user@remote`
* 持久用 frp - https://gofrp.org/docs/

### 14.占位符

* 编码中，autowired StringValueResolver
* properties配置中`${VAR}`
* @Value和@RequestMapping中`${VAR}`

### 15.IDEA提示 component, or scanned

导入wings工程，Idea会无法处理spring.factories中的WingsAutoConfiguration，会报类似以下信息

Not registered via @EnableConfigurationProperties, marked as Spring component, 
or scanned via @ConfigurationPropertiesScan

此时在，Project Structure中的Facets中的spring，对每个主工程，
导入`Code based configuration`，选择WingsAutoConfiguration，即可。

### 16.Jooq隐秘的NullPointerException

在jooq映射enum类型是，如果converter错误，可能会出现以下NPE，不能通过stack定位问题，需要分析SQL

```
java.lang.NullPointerException
at org.jooq.impl.DefaultExecuteContext.exception(DefaultExecuteContext.java:737)
at org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator.handle(JooqExceptionTranslator.java:83)
at org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator.exception(JooqExceptionTranslator.java:55)
at org.jooq.impl.ExecuteListeners.exception(ExecuteListeners.java:274)
at org.jooq.impl.AbstractQuery.execute(AbstractQuery.java:390)
```
