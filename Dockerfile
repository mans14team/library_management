FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM openjdk:17-jdk-slim
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# 포트 설정 추가
EXPOSE 8080

# JVM 옵션 설정
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"
ENV TZ=Asia/Seoul

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]