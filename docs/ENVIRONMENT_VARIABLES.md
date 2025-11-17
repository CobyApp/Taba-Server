# 환경 변수 가이드

Taba Backend에서 사용하는 모든 환경 변수를 정리한 문서입니다.

## 📋 필수 환경 변수

### 데이터베이스

| 변수 | 설명 | 예시 | 기본값 |
|------|------|------|--------|
| `DB_HOST` | 데이터베이스 호스트 | `localhost` 또는 `mysql` | `localhost` |
| `DB_PORT` | 데이터베이스 포트 | `3306` | `3306` |
| `DB_NAME` | 데이터베이스 이름 | `taba` | `taba` |
| `DB_USERNAME` | 데이터베이스 사용자 | `root` 또는 `taba_user` | `root` |
| `DB_PASSWORD` | 데이터베이스 비밀번호 | `MySecurePassword123!` | 없음 (필수) |

### JWT

| 변수 | 설명 | 예시 | 기본값 |
|------|------|------|--------|
| `JWT_SECRET` | JWT 서명 키 (256비트) | `openssl rand -hex 32` 출력 | 없음 (필수) |
| `JWT_EXPIRATION` | JWT 만료 시간 (밀리초) | `604800000` (7일) | `604800000` |

### 서버

| 변수 | 설명 | 예시 | 기본값 |
|------|------|------|--------|
| `SERVER_PORT` | 서버 포트 | `8080` | `8080` |
| `SERVER_URL` | 서버 전체 URL | `http://cobyserver.iptime.org:8080/api/v1` | 없음 (필수) |
| `SPRING_PROFILES_ACTIVE` | Spring 프로파일 | `dev` 또는 `prod` | `dev` |

## 🔧 선택사항 환경 변수

### Redis

| 변수 | 설명 | 예시 | 기본값 |
|------|------|------|--------|
| `REDIS_HOST` | Redis 호스트 | `localhost` 또는 `redis` | `localhost` |
| `REDIS_PORT` | Redis 포트 | `6379` | `6379` |
| `REDIS_PASSWORD` | Redis 비밀번호 | `redis_password` | 빈 문자열 |

### 파일 업로드

| 변수 | 설명 | 예시 | 기본값 |
|------|------|------|--------|
| `FILE_UPLOAD_DIR` | 파일 업로드 디렉토리 | `/app/uploads` | `uploads` |

### 메일 (선택사항)

| 변수 | 설명 | 예시 | 기본값 |
|------|------|------|--------|
| `MAIL_HOST` | SMTP 서버 호스트 | `smtp.gmail.com` | `smtp.gmail.com` |
| `MAIL_PORT` | SMTP 서버 포트 | `587` | `587` |
| `MAIL_USERNAME` | SMTP 사용자 이름 | `your_email@gmail.com` | 없음 |
| `MAIL_PASSWORD` | SMTP 비밀번호 | `your_app_password` | 없음 |

## 🖥️ 로컬 개발 환경 설정

```bash
# 필수 환경 변수
export DB_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -hex 32)

# 선택사항 환경 변수
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=taba
export DB_USERNAME=root
export SPRING_PROFILES_ACTIVE=dev
```

## 🌐 서버 배포 환경 설정

### GitHub Actions (자동 배포)

GitHub Secrets에 다음을 설정:

**필수 Secrets (7개)**:
1. `SSH_PRIVATE_KEY` - SSH 개인 키
2. `SSH_USER` - `coby`
3. `DB_NAME` - `taba`
4. `DB_USERNAME` - `taba_user`
5. `DB_PASSWORD` - 데이터베이스 비밀번호
6. `JWT_SECRET` - `openssl rand -hex 32` 출력
7. `SERVER_URL` - `http://cobyserver.iptime.org:8080/api/v1`

**선택사항 Secrets**:
- `JWT_EXPIRATION` - `604800000` (기본값 사용 가능)
- `REDIS_PASSWORD` - (없으면 빈 문자열)

상세한 설정 방법은 [GitHub Secrets 설정 가이드](GITHUB_SECRETS.md)를 참고하세요.

### 수동 배포

```bash
# 환경 변수 설정
export DB_NAME=taba
export DB_USERNAME=taba_user
export DB_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -hex 32)
export SERVER_URL=http://cobyserver.iptime.org:8080/api/v1

# 배포 실행
./deploy.sh coby@cobyserver.iptime.org 8080
```

## 🔐 보안 권장사항

1. **강력한 비밀번호 사용**
   - `DB_PASSWORD`: 최소 16자, 대소문자, 숫자, 특수문자 포함
   - `JWT_SECRET`: 반드시 `openssl rand -hex 32`로 생성

2. **환경 변수 관리**
   - 로컬: 환경 변수 직접 설정 (`.env` 파일 사용 안 함)
   - 서버: GitHub Secrets 사용 (자동 배포)
   - 절대 코드에 커밋하지 않기

3. **정기 업데이트**
   - 비밀번호는 3-6개월마다 변경 권장
   - JWT_SECRET 변경 시 모든 사용자 재로그인 필요

## 📝 환경 변수 확인

### 로컬 개발

```bash
# 현재 설정된 환경 변수 확인
env | grep -E "DB_|JWT_|SERVER_|REDIS_"
```

### 서버 배포

```bash
# Docker 컨테이너의 환경 변수 확인
ssh coby@cobyserver.iptime.org
cd ~/taba_backend
docker-compose exec backend env | grep -E "DB_|JWT_|SERVER_|REDIS_"
```

---

**최종 업데이트**: 2024-12-XX

