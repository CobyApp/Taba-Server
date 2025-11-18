# 환경 변수 가이드

## 필수 환경 변수

| 변수 | 설명 | 예시 |
|------|------|------|
| `DB_PASSWORD` | 데이터베이스 비밀번호 | `MySecurePassword123!` |
| `JWT_SECRET` | JWT 서명 키 (256비트) | `openssl rand -hex 32` 출력 |
| `SERVER_URL` | 서버 전체 URL | `https://taba.asia/api/v1` |

## 선택사항 환경 변수

| 변수 | 설명 | 기본값 |
|------|------|--------|
| `DB_HOST` | 데이터베이스 호스트 | `localhost` |
| `DB_PORT` | 데이터베이스 포트 | `3306` |
| `DB_NAME` | 데이터베이스 이름 | `taba` |
| `DB_USERNAME` | 데이터베이스 사용자 | `root` |
| `JWT_EXPIRATION` | JWT 만료 시간 (밀리초) | `604800000` (7일) |
| `REDIS_HOST` | Redis 호스트 | `localhost` |
| `REDIS_PORT` | Redis 포트 | `6379` |
| `REDIS_PASSWORD` | Redis 비밀번호 | 빈 문자열 |
| `FILE_UPLOAD_DIR` | 파일 업로드 디렉토리 | `uploads` |
| `SPRING_PROFILES_ACTIVE` | Spring 프로파일 | `dev` |

## 로컬 개발 환경 설정

```bash
export DB_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -hex 32)
```

## 서버 배포 환경 설정

GitHub Secrets에 설정하면 자동 배포 시 사용됩니다. 상세한 설정 방법은 [GitHub Secrets 설정](GITHUB_SECRETS.md)을 참고하세요.
