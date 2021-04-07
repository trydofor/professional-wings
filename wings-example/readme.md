# 9.演示例子(example)

运行`demo-devops/wings-init-project.sh`生成一个样板工程。

## 9.1.前置条件

 * 了解 `maven`，缺什么，补什么。
 * 了解 `spring*`，`看官方文档，不要百度` x 3！
 * 了解 `mysql*`数据库，mysql

## 9.2.自建环境

``` bash
# 设置变量
USER=trydofor
PASS=moilioncircle

# 创建一个mysql数据库
docker run -d \
--name wings-mysql \
-e MYSQL_DATABASE=wings-example \
-e MYSQL_USER=${USER} \
-e MYSQL_ROOT_PASSWORD=${PASS} \
-e MYSQL_PASSWORD=${PASS} \
-p 3306:3306 \
mysql:5.7
```

## 9.3.压力测试

压力测试，必须`ulimit -n`在10k以上，同一内网以忽略带宽限制。

### maven 打包和启动

``` bash
mvn -U clean package
demo-devops/wings-starter.sh start
# Ctrl-C停止日志输出
demo-devops/wings-starter.sh stop
```

### jmeter 模拟10K用户

无界面启动Jmeter，如果jmeter不在PATH中，则需要自行制定全路径
如 `~/Applications-cli/apache-jmeter-5.4.1/bin/jmeter`

```bash
ulimit -n 20000

JVM_ARGS="-Xmx4G -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:G1ReservePercent=20"

rm -rf demo-devops/target/load-test/ &&\
mkdir -p demo-devops/target/load-test/report

jmeter -n \
-t demo-devops/src/test/jmeter/load-test.jmx \
-l demo-devops/target/load-test/load-test.jtl \
-j demo-devops/target/load-test/load-test.log \
-e -o demo-devops/target/load-test/report
```
