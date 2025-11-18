# 빠른 시작 가이드

## ⚡ 5분 안에 시작하기

### 1. 사전 요구사항
- Java 17 이상
- MySQL 8.0 이상
- Gradle (Wrapper 포함)

### 2. 데이터베이스 생성
```bash
mysql -u root -p
CREATE DATABASE taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

### 3. 환경 변수 설정
```bash
export DB_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -hex 32)
```

### 4. 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

**완료!** http://localhost:8080/api/v1/swagger-ui/index.html

---

## Docker로 실행 (선택사항)

```bash
docker-compose up -d
```

---

## 문제 해결

### 포트가 이미 사용 중
```bash
lsof -i :8080
kill -9 <PID>
```

### 데이터베이스 연결 실패
```bash
# MySQL 서비스 확인
brew services list | grep mysql  # macOS
sudo systemctl status mysql      # Linux

# MySQL 접속 테스트
mysql -u root -p
```

### Redis 연결 실패 (선택사항)
Redis는 토큰 블랙리스트에만 사용되므로 선택사항입니다.

---

## 다음 단계

- [API 명세서](API_SPECIFICATION.md) - 전체 API 엔드포인트
- [GitHub Secrets 설정](GITHUB_SECRETS.md) - 배포를 위한 환경 변수 설정
- [도메인 & HTTPS 설정](DOMAIN_HTTPS_SETUP.md) - 도메인 연결 및 SSL 인증서
