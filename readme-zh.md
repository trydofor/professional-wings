# WingsBoot 纹丝不忒

> WingsBoot(代号神翼)=BKB+飞鞋+SpringBoot，若你也喜欢过Dota，你懂的。  
> 我们主张防御式编程，May The `false` Be With You !
> 
> 中文 🇨🇳 | [English 🇺🇸](readme.md)

* [![SpringBoot-3.0](https://img.shields.io/badge/springboot-3.0-green?logo=springboot)](https://spring.io/projects/spring-boot) 框架哲学和约定下的无入侵性增强 🌱 [Apache2]
* [![Java-17](https://img.shields.io/badge/java-17-gold)](https://adoptium.net/temurin/releases/?version=11) 主要业务语言，OpenJDK长期运行 ☕️ [GPLv2+CE]
* [![Kotlin-1.7](https://img.shields.io/badge/kotlin-1.7-gold)](https://kotlinlang.org/docs/reference/) 辅助语言，做更好的Java [Apache2]
* [![Jooq-3.17](https://img.shields.io/badge/jooq-3.17-cyan)](https://www.jooq.org/download/)  主要的强类型SqlMapping 🏅 [Apache2]
* [![Mysql-8](https://img.shields.io/badge/mysql-8.0-blue)](https://dev.mysql.com/downloads/mysql/) 主要的业务数据库，推荐8，兼容5.7 💡 [GPLv2]
* [![H2Database-2.1](https://img.shields.io/badge/h2db-2.1-blue)](https://h2database.com/html/main.html) 单机数据库，以离线及断线业务 [MPL2]或[EPL1]
* [![Hazelcast-5.1](https://img.shields.io/badge/hazelcast-5.1-violet)](https://docs.hazelcast.com/hazelcast/) 分布式缓存，消息，流等 [Apache2]
* [![ServiceComb-2.8](https://img.shields.io/badge/servicecomb-2.8-violet)](https://servicecomb.apache.org) 更工程化和紧凑的微服务方案 [Apache2]
* [![ShardingSphere-5.3](https://img.shields.io/badge/shardingsphere-5.3-violet)](https://shardingsphere.apache.org) 数据库的分表分片弹性伸缩方案 [Apache2]

[Apache2]: https://www.apache.org/licenses/LICENSE-2.0
[GPLv2+CE]: https://openjdk.org/legal/gplv2+ce.html
[GPLv2]: http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
[MPL2]: https://www.mozilla.org/MPL/2.0
[EPL1]: https://opensource.org/licenses/eclipse-1.0.php

## 1.关联文档

* 官方手册 - <https://wings.fessional.pro/zh>
* 代码审查 - <https://java-code-review.moilioncircle.com>
* 文档项目 - <https://github.com/fessionalpro/wings-doc>
* 镜像站点 - <https://gitee.com/trydofor>

## 2.常用命令

```bash
## ① 获取源码，成功后进入项目目录
git clone --depth 1 https://github.com/\
trydofor/pro.fessional.wings.git
## ② 安装依赖，可跳过，支持java8编译
sdk use java 8.0.352-tem
git submodule update --remote --init
(cd observe/meepo && mvn package install)
(cd observe/mirana && mvn package install)
## ③ 安装wings，java-17
sdk use java 17.0.6-tem
mvn package install
## ④ 报告Issue
java -jar silencer-*-SNAPSHOT.jar
```

## 3.用爱发电

所有`SNAPSHOT`都是开发版，以`rollup`方式迭代，遵循`gitflow`约定。
`feature`以`dota2`英雄命名，约1推送/周，约1迭代/月，保留最近2分支。

代码及文档管理，以github作主站，以gitee作镜像，基本同步推送。

* issues - 任务类，如bug和feature等，有些会转入discuss
* discussions - 文档类，交流技术话题，不得讨论政治，宗教。
* PR及Review - 代码手谈，PR会经过调整后进入gitflow流程

项目会尽可能展示贡献者信息和足迹，如@author注释，代码注释，提交信息等。

## 4.免责声明

WingsBoot及其submodule项目，均以[Apache2]授权。请注意，

* 项目是基于现有技术，资源和团队实践的自愿贡献，没有任何明示或暗示的保证或条件。
* 项目的开发者已经尽力确保代码的质量和功能性，但不保证完全没有缺陷或错误。
* 在使用项目时，你应该自行评估其适用性，并承担使用该项目的所有风险。
* 在任何情况下，项目的开发者都不对因使用该项目而导致的任何损失、损害或其他责任承担责任。
