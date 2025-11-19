# 서버 진단 명령어 가이드

서버 터미널에서 배포 문제를 진단하기 위한 명령어 모음입니다.

## 📍 기본 위치 이동

```bash
cd ~/taba_backend
```

---

## 1. 컨테이너 상태 확인

### 전체 컨테이너 상태
```bash
# 모든 taba 관련 컨테이너 확인
docker ps -a --filter "name=taba" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}\t{{.Image}}"

# 또는 간단하게
docker ps -a | grep taba
```

### 프로덕션 컨테이너만 확인
```bash
docker ps -a | grep prod
```

### 개발 컨테이너만 확인
```bash
docker ps -a | grep dev
```

### 컨테이너 상세 정보
```bash
# 프로덕션 백엔드
docker inspect taba-backend-prod | grep -A 10 "State"

# 개발 백엔드
docker inspect taba-backend-dev | grep -A 10 "State"
```

---

## 2. 환경 변수 확인

### 프로덕션 환경 변수 확인
```bash
# 프로덕션 백엔드 환경 변수
docker exec taba-backend-prod printenv | grep -E "(DB_|JWT_|SERVER_|REDIS_|SPRING_)" | sort

# 프로덕션 MySQL 환경 변수
docker exec taba-mysql-prod printenv | grep MYSQL

# 프로덕션 Redis 환경 변수
docker exec taba-redis-prod printenv | grep REDIS
```

### 개발 환경 변수 확인
```bash
# 개발 백엔드 환경 변수
docker exec taba-backend-dev printenv | grep -E "(DB_|JWT_|SERVER_|REDIS_|SPRING_)" | sort

# 개발 MySQL 환경 변수
docker exec taba-mysql-dev printenv | grep MYSQL

# 개발 Redis 환경 변수
docker exec taba-redis-dev printenv | grep REDIS
```

### 현재 쉘의 환경 변수 확인
```bash
# 프로덕션 환경 변수 설정 후 확인
export DB_NAME="${{ secrets.DB_NAME_PROD }}"
export DB_USERNAME="${{ secrets.DB_USERNAME_PROD }}"
export DB_PASSWORD="${{ secrets.DB_PASSWORD_PROD }}"
export JWT_SECRET="${{ secrets.JWT_SECRET_PROD }}"
export SERVER_URL="${{ secrets.SERVER_URL_PROD }}"
export EXTERNAL_PORT="${EXTERNAL_PORT:-8080}"
export DB_EXTERNAL_PORT="${DB_EXTERNAL_PORT:-3306}"
export REDIS_EXTERNAL_PORT="${REDIS_EXTERNAL_PORT:-6379}"
export SPRING_PROFILES_ACTIVE=prod

# 설정된 환경 변수 확인 (비밀번호는 일부만 표시)
echo "=== 프로덕션 환경 변수 ==="
echo "DB_NAME: $DB_NAME"
echo "DB_USERNAME: $DB_USERNAME"
echo "DB_PASSWORD: ${DB_PASSWORD:0:3}***"
echo "JWT_SECRET: ${JWT_SECRET:0:10}***"
echo "SERVER_URL: $SERVER_URL"
echo "EXTERNAL_PORT: $EXTERNAL_PORT"
echo "DB_EXTERNAL_PORT: $DB_EXTERNAL_PORT"
echo "REDIS_EXTERNAL_PORT: $REDIS_EXTERNAL_PORT"
echo "SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"
```

---

## 3. 로그 확인

### 프로덕션 로그
```bash
# 프로덕션 백엔드 로그 (최근 100줄)
docker logs --tail=100 taba-backend-prod

# 프로덕션 백엔드 로그 (실시간)
docker logs -f taba-backend-prod

# 프로덕션 MySQL 로그
docker logs --tail=50 taba-mysql-prod

# 프로덕션 Redis 로그
docker logs --tail=50 taba-redis-prod
```

### 개발 로그
```bash
# 개발 백엔드 로그 (최근 100줄)
docker logs --tail=100 taba-backend-dev

# 개발 백엔드 로그 (실시간)
docker logs -f taba-backend-dev

# 개발 MySQL 로그
docker logs --tail=50 taba-mysql-dev

# 개발 Redis 로그
docker logs --tail=50 taba-redis-dev
```

