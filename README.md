# Taba Backend

Taba 백엔드 서버 - Spring Boot 기반 REST API

## 기술 스택

- **Spring Boot 3.2.0**
- **Java 17**
- **Spring Security 6.x** (JWT 인증)
- **Spring Data JPA**
- **MySQL 8.0**
- **Redis** (캐싱)
- **QueryDSL** (복잡한 쿼리)
- **MapStruct** (DTO 매핑)
- **Swagger/OpenAPI 3** (API 문서화)

## 프로젝트 구조

```
src/main/java/com/taba/
├── auth/              # 인증 관련
├── user/              # 사용자 관리
├── letter/            # 편지 관리
├── friendship/        # 친구 관계 및 꽃다발
├── invite/            # 초대 코드
├── notification/      # 알림
├── settings/           # 설정
├── file/              # 파일 업로드
└── common/            # 공통 유틸리티
```

## 초기 설정

### Gradle Wrapper 생성 (최초 1회)

Gradle이 설치되어 있지 않은 경우:

```bash
# Gradle 설치 후
gradle wrapper --gradle-version 8.5
```

또는 직접 다운로드:
- https://gradle.org/releases/ 에서 Gradle 8.5 다운로드
- `gradle/wrapper/gradle-wrapper.jar` 파일을 다운로드하여 배치

## 실행 방법

### 1. 환경 변수 설정

`.env` 파일을 생성하거나 환경 변수를 설정합니다:

```bash
DB_USERNAME=root
DB_PASSWORD=password
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=your-256-bit-secret-key-change-this-in-production
AWS_S3_BUCKET=taba-bucket
AWS_S3_REGION=ap-northeast-2
```

### 2. 데이터베이스 설정

MySQL 데이터베이스를 생성합니다:

```sql
CREATE DATABASE taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

또는

```bash
./gradlew build
java -jar build/libs/taba-backend-1.0.0.jar
```

## API 문서

애플리케이션 실행 후 다음 URL에서 Swagger UI를 확인할 수 있습니다:

- http://localhost:8080/api/v1/swagger-ui/index.html

## 주요 기능

### 인증
- 회원가입
- 로그인 (JWT 토큰 발급)
- 비밀번호 찾기/재설정
- 비밀번호 변경

### 사용자
- 프로필 조회/수정

### 편지
- 편지 작성
- 공개 편지 목록 조회 (하늘)
- 편지 상세 조회
- 편지 좋아요/저장
- 편지 신고/삭제

### 꽃다발
- 친구 목록 조회
- 친구별 편지 목록 조회
- 꽃다발 이름 변경

### 친구
- 초대 코드로 친구 추가
- 친구 목록 조회
- 친구 삭제

### 초대 코드
- 초대 코드 생성
- 현재 초대 코드 조회

### 알림
- 알림 목록 조회
- 알림 읽음 처리
- 알림 삭제

### 설정
- 푸시 알림 설정
- 언어 설정

### 파일 업로드
- 이미지 업로드

## 개발 환경

- JDK 17 이상
- Gradle 8.x
- MySQL 8.0
- Redis 6.x

## 빌드

```bash
./gradlew clean build
```

## 라이선스

MIT

