#!/bin/bash

config="$1"
schema="$2"
nodata="$3"

echo -e "\033[0;33mNOTE: version=2020-07-03, usage: ./wings-mysql-dump.sh mysql-client.cnf your_database [nodata]\033[m"

if [[ -f "$config" ]]; then
  echo -e "\033[0;33mNOTE: current config file \033[m"
  cat "$config"
  echo
else
  echo -e "\033[0;31mWARN: need param-1=mysqldump.cnf file, as follow\033[m"
  cat <<"EOF"
[client]
host=localhost
port=3306
user=trydofor
password=xxxxx
EOF
  exit
fi

if [[ "$schema" == "" ]]; then
  echo -e "\033[0;31mWARN: need param-2=database to dump\033[m"
  exit
fi

dump_head="${schema}_$(date '+%Y%m%d%H%M%S')"
dump_data_file="$dump_head-data.sql"
dump_logs_file="$dump_head-logs.sql"
dump_tbls_file="$dump_head.txt"
dump_gzip_file="$dump_head.gz"

unalias mysql >/dev/null 2>&1
unalias mysqldump >/dev/null 2>&1

mysql --defaults-extra-file="$config" -D "$schema" -N -e "show tables" > "$dump_tbls_file"

echo -e "\033[0;33mNOTE: dump trace tables without data, count=\033[m"
grep -cF '$' "$dump_tbls_file"

# shellcheck disable=SC2046
mysqldump --defaults-extra-file="$config" \
--no-data \
--set-gtid-purged=OFF \
--single-transaction \
--databases "$schema" \
--tables $(grep -F '$' "$dump_tbls_file") > "$dump_logs_file"

echo -e "\033[0;33mNOTE: dump main tables with data, count=\033[m"
grep -vcF '$' "$dump_tbls_file"

# https://dev.mysql.com/doc/refman/8.0/en/mysqldump.html#mysqldump-performance-options
dump_opt=""
if [[ "$nodata" == "nodata" ]]; then
  dump_opt='--no-data'
fi
# shellcheck disable=SC2046
mysqldump --defaults-extra-file="$config" \
--set-gtid-purged=OFF \
--single-transaction \
--opt $dump_opt \
--quick \
--max-allowed-packet=64M \
--net-buffer-length=64k \
--databases "$schema" \
--tables $(grep -vF '$' "$dump_tbls_file") > "$dump_data_file"

echo -e "\033[0;33mNOTE: dump file $dump_head\033[m"
# shellcheck disable=SC2010
ls -lsh |grep "$dump_head"

echo -e "\033[0;33mNOTE: tips for zip, scp, restore \033[m"
cat <<EOF
gzip -c "$dump_data_file" "$dump_logs_file" > "$dump_gzip_file"
scp $dump_gzip_file trydofor@moilioncircle:/data/mysql-dump/

unalias mysql
gzip -dc $dump_gzip_file | pv -Ipert | mysql --defaults-extra-file=mysql-client.cnf \\
--init-command="CREATE DATABASE IF NOT EXISTS $dump_head; use $dump_head;"

nohup gzip -dc $dump_gzip_file | mysql --defaults-extra-file=mysql-client.cnf \\
--init-command="CREATE DATABASE IF NOT EXISTS $dump_head; use $dump_head;" &
EOF
