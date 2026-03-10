# ── Stage 1: Build ──
FROM gradle:8.7-jdk17 AS builder
WORKDIR /app
COPY . .
# 실행 가능한 JAR 하나만 생성하도록 설정
RUN gradle bootJar -x test --no-daemon

# ── Stage 2: Run ──
# openjdk 대신 AWS 환경에서 가장 안정적인 corretto 사용
FROM amazoncorretto:17-al2023-headless
WORKDIR /app

# 빌드 결과물 복사
COPY --from=builder /app/build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]