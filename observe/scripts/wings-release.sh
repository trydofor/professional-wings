#!/bin/bash -e
THIS_VERSION=2024-09-09

cat <<EOF
#################################################
# Version $THIS_VERSION # for Mac&Lin
# use 'ln -s' to link this script to the execution 'target/workdir', and,
# the same basename '.env' (wings-release.env) will be auto loaded.
# e.g. ln -s /data/wings-script/wings-release.sh my-release.sh"
#    my-release.sh -> /data/wings-script/wings-release.sh"
#    my-release.env"
# If PACK_JAR is directory, SUB_FLAT determines the overwrite behavior.
#################################################
EOF
################ modify the following params ################
USER_RUN="$USER"                                   # the user to execute the script
WORK_DIR=''                                        # work dir, ie. project dir
DEST_DIR=''                                        # target list, local or remote directory [user@]host:[path] or scp://[user@]host[:port][/path]
PACK_JAR='*.jar'                                   # list of packaged files or directories
SUB_FLAT=true                                      # if the target is a directory, transfer its contents or the entire directory
SCP_ARGS=''                                        # common scp arguments
PRE_PACK=''                                        # command to execute before `pack`
PRE_PUSH=''                                        # command to execute before `push`, support for `$_JAR` variable
MVN_PACK='-U -Dmaven.test.skip=true clean compile package' # mvn package
JDK_HOME=''                                        # mvn jdk version
WEB_PACK='build'                                   # package command of web

################ NO NEED to modify the following ################
function check_cmd() {
    if ! command -v "$1" >/dev/null; then
        # rc or init script
        if [[ -f "$2" ]]; then
            # shellcheck disable=SC1090
            source "$2"
        elif [[ "$2" != "" ]]; then
            echo -e "\033[33mWARN: no command=$1, no script=$2 \033[0m"
            return 1
        else
            echo -e "\033[31mERROR: no command=$1 \033[0m"
            exit
        fi
    fi
}

function _pre_pack() {
    if [[ "$PRE_PACK" != "" ]]; then
        _pcm=$(eval "echo \"$PRE_PACK\"")
        echo -e "\033[37;42;1m ==== PRE_PACK $_pcm \033[0m"
        if ! eval "$_pcm"; then
            echo -e "\033[31mERROR: failed PRE_PACK \033[0m"
            exit
        fi
    fi
}

function _pre_push() {
    if [[ "$PRE_PUSH" != "" ]]; then
        _JAR=$1
        _pcm=$(eval "echo \"$PRE_PUSH\"")
        echo -e "\033[37;42;1m ==== PRE_PUSH $_pcm \033[0m"
        if ! eval "$_pcm"; then
            echo -e "\033[31mERROR: failed PRE_PUSH $_JAR \033[0m"
            exit
        fi
    fi
}

function build_mvn() {
    # java home & path
    if [[ "$JDK_HOME" != "" && "$JDK_HOME" != "$JAVA_HOME" ]]; then
        PATH=$JDK_HOME/bin:$PATH
        JAVA_HOME=$JDK_HOME
        echo -e "\033[37;42;1mINFO: ==== JAVA_HOME=$JAVA_HOME ==== \033[0m"
    fi

    check_cmd mvn
    check_cmd git

    echo -e "\033[37;42;1m ==== Compile $WORK_DIR ==== \033[0m"
    _pre_pack

    _git_log="git-log.txt"
    _res_log="git-log.tmp"
    echo '#Generated by Wings Release Script' >$_git_log
    git branch -v >>$_git_log
    git log --pretty=format:'%H - %an, %ad %d : %s' --graph -10 >>$_git_log
    echo >>$_git_log
    find . -type d -name 'resources' | grep '/src/main/' | while read -r res; do
        echo "$res/$_git_log" >>$_res_log
        cp $_git_log "$res/"
    done

    echo -e "\033[37;42;1m ==== Package $WORK_DIR ==== \033[0m"
    # shellcheck disable=SC2086
    mvn $MVN_PACK

    echo -e "\033[37;42;1m ==== Git $WORK_DIR ==== \033[0m"
    cat "$_git_log"
    echo -e "\033[32m ==== $_git_log ==== \033[0m"
    cat "$_res_log"

    while read -r res; do
       [[ -f "$res" ]] && rm -f "$res"
    done < "$_res_log"
    rm -f "$_git_log" "$_res_log"

    echo -e "\033[32m ==== status ==== \033[0m"
    git status

    echo -e "\033[37;42;1m ==== Mvn version ==== \033[0m"
    mvn --version
}

