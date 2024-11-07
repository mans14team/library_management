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
    apt-get install -y curl redis-tools && \
    rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# JVM 옵션 설정
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"

ENTRYPOINT ["java", "-jar", "app.jar"]