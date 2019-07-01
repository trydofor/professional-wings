#!/bin/bash
################ modify the following 2 params ################
BOOT_JAR=wings-example-1.0.0-SNAPSHOT.jar
JAVA_ARG="
-server
-Xms1536m
-Xmx1536m
-Duser.timezone=UTC
-Djava.awt.headless=true
-Dfile.encoding=UTF-8
-XX:NewSize=256m
-XX:MaxNewSize=256m
-XX:+UseConcMarkSweepGC
-XX:+CMSClassUnloadingEnabled
-XX:MaxTenuringThreshold=5
-XX:+HeapDumpOnOutOfMemoryError
-XX:-OmitStackTraceInFastThrow
-XX:HeapDumpPath=${BOOT_JAR}.heap
-Xloggc:${BOOT_JAR}.gc
-XX:+PrintGC
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
"
################ NO NEED to modify the following ################

# check java
echo -e "\e[0;32mINFO: ==== java ver ==== \e[m "
java -version
if [[ "$?" != "0" ]]; then
    echo -e '\e[0;31mERROR: can not found `java` in the $PATH\e[m'
    exit
fi

# cd script dir
cd $(dirname $(readlink -f $0))
echo -e "\e[0;32mINFO: ==== work dir ==== \e[m "
pwd

# search jar file
JAR_FILE=$(find -type f -name ${BOOT_JAR}|head -n 1)
JAR_NAME=$(basename ${JAR_FILE})

if [[ "$JAR_NAME" == "" ]]; then
    echo -e '\e[0;31mERROR: can not found jar file\e[m'
    exit
fi

LOG_FILE=${JAR_NAME}.log
PID_FILE=${JAR_NAME}.pid

count=$(ps -ef | grep ${JAR_NAME}| grep -v grep|wc -l)
case "$1" in
    start)
        if [[ ${count} == 0 ]]; then
            echo -e "\e[0;32mINFO: boot-jar=$JAR_FILE \e[m "
            echo -e "\e[0;33mNOTE: log-file=$LOG_FILE \e[m "
            echo -e "\e[0;33mNOTE: pid-file=$PID_FILE \e[m "
            nohup java ${JAVA_ARG} -jar ${JAR_FILE} > ${LOG_FILE} 2>&1 &
            echo $! > ${PID_FILE}
        else
            echo -e "\e[0;31mERROR: already $count running of $JAR_NAME\e[m "
        fi
        ps -ef | grep ${JAR_NAME}| grep -v grep
        ;;
    stop)
        if [[ ${count} == 0 ]]; then
            echo -e "\e[0;33mNOTE: not found running $JAR_NAME\e[m "
        else
            pid=$(cat ${PID_FILE})
            echo -e "\e[0;33mNOTE: killing pid=$pid of $JAR_NAME\e[m "
            kill ${pid}
            ps -ef | grep ${JAR_NAME}| grep -v grep
        fi
        ;;
    status)
        if [[ ${count} == 0 ]]; then
            echo -e "\e[0;33mNOTE: not found running $JAR_NAME\e[m "
        else
            echo -e "\e[0;33mNOTE: $count running of $JAR_NAME\e[m "
            ps -ef | grep ${JAR_NAME}| grep -v grep
        fi
        ;;
    *)
        echo -e '\e[0;31mERROR: use start|stop|status\e[m'
esac
