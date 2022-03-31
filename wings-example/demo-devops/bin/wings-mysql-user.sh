#!/bin/bash
THIS_VERSION=2022-02-14

cat << EOF
#################################################
# Version $THIS_VERSION # test on Mac and Lin
# 创建database以及和访问的用户
- {user_pre}.raw SELECT, TEMPORARY TABLE
- {user_pre}.app {raw} + INSERT, UPDATE, DELETE, EXECUTE
- {user_pre}.dev ALL - Drop
- {user_pre}.dba ALL + SELECT on mysql/sys

# Usage $0 {create|grant|passwd|help} userenv [option]
- create/grant/passwd - 创建/授权/改密码
- userenv - 环境脚本(bash语法)，wings-mysql-user.env
- option - 存在时，使用'--defaults-extra-file'
# option 详细参考client段
- https://dev.mysql.com/doc/refman/8.0/en/option-files.html
./wings-mysql-user.sh wings-mysql-user.env wings-mysql-client.cnf
#################################################
EOF

function passwd24() {
  head /dev/urandom | LC_ALL=C tr -dc A-Za-z0-9 | head -c 24
}

#####
execute=false
command="$1"
userenv="$2"
option="$3"

if [[ "$command" == "" || "$command" == "help" ]]; then
echo -e '\033[37;42;1mNOTE: users env file\033[m'
# https://dev.mysql.com/doc/refman/8.0/en/account-management-statements.html
cat << 'EOF'
execute=false
# 用户名前缀
user_pre=devall
# 授权db，空格分隔。其中`_`和`%`是通配符，可`\`转义
grant_db='%'
# passwd 空为忽略
pass_raw=$(passwd24)
pass_app=$(passwd24)
pass_dev=$(passwd24)
pass_dba=$(passwd24)
# host 默认，%
host_raw=%
host_app=10.11.%
host_dev=%
host_dba=%
EOF
echo -e '\033[37;42;1mNOTE: user manage\033[m'
cat << 'EOF'
RENAME USER 'trydofor'@'%' TO 'trydofor'@'127.0.%';
DROP USER IF EXISTS 'trydofor'@'%';
EOF
exit
fi

declare more_dba
if [[ -f "$userenv" ]]; then
  echo "load users option from $userenv"
  # shellcheck disable=SC1090
  source "$userenv"
fi

declare user_pre
if [[ "$user_pre" == "" ]]; then
  echo -e "\033[37;41;1mERROR: need user_pre in users option \033[0m"
  exit
fi

declare grant_db
if [[ "$grant_db" == "" ]]; then
  echo -e "\033[37;41;1mERROR: need grant_db in users option \033[0m"
  exit
fi

#
[[ "$pass_raw" == "" ]] && user_raw='#' pass_raw=$(passwd24)
[[ "$pass_app" == "" ]] && user_app='#' pass_app=$(passwd24)
[[ "$pass_dev" == "" ]] && user_dev='#' pass_dev=$(passwd24)
[[ "$pass_dba" == "" ]] && user_dba='#' pass_dba=$(passwd24)

#
[[ "$host_raw" == "" ]] && host_raw=%
[[ "$host_app" == "" ]] && host_app=%
[[ "$host_dev" == "" ]] && host_dev=%
[[ "$host_dba" == "" ]] && host_dba=%

exec_cmd=":"
if [[ "$execute" == "true" ]]; then
  unalias mysql >/dev/null 2>&1
  exec_cmd="mysql -vvv -f "
  if [[ -f "$option" ]]; then
    echo -e "\033[0;33mNOTE: current option file \033[m"
    cat "$option"
    exec_cmd="mysql --defaults-extra-file=$option -vvv -f "
  fi
fi

echo -e '\033[37;42;1mNOTE: users and passwd\033[m'
grep -v '^#' << EOF
${user_raw}$user_pre.raw  $pass_raw
${user_app}$user_pre.app  $pass_app
${user_dev}$user_pre.dev  $pass_dev
${user_dba}$user_pre.dba  $pass_dba
EOF

echo -e '\033[37;42;1mNOTE: sql script to execute\033[m'

if [[ "$command" == "create" ]]; then
grep -v '^#' << EOF | tee /dev/tty | $exec_cmd
-- create
${user_raw}CREATE USER '$user_pre.raw'@'$host_raw' IDENTIFIED BY '$pass_raw';
${user_app}CREATE USER '$user_pre.app'@'$host_app' IDENTIFIED BY '$pass_app';
${user_dev}CREATE USER '$user_pre.dev'@'$host_dev' IDENTIFIED BY '$pass_dev';
${user_dba}CREATE USER '$user_pre.dba'@'$host_dba' IDENTIFIED BY '$pass_dba';
EOF
fi

if [[ "$command" == "grant" ]]; then
for db_main in $grant_db; do
grep -v '^#' << EOF | tee /dev/tty | $exec_cmd
-- grant
${user_raw}GRANT SELECT, CREATE TEMPORARY TABLES ON \`$db_main\`.* TO '$user_pre.raw'@'$host_raw';
${user_app}GRANT SELECT, CREATE TEMPORARY TABLES, INSERT, UPDATE, DELETE, EXECUTE ON \`$db_main\`.* TO '$user_pre.app'@'$host_app';
${user_dev}GRANT ALL ON \`$db_main\`.* TO '$user_pre.dev'@'$host_dev';
${user_dev}REVOKE DROP ON \`$db_main\`.* FROM '$user_pre.dev'@'$host_dev';
${user_dba}GRANT ALL ON \`$db_main\`.* TO '$user_pre.dba'@'$host_dba';
${user_dba}GRANT RELOAD,SHOW VIEW,EXECUTE,FILE,PROCESS,REPLICATION CLIENT,REPLICATION SLAVE ON *.* TO '$user_pre.dba'@'$host_dba';
EOF
for mb in $more_dba; do
grep -v '^#' << EOF | tee /dev/tty | $exec_cmd
${user_dba}GRANT SELECT ON \`$mb\`.* TO '$user_pre.dba'@'$host_dba';
EOF
done
done
fi

if [[ "$command" == "passwd" ]]; then
grep -v '^#' << EOF | tee /dev/tty | $exec_cmd
-- change passwd
${user_raw}ALTER USER '$user_pre.raw'@'$host_raw' IDENTIFIED BY '$pass_raw';
${user_app}ALTER USER '$user_pre.app'@'$host_app' IDENTIFIED BY '$pass_app';
${user_dev}ALTER USER '$user_pre.dev'@'$host_dev' IDENTIFIED BY '$pass_dev';
${user_dba}ALTER USER '$user_pre.dba'@'$host_dba' IDENTIFIED BY '$pass_dba';
EOF
fi


