# Docker 배포 가이드

## 빠른 시작

### 환경 변수 설정
```bash
export DB_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -hex 32)
export SERVER_URL=http://localhost:8080/api/v1
```

### Docker Compose로 실행
```bash
docker-compose up -d
```

### 확인
- Health Check: http://localhost:8080/api/v1/actuator/health
- Swagger UI: http://localhost:8080/api/v1/swagger-ui/index.html

---

## 기본 명령어

```bash
# 시작
docker-compose up -d

# 중지
docker-compose stop

# 중지 및 제거
docker-compose down

# 로그 확인
docker-compose logs -f backend

# 재시작
docker-compose restart backend

# 이미지 재빌드
docker-compose build --no-cache

# 상태 확인
docker-compose ps
```

---

## 프로덕션 배포

### 무중단 배포 스크립트 사용
```bash
./scripts/zero-downtime-deploy.sh ~/taba_backend
```

또는 직접 빌드:
```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml build --no-cache
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

---

## 문제 해결

### 포트 충돌
```bash
docker ps | grep 8080
docker stop <container_id>
```

### 로그 확인
```bash
docker-compose logs --tail=100 backend
```

### 컨테이너 재시작
```bash
docker-compose restart backend
```
