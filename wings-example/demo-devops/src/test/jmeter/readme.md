# jmeter 5.1.1

压力测试，必须设置 `ulimit -n`，10k以上。

打包和启动服务器
``` bash
# PWD=pro.fessional.wings/
mvn clean package
./wings-starter.sh start
# 可以 Ctrl-C 
```

无界面启动Jmeter
```bash
JVM_ARGS="-Xmx4G -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:G1ReservePercent=20"

rm -rf wings-example/demo-devops/target/load-test/ &&\
mkdir -p wings-example/demo-devops/target/load-test/report &&\
jmeter -n \
-t wings-example/demo-devops/src/test/jmeter/load-test.jmx \
-l wings-example/demo-devops/target/load-test/load-test.jtl \
-j wings-example/demo-devops/target/load-test/load-test.log \
-e -o wings-example/demo-devops/target/load-test/report
```
