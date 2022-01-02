#!/bin/bash
THIS_VERSION=2021-12-21

cat << EOF
#################################################
# Version $THIS_VERSION # test on Mac and Lin
# 创建database以及和访问的用户
- {database}.raw SELECT, TEMPORARY TABLE
- {database}.app {raw} + INSERT, UPDATE, DELETE, EXECUTE
- {database}.dev ALL - Drop
- {database}.dba ALL

# Usage $0 (create|passwd|help) users [config]
- create/passwd - 创建授权/改密码
- users - 环境脚本(bash语法)，格式参考help
- config - 存在时，使用'--defaults-extra-file'
#################################################
EOF

function passwd24() {
  head /dev/urandom | LC_ALL=C tr -dc A-Za-z0-9 | head -c 24
}

#####
execute=false
command="$1"
config="$3"

if [[ "$command" == "" || "$command" == "help" ]]; then
  # https://dev.mysql.com/doc/refman/5.7/en/account-management-statements.html
  echo -e '\033[37;42;1mNOTE: users env file\033[m'
  echo 'execute=true # 非true时，仅显示sql而不执行'
  echo '# passwd 空为忽略'
  echo 'database=数据库'
  echo 'pass_raw=密码'
  echo 'pass_app=密码'
  echo 'pass_dev=密码'
  echo 'pass_dba=密码'
  echo 'host_raw=%'
  echo 'host_app=10.11.%'
  echo 'host_dev=%'
  echo 'host_dba=%'
  echo -e '\033[37;42;1mNOTE: user manage\033[m'
  echo "RENAME USER 'trydofor'@'%' TO 'trydofor'@'127.0.%';"
  echo "DROP USER IF EXISTS 'trydofor'@'%';"
  exit
fi

if [[ -f "$2" ]]; then
  echo "load users config from $2"
  # shellcheck disable=SC1090
  source "$2"
fi

declare database
if [[ "$database" == "" ]]; then
  echo -e "\033[37;41;1mERROR: need database in users config \033[0m"
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

exec_cmd="cat"
if [[ "$execute" == "true" ]]; then
  unalias mysql >/dev/null 2>&1
  exec_cmd="mysql -vvv -f -D $database"
  if [[ -f "$config" ]]; then
    echo -e "\033[0;33mNOTE: current config file \033[m"
    cat "$config"
    exec_cmd="mysql --defaults-extra-file=$config -vvv -f -D $database"
  fi
fi

echo -e '\033[37;42;1mNOTE: users and passwd\033[m'
grep -v '^#' << EOF
${user_raw}$database.raw  $pass_raw
${user_app}$database.app  $pass_app
${user_dev}$database.dev  $pass_dev
${user_dba}$database.dba  $pass_dba
EOF

echo -e '\033[37;42;1mNOTE: sql script to execute\033[m'

if [[ "$command" == "create" ]]; then
db_main=${database//_/\\_}
grep -v '^#' << EOF | tee /dev/tty | $exec_cmd
-- create
${user_raw}CREATE USER '$database.raw'@'$host_raw' IDENTIFIED BY '$pass_raw';
${user_app}CREATE USER '$database.app'@'$host_app' IDENTIFIED BY '$pass_app';
${user_dev}CREATE USER '$database.dev'@'$host_dev' IDENTIFIED BY '$pass_dev';
${user_dba}CREATE USER '$database.dba'@'$host_dba' IDENTIFIED BY '$pass_dba';
-- grant
${user_raw}GRANT SELECT, CREATE TEMPORARY TABLES ON \`$db_main%\`.* TO '$database.raw'@'$host_raw';
${user_app}GRANT SELECT, CREATE TEMPORARY TABLES, INSERT, UPDATE, DELETE, EXECUTE ON \`$db_main%\`.* TO '$database.app'@'$host_app';
${user_dev}GRANT ALL ON \`$db_main%\`.* TO '$database.dev'@'$host_dev';
${user_dev}REVOKE DROP ON \`$db_main%\`.* FROM '$database.dev'@'$host_dev';
${user_dba}GRANT ALL ON \`$db_main%\`.* TO '$database.dba'@'$host_dba';
EOF
fi

if [[ "$command" == "passwd" ]]; then
grep -v '^#' << EOF | tee /dev/tty | $exec_cmd
-- change passwd
${user_raw}ALTER USER '$database.raw'@'$host_raw' IDENTIFIED BY '$pass_raw';
${user_app}ALTER USER '$database.app'@'$host_app' IDENTIFIED BY '$pass_app';
${user_dev}ALTER USER '$database.dev'@'$host_dev' IDENTIFIED BY '$pass_dev';
${user_dba}ALTER USER '$database.dba'@'$host_dba' IDENTIFIED BY '$pass_dba';
EOF
fi


