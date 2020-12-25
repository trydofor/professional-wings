# 3.鱼人守卫(slardar)

大鱼人，是一个水手，他会星星点灯

![slardar](./slardar_full.png)

为Servlet体系下的SpringMvc的提供i18n和security的简单封装。

 * 工程化Jackson配置(wings-jackson-79.properties)
 * domain继承换肤(通过host判定，且可继承)
 * 多时区
 * caffeine cache
 * 不支持webflux
 * 特殊功能的filter

`LocaleContextResolver` 会俺以下优先级，获得多国语设置。

 1. request被设置好的`WINGS.I18N_CONTEXT`
 2. query string `locale`, `zoneid`
 3. cookie `WINGS_LOCALE`, `WINGS_ZONEID`
 4. http header `Accept-Language`,`Zone-Id`
 5. 系统默认值

此处为行为约定，基于servlet或webflux的具体实现。`WingsLocaleResolver`是一个实现。

## 3.1.Json格式约定(jackson)

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
[spring定制jackson](https://docs.spring.io/spring-boot/docs/2.4.0/reference/htmlsingle/#howto-customize-the-jackson-objectmapper) - 9.4.3. Customize the Jackson ObjectMapper

Jackson中涉及到泛型，参数类型，必备技能

``` java
TypeReference ref = new TypeReference<List<Integer>>() { };
// TypeFactory 中有很丰富的类型构造
JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Foo.class)
```

## 3.2.domain继承换肤

不同于反向代理(nginx)的rewrite，此功能是于java的extend和override一致。

* extend - 子domain拥有父domain的全部URL
* override - 子domain可以override父domain的URL
* 子domain有自己独立的URL
* domain的继承基于 host

### 3.2.1.场景举例

假设`a.com`是一个有完整的功能domain，举例包括以下3个URL

 * GET /user-list.json - 基于Controller
 * GET /css/main.css - 静态资源
 * GET /login.html - 基于Controller

此时，来个加盟商`b.com`，除了皮肤，顶级域名外，都和`a.com`一样。  
再后来，`b.com`有了自己的需求，部分界面和url和`a.com`的需求分叉了。
不同的功能自己实现，放在约定的prefix下，此时URL分布如下，

 * GET /login.html - a.com(父)，b.com(子)
 * GET /user-list.json - a.com(父)
 * GET /css/main.css - a.com(父)
 * GET /domain/b/user-list.json - b.com(子)
 * GET /domain/b/css/main.css - b.com(子)

当用户访问以下URL时，按照java的父子类override规则，调用如下，

 * a.com/login.html - /login.html(父)
 * a.com/user-list.json - /user-info.list(父)
 * a.com/css/main.css - /css/main.css(父)
 * b.com/login.html - /login.html(父)
 * b.com/user-list.json - /domain/b/user-list.json(子)
 * b.com/css/main.css - /domain/b/css/main.css(子)

实际项目中，以上场景多发生在resource和controller的Mapping中。

 * resource通常有`**`匹配，用反射ResourceHttpRequestHandler.getResource检查。
 * 若非ResourceHttpRequestHandler且match`**`，需要自己设法检查资源是否存在
 * 暂时不支持viewTemplate，同时也约定模板必须使用全路径。

根据wings mapping约定，避免使用相对路径，所以，b.com要在在class级做前缀。
``` java
@Controller
@RequestMapping("/domain/b")
public class UserController {
 
    @GetMapping("/user-info.json")
    public String fetchUserInfo() {
        // 不支持view，需要手动指定
        return "/domain/b/user-info";
    }
}
```

### 3.2.2.实现原理

在spring mvc体系中，一个请求进入servlet容器后，在worker线程中

* Filter#doFilter `before` chain.doFilter;
* DispatcherServlet#doService `call` doDispatch
* Filter#doFilter `after` chain.doFilter;

wings通过WingsDomainFilter，先检查host，如果是继承域，则构造子域全路径url，  
通过检查缓存和DispatchServlet中的HandlerMapping再构造RequestWrapper。

知识点提示，
* 在FilterChain.doFilter调用之前Request可用，而其后Response可用的，注意线程安全和性能。
* 默认静态资源在classpath中的 `/static`, `/public`, `/resources`, `/META-INF/resources`

## 3.3.常用功能

## 3.3.1.restTemplate和okhttp

默认使用okhttp3作为restTemplate的实现。按spring boot官方文档和源码约定。
并可以 autoware OkHttpClient 直接使用，默认**信任所有ssl证书**，如安全高，需要关闭。
如果需要按scope定制，使用RestTemplateBuilder，全局应用使用RestTemplateCustomizer。

[RestTemplate 定制](https://docs.spring.io/spring-boot/docs/2.4.0/reference/htmlsingle/#boot-features-resttemplate-customization)
org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration


## 3.3.2.缓存Caffeine

默认提供caffeine缓存，可以注入

* CaffeineCacheManager caffeineCacheManager
* RedisTemplate<String, Object> redisTemplate
* StringRedisTemplate stringRedisTemplate
* RedissonSpringCacheManager redissonCacheManager

其中，caffeine默认开启，且 `spring.cache.type=caffeine`，

三种不同缓存级别前缀，分别定义不同的ttl,idle,size

* `program.` - 程序配置，永存
* `general.` - 标准配置，1天
* `service.` - 服务级的，1小时
* `session.` - 会话级的，10分钟

``` java
@CacheConfig(cacheManager = Manager.CAFFEINE, 
cacheNames = Level.GENERAL + "OperatorService")

@Cacheable(key = "'all'", 
cacheNames = Level.GENERAL + "StandardRegion", 
cacheManager = Manager.CAFFEINE)

@CacheEvict(key = "'all'", 
cacheNames = Level.GENERAL + "StandardRegion", 
cacheManager = Manager.REDISSON)
```

## 3.3.3.Session,Timezone和I18n

用户登录后，自动生成时区和I18n有关的Context。
通过`SecurityContextUtil`获得相关的Context。

`WingsTerminalContext.Context`和登录终端有关的，  
需要打开TerminalFilter。


## 3.7.特别用途的filter

## 3.7.1.TerminalFilter终端

是否解析 WingsTerminalContext，默认`spring.wings.slardar.terminal.enabled=true`控制，  
同时依赖于 `WingsLocaleResolver`和`WingsRemoteResolver`

 * 设置 Locale 和 TimeZone
 * 设置 remote ip
 * 设置 user agent信息

## 3.7.2.CaptchaFilter防扒

是否开启验证码，`spring.wings.slardar.captcha.enabled=false`

通过`WingsCaptchaContext`设置规则，可以实现全局的防扒验证码。
验证码的验证规则可以自定义，比如时间戳比较，短信码比较等。

`WingsCaptchaContext.set`时，需要在handler中白名单和验证法。

 * 白名单，指验证返回`NOOP`的URI。
 * 验证码，为自定义字符串，在context中可供后续请求获得。
 * 验证法，自定义算法，成功返回`PASS`，否则`FAIL`。
 * 有效期，验证码在设定生命期内，返回`PASS`之前都有效。

举例，详见`TestCaptchaController`的三个方法。

## 3.7.3.OverloadFilter过载

是否限定请求并发，默认`spring.wings.slardar.overload.enabled=false`

 * 自动或手动设置`最大同时进行请求数`。超过时，执行`fallback`。
 * 不影响性能的情况下，记录慢响应URI和运行状态。
 * 优雅停止服务器，阻断所有新请求。
 * 相同IP请求过于频繁，执行fallback。

`最大同时进行请求数`，指已经由Controller处理，但未完成的请求。

其中，关闭`快请求`或`慢请求`功能，可以通过以下设置关闭，

 * `快请求` - `wings.slardar.overload.request-capacity=-1`
 * `慢请求` - `wings.slardar.overload.response-warn-slow=0`


## 3.9.常见问题

### 001.spring 找不到 RedissonSpringCacheManager

在maven依赖中，把slardar以下3个optional的依赖引入 

* spring-boot-starter-data-redis
* redisson-spring-data-22

### 002.修改过的默认配置

slardar，使用undertow，并提供了一下默认配置

### 003.session方案的选择

其实 hazelcast 是个不错的选择，若选用redis，切记redis必须`requirepass`

### 007.error处理，需要自定义page或handler

需要根据spring约定和实际需要，自定义一套机制。
但是不要使用`spring.mvc.throw-exception-if-no-handler-found=true`，
因为，异常之所以叫异常，就不能当做正常，避免用来处理正常事情。

 * controller层异常用`@ControllerAdvice` 和 `@ExceptionHandler`
 * service层异常，自行做业务处理，或AOP日志
 * 静态，src/main/resources/public/error/404.html
 * 模板，src/main/resources/templates/error/5xx.ftlh
 * `class MyErrorPageRegistrar implements ErrorPageRegistrar`

```
@ControllerAdvice(basePackageClasses = AcmeController.class)
public class AcmeControllerAdvice extends ResponseEntityExceptionHandler
// ///////
public ModelAndView resolveErrorView(HttpServletRequest request,
```

[error-handling](https://docs.spring.io/spring-boot/docs/2.4.0/reference/htmlsingle/#boot-features-error-handling)

### 008.undertow 启动时warn UT026010

在未配置websocket时，undertow使用默认buffer，出现以下警告。
需要定制`websocketServletWebServerCustomizer`，或设置
`spring.wings.slardar.undertow-ws.enabled=true`即可

在`io.undertow.websockets.jsr.Bootstrap` 68行，`buffers == null` 时
`UT026010: Buffer pool was not set on WebSocketDeploymentInfo, the default pool will be used`
默认 DefaultByteBufferPool(directBuffers, 1024, 100, 12);

### 009.OAuth2的参考资料

* [OAuth 2 Developers Guide](https://projects.spring.io/spring-security-oauth/docs/oauth2.html)
* [OAuth2 boot](https://docs.spring.io/spring-security-oauth2-boot/docs/current/reference/htmlsingle/)
* [Spring Security](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/)

### 010.如何配置security

security一定是系统中最为重要的部分，也是所有渗透入侵的重点，所以slardar无默认配置。

配置中可以使用Order，提供多个HttpSecurity。