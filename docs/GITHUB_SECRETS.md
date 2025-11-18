# GitHub Secrets 설정 가이드

## 필수 Secrets

GitHub 저장소 > Settings > Secrets and variables > Actions에서 다음 Secrets를 추가하세요.

| Secret 이름 | 설명 | 생성/설정 방법 |
|------------|------|--------------|
| `SSH_PRIVATE_KEY` | SSH 개인 키 전체 내용 | `ssh-keygen -t ed25519 -f ~/.ssh/github_actions_deploy` 후 `cat ~/.ssh/github_actions_deploy` |
| `SSH_USER` | SSH 사용자 이름 | `coby` |
| `DB_NAME` | 데이터베이스 이름 | `taba` |
| `DB_USERNAME` | DB 사용자 이름 | `taba_user` |
| `DB_PASSWORD` | DB 비밀번호 | 강력한 비밀번호 |
| `JWT_SECRET` | JWT 비밀키 (256비트) | `openssl rand -hex 32` |
| `SERVER_URL` | 서버 전체 URL | `https://taba.asia/api/v1` |
| `FCM_SERVICE_ACCOUNT_KEY_JSON` | Firebase 서비스 계정 키 JSON | Firebase Console > 프로젝트 설정 > 서비스 계정 > 새 비공개 키 생성 |

## 선택사항 Secrets

| Secret 이름 | 설명 | 기본값 |
|------------|------|--------|
| `JWT_EXPIRATION` | JWT 만료 시간 (밀리초) | `604800000` (7일) |
| `REDIS_PASSWORD` | Redis 비밀번호 | 빈 문자열 |

## Secrets 추가 방법

1. GitHub 저장소 > Settings > Secrets and variables > Actions
2. New repository secret 클릭
3. Name과 Secret 값 입력
4. Add secret 클릭

## 보안 권장사항

- `JWT_SECRET`: 반드시 `openssl rand -hex 32`로 생성 (64자리 16진수)
- `DB_PASSWORD`: 최소 16자, 대소문자, 숫자, 특수문자 포함
- Secrets는 절대 코드에 커밋하지 마세요
