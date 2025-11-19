# 환경 변수 요약

## 🔴 필수 환경 변수 (GitHub Secrets)

### 배포 관련
- `SSH_PRIVATE_KEY` - SSH 개인 키 (배포용)
- `SSH_USER` - SSH 사용자명 (`coby`)

### 데이터베이스
- `DB_NAME` - 데이터베이스 이름 (`taba`)
- `DB_USERNAME` - 데이터베이스 사용자명 (`taba_user`)
- `DB_PASSWORD` - 데이터베이스 비밀번호 ⚠️ **필수**

### 인증
- `JWT_SECRET` - JWT 서명 키 (256비트, 64자리) ⚠️ **필수**
  ```bash
  openssl rand -hex 32
  ```

### 서버 설정
- `SERVER_URL` - 서버 전체 URL ⚠️ **필수**
  - 프로덕션: `https://www.taba.asia/api/v1`
  - 로컬: `http://localhost:8080/api/v1`

### Firebase
- `FCM_SERVICE_ACCOUNT_KEY_JSON` - Firebase 서비스 계정 키 JSON (전체 내용)

---

## 🟡 선택사항 환경 변수 (기본값 있음)

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `DB_HOST` | `mysql` (프로덕션), `localhost` (로컬) | 데이터베이스 호스트 |
| `DB_PORT` | `3306` | 데이터베이스 포트 |
| `REDIS_HOST` | `redis` (프로덕션), `localhost` (로컬) | Redis 호스트 |
| `REDIS_PORT` | `6379` | Redis 포트 |
| `REDIS_PASSWORD` | 빈 문자열 | Redis 비밀번호 |
| `JWT_EXPIRATION` | `604800000` (7일) | JWT 만료 시간 (밀리초) |
| `SERVER_PORT` | `8080` | 서버 포트 |
| `SPRING_PROFILES_ACTIVE` | `prod` (프로덕션), `dev` (로컬) | Spring 프로파일 |
| `FILE_UPLOAD_DIR` | `/app/uploads` | 파일 업로드 디렉토리 |
| `MAIL_HOST` | `smtp.gmail.com` | SMTP 서버 호스트 |
| `MAIL_PORT` | `587` | SMTP 서버 포트 |
| `MAIL_USERNAME` | 빈 문자열 | SMTP 사용자명 |
| `MAIL_PASSWORD` | 빈 문자열 | SMTP 비밀번호 |
| `EXTERNAL_PORT` | `8080` | 외부 포트 (배포 스크립트용) |

---

## 📋 빠른 설정 (로컬 개발)

```bash
export DB_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -hex 32)
export SERVER_URL=http://localhost:8080/api/v1
```

---

## 📋 빠른 설정 (GitHub Secrets)

### 필수 (8개)
1. `SSH_PRIVATE_KEY` - `cat ~/.ssh/github_actions_deploy`
2. `SSH_USER` - `coby`
3. `DB_NAME` - `taba`
4. `DB_USERNAME` - `taba_user`
5. `DB_PASSWORD` - 데이터베이스 비밀번호
6. `JWT_SECRET` - `openssl rand -hex 32`
7. `SERVER_URL` - `https://www.taba.asia/api/v1`
8. `FCM_SERVICE_ACCOUNT_KEY_JSON` - Firebase JSON 전체 내용

### 선택사항 (2개)
- `JWT_EXPIRATION` - `604800000` (기본값 사용 가능)
- `REDIS_PASSWORD` - 빈 문자열 (기본값 사용 가능)

---

## 🔗 관련 문서

- [GitHub Secrets 설정 가이드](docs/GITHUB_SECRETS_SETUP.md) - 상세한 시크릿 설정 방법 (환경별 분리)
- [사용 가이드](USAGE.md) - 환경 변수 설정 방법 포함
- [설정 체크리스트](SETUP_CHECKLIST.md) - GitHub Secrets 설정 포함

