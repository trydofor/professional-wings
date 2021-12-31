#!/bin/bash
THIS_VERSION=2021-12-21

cat << EOF
#################################################
# Version $THIS_VERSION # test on Mac and Lin
# 通过 mysqldump 生成 'db-ts' 开头的以下文件，
- {db-ts}-main.sql 主表
- {db-ts}-logs.sql log表
- {db-ts}.log db中所有的表及dump文件
- {db-ts}.txt scp及restore手册

# Usage $0 database [config] [nodata]
- database - 需要dump的database，必填
- config - 存在时，使用'--defaults-extra-file'
- nodata - 非空时，增加'--no-data'参数
#################################################
EOF

database="$1"
config="$2"
nodata="$3"

if [[ "$database" == "" ]]; then
  echo -e "\033[0;31mWARN: need param-1=database to dump\033[m"
  exit
fi

# https://dev.mysql.com/doc/refman/5.7/en/option-files.html
my_conf=""
if [[ -f "$config" ]]; then
  echo -e "\033[0;33mNOTE: current config file \033[m"
  cat "$config"
  my_conf="--defaults-extra-file=$config"
else
  echo -e "\033[0;31mNOTE: use mysql default(my.cnf), something like\033[m"
  echo '[client]'
  echo 'host=127.0.0.1'
  echo 'port=3306'
  echo 'user=trydofor'
  echo 'password=xxxxx'
fi
echo

# https://dev.mysql.com/doc/refman/5.7/en/mysqldump.html#mysqldump-performance-options
dump_opt=""
if [[ "$nodata" != "" ]]; then
  dump_opt='--no-data'
fi

###
dump_head="${database}-$(date '+%Y%m%d%H%M%S')"
dump_main_file="$dump_head-main.sql"
dump_logs_file="$dump_head-logs.sql"
dump_tbls_file="$dump_head.log"
dump_help_file="$dump_head.txt"
dump_gzip_file="$dump_head.gz"

unalias mysql >/dev/null 2>&1
unalias mysqldump >/dev/null 2>&1

if mysql "$my_conf" -D "$database" -N -e "show tables" > "$dump_tbls_file"; then
  echo "successfully show tables"
else
  echo -e "\033[37;41;1mERROR: failed to show tables of $database \033[0m"
  rm -rf "$dump_tbls_file"
  exit
fi

logs_cnt=$(grep -cF '$' "$dump_tbls_file")
if [[ $logs_cnt == 0 ]]; then
  echo "no logs tables to dump"
  echo "-- no logs tables to dump" > "$dump_logs_file"
else
  echo -e "\033[0;33mNOTE: dump logs tables without data, tables=\033[m"
  grep -F '$' "$dump_tbls_file"

  # shellcheck disable=SC2046
  mysqldump "$my_conf" \
  --no-data \
  --set-gtid-purged=OFF \
  --single-transaction \
  --column-statistics=0 \
  --databases "$database" \
  --tables $(grep -F '$' "$dump_tbls_file") > "$dump_logs_file"
fi

main_cnt=$(grep -cvF '$' "$dump_tbls_file")
if [[ $main_cnt == 0 ]]; then
  echo "no main tables to dump"
  echo "-- no main tables to dump" > "$dump_main_file"
else
  echo -e "\033[0;33mNOTE: dump main tables with data, count=\033[m"
  # shellcheck disable=SC2046
  mysqldump "$my_conf" \
  --set-gtid-purged=OFF \
  --single-transaction \
  --column-statistics=0 \
  --opt $dump_opt \
  --quick \
  --max-allowed-packet=64M \
  --net-buffer-length=64k \
  --databases "$database" \
  --tables $(grep -vF '$' "$dump_tbls_file") > "$dump_main_file"
fi

echo -e "\033[0;33mNOTE: dump file $dump_head\033[m"
echo >> "$dump_tbls_file"
# shellcheck disable=SC2010
ls -lsh |grep "$dump_head" | tee -a "$dump_tbls_file"

echo -e "\033[0;33mNOTE: tips for zip, scp, restore \033[m"
tee -a "$dump_help_file" << EOF
gzip -c $dump_logs_file $dump_main_file> $dump_gzip_file
scp $dump_gzip_file trydofor@moilioncircle:/data/mysql-dump/

unalias mysql
gzip -dc $dump_gzip_file | pv -Ipert | mysql $my_conf \\
--init-command="CREATE DATABASE IF NOT EXISTS $dump_head; use $dump_head;"

nohup gzip -dc $dump_gzip_file | mysql $my_conf \\
--init-command="CREATE DATABASE IF NOT EXISTS $dump_head; use $dump_head;" &
EOF
