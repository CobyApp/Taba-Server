# 환경 변수 요약

Taba Backend 프로젝트에서 사용하는 환경 변수에 대한 빠른 참조 가이드입니다.

> **상세 가이드**: [GitHub Secrets 설정 가이드](docs/GITHUB_SECRETS_SETUP.md) - 환경별 시크릿 설정 방법

## 🔴 필수 환경 변수

### 배포 관련 (GitHub Secrets)

#### SSH 배포 설정
- `SSH_PRIVATE_KEY` - SSH 개인 키 (배포용)
- `SSH_HOST` - 서버 IP 주소 또는 도메인
- `SSH_USER` - SSH 사용자명 (예: `coby`)

#### Firebase 설정
- `FCM_SERVICE_ACCOUNT_KEY_JSON` - Firebase 서비스 계정 키 JSON 전체 내용

### 데이터베이스 (환경별 분리)

#### 프로덕션 (`*_PROD`)
- `DB_NAME_PROD` - 프로덕션 데이터베이스 이름
- `DB_USERNAME_PROD` - 프로덕션 데이터베이스 사용자명
- `DB_PASSWORD_PROD` - 프로덕션 데이터베이스 비밀번호 ⚠️ **강력한 비밀번호 필수**
- `DB_EXTERNAL_PORT_PROD` - 프로덕션 MySQL 외부 포트 (기본값: `3306`)

#### 개발 (`*_DEV`)
- `DB_NAME_DEV` - 개발 데이터베이스 이름
- `DB_USERNAME_DEV` - 개발 데이터베이스 사용자명
- `DB_PASSWORD_DEV` - 개발 데이터베이스 비밀번호
- `DB_EXTERNAL_PORT_DEV` - 개발 MySQL 외부 포트 (기본값: `3307`)

### 인증 (환경별 분리)

#### 프로덕션
- `JWT_SECRET_PROD` - 프로덕션 JWT 서명 키 (256비트, 64자리) ⚠️ **필수**
  ```bash
  openssl rand -hex 32
  ```

#### 개발
- `JWT_SECRET_DEV` - 개발 JWT 서명 키 (256비트, 64자리) ⚠️ **필수**
  ```bash
  openssl rand -hex 32
  ```

### 서버 설정 (환경별 분리)

#### 프로덕션
- `SERVER_URL_PROD` - 프로덕션 서버 전체 API URL ⚠️ **필수**
  - 예: `https://www.taba.asia/api/v1`
- `EXTERNAL_PORT_PROD` - 프로덕션 백엔드 외부 포트 (기본값: `8080`)

#### 개발
- `SERVER_URL_DEV` - 개발 서버 전체 API URL ⚠️ **필수**
  - 예: `https://dev.taba.asia/api/v1`
- `EXTERNAL_PORT_DEV` - 개발 백엔드 외부 포트 (기본값: `8081`)

### Redis (환경별 분리, 선택사항)

#### 프로덕션
- `REDIS_PASSWORD_PROD` - 프로덕션 Redis 비밀번호 (기본값: 빈 문자열)
- `REDIS_EXTERNAL_PORT_PROD` - 프로덕션 Redis 외부 포트 (기본값: `6379`)

#### 개발
- `REDIS_PASSWORD_DEV` - 개발 Redis 비밀번호 (기본값: 빈 문자열)
- `REDIS_EXTERNAL_PORT_DEV` - 개발 Redis 외부 포트 (기본값: `6380`)

---

## 🟡 선택사항 환경 변수 (기본값 있음)

### 공통 설정

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `DB_HOST` | `mysql` (Docker), `localhost` (로컬) | 데이터베이스 호스트 |
| `DB_PORT` | `3306` | 데이터베이스 포트 |
| `REDIS_HOST` | `redis` (Docker), `localhost` (로컬) | Redis 호스트 |
| `REDIS_PORT` | `6379` | Redis 포트 |
| `JWT_EXPIRATION` | `604800000` (7일) | JWT 만료 시간 (밀리초) |
| `SERVER_PORT` | `8080` | 서버 포트 |
| `SPRING_PROFILES_ACTIVE` | `prod` (프로덕션), `dev` (로컬) | Spring 프로파일 |
| `FILE_UPLOAD_DIR` | `/app/uploads` | 파일 업로드 디렉토리 |

### 메일 설정 (비밀번호 찾기 기능 사용 시 필수)

#### GitHub Secrets 설정 (프로덕션/개발 공통)

**공통 설정 (Gmail):**
- `MAIL_USERNAME` - 발신 이메일 주소 (예: `your-email@gmail.com`) ⚠️ **필수**
- `MAIL_PASSWORD` - Gmail 앱 비밀번호 (16자리) ⚠️ **필수**
- `MAIL_HOST` (선택, 기본값: `smtp.gmail.com`)
- `MAIL_PORT` (선택, 기본값: `587`)

**프론트엔드 URL (환경별 분리):**
- `FRONTEND_URL_PROD` (선택, 기본값: `http://localhost:3000`)
- `FRONTEND_URL_DEV` (선택, 기본값: `http://localhost:3000`)

#### 로컬 개발 환경 설정

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `MAIL_HOST` | `smtp.gmail.com` | SMTP 서버 호스트 (Gmail: `smtp.gmail.com`) |
| `MAIL_PORT` | `587` | SMTP 서버 포트 (Gmail: `587`) |
| `MAIL_USERNAME` | 빈 문자열 | **필수** - 발신 이메일 주소 (예: `your-email@gmail.com`) |
| `MAIL_PASSWORD` | 빈 문자열 | **필수** - 이메일 비밀번호 또는 앱 비밀번호 (Gmail은 앱 비밀번호 사용 권장) |
| `FRONTEND_URL` | `http://localhost:3000` | 프론트엔드 URL (비밀번호 재설정 링크용, 선택사항) |

