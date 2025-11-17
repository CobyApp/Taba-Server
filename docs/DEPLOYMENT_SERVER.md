# 서버 배포 가이드

이 가이드는 `cobyserver.iptime.org` 서버에 Taba Backend를 배포하는 방법을 설명합니다.

## 📋 사전 요구사항

1. **서버 접속 정보**
   - 서버 주소: `cobyserver.iptime.org`
   - SSH 접속: `ssh coby@cobyserver.iptime.org`
   - 내부 IP: `192.168.0.3` (포트포워드 설정용)

2. **서버에 설치 필요**
   - Docker
   - Docker Compose

## 🚀 빠른 배포

### 자동 배포 스크립트 사용

```bash
# 기본 포트(8080)로 배포
./deploy.sh coby@cobyserver.iptime.org 8080

# 다른 포트로 배포 (예: 3001)
./deploy.sh coby@cobyserver.iptime.org 3001
```

## 📝 수동 배포 단계

### 1. 서버 접속 및 준비

```bash
ssh coby@cobyserver.iptime.org
```

### 2. Docker 및 Docker Compose 설치 확인

```bash
docker --version
docker-compose --version
```

설치되어 있지 않다면:

```bash
# Docker 설치 (Ubuntu/Debian)
sudo apt update
sudo apt install -y docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER
```

### 3. 프로젝트 디렉토리 생성

```bash
mkdir -p ~/taba_backend
cd ~/taba_backend
```

### 4. 파일 업로드

로컬에서 다음 파일들을 서버로 전송:

```bash
# 로컬에서 실행
scp docker-compose.yml docker-compose.prod.yml Dockerfile coby@cobyserver.iptime.org:~/taba_backend/
scp -r src/main/resources/db coby@cobyserver.iptime.org:~/taba_backend/
```

### 5. 환경 변수 설정

서버에서 `.env` 파일 생성:

```bash
cd ~/taba_backend
nano .env
```

`.env` 파일 내용:

```bash
# 데이터베이스 설정
DB_HOST=mysql
DB_PORT=3306
DB_NAME=taba
DB_USERNAME=taba_user
DB_PASSWORD=강력한_비밀번호_설정

# Redis 설정 (선택사항)
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT 설정
JWT_SECRET=256비트_랜덤_문자열_생성_필요

# 서버 설정
EXTERNAL_PORT=8080
SERVER_URL=http://cobyserver.iptime.org:8080/api/v1
SPRING_PROFILES_ACTIVE=prod
```

**중요**: 
- `DB_PASSWORD`: 강력한 비밀번호로 변경
- `JWT_SECRET`: 256비트 랜덤 문자열 생성 (예: `openssl rand -hex 32`)

### 6. Docker 이미지 빌드 및 실행

```bash
cd ~/taba_backend

# 이미지 빌드
docker-compose -f docker-compose.yml -f docker-compose.prod.yml build

# 컨테이너 시작
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# 상태 확인
docker-compose ps

# 로그 확인
docker-compose logs -f backend
```

## 🔧 포트포워드 설정

ipTIME 라우터 관리 페이지에서 포트포워드 설정이 필요합니다.

### 설정 방법

1. 라우터 관리 페이지 접속: `http://cobyserver.iptime.org:3000`
2. **고급 설정** > **NAT/라우터 관리** > **포트포워드 설정** 이동
3. 새 규칙 추가:
   - **규칙이름**: `TABA-BACKEND`
   - **내부 IP주소**: `192.168.0.3`
   - **프로토콜**: `TCP`
   - **외부 포트**: `8080` (또는 원하는 포트)
   - **내부 포트**: `8080`
4. **적용** 버튼 클릭

### 포트 선택 가이드

기존 포트포워드 설정을 확인하여 충돌하지 않는 포트를 선택하세요:

- **8080**: 기본 포트 (권장)
- **3001**: 다른 서비스와 충돌 시
- **8081**: 대안 포트

## 🔍 배포 확인

### 1. Health Check

