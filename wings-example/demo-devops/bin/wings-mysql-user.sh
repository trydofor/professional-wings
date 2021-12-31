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

#####
execute=false
database=""
command="$1"
config="$3"

if [[ "$command" == "" || "$command" == "help" ]]; then
  # https://dev.mysql.com/doc/refman/5.7/en/account-management-statements.html
  echo -e '\033[37;42;1mNOTE: users env file\033[m'
  echo 'execute=true # 非true时，仅显示sql而不执行'
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

if [[ "$database" == "" ]]; then
  echo -e "\033[37;41;1mERROR: need database in users config \033[0m"
  exit
fi

#
len=24
if [[ "$pass_raw" == "" ]]; then
  echo "set random password to raw"
  pass_raw=$(head /dev/urandom | LC_ALL=C tr -dc A-Za-z0-9 | head -c $len)
fi
if [[ "$pass_app" == "" ]]; then
  echo "set random password to app"
  pass_app=$(head /dev/urandom | LC_ALL=C tr -dc A-Za-z0-9 | head -c $len)
fi
if [[ "$pass_dev" == "" ]]; then
  echo "set random password to dev"
  pass_dev=$(head /dev/urandom | LC_ALL=C tr -dc A-Za-z0-9 | head -c $len)
fi
if [[ "$pass_dba" == "" ]]; then
  echo "set random password to dba"
  pass_dba=$(head /dev/urandom | LC_ALL=C tr -dc A-Za-z0-9 | head -c $len)
fi

#
if [[ "$host_raw" == "" ]]; then
  host_raw=%
fi
if [[ "$host_app" == "" ]]; then
  host_app=%
fi
if [[ "$host_dev" == "" ]]; then
  host_dev=%
fi
if [[ "$host_dba" == "" ]]; then
  host_dba=%
fi

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
cat << EOF
$database.raw  $pass_raw
$database.app  $pass_app
$database.dev  $pass_dev
$database.dba  $pass_dba
EOF

echo -e '\033[37;42;1mNOTE: sql script to execute\033[m'

if [[ "$command" == "create" ]]; then
db_main=${database//_/\\_}
$exec_cmd << EOF
-- create
CREATE USER '$database.raw'@'$host_raw' IDENTIFIED BY '$pass_raw';
CREATE USER '$database.app'@'$host_app' IDENTIFIED BY '$pass_app';
CREATE USER '$database.dev'@'$host_dev' IDENTIFIED BY '$pass_dev';
CREATE USER '$database.dba'@'$host_dba' IDENTIFIED BY '$pass_dba';
-- grant
GRANT SELECT, CREATE TEMPORARY TABLES ON \`$db_main%\`.* TO '$database.raw'@'$host_raw';
GRANT SELECT, CREATE TEMPORARY TABLES, INSERT, UPDATE, DELETE, EXECUTE ON \`$db_main%\`.* TO '$database.app'@'$host_app';
GRANT ALL ON \`$db_main%\`.* TO '$database.dev'@'$host_dev';
REVOKE DROP ON \`$db_main%\`.* FROM '$database.dev'@'$host_dev';
GRANT ALL ON \`$db_main%\`.* TO '$database.dba'@'$host_dba';
EOF
fi

if [[ "$command" == "passwd" ]]; then
$exec_cmd << EOF
-- change passwd
ALTER USER '$database.raw'@'$host_raw' IDENTIFIED BY '$pass_raw';
ALTER USER '$database.app'@'$host_app' IDENTIFIED BY '$pass_app';
ALTER USER '$database.dev'@'$host_dev' IDENTIFIED BY '$pass_dev';
ALTER USER '$database.dba'@'$host_dba' IDENTIFIED BY '$pass_dba';
EOF
fi


