# 빠른 참조 가이드

자주 사용하는 명령어와 설정을 빠르게 찾을 수 있는 가이드입니다.

## 🚀 로컬 개발

```bash
# 환경 변수 설정 (로컬 개발용)
export DB_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -hex 32)

# 데이터베이스 생성
mysql -u root -p -e "CREATE DATABASE taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## 🌐 서버 배포

### 자동 배포 (GitHub Actions)

```bash
git push origin main  # 자동 배포!
```

### 수동 배포

```bash
./deploy.sh coby@cobyserver.iptime.org 8080
```

## 🔧 환경 변수

### 필수
- `DB_PASSWORD`: MySQL 비밀번호
- `JWT_SECRET`: `openssl rand -hex 32`

### 설정 방법
- 로컬: 환경 변수 직접 설정 (`export`)
- 서버: GitHub Secrets (자동 배포) 또는 환경 변수 직접 설정 (수동 배포)

## 📍 주요 URL

### 로컬
- API: http://localhost:8080/api/v1
- Swagger: http://localhost:8080/api/v1/swagger-ui/index.html

### 서버
- API: https://www.taba.asia/api/v1
- Swagger: https://www.taba.asia/api/v1/swagger-ui/index.html

## 🛠️ 주요 명령어

```bash
# 빌드
./gradlew clean build

# Docker 실행
docker-compose up -d

# 서버 로그 확인
ssh coby@cobyserver.iptime.org "cd ~/taba_backend && docker-compose logs -f backend"
```

## 📚 상세 문서

- **사용 가이드**: [USAGE.md](USAGE.md)
- **API 명세**: [docs/API_SPECIFICATION.md](docs/API_SPECIFICATION.md)
- **자동 배포 설정**: [docs/GITHUB_ACTIONS_SETUP.md](docs/GITHUB_ACTIONS_SETUP.md)

