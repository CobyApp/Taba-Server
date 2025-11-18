# 서버 설정 명령어 모음

서버에서 실행해야 할 명령어들을 정리한 문서입니다.

## 포트 설정 요약

- **프로덕션**: 외부 포트 `8080` → 내부 포트 `8080`
- **개발**: 외부 포트 `8081` → 내부 포트 `8080`

## 1. Nginx 설정

### 설정 파일 생성

```bash
sudo nano /etc/nginx/sites-available/taba
```

### 설정 파일 내용

[NGINX_SETUP.md](NGINX_SETUP.md) 파일의 내용을 참고하여 설정하세요.

### 설정 활성화

```bash
# 심볼릭 링크 생성
sudo ln -s /etc/nginx/sites-available/taba /etc/nginx/sites-enabled/

# 설정 파일 문법 확인
sudo nginx -t

# Nginx 재시작
sudo systemctl restart nginx

# 상태 확인
sudo systemctl status nginx
```

## 2. 포트 확인

### 컨테이너 포트 확인

```bash
# 모든 컨테이너 상태 확인
docker ps -a

# 프로덕션 백엔드 포트 확인
docker port taba-backend-prod

# 개발 백엔드 포트 확인
docker port taba-backend-dev

# 포트 사용 확인
sudo netstat -tlnp | grep -E "(8080|8081)"
# 또는
sudo ss -tlnp | grep -E "(8080|8081)"
```

### 헬스체크

```bash
# 프로덕션 헬스체크 (로컬)
curl http://localhost:8080/api/v1/actuator/health

# 개발 헬스체크 (로컬)
curl http://localhost:8081/api/v1/actuator/health

# 프로덕션 헬스체크 (Nginx를 통한 접근)
curl https://www.taba.asia/api/v1/actuator/health

# 개발 헬스체크 (Nginx를 통한 접근)
curl https://dev.taba.asia/api/v1/actuator/health
```

## 3. 컨테이너 상태 확인

### 모든 환경 컨테이너 확인

```bash
cd ~/taba_backend

# 개발 환경 컨테이너
docker ps -a --filter "name=taba-backend-dev" --filter "name=taba-mysql-dev" --filter "name=taba-redis-dev"

# 프로덕션 환경 컨테이너
docker ps -a --filter "name=taba-backend-prod" --filter "name=taba-mysql-prod" --filter "name=taba-redis-prod"

# 모든 Taba 컨테이너
docker ps -a | grep taba
```

### 컨테이너 로그 확인

```bash
# 프로덕션 백엔드 로그
docker logs --tail=100 taba-backend-prod

# 개발 백엔드 로그
docker logs --tail=100 taba-backend-dev

# 실시간 로그 확인
docker logs -f taba-backend-prod
docker logs -f taba-backend-dev
```

## 4. 컨테이너 재시작

### 특정 환경만 재시작

```bash
cd ~/taba_backend

# 개발 환경 재시작
docker-compose -f docker-compose.dev.yml restart backend

# 프로덕션 환경 재시작
docker-compose -f docker-compose.dev.yml -f docker-compose.prod.yml restart backend
```

### 모든 환경 재시작

```bash
cd ~/taba_backend

# 개발 환경
docker-compose -f docker-compose.dev.yml restart

# 프로덕션 환경
docker-compose -f docker-compose.dev.yml -f docker-compose.prod.yml restart
```

## 5. Nginx 로그 확인

```bash
# 프로덕션 접근 로그
sudo tail -f /var/log/nginx/taba-prod-access.log

# 개발 접근 로그
sudo tail -f /var/log/nginx/taba-dev-access.log

# 프로덕션 에러 로그
sudo tail -f /var/log/nginx/taba-prod-error.log

# 개발 에러 로그
sudo tail -f /var/log/nginx/taba-dev-error.log

# 전체 에러 로그
sudo tail -f /var/log/nginx/error.log
```

## 6. SSL 인증서 설정 (Let's Encrypt)

### Certbot 설치

```bash
sudo apt update
sudo apt install certbot python3-certbot-nginx
```

### SSL 인증서 발급

```bash
# 프로덕션 도메인
sudo certbot --nginx -d taba.asia -d www.taba.asia

# 개발 도메인
sudo certbot --nginx -d dev.taba.asia
```

### 인증서 자동 갱신 확인

```bash
sudo certbot renew --dry-run
```

## 7. 방화벽 설정 (UFW)

```bash
# HTTP/HTTPS 포트 허용
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# 상태 확인
sudo ufw status

# 활성화 (필요시)
sudo ufw enable
```

## 8. 문제 해결

### 포트 충돌 확인

```bash
# 특정 포트 사용 중인 프로세스 확인
sudo lsof -i :8080
sudo lsof -i :8081

# 또는
sudo netstat -tlnp | grep 8080
sudo netstat -tlnp | grep 8081
```

### Nginx 설정 다시 로드 (재시작 없이)

```bash
sudo nginx -s reload
```

### Nginx 설정 문법 확인

```bash
sudo nginx -t
```

### Docker 네트워크 확인

```bash
# 네트워크 목록
docker network ls

# 네트워크 상세 정보
docker network inspect taba-network
```

## 9. 빠른 상태 확인 스크립트

서버에서 다음 명령어를 실행하면 모든 상태를 한 번에 확인할 수 있습니다:

```bash
cd ~/taba_backend && \
echo "=== 컨테이너 상태 ===" && \
docker ps -a | grep taba && \
echo "" && \
echo "=== 포트 확인 ===" && \
sudo ss -tlnp | grep -E "(8080|8081)" && \
echo "" && \
echo "=== 프로덕션 헬스체크 ===" && \
curl -s http://localhost:8080/api/v1/actuator/health | head -5 && \
echo "" && \
echo "=== 개발 헬스체크 ===" && \
curl -s http://localhost:8081/api/v1/actuator/health | head -5
```

## 참고 문서

- [Nginx 설정 가이드](NGINX_SETUP.md) - 상세한 Nginx 설정 방법
- [502 Bad Gateway 해결 가이드](TROUBLESHOOTING_502.md) - 문제 해결 방법

