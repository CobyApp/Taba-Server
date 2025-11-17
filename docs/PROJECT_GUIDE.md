# Taba Backend 프로젝트 가이드

## 📋 목차
1. [프로젝트 개요](#프로젝트-개요)
2. [기술 스택 상세](#기술-스택-상세)
3. [프로젝트 구조](#프로젝트-구조)
4. [설치 및 실행](#설치-및-실행)
5. [API 사용 방법](#api-사용-방법)
6. [개발 가이드](#개발-가이드)

---

## 프로젝트 개요

Taba는 편지를 주고받으며 친구와의 관계를 꽃다발로 표현하는 소셜 플랫폼입니다. 이 백엔드는 Spring Boot 기반의 RESTful API 서버로, 모바일 앱과 웹 클라이언트를 위한 모든 기능을 제공합니다.

### 주요 기능
- **인증 시스템**: JWT 기반 회원가입/로그인, 비밀번호 재설정, 토큰 블랙리스트 관리
- **사용자 관리**: 프로필 이미지 업로드 (회원가입/수정 시), 프로필 정보 수정
- **편지 관리**: 공개/비공개 편지 작성, 템플릿 및 이미지 첨부, 신고, 예약 발송, 답장 (자동 친구 추가)
- **공개 편지 수신자 관리**: 공개 편지의 복수 수신자 지원, 사용자별 읽음 상태 추적
- **친구 시스템**: 초대 코드 기반 친구 추가 (3분 유효), 친구 목록 조회, 꽃다발 관리, 친구별 편지 목록 조회
- **알림 시스템**: 실시간 알림 발송 및 관리, 배치 읽음 처리 최적화
- **파일 업로드**: 이미지 업로드 및 URL 반환 (로컬 저장)

---

## 기술 스택 상세

### 1. 프레임워크 및 라이브러리

#### Spring Boot 3.2.0
- **역할**: 애플리케이션의 핵심 프레임워크
- **특징**: 
  - 자동 설정(Auto Configuration)
  - 내장 서버(Tomcat)
  - 프로덕션 준비 기능(Actuator)
- **사용 이유**: 빠른 개발, 풍부한 생태계, 엔터프라이즈급 기능

#### Spring Security 6.x
- **역할**: 인증 및 권한 관리
- **구현**: 
  - JWT(JSON Web Token) 기반 인증
  - Bearer Token 방식
  - 필터 체인을 통한 요청 검증
- **보안 기능**:
  - 비밀번호 암호화 (BCrypt)
  - CSRF 보호
  - CORS 설정

#### Spring Data JPA
- **역할**: 데이터베이스 접근 추상화
- **기능**:
  - 엔티티 매핑
  - 자동 쿼리 생성
  - 트랜잭션 관리
- **장점**: 
  - 반복적인 CRUD 코드 제거
  - 타입 안전성
  - 쿼리 최적화

#### QueryDSL 5.0.0
- **역할**: 타입 안전한 동적 쿼리 작성
- **사용 예시**:
  ```java
  QLetter letter = QLetter.letter;
  queryFactory.selectFrom(letter)
      .where(letter.visibility.eq(Visibility.PUBLIC))
      .fetch();
  ```
- **장점**: 컴파일 타임 오류 검출, IDE 자동완성

#### MapStruct 1.5.5
- **역할**: DTO와 Entity 간 매핑
- **사용 예시**:
  ```java
  @Mapper(componentModel = "spring")
  public interface UserMapper {
      UserDto toDto(User user);
  }
  ```
- **장점**: 
  - 컴파일 타임 코드 생성
  - 런타임 성능 우수
  - 보일러플레이트 코드 제거

### 2. 데이터베이스

#### MySQL 8.0
- **역할**: 관계형 데이터베이스
- **설정**:
  - UTF-8MB4 인코딩 (이모지 지원)
  - InnoDB 엔진
  - 커넥션 풀링 (HikariCP)
- **테이블 구조**: 주요 테이블
  - `users`: 사용자 정보 (프로필 이미지 포함)
  - `letters`: 편지 정보
  - `letter_recipients`: 편지 수신자 (공개 편지의 복수 수신자 지원)
  - `friendships`: 친구 관계
  - `letter_images`: 편지 첨부 이미지
  - `letter_reports`: 편지 신고
  - `invite_codes`: 초대 코드
  - `notifications`: 알림
  - `password_reset_tokens`: 비밀번호 재설정 토큰
- **공개 편지 수신자 관리**: `letter_recipients` 테이블을 사용하여 공개 편지를 읽은 모든 사용자 추적
  - 공개 편지(`PUBLIC`)를 읽은 사용자는 자동으로 `letter_recipients`에 기록됨
  - 각 사용자별로 읽음 상태(`isRead`)와 읽은 시간(`readAt`) 관리
- **친구 간 편지 조회**: `Letter` 테이블을 직접 조회하여 양방향 편지를 가져옵니다
  - `sender_id`와 `recipient_id`를 이용한 양방향 쿼리
  - `visibility = 'DIRECT'`인 편지만 조회
  - 읽음 상태는 `Letter.isRead` 필드로 관리 (recipient 기준)

#### Redis
- **역할**: 캐싱 및 토큰 블랙리스트 관리
- **사용 예시**:
  - JWT 토큰 블랙리스트 (로그아웃 처리)
  - 사용자 프로필 캐싱
  - 공개 편지 목록 캐싱
  - 친구 목록 캐싱

### 3. 인증 및 보안

#### JWT (JSON Web Token)
- **라이브러리**: jjwt 0.12.3
- **구조**:
  - Header: 알고리즘 정보
  - Payload: 사용자 ID
  - Signature: 서버 서명
- **토큰 만료**: 7일
- **저장 위치**: 클라이언트 (로컬 스토리지/쿠키)
- **토큰 블랙리스트**: Redis를 사용하여 로그아웃된 토큰 관리

#### BCrypt
- **역할**: 비밀번호 해싱
- **특징**: 
  - 솔트 자동 생성
  - 단방향 암호화
  - Rainbow Table 공격 방지

### 4. API 문서화

#### Swagger/OpenAPI 3
- **라이브러리**: springdoc-openapi 2.3.0
- **접근 URL**: `http://localhost:8080/api/v1/swagger-ui/index.html`
- **기능**:
  - API 엔드포인트 자동 문서화
  - 요청/응답 스키마 표시
  - 테스트 인터페이스 제공

### 5. 빌드 도구

#### Gradle 8.5
- **역할**: 의존성 관리 및 빌드
- **특징**:
  - Groovy/Kotlin DSL
  - 증분 빌드
  - 플러그인 시스템

---

## 프로젝트 구조

```
taba_backend/
├── src/main/java/com/taba/
│   ├── TabaApplication.java          # 메인 애플리케이션 클래스
│   │
│   ├── auth/                         # 인증 모듈
│   │   ├── config/                   # Security 설정
│   │   ├── controller/               # 인증 API
│   │   ├── dto/                      # 요청/응답 DTO
│   │   ├── entity/                   # PasswordResetToken
│   │   ├── filter/                   # JWT 필터
│   │   ├── repository/               # 리포지토리
│   │   ├── service/                  # 비즈니스 로직 (TokenBlacklistService 포함)
│   │   └── util/                     # JWT 유틸리티
│   │
│   ├── user/                         # 사용자 모듈
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/                   # User 엔티티
│   │   ├── repository/
│   │   └── service/
│   │
│   ├── letter/                       # 편지 모듈
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/                   # Letter, LetterImage, LetterReport, LetterRecipient
│   │   ├── repository/
│   │   ├── scheduler/                # 예약 발송 스케줄러 (알림 발송 포함)
│   │   └── service/
│   │
│   ├── friendship/                   # 친구 관계 모듈
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/                   # Friendship
│   │   ├── repository/
│   │   └── service/
│   │
│   ├── invite/                       # 초대 코드 모듈
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/                   # InviteCode
│   │   ├── repository/
│   │   └── service/
│   │
│   ├── notification/                  # 알림 모듈
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/                   # Notification
│   │   ├── repository/
│   │   └── service/
│   │
│   ├── settings/                     # 설정 모듈
│   │   └── controller/
│   │
│   ├── file/                         # 파일 업로드 모듈
│   │   ├── controller/
│   │   └── service/
│   │
│   └── common/                       # 공통 모듈
│       ├── config/                    # 공통 설정
│       ├── dto/                       # ApiResponse
│       ├── entity/                    # BaseEntity
│       ├── exception/                 # 예외 처리
│       └── util/                      # 유틸리티
│
├── src/main/resources/
│   ├── application.yml               # 기본 설정
│   ├── application-dev.yml           # 개발 환경 설정
│   ├── application-prod.yml          # 프로덕션 설정
│   └── db/                           # 데이터베이스 스크립트
│
├── build.gradle                       # Gradle 빌드 설정
├── settings.gradle                    # Gradle 프로젝트 설정
└── README.md                          # 프로젝트 설명
```

### 레이어 아키텍처

```
┌─────────────────────────────────────┐
│         Controller Layer            │  ← REST API 엔드포인트
│      (요청/응답 처리, 검증)          │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│          Service Layer              │  ← 비즈니스 로직
│    (트랜잭션 관리, 예외 처리)         │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│        Repository Layer             │  ← 데이터 접근
│      (JPA, QueryDSL 쿼리)           │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Entity Layer                │  ← 데이터베이스 매핑
│      (JPA 엔티티, 관계 정의)          │
└─────────────────────────────────────┘
```

---

## 설치 및 실행

### 사전 요구사항

1. **Java 17 이상**
   ```bash
   java -version
   # java version "17.0.x" 이상 필요
   ```

2. **MySQL 8.0 이상**
   ```bash
   mysql --version
   # mysql Ver 8.0.x 이상 필요
   ```

3. **Redis (토큰 블랙리스트 및 캐싱용, 선택사항)**
   ```bash
   redis-cli --version
   ```
   
   **설치 방법**:
   ```bash
   # macOS
   brew install redis
   brew services start redis
   
   # Linux
   sudo apt install redis-server
   sudo systemctl start redis
   ```

4. **Gradle (또는 Gradle Wrapper)**

### 1단계: 프로젝트 클론 및 이동

```bash
cd /Users/coby/Git/taba_backend
```

### 2단계: MySQL 데이터베이스 설정

#### 2-1. MySQL 서비스 시작

**macOS (Homebrew)**:
```bash
brew services start mysql
```

**Linux**:
```bash
sudo systemctl start mysql
```

**Windows**: MySQL 서비스가 자동으로 시작됩니다.

#### 2-2. 데이터베이스 생성

```bash
# MySQL 접속
mysql -u root -p

# 데이터베이스 생성
CREATE DATABASE taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

또는 스크립트 실행:
```bash
mysql -u root -p < src/main/resources/db/init.sql
```

### 3단계: 환경 변수 설정

#### 방법 1: .env 파일 사용 (권장)

프로젝트 루트에 `.env` 파일 생성:

```bash
# MySQL 설정
DB_HOST=localhost
DB_PORT=3306
DB_NAME=taba
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

# Redis 설정
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT 설정
JWT_SECRET=your-256-bit-secret-key-change-this-in-production

# 서버 URL (파일 업로드용)
SERVER_URL=http://localhost:8080/api/v1
```

애플리케이션은 자동으로 `.env` 파일을 읽습니다.

#### 방법 2: 환경 변수로 설정

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=your-256-bit-secret-key-change-this-in-production
```

#### 방법 3: application.yml 직접 수정

`src/main/resources/application.yml` 파일을 열어서 수정:

```yaml
spring:
  datasource:
    username: root
    password: your_password
```

### 4단계: Gradle Wrapper 생성 (최초 1회)

Gradle이 설치되어 있지 않은 경우:

```bash
# Gradle 설치 (macOS)
brew install gradle

# Wrapper 생성
gradle wrapper --gradle-version 8.5
```

또는 이미 Wrapper가 있다면:
```bash
chmod +x gradlew
```

### 5단계: 의존성 다운로드 및 빌드

```bash
# 의존성 다운로드
./gradlew build --refresh-dependencies

# 또는
./gradlew clean build
```

### 6단계: 애플리케이션 실행

#### 개발 환경으로 실행 (자동 테이블 생성)

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

또는:

```bash
java -jar build/libs/taba-backend-1.0.0.jar --spring.profiles.active=dev
```

#### 프로덕션 환경으로 실행

```bash
./gradlew bootRun
```

### 7단계: 실행 확인

애플리케이션이 정상적으로 실행되면:

1. **콘솔 로그 확인**:
   ```
   Started TabaApplication in X.XXX seconds
   ```

2. **Health Check**:
   ```bash
   curl http://localhost:8080/api/v1/actuator/health
   ```

3. **Swagger UI 접속**:
   ```
   http://localhost:8080/api/v1/swagger-ui/index.html
   ```

---

## API 사용 방법

### 1. API 기본 정보

- **Base URL**: `http://localhost:8080/api/v1`
- **인증 방식**: JWT Bearer Token
- **Content-Type**: `application/json`

### 2. 인증 플로우

#### 2-1. 회원가입

```bash
# 프로필 이미지 없이 회원가입
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -F "email=user@example.com" \
  -F "password=password123" \
  -F "nickname=사용자" \
  -F "agreeTerms=true" \
  -F "agreePrivacy=true"

# 프로필 이미지와 함께 회원가입
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -F "email=user@example.com" \
  -F "password=password123" \
  -F "nickname=사용자" \
  -F "agreeTerms=true" \
  -F "agreePrivacy=true" \
  -F "profileImage=@/path/to/image.jpg"
```

**응답**:
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": "uuid",
      "email": "user@example.com",
      "nickname": "사용자"
    }
  }
}
```

#### 2-2. 로그인

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

#### 2-3. 인증이 필요한 API 호출

```bash
curl -X GET http://localhost:8080/api/v1/users/{userId} \
  -H "Authorization: Bearer {token}"
```

### 3. 주요 API 엔드포인트

#### 인증 API
- `POST /auth/signup` - 회원가입
- `POST /auth/login` - 로그인
- `POST /auth/forgot-password` - 비밀번호 찾기
- `POST /auth/reset-password` - 비밀번호 재설정
- `PUT /auth/change-password` - 비밀번호 변경
- `POST /auth/logout` - 로그아웃 (토큰 블랙리스트 처리)

#### 사용자 API
- `GET /users/{userId}` - 프로필 조회
- `PUT /users/{userId}` - 프로필 수정

#### 편지 API
- `POST /letters` - 편지 작성 (예약 발송 지원)
- `GET /letters/public` - 공개 편지 목록
- `GET /letters/{letterId}` - 편지 상세 조회
- `POST /letters/{letterId}/report` - 편지 신고
- `POST /letters/{letterId}/reply` - 편지 답장 (자동 친구 추가)
- `DELETE /letters/{letterId}` - 편지 삭제

#### 친구 API
- `POST /friends/invite` - 초대 코드로 친구 추가 (검증 및 양방향 관계 생성)
- `GET /friends` - 친구 목록 조회
- `DELETE /friends/{friendId}` - 친구 삭제 (양방향 관계 삭제)

#### 꽃다발 API
- `GET /bouquets` - 꽃다발 목록 (읽지 않은 편지 수 포함)
- `GET /bouquets/{friendId}/letters` - 친구별 편지 목록 (Letter 테이블 직접 조회)
- `PUT /bouquets/{friendId}/name` - 꽃다발 이름 변경

#### 초대 코드 API
- `POST /invite-codes/generate` - 초대 코드 생성
- `GET /invite-codes/current` - 현재 초대 코드 조회

#### 알림 API
- `GET /notifications` - 알림 목록 (카테고리별 필터링 지원)
- `PUT /notifications/{notificationId}/read` - 읽음 처리
- `PUT /notifications/read-all` - 전체 읽음 처리 (배치 업데이트 최적화)
- `DELETE /notifications/{notificationId}` - 알림 삭제

### 4. Swagger UI 사용

1. 브라우저에서 접속:
   ```
   http://localhost:8080/api/v1/swagger-ui/index.html
   ```

2. "Authorize" 버튼 클릭

3. JWT 토큰 입력:
   ```
   Bearer {your_token}
   ```

4. API 테스트:
   - 각 엔드포인트의 "Try it out" 버튼 클릭
   - 파라미터 입력
   - "Execute" 버튼 클릭

---

## 개발 가이드

### 코드 스타일

#### 1. 패키지 구조
- 도메인별로 패키지 분리 (auth, user, letter 등)
- 각 패키지 내: controller, service, repository, dto, entity

#### 2. 네이밍 컨벤션
- **클래스**: PascalCase (UserService, LetterController)
- **메서드**: camelCase (getUser, createLetter)
- **상수**: UPPER_SNAKE_CASE (MAX_SIZE, DEFAULT_TIMEOUT)

#### 3. 예외 처리
```java
// BusinessException 사용
throw new BusinessException(ErrorCode.USER_NOT_FOUND);

// GlobalExceptionHandler가 자동 처리
```

#### 4. 트랜잭션 관리
```java
@Transactional  // 쓰기 작업
@Transactional(readOnly = true)  // 읽기 작업
```

### 새로운 기능 추가하기

#### 1. 엔티티 생성
```java
@Entity
@Table(name = "new_table")
public class NewEntity extends BaseEntity {
    // 필드 정의
}
```

#### 2. Repository 생성
```java
@Repository
public interface NewRepository extends JpaRepository<NewEntity, String> {
    // 커스텀 쿼리
}
```

#### 3. Service 생성
```java
@Service
@RequiredArgsConstructor
public class NewService {
    private final NewRepository repository;
    
    @Transactional
    public NewDto create(NewCreateRequest request) {
        // 비즈니스 로직
    }
}
```

#### 4. Controller 생성
```java
@RestController
@RequestMapping("/new")
@RequiredArgsConstructor
public class NewController {
    private final NewService service;
    
    @PostMapping
    public ResponseEntity<ApiResponse<NewDto>> create(@RequestBody NewCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.create(request)));
    }
}
```

### 디버깅

#### 1. 로그 레벨 변경
`application-dev.yml`:
```yaml
logging:
  level:
    com.taba: DEBUG
    org.hibernate.SQL: DEBUG
```

#### 2. SQL 쿼리 확인
개발 환경에서 자동으로 콘솔에 출력됩니다.

#### 3. JWT 토큰 디버깅
https://jwt.io 에서 토큰 디코딩 가능

### 테스트

```bash
# 단위 테스트 실행
./gradlew test

# 특정 테스트만 실행
./gradlew test --tests "UserServiceTest"
```

---

## 문제 해결

### 1. 데이터베이스 연결 실패

**증상**: `Communications link failure`

**해결**:
- MySQL 서비스가 실행 중인지 확인
- 포트 번호 확인 (기본: 3306)
- 사용자 권한 확인

### 2. 포트 충돌

**증상**: `Port 8080 is already in use`

**해결**:
```yaml
# application.yml
server:
  port: 8081  # 다른 포트 사용
```

### 3. JWT 토큰 오류

**증상**: `Invalid token`

**해결**:
- 토큰 만료 확인 (7일)
- JWT_SECRET 환경 변수 확인
- Authorization 헤더 형식 확인: `Bearer {token}`
- 로그아웃된 토큰인지 확인 (Redis 블랙리스트)

### 4. 빌드 실패

**증상**: `Could not resolve dependencies`

**해결**:
```bash
./gradlew clean build --refresh-dependencies
```

### 5. Redis 연결 실패

**증상**: `Unable to connect to Redis`

**해결**:
- Redis 서비스 실행 확인
- Redis 포트 확인 (기본: 6379)
- Redis 비밀번호 확인 (설정된 경우)

```bash
# Redis 서비스 확인
redis-cli ping
# 응답: PONG
```

---

## 배포

### 프로덕션 빌드

```bash
./gradlew clean build -x test
```

### JAR 파일 실행

```bash
java -jar build/libs/taba-backend-1.0.0.jar \
  --spring.profiles.active=prod \
  --DB_USERNAME=prod_user \
  --DB_PASSWORD=prod_password
```

### Docker (선택사항)

```dockerfile
FROM openjdk:17-jdk-slim
COPY build/libs/taba-backend-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

---

## 추가 리소스

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring Security 가이드](https://spring.io/guides/topicals/spring-security-architecture)
- [JPA 공식 문서](https://spring.io/projects/spring-data-jpa)
- [MySQL 공식 문서](https://dev.mysql.com/doc/)

---

## 라이선스

MIT License

