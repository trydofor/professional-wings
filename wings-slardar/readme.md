# 3.鱼人守卫(slardar)

为Servlet体系下的SpringMvc的提供权限等控制。


## 3.1.Controller约定

要在方法上写全路径，不要相对于controller，这样搜索可以从URL直接匹配。
controller所在package的名字，应该和url的目录保持相同的结构。

## 3.2.过载保护Filter

根据每个请求的响应时间，计算容许延时`response-latency`内的最大连接数。
当该请求的连接数超过其最大连接数时，执行`fallback`返回值。

 * 根据`logger-interval`控制日志间隔。
 * 根据`response-infofreq`控制info内容。
 * 根据`response-overrate`控制最大值的范围。

## 3.3.Security预定

引入spring security包时自动生效，没有魔法，都在官方文档，读3遍。

[6.10 Multiple HttpSecurity](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#jc-authentication)
[8. Architecture and Implementation](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#overall-architecture)
[30. Security](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-security.html)
