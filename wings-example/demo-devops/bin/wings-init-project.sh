#!/bin/bash
THIS_VERSION=2021-12-21
echo -e "\033[37;42;1mScript-Version $THIS_VERSION \033[0m"

THIS_PATH=$(pwd)
# shellcheck disable=SC2164,SC2046,SC2006
BASE_PATH=$(cd `dirname "$0"`; dirname `pwd`)
echo "wings-example的目录是 $BASE_PATH"
cd "$BASE_PATH" || exit

if java -version ; then
  echo "没有找到 javac和java环境"
  exit
fi

JAVA_ROOT="demo-devops/src/test/java"
CLAZ_ROOT="demo-devops/target/test-classes"
JAVA_FILE="com/moilioncircle/roshan/devops/init/*.java"
CLASS_RUN="com.moilioncircle.roshan.devops.init.WingsInitProjectSwing"

VERS=$(grep -E '/revision>|/changelist>' ../pom.xml | \
sort -r | tr -d '\n '| \
sed -E 's:<revision>|</changelist>::g'| \
sed -E 's:</revision><changelist>:.:g')

echo "编译java文件 $JAVA_FILE to $CLAZ_ROOT"
mkdir -p $CLAZ_ROOT
javac -d $CLAZ_ROOT -encoding utf-8 "$JAVA_ROOT/$JAVA_FILE"

echo "Wings Version=$VERS"
echo "执行class文件 $CLASS_RUN"
java -cp $CLAZ_ROOT $CLASS_RUN "$BASE_PATH" "$THIS_PATH" "$VERS"

echo "制作完成了，可以直接进入目录 mvn compile"
