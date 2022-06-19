#!/bin/bash

java_ver=$1
boot_jar=$2

path_jar=$(realpath -s "$boot_jar")
work_dir=$(dirname "$path_jar")
cd "$work_dir" || exit

temp_dir="boot-jar"
mkdir -p ${temp_dir}
trap 'rm -rf ${temp_dir}' EXIT
unzip -q "${boot_jar}" -d "${temp_dir}"

rm -rf "$path_jar.jdeps.*"
find "${temp_dir}/BOOT-INF/lib" -type f -name '*.jar' | while read -r _jar; do
    echo "=== $_jar"
    jdeps -R \
    --class-path \'${temp_dir}/BOOT-INF/lib/*\' \
    --jdk-internals \
    --multi-release "$java_ver" \
    "$_jar" >> "$path_jar.jdeps.out" 2>>"$path_jar.jdeps.err"
done

grep 'JDK internal API (' "$path_jar.jdeps.out" | awk '{print $3 $7}' | sort | uniq
