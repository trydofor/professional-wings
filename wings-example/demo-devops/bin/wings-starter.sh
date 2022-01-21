#!/bin/bash
THIS_VERSION=2022-01-20

cat <<EOF
#################################################
# Version $THIS_VERSION # for Mac&Lin / BusyBox&Bash
# 使用'ln -s'把此脚本软连接到'执行目录/workdir'，
# 其同名'env'如（wings-starter.env）会被自动载入。
# 同一主机环境，同一boot.jar只能执行一份，多份需更名。
# 'BOOT_CNF|BOOT_ARG|JAVA_ARG' 内变量可被延时求值，
# 使用 ' 为延时求值，使用 " 为立即求值。
#################################################
EOF
################ modify the following params ################
WORK_DIR=''      # 脚本生成文件，日志的目录，默认空（脚本位置）
TAIL_LOG='log'   # 默认tail的日志，"log|out|new|ask"
USER_RUN="$USER" # 用来启动程序的用户
PORT_RUN=''      # 默认端口，空时
BOOT_JAR=''      # 主程序。可通过$1覆盖，绝对路径或相对WORK_DIR
ARGS_RUN='start' # 默认参数。若$1或$2指定
BOOT_OUT=''      # 控制台日志，默认 $BOOT_JAR-*.out
BOOT_LOG=''      # 程序日志，需要外部指定，用来tail
BOOT_PID=''      # 主程序pid，默认 $BOOT_JAR.pid
BOOT_CNF=''      # 外部配置。通过env覆盖
BOOT_ARG=''      # 启动参数。通过env覆盖
JAVA_XMS='2G'    # 启动参数。通过env覆盖
JAVA_XMX='4G'    # 启动参数。通过env覆盖
WARN_TXT=''      # 预设的警告词
WARN_AGO=''      # 日志多少秒不更新，则警报，空表示忽略
WARN_RUN=''      # 若pid消失或日志无更新则执行
# shellcheck disable=SC2016
JAVA_ARG='-server
-Djava.awt.headless=true
-Dfile.encoding=UTF-8

-Xms${JAVA_XMS}
-Xmx${JAVA_XMX}
-XX:MetaspaceSize=128m
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:ParallelGCThreads=8
-XX:ConcGCThreads=8
-XX:InitiatingHeapOccupancyPercent=70

-XX:HeapDumpPath=${BOOT_TKN}.heap
-Xloggc:${BOOT_TKN}.gc
-XX:+PrintGC
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
'

#-XX:+UseConcMarkSweepGC
#-XX:NewSize=256m
#-XX:MaxNewSize=256m
#-XX:+CMSClassUnloadingEnabled
#-XX:MaxTenuringThreshold=5
#-XX:+HeapDumpOnOutOfMemoryError
#-XX:-OmitStackTraceInFastThrow

################ NO NEED to modify the following ################
BOOT_DTM=$(date '+%y%m%d%H%M%S') # 启动日时
BOOT_TKN=''                      # 启动token，由jar+dtm构成
BOOT_MD5=''                      # 以safe模式执行的文件md5sum
JAR_NAME=''                      # boot-jar本名
#
function print_help() {
    echo -e '\033[32m docker \033[m start in docker with console log'
    echo -e '\033[32m start \033[m start the {boot-jar} and tail the log'
    echo -e '\033[32m stop [snd=30]\033[m stop the {boot-jar} gracefully in {snd} seconds'
    echo -e '\033[32m status \033[m show the {boot-jar} runtime status'
    echo -e '\033[32m warn \033[m monitor the {boot-jar} and log'
    echo -e '\033[32m clean [days=30] [y] \033[m clean up log-file {days} ago'
    echo -e '\033[32m cron \033[m show the {boot-jar} crontab usage'
    echo -e '\033[32m free \033[m check memory free'
    echo -e '\033[32m check \033[m check shell command'
    echo -e '\033[37;43;1m default ./wings-starter.sh start\033[m'
    echo -e '\033[37;43;1m default ./wings-starter.sh boot.jar start\033[m'
}

function print_args() {
    echo -e "\033[37;42;1mINFO: ==== boot arguments ==== \033[0m"
    echo "boot-jar=$BOOT_JAR"
    echo "boot-md5=$BOOT_MD5"
    echo "boot-tkn=$BOOT_TKN"
    echo "boot-pid=$BOOT_PID"
    echo "boot-log=$BOOT_LOG"
    echo "boot-out=$BOOT_OUT"
    echo "boot-arg=$BOOT_ARG"

    echo -e "\033[37;42;1mINFO: ==== java arguments ==== \033[0m"
    echo "$JAVA_ARG"
}

