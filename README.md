# Taba Backend

Taba 백엔드 서버 - Spring Boot 기반 REST API

## 기술 스택

- **Spring Boot 3.2.0** - 핵심 프레임워크
- **Java 17** - 프로그래밍 언어
- **Spring Security 6.x** - JWT 기반 인증 및 보안
- **Spring Data JPA** - 데이터베이스 접근 추상화
- **MySQL 8.0** - 관계형 데이터베이스
- **Redis** - 토큰 블랙리스트 관리 (선택사항)
- **QueryDSL 5.0.0** - 타입 안전한 동적 쿼리
- **MapStruct 1.5.5** - DTO 매핑 자동화
- **Swagger/OpenAPI 3** - API 문서화 및 테스트
- **Gradle 8.5** - 빌드 도구
- **HikariCP** - 커넥션 풀링
- **BCrypt** - 비밀번호 암호화
- **JJWT 0.12.3** - JWT 토큰 처리
- **dotenv-java 3.0.0** - .env 파일 지원

## 프로젝트 구조

```
src/main/java/com/taba/
├── auth/              # 인증 관련
├── user/              # 사용자 관리
├── letter/            # 편지 관리
├── friendship/        # 친구 관계 및 꽃다발
├── invite/            # 초대 코드
├── notification/      # 알림
├── settings/          # 설정
├── file/              # 파일 업로드
└── common/            # 공통 유틸리티
```

## 초기 설정

### 1. 환경 변수 설정

`.env` 파일을 프로젝트 루트에 생성합니다:

```bash
# MySQL 설정
DB_HOST=localhost
DB_PORT=3306
DB_NAME=taba
DB_USERNAME=root
DB_PASSWORD=your_password

# Redis 설정 (선택사항)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT 설정
JWT_SECRET=your-256-bit-secret-key-change-this-in-production

# 서버 설정 (선택사항)
SERVER_URL=http://localhost:8080/api/v1
```

### 2. MySQL 데이터베이스 설정

```bash
# MySQL 접속
mysql -u root -p

# 데이터베이스 생성
CREATE DATABASE taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

또는 스크립트 실행:

```bash
mysql -u root -p < src/main/resources/db/init.sql
```

### 3. Redis 설정 (선택사항)

Redis는 토큰 블랙리스트 기능에 사용됩니다. Redis가 없어도 애플리케이션은 정상 작동하지만, 로그아웃된 토큰을 블랙리스트에 추가하는 기능만 비활성화됩니다.

**macOS (Homebrew)**:
```bash
brew install redis
brew services start redis
```

**Linux**:
```bash
sudo apt install redis-server
sudo systemctl start redis
```

## 실행 방법

### 개발 환경

```bash
./gradlew bootRun
```

또는

```bash
./gradlew build
java -jar build/libs/taba-backend-1.0.0.jar
```

### 프로덕션 환경

```bash
./gradlew clean build
java -jar build/libs/taba-backend-1.0.0.jar --spring.profiles.active=prod
```

## API 문서

애플리케이션 실행 후 다음 URL에서 Swagger UI를 확인할 수 있습니다:

- **로컬**: http://localhost:8080/api/v1/swagger-ui/index.html

## 주요 기능

### 인증
- 회원가입
- 로그인 (JWT 토큰 발급)
- 비밀번호 찾기/재설정
- 비밀번호 변경
- 로그아웃 (토큰 블랙리스트 처리)

### 사용자
- 프로필 조회/수정 (친구 수, 보낸 편지 수 포함)

### 편지
- 편지 작성 (예약 발송 지원, 템플릿 및 이미지 첨부)
- 공개 편지 목록 조회 (씨앗 - 로그인 시 자신이 작성한 편지 제외)
- 편지 상세 조회 (자동 읽음 처리)
- 편지 답장 (자동 친구 추가)
- 편지 신고 (중복 신고 방지)
- 편지 삭제

### 친구
- 초대 코드 생성/조회 (3분 유효)
- 초대 코드로 친구 추가 (양방향 관계 생성)
- 친구 목록 조회
- 친구 삭제 (양방향 관계 삭제)

### 꽃다발
- 꽃다발 목록 조회 (친구별, 읽지 않은 편지 수 포함)
- 친구별 편지 목록 조회 (Letter 테이블 직접 조회)
- 꽃다발 이름 변경

### 알림
- 알림 목록 조회 (카테고리별 필터링)
- 알림 읽음 처리 (개별/전체, 배치 최적화)
- 알림 삭제
- 예약 편지 발송 시 자동 알림 생성

### 설정
- 푸시 알림 설정
- 언어 설정

### 파일 업로드
- 이미지 업로드 (로컬 저장, URL 반환)
- 서버 URL 기반 파일 경로 제공

## 개발 환경

- JDK 17 이상
- Gradle 8.x
- MySQL 8.0
- Redis 6.x (선택사항)

## 빌드

```bash
./gradlew clean build
```

## 📚 상세 문서

- **[프로젝트 가이드](docs/PROJECT_GUIDE.md)** - 프로젝트 상세 설명 및 개발 가이드
- **[API 명세서](docs/API_SPECIFICATION.md)** - 전체 API 엔드포인트 상세 명세
- **[빠른 시작 가이드](docs/QUICK_START.md)** - 빠른 실행을 위한 가이드

## 라이선스

MIT