function build_web() {
    # nvm
    if [[ -f ".nvmrc" ]]; then
        check_cmd nvm "$HOME/.nvm/nvm.sh" &&  nvm use
    fi

    _pre_pack

    _cmd=$1
    if [[ "$_cmd" == "" ]]; then
        if [[ -f "pnpm-lock.yaml" ]]; then
            _cmd=pnpm
        elif [[ -f "yarn.lock" ]]; then
            _cmd=yarn
        elif [[ -f "package-lock.json" ]]; then
            _cmd=npm
        fi
    fi

    # build
    echo -e "\033[32m web pack $_cmd \033[0m"
    if [[ "$_cmd" == "pnpm" ]]; then
        pnpm install
        pnpm $WEB_PACK
    elif [[ "$_cmd" == "yarn" ]]; then
        yarn install
        yarn $WEB_PACK
    elif [[ "$_cmd" == "npm" ]]; then
        npm install
        npm run $WEB_PACK
    else
        echo -e "\033[31mWARN: skip unknown command $_cmd \033[0m"
    fi

    # build hash
    echo -e "\033[37;42;1m ==== BuildInfo $WORK_DIR ==== \033[0m"
    check_cmd git
    _inf="build-info.js"
    _gid=$(git show --quiet --format="%H")
    _udt=$(date -u +'%Y-%m-%d %H:%M:%S')
    for _jar in $PACK_JAR; do
        if [[ -d "$_jar" ]]; then
            echo "{\"gitid\":\"$_gid\",\"build\":\"$_udt\"}" > "$_jar/$_inf"
        else
            echo "skip to make $_inf to $_jar"
        fi
    done

    echo -e "\033[32m ==== node version ==== \033[0m"
    node --version
    echo -e "\033[32m ==== $_cmd version ==== \033[0m"
    $_cmd --version
}

function build_auto() {

    # only pre
    if [[ "$1" == "pre" ]]; then
        _pre_pack
        exit
    fi

    ## https://asdf-vm.com
    if [[ -f ".tool-versions" ]]; then
        check_cmd asdf "$HOME/.asdf/asdf.sh" && asdf install
    fi

    # mvn
    if [[ -f "pom.xml" || "$1" == "mvn" ]]; then
        build_mvn
        return
    fi
    # web
    if [[ -f "package.json" ]]; then
        build_web "$1"
        return
    fi

    echo -e "\033[31mERROR: unknown build type \033[0m"
}

function git_stat() {
    echo -e "\033[37;42;1m ==== STAT $WORK_DIR ==== \033[0m"
    echo -e "\033[32m --pretty=format:'%h - %an, %ad %d : %s' --graph -15 \033[0m"
    git --no-pager log --date=iso-strict --pretty=format:'%h - %an, %ad %d : %s' --graph -15
    echo
    echo -e "\033[32m --stat --graph -3 \033[0m"
    git --no-pager log --date=iso-strict --stat --graph -3
}

#############
# load env
echo -e "\033[37;42;1mINFO: ==== boot env ==== \033[0m"
this_file="$0"
if [[ -L "$this_file" ]]; then
    link_file=$(realpath "$this_file")
    link_envs=${link_file%.*}.env
    if [[ -f "$link_envs" ]]; then
        echo "env-link=$link_envs"
        # shellcheck disable=SC1090
        source "$link_envs"
    fi
fi

this_envs=${this_file%.*}.env
if [[ -f "$this_envs" ]]; then
    echo "env-file=$this_envs"
    # shellcheck disable=SC1090
    source "$this_envs"
else
    echo -e "\033[31mERROR: no env file found. $this_envs \033[0m"
    exit
fi

if [[ "$USER_RUN" != "$USER" ]]; then
    echo -e "\033[37;41;1mERROR: need user $USER_RUN to run \033[0m"
    exit
fi

# change workdir
if [[ "$WORK_DIR" == "" ]]; then
    WORK_DIR=$(dirname "$this_file")
fi

cd "$WORK_DIR" || exit
WORK_DIR=$(realpath -s "$WORK_DIR")
echo "work-dir=$WORK_DIR"

