#!/bin/bash
cat <<'EOF'
#################################################
# version 2021-07-01 # test on mac and lin
# 使用`ln -s`把此脚本软连接到`执行目录/workdir`，
# 其同名`env`如（wings-starter.env）会被自动载入。
# `BOOT_CNF|BOOT_ARG|JAVA_ARG`内变量可被延时求值，
# 使用`'`为延时求值，使用`"`为立即求值。
#################################################
EOF
################ modify the following params ################
TAIL_LOG='out'   # 默认tail的日志，"log|out|''"
USER_RUN="$USER" # 用来启动程序的用户
PORT_RUN=''      # 默认端口，空时
ARGS_RUN="start" # 默认参数。空时使用$1
BOOT_JAR="$2"    # 主程序。通过env覆盖
BOOT_OUT=''      # 控制台日志，默认 $BOOT_JAR.out
BOOT_LOG=''      # 程序日志，需要外部指定，用来tail
BOOT_PID=''      # 主程序pid，默认 $BOOT_JAR.pid
BOOT_CNF=''      # 外部配置。通过env覆盖
BOOT_ARG=''      # 启动参数。通过env覆盖
JAVA_XMS='2G'    # 启动参数。通过env覆盖
JAVA_XMX='4G'    # 启动参数。通过env覆盖
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

-XX:HeapDumpPath=${JAR_NAME}-${BOOT_DTM}.heap
-Xloggc:${JAR_NAME}-${BOOT_DTM}.gc
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

################ script dir ################
# load env
echo -e "\033[37;42;1mINFO: ==== boot env ==== \033[0m"
this_file="$0"
this_envs=${this_file%.*}.env
if [[ -f "$this_envs" ]]; then
    echo "env file. $this_envs"
    source "$this_envs"
else
    echo -e "\033[31mWARN: no env file found. $this_envs \033[0m"
fi

# change workdir after found env-file
cd $(dirname $this_file) || exit
echo "work dir $(pwd)"

# check java
echo -e "\033[37;42;1mINFO: ==== java version ==== \033[0m"

if ! java -version; then
    echo -e "\033[37;41;1mERROR: can not found 'java' in the $PATH\033[0m"
    exit
fi

# check jar
if [[ ! -f "$BOOT_JAR" ]]; then
    BOOT_JAR=$(find . -type f -name "$BOOT_JAR" | head -n 1)
fi
if [[ ! -f "$BOOT_JAR" ]]; then
    echo -e "\033[37;41;1mERROR: can not found jar file, $BOOT_JAR\033[0m"
    exit
fi

# check out
JAR_NAME=$(basename "$BOOT_JAR")
if [[ "$BOOT_OUT" == "" ]]; then
    BOOT_OUT=${JAR_NAME}.out
fi

# check pid
if [[ "$BOOT_PID" == "" ]]; then
    BOOT_PID=${JAR_NAME}.pid
fi

# check ps
#count=$(ps -ef -u $USER_RUN | grep -E "java.+$BOOT_JAR " | grep -v grep | wc -l)
count=$(pgrep -f -u "$USER_RUN" " $BOOT_JAR " | wc -l)

# check arg
if [[ "$1" != "" ]]; then
    ARGS_RUN="$1"
fi

# check memory, only in linux, not mac
if [[ -f "/proc/meminfo" ]]; then
    echo -e "\033[37;42;1mINFO: ==== system memory ==== \033[0m"
    mem_tot=$(head /proc/meminfo | grep MemTotal | awk '{print $2"K"}' | numfmt --from=auto --to-unit=1M)
    mem_fre=$(head /proc/meminfo | grep MemFree | awk '{print $2"K"}' | numfmt --from=auto --to-unit=1M)
    mem_avl=$(head /proc/meminfo | grep MemAvailable | awk '{print $2"K"}' | numfmt --from=auto --to-unit=1M)
    mem_min=$(numfmt --from=auto --to-unit=1M $JAVA_XMS)
    mem_max=$(numfmt --from=auto --to-unit=1M $JAVA_XMX)
    if [[ "$mem_avl" < "$mem_min" ]]; then
        echo -e "\033[33mNOTE: Available=${mem_avl}Mb < JAVA_XMS=${mem_min}Mb, Total=${mem_tot}Mb, Free=${mem_fre}Mb \033[0m"
    fi
    if [[ "$mem_avl" < "$mem_max" ]]; then
        echo -e "\033[33mNOTE: Available=${mem_avl}Mb < JAVA_XMX=${mem_max}Mb, Total=${mem_tot}Mb, Free=${mem_fre}Mb \033[0m"
    fi
    head /proc/meminfo
fi

# calc env
BOOT_DTM=$(date '+%y%m%d%H%M%S')
BOOT_CNF=$(eval "echo \"$BOOT_CNF\"")
BOOT_ARG=$(eval "echo \"$BOOT_ARG\"")
JAVA_ARG=$(eval "echo \"$JAVA_ARG\"")

if [[ "$BOOT_CNF" != "" ]]; then
    BOOT_ARG="--spring.config.location=$BOOT_CNF $BOOT_ARG"
fi
if [[ "$PORT_RUN" != "" ]]; then
    BOOT_ARG="--server.port=$PORT_RUN $BOOT_ARG"
fi
if [[ "$BOOT_LOG" != "" ]]; then
    BOOT_ARG="--logging.file.name=$BOOT_LOG $BOOT_ARG"
fi

echo -e "\033[37;42;1mINFO: ==== boot arguments ==== \033[0m"
cat <<EOF
boot-jar=$BOOT_JAR
boot-pid=$BOOT_PID
boot-log=$BOOT_LOG
boot-out=$BOOT_OUT
boot-arg=$BOOT_ARG
EOF

