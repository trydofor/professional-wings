# 1.沉默术士(silencer)

一切魔法，遭遇了他，都将归于寂静。silence fool

![silencer](./silencer_full.png)

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
 * `*Configuration` 必须都条件加载，前缀`spring.wings.`，可以关闭。
 * `**/spring/boot/` 手动加载，boot有关的配置，如`spring.factories`
 * `**/spring/bean/`  自动加载，比如@ComponentScan指定。
 * `**/spring/conf/` 自动或手动加载，需要暴露的properties的配置。
 
使用`idea`开发时，需要在`Project Structure`/`Facets`/`Spring`设置中加入

 * `boot/WingsComponentScanner`或`/bean/*` 
 * `boot/WingsDataSourceConfigAware` 用来识别`FlywaveDataSources`
 
打开以下配置，`Settings`/`Annotation Processors`/`Enable annotation processing`

注意：在`@Configuration`中的内部类，`static class`是按独立类处理的，不受外层约束。

在wings工程中，会存在`wings-conditional-manager.properties`配置，作为功能开关
可以通过属性`spring.wings.verbose.enabled=true` 通过日志INFO查看。
 
## 1.2.自动配置(wings-conf)

支持配置文件的`分割`，`覆盖`，`禁用`和`profile`，更有利于工程化的管理。

* 分隔，指配置项可以按模块，功能，自由组成独立的配置文件。
* 覆盖，配置项按一定的优先级（加载顺序）可以覆盖
* 禁用，可以通过block-list，禁止某配置文件加载
* profile，同spring规则。

### 1.2.1.配置分割

实际项目开发中，只有一个 `application.*`不利于分工和管理的，应该是，

 * shardingsphere-datasource-79.properties
 * shardingsphere-sharding-79.properties
 * logger-logback-79.properties

通过`EnvironmentPostProcessor`扫描`各路径`中`/wings-conf/**/*.*`，规则如下，

 1. Command line arguments. `--spring.config.location`
 2. Java System properties `spring.config.location`
 3. OS environment variables. `SPRING_CONFIG_LOCATION`
 4. default `classpath:/,classpath:/config/,file:./,file:./config/`
 5. `classpath:/`会被以`classpath*:/`扫描
 6. 任何非`classpath:`,`classpath*:`的，都以`file:`扫描
 7. 以`/`结尾的当做目录，否则作为文件
 8. 从以上路径，优先加载`application.*`，次之`wings-conf/**/*.*`

`各路径`指按照上述顺序，把路径拆分后，依次扫描，优先级为FIFO先进先出（值覆盖有关）。

目前只加载 `*.yml`, `*.yaml`,`*.xml`, `*.properties`三种扩展名的配置文件。
默认的文件名字后面，都会跟上`-79`序号，方便根据文件名排序设置默认值。

每个配置文件都由一下几部分构成:`dirname`+`basename`+`seq`+`profile`+`extname`.
例如，`classpath:/wings-conf`+`/`+`wings-mirana`+`-`+`79`+`.`+`properties`
相同`basename`为同一配置，配置无序号，视序号为`99`。

配置文件，以Resource首先按扫码顺序排序，然后按base归类，按seq升序调整（值覆盖有关）。

所有配置文件必须UTF8编码，这样才可以更好的支持unicode，可以直接写中文。
自动配置时对非ascii进行自动转义，以支持spring默认的按byte读取行为。

### 1.2.2.配置文件profile

支持`profile`格式，但是从命名上，要求`profile`用`.`标识，和spring对比如下。
文件名不建议使用`@`，`profile`不包括`.`，否则会造成解析错误。

 * `application.properties`
 * `application-{profile}.properties`
 * `wings-conf/shardingsphere-datasource-79.properties`
 * `wings-conf/shardingsphere-datasource-79@{profile}.properties`

相同`basename`+`seq`的config是同一组，会移除掉非活动的profile
以`@`区分profile主要是因为，wings-conf文件名中存在`-`，避免造成误解析。
在使用`spring.profiles.active`时，要确保配置文件按spring约定加载。

### 1.2.3.配置禁用

存在于`/wings-conf/wings-conf-block-list.cnf`的文件名，不会自动加载。

 * 一行一个文件名，区分大小写。
 * `#`开头标识注释，自动忽略首尾空白。
 * 以`String.endWith`判断，全路径精确匹配。
 * `profile`格式的配置文件，需要单独

### 1.2.4.参考资料

