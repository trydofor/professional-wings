#!/bin/bash
cat <<'EOF'
#################################################
# version 2020-12-25 # test on mac and lin
# 使用`ln -s`把此脚本软连接到`执行目录/workdir`，
# 其同名`env`如（wings-starter.env）会被自动载入。
# `BOOT_CNF|BOOT_ARG|JAVA_ARG`内变量可被延时求值，
# 使用`'`为延时求值，使用`"`为立即求值。
#################################################
EOF
BOOT_DTM=$(date '+-%y%m%d%H%M%S')
################ modify the following params ################
USER_RUN="$USER" # 用来启动程序的用户。
PORT_RUN=''      # 默认端口，空时
ARGS_RUN="start" # 默认参数。空时使用$1
BOOT_JAR="$2"    # 主程序。通过env覆盖
BOOT_OUT=''      # 控制台日志，默认 $BOOT_JAR.out
BOOT_LOG=''      # 程序日志，需要外部指定，用来tail
BOOT_PID=''      # 主程序pid，默认 $BOOT_JAR.pid
BOOT_CNF=''      # 外部配置。通过env覆盖
BOOT_ARG=''      # 启动参数。通过env覆盖
JAVA_XMS='2G'    # 启动参数。通过env覆盖
JAVA_XMX='2G'    # 启动参数。通过env覆盖
JAVA_ARG='
-server
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

-XX:HeapDumpPath=${BOOT_JAR}${BOOT_DTM}.heap
-Xloggc:${BOOT_JAR}${BOOT_DTM}.gc
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
this_file="$0"
cd $(dirname $this_file) || exit
echo -e "\033[0;32mINFO: ==== work dir ==== \033[m"
pwd

# load env
thie_envf=${this_file%.*}.env
if [[ -f "$thie_envf" ]]; then
    echo -e "\033[0;32mINFO: load env file, $thie_envf ==== \033[m"
    source "$thie_envf"
else
    echo -e "\033[0;31mWARN: no env file found, $thie_envf \033[m"
fi

# calc env
BOOT_CNF=$(eval "echo \"$BOOT_CNF\"")
BOOT_ARG=$(eval "echo \"$BOOT_ARG\"")
JAVA_ARG=$(eval "echo \"$JAVA_ARG\"")

# check user
if [[ "$USER_RUN" != "$USER" ]]; then
    echo -e "\033[0;31mERROR: need user $USER_RUN to run\033[m"
    exit
fi

# check java
echo -e "\033[0;32mINFO: ==== java version ==== \033[m"

if ! java -version; then
    echo -e "\033[0;31mERROR: can not found 'java' in the $PATH\033[m"
    exit
fi

# check jar
if [[ ! -f "$BOOT_JAR" ]]; then
    BOOT_JAR=$(find . -type f -name "$BOOT_JAR" | head -n 1)
fi
if [[ ! -f "$BOOT_JAR" ]]; then
    echo -e "\033[0;31mERROR: can not found jar file, $BOOT_JAR\033[m"
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
count=$(pgrep -f -u "$USER_RUN"  " $BOOT_JAR " | wc -l)

# check arg
if [[ "$1" != "" ]]; then
    ARGS_RUN="$1"
