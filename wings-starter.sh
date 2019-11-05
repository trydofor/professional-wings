#!/bin/bash
cat <<'EOF'
#################################################
# 使用`ln -s`把此脚本软连接到`执行目录/workdir`，
# 其同名`env`如（wings-starter.env）会被自动载入。
# `BOOT_CNF|BOOT_ARG|JAVA_ARG`内变量可被延时求值，
# 使用`'`为延时求值，使用`"`为立即求值。
#################################################
EOF

################ modify the following params ################
USER_RUN="$USER" # 用来启动程序的用户。
EXEC_CMD="start" # 默认的行为。空时使用$1
BOOT_JAR="$2"    # 主程序。通过env覆盖
BOOT_LOG=''      # 控制台日志，默认 $BOOT_JAR.log
BOOT_PID=''      # 主程序pid，默认 $BOOT_JAR.pid
BOOT_CNF=''      # 外部配置。通过env覆盖
BOOT_ARG=''      # 启动参数。通过env覆盖
JAVA_ARG='
-server
-Duser.timezone=Asia/Shanghai
-Djava.awt.headless=true
-Dfile.encoding=UTF-8

-Xms6G
-Xmx6G
-XX:MetaspaceSize=128m
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:ParallelGCThreads=8
-XX:ConcGCThreads=8
-XX:InitiatingHeapOccupancyPercent=70

-XX:HeapDumpPath=${BOOT_JAR}.heap
-Xloggc:${BOOT_JAR}.gc
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
echo -e "\e[0;32mINFO: ==== work dir ==== \e[m"
pwd

# load env
thie_envf=$(basename "$this_file" | sed -r 's/\.\w+/.env/g')
if [[ -f "$thie_envf" ]]; then
    echo -e "\e[0;32mINFO: load env file, $thie_envf ==== \e[m"
    source "$thie_envf"
fi

# refine env
BOOT_CNF=$(eval "echo \"$BOOT_CNF\"")
BOOT_ARG=$(eval "echo \"$BOOT_ARG\"")
JAVA_ARG=$(eval "echo \"$JAVA_ARG\"")

# check user
if [[ "$USER_RUN" != "$USER" ]]; then
    echo -e "\e[0;31mERROR: need user $USER_RUN to run\e[m"
    exit
fi

# check java
echo -e "\e[0;32mINFO: ==== java version ==== \e[m"
java -version
if [[ "$?" != "0" ]]; then
    echo -e '\e[0;31mERROR: can not found `java` in the $PATH\e[m'
    exit
fi

# search jar file
JAR_FILE=$(find . -type f -name ${BOOT_JAR} | head -n 1)
if [[ ! -f "$JAR_FILE" ]]; then
    echo -e "\e[0;31mERROR: can not found jar file, ${BOOT_JAR}\e[m"
    exit
fi

JAR_NAME=$(basename ${JAR_FILE})
if [[ "$BOOT_LOG" == "" ]]; then
    BOOT_LOG=${JAR_NAME}.log
fi

if [[ "$BOOT_PID" == "" ]]; then
    BOOT_PID=${JAR_NAME}.pid
fi

count=$(ps -ef | grep "$JAR_NAME" | grep -v grep | grep -v "$this_file" | wc -l)
if [[ "$1" != "" ]]; then
    EXEC_CMD="$1"
fi
case "$EXEC_CMD" in
    start)
        if [[ ${count} == 0 ]]; then
            if [ "$BOOT_CNF" != "" ];then
                BOOT_ARG="--spring.config.location=$BOOT_CNF $BOOT_ARG"
            fi
            echo -e "\e[0;32mINFO: boot-jar=$JAR_FILE \e[m"
            echo -e "\e[0;33mNOTE: boot-pid=$BOOT_PID \e[m"
            echo -e "\e[0;33mNOTE: boot-log=$BOOT_LOG \e[m"
            echo -e "\e[0;33mNOTE: boot-arg=$BOOT_ARG \e[m"
            echo -e "\e[0;33mNOTE: java-arg=$JAVA_ARG \e[m"
            nohup java ${JAVA_ARG} -jar ${JAR_FILE} ${BOOT_ARG} > ${BOOT_LOG} 2>&1 &
            echo $! > ${BOOT_PID}
            sleep 2
        else
            echo -e "\e[0;31mERROR: already $count running of $JAR_NAME\e[m"
        fi
        echo -e "\e[0;33mNOTE: current process info \e[m"
        ps -ef | grep ${JAR_NAME}| grep -v grep
        
        echo -e "\e[0;33mNOTE: tail current log, Ctrl-C to skip \e[m"
        tail -n 50 -f ${BOOT_LOG}
        ;;

    stop)
        if [[ ${count} == 0 ]]; then
            echo -e "\e[0;33mNOTE: not found running $JAR_NAME\e[m"
        else
            timeout=60
            pid=$(cat ${BOOT_PID})
            echo -e "\e[0;33mNOTE: killing pid=$pid of $JAR_NAME\e[m"
            kill ${pid}

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
                if [[ $(ps -ef | grep ${JAR_NAME} | grep -v grep | wc -l) == 0 ]]; then
                    echo -e "\e[0;33mNOTE: successfully stop in $i seconds, pid=$pid of $JAR_NAME\e[m"
                    exit
                fi
            done
            echo -e "\e[0;31mWARN: stopping timeout[${timeout}s], pid=$pid\e[m"
            echo -e "\e[0;31mWARN: need manually check the ${JAR_NAME}\e[m"
            ps -ef | grep ${JAR_NAME}| grep -v grep
        fi
        ;;

    status)
        if [[ ${count} == 0 ]]; then
            echo -e "\e[0;33mNOTE: not found running $JAR_NAME\e[m"
        else
            echo -e "\e[0;33mNOTE: $count running of $JAR_NAME\e[m"
            ps -ef | grep ${JAR_NAME}| grep -v grep
            echo -e "\e[0;33mNOTE: last 20 lines of $BOOT_LOG\e[m"
            tail -n 20 ${BOOT_LOG}
        fi
        ;;

    *)
        echo -e '\e[0;31mERROR: use start|stop|status\e[m'
        echo -e '\e[0;31meg ./wings-starter.sh start\e[m'
esac