```bash
curl http://cobyserver.iptime.org:8080/api/v1/actuator/health
```

예상 응답:
```json
{"status":"UP"}
```

### 2. Swagger UI 접속

브라우저에서 접속:
```
http://cobyserver.iptime.org:8080/api/v1/swagger-ui/index.html
```

### 3. 로그 확인

```bash
ssh coby@cobyserver.iptime.org
cd ~/taba_backend
docker-compose logs -f backend
```

## 🛠️ 유지보수

### 컨테이너 재시작

```bash
cd ~/taba_backend
docker-compose restart backend
```

### 컨테이너 중지

```bash
cd ~/taba_backend
docker-compose down
```

### 컨테이너 완전 삭제 (데이터 보존)

```bash
cd ~/taba_backend
docker-compose down
```

### 컨테이너 및 볼륨 삭제 (데이터 삭제)

```bash
cd ~/taba_backend
docker-compose down -v
```

### 로그 확인

```bash
# 모든 서비스 로그
docker-compose logs -f

# 특정 서비스 로그
docker-compose logs -f backend
docker-compose logs -f mysql
docker-compose logs -f redis

# 최근 100줄
docker-compose logs --tail=100 backend
```

### 데이터베이스 백업

```bash
cd ~/taba_backend
docker-compose exec mysql mysqldump -u taba_user -p taba > backup_$(date +%Y%m%d_%H%M%S).sql
```

### 데이터베이스 복원

```bash
cd ~/taba_backend
docker-compose exec -T mysql mysql -u taba_user -p taba < backup.sql
```

## 🔄 업데이트 배포

### 1. 코드 업데이트 후 재배포

```bash
# 로컬에서
./gradlew clean build -x test
./deploy.sh coby@cobyserver.iptime.org 8080
```

### 2. 수동 업데이트

```bash
ssh coby@cobyserver.iptime.org
cd ~/taba_backend

# 새 이미지 빌드
docker-compose -f docker-compose.yml -f docker-compose.prod.yml build --no-cache

# 컨테이너 재시작
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

## ⚠️ 문제 해결

### 포트 충돌

**증상**: `Bind for 0.0.0.0:8080 failed: port is already allocated`

**해결**:
1. 다른 포트 사용: `.env` 파일에서 `EXTERNAL_PORT` 변경
2. 기존 컨테이너 확인: `docker ps`
3. 기존 컨테이너 중지: `docker stop <container_id>`

### 데이터베이스 연결 실패

**증상**: `Communications link failure`

**해결**:
1. MySQL 컨테이너 상태 확인: `docker-compose ps mysql`
2. MySQL 로그 확인: `docker-compose logs mysql`
3. `.env` 파일의 데이터베이스 설정 확인

### 메모리 부족

**증상**: 컨테이너가 자주 재시작됨

**해결**:
1. 서버 메모리 확인: `free -h`
2. `docker-compose.prod.yml`의 리소스 제한 조정
3. 불필요한 컨테이너 중지

## 📊 모니터링

### 리소스 사용량 확인

```bash
docker stats
```

### 디스크 사용량 확인

```bash
docker system df
```

### 볼륨 확인

```bash
docker volume ls
```

## 🔐 보안 권장사항

1. **강력한 비밀번호 사용**
   - 데이터베이스 비밀번호
   - JWT Secret
   - Redis 비밀번호 (사용 시)

2. **방화벽 설정**
   - 필요한 포트만 열기
   - SSH는 키 기반 인증 사용

3. **정기 업데이트**
   - Docker 이미지 업데이트
   - 보안 패치 적용

4. **백업**
   - 정기적인 데이터베이스 백업
   - `.env` 파일 백업 (안전한 위치에)

## 📞 지원

문제가 발생하면 다음을 확인하세요:

1. 로그: `docker-compose logs -f`
2. 컨테이너 상태: `docker-compose ps`
3. 네트워크: `docker network ls`
4. 포트포워드 설정 확인

---

**최종 업데이트**: 2024-12-XX

