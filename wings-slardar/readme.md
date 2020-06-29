# 3.鱼人守卫(slardar)

大鱼人，是一个水手，他会星星点灯

![slardar](./slardar_full.png)

为Servlet体系下的SpringMvc和WebSocket的提供OAuth2的鉴/授权等控制。
当有多个`WebSecurityConfigurerAdapter`时，需要注意`Order`的顺序。

## 3.1.OAuth2xFilter扩展

只应在 AuthorizationServer使用，不需要在ResourceServer使用。
OAuth2比用户密码的登录方式更有利于扩展，十分成熟，被广泛使用和支持。
Slardar通过Filter增加`grant_type=password`的别名穿透机制，使

 * 使用`password`模式，不暴露`client_secret`
 * 使获得UserDetail时，可以通过Context获得request参数。
 
别名`alias`的用途，除了因此oauth特征外，还用来识别登录类型，比如
`oauth-password-alias=sms,em`，的配置，可以区分短信和邮件名登录。
这样，`client-id`和`grant-type`和`password`别名有无数种组合，
达到在控制层识别，并控制验证行为。

这样在不污染Spring和OAuth2的情况下，可使`/oauth/token` 支持

 * 短信验证码登录（POST方式）
 * 用户名密码登录（POST方式）
 * API授权（POST+JSON+验签）

方法级的权限控制`@EnableGlobalMethodSecurity(securedEnabled = true)`，
尽量使用`@Secured("IS_AUTHENTICATED_ANONYMOUSLY")`，这样会方便查找，
非复杂条件不用使用SpEL的`@PreAuthorize("hasAuthority('ROLE_TELLER')")`。
@Secured只作用于`ROLE_*`，在不自定义`RoleVoter`时得用`@PreAuthorize`

一般情况下，不需要方法级的控制，在Security使用filter拦截更适合。

 * ClientDetailsService，默认从配置文件中加载(只能build)
 * UserDetailsService，自定义，全局注入即可。
 * WingsTokenStore，集合memory和Redis，在auth和res上缓存UserDetail。
 * WingsTokenEnhance，支持wing格式的token和第三方token。
 * UserDetail和TypeIdI18nUserDetail自定义，内部保存需要的状态。

实际项目中，每个工程会独立配置Security和OAuth的各个服务器。
因此，提供了`WingsOAuth2xConfiguration.Helper`，注入后，协助配置。
当`WebSecurity`和`ResourceServer`共存时，以`ResourceServer`做配置即可

```
// ======= extends WebSecurityConfigurerAdapter and expose Bean ====
// need AuthenticationManager Bean
// password grants are switched on by injecting an AuthenticationManager
@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
@Override
public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
}
```

对于ResourceServer与AuthorizationServer分开的情况，参考example工程，只需要提供
ResourceServer和`@Bean RemoteTokenServices tokenService()`即可。

对于使用`WingsOAuth2xLogin`登录功能，有时需要处理Oauth2信息，如下即可，
```
@ExceptionHandler(Exception.class)
public ResponseEntity<OAuth2Exception> handleException(Exception e) 
```

关于详细配置项，参考 wings-filter-oauth2x-79.properties 的注释
`wings.slardar.oauth2x.client.*`是slardar特有的client，属于配置项，
能够被filter使用，会自动填充`client_secret`。

## 3.2.CaptchaFilter防扒

通过`WingsCaptchaContext`设置规则，可以实现全局的防扒验证码。
验证码的验证规则可以自定义，比如时间戳比较，短信码比较等。

`WingsCaptchaContext.set`时，需要在handler中白名单和验证法。

 * 白名单，指验证返回`NOOP`的URI。
 * 验证码，为自定义字符串，在context中可供后续请求获得。
 * 验证法，自定义算法，成功返回`PASS`，否则`FAIL`。
 * 有效期，验证码在设定生命期内，返回`PASS`之前都有效。

举例，详见`TestCaptchaController`的三个方法。