function check_cmd() {
    cmd=$(printf "%-10s" "$1")
    if info=$(which "$1") >/dev/null 2>&1; then
        echo -e "\033[32m $cmd \033[m $info"
    else
        echo -e "\033[31m $cmd not found \033[m"
    fi
}

function check_user() {
    if [[ "$USER_RUN" != "$USER" ]]; then
        echo -e "\033[37;41;1mERROR: need user $USER_RUN to run \033[0m"
        exit
    fi
}

function check_java() {
    echo -e "\033[37;42;1mINFO: ==== java version ==== \033[0m"
    if ! java -version; then
        echo -e "\033[37;41;1mERROR: can not found 'java' in the $PATH \033[0m"
        exit
    fi
}

function check_boot() {
    if [[ -L "$BOOT_JAR" || -f "$BOOT_JAR" ]]; then
        BOOT_JAR=$(realpath -s "$BOOT_JAR")
    elif [[ "$1" == "" ]]; then
        frm=$(dirname "$BOOT_JAR")
        tkn=$(basename "$BOOT_JAR")

        cnt=$(find "$frm" -name "$tkn" | wc -l)
        if [[ $cnt -ne 1 ]]; then
            find "$frm" -name "$tkn"
            echo -e "\033[37;41;1mERROR: found $cnt jar file, $BOOT_JAR \033[0m"
            exit
        fi
        BOOT_JAR=$(find "$frm" -name "$tkn")
        check_boot end
    else
        echo -e "\033[37;41;1mERROR: can not found jar file, $BOOT_JAR \033[0m"
        exit
    fi
}

################ script body ################
# load env
echo -e "\033[37;42;1mINFO: ==== boot env ==== \033[0m"
this_file="$0"
this_envs=${this_file%.*}.env
if [[ -f "$this_envs" ]]; then
    echo "env-file=$this_envs"
    # shellcheck disable=SC1090
    source "$this_envs"
else
    echo -e "\033[31mWARN: no env file found. $this_envs \033[0m"
fi

# change workdir
if [[ "$WORK_DIR" == "" ]]; then
    WORK_DIR=$(dirname "$this_file")
fi
cd "$WORK_DIR" || exit
WORK_DIR=$(realpath -s "$WORK_DIR")
echo "work-dir=$WORK_DIR"

# check arg
if [[ -L "$1" || -f "$1" ]]; then
    BOOT_JAR="$1"
    shift
fi
if [[ "$1" != "" ]]; then
    ARGS_RUN="$1"
fi

# check boot jar
check_boot
JAR_NAME=$(basename "$BOOT_JAR")
BOOT_TKN="${JAR_NAME}-${BOOT_DTM}"

file_md5="${JAR_NAME}.md5"
if [[ -f "$file_md5" ]]; then
    BOOT_MD5=$(awk '{print $1}' <"$file_md5")
fi

# check pid&out
if [[ "$BOOT_PID" == "" ]]; then
    BOOT_PID="${JAR_NAME}.pid"
fi
if [[ "$BOOT_OUT" == "" ]]; then
    # shellcheck disable=SC2012,SC2086
    BOOT_OUT="$(ls -t ${JAR_NAME}*.out 2>/dev/null | head -n1)"
fi

# lazy env
BOOT_CNF=$(eval "echo \"$BOOT_CNF\"")
BOOT_ARG=$(eval "echo \"$BOOT_ARG\"")
JAVA_ARG=$(eval "echo \"$JAVA_ARG\"")

# calc arg
if [[ "$BOOT_CNF" != "" ]]; then
    BOOT_ARG="--spring.config.location=$BOOT_CNF $BOOT_ARG"
fi
if [[ "$PORT_RUN" != "" ]]; then
    BOOT_ARG="--server.port=$PORT_RUN $BOOT_ARG"
fi
if [[ "$BOOT_LOG" != "" ]]; then
    BOOT_ARG="--logging.file.name=$BOOT_LOG $BOOT_ARG"
fi

# check ps
grep_key=" -jar ${BOOT_JAR}[ -]"
count=$(pgrep -f "$grep_key" | wc -l)

