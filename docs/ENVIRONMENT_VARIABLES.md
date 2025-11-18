# 환경 변수 가이드

## 📋 필수 환경 변수

| 변수 | 설명 | 예시 | GitHub Secret |
|------|------|------|---------------|
| `DB_NAME` | 데이터베이스 이름 | `taba` | `DB_NAME` |
| `DB_USERNAME` | 데이터베이스 사용자명 | `taba_user` | `DB_USERNAME` |
| `DB_PASSWORD` | 데이터베이스 비밀번호 | `MySecurePassword123!` | `DB_PASSWORD` |
| `JWT_SECRET` | JWT 서명 키 (256비트, 64자리) | `openssl rand -hex 32` 출력 | `JWT_SECRET` |
| `SERVER_URL` | 서버 전체 URL (파일 업로드 URL 생성용) | `https://www.taba.asia/api/v1` | `SERVER_URL` |
| `SSH_PRIVATE_KEY` | SSH 개인 키 (배포용) | SSH 키 전체 내용 | `SSH_PRIVATE_KEY` |
| `SSH_USER` | SSH 사용자명 (배포용) | `coby` | `SSH_USER` |
| `FCM_SERVICE_ACCOUNT_KEY_JSON` | Firebase 서비스 계정 키 JSON (전체 내용) | JSON 파일 전체 내용 | `FCM_SERVICE_ACCOUNT_KEY_JSON` |

## 🔧 선택사항 환경 변수 (기본값 있음)

### 데이터베이스
| 변수 | 설명 | 기본값 |
|------|------|--------|
| `DB_HOST` | 데이터베이스 호스트 | `mysql` (프로덕션), `localhost` (로컬) |
| `DB_PORT` | 데이터베이스 포트 | `3306` |

### Redis (선택사항 - 토큰 블랙리스트용)
| 변수 | 설명 | 기본값 |
|------|------|--------|
| `REDIS_HOST` | Redis 호스트 | `redis` (프로덕션), `localhost` (로컬) |
| `REDIS_PORT` | Redis 포트 | `6379` |
| `REDIS_PASSWORD` | Redis 비밀번호 | 빈 문자열 |

### JWT
| 변수 | 설명 | 기본값 |
|------|------|--------|
| `JWT_EXPIRATION` | JWT 만료 시간 (밀리초) | `604800000` (7일) |

### 서버 설정
| 변수 | 설명 | 기본값 |
|------|------|--------|
| `SERVER_PORT` | 서버 포트 | `8080` |
| `SPRING_PROFILES_ACTIVE` | Spring 프로파일 | `prod` (프로덕션), `dev` (로컬) |
| `FILE_UPLOAD_DIR` | 파일 업로드 디렉토리 | `/app/uploads` |

### 메일 (선택사항 - 비밀번호 재설정 등)
| 변수 | 설명 | 기본값 |
|------|------|--------|
| `MAIL_HOST` | SMTP 서버 호스트 | `smtp.gmail.com` |
| `MAIL_PORT` | SMTP 서버 포트 | `587` |
| `MAIL_USERNAME` | SMTP 사용자명 (이메일) | 빈 문자열 |
| `MAIL_PASSWORD` | SMTP 비밀번호 (앱 비밀번호) | 빈 문자열 |

### 배포 관련
| 변수 | 설명 | 기본값 |
|------|------|--------|
| `EXTERNAL_PORT` | 외부 포트 (배포 스크립트용) | `8080` |

---

## 🖥️ 로컬 개발 환경 설정

```bash
# 필수 환경 변수
export DB_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -hex 32)

# 선택사항 (기본값 사용 가능)
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=taba
export DB_USERNAME=root
export SPRING_PROFILES_ACTIVE=dev
export SERVER_URL=http://localhost:8080/api/v1
```

---

## 🌐 프로덕션 배포 환경 설정 (GitHub Actions)

GitHub Secrets에 다음을 설정하면 자동으로 환경 변수로 전달됩니다:

### 필수 Secrets (8개)

1. **`SSH_PRIVATE_KEY`** - SSH 개인 키 (배포용)
2. **`SSH_USER`** - SSH 사용자명 (`coby`)
3. **`DB_NAME`** - 데이터베이스 이름 (`taba`)
4. **`DB_USERNAME`** - 데이터베이스 사용자명 (`taba_user`)
5. **`DB_PASSWORD`** - 데이터베이스 비밀번호
6. **`JWT_SECRET`** - JWT 비밀키 (`openssl rand -hex 32` 출력)
7. **`SERVER_URL`** - 서버 전체 URL (`https://www.taba.asia/api/v1`)
8. **`FCM_SERVICE_ACCOUNT_KEY_JSON`** - Firebase 서비스 계정 키 JSON 전체 내용

### 선택사항 Secrets (2개)

- **`JWT_EXPIRATION`** - JWT 만료 시간 (기본값: `604800000` 사용 가능)
- **`REDIS_PASSWORD`** - Redis 비밀번호 (없으면 빈 문자열)

---

## 🔐 환경 변수 생성 방법

### JWT_SECRET 생성
```bash
openssl rand -hex 32
# 출력 예시: a1b2c3d4e5f6... (64자리 16진수 문자열)
```

### FCM_SERVICE_ACCOUNT_KEY_JSON 생성
1. Firebase Console 접속: https://console.firebase.google.com/
2. 프로젝트 선택
3. 프로젝트 설정 > 서비스 계정 탭
4. "새 비공개 키 생성" 클릭
5. 다운로드한 JSON 파일의 **전체 내용**을 복사

### SSH_PRIVATE_KEY 생성
```bash
ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/github_actions_deploy
cat ~/.ssh/github_actions_deploy
# 출력 전체 내용 복사 (-----BEGIN OPENSSH PRIVATE KEY----- 부터 -----END OPENSSH PRIVATE KEY----- 까지)
```

---

## 📝 환경 변수 확인

### 로컬 개발
```bash
env | grep -E "DB_|JWT_|SERVER_|REDIS_|SPRING_"
```

### 프로덕션 (Docker 컨테이너 내부)
```bash
ssh user@server
cd ~/taba_backend
docker-compose exec backend env | grep -E "DB_|JWT_|SERVER_|REDIS_|SPRING_"
```

---

## ⚠️ 중요 사항

1. **절대 코드에 커밋하지 마세요** - 환경 변수는 모두 `.gitignore`에 포함되어야 합니다
2. **강력한 비밀번호 사용** - `DB_PASSWORD`: 최소 16자, 대소문자, 숫자, 특수문자 포함
3. **JWT_SECRET은 반드시 안전하게 생성** - `openssl rand -hex 32` 사용 (64자리)
4. **정기 업데이트** - 비밀번호는 3-6개월마다 변경 권장
5. **JWT_SECRET 변경 시** - 모든 사용자가 재로그인 필요

---

## 📚 참고 문서

- [GitHub Secrets 설정 가이드](GITHUB_SECRETS.md) - GitHub Actions Secrets 설정 방법
- [환경 변수 상세 설명](../ENVIRONMENT_VARIABLES.md) - 각 환경 변수의 상세 설명
