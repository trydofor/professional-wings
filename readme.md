# WingsBoot wins bigger

> WingsBoot=BKB + BootsOfTravel + SpringBoot. if you've liked Dota, you know.   
> We advocate defensive programming, May The `false` Be With You !
>
> English 🇺🇸 | [中文 🇨🇳](readme-zh.md)

* [![SpringBoot-3.0](https://img.shields.io/badge/springboot-3.0-green?logo=springboot)](https://spring.io/projects/spring-boot) Philosophy and Conventions, Non-Intrusion Enhancement 🌱 [Apache2]
* [![Java-17](https://img.shields.io/badge/java-17-gold)](https://adoptium.net/temurin/releases/?version=11) Main business language, OpenJDK long-time running ☕️ [GPLv2+CE]
* [![Kotlin-1.7](https://img.shields.io/badge/kotlin-1.7-gold)](https://kotlinlang.org/docs/reference/) Assisted language, as a better Java [Apache2]
* [![Jooq-3.17](https://img.shields.io/badge/jooq-3.17-cyan)](https://www.jooq.org/download/)  The main type-safe SqlMapping 🏅 [Apache2]
* [![Mysql-8](https://img.shields.io/badge/mysql-8.0-blue)](https://dev.mysql.com/downloads/mysql/) Main business database, 8 recommended, 5.7 compatible 💡 [GPLv2]
* [![H2Database-2.1](https://img.shields.io/badge/h2db-2.1-blue)](https://h2database.com/html/main.html) Standalone database for offline and disconnected operations [MPL2] or [EPL1]
* [![Hazelcast-5.1](https://img.shields.io/badge/hazelcast-5.1-violet)](https://hazelcast.org/imdg/) IMDG，Distributed caching, messaging, streaming, etc. [Apache2]
* [![ServiceComb-2.8](https://img.shields.io/badge/servicecomb-2.8-violet)](https://servicecomb.apache.org) more engineering and compact miscroservice solution [Apache2]
* [![ShardingSphere-5.2](https://img.shields.io/badge/shardingsphere-5.2-violet)](https://shardingsphere.apache.org) Database RW splitting, data sharding and elastic scaling [Apache2]

[Apache2]: https://www.apache.org/licenses/LICENSE-2.0
[GPLv2+CE]: https://openjdk.org/legal/gplv2+ce.html
[GPLv2]: http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
[MPL2]: https://www.mozilla.org/MPL/2.0
[EPL1]: https://opensource.org/licenses/eclipse-1.0.php

## 1.Related Documents

* Official Doc - <https://wings.fessional.pro/en/>
* NotBad Review - <https://java-code-review.moilioncircle.com>
* Doc GitHub - <https://github.com/fessionalpro/wings-doc>
* [Gitee](https://gitee.com/trydofor) is the mirror site

## 2.Useful commands

```bash
# ① get source code
git clone --depth 1 https://github.com/\
trydofor/pro.fessional.wings.git
# ② install dependency useing java8
# sdk use java 8.0.352-tem
git submodule update --remote --init
(cd observe/meepo && mvn package install)
(cd observe/mirana && mvn package install)
# ③ install wings using java-17
sdk use java 17.0.6-tem
mvn package install
```

## 3.Powered by Love

All `SNAPSHOT`s are in development, `rollup` iteration, `gitflow` conventions.
All `feature` branches are named after the `dota2` hero,
about 1 push/week, 1 iteration/month, keep the last 2 branches.

Codes and docs use `github` as the main site and `gitee` as the mirror,
basically synchronized push.

* issues - task like action, such as bugs and features
* discussions - doc, tech topics, no political, religious discussions.
* PR and Review - PR will be reviewed and merged as the gitflow process

We will keep contributor and footprint as much as possible,
such as @author comments, code comments, commit information, etc.

## 4.Disclaimers

WingsBoot and its submodule project are licensed under [Apache2]. but I am,

* Not responsible for any damage caused by the code or doc.
* No obligation to provide consulting, development, etc.
* Non-free Consulting is available, about 3K CNY/H