### 에러 로그만 필터링
```bash
# 프로덕션 백엔드 에러만
docker logs taba-backend-prod 2>&1 | grep -i error

# 개발 백엔드 에러만
docker logs taba-backend-dev 2>&1 | grep -i error
```

---

## 4. 포트 및 네트워크 확인

### 포트 사용 확인
```bash
# 프로덕션 포트 확인 (8080, 3306, 6379)
sudo ss -tlnp | grep -E ":(8080|3306|6379) "

# 개발 포트 확인 (8081, 3307, 6380)
sudo ss -tlnp | grep -E ":(8081|3307|6380) "

# 모든 포트 확인
sudo ss -tlnp | grep LISTEN
```

### Docker 네트워크 확인
```bash
# 네트워크 목록
docker network ls

# taba-network 상세 정보
docker network inspect taba-network

# 네트워크에 연결된 컨테이너 확인
docker network inspect taba-network | grep -A 5 "Containers"
```

---

## 5. MySQL 연결 테스트

### 프로덕션 MySQL 연결 테스트
```bash
# 컨테이너 내부에서 root로 연결 테스트
docker exec taba-mysql-prod mysql -u root -p"$DB_PASSWORD" -e "SELECT 1;" 2>&1

# 사용자로 연결 테스트
docker exec taba-mysql-prod mysql -u "$DB_USERNAME" -p"$DB_PASSWORD" -e "USE \`$DB_NAME\`; SELECT 1;" 2>&1

# 데이터베이스 목록 확인
docker exec taba-mysql-prod mysql -u root -p"$DB_PASSWORD" -e "SHOW DATABASES;" 2>&1

# 사용자 목록 확인
docker exec taba-mysql-prod mysql -u root -p"$DB_PASSWORD" -e "SELECT User, Host FROM mysql.user;" 2>&1

# 권한 확인
docker exec taba-mysql-prod mysql -u root -p"$DB_PASSWORD" -e "SHOW GRANTS FOR '$DB_USERNAME'@'%';" 2>&1
```

### 개발 MySQL 연결 테스트
```bash
# 개발 환경 변수 설정 후
export DB_NAME="${{ secrets.DB_NAME_DEV }}"
export DB_USERNAME="${{ secrets.DB_USERNAME_DEV }}"
export DB_PASSWORD="${{ secrets.DB_PASSWORD_DEV }}"

# 컨테이너 내부에서 root로 연결 테스트
docker exec taba-mysql-dev mysql -u root -p"$DB_PASSWORD" -e "SELECT 1;" 2>&1

# 사용자로 연결 테스트
docker exec taba-mysql-dev mysql -u "$DB_USERNAME" -p"$DB_PASSWORD" -e "USE \`$DB_NAME\`; SELECT 1;" 2>&1
```

---

## 6. Redis 연결 테스트

### 프로덕션 Redis 연결 테스트
```bash
# 비밀번호 없이 연결 테스트
docker exec taba-redis-prod redis-cli ping

# 비밀번호가 있는 경우
docker exec taba-redis-prod redis-cli -a "$REDIS_PASSWORD" ping

# Redis 정보 확인
docker exec taba-redis-prod redis-cli INFO server | head -10
```

### 개발 Redis 연결 테스트
```bash
# 비밀번호 없이 연결 테스트
docker exec taba-redis-dev redis-cli ping

# 비밀번호가 있는 경우
export REDIS_PASSWORD="${{ secrets.REDIS_PASSWORD_DEV }}"
docker exec taba-redis-dev redis-cli -a "$REDIS_PASSWORD" ping
```

---

## 7. 헬스체크 확인

### 프로덕션 헬스체크
```bash
# 컨테이너 내부 헬스체크
docker exec taba-backend-prod wget --no-verbose --tries=1 --spider http://localhost:8080/api/v1/actuator/health 2>&1

# 로컬 호스트 헬스체크
curl -v http://localhost:8080/api/v1/actuator/health

# 외부에서 헬스체크 (서버 IP 사용)
curl -v http://$(hostname -I | awk '{print $1}'):8080/api/v1/actuator/health
```

