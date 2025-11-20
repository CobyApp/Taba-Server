# Taba Backend

Taba 백엔드 서버 - Spring Boot 기반 REST API

## 기술 스택

- **Spring Boot 3.2.0** - 핵심 프레임워크
- **Java 17** - 프로그래밍 언어
- **Spring Security 6.x** - JWT 기반 인증 및 보안
- **Spring Data JPA** - 데이터베이스 접근 추상화
- **MySQL 8.0** - 관계형 데이터베이스
- **Redis 7** - 토큰 블랙리스트 관리
- **QueryDSL 5.0.0** - 타입 안전한 동적 쿼리
- **MapStruct 1.5.5** - DTO 매핑 자동화
- **Swagger/OpenAPI 3** - API 문서화 및 테스트
- **Gradle 8.5** - 빌드 도구
- **HikariCP** - 커넥션 풀링
- **BCrypt** - 비밀번호 암호화
- **JJWT 0.12.3** - JWT 토큰 처리
- **Firebase Admin SDK** - FCM 푸시 알림

## 프로젝트 구조

```
src/main/java/com/taba/
├── auth/              # 인증 관련 (회원가입, 로그인, JWT)
├── user/              # 사용자 관리 (프로필 조회/수정)
├── letter/            # 편지 관리 (작성, 조회, 신고, 스케줄러)
├── friendship/        # 친구 관계 (목록, 편지 요약)
├── invite/            # 초대 코드 (생성, 조회, 친구 추가)
├── notification/      # 알림 (목록, 읽음 처리, FCM)
├── settings/          # 설정 (푸시 알림, 언어)
├── file/              # 파일 업로드 (이미지)
└── common/            # 공통 유틸리티 (예외 처리, 보안, 설정)
```

## 빠른 시작

### 로컬 개발 환경

```bash
# 1. 환경 변수 설정
export DB_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -hex 32)
export SERVER_URL=http://localhost:8080/api/v1

# 2. 데이터베이스 생성
mysql -u root -p -e "CREATE DATABASE taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 3. 애플리케이션 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Docker Compose로 실행

```bash
# 개발 환경
docker-compose -f docker-compose.dev.yml up -d

# 프로덕션 환경 (환경 변수 필요)
docker-compose -f docker-compose.prod.yml up -d
```

## API 문서

애플리케이션 실행 후 다음 URL에서 Swagger UI를 확인할 수 있습니다:

- **로컬**: http://localhost:8080/api/v1/swagger-ui/index.html
- **개발 환경**: https://dev.taba.asia/api/v1/swagger-ui/index.html
- **프로덕션**: https://www.taba.asia/api/v1/swagger-ui/index.html

**상세 API 명세서**:
- [API 명세서](docs/API_SPECIFICATION.md) - 개발/프로덕션 환경 공통 API 명세

## 주요 기능

### 인증 (`/auth`)
- 회원가입 (프로필 이미지 업로드 지원)
- 로그인 (JWT 토큰 발급)
- 비밀번호 찾기/재설정 (이메일 발송)
- 로그아웃 (토큰 블랙리스트 처리)

### 사용자 (`/users`)
- 내 프로필 조회 (친구 수, 보낸 편지 수 포함)
- 프로필 수정 (닉네임, 프로필 이미지)

### 편지 (`/letters`)
- 편지 작성 (예약 발송 지원, 템플릿 및 이미지 첨부)
- 공개 편지 목록 조회 (씨앗 - 로그인 시 자신이 작성한 편지 제외)
- 편지 상세 조회 (자동 읽음 처리)
- 편지 답장 (자동 친구 추가)
- 편지 신고 (중복 신고 방지)
- 편지 삭제

### 친구 (`/friends`)
- 초대 코드 생성/조회 (3분 유효, 6자리 숫자+영문 조합)
- 초대 코드로 친구 추가 (양방향 관계 생성)
- 친구 목록 조회
- 친구별 편지 목록 조회 (읽지 않은 편지 수 포함)
- 친구 삭제 (양방향 관계 삭제)

### 알림 (`/notifications`)
- 알림 목록 조회 (카테고리별 필터링)
- 알림 읽음 처리 (개별/전체, 배치 최적화)
- 알림 삭제
- 예약 편지 발송 시 자동 알림 생성 (FCM 푸시 알림)

### 설정 (`/settings`)
- 푸시 알림 설정
- 언어 설정

### 파일 업로드 (`/files`)
- 이미지 업로드 (로컬 저장, URL 반환)
- 서버 URL 기반 파일 경로 제공

## 개발 환경

- **JDK**: 17 이상
- **Gradle**: 8.x
- **MySQL**: 8.0
- **Redis**: 7.x (선택사항, 토큰 블랙리스트용)

## 빌드

```bash
# 빌드
./gradlew clean build

