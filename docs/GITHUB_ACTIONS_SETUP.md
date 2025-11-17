# GitHub Actions 자동 배포 설정 가이드

이 가이드는 GitHub Actions를 사용하여 main 브랜치에 푸시할 때마다 자동으로 서버에 배포되도록 설정하는 방법을 설명합니다.

## 📋 사전 요구사항

1. GitHub 저장소에 접근 권한
2. 서버 SSH 접근 권한
3. 서버에 Docker 및 Docker Compose 설치

## 🔑 1. SSH 키 생성 및 설정

### 로컬에서 SSH 키 생성 (이미 있으면 생략)

```bash
# SSH 키 생성 (없는 경우)
ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/github_actions_deploy

# 또는 RSA 사용
ssh-keygen -t rsa -b 4096 -C "github-actions" -f ~/.ssh/github_actions_deploy
```

### 공개 키를 서버에 추가

```bash
# 공개 키를 서버에 복사
ssh-copy-id -i ~/.ssh/github_actions_deploy.pub coby@cobyserver.iptime.org

# 또는 수동으로 추가
cat ~/.ssh/github_actions_deploy.pub | ssh coby@cobyserver.iptime.org "mkdir -p ~/.ssh && cat >> ~/.ssh/authorized_keys"
```

### 서버에서 권한 설정

```bash
ssh coby@cobyserver.iptime.org
chmod 700 ~/.ssh
chmod 600 ~/.ssh/authorized_keys
```

### SSH 접속 테스트

```bash
ssh -i ~/.ssh/github_actions_deploy coby@cobyserver.iptime.org
```

## 🔐 2. GitHub Secrets 설정

GitHub 저장소에서 다음 Secrets를 설정해야 합니다:

### Secrets 설정 방법

1. GitHub 저장소로 이동
2. **Settings** > **Secrets and variables** > **Actions** 클릭
3. **New repository secret** 클릭
4. 아래 Secrets 추가:

### 필요한 Secrets

#### SSH_PRIVATE_KEY
- **이름**: `SSH_PRIVATE_KEY`
- **값**: 개인 키 전체 내용
  ```bash
  cat ~/.ssh/github_actions_deploy
  ```
- **설명**: 서버 SSH 접속용 개인 키

#### SSH_USER
- **이름**: `SSH_USER`
- **값**: `coby`
- **설명**: SSH 사용자 이름

#### DB_NAME
- **이름**: `DB_NAME`
- **값**: `taba`
- **설명**: 데이터베이스 이름

#### DB_USERNAME
- **이름**: `DB_USERNAME`
- **값**: `taba_user`
- **설명**: 데이터베이스 사용자 이름

#### DB_PASSWORD
- **이름**: `DB_PASSWORD`
- **값**: 데이터베이스 비밀번호
- **설명**: MySQL 데이터베이스 비밀번호

#### JWT_SECRET
- **이름**: `JWT_SECRET`
- **값**: 256비트 랜덤 문자열
  ```bash
  openssl rand -hex 32
  ```
- **설명**: JWT 토큰 서명용 비밀키

#### JWT_EXPIRATION (선택사항)
- **이름**: `JWT_EXPIRATION`
- **값**: `604800000` (7일, 밀리초)
- **설명**: JWT 토큰 만료 시간

#### SERVER_URL
- **이름**: `SERVER_URL`
- **값**: `http://cobyserver.iptime.org:8080/api/v1`
- **설명**: 서버 전체 URL

#### REDIS_PASSWORD (선택사항)
- **이름**: `REDIS_PASSWORD`
- **값**: Redis 비밀번호 (없으면 빈 문자열)
- **설명**: Redis 비밀번호 (사용하지 않으면 빈 값)

## 📝 3. Secrets 추가 예시

### SSH_PRIVATE_KEY 추가

```bash
# 로컬에서 개인 키 내용 복사
cat ~/.ssh/github_actions_deploy

# 출력된 전체 내용을 GitHub Secrets에 붙여넣기
# (-----BEGIN OPENSSH PRIVATE KEY----- 부터 -----END OPENSSH PRIVATE KEY----- 까지)
```

### JWT_SECRET 생성

```bash
# JWT Secret 생성
openssl rand -hex 32

# 출력된 값을 GitHub Secrets의 JWT_SECRET에 추가
```

### 전체 Secrets 목록

필수 Secrets (7개):
1. `SSH_PRIVATE_KEY` - SSH 개인 키
2. `SSH_USER` - `coby`
3. `DB_NAME` - `taba`
4. `DB_USERNAME` - `taba_user`
5. `DB_PASSWORD` - 데이터베이스 비밀번호
6. `JWT_SECRET` - `openssl rand -hex 32` 출력
7. `SERVER_URL` - `http://cobyserver.iptime.org:8080/api/v1`

