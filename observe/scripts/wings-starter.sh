#!/bin/bash -e
THIS_VERSION=2024-03-05
################ system env to use ################
# JAVA_HOME      # if JDK_HOME is not valid
# JAVA_OPTS      # prepend to java args
# BOOT_OPTS      # prepend to spring-boot args
# BOOT_ENVF=''   # `*.env` file, load after env-link and env-file
# TZ             # java default timezone
# SPRING_APPLICATION_JSON # springboot default json properties
################ modify the following params ################
WORK_DIR=''      # directory of script-generated files and logs. default empty (script location)
TAIL_LOG='log'   # the log to tail, (log|out|new|ask)
USER_RUN=$USER   # the user to execute the script (ubuntu)
PORT_RUN=''      # default server port (10086)
ARGS_RUN='start' # default args (start) use `$1` if empty
BOOT_JAR=''      # path of boot jar (/data/boot/wings.jar), can be overridden by $1, absolute or relative to WORK_DIR
BOOT_OUT=''      # console output (/data/boot/wings.out), default $BOOT_JAR.out
BOOT_LOG=''      # app log (/data/logs/wings.log) specified externally, used for `tail`
BOOT_PID=''      # app pid (/data/logs/wings.pid) default $BOOT_JAR.pid
BOOT_CNF=''      # external config (/data/conf/wings/common/,/data/conf/wings/front/)
BOOT_ARG=''      # args of boot jar (--app.test=one) can lazily evaluate
JAVA_XMS='1G'    # args of JVM
JAVA_XMX='3G'    # args of JVM
WARN_TXT=''      # keyword of warning
WARN_AGO=''      # alert if the log is not updated for N seconds, empty for disable
WARN_RUN=''      # execute if the no PID or no log update
# shellcheck disable=SC2153
JDK_HOME='' # specified jdk version (/data/java/jdk-11.0.2)
# args of Java8, can lazily evaluate
# shellcheck disable=SC2016
JDK8_ARG='
-Xloggc:${BOOT_TKN}.gc
-XX:+PrintGC
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
'
# args of Java9+, can lazily evaluate
# shellcheck disable=SC2016
JDK9_ARG='
--add-modules=java.se
--add-exports=java.base/jdk.internal.ref=ALL-UNNAMED
--add-opens=java.base/java.io=ALL-UNNAMED
--add-opens=java.base/java.lang.invoke=ALL-UNNAMED
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.net=ALL-UNNAMED
--add-opens=java.base/java.nio=ALL-UNNAMED
--add-opens=java.base/java.util=ALL-UNNAMED
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED
--add-opens=java.base/sun.security.x509=ALL-UNNAMED
--add-opens=java.management/sun.management=ALL-UNNAMED
--add-opens=jdk.management/com.sun.management.internal=ALL-UNNAMED
--add-opens=jdk.unsupported/sun.misc=ALL-UNNAMED
-Xlog:gc*=info:file=${BOOT_TKN}.gc:time,tid,tags:filecount=5,filesize=100m
'
# args of JVM, can lazily evaluate
# shellcheck disable=SC2016
JAVA_ARG='
-server
-Djava.awt.headless=true
-Dfile.encoding=UTF-8
-Xms${JAVA_XMS}
-Xmx${JAVA_XMX}
-XX:MetaspaceSize=256M
-XX:AutoBoxCacheMax=20000
-XX:MaxDirectMemorySize=1024M
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${BOOT_TKN}.hprof
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:ParallelGCThreads=8
-XX:ConcGCThreads=8
'
# -XX:+ExitOnOutOfMemoryError #docker
TIME_ZID='' # java timezone (UTC|GMT+8|Asia/Shanghai)

# ext args
JAVA_EXT=''
BOOT_EXT=''

