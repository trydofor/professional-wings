# 4.演示例子(example)

一个需要有点动手能力的简单的例子。

 * 直接在IDE中run `WingsExampleApplication`
 * `mvn install` + `mvn spring-boot:run`
 * `mvn package` + `wings-starter.sh`

## 4.1.前置条件

 * 了解 `maven`，缺什么，补什么。
 * 了解 `spring*`，`看官方文档，不要百度` x 3！
 * 了解 `mysql*`数据库，mysql,mariadb,percona
 * 了解 `redis`缓存

## 4.2.演示内容
 
 * 语言和时区切换
 * Json数据格式确认
 * 数据库版本管理，升级，降级
 * 对数据更新和删除的追踪。
 * 分表分库，水平分2张,读写分离
 * 用户权限
 
**说明**，不建议在Controller中执行数据库操作，是否危险。
建议在Dev环境下，执行main或Test方法手动操作。

具体例子参考源码：
`wings-faceless/src/test/kotlin/`下的， 
`pro/fessional/wings/faceless/sample/*`

## 4.3.自建环境

``` bash
# 设置变量
USER=trydofor
PASS=moilioncircle

# 启动redis，一定要**密码**
docker run -d \
--name wings-redis \
-p 6379:6379 \
redis --requirepass ${PASS}

# 创建一个mysql数据库
docker run -d \
--name wings-mysql \
-e MYSQL_DATABASE=wings_0 \
-e MYSQL_USER=${USER} \
-e MYSQL_ROOT_PASSWORD=${PASS} \
-e MYSQL_PASSWORD=${PASS} \
-p 3306:3306 \
mysql:5.7

# 创建第二个数据库
docker exec wings-mysql mysql -uroot -p${PASS} -e "
CREATE DATABASE IF NOT EXISTS wings_1;
GRANT ALL PRIVILEGES ON wings_1.* TO ${USER}@'%';
FLUSH PRIVILEGES;"

```

## 4.4.配置运行

 * 在`localhost:3306`配置数据库2个，`wings_0`和`wings_1`
 * 数据库用户名`trydofor`，密码`moilioncircle`，或者自己改配置
 * redis，密码`moilioncircle`，或者自己改配置
 * 运行example，访问 http://127.0.0.1:8080/
 * 配置一个本地redis做session和缓存，用docker的就好。
 
使用`maven`管理依赖，可以有下面3中方式。
方式一，以`parent`形式，当前工程形式。
 ``` xml
 <parent>
    <groupId>pro.fessional</groupId>
    <artifactId>wings-home</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</parent>
 ```
 
方式二，有其他`parent`以`import`形式，然后用啥填啥。
``` xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>pro.fessional</groupId>
            <artifactId>wings-home</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <scope>import</scope>
            <type>pom</type>
        </dependency>
    </dependencies>
</dependencyManagement>
 ```

方式三，直接依赖
``` xml
<dependencies>
    <dependency>
        <groupId>pro.fessional</groupId>
        <artifactId>wings-faceless</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

部署启动
```
cd wings-example
mvn clean package
cd ..
./wings-starter.sh start
# Ctrl-C停止日志输出
./wings-starter.sh stop
```