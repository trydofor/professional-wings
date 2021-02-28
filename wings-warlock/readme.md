# 4.术士大叔(warlock)

无视魔免的地狱火加上致命链接，可以瞬间改变局势。

![slardar](./warlock_full.png)

基于wings脚手架，包装了一些业务组件，复用或复制，可以快速实现业务功能。

 * 身份认证：OIDC/SSO，Form，RememberMe，API签名，第三方，手机短信
 * 令牌传递：session，token
 * 功能权限：role继承和扩展，身份马甲，临时增减，超级用户
 * 数据隔离：管辖隔离，职能继承，助理扩展，临时授权。

## 4.1.多种登录验证

### 4.1.1.集成Github

在github上设置，需要`App ID`，`Client ID`和`Client secret`，注意不用外泄。
设置入口如下 Settings | Developer settings | GitHub Apps

 * Homepage URL - http://127.0.0.1:8084
 * Callback URL - http://127.0.0.1:8084/auth/github/login.json


