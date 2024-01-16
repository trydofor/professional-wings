# WingsBoot wins bigger

![Maven Central](https://img.shields.io/maven-central/v/pro.fessional/wings?color=00DD00)
![Sonatype Snapshots](https://img.shields.io/nexus/s/pro.fessional/wings?server=https%3A%2F%2Foss.sonatype.org)
[![Coverage Status](https://coveralls.io/repos/github/trydofor/professional-wings/badge.svg)](https://coveralls.io/github/trydofor/professional-wings)

> WingsBoot=BKB + BootsOfTravel + SpringBoot. if you've liked Dota, you know.   
> We advocate defensive programming, May The `false` Be With You !
>
> English 🇺🇸 | [中文 🇨🇳](readme-zh.md)

* [![SpringBoot-3.2](https://img.shields.io/badge/springboot-3.2-green?logo=springboot)](https://spring.io/projects/spring-boot) Philosophy and Conventions, Non-Intrusion Enhancement 🌱 [Apache2]
* [![Java-21](https://img.shields.io/badge/java-21-gold)](https://adoptium.net/temurin/releases/?version=11) Main business language, OpenJDK long-time running ☕️ [GPLv2+CE]
* [![Kotlin-1.9](https://img.shields.io/badge/kotlin-1.9-gold)](https://kotlinlang.org/docs/reference/) Assisted language, as a better Java [Apache2]
* [![Jooq-3.18](https://img.shields.io/badge/jooq-3.18-cyan)](https://www.jooq.org/download/)  The main type-safe SqlMapping 🏅 [Apache2]
* [![Mysql-8.0](https://img.shields.io/badge/mysql-8.0-blue)](https://dev.mysql.com/downloads/mysql/) Main business database, recommend 8.0.31 💡 [GPLv2]
* [![H2Database-2.2](https://img.shields.io/badge/h2db-2.2-blue)](https://h2database.com/html/main.html) Standalone database for offline and disconnected operations [MPL2] or [EPL1]
* [![Hazelcast-5.3](https://img.shields.io/badge/hazelcast-5.3-violet)](https://docs.hazelcast.com/hazelcast/) Distributed caching, messaging, streaming, etc. [Apache2]
* [![ServiceComb-3.0](https://img.shields.io/badge/servicecomb-3.0-violet)](https://servicecomb.apache.org) more engineering and compact miscroservice solution [Apache2]
* [![ShardingSphere-5.4](https://img.shields.io/badge/shardingsphere-5.4-violet)](https://shardingsphere.apache.org) Database RW splitting, data sharding and elastic scaling [Apache2]

[Apache2]: https://www.apache.org/licenses/LICENSE-2.0
[GPLv2+CE]: https://openjdk.org/legal/gplv2+ce.html
[GPLv2]: http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
[MPL2]: https://www.mozilla.org/MPL/2.0
[EPL1]: https://opensource.org/licenses/eclipse-1.0.php

## 1.Related Documents

* Official Doc - <https://wings.fessional.pro>
* NotBad Code Review - <https://java-code-review.moilioncircle.com>
* Doc GitHub - <https://github.com/fessionalpro/wings-doc>
* Mirror Site - <https://gitee.com/trydofor>

## 2.Useful commands

```bash
## (1) get source code
git clone --depth 1 https://github.com/\
trydofor/pro.fessional.wings.git
## (2) install dependency using java8
# asdf shell java temurin-8.0.382+5
git submodule update --remote --init
(cd observe/meepo && mvn package install)
(cd observe/mirana && mvn package install)
## (3) install wings using java-21
# asdf shell java temurin-21.0.1+12.0.LTS
mvn package install
## (4) report issue
java -jar silencer-*-SNAPSHOT.jar
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

## 4.Disclaimer

WingsBoot and its submodule projects are licensed under [Apache2]. Please note that,

* The projects are voluntary contributions based on existing technologies, resources and team practices,
  without any express or implied warranties or conditions.
* The developers of the projects have made efforts to ensure the quality and functionality of the code,
  but do not guarantee that the projects are completely free of defects or bugs.
* When using the projects, you must make your own evaluation of their suitability and
  assume all risks associated with their use.
* Under no circumstances will the developers of the projects be liable for any loss, damages,
  or other liabilities arising from the use of the projects.
