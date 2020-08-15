# 0.专业大翅 (pro.fessional.wings)

不是为吃货准备的伪装成吃货的项目，追忆大把的青春。  
其目标是单应用，分表分库，微服务的平滑过渡。

![wings ti6](./wings-ti6-champion.png)

Wings是springboot的一个脚手架，没有魔法和定制，主要有以下特点：

 * 提供了多语言多时区真实解决方案（动态语言包，时区，夏令时，闰秒）
 * 提供了数据库版本和数据版本管理（表变更变多了，数据维护多了）
 * 安排了一套油腻的约定和工程实践（枚举类，配置文件，模板等约定）
 * 解决了软件开发中最难的命名问题（允许使用中文命名，解决行业黑话）
 * 功能池很深，对功能有独到的理解（读3遍官方文档，debug部分源码）
 * 不懂代码的看文档，都看不懂别用（这是你的homework，及格线）
 * java-8, kotlin-1.3.x, springboot-2.2.x, jooq, mysql

## 0.1.项目技术

项目秉承以下价值观和团队规则

 * 静态优于动态，能编码的就不反射。
 * 强类型优于弱类型，能class就不map，能enum就不const
 * 编译时优于运行时，能在编译时解决的必须解决
 * IDE优于editor，IDE能提供语法分析，上下文解析
 * 命名规约中，可读性优先。不怕长，不怕怪异
 * 奥卡姆剃刀，能简单的实现，就不用搞复杂的
 * 防御性编程风格，默认输入数据不可信，必须验证。

由以下几个子工程构成

 * [演示例子/example](wings-example/readme.md) 集成了以上的例子
 * [沉默术士/silencer](wings-silencer/readme.md) 工程化的自动装配，I18n等
 * [虚空假面/faceless](wings-faceless/readme.md) DAO，分表分库，数据版本管理
 * [鱼人守卫/slardar](wings-slardar/readme.md) 基于Servlet体系的WebMvc
 * [术士大叔/warlock](wings-warlock/readme.md) 基于Servlet体系的AuthZ和AuthN

