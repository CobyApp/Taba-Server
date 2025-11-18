# 설정 체크리스트

Taba Backend를 처음 설정할 때 확인해야 할 항목들을 정리한 문서입니다.

## ✅ GitHub Secrets 설정 (서버 배포 시 필수)

GitHub 저장소 **Settings > Secrets and variables > Actions**에서 다음 Secrets를 추가하세요:

### 필수 Secrets (7개)

- [ ] `SSH_PRIVATE_KEY` - SSH 개인 키
  ```bash
  ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/github_actions_deploy
  cat ~/.ssh/github_actions_deploy
  # 출력 전체 복사
  ```

- [ ] `SSH_USER` - `coby`

- [ ] `DB_NAME` - `taba`

- [ ] `DB_USERNAME` - `taba_user`

- [ ] `DB_PASSWORD` - 강력한 데이터베이스 비밀번호

- [ ] `JWT_SECRET` - JWT 서명 키
  ```bash
  openssl rand -hex 32
  # 출력 복사
  ```

- [ ] `SERVER_URL` - `https://www.taba.asia/api/v1`

### 선택사항 Secrets

- [ ] `JWT_EXPIRATION` - `604800000` (기본값 사용 가능)
- [ ] `REDIS_PASSWORD` - (없으면 빈 문자열)

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

- [환경 변수 요약](ENVIRONMENT_VARIABLES.md) - 필수 환경 변수 목록
- [사용 가이드](USAGE.md) - 상세 사용법
- [빠른 참조 가이드](QUICK_REFERENCE.md) - 자주 사용하는 명령어

