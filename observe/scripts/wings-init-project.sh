#!/bin/bash
THIS_VERSION=2021-12-21

# relative to wings-example
JAVA_ROOT="winx-devops/src/*/java"
CLAZ_ROOT="winx-devops/target/test-classes"
JAVA_FILE="com/moilioncircle/wings/devops/init/*.java"
CLASS_RUN="com.moilioncircle.wings.devops.init.WingsInitProjectSwing"

###
echo -e "\033[37;42;1mScript-Version $THIS_VERSION \033[0m"

## change to wings-example
# shellcheck disable=SC2046
_this_path=$(dirname $(realpath -s "$0"))
# shellcheck disable=SC2164
_base_path=$(cd "$_this_path"; cd ../../example; pwd)
echo -e "\033[37;42;1m wings-example=$_base_path \033[0m"
cd "$_base_path" || exit

if ! java -version; then
    echo -e "\033[37;41;1mERROR: no java and javac \033[0m"
    exit
fi

_wings_ver=$(grep -E '/revision>|/changelist>' ../pom.xml |
    sort -r | tr -d '\n ' |
    sed -E 's:<revision>|</changelist>::g' |
    sed -E 's:</revision><changelist>:.:g')

echo -e "\033[37;42;1m compile java $JAVA_FILE to $CLAZ_ROOT \033[0m"
mkdir -p $CLAZ_ROOT
# shellcheck disable=SC2086
javac -d $CLAZ_ROOT -encoding utf-8 $JAVA_ROOT/$JAVA_FILE

echo -e "\033[37;42;1m wings-version=$_wings_ver class=$CLASS_RUN \033[0m"
java -cp $CLAZ_ROOT $CLASS_RUN "$_base_path" "$_this_path" "$_wings_ver"

echo -e "\033[37;42;1m done. \033[0m"
