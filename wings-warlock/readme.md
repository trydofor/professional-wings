# 4.术士大叔(warlock)

无视魔免的地狱火加上致命链接，可以瞬间改变局势。

![slardar](./warlock_full.png)

基于wings脚手架，包装了一些业务组件，复用或复制，可以快速实现业务功能。

* 身份认证：OIDC/SSO，Form，RememberMe，API签名，第三方，手机短信
* 令牌传递：session，token
* 功能权限：role继承和扩展，身份马甲，临时增减，超级用户
* 数据隔离：管辖隔离，职能继承，助理扩展，临时授权。

## 4.1.登录验证

### 4.1.1.集成Github

在github上设置，需要`App ID`，`Client ID`和`Client secret`，注意不用外泄。 设置入口如下 Settings |
Developer settings | GitHub Apps

* Homepage URL - http://127.0.0.1:8084
* Callback URL - http://127.0.0.1:8084/auth/github/login.json

## 4.2.功能权限

功能权限，有权限(Perm)和角色(Role)构成。都在db中定义，通过模板自动生成java类

### 4.2.1 权限 Perm

Perm由scope和action构成，都采用`英句号`分隔`全小写`命名法，参考java变量命名。

格式为 scope + ('.' + scope )* + '.' + action，多个级联scope加一个action

* scope是一个名词，支持所属关系，使用`.`分隔，`system.menu`属于`system`
* 第一个scope不可以是ROLE前缀（如spring中默认是ROLE_）
* action是一个动词，支持scope包含，如`system.read`包含`system.menu.read`
* `*`表示包含所有动作，仅配置所属关系使用，不在具体方法上使用。

Perm主要用在方法级的鉴权上，即在方法上增加的注解，如`@Secured`，`@Pre*`。

```java
// 推荐
@Secured(PermConstant.System.User.read)
// 不推荐
@PreAuthorize("hasAnyAuthority(T(pro.fessional.wings.warlock.security.autogen.PermConstant$System$User).read)")
```

### 4.2.2.角色 Role

Role不支持继承，`全大写`无分隔命名法（区分权限），参考java变量命名。

* 在自动生成的java类中，采用和spring相同的`ROLE_`前缀。
* Role是扁平的，但可配置继承，如LEADER包括MEMBER
* 无分隔，指以`_`链接的词，当做同一个词看待。
* db中不建议使用前缀，加载是自动增加（如spring中默认是ROLE_）

Role主要用在filter级的配置上，如在配置url权限时。当然也可用在方法级。
在配置文件中使用时，需要带上spring自动添加的前缀，建议使用前缀，以区分perm。

### 4.2.3.远行机制

Warlock在用户通过身边鉴别（renew）后，会分别加载和用户绑定的Perm和Role，
并扁平化其各自的所属和继承关系，全部加载到SecurityContext中。

当Perm和Role(含前缀)的字符串以`-`开头时，标识排除此权限，最高优先级。

## 4.3.数据权限

数据权限，包括了用户，部门，公司，三个层级的可见性。

* 用户(User)，以user_id为主，同时包括子账号。
* 部门(Dept)，以dept_id为主，包括了部门间所属关系
* 公司(Corp)，以corp_id为主，通常和domain有关

## 4.4.功能定制

和wings所有工程一样，所有远行功能都可以通过 spring-wings-enabled-77.properties 关闭。
不过功能之间的依赖，需要使用者自行关照，需要阅读代码。后续文档中提到`暴露`，指声明一个Bean

### 4.4.1.定制登录

登录分登录页面`login-page*`和处理接口`*login*`，前者（有`page`），区别如下，

* login-page，展示给用户的登录页面，一般是401时自动重定向。
* login，为提交凭证后的处理或回调接口，由filter执行。

可以通过以下4种方式，不同程度的改变Warlock提供的默认登录页面和返回结果。

* 暴露 ComboWingsAuthPageHandler.Combo，增加处理细节。
* 暴露 WingsAuthPageHandler，替换处理细节。
* 指定 wings.warlock.security.login-page，定向到自定义页面。
* 暴露 AuthenticationSuccessHandler，AuthenticationFailureHandler处理登录事件。
* 暴露 LogoutSuccessHandler 处理登出的事件。

默认实现中，login中会在cookie和header中放置sessionId，logout是清空session。

NonceLoginSuccessHandler配合NonceTokenSessionHelper实现了oauth一次性token换取session的功能。
所以如果需要此功能，需要在自行实现AuthenticationSuccessHandler继承NonceLoginSuccessHandler。

### 4.4.2.定制验证

* 暴露 ComboWingsAuthDetailsSource.Combo，增加details
* 暴露 WingsAuthDetailsSource 替换处理细节
* 暴露 ComboWingsUserDetailsService.Combo，增加加载细节
* 暴露 WingsUserDetailsService，替换用户加载

### 4.4.3.定制授权

除了默认实现的user，role，perm体系外，warlock支持一下用户和权限的细粒度定制

* NonceUserDetailsCombo - 一次性登录。
* MemoryUserDetailsCombo - 按uid，登录名，登录方式，挂载用户和权限。
* NonceTokenSessionHelper - oauth2流程外，通过一次性state换取sessionId。
