#!/bin/bash

THIS_PATH=$(pwd)
BASE_PATH=$(cd `dirname "$0"`; pwd)
echo "wings-example的目录是 $BASE_PATH"
cd "$BASE_PATH"

java -version
if [[ $? != 0 ]];then
  echo "没有找到 javac和java环境"
  exit
fi

JAVA_FILE="pro/fessional/wings/example/exec/Wings0InitProject.java"
JAVA_ROOT="src/test/java"
CLASS_RUN="pro.fessional.wings.example.exec.Wings0InitProject"

echo "编译java文件 $JAVA_FILE"
javac -encoding utf-8 "$JAVA_ROOT/$JAVA_FILE"

echo "执行class文件 $CLASS_RUN"
java -cp $JAVA_ROOT $CLASS_RUN "$BASE_PATH" "$THIS_PATH"

echo "制作完成了，可以直接进入目录 mvn compile"