涉及技术和知识点

 * [Spring Boot](https://docs.spring.io/spring-boot/docs/2.2.7.RELEASE/reference/htmlsingle/)
 * [Apache ShardingSphere](https://shardingsphere.apache.org/index_zh.html)
 * [Jooq - 强类型 sql-mapping](https://www.jooq.org/)

## 0.2.编码风格

使用`IntelliJIdea`作为开发`IDE`，可使用`code style`和`live templates`。
`wings-idea-style.xml`在`Setting/Editor/Code Style`导入。

`wings-idea-live.xml`需要手动放到`$config/templates/`，没有则新建

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
 * CheckStyle - 代码质量
 * GeneateAllSetter - alt-enter 生成全部 po.setXxx("")
 * Git Flow Integration - 集成了git-flow
 * GitToolBox - 自动 fetch
 * Grep Console - 控制台的日志分颜色显示和过滤
 * kotlin - 默认安装了
 * lombok - IntelliJ Lombok plugin
 * MapStruct Support - MapStruct support
 * Maven Helper - 帮助管理maven
 * Quick File Preview - 单击快速浏览文件
 * Rainbow Brackets - 彩虹括号
 * Request mapper - 快速查找 mapping
 * Statistic - 统计一下自己的代码
 * String Manipulation -  对字符串的各种操作和转换。

### 0.2.1.Java风格，遵循标准的java规范，但**可读性优先**。

 * `static final` 不一定全大写。如`logger`比`LOG`可读性好。
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

本地日时，必须有`时区`配合，命名后缀如下，

 * `时区`，以`_tz`或`_zid`为后缀，内容为`ZoneId`。
 * `日时`，系统和本地，分别以`_dt`和`_ldt`结尾。
 * `日期`，系统和本地，分别以`_dd`和`_ldd`结尾。
 * `时间`，系统和本地，分别以`_tm`和`_ltm`结尾。
 
日期时间，需要程序运行环境和db在统一时区，推荐核心用户所在时区。UTC不是最优的选择。
根据业务场景，按需要处理时间存储格式和读写比例。

 * 统计类业务，通常写入时转化，存入用户本地时间（和时区），读取时不转换。
 * 协作类业务，通常写入时，使用系统时间，读取时转换。

如果需要转换时间，需要在用户界面统一（如controller）处理。

### 0.2.4.属性文件风格

 * 尽量使用`properties`和列编辑，`yml`的缩进有时会困扰。
 * 一组关联属性，一个`properties`，分成文件便于管理。
 * `conditional*`类spring配置开关，使用`spring.`前缀。
 * `wings-`功能类配置，使用`wings.`前缀。
 * 推荐`kebab-caseae`命名，即`key`全小写，使用`-`分割。

### 0.2.5.Spring注入风格，在`silencer`和`faceless`有详细说明。

 * 优先使用`constructor`注入，用`lombok`的`@RequiredArgsConstructor`。
 * 次之使用`setter`注入，用`lombok`的`@Setter(onMethod = @__({@Autowired}))`
   或`kotlin`的`@Autowired lateinit var`。
 * 不要使用`Field`注入，坏处自己搜。

### 0.2.5.Spring MVC中的 RequestMapping 约定

 * 在方法上写全路径`@RequestMapping("/a/b/c.html")`
 * 在controller上写版本号`@RequestMapping("/v1")`
 * 不要相写相对路径，这样才可以通过URL直接搜索匹配。
 * 不管REST还是其他，url一定有扩展名，用来标识MIME和过滤

### 0.2.7.Spring Service 的接口和 DTO 约定

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

简化代码，开发时，需要自己在pom中引入

### 0.3.8.git-flow

使用`git flow`管理工程

[git-flow-cheatsheet](http://danielkummer.github.io/git-flow-cheatsheet/)

### 0.3.9.guava&commons

以下3个是java程序员进阶必备的工具包，其中commons-lang3，spring-boot定义了版本。

 * guava - https://github.com/google/guava
 * commons-lang3 - https://commons.apache.org/proper/commons-lang/
 * commons-io - http://commons.apache.org/proper/commons-io/

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

### 002.工程中哪些参数是必须打开的

``` bash
# 找到所以开关文件
find . -name 'wings-conditional-manager.properties' \
| egrep -v -E 'target/|example/' 

./wings-slardar/src/main/resources/wings-conf/wings-conditional-manager.properties
./wings-faceless/src/main/resources/wings-conf/wings-conditional-manager.properties
./wings-silencer/src/main/resources/wings-conf/wings-conditional-manager.properties

# 找到所false的开关
find . -name 'wings-conditional-manager.properties' \
| egrep -v -E 'target/|example/' \
| xargs grep 'false'

# 以下2个需要在flywave和enum时开启
spring.wings.flywave.enabled=false
spring.wings.enumi18n.enabled=false
``` 

### 003.如何创建一个工程

``` bash
git clone https://gitee.com/trydofor/pro.fessional.wings.git
cd pro.fessional.wings
wings-example/wings-init-project.sh

# 如果不能执行bash，那么自行编译和执行
cd wings-example/src/test/java/
pro/fessional/wings/example/exec/Wings0InitProject.java
```

### 004.lib工程和boot工程的区别

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

### 005.jackson和fastjson

wings中和springboot一样，默认采用了jackson进行json和xml绑定。
不过wings的中对json的格式有特殊约定，比如日期格式，数字以字符串传递。
再与外部api交换数据时可能格式不匹配，这时需要用有background

 * 使用2套jackson配置
 * 使用jackson注解 @JsonRawValue
 * 使用fastjson(不推荐，需1.2.69+，SafeMode, 安全漏洞)

### 006.为什么是dota的英雄

有这样一个团队，她是做对日金融的，穿拖鞋裤衩上班，课间可以团dota，cs，跑跑卡丁车。
日本人组团爱上了瓜子，黄飞红，米线，火锅。团队只有一个要求，活干的漂亮，快，零缺陷。

我本人与dota有缘无分，现在也就连直播都不爱看了，只是心中有个地方，叫dota

 * TI6，她在西雅图，我在特拉华
 * TI9，她在奔驰馆，我在大虹桥

### 007.类型间Mapping比较

根据以下文章，推荐使用 Mapstruct，主要是其静态性。
对于比较复杂的mapping，使用expression，qualifiedByName，spring注入。
自动生成的代码位于`target/generated-sources/annotations/`

 * [Quick Guide to MapStruct](https://www.baeldung.com/mapstruct)
 * [Mapping Collections with MapStruct](https://www.baeldung.com/java-mapstruct-mapping-collections)
 * [Mapstruct 性能比较](https://www.baeldung.com/java-performance-mapping-frameworks)
 * [Mapstruct ide&mvn支持](https://mapstruct.org/documentation/installation/)