################ NO NEED to modify the following ################
BOOT_DTM=$(date '+%y%m%d%H%M%S') # datetime of boot
BOOT_TKN=''                      # boot token, Composed of jar+dtm
BOOT_MD5=''                      # execute md5+jar in safe mode
JAR_NAME=''                      # basename of boot-jar
#
function print_envs() {
    echo -e "#################################################"
    echo -e "# Version \033[32m$THIS_VERSION\033[m # for Mac&Lin / BusyBox&Bash"
    echo -e "# use 'ln -s' to link this script to the execution 'target/workdir',"
    echo -e "# the same basename '.env' (wings-release.env) will be auto loaded."
    echo -e "# only one boot.jar run on one host, rename it to run more copies."
    echo -e "# 'BOOT_ARG|JAVA_ARG|JDK8_ARG|JDK9_ARG' can be lazily evaluated"
    echo -e "# in evaluation, ' for delayed, \" for immediate. default Java 11 G1"
    echo -e "#################################################"
    echo -e "\033[37;42;1mINFO: ==== boot env ==== \033[0m"
    echo "work-dir=$WORK_DIR"
    echo "env-link=$link_envs"
    echo "env-file=$this_envs"
    # shellcheck disable=SC2153
    echo "env-boot=$BOOT_ENVF"
    echo "grep-key='$grep_key'"
}

function print_help() {
    echo
    echo -e '\033[32m docker \033[m start in docker with console log'
    echo -e '\033[32m start \033[m start the {boot-jar} and tail the log'
    echo -e '\033[32m starts \033[m start but Not wait log'
    echo -e '\033[32m stop [snd=30]\033[m stop the {boot-jar} gracefully in {snd} seconds'
    echo -e '\033[32m stops [snd=30]\033[m stop but Not confirm'
    echo -e '\033[32m status \033[m show the {boot-jar} runtime status'
    echo -e '\033[32m warn \033[m monitor the {boot-jar} and log'
    echo -e '\033[32m live \033[m monitor the {boot-jar} and auto restart if lost pid'
    echo -e '\033[32m clean [days=30] [y] \033[m clean up log-file {days} ago but newest'
    echo -e '\033[32m clean-jar [days=30] [y] \033[m clean up boot-jar {days} ago but newest'
    echo -e '\033[32m config \033[m print config envs'
    echo -e '\033[32m tail \033[m tail boot log or out'
    echo
    echo -e '\033[32m cron \033[m show the {boot-jar} crontab usage'
    echo -e '\033[32m free \033[m check memory free'
    echo -e '\033[32m check \033[m check shell command'
    echo -e '\033[32m help \033[m print help message'
    echo
    echo -e 'default is \033[37;43;1m start \033[m, for example'
    echo -e './wings-starter.sh'
    echo -e './wings-starter.sh status'
    echo -e './wings-starter.sh boot.jar start'
}

function print_args() {
    echo -e "\033[37;42;1mINFO: ==== boot arguments ==== \033[0m"
    echo "BOOT_JAR=$BOOT_JAR"
    echo "BOOT_MD5=$BOOT_MD5"
    echo "BOOT_TKN=$BOOT_TKN"
    echo "BOOT_PID=$BOOT_PID"
    echo "BOOT_LOG=$BOOT_LOG"
    echo "BOOT_OUT=$BOOT_OUT"
    echo "BOOT_ARG=$BOOT_ARG"
    echo -e "\033[37;42;1mINFO: ==== java arguments ==== \033[0m"
    echo "$JAVA_OPT"
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
    user=$(id -un) # no $USER in crontab, use id -un instead
    if [[ "$USER_RUN" != "$user" ]]; then
        echo -e "\033[37;41;1mERROR: need user $USER_RUN to run, but $user \033[0m"
        id -un
        exit
    fi
}

