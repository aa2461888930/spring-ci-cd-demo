#!/usr/bin/env bash
# 本地跑 Maven 的包装脚本：固定 JDK8 + 工作区内的 Maven 仓库
set -e
export JAVA_HOME="$(/usr/libexec/java_home -v 1.8)"
export PATH="/Users/dkj/Documents/deepseek-harness/default-workspace/_tools/apache-maven-3.9.9/bin:$PATH"
export MAVEN_REPO="/Users/dkj/Documents/deepseek-harness/default-workspace/_tools/m2"
mkdir -p "$MAVEN_REPO"
exec mvn -Dmaven.repo.local="$MAVEN_REPO" -B -ntp "$@"