# 테스트 포함 빌드
./gradlew clean build test

# JAR 파일 생성
./gradlew bootJar
```

## 배포

### 자동 배포 (GitHub Actions) - 권장 ⭐

프로젝트는 GitHub Actions를 통해 자동 배포됩니다.

**배포 트리거**:
```bash
git push origin main     # 프로덕션 배포
git push origin release  # 프로덕션 배포
git push origin develop  # 개발 환경 배포
```

**특징**:
- ✅ **자동 배포**: GitHub Actions를 통한 자동 배포
- ✅ **환경 분리**: 프로덕션/개발 환경 완전 분리
  - 프로덕션: `~/taba_backend_prod` 디렉토리, 포트 8080/3306/6379
  - 개발: `~/taba_backend_dev` 디렉토리, 포트 8081/3307/6380
- ✅ **독립 실행**: 두 환경이 동시에 실행 가능
- ✅ **자동 헬스체크**: 배포 후 새 인스턴스가 정상 작동하는지 확인

**초기 설정** (최초 1회):
- [GitHub Secrets 설정 가이드](docs/GITHUB_SECRETS_SETUP.md) - 환경별 시크릿 설정 방법
- [환경 변수 요약](ENVIRONMENT_VARIABLES.md) - 필수 환경 변수 목록

### 수동 배포

```bash
# 서버에 SSH 접속 후
cd ~/taba_backend_prod  # 또는 ~/taba_backend_dev
docker-compose -f docker-compose.prod.yml up -d --build
```

## 환경 변수

프로젝트는 환경별로 다른 설정을 사용합니다:

- **프로덕션**: `application-prod.yml` + 환경 변수 (GitHub Secrets)
- **개발**: `application-dev.yml` + 환경 변수 (기본값 사용 가능)

**필수 환경 변수**:
- `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` - 데이터베이스 설정
- `JWT_SECRET` - JWT 토큰 서명 키 (256비트)
- `SERVER_URL` - 서버 전체 API URL
- `FCM_SERVICE_ACCOUNT_KEY_JSON` - Firebase 서비스 계정 키

**상세 정보**: [환경 변수 요약](ENVIRONMENT_VARIABLES.md)

## 데이터베이스

### 스키마 관리

- **개발 환경**: JPA의 `ddl-auto: update`를 사용하여 자동으로 스키마가 생성/업데이트됩니다.
- **프로덕션 환경**: `ddl-auto: validate`를 사용하여 스키마 검증만 수행합니다.

**스키마 문서**: [src/main/resources/db/schema.sql](src/main/resources/db/schema.sql)

### 목업 데이터

개발/테스트용 목업 데이터를 사용할 수 있습니다:

```bash
# 로컬 환경
mysql -u root -p taba < src/main/resources/db/mock-data.sql

# 개발 환경
mysql -h 서버IP -P 3307 -u taba_user_dev -p taba_dev < src/main/resources/db/mock-data.sql
```

**⚠️ 주의**: 프로덕션 환경에서는 절대 실행하지 마세요!

**관련 파일**:
- [schema.sql](src/main/resources/db/schema.sql) - 데이터베이스 스키마 문서
- [init.sql](src/main/resources/db/init.sql) - 데이터베이스 초기화 스크립트
- [mock-data.sql](src/main/resources/db/mock-data.sql) - 목업 데이터 생성 스크립트

## 📚 문서

### 사용 가이드
- **[사용 가이드](USAGE.md)** - 로컬 개발, 배포, 환경 변수 등 핵심 사용법
- **[설정 체크리스트](SETUP_CHECKLIST.md)** - 초기 설정 시 확인할 항목들

### API 문서
- **[API 명세서](docs/API_SPECIFICATION.md)** - 개발/프로덕션 환경 공통 API 엔드포인트 상세 명세

### 배포 및 설정
- **[GitHub Secrets 설정 가이드](docs/GITHUB_SECRETS_SETUP.md)** - GitHub Actions 자동 배포를 위한 시크릿 설정
- **[환경 변수 요약](ENVIRONMENT_VARIABLES.md)** - 환경 변수 빠른 참조

## 라이선스

MIT
