#!/bin/bash
THIS_VERSION=2022-01-22

TEMP_DIR="../../example/winx-devops/target" # To avoid copy, recommend the same partition on the hard disk.
BOOT_JAR="../../example/winx-devops/target/winx-devops-*-SNAPSHOT.jar"
BOOT_ENV="./wings-starter.env"
BOOT_BSH="./wings-starter.sh"

DOCK_DIR="/opt/"
DOCK_TAG="wings/winx-devops"

####
function show_help() {
    echo -e '\033[32m default print Dockerfile\033[m'
    echo -e '\033[32m clean \033[m clean docker temp dir'
    echo -e '\033[32m build \033[m docker build image'
    echo -e '\033[32m help \033[m show this'
}

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

        if ln "$lnk_it" "$dir_to/$name_file"; then
            echo "hard linked $lnk_it"
            return
        fi
        echo -e "\033[31mWARN: failed to link file, need to copy it \033[0m copy or exit [y/n]"
        read -r yon
        if [[ "$yon" != "y" ]]; then
            echo "change TEMP_DIR and files in same disk partition"
            exit
        fi
        cp "$lnk_it" "$dir_to/$name_file"
    elif [[ "$cmd_it" != "end" ]]; then
        frm=$(dirname "$lnk_it")
        tkn=$(basename "$lnk_it")

        cnt=$(find "$frm" -name "$tkn" | wc -l)
        if [[ $cnt -ne 1 ]]; then
            find "$frm" -name "$tkn"
            echo -e "\033[37;41;1mERROR: found $cnt file, $lnk_it \033[0m should clean"
            exit
        fi
        arg=$(find "$frm" -name "$tkn")
        link_file "$dir_to" "$arg" end
    else
        echo -e "\033[37;41;1mERROR: can not found file, $lnk_it \033[0m"
        exit
    fi
}

####
echo -e "\033[37;42;1mScript-Version $THIS_VERSION \033[0m"
if [[ "$1" == "help" ]]; then
    show_help
    exit
fi

tmp_pre="$TEMP_DIR/docker"
tmp_dir=$tmp_pre-$(date '+%y%m%d%H%M%S')
mkdir -p "$tmp_dir"

if [[ "$1" == "clean" ]]; then
    echo "clean tempdir $tmp_pre-[0-9]*"
    rm -rf $tmp_pre-[0-9]*
    exit
fi

link_file "$tmp_dir" "$BOOT_JAR"
link_file "$tmp_dir" "$BOOT_ENV" env
link_file "$tmp_dir" "$BOOT_BSH"

cd "$tmp_dir" || exit

echo "temp-dir=$tmp_dir"
echo -e "\033[37;42;1m ==== Dockerfile ==== \033[0m"

tee Dockerfile <<EOF
FROM openjdk:11-jdk

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
(cd "$tmp_dir" && docker build -t "$DOCK_TAG" .)
docker run -it --rm $DOCK_TAG
docker run -it --rm --entrypoint /bin/sh $DOCK_TAG
# use help as param1 to see help
EOF
