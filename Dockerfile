# 멀티 스테이지 빌드를 사용하여 최적화된 Docker 이미지 생성

# Stage 1: 빌드 스테이지
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Gradle 캐시를 활용하기 위해 의존성 파일 먼저 복사
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle

# 의존성 다운로드 (캐시 활용)
RUN gradle dependencies --no-daemon || true

# 소스 코드 복사 및 빌드
COPY src ./src
# clean 빌드로 오래된 클래스 파일 제거
RUN gradle clean build -x test --no-daemon

# Stage 2: 실행 스테이지
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 타임존 설정 및 wget 설치 (헬스체크용)
RUN apt-get update && \
    apt-get install -y --no-install-recommends tzdata wget && \
    ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 애플리케이션 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 업로드 디렉토리 생성
RUN mkdir -p /app/uploads

# 포트 노출
EXPOSE 8080

# 헬스체크
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/v1/actuator/health || exit 1

# 애플리케이션 실행
ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", \
  "-jar", \
  "app.jar"]

