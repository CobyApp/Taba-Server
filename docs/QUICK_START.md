# 빠른 시작 가이드

이 가이드는 Taba Backend를 처음부터 실행하는 단계별 가이드입니다.

## ⚡ 5분 안에 시작하기

### 1. 사전 요구사항 확인

```bash
# Java 버전 확인 (17 이상 필요)
java -version

# MySQL 버전 확인 (8.0 이상 필요)
mysql --version

# Gradle 확인 (선택사항, Wrapper 사용 가능)
gradle --version
```

### 2. MySQL 데이터베이스 생성

```bash
# MySQL 접속
mysql -u root -p

# 데이터베이스 생성
CREATE DATABASE taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

### 3. 환경 변수 설정

터미널에서 실행:

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=my-super-secret-jwt-key-change-in-production-256-bits
```

또는 `application.yml` 파일을 직접 수정:

```yaml
spring:
  datasource:
    username: root
    password: your_password
```

### 4. 프로젝트 빌드 및 실행

```bash
# 프로젝트 디렉토리로 이동
cd /Users/coby/Git/taba_backend

# 개발 환경으로 실행 (자동 테이블 생성)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### 5. 실행 확인

브라우저에서 접속:
- Swagger UI: http://localhost:8080/api/v1/swagger-ui/index.html
- API Health: http://localhost:8080/api/v1/actuator/health

---

## 📝 상세 실행 과정

### 단계 1: 프로젝트 구조 이해

```
taba_backend/
├── src/main/java/          # 소스 코드
├── src/main/resources/      # 설정 파일
├── build.gradle            # 의존성 관리
└── README.md               # 프로젝트 설명
```

### 단계 2: 데이터베이스 설정

#### MySQL 설치 확인

**macOS**:
```bash
# Homebrew로 설치된 경우
brew services list | grep mysql

# 실행 중이 아니면 시작
brew services start mysql
```

**Linux**:
```bash
sudo systemctl status mysql
sudo systemctl start mysql
```

#### 데이터베이스 생성

```bash
# 방법 1: 직접 SQL 실행
mysql -u root -p
CREATE DATABASE taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;

# 방법 2: 스크립트 실행
mysql -u root -p < src/main/resources/db/init.sql
```

### 단계 3: 애플리케이션 설정

#### application.yml 확인

주요 설정 항목:
- **데이터베이스 연결**: `spring.datasource.*`
- **JWT 설정**: `jwt.secret`, `jwt.expiration`
- **서버 포트**: `server.port` (기본: 8080)

#### 환경 변수 설정

**방법 1: 환경 변수**
```bash
export DB_USERNAME=root
export DB_PASSWORD=password
export JWT_SECRET=your-secret-key
```

**방법 2: .env 파일 (권장)**

`.env` 파일 생성:
```bash
# MySQL 설정
DB_HOST=localhost
DB_PORT=3306
DB_NAME=taba
DB_USERNAME=root
DB_PASSWORD=your_password

# Redis 설정
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT 설정
JWT_SECRET=your-256-bit-secret-key-change-this-in-production

# 서버 설정 (선택사항)
SERVER_URL=http://localhost:8080/api/v1
```

애플리케이션은 자동으로 `.env` 파일을 읽습니다 (dotenv-java 라이브러리 사용).

### 단계 4: 빌드 및 실행

#### 첫 실행 (의존성 다운로드)

```bash
# Gradle Wrapper 권한 부여
chmod +x gradlew

# 의존성 다운로드 및 빌드
./gradlew clean build

# 개발 환경으로 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
```

#### 실행 옵션

**개발 환경** (자동 테이블 생성):
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

**프로덕션 환경** (스키마 검증만):
```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

**특정 포트로 실행**:
```bash
./gradlew bootRun --args='--server.port=8081'
```

### 단계 5: 실행 확인

#### 로그 확인

정상 실행 시 다음과 같은 로그가 출력됩니다:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

2024-01-01 12:00:00 - Starting TabaApplication
2024-01-01 12:00:01 - HikariPool-1 - Starting...
2024-01-01 12:00:02 - HikariPool-1 - Start completed.
2024-01-01 12:00:03 - Started TabaApplication in 2.5 seconds
```

#### API 테스트

**Health Check**:
```bash
curl http://localhost:8080/api/v1/actuator/health
```

**Swagger UI 접속**:
브라우저에서 http://localhost:8080/api/v1/swagger-ui/index.html 접속

---

## 🔧 문제 해결

### 문제 1: 포트가 이미 사용 중

**에러 메시지**:
```
Web server failed to start. Port 8080 was already in use.
```

**해결 방법**:
```bash
# 포트 사용 중인 프로세스 확인
lsof -i :8080

# 프로세스 종료
kill -9 <PID>

# 또는 다른 포트 사용
./gradlew bootRun --args='--server.port=8081'
```

### 문제 2: 데이터베이스 연결 실패

**에러 메시지**:
```
Communications link failure
```

**해결 방법**:
1. MySQL 서비스 실행 확인
2. 사용자 권한 확인
3. 방화벽 설정 확인

```bash
# MySQL 서비스 상태 확인
brew services list | grep mysql  # macOS
sudo systemctl status mysql      # Linux

# MySQL 접속 테스트
mysql -u root -p
```

### 문제 3: 테이블이 생성되지 않음

**해결 방법**:
- 개발 환경 프로파일 사용 확인
- `ddl-auto: update` 설정 확인

```bash
# 개발 환경으로 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### 문제 4: Gradle Wrapper 오류

**에러 메시지**:
```
bash: ./gradlew: Permission denied
```

**해결 방법**:
```bash
chmod +x gradlew
```

### 문제 5: Redis 연결 실패

**에러 메시지**:
```
Unable to connect to Redis
```

**해결 방법**:
```bash
# Redis 설치 (macOS)
brew install redis
brew services start redis

# Redis 연결 테스트
redis-cli ping
# 응답: PONG

# Redis가 선택사항이므로, 토큰 블랙리스트 기능만 사용 불가
# 다른 기능은 정상 작동
```

---

## 🚀 다음 단계

프로젝트가 정상적으로 실행되면:

1. **Swagger UI에서 API 테스트**
   - http://localhost:8080/api/v1/swagger-ui/index.html

2. **회원가입 및 로그인 테스트**
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/signup \
     -H "Content-Type: application/json" \
     -d '{"email":"test@example.com","password":"password123","nickname":"테스트","agreeTerms":true,"agreePrivacy":true}'
   ```

3. **상세 가이드 읽기**
   - [PROJECT_GUIDE.md](PROJECT_GUIDE.md) - 전체 프로젝트 가이드
   - [DATABASE_SETUP.md](DATABASE_SETUP.md) - 데이터베이스 상세 설정

---

## 📚 추가 리소스

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [MySQL 공식 문서](https://dev.mysql.com/doc/)
- [Gradle 사용 가이드](https://docs.gradle.org/)