### 개발 헬스체크
```bash
# 컨테이너 내부 헬스체크
docker exec taba-backend-dev wget --no-verbose --tries=1 --spider http://localhost:8080/api/v1/actuator/health 2>&1

# 로컬 호스트 헬스체크
curl -v http://localhost:8081/api/v1/actuator/health
```

---

## 8. Docker Compose 파일 확인

### 프로덕션 docker-compose 파일 확인
```bash
# 파일 존재 확인
ls -la docker-compose.prod.yml

# 파일 내용 확인
cat docker-compose.prod.yml | head -50

# 환경 변수 사용 확인
grep -E "\$\{" docker-compose.prod.yml
```

### 개발 docker-compose 파일 확인
```bash
# 파일 존재 확인
ls -la docker-compose.dev.yml

# 파일 내용 확인
cat docker-compose.dev.yml | head -50
```

---

## 9. 볼륨 확인

### Docker 볼륨 확인
```bash
# 모든 볼륨 확인
docker volume ls | grep taba

# 프로덕션 볼륨 상세 정보
docker volume inspect mysql_data_prod
docker volume inspect redis_data_prod
docker volume inspect uploads_data_prod

# 개발 볼륨 상세 정보
docker volume inspect mysql_data_dev
docker volume inspect redis_data_dev
docker volume inspect uploads_data_dev
```

---

## 10. 컨테이너 재시작 및 재배포

### 프로덕션 컨테이너 재시작
```bash
cd ~/taba_backend

# 환경 변수 설정 (GitHub Secrets 값으로 대체)
export DB_NAME="${{ secrets.DB_NAME_PROD }}"
export DB_USERNAME="${{ secrets.DB_USERNAME_PROD }}"
export DB_PASSWORD="${{ secrets.DB_PASSWORD_PROD }}"
export REDIS_PASSWORD="${{ secrets.REDIS_PASSWORD_PROD }}"
export JWT_SECRET="${{ secrets.JWT_SECRET_PROD }}"
export SERVER_URL="${{ secrets.SERVER_URL_PROD }}"
export EXTERNAL_PORT="${EXTERNAL_PORT:-8080}"
export DB_EXTERNAL_PORT="${DB_EXTERNAL_PORT:-3306}"
export REDIS_EXTERNAL_PORT="${REDIS_EXTERNAL_PORT:-6379}"
export SPRING_PROFILES_ACTIVE=prod

# 기존 컨테이너 정리
docker stop taba-backend-prod taba-mysql-prod taba-redis-prod 2>/dev/null || true
docker rm -f taba-backend-prod taba-mysql-prod taba-redis-prod 2>/dev/null || true
docker-compose -f docker-compose.prod.yml down --remove-orphans 2>/dev/null || true

# 네트워크 생성
docker network create taba-network 2>/dev/null || true

# MySQL, Redis 시작
docker-compose -f docker-compose.prod.yml up -d mysql redis

# MySQL 헬스체크 대기
echo "Waiting for MySQL..."
for i in {1..12}; do
  HEALTH=$(docker inspect --format='{{.State.Health.Status}}' taba-mysql-prod 2>/dev/null || echo "unknown")
  echo "MySQL health check $i/12: $HEALTH"
  if [ "$HEALTH" = "healthy" ]; then
    echo "✅ MySQL is healthy"
    break
  fi
  sleep 5
done

# MySQL 사용자 생성
docker exec taba-mysql-prod mysql -u root -p"$DB_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS \`$DB_NAME\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>&1 | grep -v "Warning" || true
docker exec taba-mysql-prod mysql -u root -p"$DB_PASSWORD" -e "DROP USER IF EXISTS '$DB_USERNAME'@'%';" 2>&1 | grep -v "Warning" || true
docker exec taba-mysql-prod mysql -u root -p"$DB_PASSWORD" -e "CREATE USER '$DB_USERNAME'@'%' IDENTIFIED BY '$DB_PASSWORD';" 2>&1 | grep -v "Warning" || true
docker exec taba-mysql-prod mysql -u root -p"$DB_PASSWORD" -e "GRANT ALL PRIVILEGES ON \`$DB_NAME\`.* TO '$DB_USERNAME'@'%'; FLUSH PRIVILEGES;" 2>&1 | grep -v "Warning" || true

# 백엔드 시작
docker-compose -f docker-compose.prod.yml up -d backend

# 로그 확인
sleep 10
docker logs --tail=100 taba-backend-prod
```

