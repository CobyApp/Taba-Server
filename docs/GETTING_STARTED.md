# 시작하기

Taba Backend를 시작하는 가장 빠른 방법입니다.

## 🚀 빠른 시작 (3단계)

### 1. 환경 설정

```bash
# 환경 변수 설정 (로컬 개발용)
export DB_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -hex 32)
```

**필수 설정**:
- `DB_PASSWORD`: MySQL 비밀번호
- `JWT_SECRET`: `openssl rand -hex 32` 명령으로 생성

### 2. 데이터베이스 생성

```bash
mysql -u root -p
CREATE DATABASE taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

### 3. 실행

```bash
# 개발 환경으로 실행 (자동 테이블 생성)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

**완료!** http://localhost:8080/api/v1/swagger-ui/index.html 에서 API 확인

---

## 📦 Docker로 실행 (선택사항)

```bash
# Docker Compose로 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f backend
```

---

## 🌐 서버 배포

### 자동 배포 (GitHub Actions) - 권장

main 브랜치에 푸시하면 자동 배포됩니다.

**초기 설정** (최초 1회):
1. [GitHub Actions 설정 가이드](GITHUB_ACTIONS_SETUP.md) 참고
2. [GitHub Secrets 설정 가이드](GITHUB_SECRETS.md) 참고 ⭐

**사용**: 그냥 `git push origin main` 하면 자동 배포!

### 수동 배포

```bash
./deploy.sh coby@cobyserver.iptime.org 8080
```

---

## 📚 더 알아보기

- **로컬 개발**: [빠른 시작 가이드](QUICK_START.md)
- **API 사용**: [API 명세서](API_SPECIFICATION.md)
- **프로젝트 구조**: [프로젝트 가이드](PROJECT_GUIDE.md)
- **자동 배포**: [GitHub Actions 설정](GITHUB_ACTIONS_SETUP.md)

