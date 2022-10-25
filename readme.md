# WingsBoot 纹丝不忒

> WingsBoot(代号神翼)=BKB+飞鞋+SpringBoot，若你也喜欢过Dota，你懂的。  
> 我们主张防御式编程，May The `false` Be With You !

* [![SpringBoot-2.6](https://img.shields.io/badge/springboot-2.6-green?logo=springboot)](https://spring.io/projects/spring-boot) 框架哲学和约定下的无入侵性增强 🌱 [Apache2]
* [![Java-11](https://img.shields.io/badge/java-11-gold)](https://adoptium.net/temurin/releases/?version=11) 主要业务语言，OpenJDK长期运行 ☕️ [GPLv2+CE]
* [![Kotlin-1.6](https://img.shields.io/badge/kotlin-1.6-gold)](https://kotlinlang.org/docs/reference/) 辅助语音，做更好的Java [Apache2]
* [![Jooq-3.14](https://img.shields.io/badge/jooq-3.14-cyan)](https://www.jooq.org/download/)  主要的强类型SqlMapping 🏅 [Apache2]
* [![Mysql-8](https://img.shields.io/badge/mysql-8.0-blue)](https://dev.mysql.com/downloads/mysql/) 主要的业务数据库，支持5.7，推荐8体系 💡 [GPLv2]
* [![H2Database-2.1](https://img.shields.io/badge/h2db-2.1-blue)](https://h2database.com/html/main.html) 单机数据库，以离线及断线业务 [MPL2]或[EPL1]
* [![Hazelcast-4.2](https://img.shields.io/badge/hazelcast-4.2-violet)](https://hazelcast.org/imdg/) IMDG，分布式缓存，消息，流等 [Apache2]
* [![ServiceComb-2.8](https://img.shields.io/badge/servicecomb-2.8-violet)](https://servicecomb.apache.org) 更工程化和紧凑的Cloud方案 [Apache2]

[Apache2]: https://www.apache.org/licenses/LICENSE-2.0
[GPLv2+CE]: https://openjdk.org/legal/gplv2+ce.html
[GPLv2]: http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
[MPL2]: https://www.mozilla.org/MPL/2.0
[EPL1]: https://opensource.org/licenses/eclipse-1.0.php

## 1.关联文档

* Official Doc - <https://wings.fessional.pro>
* NotBad Review - <https://java-code-review.moilioncircle.com>
* Doc GitHub - <https://github.com/fessionalpro/wings-doc>
* [Gitee](https://gitee.com/trydofor) is the mirror site 

## 2.常用命令

```bash
# ① 国内镜像，成功后进入项目目录
git clone --depth 1 https://github.com/\
trydofor/pro.fessional.wings.git
# ② 安装依赖，可跳过，支持java8编译
# sdk use java 8.0.332-zulu
git submodule update --remote --init
(cd observe/meepo && mvn package install)
(cd observe/mirana && mvn package install)
# ③ 安装wings，java-11
sdk use java 11.0.2-open
mvn package install
```

## 3.用爱发电

所有`SNAPSHOT`都是开发版，以`rollup`方式迭代，遵循`gitflow`约定。
`feature`以`dota2`英雄命名，约1推送/周，约1迭代/月，保留最近2分支。

代码及文档管理，以github作主站，以gitee作镜像，基本同步推送。

* issues - 任务类，有bug和feature两个模板，可并入discuss
* discussions - 文档类，交流技术话题，不得讨论政治，宗教。
* PR及Review - 代码手谈，PR会经过调整后进入gitflow流程

项目会尽可能展示贡献者信息和足迹，如@author注释，代码注释，提交信息等。

## 4.免责声明

WingsBoot及其submodule项目，均以Apache2授权。但本人，

* 不对滥用技术或手册造成的任何损失负责。
* 没有义务提供咨询，答疑，开发等服务。
* 可付费咨询，3K CNY/H
