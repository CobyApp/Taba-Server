# 사용 가이드

Taba Backend 프로젝트의 핵심 사용법을 정리한 문서입니다.

## 📋 목차

1. [로컬 개발](#로컬-개발)
2. [서버 배포](#서버-배포)
3. [환경 변수](#환경-변수)
4. [주요 명령어](#주요-명령어)

---

## 🖥️ 로컬 개발

### 1. 환경 설정

```bash
# 환경 변수 설정 (로컬 개발용)
export DB_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -hex 32)
```

**필수 설정**:
- `DB_PASSWORD`: MySQL 비밀번호
- `JWT_SECRET`: `openssl rand -hex 32` 명령으로 생성

### 2. 데이터베이스 생성

```bash
mysql -u root -p
CREATE DATABASE taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

### 3. 실행

```bash
# 개발 환경 (자동 테이블 생성)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

**확인**: http://localhost:8080/api/v1/swagger-ui/index.html

---

## 🌐 서버 배포

### 자동 배포 (GitHub Actions) - 권장 ⭐

**초기 설정** (최초 1회):
1. SSH 키 생성:
   ```bash
   ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/github_actions_deploy
   ssh-copy-id -i ~/.ssh/github_actions_deploy.pub coby@cobyserver.iptime.org
   ```

2. GitHub Secrets 설정:
   - 저장소 Settings > Secrets and variables > Actions
   - [GitHub Secrets 설정 가이드](docs/GITHUB_SECRETS.md) 참고
   - 필수 Secrets:
     - `SSH_PRIVATE_KEY`, `SSH_USER`
     - `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
     - `JWT_SECRET`, `SERVER_URL`

**사용**: 
```bash
git add .
git commit -m "Update"
git push origin main
```
→ 자동 배포 완료!

### 수동 배포 (선택사항)

```bash
# 환경 변수 설정 후 실행
export DB_NAME=taba
export DB_USERNAME=taba_user
export DB_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -hex 32)
export SERVER_URL=https://www.taba.asia/api/v1

./deploy.sh coby@cobyserver.iptime.org 8080
```

---

## 🔧 환경 변수

### 필수 환경 변수

| 변수 | 설명 | 예시 |
|------|------|------|
| `DB_PASSWORD` | MySQL 비밀번호 | `my_secure_password` |
| `JWT_SECRET` | JWT 서명 키 (256비트) | `openssl rand -hex 32` |

### 선택 환경 변수

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `DB_HOST` | `localhost` | 데이터베이스 호스트 |
| `DB_PORT` | `3306` | 데이터베이스 포트 |
| `DB_NAME` | `taba` | 데이터베이스 이름 |
| `DB_USERNAME` | `root` | 데이터베이스 사용자 |
| `REDIS_HOST` | `localhost` | Redis 호스트 |
| `REDIS_PORT` | `6379` | Redis 포트 |
| `SERVER_PORT` | `8080` | 서버 포트 |
| `SPRING_PROFILES_ACTIVE` | `dev` | Spring 프로파일 |

### 환경 변수 설정 방법

- **로컬 개발**: 환경 변수 직접 설정 (`export`)
- **서버 배포**: GitHub Secrets (자동 배포) 또는 환경 변수 직접 설정 (수동 배포)

---

## 🛠️ 주요 명령어

### 개발

```bash
# 애플리케이션 실행
./gradlew bootRun --args='--spring.profiles.active=dev'

# 빌드
./gradlew clean build

# 테스트
./gradlew test
```

### Docker

```bash
# 로컬 Docker 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f backend

# 중지
docker-compose down
```

### 배포

```bash
# 자동 배포 (GitHub Actions)
git push origin main

# 수동 배포
./deploy.sh coby@cobyserver.iptime.org 8080
```

### 서버 관리

```bash
# 서버 접속
ssh coby@cobyserver.iptime.org

# 컨테이너 상태 확인
cd ~/taba_backend
docker-compose ps

# 로그 확인
docker-compose logs -f backend

# 재시작
docker-compose restart backend

# 중지
docker-compose down

# 완전 삭제 (데이터 포함)
docker-compose down -v
```

---

## 📍 주요 URL

### 로컬 개발
- API: http://localhost:8080/api/v1
- Swagger UI: http://localhost:8080/api/v1/swagger-ui/index.html
- Health Check: http://localhost:8080/api/v1/actuator/health

### 서버 배포
- API: https://www.taba.asia/api/v1
- Swagger UI: https://www.taba.asia/api/v1/swagger-ui/index.html
- Health Check: https://www.taba.asia/api/v1/actuator/health

---

## ❓ 문제 해결

### 포트 충돌
```bash
# 포트 사용 중인 프로세스 확인
lsof -i :8080

# 다른 포트 사용
./gradlew bootRun --args='--server.port=8081'
```

### 데이터베이스 연결 실패
1. MySQL 서비스 실행 확인
2. 환경 변수의 데이터베이스 설정 확인 (`DB_PASSWORD`, `DB_USERNAME` 등)
3. 데이터베이스 생성 확인

### 배포 실패
1. GitHub Actions 로그 확인
2. SSH 키 설정 확인
3. GitHub Secrets 확인

---

## 📚 더 알아보기

- **API 명세서**: [docs/API_SPECIFICATION.md](docs/API_SPECIFICATION.md)
- **설정 체크리스트**: [SETUP_CHECKLIST.md](SETUP_CHECKLIST.md) ⭐
- **환경 변수**: [ENVIRONMENT_VARIABLES.md](ENVIRONMENT_VARIABLES.md)
- **GitHub Secrets 설정**: [docs/GITHUB_SECRETS_SETUP.md](docs/GITHUB_SECRETS_SETUP.md)

