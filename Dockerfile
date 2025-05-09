# 使用官方 OpenJDK 17 基礎映像檔（可依你 build.gradle 中的版本調整）
FROM eclipse-temurin:17-jdk-alpine

# 建立 app 目錄
WORKDIR /app

# 複製 jar 檔到容器
COPY build/libs/*.jar app.jar

# 曝露 port（如果你在 application.properties 有設定 server.port 也要對應）
EXPOSE 8080

# 執行 jar 檔
ENTRYPOINT ["java", "-jar", "app.jar"]
