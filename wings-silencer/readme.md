# 1.沉默术士(silencer)

最小化依赖 springboot，提供以下能力

 * 自动加载SpringBoot配置(wings-conf)
 * properties中的直接写中文，不需要unicode转码。
 * 自动加载i8n配置(wings-i18n)

 
## 1.1.spring命名规则

 * `/wings-conf/` 放置拆分的配置文件，按字母顺序加载和覆盖。
 * `/wings-i18n/` 放置拆分的多国语的信息文件。
 * `**/spring/boot/` boot有关的配置，如`spring.factories`
 * `**/spring/bean/` 和DI有关的bean。
 * `**/spring/conf/` 和properties有关的配置。
 
使用`idea`开发时，需要在`Project Structure`/`Facets`/`Spring`设置中加入

 * `boot/WingsSilencerAutoComponentScan`，用来识别`bean/*`， 
 * `boot/WingsDataSourceAutoConfiguration` 用来识别`FlywaveDataSources`
 
打开以下配置，`Settings`/`Annotation Processors`/`Enable annotation processing`
 
## 1.2.自动配置（wings-conf）

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

支持`profile`格式，但是从命名上，要求`profile`用`.`标识，和spring对比如下。
文件名不建议使用`.`，`profile`不包括`.`，否则会造成解析错误。

 * `application.properties`
 * `wings-conf/shardingsphere-datasource-79.properties`
 * `application-{profile}.properties`
 * `wings-conf/shardingsphere-datasource-79.{profile}.properties`

[参考资料 docs.spring.io](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)

 - "23.5 Application Events and Listeners"
 - "24. Externalized Configuration"
 - "77.3 Change the Location of External Properties of an Application"
 - "76.3 Customize the Environment or ApplicationContext Before It Starts"

 
## 1.2.自动多国语（wings-i18n）

spring自身对多国语(I18N)支持的很好，稍加组织就可利用，就可以更好的工程化。
自动扫描`classpath*:/wings-i18n/**/*.properties`，加载分隔成多份的配置。

spring对MessageSource的加载与configuration的机制不同，不需要unicode转义。