### 개발 컨테이너 재시작
```bash
cd ~/taba_backend

# 환경 변수 설정 (GitHub Secrets 값으로 대체)
export DB_NAME="${{ secrets.DB_NAME_DEV }}"
export DB_USERNAME="${{ secrets.DB_USERNAME_DEV }}"
export DB_PASSWORD="${{ secrets.DB_PASSWORD_DEV }}"
export REDIS_PASSWORD="${{ secrets.REDIS_PASSWORD_DEV }}"
export JWT_SECRET="${{ secrets.JWT_SECRET_DEV }}"
export SERVER_URL="${{ secrets.SERVER_URL_DEV }}"
export EXTERNAL_PORT="${EXTERNAL_PORT:-8081}"
export DB_EXTERNAL_PORT="${DB_EXTERNAL_PORT:-3307}"
export REDIS_EXTERNAL_PORT="${REDIS_EXTERNAL_PORT:-6380}"
export SPRING_PROFILES_ACTIVE=dev

# 기존 컨테이너 정리
docker stop taba-backend-dev taba-mysql-dev taba-redis-dev 2>/dev/null || true
docker rm -f taba-backend-dev taba-mysql-dev taba-redis-dev 2>/dev/null || true
docker-compose -f docker-compose.dev.yml down --remove-orphans 2>/dev/null || true

# 네트워크 생성
docker network create taba-network 2>/dev/null || true

# 모든 서비스 시작
docker-compose -f docker-compose.dev.yml up -d

# 로그 확인
sleep 10
docker logs --tail=100 taba-backend-dev
```

---

## 11. 종합 진단 스크립트

### 프로덕션 종합 진단
```bash
#!/bin/bash
cd ~/taba_backend

echo "=== 프로덕션 환경 진단 ==="
echo ""

echo "1. 컨테이너 상태:"
docker ps -a --filter "name=prod" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo ""

echo "2. 포트 사용 확인:"
sudo ss -tlnp | grep -E ":(8080|3306|6379) " || echo "포트가 사용되지 않음"
echo ""

echo "3. 네트워크 확인:"
docker network inspect taba-network 2>/dev/null | grep -A 5 "Containers" || echo "네트워크가 없음"
echo ""

echo "4. 프로덕션 백엔드 로그 (최근 30줄):"
docker logs --tail=30 taba-backend-prod 2>&1 || echo "백엔드 컨테이너가 없음"
echo ""

echo "5. 프로덕션 MySQL 로그 (최근 20줄):"
docker logs --tail=20 taba-mysql-prod 2>&1 || echo "MySQL 컨테이너가 없음"
echo ""

echo "6. 프로덕션 Redis 로그 (최근 20줄):"
docker logs --tail=20 taba-redis-prod 2>&1 || echo "Redis 컨테이너가 없음"
echo ""

echo "7. 헬스체크:"
if docker ps | grep -q taba-backend-prod; then
  curl -s http://localhost:8080/api/v1/actuator/health || echo "헬스체크 실패"
else
  echo "백엔드 컨테이너가 실행 중이 아님"
fi
echo ""

echo "=== 진단 완료 ==="
```

### 개발 종합 진단
```bash
#!/bin/bash
cd ~/taba_backend

echo "=== 개발 환경 진단 ==="
echo ""

echo "1. 컨테이너 상태:"
docker ps -a --filter "name=dev" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo ""

echo "2. 포트 사용 확인:"
sudo ss -tlnp | grep -E ":(8081|3307|6380) " || echo "포트가 사용되지 않음"
echo ""

echo "3. 네트워크 확인:"
docker network inspect taba-network 2>/dev/null | grep -A 5 "Containers" || echo "네트워크가 없음"
echo ""

echo "4. 개발 백엔드 로그 (최근 30줄):"
docker logs --tail=30 taba-backend-dev 2>&1 || echo "백엔드 컨테이너가 없음"
echo ""

echo "5. 개발 MySQL 로그 (최근 20줄):"
docker logs --tail=20 taba-mysql-dev 2>&1 || echo "MySQL 컨테이너가 없음"
echo ""

echo "6. 개발 Redis 로그 (최근 20줄):"
docker logs --tail=20 taba-redis-dev 2>&1 || echo "Redis 컨테이너가 없음"
echo ""

echo "7. 헬스체크:"
if docker ps | grep -q taba-backend-dev; then
  curl -s http://localhost:8081/api/v1/actuator/health || echo "헬스체크 실패"
else
  echo "백엔드 컨테이너가 실행 중이 아님"
fi
echo ""

echo "=== 진단 완료 ==="
```

