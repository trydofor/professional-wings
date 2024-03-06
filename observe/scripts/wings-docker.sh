#!/bin/bash -e
THIS_VERSION=2024-02-25

IMAGE_APP_ROOT=/app
IMAGE_APP_CONF="$IMAGE_APP_ROOT/conf"
IMAGE_APP_DATA="$IMAGE_APP_ROOT/data"
IMAGE_APP_LOGS="$IMAGE_APP_ROOT/logs"
IMAGE_APP_PORT=8080
IMAGE_APP_USER=nobody
IMAGE_ENV_JAVA_XMS="1G"
IMAGE_ENV_JAVA_XMX="3G"
IMAGE_ENV_JAVA_ADD="\
--add-modules=java.se \
--add-exports=java.base/jdk.internal.ref=ALL-UNNAMED \
--add-opens=java.base/java.io=ALL-UNNAMED \
--add-opens=java.base/java.lang.invoke=ALL-UNNAMED \
--add-opens=java.base/java.lang=ALL-UNNAMED \
--add-opens=java.base/java.net=ALL-UNNAMED \
--add-opens=java.base/java.nio=ALL-UNNAMED \
--add-opens=java.base/java.util=ALL-UNNAMED \
--add-opens=java.base/jdk.internal.ref=ALL-UNNAMED \
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
--add-opens=java.base/sun.security.x509=ALL-UNNAMED \
--add-opens=java.management/sun.management=ALL-UNNAMED \
--add-opens=jdk.management/com.sun.management.internal=ALL-UNNAMED \
--add-opens=jdk.unsupported/sun.misc=ALL-UNNAMED"

IMAGE_ENV_JAVA_OPTS="\
$IMAGE_ENV_JAVA_ADD \
-server \
-Djava.awt.headless=true -Dfile.encoding=UTF-8 \
-XX:MetaspaceSize=256M -XX:AutoBoxCacheMax=20000 \
-XX:MaxDirectMemorySize=1024M \
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$IMAGE_APP_LOGS/application.hprof \
-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=8 -XX:ConcGCThreads=8 \
-Xlog:gc*=info:file=$IMAGE_APP_LOGS/application.gc:time,tid,tags:filecount=5,filesize=100m  "

#######
function show_help() {
    echo -e '\033[32m clean <dep|app|all> <boot-jar> \033[m clean docker-<dep|app|all> build dir of boot-jar'
    echo -e '\033[32m print <dep|app|all> <boot-jar> \033[m print docker-<dep|app|all> Dockerfile of boot-jar'
    echo -e '\033[32m unzip <dep|app|all> <boot-jar> \033[m unzip docker-<dep|app|all> files of boot-jar'
    echo -e '\033[32m build <dep|app|all> <boot-jar> \033[m build docker-<dep|app|all> image of boot-jar'
    echo -e '\033[32m help \033[m show this'
    echo -e '\033[32m <dep> \033[m only dependencies + spring-boot-loader'
    echo -e '\033[32m <app> \033[m <dep> + napshot-dependencies + application'
    echo -e '\033[32m <all> \033[m <dep> + <app>'
}

