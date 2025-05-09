# 使用官方 OpenJDK 基礎映像
FROM openjdk:17

# 複製 jar 檔進容器，名稱統一為 app.jar
COPY build/libs/*SNAPSHOT.jar app.jar

# 執行 Spring Boot 應用
ENTRYPOINT ["java", "-jar", "app.jar"]