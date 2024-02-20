#!/bin/bash

# check maven 3.5+
_ver_mvn=$(mvn --version | awk -F ' ' '/Apache Maven/ {print $3}')
IFS='.' read -r -a ptv <<< "$_ver_mvn"
if ((ptv[0] < 3)) || ((ptv[0] == 3 && ptv[1] < 9)); then
  echo -e "\033[31mREQUIRE maven 3.9+, but $_ver_mvn \033[0m"
  echo -e "asdf install maven 3.9.6[0m"
  exit
fi

# check java 21+
_ver_jdk=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
IFS='.' read -r -a ptj <<< "$_ver_jdk"
if ((ptj[0] < 21)); then
  echo -e "\033[31mREQUIRE java 21+, but $_ver_jdk \033[0m"
  echo "asdf install java temurin-21.0.1+12.0.LTS"
  exit
fi

if [[ "$MAVEN_OPTS" == "" ]]; then
  MAVEN_OPTS="-Xmx2g"
fi

if [[ "$LOG_LEVEL" == "" ]]; then
  echo -e "\033[32m log level (INFO)? [INFO|DEBUG|WARN|ERROR]\033[m"
  read -r _ans </dev/tty
  if [[ "$_ans" == "" ]] ;then
    LOG_LEVEL="INFO"
  else
    LOG_LEVEL="$_ans"
  fi
fi

if [[ "$TEST_VERBOSE" == "" ]]; then
  if [[ "$LOG_LEVEL" == "DEBUG" ]];then
    TEST_VERBOSE="true"
  else
    TEST_VERBOSE="false"
  fi
fi

if [[ "$COVERALLS_DRYRUN" == "" ]]; then
  echo -e "\033[32m dryrun coveralls (y)? [y|n]\033[m"
  read -r _ans </dev/tty
  if [[ "$_ans" == "" || "$_ans" == "y" ]]; then
    COVERALLS_DRYRUN="true"
  else
    COVERALLS_DRYRUN="false"
  fi
fi

this_file="$0"
# shellcheck disable=SC2164,SC2046
cd $(dirname "$this_file")
cd ../.. # to wings project dir

echo -e "\033[32m from which step (1) to run?\033[m"
echo "1 - clean install"
echo "2 - devs-initdb"
echo "3 - test"
echo "4 - coverage"
echo "5 - coverall report"
read -r _ans </dev/tty
_step=1;
if [[ "$_ans" != "" ]]; then
    _step="$_ans"
fi
# ##############
TZ=Asia/Shanghai
echo "====================="
pwd
echo "TZ=$TZ"
echo "MAVEN_OPTS=$MAVEN_OPTS"
echo "LOG_LEVEL=$LOG_LEVEL"
echo "TEST_VERBOSE=$TEST_VERBOSE"
echo "COVERALLS_DRYRUN=$COVERALLS_DRYRUN"
echo "FROM STEP $_step TO RUN"
echo "====================="
set -e
set -x
[[ "$_step" -le "1" ]] && mvn -P '!module-example,!module-devs' -Dmaven.test.skip=true clean install
[[ "$_step" -le "2" ]] && mvn -pl ':devs-codegen' -Ddevs-initdb=true clean test
[[ "$_step" -le "3" ]] && mvn -P 'report-coverage,!module-example,!module-devs' test
[[ "$_step" -le "4" ]] && mvn -P 'report-coverage' -pl ':devs-coverage' -am jacoco:report-aggregate
[[ "$_step" -le "5" ]] && mvn -pl ':devs-coverage' -DrepoToken=$COVERALLS_WINGS -DdryRun=$COVERALLS_DRYRUN -Dwings.rootdir=../.. coveralls:report

