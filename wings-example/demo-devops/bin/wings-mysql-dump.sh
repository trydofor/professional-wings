#!/bin/bash
THIS_VERSION=2022-02-06

cat << EOF
#################################################
# Version $THIS_VERSION # test on Mac and Lin
# 通过 mysqldump 生成 'db-ts' 开头的以下文件，
- {db-ts}-main.sql 主表
- {db-ts}-logs.sql log表
- {db-ts}-tbl.log dump的表及结果信息
- {db-ts}-tip.txt scp及restore手册

# Usage $0 database [option] [nodata]
- database - 需要dump的database，必填
- option - 存在时，使用'--defaults-extra-file'
- nodata - 非空时，增加'--skip-dump-rows'参数
# option 详细参考client和mysqldump段
- https://dev.mysql.com/doc/refman/8.0/en/option-files.html
#################################################
EOF

database="$1"
option="$2"
nodata="$3"

if [[ "$database" == "" ]]; then
  echo -e "\033[0;31mWARN: need param-1=database to dump\033[m"
  exit
fi

# https://dev.mysql.com/doc/refman/8.0/en/option-files.html
opt_file=""
if [[ -f "$option" ]]; then
  echo -e "\033[0;33mNOTE: current option file \033[m"
  cat "$option"
  opt_file="--defaults-extra-file=$option"
else
  echo -e "\033[0;31mNOTE: use mysql default(my.cnf), something like\033[m"
cat << 'EOF'
[client]
protocol=tcp
host=127.0.0.1
port=3306
user=trydofor
password=moilioncircle

[mysqldump]
#column-statistics=0
max-allowed-packet=64M
net-buffer-length=64k
set-gtid-purged=OFF
single-transaction
EOF
fi
echo

opt_nodata=""
if [[ "$nodata" != "" ]]; then
  opt_nodata='--no-data'
fi

###
dump_head="${database}-$(date '+%Y%m%d%H%M%S')"
dump_main_file="$dump_head-main.sql"
dump_logs_file="$dump_head-logs.sql"
dump_tbl_file="$dump_head-tbl.log"
dump_tip_file="$dump_head-tip.txt"
dump_tar_file="$dump_head.tgz"
dump_md5_file="$dump_head.md5"

unalias mysql >/dev/null 2>&1
unalias mysqldump >/dev/null 2>&1

if mysql "$opt_file" -D "$database" -N -e "show tables" > "$dump_tbl_file"; then
  echo "successfully show tables"
else
  echo -e "\033[37;41;1mERROR: failed to show tables of $database \033[0m"
  rm -rf "$dump_tbl_file"
  exit
fi

logs_cnt=$(grep -cE '\$|__' "$dump_tbl_file")
if [[ $logs_cnt == 0 ]]; then
  echo "no logs tables to dump"
  echo "-- no logs tables to dump" > "$dump_logs_file"
else
  echo -e "\033[0;33mNOTE: dump logs tables without data, tables=\033[m"
  grep -E '\$|_+$' "$dump_tbl_file"

  # shellcheck disable=SC2046
  if mysqldump "$opt_file" --no-data \
  "$database" $(grep -E '\$|__' "$dump_tbl_file") > "$dump_logs_file"; then
    echo "successfully dump logs"
  else
    echo -e "\033[37;41;1mERROR: failed to dump logs \033[0m"
    exit
  fi
fi

main_cnt=$(grep -cvE '\$|__' "$dump_tbl_file")
if [[ $main_cnt == 0 ]]; then
  echo "no main tables to dump"
  echo "-- no main tables to dump" > "$dump_main_file"
else
  echo -e "\033[0;33mNOTE: dump main tables with data, count=\033[m"
  # shellcheck disable=SC2046
  if mysqldump "$opt_file" $opt_nodata \
  "$database" $(grep -vE '\$|__' "$dump_tbl_file") > "$dump_main_file"; then
    echo "successfully dump main"
  else
    echo -e "\033[37;41;1mERROR: failed to dump main \033[0m"
    exit
  fi
fi

echo -e "\033[0;33mNOTE: dump file $dump_head\033[m"
echo >> "$dump_tbl_file"

# shellcheck disable=SC2010
ls -lsh |grep "$dump_head" | tee -a "$dump_tbl_file"

echo -e "\033[0;33mNOTE: tips for zip, scp, restore \033[m"
tee -a "$dump_tip_file" << EOF
md5sum -c $dump_md5_file

tar -tzf $dump_tar_file
tar -xzf $dump_tar_file

scp $dump_tar_file trydofor@moilioncircle:/data/mysql-dump/

unalias mysql
newdb="\\\`$dump_head\\\`"

# with progress
cat $dump_logs_file $dump_main_file \\
| pv -Ipert \\
| sed -E 's/DEFINER=[^*]+/DEFINER=CURRENT_USER/g' \\
| mysql $opt_file \\
--init-command="CREATE DATABASE IF NOT EXISTS \$newdb; use \$newdb;"

# nohup
nohup \\
cat $dump_logs_file $dump_main_file \\
| sed -E 's/DEFINER=[^*]+/DEFINER=CURRENT_USER/g' \\
| mysql $opt_file \\
--init-command="CREATE DATABASE IF NOT EXISTS \$newdb; use \$newdb;" \\
&
EOF

echo -e "\033[0;33mNOTE: tar files into $dump_tar_file \033[m"
tar -czf "$dump_tar_file" "$dump_main_file" "$dump_logs_file" "$dump_tbl_file" "$dump_tip_file" \
&& md5sum "$dump_tar_file" | tee "$dump_md5_file" \
&& rm -f "$dump_main_file" "$dump_logs_file" "$dump_tbl_file" "$dump_tip_file"

