# GitHub Secrets 설정 가이드

이 문서는 GitHub Actions에서 사용할 환경 변수(Secrets) 설정 방법을 설명합니다.

## 📋 필수 Secrets

GitHub 저장소의 **Settings > Secrets and variables > Actions**에서 다음 Secrets를 추가하세요.

### 1. SSH 관련

#### SSH_PRIVATE_KEY
- **설명**: 서버 SSH 접속용 개인 키
- **생성 방법**:
  ```bash
  ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/github_actions_deploy
  cat ~/.ssh/github_actions_deploy
  ```
- **값**: 출력된 전체 내용 (-----BEGIN OPENSSH PRIVATE KEY----- 부터 -----END OPENSSH PRIVATE KEY----- 까지)

#### SSH_USER
- **설명**: SSH 사용자 이름
- **값**: `coby`

### 2. 데이터베이스 설정

#### DB_NAME
- **설명**: 데이터베이스 이름
- **값**: `taba`

#### DB_USERNAME
- **설명**: 데이터베이스 사용자 이름
- **값**: `taba_user`

#### DB_PASSWORD
- **설명**: 데이터베이스 비밀번호
- **값**: 강력한 비밀번호 (예: `MySecurePassword123!`)

### 3. JWT 설정

#### JWT_SECRET
- **설명**: JWT 토큰 서명용 비밀키 (256비트)
- **생성 방법**:
  ```bash
  openssl rand -hex 32
  ```
- **값**: 생성된 64자리 16진수 문자열

#### JWT_EXPIRATION (선택사항)
- **설명**: JWT 토큰 만료 시간 (밀리초)
- **기본값**: `604800000` (7일)
- **값**: `604800000` (또는 원하는 값)

### 4. 서버 설정

#### SERVER_URL
- **설명**: 서버 전체 URL
- **값**: `http://cobyserver.iptime.org:8080/api/v1`

## 🔧 선택사항 Secrets

### Redis 설정 (Redis 사용 시)

#### REDIS_PASSWORD
- **설명**: Redis 비밀번호
- **값**: Redis 비밀번호 (없으면 빈 문자열 `""`)

### 메일 설정 (이메일 기능 사용 시)

#### MAIL_HOST
- **설명**: SMTP 서버 호스트
- **값**: `smtp.gmail.com` (또는 사용하는 SMTP 서버)

#### MAIL_PORT
- **설명**: SMTP 서버 포트
- **값**: `587` (또는 사용하는 포트)

#### MAIL_USERNAME
- **설명**: SMTP 사용자 이름
- **값**: 이메일 주소

#### MAIL_PASSWORD
- **설명**: SMTP 비밀번호 (또는 앱 비밀번호)
- **값**: 이메일 비밀번호 또는 앱 비밀번호

## 📝 Secrets 추가 방법

1. GitHub 저장소로 이동
2. **Settings** 클릭
3. 왼쪽 메뉴에서 **Secrets and variables** > **Actions** 클릭
4. **New repository secret** 버튼 클릭
5. **Name**에 Secrets 이름 입력
6. **Secret**에 값 입력
7. **Add secret** 클릭

## ✅ Secrets 확인

모든 Secrets가 추가되었는지 확인:

- ✅ SSH_PRIVATE_KEY
- ✅ SSH_USER
- ✅ DB_NAME
- ✅ DB_USERNAME
- ✅ DB_PASSWORD
- ✅ JWT_SECRET
- ✅ SERVER_URL
- ⚪ JWT_EXPIRATION (선택사항)
- ⚪ REDIS_PASSWORD (선택사항)
- ⚪ MAIL_HOST (선택사항)
- ⚪ MAIL_PORT (선택사항)
- ⚪ MAIL_USERNAME (선택사항)
- ⚪ MAIL_PASSWORD (선택사항)

## 🔐 보안 권장사항

1. **강력한 비밀번호 사용**
   - DB_PASSWORD: 최소 16자, 대소문자, 숫자, 특수문자 포함
   - JWT_SECRET: 반드시 `openssl rand -hex 32`로 생성

2. **정기 업데이트**
   - 비밀번호는 3-6개월마다 변경 권장
   - JWT_SECRET 변경 시 모든 사용자 재로그인 필요

3. **접근 제한**
   - 저장소 접근 권한을 필요한 사람만 부여
   - Secrets는 절대 코드에 커밋하지 않기

## 🔄 Secrets 업데이트

Secrets를 업데이트하려면:

1. **Secrets and variables** > **Actions** 이동
2. 업데이트할 Secret 클릭
3. **Update** 버튼 클릭
4. 새 값 입력 후 **Update secret** 클릭

## ❓ 문제 해결

### Secrets가 적용되지 않음

- GitHub Actions 워크플로우가 Secrets를 올바르게 참조하는지 확인
- Secrets 이름이 정확한지 확인 (대소문자 구분)
- 워크플로우를 다시 실행해보기

### JWT 토큰 오류

- JWT_SECRET이 올바르게 설정되었는지 확인
- JWT_SECRET은 64자리 16진수 문자열이어야 함

### 데이터베이스 연결 실패

- DB_PASSWORD가 올바른지 확인
- DB_USERNAME과 DB_NAME이 올바른지 확인

---

**최종 업데이트**: 2024-12-XX

