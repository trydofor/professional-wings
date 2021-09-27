# 拼写错误，如鲠在喉

## 执行sql脚本

在未分表且无影子表时，可手动执行升级sql。 否则建议使用flywave执行，避免遗漏。

* rename win_user_anthn
* update sys_light_sequence
* update shard table_#
* update trace table$log
* update triggers

## 替换java文件

全工程的java文件，替换以下字符串

``` bash
sed -i 's/user_anthn/user_authn/g'
sed -i 's/UserAnthn/UserAuthn/g'
```
