# GitHub Secrets 설정 가이드

GitHub Actions를 통한 자동 배포를 위해 필요한 GitHub Secrets 설정 방법입니다.

## 📋 설정 위치

1. GitHub 저장소로 이동
2. **Settings** → **Secrets and variables** → **Actions** 클릭
3. **New repository secret** 버튼 클릭하여 각 시크릿 추가

---

## 🔴 필수 시크릿 (프로덕션 + 개발 환경)

### 1. SSH 배포 설정

#### `SSH_PRIVATE_KEY`
- **설명**: 서버에 SSH 접속하기 위한 개인 키
- **생성 방법**:
  ```bash
  # 서버에서 SSH 키 생성 (없는 경우)
  ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/github_actions_deploy
  
  # 공개 키를 authorized_keys에 추가
  cat ~/.ssh/github_actions_deploy.pub >> ~/.ssh/authorized_keys
  
  # 개인 키 내용 복사 (로컬에서)
  cat ~/.ssh/github_actions_deploy
  ```
- **값 예시**: 
  ```
  -----BEGIN OPENSSH PRIVATE KEY-----
  b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAABlwAAAAdzc2gtcn
  ...
  -----END OPENSSH PRIVATE KEY-----
  ```

#### `SSH_HOST`
- **설명**: 서버 IP 주소 또는 도메인
- **값 예시**: `123.456.789.0` 또는 `taba.asia`

#### `SSH_USER`
- **설명**: SSH 접속 사용자명
- **값 예시**: `coby`

---

### 2. Firebase 설정

