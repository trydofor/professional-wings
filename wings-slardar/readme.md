# 3.鱼人守卫(slardar)

为Servlet体系下的SpringMvc的提供权限等控制。


## 1.5.Controller约定

要在方法上写全路径，不要相对于controller，这样搜索可以从URL直接匹配。
controller所在package的名字，应该和url的目录保持相同的结构。

## 1.6.Security预定

引入spring security包时自动生效，没有魔法，都在官方文档，读3遍。

[6.10 Multiple HttpSecurity](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#jc-authentication)
[8. Architecture and Implementation](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#overall-architecture)
[30. Security](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-security.html)
