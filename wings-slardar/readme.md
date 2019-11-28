# 3.鱼人守卫(slardar)

为Servlet体系下的SpringMvc和WebSocket的提供OAuth2的鉴/授权等控制。
当有多个`WebSecurityConfigurerAdapter`时，需要注意`Order`的顺序。

## 3.1.OAuth2xFilter扩展

OAuth2比用户密码的登录方式更有利于扩展，十分成熟，被广泛使用和支持。
Slardar通过Filter增加`grant_type=password`的别名穿透机制，使

 * 使用`password`模式，不暴露`client_secret`
 * 使获得UserDetail时，可以通过Context获得request参数。

这样在不污染Spring和OAuth2的情况下，可使`/oauth/token` 支持

 * 短信验证码登录（POST方式）
 * 用户名密码登录（POST方式）
 * API授权（POST+JSON+验签）

方法级的权限控制`@EnableGlobalMethodSecurity(securedEnabled = true)`，
尽量使用`@Secured("IS_AUTHENTICATED_ANONYMOUSLY")`，这样会方便查找，
尽量不要使用表达式`@PreAuthorize("hasAuthority('ROLE_TELLER')")`。

 * ClientDetailsService，默认从配置文件中加载(只能build)
 * UserDetailsService，自定义，全局注入即可。
 * WingsTokenStore，集合memory和Redis，在auth和res上缓存UserDetail。
 * WingsTokenEnhance，支持wing格式的token和第三方token。
 * UserDetail和TypeIdI18nUserDetail自定义，内部保存需要的状态。

实际项目中，每个工程会独立配置Security和OAuth的各个服务器。
因此，提供了`WingsOAuth2xConfiguration.Helper`，注入后，协助配置。

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

## 3.6.Controller约定

MVC中的RequestMapping约定如下

 * 在方法上写全路径`@RequestMapping("/a/b/c.html")`
 * 在controller上写版本号`@RequestMapping("/v1")`
 * 不要相写相对路径，这样才可以通过URL直接搜索匹配。


## 3.9.参考资料

[OAuth 2 Developers Guide](https://projects.spring.io/spring-security-oauth/docs/oauth2.html)
[OAuth2 boot](https://docs.spring.io/spring-security-oauth2-boot/docs/current/reference/htmlsingle/)
[Spring Security](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/)