---

## 12. Nginx 연결 확인

### Nginx 상태 확인
```bash
# Nginx 상태
sudo systemctl status nginx

# Nginx 설정 파일 확인
sudo nginx -t

# Nginx 로그 확인
sudo tail -50 /var/log/nginx/error.log
sudo tail -50 /var/log/nginx/access.log
```

### Nginx에서 백엔드 연결 테스트
```bash
# Nginx 설정에서 백엔드 포트 확인
sudo grep -r "proxy_pass" /etc/nginx/sites-enabled/ | grep -E "(8080|8081)"

# Nginx 재시작
sudo systemctl restart nginx
sudo systemctl status nginx
```

---

## 13. 디스크 및 리소스 확인

### 디스크 사용량
```bash
# 전체 디스크 사용량
df -h

# Docker 볼륨 사용량
docker system df -v
```

### 메모리 및 CPU 사용량
```bash
# 컨테이너 리소스 사용량
docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}"

# 시스템 리소스
free -h
top -bn1 | head -20
```

---

## 14. 빠른 문제 해결 체크리스트

```bash
cd ~/taba_backend

# 1. 컨테이너가 실행 중인가?
docker ps | grep taba

# 2. 포트가 열려있는가?
sudo ss -tlnp | grep -E ":(8080|8081|3306|3307|6379|6380) "

# 3. 네트워크가 존재하는가?
docker network ls | grep taba-network

# 4. 백엔드가 헬스체크를 통과하는가?
curl -f http://localhost:8080/api/v1/actuator/health 2>&1  # 프로덕션
curl -f http://localhost:8081/api/v1/actuator/health 2>&1  # 개발

# 5. MySQL이 연결 가능한가?
docker exec taba-mysql-prod mysql -u root -p"$DB_PASSWORD" -e "SELECT 1;" 2>&1  # 프로덕션
docker exec taba-mysql-dev mysql -u root -p"$DB_PASSWORD" -e "SELECT 1;" 2>&1  # 개발

# 6. Redis가 연결 가능한가?
docker exec taba-redis-prod redis-cli ping 2>&1  # 프로덕션
docker exec taba-redis-dev redis-cli ping 2>&1  # 개발

# 7. 환경 변수가 설정되어 있는가?
docker exec taba-backend-prod printenv | grep -E "(DB_NAME|JWT_SECRET|SERVER_URL)"  # 프로덕션
docker exec taba-backend-dev printenv | grep -E "(DB_NAME|JWT_SECRET|SERVER_URL)"  # 개발
```

---

## 💡 문제별 해결 방법

### 백엔드가 시작되지 않는 경우
1. 로그 확인: `docker logs --tail=100 taba-backend-prod`
2. 환경 변수 확인: `docker exec taba-backend-prod printenv`
3. MySQL/Redis 연결 확인: 위의 MySQL/Redis 연결 테스트 실행

### MySQL 연결 오류가 발생하는 경우
1. MySQL 로그 확인: `docker logs taba-mysql-prod`
2. 사용자 및 권한 확인: 위의 MySQL 연결 테스트 실행
3. 데이터베이스 존재 확인: `docker exec taba-mysql-prod mysql -u root -p"$DB_PASSWORD" -e "SHOW DATABASES;"`

### 포트 충돌이 발생하는 경우
1. 포트 사용 확인: `sudo ss -tlnp | grep -E ":(8080|3306|6379) "`
2. 다른 프로세스 종료 또는 포트 변경

### 502 Bad Gateway 오류가 발생하는 경우
1. 백엔드 컨테이너 실행 확인: `docker ps | grep taba-backend-prod`
2. 로컬 헬스체크: `curl http://localhost:8080/api/v1/actuator/health`
3. Nginx 설정 확인: `sudo nginx -t`
4. Nginx 로그 확인: `sudo tail -50 /var/log/nginx/error.log`