fi
case "$ARGS_RUN" in
    start)
        if [[ "$BOOT_CNF" != "" ]]; then
            BOOT_ARG="--spring.config.location=$BOOT_CNF $BOOT_ARG"
        fi
        if [[ "$PORT_RUN" != "" ]]; then
            BOOT_ARG="--server.port=$PORT_RUN $BOOT_ARG"
        fi
        echo -e "\033[0;32mINFO: boot-jar=$BOOT_JAR \033[m"
        echo -e "\033[0;32mINFO: boot-pid=$BOOT_PID \033[m"
        echo -e "\033[0;32mINFO: boot-log=$BOOT_LOG \033[m"
        echo -e "\033[0;32mINFO: boot-out=$BOOT_OUT \033[m"
        echo -e "\033[0;32mINFO: boot-arg=$BOOT_ARG \033[m"
        echo -e "\033[0;32mINFO: java-arg=$JAVA_ARG \033[m"

        if [[ $count -eq 0 ]]; then
            if [[ -f "${BOOT_OUT}" ]]; then
                echo -e "\033[0;33mNOTE: backup old output \033[m"
                mv "${BOOT_OUT}" "${BOOT_OUT}.$(date '+%y%m%d-%H%M%S')"
            fi

            nohup java ${JAVA_ARG} -jar ${BOOT_JAR} ${BOOT_ARG} > ${BOOT_OUT} 2>&1 &
            echo $! > "$BOOT_PID"
            sleep 2
        else
            echo -e "\033[0;31mERROR: already $count running of $JAR_NAME\033[m"
        fi

        cpid=$(pgrep -f "$JAR_NAME")
        if [[ "$cpid" == "" ]];then
          echo -e "\033[0;31mERROR: failed to get PID of $JAR_NAME\033[m"
          exit
        else
            echo -e "\033[0;33mNOTE: current PID=$cpid of $JAR_NAME \033[m"
            ps -fwww "$cpid"
        fi

        tail_log="$BOOT_OUT"
        if [[ -f "$BOOT_LOG" ]]; then
            echo -e "\033[0;33mNOTE: monitor the log-file? input the number \033[m"
            echo -e "\033[0;33mNOTE: 1 - $BOOT_LOG \033[m"
            echo -e "\033[0;33mNOTE: 2 - $BOOT_OUT \033[m"
            echo -e "\033[0;33mNOTE: ENTER to BREAK \033[m"
            read -r num
            case "$num" in
              1) tail_log="$BOOT_LOG";;
              2) tail_log="$BOOT_OUT";;
              *) tail_log=""
            esac
        else
            echo -e "\033[0;31mWARN: not found boot-log=$BOOT_LOG \033[m"
        fi

        if [[ -f "$tail_log" ]]; then
            echo -e "\033[0;33mNOTE: tail current file=$tail_log, Ctrl-C to break \033[m"
            tail -n 50 -f "$tail_log"
        fi
        ;;

    stop)
        if [[ $count -eq 0 ]]; then
            echo -e "\033[0;33mNOTE: not found running $JAR_NAME\033[m"
        else
            cpid=$(pgrep -f "$JAR_NAME")
            echo -e "\033[0;33mNOTE: current PID=$cpid of $JAR_NAME \033[m"
            timeout=60
            pid=$(cat "$BOOT_PID")
            if [[ $pid -ne $cpid ]]; then
                echo -e "\033[0;31mWARN: pid not match, proc-pid=$cpid, file-pid=$pid\033[m"
                echo -e "\033[0;31mWARN: press <y> to kill $cpid, ohters to kill $pid\033[m"
                read -r yon
                if [[ "$yon" == "y" ]]; then
                  pid=$cpid
                fi
            fi
            echo -e "\033[0;33mNOTE: killing boot.pid=$pid of $JAR_NAME\033[m"
            kill "$pid"

            icon=''
            for (( i = 0; i < timeout; i++)); do
                for((j=0;j< 10;j++));do
                    printf "[%ds][%-60s]\r" "$i" "$icon"
                    if [[ ${#icon} -ge 60 ]]; then
                        icon=''
                    else
                        icon='#'${icon}
                    fi
                    sleep 0.1
                done
                if [[ $(pgrep -f -u "$USER_RUN"  " $BOOT_JAR " | wc -l) -eq 0 ]]; then
                    echo -e "\033[0;33mNOTE: successfully stop in $i seconds, pid=$pid of $JAR_NAME\033[m"
                    exit
                fi
            done
            cpid=$(pgrep -f "$JAR_NAME")
            echo -e "\033[0;31mWARN: stopping timeout[${timeout}s], pid=$pid\033[m"
            echo -e "\033[0;31mWARN: need manually check PID=$cpid of ${JAR_NAME}\033[m"
            echo -e "\033[0;33mNOTE: <ENTER> to 'kill -9 $pid', <Ctrl-C> to exit\033[m"
            read -r
            kill -9 "$pid"
        fi
        ;;

    status)
        if [[ $count -eq 0 ]]; then
            echo -e "\033[0;33mNOTE: not found running $JAR_NAME\033[m"
        else
            tail_num=10
            echo -e "\033[0;33mNOTE: last $tail_num lines of output=$BOOT_OUT\033[m"
            tail -n $tail_num "$BOOT_OUT"
            if [[ -f "$BOOT_LOG" ]]; then
                echo -e "\033[0;33mNOTE: tail $tail_num lines of log-file= $BOOT_LOG \033[m"
                tail -n $tail_num "$BOOT_LOG"
            fi
            pid=$(cat "$BOOT_PID")
            cpid=$(pgrep -f "$JAR_NAME")
            echo -e "\033[0;33mNOTE: boot.pid=$pid \033[m"
            echo -e "\033[0;33mNOTE: current PID=$cpid of $JAR_NAME \033[m"
            ps -fwww "$cpid"

            if [[ $pid -ne $cpid ]]; then
                echo -e "\033[0;31mWARN: pid not match, proc-pid=$cpid, file-pid=$pid\033[m"
            fi

            echo -e "\033[0;33mNOTE: jstat -gcutil $cpid 1000 5 \033[m"
            jstat -gcutil "$cpid" 1000 5

            echo -e "\033[0;33mNOTE: === other useful command === \033[m"
            echo -e "\033[0;32m jmap -heap $cpid \033[m mac's bug=8161164, lin's ptrace_scope"
            echo -e "\033[0;32m profiler.sh -d 30 -f profile.svg $cpid \033[m https://github.com/jvm-profiling-tools/async-profiler"
            echo -e "\033[0;32m java -jar arthas-boot.jar $cpid \033[m https://github.com/alibaba/arthas"
        fi
        ;;

    *)
        echo -e '\033[0;31mERROR: use start|stop|status\033[m'
        echo -e '\033[0;31meg ./wings-starter.sh start\033[m'
esac
