# Taba Backend

Taba 백엔드 서버 - Spring Boot 기반 REST API

> **시작하기**: [USAGE.md](USAGE.md)를 먼저 읽어보세요!

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

## 프로젝트 구조

```
src/main/java/com/taba/
├── auth/              # 인증 관련
├── user/              # 사용자 관리
├── letter/            # 편지 관리
├── friendship/        # 친구 관계
├── invite/            # 초대 코드
├── notification/      # 알림
├── settings/          # 설정
├── file/              # 파일 업로드
└── common/            # 공통 유틸리티
```

## 빠른 시작

```bash
# 1. 환경 변수 설정 (로컬 개발용)
export DB_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -hex 32)

# 2. 데이터베이스 생성
mysql -u root -p -e "CREATE DATABASE taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 3. 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
```

**상세 가이드**: [USAGE.md](USAGE.md) 참고

## API 문서

애플리케이션 실행 후 다음 URL에서 Swagger UI를 확인할 수 있습니다:

- **로컬**: http://localhost:8080/api/v1/swagger-ui/index.html
- **프로덕션**: https://www.taba.asia/api/v1/swagger-ui/index.html

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
- 초대 코드 생성/조회 (3분 유효, 6자리 숫자+영문 조합)
- 초대 코드로 친구 추가 (양방향 관계 생성)
- 친구 목록 조회
- 친구별 편지 목록 조회 (읽지 않은 편지 수 포함)
- 친구 삭제 (양방향 관계 삭제)

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

## 배포

### 자동 배포 (GitHub Actions) - 권장 ⭐

```bash
git push origin main     # 프로덕션 배포
git push origin release  # 프로덕션 배포
git push origin develop  # 개발 환경 배포
```

**특징**:
- ✅ **자동 배포**: GitHub Actions를 통한 자동 배포
- ✅ **자동 헬스체크**: 배포 후 새 인스턴스가 정상 작동하는지 확인
- 🔄 **환경 분리**: 프로덕션/개발 환경 분리 배포

**초기 설정** (최초 1회): 
- [설정 체크리스트](SETUP_CHECKLIST.md) - GitHub Secrets 및 SSH 키 설정 포함
- [환경 변수 요약](ENVIRONMENT_VARIABLES.md) - 필수 환경 변수 목록

### 수동 배포

```bash
./deploy.sh coby@cobyserver.iptime.org 8080
```

### Docker 로컬 실행

```bash
docker-compose up -d
```

## 📚 문서

### 빠른 시작
- **[사용 가이드](USAGE.md)** ⭐ - 핵심 사용법 정리
- **[빠른 참조 가이드](QUICK_REFERENCE.md)** ⭐ - 자주 사용하는 명령어와 설정
- **[설정 체크리스트](SETUP_CHECKLIST.md)** ⭐ - 초기 설정 확인 항목
- **[환경 변수 요약](ENVIRONMENT_VARIABLES.md)** - 환경 변수 빠른 참조

### API 문서
- **[API 명세서 (개발 환경)](docs/API_SPECIFICATION_DEV.md)** - 개발 환경 API 엔드포인트 상세 명세
- **[API 명세서 (프로덕션)](docs/API_SPECIFICATION_PROD.md)** - 프로덕션 환경 API 엔드포인트 상세 명세

### 문제 해결
- **[502 Bad Gateway 해결 가이드](docs/TROUBLESHOOTING_502.md)** - 502 에러 진단 및 해결 방법

## 라이선스

MIT