function docker_file() {
    _cmd=(cat)
    if [ "$BUILD_MOD" == "dep" ]; then
        _cmd=(grep -v -E 'COPY snapshot-dependencies/|COPY application/')
    elif [ "$BUILD_MOD" == "app" ]; then
        _cmd=(grep -v -E 'COPY dependencies/|COPY spring-boot-loader/')
    fi

"${_cmd[@]}" << EOF
FROM $IMAGE_TAG_FROM

VOLUME $IMAGE_APP_CONF
VOLUME $IMAGE_APP_DATA
VOLUME $IMAGE_APP_LOGS

ENV JAVA_XMS="$IMAGE_ENV_JAVA_XMS"
ENV JAVA_XMX="$IMAGE_ENV_JAVA_XMX"
ENV JAVA_OPTS="$IMAGE_ENV_JAVA_OPTS"
ENV SPRING_APPLICATION_JSON="{\
\"server.port\":$IMAGE_APP_PORT,\
\"spring.config.location\":\"optional:file:$IMAGE_APP_CONF\",\
\"logging.file.name\":\"$IMAGE_APP_LOGS/application.log\"\
}"

WORKDIR $IMAGE_APP_ROOT
USER $IMAGE_APP_USER

COPY dependencies/ $IMAGE_APP_ROOT
COPY spring-boot-loader/ $IMAGE_APP_ROOT
COPY snapshot-dependencies/ $IMAGE_APP_ROOT
COPY application/ $IMAGE_APP_ROOT

EXPOSE $IMAGE_APP_PORT
EXPOSE 5701

ENTRYPOINT ["/bin/bash", "-c", "exec java -Xms\$JAVA_XMS -Xmx\$JAVA_XMX \$JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
EOF
}

#######
BUILD_ARG="$1"
echo -e "\033[37;42;1mScript-Version $THIS_VERSION \033[0m"

## help
if [[ "$BUILD_ARG" == "help" ]]; then
    show_help
    exit
fi

BUILD_MOD="$2"
if [[ "$BUILD_MOD" == "dep"  ||  "$BUILD_MOD" == "app"  ||  "$BUILD_MOD" == "all" ]]; then
    echo -e "\033[32m build mod=$BUILD_MOD \033[0m"
else
    echo -e "\033[31mERROR: bad arg2, $BUILD_MOD \033[0m"
    show_help
    exit
fi

## build
BUILD_BOOT_JAR="$3"
BUILD_WORK_DIR=
IMAGE_TAG_BOOT=
IMAGE_TAG_FROM="eclipse-temurin:21-jre"
if [[ -f "$BUILD_BOOT_JAR" ]]; then
    _this=$(realpath "$BUILD_BOOT_JAR")
    _temp=$(dirname "$_this")
    BUILD_WORK_DIR="$_temp/docker-$BUILD_MOD"
    IMAGE_TAG_BOOT=$(basename "$_this" | sed -E 's/\.[^.]*$//' | sed -E 's/-([0-9]+\.)/:\1/')  # winx-front:3.2.110-SNAPSHOT
    if [[ "$BUILD_MOD" == "dep" ]]; then
        IMAGE_TAG_BOOT="${IMAGE_TAG_BOOT}-DEP"
    elif [[ "$BUILD_MOD" == "app" ]]; then
        IMAGE_TAG_FROM="${IMAGE_TAG_BOOT}-DEP"
    fi
    echo -e "\033[32m work dir=$BUILD_WORK_DIR \033[0m"
    echo -e "\033[32m boot tag=$IMAGE_TAG_BOOT \033[0m"
    echo -e "\033[32m from tag=$IMAGE_TAG_FROM \033[0m"
else
    echo -e "\033[31mERROR: no boot-jar $BUILD_BOOT_JAR \033[0m"
    echo -e "\033[32m mvn clean package \033[0m to build"
    show_help
    exit
fi

case "$BUILD_ARG" in
    print)
        echo -e "\033[37;42;1mINFO: ==== Dockerfile ==== \033[0m"
        docker_file
        echo -e "\033[37;42;1mINFO: ==== docker run ==== \033[0m"
        echo "WINGS_DOCKER_NET=wings-app"
        echo "WINGS_DOCKER_OPTS=(--network \$WINGS_DOCKER_NET -e TZ=Asia/Shanghai -v ./data:/app/data -v ./conf:/app/conf -v ./logs:/app/logs -p 8091:8080)"
        echo "docker network create --driver bridge \$WINGS_DOCKER_NET"
        echo "docker run -it --rm \${WINGS_DOCKER_OPTS[@]} $IMAGE_TAG_BOOT"
        echo "docker run -it --rm \${WINGS_DOCKER_OPTS[@]} --entrypoint /bin/bash $IMAGE_TAG_BOOT"
        ;;
    clean)
        if [[ -d "$BUILD_WORK_DIR" ]];then
            echo -e "\033[32m clean $BUILD_WORK_DIR"
            rm -rf "$BUILD_WORK_DIR"
        else
            echo -e "\033[33mINFO: not found $BUILD_WORK_DIR \033[0m"
        fi
        ;;
    unzip)
        [[ -d "$BUILD_WORK_DIR" ]] && rm -rf "$BUILD_WORK_DIR"
        mkdir -p "$BUILD_WORK_DIR"
        echo -e "\033[32m unzip layered jar \033[0m"
        java -Djarmode=layertools -jar "$BUILD_BOOT_JAR" extract --destination "$BUILD_WORK_DIR"
        echo -e "\033[32m delete spring-boot-jarmode-layertools.jar \033[0m"
        find "$BUILD_WORK_DIR/dependencies" -name 'spring-boot-jarmode-layertools-*.jar' -delete
        ;;
    build)
        cd "$BUILD_WORK_DIR" || exit
        docker_file > Dockerfile
        echo -e "\033[32m build Dockerfile \033[0m"
        docker build -t "$IMAGE_TAG_BOOT" .
        ;;
    *)
        show_help
        ;;
esac
