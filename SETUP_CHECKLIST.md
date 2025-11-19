# 설정 체크리스트

Taba Backend를 처음 설정할 때 확인해야 할 항목들을 정리한 문서입니다.

## ✅ GitHub Secrets 설정 (서버 배포 시 필수)

> 📖 **상세 가이드**: [GitHub Secrets 설정 가이드](docs/GITHUB_SECRETS_SETUP.md) 참조

GitHub 저장소 **Settings > Secrets and variables > Actions**에서 다음 Secrets를 추가하세요:

### 공통 필수 Secrets (4개)

- [ ] `SSH_PRIVATE_KEY` - SSH 개인 키
- [ ] `SSH_HOST` - 서버 IP 또는 도메인
- [ ] `SSH_USER` - SSH 사용자명 (`coby`)
- [ ] `FCM_SERVICE_ACCOUNT_KEY_JSON` - Firebase 서비스 계정 키 JSON 전체

### 프로덕션 환경 필수 Secrets (5개)

- [ ] `DB_NAME_PROD` - 프로덕션 데이터베이스 이름
- [ ] `DB_USERNAME_PROD` - 프로덕션 데이터베이스 사용자명
- [ ] `DB_PASSWORD_PROD` - 프로덕션 데이터베이스 비밀번호
- [ ] `JWT_SECRET_PROD` - 프로덕션 JWT 서명 키 (`openssl rand -hex 32`)
- [ ] `SERVER_URL_PROD` - 프로덕션 서버 URL (`https://taba.asia/api/v1`)

### 개발 환경 필수 Secrets (5개)

- [ ] `DB_NAME_DEV` - 개발 데이터베이스 이름
- [ ] `DB_USERNAME_DEV` - 개발 데이터베이스 사용자명
- [ ] `DB_PASSWORD_DEV` - 개발 데이터베이스 비밀번호
- [ ] `JWT_SECRET_DEV` - 개발 JWT 서명 키 (`openssl rand -hex 32`)
- [ ] `SERVER_URL_DEV` - 개발 서버 URL (`https://dev.taba.asia/api/v1`)

### 선택사항 Secrets (기본값 사용 가능)

- [ ] `DB_EXTERNAL_PORT_PROD` (기본값: 3306)
- [ ] `REDIS_PASSWORD_PROD` (기본값: 빈 문자열)
- [ ] `REDIS_EXTERNAL_PORT_PROD` (기본값: 6379)
- [ ] `EXTERNAL_PORT_PROD` (기본값: 8080)
- [ ] `DB_EXTERNAL_PORT_DEV` (기본값: 3307)
- [ ] `REDIS_PASSWORD_DEV` (기본값: 빈 문자열)
- [ ] `REDIS_EXTERNAL_PORT_DEV` (기본값: 6380)
- [ ] `EXTERNAL_PORT_DEV` (기본값: 8081)

## ✅ SSH 키 서버 등록

```bash
ssh-copy-id -i ~/.ssh/github_actions_deploy.pub coby@cobyserver.iptime.org
```

## ✅ 포트포워드 설정

라우터 관리 페이지에서:
- 외부 포트: `8080` → 내부 IP: `192.168.0.3:8080` (TCP)

## ✅ 테스트

1. **GitHub Actions 테스트**
   ```bash
   git push origin main
   ```
   - Actions 탭에서 배포 진행 확인

2. **Health Check**
   ```bash
   curl https://www.taba.asia/api/v1/actuator/health
   ```

3. **Swagger UI 확인**
   - https://www.taba.asia/api/v1/swagger-ui/index.html

## 📚 참고 문서

- [GitHub Secrets 설정 가이드](docs/GITHUB_SECRETS_SETUP.md) - 상세한 시크릿 설정 방법
- [환경 변수 요약](ENVIRONMENT_VARIABLES.md) - 필수 환경 변수 목록
- [사용 가이드](USAGE.md) - 상세 사용법
- [빠른 참조 가이드](QUICK_REFERENCE.md) - 자주 사용하는 명령어

