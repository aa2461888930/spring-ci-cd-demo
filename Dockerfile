# 运行阶段：Java 8 运行时。jar 由流水线在前面构建好，这里不再编译
FROM eclipse-temurin:8-jre

WORKDIR /app

# 版本号通过构建参数注入，容器里就能看到是哪个提交
ARG APP_VERSION=dev
ENV APP_VERSION=${APP_VERSION}

COPY target/app.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
