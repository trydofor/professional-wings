# 3.鱼人守卫(slardar)

大鱼人，是一个水手，他会星星点灯

![slardar](./slardar_full.png)

为Servlet体系下的SpringMvc的提供i18n和security的简单封装。

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

 * WingsTerminalContext.Context 登录终端有关的

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

### 007.error处理，需要自定义page或handler

需要根据spring约定和实际需要，自定义一套机制。
但是不要使用`spring.mvc.throw-exception-if-no-handler-found=true`，
因为，异常之所以叫异常，就不能当做正常，避免用来处理正常事情。

 * controller层异常用`@ControllerAdvice` 和 `@ExceptionHandler`
 * service层异常，自行做业务处理，或AOP日志
 
[error-handling](https://docs.spring.io/spring-boot/docs/2.2.7.RELEASE/reference/htmlsingle/#boot-features-error-handling)

### 008.undertow 启动时warn UT026010

在未配置websocket时，undertow使用默认buffer，出现以下警告。
需要定制`websocketServletWebServerCustomizer`，或设置
`spring.wings.slardar.undertow-ws.enabled=true`即可

在`io.undertow.websockets.jsr.Bootstrap` 68行，`buffers == null` 时
`UT026010: Buffer pool was not set on WebSocketDeploymentInfo, the default pool will be used`
默认 DefaultByteBufferPool(directBuffers, 1024, 100, 12);

