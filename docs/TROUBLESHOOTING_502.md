# 502 Bad Gateway 문제 해결 가이드

## 🔍 서버에서 확인할 명령어들

### 1. Docker 컨테이너 상태 확인
```bash
cd ~/taba_backend
docker-compose -f docker-compose.dev.yml -f docker-compose.prod.yml ps
# 또는
docker ps -a | grep taba
```

### 2. 백엔드 컨테이너가 실행 중인지 확인
```bash
docker ps | grep backend
# 실행 중이면 컨테이너 ID와 상태가 표시됨
```

### 3. 백엔드 로그 확인 (가장 중요!)
```bash
cd ~/taba_backend
docker-compose -f docker-compose.dev.yml -f docker-compose.prod.yml logs --tail=100 backend
# 또는 최근 200줄
docker-compose -f docker-compose.dev.yml -f docker-compose.prod.yml logs --tail=200 backend
# 실시간 로그 확인
docker-compose -f docker-compose.dev.yml -f docker-compose.prod.yml logs -f backend
```

### 4. 백엔드 컨테이너 내부에서 헬스체크
```bash
docker exec taba-backend-prod wget --no-verbose --tries=1 --spider http://localhost:8080/api/v1/actuator/health
# 또는
docker exec taba-backend-prod curl -f http://localhost:8080/api/v1/actuator/health
```

### 5. 포트 확인 (8080 포트가 열려있는지)
```bash
# 백엔드가 8080 포트에서 리스닝 중인지 확인
netstat -tlnp | grep 8080
# 또는
ss -tlnp | grep 8080
# 또는
lsof -i :8080
```

### 6. 서버 내부에서 백엔드 직접 접속 테스트
```bash
# 로컬호스에서 테스트
curl http://localhost:8080/api/v1/actuator/health
# 또는
curl http://127.0.0.1:8080/api/v1/actuator/health
```

### 7. Nginx 설정 확인 (Nginx를 사용하는 경우)
```bash
# Nginx 설정 파일 확인
sudo nginx -t
# Nginx 에러 로그 확인
sudo tail -f /var/log/nginx/error.log
# Nginx 설정 파일 위치 확인
sudo find /etc/nginx -name "*taba*" -o -name "*default*"
```

### 8. Docker 네트워크 확인
```bash
docker network ls
docker network inspect taba_backend_taba-network
```

### 9. MySQL과 Redis 연결 확인
```bash
# MySQL 연결 확인
docker exec taba-mysql-prod mysqladmin ping -h localhost -u root -p
# Redis 연결 확인
docker exec taba-redis-prod redis-cli ping
```

### 10. 전체 컨테이너 상태 및 리소스 확인
```bash
docker stats --no-stream
```

### 11. 백엔드 컨테이너 재시작 (문제 해결 시도)
```bash
cd ~/taba_backend
docker-compose -f docker-compose.dev.yml -f docker-compose.prod.yml restart backend
# 또는 완전히 재시작
docker-compose -f docker-compose.dev.yml -f docker-compose.prod.yml stop backend
docker-compose -f docker-compose.dev.yml -f docker-compose.prod.yml up -d backend
```

## 🚨 일반적인 원인 및 해결 방법

### 원인 1: 백엔드 컨테이너가 실행되지 않음
**확인:**
```bash
docker ps | grep backend
```

**해결:**
```bash
cd ~/taba_backend
docker-compose -f docker-compose.dev.yml -f docker-compose.prod.yml up -d backend
docker-compose -f docker-compose.dev.yml -f docker-compose.prod.yml logs backend
```

### 원인 2: 백엔드가 시작 중이거나 크래시됨
**확인:**
```bash
docker-compose -f docker-compose.dev.yml -f docker-compose.prod.yml logs --tail=100 backend
```

**해결:** 로그에서 에러 메시지 확인 후 수정

### 원인 3: MySQL/Redis 연결 실패
**확인:**
```bash
docker-compose -f docker-compose.dev.yml -f docker-compose.prod.yml logs backend | grep -i "error\|exception\|mysql\|redis"
```

**해결:**
```bash
# MySQL과 Redis가 실행 중인지 확인
docker ps | grep -E "mysql|redis"
# 실행되지 않았다면 시작
docker-compose -f docker-compose.dev.yml -f docker-compose.prod.yml up -d mysql redis
```

### 원인 4: 포트 충돌
**확인:**
```bash
lsof -i :8080
```

**해결:** 다른 프로세스가 8080 포트를 사용 중이면 종료하거나 포트 변경

### 원인 5: Nginx 프록시 설정 문제
**확인:**
```bash
sudo nginx -t
sudo tail -50 /var/log/nginx/error.log
```

**해결:** Nginx 설정에서 `proxy_pass`가 올바른 포트(8080)를 가리키는지 확인

## 📋 빠른 진단 스크립트

서버에서 다음 명령어를 실행하면 한 번에 모든 상태를 확인할 수 있습니다:

```bash
cd ~/taba_backend && \
echo "=== Docker 컨테이너 상태 ===" && \
docker-compose -f docker-compose.dev.yml -f docker-compose.prod.yml ps && \
echo "" && \
echo "=== 백엔드 로그 (최근 50줄) ===" && \
docker-compose -f docker-compose.dev.yml -f docker-compose.prod.yml logs --tail=50 backend && \
echo "" && \
echo "=== 포트 확인 ===" && \
netstat -tlnp | grep 8080 || echo "포트 8080이 열려있지 않음" && \
echo "" && \
echo "=== 로컬 헬스체크 ===" && \
curl -f http://localhost:8080/api/v1/actuator/health || echo "로컬 헬스체크 실패"
```

