#도커 연결

# ── Stage 1: Build ────────────────────────
FROM gradle:8.7-jdk17 AS builder
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src ./src

RUN gradle build -x test --no-daemon

# ── Stage 2: Run ──────────────────────────
FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