echo -e "\033[37;42;1mINFO: ==== java arguments ==== \033[0m"
echo "$JAVA_ARG"

# check user
if [[ "$USER_RUN" != "$USER" ]]; then
    echo -e "\033[37;41;1mERROR: need user $USER_RUN to run\033[0m"
    exit
fi

case "$ARGS_RUN" in
    start)
        if [[ $count -eq 0 ]]; then
            if [[ -f "${BOOT_OUT}" ]]; then
                echo -e "\033[33mNOTE: backup old output \033[0m"
                mv "${BOOT_OUT}" "${BOOT_OUT}-${BOOT_DTM}.bak"
            fi

            nohup java ${JAVA_ARG} -jar ${BOOT_JAR} ${BOOT_ARG} >${BOOT_OUT} 2>&1 &
            echo $! >"$BOOT_PID"
            sleep 2
        else
            echo -e "\033[37;41;1mERROR: already $count running of $JAR_NAME\033[0m"
        fi

        cpid=$(pgrep -f "$JAR_NAME")
        if [[ "$cpid" == "" ]]; then
            echo -e "\033[37;41;1mERROR: failed to get PID of $JAR_NAME\033[0m"
            exit
        else
            echo -e "\033[37;43;1mNOTE: current PID=$cpid of $JAR_NAME \033[0m"
            ps -fwww "$cpid"
        fi

        tail_log="$BOOT_OUT"
        if [[ -f "$BOOT_LOG" ]]; then
            echo -e "\033[37;43;1mNOTE: monitor the log-file? input the number \033[0m"
            echo -e "\033[33mNOTE: 1 - $BOOT_LOG \033[0m"
            echo -e "\033[33mNOTE: 2 - $BOOT_OUT \033[0m"
            echo -e "\033[33mNOTE: ENTER to BREAK \033[0m"
            if [[ "$TAIL_LOG" == 'log' ]]; then
                tail_log="$BOOT_LOG"
            elif [[ "$TAIL_LOG" == 'out' ]]; then
                tail_log="$BOOT_OUT"
            else
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
        if [[ $count -eq 0 ]]; then
            echo -e "\033[37;43;1mNOTE: not found running $JAR_NAME\033[0m"
        else
            cpid=$(pgrep -f "$JAR_NAME")
            echo -e "\033[33mNOTE: current PID=$cpid of $JAR_NAME \033[0m"
            timeout=60
            pid=$(cat "$BOOT_PID")
            if [[ $pid -ne $cpid ]]; then
                echo -e "\033[31mWARN: pid not match, proc-pid=$cpid, file-pid=$pid\033[0m"
                echo -e "\033[31mWARN: press <y> to kill $cpid, ohters to kill $pid\033[0m"
                read -r yon
                if [[ "$yon" == "y" ]]; then
                    pid=$cpid
                fi
            fi
            echo -e "\033[33mNOTE: killing boot.pid=$pid of $JAR_NAME\033[0m"
            kill "$pid"

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
                if [[ $(pgrep -f -u "$USER_RUN" " $BOOT_JAR " | wc -l) -eq 0 ]]; then
                    echo -e "\033[33mNOTE: successfully stop in $i seconds, pid=$pid of $JAR_NAME\033[0m"
                    exit
                fi
            done
            cpid=$(pgrep -f "$JAR_NAME")
            echo -e "\033[37;41;1mWARN: stopping timeout[${timeout}s], pid=$pid\033[0m"
            echo -e "\033[31mWARN: need manually check PID=$cpid of ${JAR_NAME}\033[0m"
            echo -e "\033[33mNOTE: <ENTER> to 'kill -9 $pid', <Ctrl-C> to exit\033[0m"
            read -r
            kill -9 "$pid"
        fi
        ;;

    status)
        if [[ $count -eq 0 ]]; then
            echo -e "\033[37;43;1mNOTE: not found running $JAR_NAME\033[0m"
        else
            tail_num=10
            echo -e "\033[37;43;1mNOTE: last $tail_num lines of output=$BOOT_OUT\033[0m"
            tail -n $tail_num "$BOOT_OUT"
            if [[ -f "$BOOT_LOG" ]]; then
                echo -e "\033[37;43;1mNOTE: tail $tail_num lines of log-file= $BOOT_LOG \033[0m"
                tail -n $tail_num "$BOOT_LOG"
            fi
            pid=$(cat "$BOOT_PID")
            cpid=$(pgrep -f "$JAR_NAME")
            echo -e "\033[37;43;1mNOTE: boot.pid=$pid \033[0m"
            echo -e "\033[33mNOTE: current PID=$cpid of $JAR_NAME \033[0m"
            ps -fwww "$cpid"

            if [[ $pid -ne $cpid ]]; then
                echo -e "\033[31mWARN: pid not match, proc-pid=$cpid, file-pid=$pid\033[0m"
            fi

            echo -e "\033[37;43;1mNOTE: jstat -gcutil $cpid 1000 5 \033[0m"
            jstat -gcutil "$cpid" 1000 5

            echo -e "\033[37;43;1mNOTE: ==== other useful command ==== \033[0m"
            echo -e "\033[32m jmap -heap $cpid \033[m mac's bug=8161164, lin's ptrace_scope"
            echo -e "\033[32m profiler.sh -d 30 -f profile.svg $cpid \033[m https://github.com/jvm-profiling-tools/async-profiler"
            echo -e "\033[32m java -jar arthas-boot.jar $cpid \033[m https://github.com/alibaba/arthas"
        fi
        ;;

    *)
        echo -e '\033[37;41;1mERROR: use start|stop|status\033[m'
        echo -e '\033[31meg ./wings-starter.sh start\033[m'
        ;;
esac
echo
