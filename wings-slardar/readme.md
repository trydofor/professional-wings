# 3.鱼人守卫(slardar)

为Servlet体系下的SpringMvc的提供权限等控制。


## 3.1.Controller约定

要在方法上写全路径，不要相对于controller，这样搜索可以从URL直接匹配。
controller所在package的名字，应该和url的目录保持相同的结构。

## 3.2.过载保护OverloadFilter

 * 自动或手动设置`最大同时进行请求数`。超过时，执行`fallback`。
 * 不影响性能的情况下，记录慢响应URI和运行状态。
 * 优雅停止服务器，阻断所有新请求。
 * 相同IP请求过于频繁，执行fallback。
 
 `最大同时进行请求数`，指已经由Controller处理，但未完成的请求。

## 3.3.Security预定

引入spring security包时自动生效，没有魔法，都在官方文档，读3遍。
默认 `spring.wings.security.blank.enabled=false` 不生效。

在有多个`WebSecurityConfigurerAdapter`配置时，需要注意`Order`的顺序。 

[6.10 Multiple HttpSecurity](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#jc-authentication)
[8. Architecture and Implementation](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#overall-architecture)
[30. Security](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-security.html)