function check_java() {
    echo -e "\033[37;42;1mINFO: ==== java version ==== \033[0m"
    if ! java -version; then
        echo -e "\033[37;41;1mERROR: can not found 'java' in the $PATH \033[0m"
        exit
    fi

    if [[ $(java -help 2>&1 | grep -cF 'add-modules') -gt 0 ]]; then
        JAVA_OPT="$JDK9_ARG $JAVA_ARG"
    else
        JAVA_OPT="$JDK8_ARG $JAVA_ARG"
    fi

    if [[ "$TIME_ZID" != "" ]]; then
        JAVA_OPT="$JAVA_OPT -Duser.timezone=$TIME_ZID"
    fi

    # shellcheck disable=SC2153
    JAVA_OPT="$JAVA_OPTS $JAVA_OPT $JAVA_EXT"
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

function safe_start() {
    # safe backup
    md5sum "$BOOT_JAR" >"$file_md5"
    BOOT_MD5=$(awk '{print $1}' <"$file_md5")
    # `_` as delimiter
    safe_jar="${BOOT_JAR}_${BOOT_MD5}"
    if [[ ! -f "$safe_jar" ]]; then
        echo -e "\033[33mNOTE: copy safe_jar  $safe_jar \033[0m"
        # do Not use link (soft and hard), as it can overwrite source
        cp "$BOOT_JAR" "$safe_jar"
    fi

    #
    print_args
    touch "$BOOT_OUT"
    real_out=$(realpath "$BOOT_OUT")
    # shellcheck disable=SC2086
    nohup java $JAVA_OPT -Dwings.console.out=$real_out -jar $safe_jar $BOOT_ARG >$BOOT_OUT 2>&1 &
    echo "$! ${BOOT_TKN}" >"$BOOT_PID"
    sleep 2
}

################ script body ################
# load env
this_file="$0"
link_envs=""
if [[ -L "$this_file" ]]; then
    link_file=$(realpath "$this_file")
    link_envs=${link_file%.*}.env
    if [[ -f "$link_envs" ]]; then
        echo -e "\033[37;42;1mINFO: load link-envs form $link_envs ==== \033[0m"
        # shellcheck disable=SC1090
        source "$link_envs"
    fi
fi

this_envs=${this_file%.*}.env
if [[ -f "$this_envs" ]]; then
    echo -e "\033[37;42;1mINFO: load this-envs form $this_envs ==== \033[0m"
    # shellcheck disable=SC1090
    source "$this_envs"
else
    echo -e "\033[31mWARN: no env file found. $this_envs \033[0m"
fi

if [[ -f "$BOOT_ENVF" ]]; then
    echo -e "\033[37;42;1mINFO: load boot-envs form $BOOT_ENVF ==== \033[0m"
    # shellcheck disable=SC1090
    source "$BOOT_ENVF"
fi

# change workdir
if [[ "$WORK_DIR" == "" ]]; then
    WORK_DIR=$(dirname "$this_file")
else
    # shellcheck disable=SC2164,SC2046
    cd $(dirname "$this_file")
fi
cd "$WORK_DIR" || exit
WORK_DIR=$(realpath -s .)

# check arg
if [[ -L "$1" || -f "$1" ]]; then
    BOOT_JAR="$1"
    shift
fi
if [[ "$1" != "" ]]; then
    ARGS_RUN="$1"
fi

# command without check boot
case "$ARGS_RUN" in
    cron)
        this_path=$(realpath -s "$this_file")
        echo -e "\033[37;43;1mNOTE: ==== crontab usage ==== \033[0m"
        echo -e "\033[32m crontab -e -u $USER_RUN \033[m"
        echo -e "\033[32m crontab -l -u $USER_RUN \033[m"
        echo -e "\033[32m */5 * * * * $this_path warn \033[m"
        echo -e "\033[32m */5 * * * * $this_path live >> $this_path.cron \033[m"
        echo -e "\033[32m 0 0 * * * $this_path clean 30 y \033[m"
        exit
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
        exit
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
        check_cmd id
        check_cmd java
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
        check_cmd timeout
        check_cmd touch
        check_cmd tr
        check_cmd wc
        check_cmd which
        check_cmd xargs
        exit
        ;;
    help)
        echo -e '\033[37;42;1mNOTE: help info, use the following\033[m'
        print_help
        print_envs
        exit
        ;;