# exec cmd
case "$ARGS_RUN" in
    docker)
        check_java
        print_args
        if [[ $count -eq 0 ]]; then
            # shellcheck disable=SC2086
            java $JAVA_ARG -jar $BOOT_JAR $BOOT_ARG 2>&1
        else
            echo -e "\033[37;41;1mERROR: has $count running $BOOT_JAR \033[0m"
            pgrep -alf "$grep_key"
        fi
        ;;
    start)
        check_java

        if [[ $count -ne 0 ]]; then
            print_args
            echo -e "\033[37;41;1mERROR: has $count running $BOOT_JAR \033[0m"
            pgrep -alf "$grep_key"
            exit
        fi

        # safe backup
        md5sum "$BOOT_JAR" >"$file_md5"
        BOOT_MD5=$(awk '{print $1}' <"$file_md5")
        safe_jar="$BOOT_JAR-$BOOT_MD5"
        if [[ ! -f "$safe_jar" ]]; then
            echo -e "\033[33mNOTE: link safe_jar  $safe_jar \033[0m"
            mv -f "$BOOT_JAR" "$safe_jar"
            ln -sf "$(basename "$safe_jar")" "$BOOT_JAR"
        fi

        BOOT_OUT="${BOOT_TKN}.out"
        #
        print_args
        check_user

        # shellcheck disable=SC2086
        nohup java $JAVA_ARG -jar $safe_jar $BOOT_ARG >$BOOT_OUT 2>&1 &
        echo $! >"$BOOT_PID"
        sleep 2

        cid=$(pgrep -f "$grep_key" | tr '\n' ' ')
        if [[ "$cid" == "" ]]; then
            echo -e "\033[37;41;1mERROR: failed to check PID by $BOOT_JAR \033[0m"
            exit
        else
            echo -e "\033[37;43;1mNOTE: current PID=$cid of $BOOT_JAR \033[0m"
            # shellcheck disable=SC2086
            ps -fwww $cid
        fi

        tail_log="$BOOT_OUT"
        if [[ -f "$BOOT_LOG" ]]; then
            if [[ "$TAIL_LOG" == 'log' ]]; then
                tail_log="$BOOT_LOG"
            elif [[ "$TAIL_LOG" == 'out' ]]; then
                tail_log="$BOOT_OUT"
            elif [[ "$TAIL_LOG" == 'new' ]]; then
                if [[ "$BOOT_LOG" -nt "$BOOT_OUT" ]]; then
                    tail_log="$BOOT_LOG"
                else
                    tail_log="$BOOT_OUT"
                fi
                echo -e "\033[33mNOTE: $tail_log is newer \033[0m"
            else
                echo -e "\033[37;43;1mNOTE: monitor the log-file? input the number \033[0m"
                echo -e "\033[33mNOTE: 1 - $BOOT_LOG \033[0m"
                echo -e "\033[33mNOTE: 2 - $BOOT_OUT \033[0m"
                echo -e "\033[33mNOTE: ENTER to BREAK \033[0m"

                read -r num
                case "$num" in
                    1) tail_log="$BOOT_LOG" ;;
                    2) tail_log="$BOOT_OUT" ;;
                    *) tail_log="" ;;
                esac
            fi
        else
            echo -e "\033[31mWARN: not found boot-log=$BOOT_LOG \033[0m"
        fi

        if [[ -f "$tail_log" ]]; then
            echo -e "\033[37;43;1mNOTE: tail current file=$tail_log, Ctrl-C to break \033[0m"
            tail -n 50 -f "$tail_log"
        fi
        ;;
    stop)
        print_args

        if [[ $count -eq 0 ]]; then
            echo -e "\033[37;43;1mNOTE: not running $BOOT_JAR \033[0m"
            exit
        fi

        cid=$(pgrep -f "$grep_key" | tr '\n' ' ')
        echo -e "\033[33mNOTE: current PID=$cid of $BOOT_JAR \033[0m"
        timeout="$2"
        if [[ "$timeout" == "" ]]; then
            timeout=30
        fi

        check_user

        pid=$(cat "$BOOT_PID")
        if [[ $pid -ne $cid ]]; then
            echo -e "\033[31mWARN: pid not match, file-pid=$pid , proc-pid=$cid \033[0m"
            echo -e "\033[31mWARN: press <ENTER> to kill $pid, <y> to kill all $cid \033[0m"
            read -r yon
            if [[ "$yon" == "y" ]]; then
                pid=$cid
            fi
        fi
        echo -e "\033[33mNOTE: killing boot.pid=$pid of $BOOT_JAR \033[0m"
        # shellcheck disable=SC2086
        kill $pid

        icon=''
        for ((i = 0; i < timeout; i++)); do
            for ((j = 0; j < 10; j++)); do
                printf "[%ds][%-60s]\r" "$i" "$icon"
                if [[ ${#icon} -ge 60 ]]; then
                    icon=''
                else
                    icon='#'${icon}
                fi
                sleep 0.1
            done

            if [[ $(pgrep -f "$grep_key" | wc -l) -eq 0 ]]; then
                echo -e "\033[33mNOTE: successfully stop in $i seconds, pid=$pid of $BOOT_JAR \033[0m"
                exit
            fi
        done
        cid=$(pgrep -f "$grep_key" | tr '\n' ' ')
        echo -e "\033[37;41;1mWARN: stopping timeout[${timeout}s], pid=$pid \033[0m"
        echo -e "\033[31mWARN: need manually check PID=$cid of $BOOT_JAR \033[0m"
        echo -e "\033[33mNOTE: <ENTER> to 'kill -9 $pid', <Ctrl-C> to exit \033[0m"
        read -r
        # shellcheck disable=SC2086
        kill -9 $pid
        ;;
    status)
        print_args

        if [[ $count -eq 0 ]]; then
            echo -e "\033[37;43;1mNOTE: not running $BOOT_JAR \033[0m"
            exit
        fi

        tail_num=10
        if [[ -f "$BOOT_OUT" ]]; then
            echo -e "\033[37;43;1mNOTE: last $tail_num lines of output=$BOOT_OUT \033[0m"
            tail -n $tail_num "$BOOT_OUT"
        fi
        if [[ -f "$BOOT_LOG" ]]; then
            echo -e "\033[37;43;1mNOTE: tail $tail_num lines of log-file= $BOOT_LOG \033[0m"
            tail -n $tail_num "$BOOT_LOG"
        fi
        pid=$(cat "$BOOT_PID")
        cid=$(pgrep -f "$grep_key" | tr '\n' ' ')
        echo -e "\033[37;43;1mNOTE: boot.pid=$pid \033[0m"
        echo -e "\033[33mNOTE: current PID=$cid of $BOOT_JAR \033[0m"
        # shellcheck disable=SC2086
        ps -fwww $cid

        if [[ $pid -ne $cid ]]; then
            echo -e "\033[31mWARN: pid not match, proc-pid=$cid, file-pid=$pid \033[0m"
        fi

        # shellcheck disable=SC2009,SC2086
        mrs=$(ps -o rss $cid | grep -v RSS | numfmt --grouping)
        # shellcheck disable=SC2009,SC2086
        mvs=$(ps -o vsz $cid | grep -v VSZ | numfmt --grouping)
        echo -e "\033[37;43;1mNOTE: ps -o rss -o vsz $cid \033[0m"
        echo -e "\033[32m Resident= $mrs Kb\033[m"
        echo -e "\033[32m Virtual=  $mvs Kb\033[m"

        echo -e "\033[37;43;1mNOTE: ==== other useful command ==== \033[0m"
        echo -e "\033[32m jmap -heap $cid \033[m mac's bug=8161164, lin's ptrace_scope"
        echo -e "\033[32m profiler.sh -d 30 -f profile.svg $cid \033[m https://github.com/jvm-profiling-tools/async-profiler"
        echo -e "\033[32m java -jar arthas-boot.jar $cid \033[m https://github.com/alibaba/arthas"

        check_user
        echo -e "\033[37;43;1mNOTE: jstat -gcutil $cid 1000 5 \033[0m"
        # shellcheck disable=SC2086
        jstat -gcutil $cid 1000 5
        ;;
    warn)
        warn_got=''
        if [[ $count -eq 0 ]]; then
            echo -e "\033[33mNOTE: not running $BOOT_JAR \033[0m"
            WARN_TXT="$WARN_TXT,PID"
            warn_got="pid"
        fi

        if [[ "$WARN_AGO" != "" ]]; then
            log_time=$(date +%s -r "$BOOT_LOG")
            ago_time=$(date +%s -d "now -$WARN_AGO second")
            if [[ log_time -lt ago_time ]]; then
                echo -e "\033[33mNOTE: no update in $WARN_AGO seconds, log $BOOT_LOG \033[0m"
                WARN_TXT="$WARN_TXT,LOG"
                warn_got="log"
            fi
        fi

        if [[ "$warn_got" != "" ]]; then
            if [[ "$WARN_RUN" == "" ]]; then
                echo -e "\033[33mNOTE: skip monitor for empty WARN_RUN \033[0m"
            else
                echo "$WARN_RUN"
                eval "$WARN_RUN"
                echo
                echo -e "\033[33mNOTE: sent warn notice \033[0m"
            fi
        else
            echo -e "\033[37;42;1mNOTE: good status : PID and LOG \033[0m"
        fi
        ;;
    clean)
        dys="$2"
        if [[ "$dys" == "" ]]; then
            dys=30
        fi
        echo -e "\033[32m find . -name \"${JAR_NAME}[.-]*\" -type f -mtime +$dys \033[m"
        old=$(find . -name "${JAR_NAME}[.-]*" -type f -mtime +$dys | wc -l)
        if [[ $old -gt 10 ]]; then
            find . -name "${JAR_NAME}[.-]*" -type f -mtime +$dys
            echo -e "\033[37;43;1mNOTE: ==== clear ${dys}-days ago log file ==== \033[0m"
            check_user

            yon="$3"
            if [[ "$3" == "" ]]; then
                echo -e "\033[31mWARN: press <y> to rm them all \033[0m"
                read -r yon
            fi
            if [[ "$yon" == "y" ]]; then
                find . -name "${JAR_NAME}[.-]*" -type f -mtime +$dys -print0 | xargs -0 rm -f
            fi
        else
            echo -e "\033[37;42;1mNOTE: few ${dys}-days ago logs \033[0m"
        fi
        ;;
    cron)
        this_path=$(realpath -s "$this_file")
        echo -e "\033[37;43;1mNOTE: ==== crontab usage ==== \033[0m"
        echo -e "\033[32m crontab -e -u $USER_RUN \033[m"
        echo -e "\033[32m crontab -l -u $USER_RUN \033[m"
        echo -e "\033[32m */5 * * * * $this_path warn \033[m"
        echo -e "\033[32m 0 0 * * * $this_path clean 30 y \033[m"
        ;;
    free)
        echo -e "\033[37;42;1mINFO: ==== system memory ==== \033[0m"
        if [[ -f "/proc/meminfo" ]]; then # in linux
            mem_tot=$(head /proc/meminfo | grep MemTotal | awk '{print $2"K"}' | numfmt --from=auto --to-unit=1M)
            mem_fre=$(head /proc/meminfo | grep MemFree | awk '{print $2"K"}' | numfmt --from=auto --to-unit=1M)
            mem_avl=$(head /proc/meminfo | grep MemAvailable | awk '{print $2"K"}' | numfmt --from=auto --to-unit=1M)
            mem_min=$(numfmt --from=auto --to-unit=1M $JAVA_XMS)
            mem_max=$(numfmt --from=auto --to-unit=1M $JAVA_XMX)
            if [[ "$mem_avl" -lt "$mem_min" ]]; then
                echo -e "\033[33mNOTE: Available=${mem_avl}Mb < JAVA_XMS=${mem_min}Mb, Total=${mem_tot}Mb, Free=${mem_fre}Mb \033[0m"
            fi
            if [[ "$mem_avl" -lt "$mem_max" ]]; then
                echo -e "\033[33mNOTE: Available=${mem_avl}Mb < JAVA_XMX=${mem_max}Mb, Total=${mem_tot}Mb, Free=${mem_fre}Mb \033[0m"
            fi
            head /proc/meminfo
        else
            vm_stat
        fi
        ;;
    check)
        echo -e "\033[37;42;1mINFO: ==== check command ==== \033[0m"
        check_cmd awk
        check_cmd basename
        check_cmd cat
        check_cmd date
        check_cmd find
        check_cmd grep
        check_cmd head
        check_cmd java
        check_cmd jstat
        check_cmd kill
        check_cmd ln
        check_cmd ls
        check_cmd md5sum
        check_cmd mv
        check_cmd nohup
        check_cmd numfmt
        check_cmd pgrep
        check_cmd printf
        check_cmd ps
        check_cmd realpath
        check_cmd tail
        check_cmd tr
        check_cmd wc
        check_cmd which
        check_cmd xargs
        ;;
    *)
        if [[ "$ARGS_RUN" == "help" ]]; then
            echo -e '\033[37;42;1mNOTE: help info, use the following\033[m'
        else
            echo -e '\033[37;41;1mERROR: unsupported command, use the following\033[m'
        fi
        print_help
        ;;
esac
echo