# check arg
case "$1" in
    last)
        check_cmd git
        echo -e "\033[37;42;1m ==== GIT $WORK_DIR ==== \033[0m"
        git status
        git log --pretty=format:'%H - %an, %ad %d : %s' --graph -10
        echo -e "\033[37;42;1m ==== DST package ==== \033[0m"
        _dst=""
        for _jar in $PACK_JAR; do
            if [[ -f "$_jar" || -d "$_jar" ]]; then
                ls -l "$_jar"
                _dst=$(realpath "$_jar")
            else
                _tmp=$(find . -type f -name "$_jar")
                if [[ -f "$_tmp" ]]; then
                    ls -l "$_tmp"
                    _dst=$(realpath "$_tmp")
                fi
            fi
        done

        echo -e "\033[37;42;1m ==== DST build info ==== \033[0m"
        if [[ -d "$_dst" ]]; then
            find "$_jar" -maxdepth 1 -name 'index.html' | while read -r _idx; do
                echo "$_dst/index.html"
                grep 'WingsGitHash' "$_idx" | sed -n 's/.*\(<!-- WingsGitHash .* -->\).*/\1/p'
                break
            done
        elif [[ -f "$_dst" && $_dst == *.jar ]]; then
            giti=$(jar tf "$_dst" 2>/dev/null | grep git.properties )
            if [[ "$giti" != "" ]]; then
                tmp="./tmp-$BOOT_MD5"
                mkdir -p "$tmp"
                (cd "$tmp" && jar xf "$_dst" "$giti")
                echo "$_dst"
                grep -vE '=$' "$tmp/$giti"
                rm -rf "$tmp"
            fi
        fi
        ;;
    stat)
        check_cmd git
        git_stat
        ;;
    pull)
        check_cmd git

        echo -e "\033[37;42;1m ==== PULL $WORK_DIR ==== \033[0m"
        git fetch
        git reset --hard '@{u}'
        git clean -fd

        git_stat
        ;;
    pack)
        echo -e "\033[37;42;1m ==== BUILD $WORK_DIR ==== \033[0m"
        build_auto "$2"
        ;;
    push)
        echo -e "\033[37;42;1m ==== SEEK package ==== \033[0m"
        _jar_need=""
        _jar_info=""
        _yna="n"
        for _jar in $PACK_JAR; do
            _tmp=""
            if [[ -f "$_jar" || -d "$_jar" ]]; then
                _tmp=$_jar
            else
                _tmp=$(find . -type f -name "$_jar")
                if [[ ! -f "$_tmp" ]]; then
                    echo -e "\033[31mERROR: not file. $_jar \033[0m"
                    _jar_info="$_jar_info skip \033[31m $_jar \033[0m => not find\n"
                    continue
                fi
            fi

            _rp=$(realpath "$_tmp")
            if [[ "$_yna" != "a" ]]; then
                echo -e "[y/n/a]? \033[32m $_jar \033[0m => $_rp"
                read -r _yna </dev/tty
            fi

            if [[ "$_yna" != "n" ]]; then
                _pre_push "$_tmp"
                _jar_need="$_jar_need $_tmp"
                _jar_info="$_jar_info need \033[32m $_jar \033[0m => $_rp\n"
            else
                _jar_info="$_jar_info skip \033[33m $_jar \033[0m => $_rp\n"
            fi
        done

        echo -e "\033[37;42;1m ==== LIST package ==== \033[0m"
        echo -e "$_jar_info"

        if [[ "$2" == "pre" || "$_jar_need" == "" ]]; then
            echo -e "\033[31mERROR: not file to push \033[0m"
            exit
        fi

        _yna="n"
        echo -e "\033[37;42;1m ==== PUSH package ==== \033[0m"
        for _dst in $DEST_DIR; do
            if [[ "$_yna" != "a" ]]; then
                echo -e "[y/n/a]? \033[32m $_dst \033[0m"
                read -r _yna </dev/tty
            fi
            if [[ "$_yna" == "n" ]]; then
                continue
            fi

            for _jar in $_jar_need; do
                _cmd="cp -r"
                _tgt=$_dst
                if [[ ! -d "$_dst" ]]; then
                    _cmd="scp -r $SCP_ARGS"
                    # [user@]host:[path] scp://[user@]host[:port][/path]
                    if [[ $_dst =~ scp:// && "$(man scp | grep scp://)" == "" ]]; then
                        pt=$(echo "$_dst" | sed -E 's=scp://([^:]*:)([0-9]*)(.*)=\2=')
                        if [[ $pt =~ ^[0-9]+$ ]]; then
                            _cmd="$_cmd -P $pt"
                            _tgt=$(echo "$_dst" | sed -E 's=scp://([^:]*:)([0-9]*)(.*)=\1\3=')
                        fi
                    fi
                fi
                echo "$_jar => $_dst"
                if [[ -d "$_jar" && "$SUB_FLAT" == "true" ]]; then
                    # shellcheck disable=SC2086
                    $_cmd $_jar/* "$_tgt"
                else
                    # shellcheck disable=SC2086
                    $_cmd $_jar "$_tgt"
                fi
            done
        done
        ;;
    *)
        echo -e '\033[37;42;1mNOTE: help info, use the following\033[0m'
        echo -e '\033[32m last \033[0m last release info'
        echo -e '\033[32m stat \033[0m git log last 15/3 stat'
        echo -e '\033[32m pull \033[0m git pull remote'
        echo -e '\033[32m pack \033[0m auto mvn/npm/yarn/pnpm '
        echo -e '\033[32m pack mvn \033[0m mvn clean compile package'
        echo -e '\033[32m pack npm \033[0m npm build'
        echo -e '\033[32m pack pnpm \033[0m pnpm build'
        echo -e '\033[32m pack yarn \033[0m yarn build'
        echo -e '\033[32m pack pre \033[0m only exec PRE_PACK'
        echo -e '\033[32m push \033[0m push to dest'
        echo -e '\033[32m push pre \033[0m only exec PRE_PUSH'
        ;;
esac
echo
