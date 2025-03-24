#!/bin/bash
# shellcheck disable=SC2164,SC2046
this_file="$0"
cd $(dirname "$this_file")
cd ../.. # to wings project dir
wings_root=$(pwd)
echo -e "\033[37;42;1m WingsRoot at ${wings_root}\033[0m"
#
echo -e "\033[37;42;1m which dev (1) to run?\033[0m"
echo "1 - check deps/plugin updates"
echo "2 - clean idea cache"
read -r _cmmd </dev/tty
case "$_cmmd" in
    1)
        cd radiant/devs-mvndeps
        mvn -P deps-minor versions:display-dependency-updates
        mvn -P deps-major versions:display-dependency-updates
        mvn versions:display-plugin-updates
        echo -e "\033[37;42;1m# 1.major deps updates\033[0m"
        cat target/dependency-updates.txt-major
        echo -e "\033[37;42;1m# 2.minor deps updates\033[0m"
        cat target/dependency-updates.txt-minor
        echo -e "\033[37;42;1m# 3.maven plugin updates\033[0m"
        cat target/dependency-updates.txt
        ;;
    2)
        echo -e "delete *.iml .idea"
        find . -type f -name '*.iml' -print0 | xargs -0 rm -f
        find . -type d -name '.idea' -print0 | xargs -0 rm -rf
        echo -e "delete flatten pom.xml"
        find . -type f -name '.flattened-pom.xml' -print0 | xargs -0 rm -f
        find . -type f -name '.pom.xml' -print0 | xargs -0 rm -f
        ;;
    *)
        echo "noop"
        ;;
esac
cd "$wings_root"
echo