#### `FCM_SERVICE_ACCOUNT_KEY_JSON`
- **설명**: Firebase Cloud Messaging 서비스 계정 키 JSON 파일 전체 내용
- **생성 방법**:
  1. [Firebase Console](https://console.firebase.google.com/) 접속
  2. 프로젝트 선택 → **프로젝트 설정** → **서비스 계정** 탭
  3. **새 비공개 키 생성** 클릭
  4. 다운로드된 JSON 파일 전체 내용 복사
- **값 예시**:
  ```json
  {
    "type": "service_account",
    "project_id": "your-project-id",
    "private_key_id": "...",
    "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
    ...
  }
  ```
- **주의**: JSON 전체 내용을 그대로 붙여넣기 (줄바꿈 포함)

---

## 🟢 프로덕션 환경 시크릿

### 3. 데이터베이스 설정 (프로덕션)

#### `DB_NAME_PROD`
- **설명**: 프로덕션 MySQL 데이터베이스 이름
- **값 예시**: `taba_prod` 또는 `taba`

#### `DB_USERNAME_PROD`
- **설명**: 프로덕션 MySQL 사용자명
- **값 예시**: `taba_user_prod` 또는 `taba_user`

#### `DB_PASSWORD_PROD`
- **설명**: 프로덕션 MySQL 비밀번호
- **생성 방법**:
  ```bash
  # 강력한 비밀번호 생성
  openssl rand -base64 32
  ```
- **값 예시**: `your_secure_password_here`
- **주의**: ⚠️ **반드시 강력한 비밀번호 사용**

#### `DB_EXTERNAL_PORT_PROD` (선택사항)
- **설명**: 프로덕션 MySQL 외부 포트 (외부에서 DB 접속 시 사용)
- **기본값**: `3306` (설정하지 않으면 자동 사용)
- **값 예시**: `3306`
- **외부 접속 예시**: 
  ```bash
  # 외부에서 프로덕션 DB 접속
  mysql -h 서버IP -P 3306 -u taba_user_prod -p
  ```
- **주의**: 개발 환경(3307)과 포트가 다르므로 동시 실행 가능

#### `REDIS_PASSWORD_PROD` (선택사항)
- **설명**: 프로덕션 Redis 비밀번호
- **기본값**: 빈 문자열 (비밀번호 없음)
- **값 예시**: `your_redis_password` 또는 비워두기

#### `REDIS_EXTERNAL_PORT_PROD` (선택사항)
- **설명**: 프로덕션 Redis 외부 포트 (외부에서 Redis 접속 시 사용)
- **기본값**: `6379` (설정하지 않으면 자동 사용)
- **값 예시**: `6379`
- **외부 접속 예시**: 
  ```bash
  # 외부에서 프로덕션 Redis 접속
  redis-cli -h 서버IP -p 6379
  ```
- **주의**: 개발 환경(6380)과 포트가 다르므로 동시 실행 가능

---

### 4. 인증 설정 (프로덕션)

#### `JWT_SECRET_PROD`
- **설명**: 프로덕션 JWT 토큰 서명 키 (256비트)
- **생성 위치**: ✅ **로컬 컴퓨터에서 생성 권장** (보안상 가장 안전)
- **생성 방법**:
  ```bash
  # 로컬 터미널에서 실행 (Mac/Linux/Windows Git Bash)
  openssl rand -hex 32
  
  # 출력 예시: a1b2c3d4e5f6789012345678901234567890abcdef1234567890abcdef123456
  ```
- **다른 생성 방법** (권장하지 않음):
  - 온라인 랜덤 생성기: 보안상 위험하므로 사용하지 않는 것을 권장
  - 서버에서 생성: 가능하지만 로컬에서 생성 후 복사하는 것이 더 안전
- **값 예시**: `a1b2c3d4e5f6...` (64자리 16진수 문자열)
- **주의**: 
  - ⚠️ **절대 공개하지 말 것** (GitHub, 공개 저장소, 채팅 등)
  - ⚠️ **프로덕션과 개발 환경은 다른 키 사용 권장**
  - ⚠️ **키 변경 시 기존 토큰이 모두 무효화됨**

---

### 5. 서버 설정 (프로덕션)

#### `SERVER_URL_PROD`
- **설명**: 프로덕션 서버 전체 API URL
- **값 예시**: `https://taba.asia/api/v1` 또는 `https://api.taba.asia/api/v1`
- **형식**: `https://도메인/api/v1`

#### `EXTERNAL_PORT_PROD` (선택사항)
- **설명**: 프로덕션 백엔드 외부 포트 (외부에서 API 접속 시 사용)
- **기본값**: `8080` (설정하지 않으면 자동 사용)
- **값 예시**: `8080`
- **외부 접속 예시**: 
  ```bash
  # 외부에서 프로덕션 API 접속
  curl http://서버IP:8080/api/v1/actuator/health
  ```
- **주의**: 개발 환경(8081)과 포트가 다르므로 동시 실행 가능

---

## 🟡 개발 환경 시크릿

### 6. 데이터베이스 설정 (개발)

#### `DB_NAME_DEV`
- **설명**: 개발 MySQL 데이터베이스 이름
- **값 예시**: `taba_dev`

#### `DB_USERNAME_DEV`
- **설명**: 개발 MySQL 사용자명
- **값 예시**: `taba_user_dev`

#### `DB_PASSWORD_DEV`
- **설명**: 개발 MySQL 비밀번호
- **값 예시**: `dev_password_here`
- **주의**: 개발 환경이므로 프로덕션보다 낮은 보안 수준 허용 가능

#### `DB_EXTERNAL_PORT_DEV` (선택사항)
- **설명**: 개발 MySQL 외부 포트 (외부에서 DB 접속 시 사용)
- **기본값**: `3307` (설정하지 않으면 자동 사용)
- **값 예시**: `3307`
- **외부 접속 예시**: 
  ```bash
  # 외부에서 개발 DB 접속
  mysql -h 서버IP -P 3307 -u taba_user_dev -p
  ```
- **주의**: 프로덕션 환경(3306)과 포트가 다르므로 동시 실행 가능

#### `REDIS_PASSWORD_DEV` (선택사항)
- **설명**: 개발 Redis 비밀번호
- **기본값**: 빈 문자열 (비밀번호 없음)
- **값 예시**: 비워두기

#### `REDIS_EXTERNAL_PORT_DEV` (선택사항)
- **설명**: 개발 Redis 외부 포트 (외부에서 Redis 접속 시 사용)
- **기본값**: `6380` (설정하지 않으면 자동 사용)
- **값 예시**: `6380`
- **외부 접속 예시**: 
  ```bash
  # 외부에서 개발 Redis 접속
  redis-cli -h 서버IP -p 6380
  ```
- **주의**: 프로덕션 환경(6379)과 포트가 다르므로 동시 실행 가능

---

### 7. 인증 설정 (개발)

#### `JWT_SECRET_DEV`
- **설명**: 개발 JWT 토큰 서명 키 (256비트)
- **생성 위치**: ✅ **로컬 컴퓨터에서 생성 권장** (보안상 가장 안전)
- **생성 방법**:
  ```bash
  # 로컬 터미널에서 실행 (Mac/Linux/Windows Git Bash)
  openssl rand -hex 32
  
  # 출력 예시: dev1234567890abcdef1234567890abcdef1234567890abcdef1234567890ab
  ```
- **다른 생성 방법** (권장하지 않음):
  - 온라인 랜덤 생성기: 보안상 위험하므로 사용하지 않는 것을 권장
  - 서버에서 생성: 가능하지만 로컬에서 생성 후 복사하는 것이 더 안전
- **값 예시**: `dev_secret_key_here...` (64자리 16진수 문자열)
- **주의**: 
  - ⚠️ **절대 공개하지 말 것** (GitHub, 공개 저장소, 채팅 등)
  - ⚠️ **프로덕션과 개발 환경은 다른 키 사용 권장**
  - ⚠️ **키 변경 시 기존 토큰이 모두 무효화됨**

---

### 8. 서버 설정 (개발)

#### `SERVER_URL_DEV`
- **설명**: 개발 서버 전체 API URL
- **값 예시**: `https://dev.taba.asia/api/v1`
- **형식**: `https://도메인/api/v1`

#### `EXTERNAL_PORT_DEV` (선택사항)
- **설명**: 개발 백엔드 외부 포트 (외부에서 API 접속 시 사용)
- **기본값**: `8081` (설정하지 않으면 자동 사용)
- **값 예시**: `8081`
- **외부 접속 예시**: 
  ```bash
  # 외부에서 개발 API 접속
  curl http://서버IP:8081/api/v1/actuator/health
  ```
- **주의**: 프로덕션 환경(8080)과 포트가 다르므로 동시 실행 가능

---

## 📊 시크릿 설정 체크리스트

### 필수 시크릿 (공통)
- [ ] `SSH_PRIVATE_KEY`
- [ ] `SSH_HOST`
- [ ] `SSH_USER`
- [ ] `FCM_SERVICE_ACCOUNT_KEY_JSON`

### 프로덕션 환경 필수
- [ ] `DB_NAME_PROD`
- [ ] `DB_USERNAME_PROD`
- [ ] `DB_PASSWORD_PROD`
- [ ] `JWT_SECRET_PROD`
- [ ] `SERVER_URL_PROD`

### 개발 환경 필수
- [ ] `DB_NAME_DEV`
- [ ] `DB_USERNAME_DEV`
- [ ] `DB_PASSWORD_DEV`
- [ ] `JWT_SECRET_DEV`
- [ ] `SERVER_URL_DEV`

### 선택사항 (기본값 사용 가능)
- [ ] `DB_EXTERNAL_PORT_PROD` (기본값: 3306)
- [ ] `REDIS_PASSWORD_PROD` (기본값: 빈 문자열)
- [ ] `REDIS_EXTERNAL_PORT_PROD` (기본값: 6379)
- [ ] `EXTERNAL_PORT_PROD` (기본값: 8080)
- [ ] `DB_EXTERNAL_PORT_DEV` (기본값: 3307)
- [ ] `REDIS_PASSWORD_DEV` (기본값: 빈 문자열)
- [ ] `REDIS_EXTERNAL_PORT_DEV` (기본값: 6380)
- [ ] `EXTERNAL_PORT_DEV` (기본값: 8081)

---

## 🔍 시크릿 확인 방법

### GitHub Actions 로그에서 확인
1. GitHub 저장소 → **Actions** 탭
2. 최근 워크플로우 실행 클릭
3. **Deploy Application** 단계에서 환경 변수 확인
4. **주의**: 실제 비밀번호는 마스킹되어 표시됨 (`***`)

### 서버에서 확인
```bash
# 컨테이너 환경 변수 확인
docker exec taba-backend-prod printenv | grep -E "(DB_|JWT_|SERVER_)"
docker exec taba-backend-dev printenv | grep -E "(DB_|JWT_|SERVER_)"

# MySQL 컨테이너 확인
docker exec taba-mysql-prod printenv | grep MYSQL
docker exec taba-mysql-dev printenv | grep MYSQL
```

---

## ⚠️ 주의사항

1. **비밀번호 보안**
   - 프로덕션 비밀번호는 반드시 강력하게 설정
   - 개발/프로덕션 비밀번호를 다르게 설정
   - 정기적으로 비밀번호 변경 권장

2. **JWT_SECRET**
   - 절대 공개 저장소에 커밋하지 말 것
   - 프로덕션/개발 환경별로 다른 키 사용 권장
   - 키 변경 시 기존 토큰이 모두 무효화됨

3. **SSH 키**
   - SSH 개인 키는 절대 공유하지 말 것
   - 키가 유출된 경우 즉시 재생성 필요

4. **Firebase 키**
   - JSON 파일 전체 내용을 그대로 붙여넣기
   - 줄바꿈 문자 포함하여 정확히 복사

5. **포트 충돌 및 외부 접속**
   - ⚠️ **중요**: 개발/프로덕션 환경이 **동시에 실행**되므로 포트가 절대 겹치면 안 됩니다
   - **기본값** (포트 충돌 방지):
     - **개발 환경**: MySQL(3307), Redis(6380), Backend(8081)
     - **프로덕션 환경**: MySQL(3306), Redis(6379), Backend(8080)
   - **외부 접속 시 포트 사용**:
     - 프로덕션 DB: `서버IP:3306`
     - 개발 DB: `서버IP:3307` ⚠️ **3306과 다름**
     - 프로덕션 Redis: `서버IP:6379`
     - 개발 Redis: `서버IP:6380` ⚠️ **6379와 다름**
     - 프로덕션 API: `서버IP:8080`
     - 개발 API: `서버IP:8081` ⚠️ **8080과 다름**
   - **포트 변경 시 주의사항**:
     - 개발/프로덕션 포트를 변경할 경우, 반드시 서로 다른 포트를 사용해야 함
     - 예: 개발을 3308로 변경하면 프로덕션은 3306 유지, 또는 프로덕션을 3308로 변경

---

## 🔗 관련 문서

- [환경 변수 가이드](../ENVIRONMENT_VARIABLES.md)
- [배포 워크플로우](../.github/workflows/deploy.yml)
- [Docker Compose 설정](../docker-compose.dev.yml)
- [프로덕션 Docker Compose 설정](../docker-compose.prod.yml)