esac

# check boot jar
check_boot
JAR_NAME=$(basename "$BOOT_JAR")

# boot pid
if [[ "$BOOT_PID" == "" ]]; then
    BOOT_PID="${JAR_NAME}.pid"
fi

# boot tkn `-` as delimiter
BOOT_TKN="${JAR_NAME}-${BOOT_DTM}"
if [[ "$ARGS_RUN" != "start" && -f "$BOOT_PID" ]]; then
    old_tkn=$(awk '{print $2}' "$BOOT_PID")
    if [[ "$old_tkn" != "" ]]; then
        BOOT_TKN=$old_tkn
    fi
fi

# boot out
if [[ "$BOOT_OUT" == "" ]]; then
    BOOT_OUT="${BOOT_TKN}.out"
fi

# boot md5
file_md5="${JAR_NAME}.md5"
if [[ -f "$file_md5" ]]; then
    BOOT_MD5=$(awk '{print $1}' <"$file_md5")
fi

# java home & path
if [[ "$JDK_HOME" != "" && "$JDK_HOME" != "$JAVA_HOME" ]]; then
    PATH=$JDK_HOME/bin:$PATH
    JAVA_HOME=$JDK_HOME
    echo -e "\033[37;42;1mINFO: ==== JAVA_HOME=$JAVA_HOME ==== \033[0m"
fi

# lazy env eval
BOOT_ARG=$(eval "echo \"$BOOT_ARG\"")
JAVA_ARG=$(eval "echo \"$JAVA_ARG\"")
JDK8_ARG=$(eval "echo \"$JDK8_ARG\"")
JDK9_ARG=$(eval "echo \"$JDK9_ARG\"")

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

# shellcheck disable=SC2153
BOOT_ARG="$BOOT_OPTS $BOOT_ARG $BOOT_EXT"

# check ps
grep_key=" -jar ${BOOT_JAR}[ _-]"
count=$(pgrep -f "$grep_key" | wc -l)