선택사항 Secrets:
- `JWT_EXPIRATION` - `604800000` (기본값 사용 가능)
- `REDIS_PASSWORD` - (없으면 빈 문자열)

## 🚀 4. Workflow 파일 확인

`.github/workflows/deploy.yml` 파일이 올바르게 설정되어 있는지 확인:

```yaml
name: Deploy to Server

on:
  push:
    branches:
      - main
```

## ✅ 5. 테스트

### 첫 배포 테스트

1. main 브랜치에 변경사항 푸시:
   ```bash
   git add .
   git commit -m "Test deployment"
   git push origin main
   ```

2. GitHub Actions 탭에서 실행 확인:
   - 저장소 > **Actions** 탭
   - "Deploy to Server" 워크플로우 확인
   - 실행 상태 및 로그 확인

### 수동 실행 테스트

GitHub Actions에서 수동으로 실행할 수도 있습니다:

1. **Actions** 탭 이동
2. **Deploy to Server** 워크플로우 선택
3. **Run workflow** 버튼 클릭
4. 브랜치 선택 후 **Run workflow** 실행

## 🔍 6. 배포 확인

### Health Check

```bash
curl http://cobyserver.iptime.org:8080/api/v1/actuator/health
```

### Swagger UI

브라우저에서 접속:
```
http://cobyserver.iptime.org:8080/api/v1/swagger-ui/index.html
```

### 서버 로그 확인

```bash
ssh coby@cobyserver.iptime.org
cd ~/taba_backend
docker-compose logs -f backend
```

## ⚠️ 문제 해결

### SSH 연결 실패

**증상**: `Permission denied (publickey)`

**해결**:
1. SSH 키가 올바르게 추가되었는지 확인
2. 서버의 `~/.ssh/authorized_keys` 파일 확인
3. 권한 확인: `chmod 600 ~/.ssh/authorized_keys`

### Secrets 누락

**증상**: `Secret not found`

**해결**:
1. GitHub 저장소 Settings > Secrets 확인
2. 모든 필수 Secrets가 추가되었는지 확인
3. Secrets 이름이 정확한지 확인 (대소문자 구분)

### Docker 빌드 실패

**증상**: `Build failed`

**해결**:
1. 서버에 Docker 및 Docker Compose 설치 확인
2. 서버 디스크 공간 확인
3. GitHub Actions 로그에서 상세 에러 확인

### Health Check 실패

**증상**: `Health check failed`

**해결**:
1. 컨테이너가 정상 실행 중인지 확인
2. 포트포워드 설정 확인
3. 서버 로그 확인: `docker-compose logs backend`

## 🔄 7. 배포 프로세스

배포는 다음 순서로 진행됩니다:

1. **코드 체크아웃**: GitHub에서 최신 코드 다운로드
2. **SSH 설정**: SSH 키 설정 및 서버 연결
3. **파일 전송**: Docker 관련 파일 전송
4. **환경 변수 주입**: GitHub Secrets에서 환경 변수 주입
5. **Docker 빌드**: 새 이미지 빌드
6. **컨테이너 시작**: Docker Compose로 컨테이너 시작
7. **Health Check**: 배포 성공 여부 확인

## 📊 8. 배포 알림 (선택사항)

### Slack 알림 추가

`.github/workflows/deploy.yml`에 Slack 알림 추가:

```yaml
- name: Notify Slack
  if: always()
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
    text: 'Deployment to cobyserver.iptime.org'
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

### 이메일 알림

GitHub Actions에서 실패 시 자동으로 이메일 알림을 받을 수 있습니다 (GitHub 설정에서 활성화).

## 🔐 9. 보안 권장사항

1. **SSH 키 보안**
   - SSH 키는 절대 공개 저장소에 커밋하지 마세요
   - GitHub Secrets에만 저장하세요
   - 정기적으로 키 로테이션

2. **Secrets 관리**
   - 강력한 비밀번호 사용
   - 정기적으로 Secrets 업데이트
   - 불필요한 Secrets 삭제

3. **접근 제한**
   - 필요한 사람만 GitHub 저장소 접근 권한 부여
   - 서버 SSH 접근 제한

## 📚 참고 자료

- [GitHub Actions 공식 문서](https://docs.github.com/en/actions)
- [SSH 키 생성 가이드](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent)
- [GitHub Secrets 관리](https://docs.github.com/en/actions/security-guides/encrypted-secrets)

---

**최종 업데이트**: 2024-12-XX

