# 0.专业大翅 (professional wings)

不是为吃货准备的伪装成吃货的项目。其目标是单应用，而不是微服务。
它基于springboot，没有魔法和定制，主要有以下特点：

 * 解决了软件开发中最难的命名问题（允许使用中文命名）。
 * 提供了一套油腻的约定和工程实践（bug写多了，就有直觉了）。
 * 功能池很深，对功能有独到的理解（读3遍官方文档，debug部分源码）。
 * 不懂代码的看文档，都看不懂别用（这是你的homework，及格线）。
 * java-8, kotlin-1.3.x, springboot-2.1.x


## 0.1.项目构成

 * [演示例子/example](wings-example/readme.md) 集成了以上的例子
 * [沉默术士/silencer](wings-silencer/readme.md) 工程化的自动装配
 * [虚空假面/faceless](wings-faceless/readme.md) 分表分库，数据版本管理
 * [鱼人守卫/slardar](wings-slardar/readme.md) 基于Servlet体系的控制
 
 
## 0.2.涉及技术

 * [Spring Boot](https://docs.spring.io/spring-boot/docs/2.2.7.RELEASE/reference/htmlsingle/)
 * [Apache ShardingSphere](https://shardingsphere.apache.org/index_zh.html)
 * [Apache ServiceComb](http://servicecomb.apache.org/cn/)

## 0.3.命名风格

使用`idea`作为开发`IDE`，可使用`code style`和`live templates`。
`wings-idea-style.xml`在`Setting/Editor/Code Style`导入。

`wings-idea-live.xml`需要手动放到`$config/templates/`。
参考 https://www.jetbrains.com/help/idea/sharing-live-templates.html

 * WIN `%HOMEPATH%\.IntelliJIdea2019.2\config`
 * LIN `~/.IntelliJIdea2019.2/config`
 * MAC `~/Library/Preferences/IntelliJIdea2019.2`

### 0.3.1.Java风格，遵循标准的java规范，但**可读性优先**。

 * `static final` 不一定全大写。如`logger`比`LOG`可读性好。
 * 全大写名词（缩写或专有）只首字母大写。`Json`,`Html`,`Id`。
 * 英文无法表达的业务词汇及行业黑话，不要用拼音，用中文。`落地配`。
 * 要求4-8字母的单词都记住。
 
### 0.3.2.Sql风格，`snake_case`，即全小写，下划线分割，小写词比大写容易识别。

 * 数据库，表名，字段名，全小写。
 * SQL关键词，内置词等建议`大写`，以区别。
 * `index`以`ix_`,`uq_`,`ft_`,`pk_`区分索引类型。
 * `trigger`以`_bu`,`_bd`表示触发的时机。
 
 **时间很神奇**
 
系统内有2种时间`系统时间`和`本地时间`，数据库和 java 映射上，

 * `日期时间`，以`DATETIME`和`LocalDateTime`存储。
 * `日期`，以`DATE`和`LocalDate`存储。
 * `时间`，以`TIME`和`LocalTime`存储。

本地日时，必须有`时区`配合，命名后缀如下，

 * `时区`使用`VARCHAR(100)`存储，以`_tz`结尾，内容为`ZoneId`。
 * `日时`，系统和本地，分别以`_dt`和`_ldt`结尾。
 * `日期`，系统和本地，分别以`_dd`和`_ldd`结尾。
 * `时间`，系统和本地，分别以`_tm`和`_ltm`结尾。

### 0.3.3.Spring风格，在`silencer`和`faceless`有详细说明。

 * 优先使用`constructor`注入，用`lombok`的`@RequiredArgsConstructor`。
 * 次之使用`setter`注入，用`lombok`的`@Setter(onMethod = @__({@Autowired}))`
   或`kotlin`的`@Autowired lateinit var`。
 * 不要使用`Field`注入，坏处自己搜。

### 0.3.4.属性文件风格

 * 尽量使用`properties`和列编辑，`yml`的缩进有时会困扰。
 * 一组关联属性，一个`properties`，分成文件便于管理。
 * `conditional*`类spring配置开关，使用`spring.`前缀。
 * `wings-`功能类配置，使用`wings.`前缀。
 * 推荐`kebab-caseae`命名，即`key`全小写，使用`-`分割。

## 0.4.技术选型

技术选型，遵循Unix哲学，主要回答，`为什么`和`为什么不？`

### 0.4.1.Spring Boot

事实标准，从业人员基数大，容易拉扯队伍。

### 0.4.2.ShardingSphere

分表分库，足以解决90%的`数据大`的问题。大部分公司面临的情况是`数据大`而不是`大数据`。
`大`主要指，单表超过`500万`，查询速度超过`10ms`的`OLTP`业务场景。

此时合适的解决方案，应该是读写分离，水平分表，优化数据结构，拆分业务场景。
不建议微服务，集群，甚至`大数据`。因为服务治理的难度容易拖垮团队。

选择`shardingjdbc`，个人认为其在实践场景，文档，代码及活跃度上高于竞品。

 * [mycat](http://www.mycat.io/)

### 0.4.3.ServiceComb

阅读过部分源码，个人比较喜欢 ServiceComb 的哲学，而且力道刚刚好。

`dubbo`更多的是服务治理，中断又重启，虽社区呼声大，但时过境迁了。

`sofa`技术栈，有着金服实践，功能强大，社区活跃，仍在不断开源干货中。
如果团队够大，项目够复杂，管理和协作成本很高时，推荐使用。

 * [dubbo](http://dubbo.apache.org)
 * [sofa stack](https://www.sofastack.tech/)
 
### 0.4.4.kotlin

`kotlin`比`scala`更能胜任`更好的java`，主要考量的是团队成本，工程实践性价比。

### 0.4.5.webmvc

尽管`webflux`在模型和性能好于serverlet体系，当前更多的是阻塞IO，多线程场景。
所以，当前只考虑 webmvc，用thymeleaf模板引擎。

### 0.4.6.lombok

简化代码，开发时，需要自己在pom中引入

### 0.4.7.git-flow

使用`git flow`管理工程

[git-flow-cheatsheet](http://danielkummer.github.io/git-flow-cheatsheet/)

### 0.4.8.guava&commons

guava和commons-lang是java程序员进阶必备的工具包。

## 0.5.常见问题

### 001.getHostName() took 5004 milliseconds
InetAddress.getLocalHost().getHostName() took 5004 milliseconds to respond. 
Please verify your network configuration (macOS machines may need to add entries to /etc/hosts)

``` bash
hostname
# 输出 trydofors-Hackintosh.local

cat /etc/hosts
# 在localhost后面，填上 trydofors-Hackintosh.local
127.0.0.1	    localhost trydofors-Hackintosh.local
```