## 3.3.OverloadFilter过载

 * 自动或手动设置`最大同时进行请求数`。超过时，执行`fallback`。
 * 不影响性能的情况下，记录慢响应URI和运行状态。
 * 优雅停止服务器，阻断所有新请求。
 * 相同IP请求过于频繁，执行fallback。
 * 防扒验证码（图形或短信）
 
 `最大同时进行请求数`，指已经由Controller处理，但未完成的请求。

## 3.4.TerminalFilter终端

 * 设置 Locale 和 TimeZone
 * 设置 remote ip
 * 设置 user agent信息

## 3.5.Session,Timezone和I18n

用户登录后，自动生成时区和I18n有关的Context。
通过`SecurityContextUtil`获得相关的Context。

 * WingsOAuth2xContext.Context Oauth2有关的
 * WingsTerminalContext.Context 登录终端有关的

## 3.6.缓存Redis和Caffeine

默认提供caffeine和Redis缓存，可以注入

* CaffeineCacheManager caffeineCacheManager
* RedisTemplate<String, Object> redisTemplate
* StringRedisTemplate stringRedisTemplate
* RedissonSpringCacheManager redissonCacheManager

其中，caffeine默认开启，且 `spring.cache.type=caffeine`，
而redis和redission需要引入依赖才会开启。

三种不同缓存级别前缀，分别定义不同的ttl,idle,size

* `program.` - 程序配置，永存
* `general.` - 标准配置，1天
* `service.` - 服务级的，1小时
* `session.` - 会话级的，10分钟

``` java
@CacheConfig(cacheManager = MANAGER_CAFFEINE, 
cacheNames = LEVEL_GENERAL + "OperatorService")

@Cacheable(key = "'all'", 
cacheNames = LEVEL_GENERAL + "StandardRegion", 
cacheManager = MANAGER_CAFFEINE)

@CacheEvict(key = "'all'", 
cacheNames = LEVEL_SERVICE + "StandardRegion", 
cacheManager = MANAGER_REDISSON)
```

## 3.7.参考资料

[OAuth 2 Developers Guide](https://projects.spring.io/spring-security-oauth/docs/oauth2.html)
[OAuth2 boot](https://docs.spring.io/spring-security-oauth2-boot/docs/current/reference/htmlsingle/)
[Spring Security](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/)

## 3.8.常见问题

### 001.spring 找不到 RedissonSpringCacheManager

在maven依赖中，把slardar以下3个optional的依赖引入 

* spring-boot-starter-data-redis
* redisson-spring-data-22

### 002.修改过的默认配置

slardar，使用undertow，并提供了一下默认配置

### 003.不想使用oauth2内容

spring.wings.slardar.oauth2x.enabled=false

### 004.本地docker redis 5

```bash
docker run -d \
 --name wings-redis \
 --restart=unless-stopped \
 -v /Users/trydofor/Docker/redis/data:/data \
 -p 6379:6379 \
redis:5.0 \
redis-server --requirepass moilioncircle
```
### 005.Spring的Oauth2代码在哪里

org.springframework.security.oauth2.provider.endpoint

* TokenEndpoint - `/oauth/token`
* AuthorizationEndpoint - `/oauth/authorize`

### 006.Oauth登录 "invalid_client"

需要设置client组的属性(id,secret等)，密码一定要复杂。
`wings.slardar.oauth2x.client.xxxx.client-*`
其中xxxx要保证唯一，可用来标识不同业务模块。

### 007.error处理，需要自定义page或handler

需要根据spring约定和实际需要，自定义一套机制。
但是不要使用`spring.mvc.throw-exception-if-no-handler-found=true`，
因为，异常之所以叫异常，就不能当做正常，避免用来处理正常事情。

 * controller层异常用`@ControllerAdvice` 和 `@ExceptionHandler`
 * service层异常，自行做业务处理，或AOP日志
 
[error-handling](https://docs.spring.io/spring-boot/docs/2.2.7.RELEASE/reference/htmlsingle/#boot-features-error-handling)
