# 프로젝트 가이드

## 프로젝트 구조

```
src/main/java/com/taba/
├── auth/              # 인증 (JWT, 회원가입, 로그인)
├── user/              # 사용자 관리
├── letter/            # 편지 관리
├── friendship/        # 친구 관계
├── invite/            # 초대 코드
├── notification/      # 알림
├── settings/          # 설정
├── file/              # 파일 업로드
└── common/            # 공통 유틸리티 (예외 처리, DTO, 설정)
```

## 기술 스택

- **Spring Boot 3.2.0** - 핵심 프레임워크
- **Java 17** - 프로그래밍 언어
- **Spring Security 6.x** - JWT 기반 인증
- **Spring Data JPA** - 데이터베이스 접근
- **MySQL 8.0** - 관계형 데이터베이스
- **Redis** - 토큰 블랙리스트 (선택사항)
- **QueryDSL 5.0.0** - 타입 안전한 동적 쿼리
- **MapStruct 1.5.5** - DTO 매핑 자동화
- **Swagger/OpenAPI 3** - API 문서화
- **Docker** - 컨테이너화

## 주요 기능

- **인증**: JWT 기반 회원가입/로그인, 비밀번호 재설정
- **사용자**: 프로필 이미지 업로드, 프로필 수정
- **편지**: 공개/비공개 편지 작성, 이미지 첨부, 예약 발송, 답장
- **친구**: 초대 코드 기반 친구 추가 (6자리, 3분 유효)
- **알림**: 실시간 알림, FCM 푸시 알림 지원
- **파일**: 이미지 업로드 (로컬 저장)

## API 엔드포인트

자세한 API 명세는 [API_SPECIFICATION.md](API_SPECIFICATION.md)를 참고하세요.

### 주요 엔드포인트
- `/auth/**` - 인증 API
- `/users/**` - 사용자 API
- `/friends/**` - 친구 API
- `/letters/**` - 편지 API
- `/invite-codes/**` - 초대 코드 API
- `/notifications/**` - 알림 API
- `/settings/**` - 설정 API
- `/files/**` - 파일 API

## 개발 가이드

### 코드 스타일
- 도메인별로 패키지 분리
- DTO와 Entity 분리
- MapStruct를 사용한 매핑
- QueryDSL을 사용한 동적 쿼리

### 예외 처리
- `BusinessException` - 비즈니스 로직 예외
- `GlobalExceptionHandler` - 전역 예외 처리
- `ErrorCode` - 에러 코드 정의

### 테스트
```bash
./gradlew test
```

---

## 더 알아보기

- [빠른 시작](QUICK_START.md)
- [API 명세서](API_SPECIFICATION.md)
- [환경 변수 가이드](ENVIRONMENT_VARIABLES.md)
- [배포 가이드](DOCKER_DEPLOYMENT.md)
