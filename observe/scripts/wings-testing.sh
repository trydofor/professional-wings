#!/bin/bash

# check maven 3.5+
maven_cur=$(mvn --version | awk -F ' ' '/Apache Maven/ {print $3}')
IFS='.' read -r -a ptv <<<"$maven_cur"
if ((ptv[0] < 3)) || ((ptv[0] == 3 && ptv[1] < 5)); then
  echo -e "\033[31mREQUIRE maven 3.5+, but $maven_cur \033[0m"
  exit
fi

# check java 17+
java_cur=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
IFS='.' read -r -a ptj <<<"$java_cur"
if ((ptj[0] < 17)); then
  echo -e "\033[31mREQUIRE java 17+, but $java_cur \033[0m"
  echo "sdk ls java | grep installed"
  echo "sdk use java 17.0.6-tem"
  exit
fi

this_file="$0"
# shellcheck disable=SC2164,SC2046
cd $(dirname "$this_file")
cd ../.. # to wings project dir

module_auto="wings/silencer\
,wings/silencer-curse\
,wings/faceless\
,wings/faceless-awesome\
,wings/faceless-flywave\
,wings/faceless-jooq\
,wings/faceless-shard\
,wings/slardar\
,wings/slardar-hazel-session\
,wings/slardar-sprint\
,wings/slardar-webmvc\
,wings/warlock\
,wings/warlock-awesome\
,wings/warlock-bond\
,wings/warlock-shadow\
,radiant/tiny-mail\
,radiant/tiny-task"

module_h2db="wings/faceless-awesome\
,wings/faceless-jooq\
,wings/slardar\
,wings/slardar-hazel-session\
,wings/slardar-sprint\
,wings/slardar-webmvc\
,wings/warlock\
,wings/warlock-awesome\
,wings/warlock-bond\
,wings/warlock-shadow"

## ##############
echo "usage: $0 [init] [auto=p1,...,pn] [h2db=p1,...,pn]"

for arg in "$@"; do
  # split arguments then get arg and value
  IFS='=' read -r -a parts <<<"$arg"
  # match arg type
  if [[ "${parts[0]}" == "auto" && "${parts[1]}" != "" ]]; then
    module_auto=${parts[1]}
  elif [[ "${parts[0]}" == "h2db" && "${parts[1]}" != "" ]]; then
    module_h2db=${parts[1]}
  fi
done

echo "auto=$module_auto"
echo "h2db=$module_h2db"
echo -e "\033[32m ==== mvn version ==== \033[0m"
mvn --version || exit

# shellcheck disable=SC2034
MAVEN_OPTS="-Xmx2048m"

## install
[[ "$*" == "" || "$*" =~ .*init.* ]] && mvn -U clean install -Dmaven.test.skip=true

## auto test
[[ "$*" == "" || "$*" =~ .*auto.* ]] && (mvn test -ff -Dmaven.test.skip=false \
  -Dtesting-json='{"debug":"false",
  "sentry.logging.enabled":"false",
  "spring.wings.silencer.enabled.verbose":"false",
  "logging.level.root":"WARN",
  "logging.level.org.springframework.web":"WARN",
  "logging.level.org.jooq":"WARN"}' \
  -pl "$module_auto" || exit)

## h2db test
[[ "$*" =~ .*h2db.* ]] && (mvn test -ff -Dmaven.test.skip=false \
  -Dtesting-json='{"spring.profiles.active":"h2",
  "debug":"false",
  "sentry.logging.enabled":"false",
  "spring.wings.silencer.enabled.verbose":"false",
  "logging.level.root":"WARN",
  "logging.level.org.springframework.web":"WARN",
  "logging.level.org.jooq":"WARN"}' \
  -pl "$module_h2db" || exit)