# exec cmd
case "$ARGS_RUN" in
    docker)
        print_envs
        check_java
        print_args
        if [[ $count -eq 0 ]]; then
            # shellcheck disable=SC2086
            java $JAVA_OPT -jar $BOOT_JAR $BOOT_ARG 2>&1
        else
            echo -e "\033[37;41;1mERROR: has $count running $BOOT_JAR \033[0m"
            pgrep -alf "$grep_key"
        fi
        ;;
    start*)
        print_envs
        check_java

        if [[ $count -ne 0 ]]; then
            print_args
            echo -e "\033[37;41;1mERROR: has $count running $BOOT_JAR \033[0m"
            pgrep -alf "$grep_key"
            exit
        fi

        check_user
        safe_start

        cid=$(pgrep -f "$grep_key" | tr '\n' ' ')
        if [[ "$cid" == "" ]]; then
            echo -e "\033[37;41;1mERROR: failed to check PID by $BOOT_JAR \033[0m"
            cat "$BOOT_OUT"
            exit
        else
            echo -e "\033[37;42;1mINFO: current PID=$cid of $BOOT_JAR \033[0m"
            # shellcheck disable=SC2086
            ps -fwww $cid
        fi

        if [[ "$ARGS_RUN" != "start" ]]; then
            echo -e "\033[37;42;1mINFO: tail -f $BOOT_OUT \033[0m"
            timeout -s 9 10 tail -f "$BOOT_OUT"
            echo -e "\033[37;43;1m====== ${BOOT_JAR//?/=} ======\033[0m"
            echo -e "\033[37;43;1m====== $BOOT_JAR ======\033[0m"
            echo -e "\033[37;43;1m====== ${BOOT_JAR//?/=} ======\033[0m"
            exit
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
            echo -e "\033[37;42;1mINFO: tail -f $tail_log, Ctrl-C to break \033[0m"
            tail -f "$tail_log"
        fi
        ;;
    stop*)
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

        pid=$(awk '{print $1}' "$BOOT_PID")
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

        bars=''
        for ((i = 0; i < timeout; i++)); do
            for ((j = 0; j < 10; j++)); do
                printf "[%ds][%-60s]\r" "$i" "$bars"
                if [[ ${#bars} -ge 60 ]]; then
                    bars=''
                else
                    bars='#'${bars}
                fi
                sleep 0.1
            done

            # shellcheck disable=SC2086
            if ! ps $pid >/dev/null; then
                echo ""
                echo -e "\033[33mNOTE: successfully stop in $i seconds, pid=$pid of $BOOT_JAR \033[0m"
                rm -rf "$BOOT_PID"
                exit
            fi
        done
        echo -e "\033[37;41;1mWARN: timeout[${timeout}s] and kill -9 $pid \033[0m"
        if [[ "$ARGS_RUN" == "stop" ]]; then
            echo -e "\033[33mNOTE: <ENTER> to kill, <Ctrl-C> to exit \033[0m"
            read -r
        fi
        # shellcheck disable=SC2086
        kill -9 $pid
        rm -rf "$BOOT_PID"
        ;;
    status)
        print_envs
        if [[ $count -eq 0 ]]; then
            echo -e "\033[37;41;1mERROR: not running $BOOT_JAR \033[0m"
            exit
        fi

        check_java
        print_args

        tail_num=20
        if [[ -f "$BOOT_OUT" ]]; then
            echo -e "\033[37;42;1mINFO: tail -n $tail_num $BOOT_OUT \033[0m"
            tail -n $tail_num "$BOOT_OUT"
        fi
        if [[ -f "$BOOT_LOG" ]]; then
            echo -e "\033[37;42;1mINFO: tail -n $tail_num $BOOT_LOG \033[0m"
            tail -n $tail_num "$BOOT_LOG"
        fi
        pid=$(awk '{print $1}' "$BOOT_PID")
        cid=$(pgrep -f "$grep_key")
        echo -e "\033[37;42;1mINFO: boot.pid=$pid \033[0m"
        echo -e "\033[32m current PID=$cid of $BOOT_JAR \033[0m"
        ps -fwww "$cid" || exit

        if [[ $pid -ne $cid ]]; then
            echo -e "\033[31mWARN: pid not match, proc-pid=$cid, file-pid=$pid \033[0m"
        fi

        # shellcheck disable=SC2009
        mrs=$(ps -o rss "$cid" | grep -v RSS | numfmt --grouping)
        # shellcheck disable=SC2009
        mvs=$(ps -o vsz "$cid" | grep -v VSZ | numfmt --grouping)
        echo -e "\033[37;42;1mINFO: ps -o rss -o vsz $cid \033[0m"
        echo -e "Resident (RSS) = $(printf '%*s' 12 $mrs) Kb"
        echo -e "Virtual  (VSZ) = $(printf '%*s' 12 $mvs) Kb"

        if [[ "$USER_RUN" == "$USER" ]]; then
            echo -e "\033[37;42;1mINFO: $(which jstat) -gcutil $cid 1000 3 \033[0m"
            jstat -gcutil "$cid" 1000 3
            echo -e "\033[37;42;1mINFO: $(which jstat) -gc $cid 1000 3 \033[0m"
            jstat -gc "$cid" 1000 3
        else
            echo -e "\033[37;43;1mNOTE: sudo $(which jstat) -gcutil $cid 1000 3 \033[0m"
            echo -e "\033[37;43;1mNOTE: sudo $(which jstat) -gc $cid 1000 3 \033[0m"
        fi


        if id | grep -q '(sudo)'; then
            if which jhsdb &> /dev/null; then
                echo -e "\033[37;42;1mINFO: sudo $(which jhsdb) jmap --heap --pid $cid \033[m"
                sudo jhsdb jmap --heap --pid "$cid"
            else
                echo -e "\033[37;42;1mINFO: sudo $(which jmap) -heap $cid \033[m"
                sudo jmap -heap "$cid"
            fi
        else
            if which jhsdb &> /dev/null; then
                echo -e "\033[37;43;1mNOTE: sudo $(which jhsdb) jmap --heap --pid $cid \033[m"
            else
                echo -e "\033[37;43;1mNOTE: sudo $(which jmap) -heap $cid \033[m"
            fi
        fi

        echo -e "\033[37;43;1mNOTE: ==== useful tool ==== \033[0m"
        echo -e "\033[32m profiler.sh -d 30 -f profile.svg $cid \033[m https://github.com/jvm-profiling-tools/async-profiler"
        echo -e "\033[32m $(which java) -jar arthas-boot.jar $cid \033[m https://github.com/alibaba/arthas"
        ;;
    tail)
        file_log=$BOOT_LOG
        if [[ ! -f "$file_log" ]]; then
            file_log=$BOOT_OUT
        fi
        if [[ ! -f "$file_log" ]]; then
            echo -e "\033[37;41;1mERROR: no log found \033[0m"
            exit
        fi
        tail_num=20
        echo -e "\033[37;42;1mINFO: tail -f -n $tail_num $file_log \033[0m"
        tail -f -n $tail_num "$file_log"
        ;;
    warn)
        warn_got=''
        if [[ $count -eq 0 ]]; then
            echo -e "\033[37;41;1mWARN: not running $BOOT_JAR \033[0m"
            WARN_TXT="$WARN_TXT \\nPID not found"
            warn_got="pid"
        fi

        if [[ "$WARN_AGO" != "" ]]; then
            log_time=$(date +%s -r "$BOOT_LOG")
            ago_time=$(date +%s -d "now -$WARN_AGO second")
            if [[ log_time -lt ago_time ]]; then
                echo -e "\033[37;41;1mWARN: no update in $WARN_AGO seconds, log $BOOT_LOG \033[0m"
                WARN_TXT="$WARN_TXT \\nLOG not updated"
                warn_got="log"
            fi
        fi

        if [[ "$warn_got" != "" ]]; then
            if [[ "$WARN_RUN" == "" ]]; then
                echo -e "\033[33mNOTE: skip notice for empty WARN_RUN \033[0m"
            else
                echo "$WARN_RUN"
                eval "$WARN_RUN"
                echo
                echo -e "\033[33mNOTE: sent warn notice \033[0m"
            fi
        else
            echo -e "\033[37;42;1mNOTE: $BOOT_JAR good status in PID and LOG \033[0m"
        fi
        ;;
    live)
        if [[ $count -ne 0 ]]; then
            echo -e "\033[33mNOTE: skip $count running $BOOT_JAR \033[0m"
            exit
        fi
        if [[ ! -f "$BOOT_PID" ]]; then
            echo -e "\033[33mNOTE: skip manually stopped $BOOT_JAR \033[0m"
            exit
        fi

        check_java
        check_user
        safe_start
        ;;
    clean)
        dys="$2"
        if [[ "$dys" == "" ]]; then
            dys=30
        fi
        nwt=5
        echo -e "\033[32m top log ${nwt}-newest ${JAR_NAME} \033[m"
        # shellcheck disable=SC2012
        ls -lt "./${JAR_NAME}"-* | head -n $nwt
        echo -e "\033[32m find $(pwd) -name \"${JAR_NAME}-*\" -type f -mtime +$dys \033[m"
        old=$(find . -name "${JAR_NAME}-*" -type f -mtime +$dys | wc -l)
        if [[ $old -gt 10 ]]; then
            exs="newest-log-${JAR_NAME}.tmp"
            # shellcheck disable=SC2012
            ls -t "./${JAR_NAME}"-* | head -n $nwt >"$exs"

            find . -name "${JAR_NAME}-*" -type f -mtime +$dys -print0 | grep -zvFf "$exs" | xargs -0 ls -lt
            echo -e "\033[37;43;1mNOTE: ==== clear ${dys}-days ago log file ==== \033[0m"
            check_user

            yon="$3"
            if [[ "$3" == "" ]]; then
                echo -e "\033[31mWARN: press <y> to rm them all, pwd=${WORK_DIR} \033[0m"
                read -r yon
            fi
            if [[ "$yon" == "y" ]]; then
                find . -name "${JAR_NAME}-*" -type f -mtime +$dys -print0 | grep -zvFf "$exs" | xargs -0 rm -f
            fi
            rm -f "$exs"
        else
            echo -e "\033[37;42;1mNOTE: few ${dys}-days ago logs, pwd=${WORK_DIR} \033[0m"
        fi
        ;;
    clean-jar)
        dys="$2"
        if [[ "$dys" == "" ]]; then
            dys=30
        fi
        jrt=$(dirname "$BOOT_JAR")
        nwt=5
        echo -e "\033[32m top jar ${nwt}-newest ${JAR_NAME} \033[m"
        # shellcheck disable=SC2012
        ls -lt "${jrt}/${JAR_NAME}"[_-]* | head -n $nwt
        echo -e "\033[32m find $jrt -name \"${JAR_NAME}[_-]*\" -type f -mtime +$dys \033[m"
        old=$(find "$jrt" -name "${JAR_NAME}[_-]*" -type f -mtime +$dys | wc -l)
        if [[ $old -gt 10 ]]; then
            exs="newest-jar-${JAR_NAME}.tmp"
            # shellcheck disable=SC2012
            ls -t "${jrt}/${JAR_NAME}"[_-]* | head -n $nwt >"$exs"

            find "$jrt" -name "${JAR_NAME}[_-]*" -type f -mtime +$dys -print0 | grep -zvFf "$exs" | xargs -0 ls -lt
            echo -e "\033[37;43;1mNOTE: ==== clear ${dys}-days ago jar, exclude top ${nwt}-newest ==== \033[0m"
            check_user

            yon="$3"
            if [[ "$3" == "" ]]; then
                echo -e "\033[31mWARN: press <y> to rm them all, pwd=$jrt \033[0m"
                read -r yon
            fi
            if [[ "$yon" == "y" ]]; then
                find "$jrt" -name "${JAR_NAME}[_-]*" -type f -mtime +$dys -print0 | grep -zvFf "$exs" | xargs -0 rm -f
            fi
            rm -f "$exs"
        else
            echo -e "\033[37;42;1mNOTE: few ${dys}-days ago jars, pwd=$jrt \033[0m"
        fi
        ;;
    config)
        echo -e '\033[37;42;1mNOTE: ==== conf env ==== \033[m'
        echo "WORK_DIR=$WORK_DIR"
        echo "TAIL_LOG=$TAIL_LOG"
        echo "USER_RUN=$USER_RUN"
        echo "PORT_RUN=$PORT_RUN"
        echo "ARGS_RUN=$ARGS_RUN"
        echo "BOOT_JAR=$BOOT_JAR"
        echo "BOOT_OUT=$BOOT_OUT"
        echo "BOOT_LOG=$BOOT_LOG"
        echo "BOOT_PID=$BOOT_PID"
        echo "BOOT_CNF=$BOOT_CNF"
        echo "BOOT_ARG=$BOOT_ARG"
        echo "JAVA_XMS=$JAVA_XMS"
        echo "JAVA_XMX=$JAVA_XMX"
        echo "JDK_HOME=$JDK_HOME"
        echo "JDK8_ARG=$JDK8_ARG"
        echo "JDK9_ARG=$JDK9_ARG"
        echo "JAVA_ARG=$JAVA_ARG"
        echo "TIME_ZID=$TIME_ZID"
        echo "WARN_TXT=$WARN_TXT"
        echo "WARN_AGO=$WARN_AGO"
        echo "WARN_RUN=$WARN_RUN"
        ;;
    *)
        echo -e '\033[37;41;1mERROR: unsupported command, use the following\033[m'
        print_help
        print_envs
        ;;
esac
