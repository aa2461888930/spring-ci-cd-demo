# spring-ci-cd-demo

Java 8 + Spring Boot 2.7.18 的最小可运行项目，用来练 GitHub Actions 全流程。

- 完整教程：[docs/教程.md](docs/教程.md)
- 流水线：[.github/workflows/ci.yml](.github/workflows/ci.yml)

```bash
./dev-mvn.sh verify                            # 编译 + 7 个测试
"$(/usr/libexec/java_home -v 1.8)/bin/java" -jar target/app.jar
curl "localhost:8080/api/hello?name=CI"
```

## 接口

| 路径 | 返回 |
|---|---|
| `GET /api/hello?name=CI` | `{"message":"Hello, CI!","version":"dev"}` |
| `GET /api/health` | `{"status":"UP"}` |
