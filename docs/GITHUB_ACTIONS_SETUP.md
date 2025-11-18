# GitHub Actions 자동 배포 설정

## 1. SSH 키 생성 및 설정

### SSH 키 생성
```bash
ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/github_actions_deploy
```

### 공개 키를 서버에 추가
```bash
ssh-copy-id -i ~/.ssh/github_actions_deploy.pub user@server
```

### SSH 접속 테스트
```bash
ssh -i ~/.ssh/github_actions_deploy user@server
```

## 2. GitHub Secrets 설정

GitHub 저장소 > Settings > Secrets and variables > Actions에서 다음 Secrets 추가:

| Secret 이름 | 설명 | 예시 |
|------------|------|------|
| `SSH_PRIVATE_KEY` | SSH 개인 키 전체 내용 | `cat ~/.ssh/github_actions_deploy` 출력 |
| `SSH_USER` | SSH 사용자 이름 | `coby` |
| `DB_NAME` | 데이터베이스 이름 | `taba` |
| `DB_USERNAME` | DB 사용자 이름 | `taba_user` |
| `DB_PASSWORD` | DB 비밀번호 | |
| `JWT_SECRET` | JWT 비밀키 | `openssl rand -hex 32` |
| `SERVER_URL` | 서버 URL | `https://taba.asia/api/v1` |
| `FCM_SERVICE_ACCOUNT_KEY_JSON` | Firebase 서비스 계정 키 JSON | |

**상세 가이드**: [GitHub Secrets 설정](GITHUB_SECRETS.md)

## 3. 배포 확인

main 브랜치에 푸시하면 자동 배포됩니다:
```bash
git push origin main
```

GitHub Actions 탭에서 배포 진행 상황을 확인할 수 있습니다.

---

## 문제 해결

### SSH 연결 실패
- SSH 키 권한 확인: `chmod 600 ~/.ssh/github_actions_deploy`
- 서버에서 authorized_keys 권한 확인: `chmod 600 ~/.ssh/authorized_keys`

### 배포 실패
- GitHub Actions 로그 확인
- 서버 로그 확인: `docker-compose logs backend`
