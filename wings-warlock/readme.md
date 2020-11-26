# 4.术士大叔(warlock)

无视魔免的地狱火加上致命链接，可以瞬间改变局势。

![slardar](./warlock_full.png)

基于SpringSecurity的AuthN(Authentication/认证)和AuthZ(Authorization/授权)支持。

 * 身份认证：OIDC/SSO，Form，RememberMe，API签名，第三方，手机短信
 * 令牌传递：session，token
 * 功能权限：role继承和扩展，身份马甲，临时增减，超级用户
 * 数据隔离：管辖隔离，职能继承，助理扩展，临时授权。

## 4.1.权限场景

Auth端进行AuthN和基础的AuthZ，可以实现SSO和RememberMe，
在App和Res端可以通过uid的绑定，进行UserDetail和AuthZ的补充。

在目前的`OIDC`体系中，access-token通常有以下几个格式，

* opaque tokens (default)
* reference tokens
* JWTs (Json Web Tokens)

目前主流的产品和技术风向上，对JWT比较钟爱，可却经常误用，命中其缺点。

* 啰嗦，浪费带宽和计算资源。
* 无法废弃，续签困难。
* 并不安全，非加密


