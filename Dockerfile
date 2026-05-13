# syntax=docker/dockerfile:1
# 빌드: Gradle 공식 이미지 (Alpine 은 일부 환경에서 플러그인 저장소 DNS/SSL 이슈가 있어 Jammy 기반 사용)
FROM gradle:9.4.1-jdk21-jammy AS builder
WORKDIR /workspace

COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle
COPY src ./src

RUN GRADLE_USER_HOME=/tmp/gradle GRADLE_OPTS="-Xmx512m -XX:MaxMetaspaceSize=256m" gradle --no-daemon bootJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -g 1000 spring && adduser -u 1000 -G spring -S spring

COPY --from=builder /workspace/build/libs/*.jar app.jar

USER spring:spring
EXPOSE 8082

ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
