# 1.沉默术士(silencer)

最小化依赖 springboot，提供以下能力

 * 自动加载SpringBoot配置(wings-conf)
 * properties中的直接写中文，不需要unicode转码。
 * 自动加载i8n配置(wings-i18n)
 * 工程化Jackson配置(wings-jackson-79.properties)
 * 多时区
 * 不支持webflux
 
## 1.1.spring命名规则

 * `/wings-conf/` 自动加载，放置拆分的配置文件，按字母顺序加载和覆盖。
 * `/wings-i18n/` 自动加载，放置拆分的多国语的信息文件。
 * 所有`Configuration`必须条件加载，可以关闭。除WingsAutoConfigProcessor。
 * `**/spring/boot/` 手动加载，boot有关的配置，如`spring.factories`
 * `**/spring/bean/`  自动加载，比如@ComponentScan指定。
 * `**/spring/conf/` 自动或手动加载，需要暴露的properties的配置。
 
使用`idea`开发时，需要在`Project Structure`/`Facets`/`Spring`设置中加入

 * `boot/WingsComponentScanner`或`/bean/*` 
 * `boot/WingsDataSourceConfigAware` 用来识别`FlywaveDataSources`
 
打开以下配置，`Settings`/`Annotation Processors`/`Enable annotation processing`

注意：在`@Configuration`中的内部类，`static class`是按独立类处理的，不受外层约束。

 
## 1.2.自动配置(wings-conf)

支持配置文件的`分割`，`禁用`和`profile`，更有利于工程化的管理。

### 1.2.1.配置分割

实际项目开发中，只有一个 `application.*`不利于分工和管理的，应该是，

 * shardingsphere-datasource-79.properties
 * shardingsphere-sharding-79.properties
 * logger-logback-79.properties

通过`EnvironmentPostProcessor`扫描`各路径`中`/wings-conf/*.*`，规则如下，

 1. Command line arguments. `--spring.config.location`
 2. Java System properties `spring.config.location`
 3. OS environment variables. `SPRING_CONFIG_LOCATION`
 4. default `classpath:/,classpath:/config/,file:./,file:./config/`

`各路径`指按照上述顺序，把路径拆分后，依次扫描，序号大的优先级高（默认值有关）。

目前只加载 `*.yml`, `*.yaml`,`*.xml`, `*.properties`三种扩展名的配置文件。
默认的文件名字后面，都会跟上`-79`序号，方便根据文件名排序设置默认值。

所有配置文件必须UTF8编码，这样才可以更好的支持unicode，可以直接写中文。
自动配置时对非ascii进行自动转义，以支持spring默认的按byte读取行为。

### 1.2.2.配置文件profile

支持`profile`格式，但是从命名上，要求`profile`用`.`标识，和spring对比如下。
文件名不建议使用`.`，`profile`不包括`.`，否则会造成解析错误。

 * `application.properties`
 * `wings-conf/shardingsphere-datasource-79.properties`
 * `application-{profile}.properties`
 * `wings-conf/shardingsphere-datasource-79.{profile}.properties`


### 1.2.3.配置禁用

存在于`/wings-conf/wings-conf-black-list.cnf`的文件名，不会自动加载。

 * 一行一个文件名，区分大小写。
 * `#`开头标识注释，自动忽略首尾空白。
 * 以`String.endWith`判断，全路径精确匹配。
 * `profile`格式的配置文件，需要单独

### 1.2.4.参考资料

[参考资料 docs.spring.io](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)

 - "23.5 Application Events and Listeners"
 - "24. Externalized Configuration"
 - "77.3 Change the Location of External Properties of an Application"
 - "76.3 Customize the Environment or ApplicationContext Before It Starts"

 
## 1.3.自动多国语(wings-i18n)

spring自身对多国语(I18N)支持的很好，稍加组织就可利用，就可以更好的工程化。
自动扫描`classpath*:/wings-i18n/**/*.properties`，加载分隔成多份的配置。

spring对MessageSource的加载与configuration的机制不同，不需要unicode转义。

`LocaleContextResolver` 会俺以下优先级，获得多国语设置。

 1. request被设置好的`WINGS.I18N_CONTEXT`
 2. query string `locale`,`zoneid`
 3. cookie `WINGS_LOCALE`, `WINGS_ZONEID`
 4. http header `Accept-Language`,`Zone-Id`
 5. 系统默认值
 
此处为行为约定，基于servlet或webflux的具体实现，在其他工程。

通过注入`CombinableMessageSource`，可以动态添加和组合其他`MessageSource`。

## 1.4.Json格式约定(jackson)

考虑到java和js的差异，数据传递和功能上，有以下约定。

 * 浮点数值，以java.BigDecimal与js.string互传。
 * java.null 不输在Json中互传。
 * java.整数，与js.number/string互传。
 * java.日时，包括`util.Date`,`sql.Date`,`time.Local*|Zoned*|Instant`
 * java.日时，以时间戳形式与js.number互传。
 * java.日时，都以`yyyy-MM-dd HH:mm:ss`格式与js.string互传。
 * java.时区，以ZoneId字符串格式与js.string互传。

此外，要注意js的特殊性，和一些宽松的json格式。

 * Json中最好只有2种基本数据类型：boolean,string
 * Js不应该有任何有精度要求的金额计算，只应负责显示服务器端计算结果。
 * 因为时间的特殊性，还有时区和夏令时，在保证精度的同时要提供可读性。
 * 51bits位的long，必须使用string，因为IEE754无法正确表示。
 * 确保jsr310格式兼容，如依赖`jackson-datatype-jsr310`。
 * ZoneId应首选`IANA TZDB`格式，如`America/New_York`。
 * 带时区(`Z`)的序列化与反序列化过程，会丢失夏令时信息。
 
[参考资料 docs.spring.io](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
 
 - 79.3 Customize the Jackson ObjectMapper

