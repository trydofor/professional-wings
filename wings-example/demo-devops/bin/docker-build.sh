#!/bin/bash
TEMP_DIR="../target" # 避免复制，建议在同一硬盘分区
BOOT_JAR="../target/demo-devops-*-SNAPSHOT.jar"
BOOT_ENV="./wings-starter.env"
BOOT_BSH="./wings-starter.sh"

DOCK_DIR="/opt/"
DOCK_TAG="wings/demo-devops"

####
if [[ "$1" == "help" ]]; then
    echo -e '\033[32m default print Dockerfile\033[m'
    echo -e '\033[32m build \033[m docker build image'
    echo -e '\033[32m help \033[m show this'
    exit
fi

####
function link_file() {
    dir_to=$1
    lnk_it=$2
    cmd_it=$3
    if [[ -f "$lnk_it" ]]; then
        name_file=$(basename "$lnk_it")
        if [[ "$cmd_it" == "env" ]]; then
            nme_it=$(basename "$BOOT_JAR")
            # shellcheck disable=SC2002
            cat "$lnk_it" |
                sed -E "s/WORK_DIR=.+/WORK_DIR=''/g" |
                sed -E "s/BOOT_JAR=.+/BOOT_JAR='$nme_it'/g" \
                    >"$dir_to/$name_file"
            echo "copy edited $lnk_it"
            return
        fi

        echo "hard linked $lnk_it"
        if ln "$lnk_it" "$dir_to/$name_file"; then
            return
        fi
        echo "copy file or exit then change TEMP_DIR [y/n]"
        read -r yon
        if [[ "$yon" != "y" ]]; then
            echo "change TEMP_DIR and files in same disk partition"
            exit
        fi
        copy "$lnk_it" "$dir_to/$name_file"
    elif [[ "$cmd_it" != "end" ]]; then
        frm=$(dirname "$lnk_it")
        tkn=$(basename "$lnk_it")

        cnt=$(find "$frm" -name "$tkn" | wc -l)
        if [[ $cnt -ne 1 ]]; then
            find "$frm" -name "$tkn"
            echo -e "\033[37;41;1mERROR: found $cnt file, $lnk_it \033[0m"
            exit
        fi
        arg=$(find "$frm" -name "$tkn")
        link_file "$dir_to" "$arg" end
    else
        echo -e "\033[37;41;1mERROR: can not found file, $lnk_it \033[0m"
        exit
    fi
}

tmp_dir=$TEMP_DIR/dockbuild-$(date '+%y%m%d%H%M%S')
mkdir -p "$tmp_dir"

link_file "$tmp_dir" "$BOOT_JAR"
link_file "$tmp_dir" "$BOOT_ENV" env
link_file "$tmp_dir" "$BOOT_BSH"

cd "$tmp_dir" || exit

echo "temp-dir=$tmp_dir"
echo -e "\033[37;42;1m ==== Dockerfile ==== \033[0m"

tee Dockerfile <<EOF
FROM openjdk:8-jdk

EXPOSE 80
VOLUME /data
VOLUME /tmp

COPY ./* $DOCK_DIR
RUN chmod +x $DOCK_DIR/*.sh

WORKDIR $DOCK_DIR
# need bash to run
ENTRYPOINT ["/opt/wings-starter.sh", "docker"]
EOF

ls -al

if [[ "$1" == "build" ]]; then
    docker build -t "$DOCK_TAG" .
fi

rm -rf "$tmp_dir"
echo -e "\033[37;42;1m ==== Other Message ==== \033[0m"
cat <<EOF
docker run -it --rm $DOCK_TAG
docker run -it --rm --entrypoint /bin/sh $DOCK_TAG
EOF