**Gmail 사용 시 설정 방법:**
1. Google 계정 → 보안 → 2단계 인증 활성화
2. 앱 비밀번호 생성: [Google 계정 관리](https://myaccount.google.com/apppasswords)
3. 생성된 16자리 앱 비밀번호를 `MAIL_PASSWORD`에 설정 (공백 제거)
4. `MAIL_USERNAME`에는 Gmail 주소 설정 (예: `your-email@gmail.com`)

**다른 이메일 서비스 사용 시:**
- Outlook: `MAIL_HOST=smtp-mail.outlook.com`, `MAIL_PORT=587`
- 네이버: `MAIL_HOST=smtp.naver.com`, `MAIL_PORT=465` (SSL) 또는 `587` (TLS)
- 다음: `MAIL_HOST=smtp.daum.net`, `MAIL_PORT=465` (SSL) 또는 `587` (TLS)

---

## 📋 빠른 설정 가이드

### 로컬 개발 환경

```bash
export DB_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -hex 32)
export SERVER_URL=http://localhost:8080/api/v1
```

### GitHub Secrets 설정 (프로덕션 + 개발)

**필수 시크릿 (총 18개)**:

#### 공통 (3개)
1. `SSH_PRIVATE_KEY` - SSH 개인 키
2. `SSH_HOST` - 서버 IP 또는 도메인
3. `SSH_USER` - SSH 사용자명
4. `FCM_SERVICE_ACCOUNT_KEY_JSON` - Firebase JSON 전체 내용

#### 프로덕션 (5개)
1. `DB_NAME_PROD` - 프로덕션 데이터베이스 이름
2. `DB_USERNAME_PROD` - 프로덕션 데이터베이스 사용자명
3. `DB_PASSWORD_PROD` - 프로덕션 데이터베이스 비밀번호
4. `JWT_SECRET_PROD` - 프로덕션 JWT 서명 키 (`openssl rand -hex 32`)
5. `SERVER_URL_PROD` - 프로덕션 서버 URL

#### 개발 (5개)
1. `DB_NAME_DEV` - 개발 데이터베이스 이름
2. `DB_USERNAME_DEV` - 개발 데이터베이스 사용자명
3. `DB_PASSWORD_DEV` - 개발 데이터베이스 비밀번호
4. `JWT_SECRET_DEV` - 개발 JWT 서명 키 (`openssl rand -hex 32`)
5. `SERVER_URL_DEV` - 개발 서버 URL

**선택사항 시크릿** (기본값 사용 가능):
- `DB_EXTERNAL_PORT_PROD` (기본값: 3306)
- `DB_EXTERNAL_PORT_DEV` (기본값: 3307)
- `REDIS_PASSWORD_PROD` (기본값: 빈 문자열)
- `REDIS_PASSWORD_DEV` (기본값: 빈 문자열)
- `REDIS_EXTERNAL_PORT_PROD` (기본값: 6379)
- `REDIS_EXTERNAL_PORT_DEV` (기본값: 6380)
- `EXTERNAL_PORT_PROD` (기본값: 8080)
- `EXTERNAL_PORT_DEV` (기본값: 8081)

---

## 🔍 환경 변수 확인 방법

### GitHub Actions 로그에서 확인

1. GitHub 저장소 → **Actions** 탭
2. 최근 워크플로우 실행 클릭
3. **Deploy Application** 단계에서 환경 변수 확인
4. **주의**: 실제 비밀번호는 마스킹되어 표시됨 (`***`)

### 서버에서 확인

```bash
# 프로덕션 백엔드 컨테이너 환경 변수 확인
docker exec taba-backend-prod printenv | grep -E "(DB_|JWT_|SERVER_)"

# 개발 백엔드 컨테이너 환경 변수 확인
docker exec taba-backend-dev printenv | grep -E "(DB_|JWT_|SERVER_)"

# MySQL 컨테이너 확인
docker exec taba-mysql-prod printenv | grep MYSQL
docker exec taba-mysql-dev printenv | grep MYSQL
```

---

## ⚠️ 주의사항

1. **환경별 분리**
   - 프로덕션과 개발 환경은 완전히 독립적으로 동작합니다
   - 각 환경은 다른 디렉토리(`~/taba_backend_prod`, `~/taba_backend_dev`)와 포트를 사용합니다
   - 프로덕션과 개발 환경이 동시에 실행 가능합니다

2. **포트 충돌 방지**
   - **프로덕션**: MySQL(3306), Redis(6379), Backend(8080)
   - **개발**: MySQL(3307), Redis(6380), Backend(8081)
   - 포트를 변경할 경우, 반드시 서로 다른 포트를 사용해야 합니다

3. **비밀번호 보안**
   - 프로덕션 비밀번호는 반드시 강력하게 설정하세요
   - 개발/프로덕션 비밀번호를 다르게 설정하세요
   - 정기적으로 비밀번호 변경을 권장합니다

4. **JWT_SECRET**
   - 절대 공개 저장소에 커밋하지 마세요
   - 프로덕션/개발 환경별로 다른 키를 사용하세요
   - 키 변경 시 기존 토큰이 모두 무효화됩니다

---

## 🔗 관련 문서

- [GitHub Secrets 설정 가이드](docs/GITHUB_SECRETS_SETUP.md) - 상세한 시크릿 설정 방법 (환경별 분리)
- [README.md](README.md) - 프로젝트 개요 및 빠른 시작
