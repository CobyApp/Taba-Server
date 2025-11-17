# Docker 배포 가이드

Taba Backend를 Docker를 사용하여 배포하는 방법을 안내합니다.

## 📋 목차

1. [사전 요구사항](#사전-요구사항)
2. [빠른 시작](#빠른-시작)
3. [Docker Compose 사용](#docker-compose-사용)
4. [개별 컨테이너 실행](#개별-컨테이너-실행)
5. [프로덕션 배포](#프로덕션-배포)
6. [문제 해결](#문제-해결)

---

## 사전 요구사항

- **Docker** 20.10 이상
- **Docker Compose** 2.0 이상

설치 확인:
```bash
docker --version
docker-compose --version
```

---

## 빠른 시작

### 1. 환경 변수 설정

`.env` 파일을 생성합니다:

```bash
cp .env.example .env
```

`.env` 파일을 열어 필요한 값들을 수정합니다:

```bash
# 필수 설정
DB_PASSWORD=your_secure_password
JWT_SECRET=your-256-bit-secret-key-change-this-in-production

# 선택사항
REDIS_PASSWORD=your_redis_password
SERVER_URL=http://localhost:8080/api/v1
```

### 2. Docker Compose로 실행

```bash
# 모든 서비스 빌드 및 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f backend

# 서비스 상태 확인
docker-compose ps
```

### 3. 애플리케이션 확인

- **Health Check**: http://localhost:8080/api/v1/actuator/health
- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui/index.html

---

## Docker Compose 사용

### 기본 명령어

```bash
# 서비스 시작 (백그라운드)
docker-compose up -d

# 서비스 중지
docker-compose stop

# 서비스 중지 및 컨테이너 제거
docker-compose down

# 서비스 중지, 컨테이너 제거 및 볼륨 삭제
docker-compose down -v

# 로그 확인
docker-compose logs -f [service_name]

# 특정 서비스 재시작
docker-compose restart backend

# 이미지 재빌드
docker-compose build --no-cache

# 서비스 상태 확인
docker-compose ps
```

### 서비스 구성

`docker-compose.yml`에는 다음 서비스가 포함됩니다:

1. **mysql**: MySQL 8.0 데이터베이스
2. **redis**: Redis 7 (토큰 블랙리스트용, 선택사항)
3. **backend**: Taba Backend 애플리케이션

### 볼륨

다음 볼륨이 생성됩니다:

- `mysql_data`: MySQL 데이터 저장
- `redis_data`: Redis 데이터 저장
- `uploads_data`: 업로드된 이미지 파일 저장

---

## 개별 컨테이너 실행

### 1. MySQL 실행

```bash
docker run -d \
  --name taba-mysql \
  -e MYSQL_ROOT_PASSWORD=root_password \
  -e MYSQL_DATABASE=taba \
  -e MYSQL_USER=taba_user \
  -e MYSQL_PASSWORD=taba_password \
  -p 3306:3306 \
  -v mysql_data:/var/lib/mysql \
  mysql:8.0 \
  --character-set-server=utf8mb4 \
  --collation-server=utf8mb4_unicode_ci
```

### 2. Redis 실행 (선택사항)

```bash
docker run -d \
  --name taba-redis \
  -p 6379:6379 \
  -v redis_data:/data \
  redis:7-alpine
```

### 3. Backend 애플리케이션 빌드 및 실행

```bash
# 이미지 빌드
docker build -t taba-backend:latest .

# 컨테이너 실행
docker run -d \
  --name taba-backend \
  --link taba-mysql:mysql \
  --link taba-redis:redis \
  -p 8080:8080 \
  -e DB_HOST=mysql \
  -e DB_PORT=3306 \
  -e DB_NAME=taba \
  -e DB_USERNAME=taba_user \
  -e DB_PASSWORD=taba_password \
  -e REDIS_HOST=redis \
  -e REDIS_PORT=6379 \
  -e JWT_SECRET=your-secret-key \
  -e SPRING_PROFILES_ACTIVE=prod \
  -v uploads_data:/app/uploads \
  taba-backend:latest
```

---

## 프로덕션 배포

### 1. 프로덕션 환경 변수 설정

`.env` 파일에 프로덕션 설정을 추가합니다:

```bash
SPRING_PROFILES_ACTIVE=prod
SERVER_URL=https://api.yourdomain.com/api/v1
JWT_SECRET=your-production-secret-key-256-bits
DB_PASSWORD=strong-production-password
REDIS_PASSWORD=strong-redis-password
```

### 2. 프로덕션 Compose 파일 사용

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### 3. 리버스 프록시 설정 (Nginx 예시)

```nginx
server {
    listen 80;
    server_name api.yourdomain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 4. SSL/TLS 설정

Let's Encrypt를 사용한 SSL 인증서 설정을 권장합니다.

---

## 문제 해결

### 문제 1: 포트 충돌

**에러**: `Bind for 0.0.0.0:8080 failed: port is already allocated`

**해결**:
```bash
# 포트 사용 중인 프로세스 확인
lsof -i :8080

# docker-compose.yml에서 포트 변경
ports:
  - "8081:8080"  # 호스트 포트 변경
```

### 문제 2: MySQL 연결 실패

**에러**: `Communications link failure`

**해결**:
1. MySQL 컨테이너가 실행 중인지 확인:
   ```bash
   docker-compose ps mysql
   ```

2. MySQL 로그 확인:
   ```bash
   docker-compose logs mysql
   ```

3. 환경 변수 확인:
   ```bash
   docker-compose config
   ```

### 문제 3: Redis 연결 실패

**에러**: `Unable to connect to Redis`

**해결**:
- Redis는 선택사항이므로 Redis 컨테이너를 중지해도 애플리케이션은 정상 작동합니다
- 토큰 블랙리스트 기능만 비활성화됩니다

### 문제 4: 이미지 빌드 실패

**에러**: `Build failed`

**해결**:
```bash
# 캐시 없이 재빌드
docker-compose build --no-cache

# Docker 빌드 로그 확인
docker-compose build --progress=plain
```

### 문제 5: 볼륨 권한 문제

**에러**: `Permission denied`

**해결**:
```bash
# 업로드 디렉토리 권한 설정
docker-compose exec backend chmod 755 /app/uploads
```

---

## 유용한 명령어

### 데이터베이스 백업

```bash
# MySQL 백업
docker-compose exec mysql mysqldump -u root -p${DB_PASSWORD} taba > backup.sql

# MySQL 복원
docker-compose exec -T mysql mysql -u root -p${DB_PASSWORD} taba < backup.sql
```

### 로그 확인

```bash
# 모든 서비스 로그
docker-compose logs -f

# 특정 서비스 로그
docker-compose logs -f backend

# 최근 100줄 로그
docker-compose logs --tail=100 backend
```

### 컨테이너 내부 접근

```bash
# Backend 컨테이너 접근
docker-compose exec backend sh

# MySQL 컨테이너 접근
docker-compose exec mysql mysql -u root -p

# Redis 컨테이너 접근
docker-compose exec redis redis-cli
```

### 리소스 사용량 확인

```bash
# 컨테이너 리소스 사용량
docker stats

# 특정 컨테이너만
docker stats taba-backend
```

---

## 성능 최적화

### 1. JVM 옵션 조정

`Dockerfile`의 `ENTRYPOINT`에 JVM 옵션 추가:

```dockerfile
ENTRYPOINT ["java", \
  "-Xms512m", \
  "-Xmx1024m", \
  "-XX:+UseG1GC", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", \
  "-jar", \
  "app.jar"]
```

### 2. 데이터베이스 연결 풀 설정

`application-prod.yml`에서 HikariCP 설정 조정:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
```

---

## 보안 권장사항

1. **환경 변수 보호**: `.env` 파일을 `.gitignore`에 추가
2. **비밀번호 강도**: 프로덕션에서는 강력한 비밀번호 사용
3. **네트워크 격리**: Docker 네트워크를 사용하여 서비스 간 통신만 허용
4. **이미지 스캔**: 정기적으로 Docker 이미지 보안 스캔
5. **업데이트**: 정기적으로 베이스 이미지 업데이트

---

## 참고 자료

- [Docker 공식 문서](https://docs.docker.com/)
- [Docker Compose 공식 문서](https://docs.docker.com/compose/)
- [Spring Boot Docker 가이드](https://spring.io/guides/gs/spring-boot-docker/)

