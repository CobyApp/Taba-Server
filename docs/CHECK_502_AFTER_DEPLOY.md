# 배포 후 502 에러 확인 명령어

배포는 성공했지만 외부 헬스체크가 502를 반환하는 경우 확인할 명령어입니다.

## 서버에서 실행할 명령어

### 1. 컨테이너 상태 확인

```bash
cd ~/taba_backend

# 모든 컨테이너 상태 확인
docker ps -a | grep taba

# 개발 환경 백엔드 상태 확인
docker ps -a | grep taba-backend-dev
```

### 2. 백엔드 로그 확인

```bash
# 개발 백엔드 로그 확인 (최근 100줄)
docker logs --tail=100 taba-backend-dev

# 실시간 로그 확인
docker logs -f taba-backend-dev
```

### 3. 포트 확인

```bash
# 포트 8081이 리스닝 중인지 확인
sudo ss -tlnp | grep 8081

# 또는
sudo netstat -tlnp | grep 8081

# 백엔드 컨테이너의 포트 확인
docker port taba-backend-dev
```

### 4. 로컬 헬스체크

```bash
# 컨테이너 내부에서 헬스체크
docker exec taba-backend-dev curl -f http://localhost:8080/api/v1/actuator/health

# 호스트에서 포트 8081로 헬스체크
curl http://localhost:8081/api/v1/actuator/health
```

### 5. Nginx 설정 확인

```bash
# Nginx 설정 파일 확인
sudo cat /etc/nginx/sites-available/taba | grep -A 10 "dev.taba.asia"

# Nginx 에러 로그 확인
sudo tail -f /var/log/nginx/taba-dev-error.log

# Nginx 접근 로그 확인
sudo tail -f /var/log/nginx/taba-dev-access.log
```

### 6. Nginx 재시작

```bash
# Nginx 설정 확인
sudo nginx -t

# Nginx 재시작
sudo systemctl restart nginx

# Nginx 상태 확인
sudo systemctl status nginx
```

### 7. 백엔드 재시작

```bash
cd ~/taba_backend

# 환경 변수 설정 (GitHub Secrets 값 사용)
export DB_NAME=taba  # DB_NAME_DEV 값
export DB_USERNAME=taba_user  # DB_USERNAME_DEV 값
export DB_PASSWORD=your_dev_password  # DB_PASSWORD_DEV 값
export JWT_SECRET=your_dev_jwt_secret  # JWT_SECRET_DEV 값
export SERVER_URL=https://dev.taba.asia/api/v1  # SERVER_URL_DEV 값
export SERVER_EXTERNAL_PORT=8081
export SPRING_PROFILES_ACTIVE=dev

# 백엔드 재시작
docker-compose -f docker-compose.dev.yml restart backend

# 또는 완전히 재생성
docker-compose -f docker-compose.dev.yml stop backend
docker-compose -f docker-compose.dev.yml rm -f backend
docker-compose -f docker-compose.dev.yml up -d backend
```

### 8. 한 번에 확인하는 스크립트

```bash
cd ~/taba_backend && \
echo "=== 컨테이너 상태 ===" && \
docker ps -a | grep taba-backend-dev && \
echo "" && \
echo "=== 포트 확인 ===" && \
sudo ss -tlnp | grep 8081 && \
echo "" && \
echo "=== 백엔드 로그 (최근 50줄) ===" && \
docker logs --tail=50 taba-backend-dev && \
echo "" && \
echo "=== 로컬 헬스체크 ===" && \
curl -s http://localhost:8081/api/v1/actuator/health && \
echo "" && \
echo "=== Nginx 에러 로그 (최근 20줄) ===" && \
sudo tail -20 /var/log/nginx/taba-dev-error.log
```

## 일반적인 원인

1. **백엔드가 아직 시작 중**: 애플리케이션 시작에 시간이 걸릴 수 있음
2. **포트 불일치**: Nginx가 8081을 가리키지만 백엔드가 다른 포트에서 실행 중
3. **네트워크 문제**: 백엔드와 Nginx가 같은 네트워크에 있지 않음
4. **환경 변수 문제**: 백엔드가 시작되지 않음

## 빠른 해결

```bash
# 백엔드 재시작
cd ~/taba_backend
docker-compose -f docker-compose.dev.yml restart backend

# 30초 대기 후 헬스체크
sleep 30
curl http://localhost:8081/api/v1/actuator/health
```