[参考资料 docs.spring.io](https://docs.spring.io/spring-boot/docs/2.2.7.RELEASE/reference/htmlsingle/)

 - "4.1.6. Application Events and Listeners"
 - "4.2. Externalized Configuration"
 - "9.2.3. Change the Location of External Properties of an Application"
 - "9.1.3. Customize the Environment or ApplicationContext Before It Starts"
 
## 1.3.自动多国语(wings-i18n)

wings启动时，可以修改系统默认locale和zoneid，通过以下配置，空置表示维持系统默认。

 * wings.i18n.locale=zh_CN
 * wings.i18n.zoneid=Asia/Shanghai
 * wings.i18n.bundle=classpath*:/wings-i18n/**/*.properties

同时，spring自身对多国语(I18N)支持的很好，稍加组织就可利用，就可以更好的工程化。
自动扫描` wings.i18n.bundle`配置项（逗号分隔多个路径），加载分隔成多份的配置。

spring对MessageSource的加载与configuration的机制不同，不需要unicode转义。

`LocaleContextResolver` 会俺以下优先级，获得多国语设置。

 1. request被设置好的`WINGS.I18N_CONTEXT`
 2. query string `locale`, `zoneid`
 3. cookie `WINGS_LOCALE`, `WINGS_ZONEID`
 4. http header `Accept-Language`,`Zone-Id`
 5. 系统默认值
 
此处为行为约定，基于servlet或webflux的具体实现。`WingsLocaleResolver`是一个实现。

spring默认以如下配置为入口，逗号分隔，保留不带国家地区的bundle格式名。
`spring.messages.basename=messages,config.i18n.messages`
这样可以在classpath下存在以下格式的文件，命名避免使用`.`(会被换做`/`扫描)

 * message.properties  必须存在，以bundle名的默认文件
 * message_en.properties 推荐这种，不带国家，为所以en提供默认值
 * message_en_US.properties
 * message_en_US_UNIX.properties
 
 提供 CombinableMessageSource 可以动态添加多国语信息

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

**Json内容的国际化**，通过注解和类型自动对内容进行i18n转换，以字符串输出。
`I18nString`类型会自动转换。`CharSequence`要`@JsonI18nString`注解才转化。

自动转化时，使用注入的`messageSource`和`WingsI18nContext`获得相应语言。
使用`@JsonI18nString(false)`，可以关闭自动转换。

`R.I<T>`为常用返回值类型，当存在`i18nCode`时，会用i18n信息自动替换`message`。

常用的Jackson注解
 * @JsonRawValue - number不变字符串，字符串不转义。
 * @JsonFormat - 指定格式
 * @JsonIgnore/JsonProperty - 忽略该字段
 * @JsonProperty - 重命名
 * @JsonNaming - 命名规则
 * @JsonRootName(value = "user") - 增加一个头key
 * @JsonUnwrapped - 干掉包装类
 
[jackson注解](https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations)
[spring定制jackson](https://docs.spring.io/spring-boot/docs/2.2.7.RELEASE/reference/htmlsingle/#howto-customize-the-jackson-objectmapper) - 9.4.3. Customize the Jackson ObjectMapper

Jackson中涉及到泛型，参数类型，必备技能

``` java
TypeReference ref = new TypeReference<List<Integer>>() { };
// TypeFactory 中有很丰富的类型构造
JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Foo.class)
```

## 1.5.logging/logback

参考`wings-logging-79.properties`配置，默认使用springboot配置。

 * 只需要console输出（如果docker内）不需要额外设置。
 * 同时需要console和file，则增加以下配置`logging.file.name=/tmp/wings-example.log`
 * 只需要file，则再增加`logging.config=classpath:logback-fileonly.xml`

推荐的logging配置

 * logging.level.root=INFO
 * logging.level.org.springframework.web=DEBUG
 * logging.level.org.jooq=DEBUG
 * logging.level.<忽略项>=OFF

推荐使用`wings-starter.sh`启动，`wings-starter.env`配置基础参数。

## 1.6.restTemplate和okhttp

默认使用okhttp3作为restTemplate的实现。按spring boot官方文档和源码约定。
并可以 autoware OkHttpClient 直接使用，默认**信任所有ssl证书**，如安全高，需要关闭。
如果需要按scope定制，使用RestTemplateBuilder，全局应用使用RestTemplateCustomizer。

[RestTemplate 定制](https://docs.spring.io/spring-boot/docs/2.2.7.RELEASE/reference/htmlsingle/#boot-features-resttemplate-customization)
org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
