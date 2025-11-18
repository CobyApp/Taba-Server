# Nginx 설정 가이드

개발과 프로덕션 환경을 동시에 실행하기 위한 Nginx 리버스 프록시 설정 가이드입니다.

## 포트 설정

- **프로덕션**: 외부 포트 `8080` → 내부 포트 `8080`
- **개발**: 외부 포트 `8081` → 내부 포트 `8080`

## Nginx 설정 파일

서버에서 다음 명령어로 Nginx 설정 파일을 생성/수정하세요:

```bash
sudo nano /etc/nginx/sites-available/taba
```

또는

```bash
sudo vi /etc/nginx/sites-available/taba
```

### 전체 설정 예시

```nginx
# 프로덕션 환경 (www.taba.asia, taba.asia)
server {
    listen 80;
    listen [::]:80;
    server_name www.taba.asia taba.asia;

    # HTTP to HTTPS 리다이렉트 (Let's Encrypt 사용 시)
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name www.taba.asia taba.asia;

    # SSL 인증서 설정 (Let's Encrypt 사용 시)
    ssl_certificate /etc/letsencrypt/live/taba.asia/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/taba.asia/privkey.pem;
    
    # SSL 설정 (권장)
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    # 로그 설정
    access_log /var/log/nginx/taba-prod-access.log;
    error_log /var/log/nginx/taba-prod-error.log;

    # 클라이언트 최대 바디 크기 (파일 업로드용)
    client_max_body_size 10M;

    # 프로덕션 백엔드로 프록시
    location / {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
        
        # 타임아웃 설정
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
}

# 개발 환경 (dev.taba.asia)
server {
    listen 80;
    listen [::]:80;
    server_name dev.taba.asia;

    # HTTP to HTTPS 리다이렉트 (Let's Encrypt 사용 시)
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name dev.taba.asia;

    # SSL 인증서 설정 (Let's Encrypt 사용 시)
    ssl_certificate /etc/letsencrypt/live/dev.taba.asia/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/dev.taba.asia/privkey.pem;
    
    # SSL 설정 (권장)
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    # 로그 설정
    access_log /var/log/nginx/taba-dev-access.log;
    error_log /var/log/nginx/taba-dev-error.log;

    # 클라이언트 최대 바디 크기 (파일 업로드용)
    client_max_body_size 10M;

    # 개발 백엔드로 프록시
    location / {
        proxy_pass http://localhost:8081;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
        
        # 타임아웃 설정
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
}
```

## Nginx 설정 적용 단계

### 1. 설정 파일 생성

```bash
sudo nano /etc/nginx/sites-available/taba
```

위의 설정 내용을 복사하여 붙여넣으세요.

### 2. 심볼릭 링크 생성 (활성화)

```bash
sudo ln -s /etc/nginx/sites-available/taba /etc/nginx/sites-enabled/
```

### 3. 설정 파일 문법 확인

```bash
sudo nginx -t
```

성공하면 다음과 같은 메시지가 표시됩니다:
```
nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
nginx: configuration is ok
```

### 4. Nginx 재시작

```bash
sudo systemctl restart nginx
```

또는

```bash
sudo service nginx restart
```

### 5. 상태 확인

```bash
sudo systemctl status nginx
```

## SSL 인증서 설정 (Let's Encrypt)

### Certbot 설치

```bash
sudo apt update
sudo apt install certbot python3-certbot-nginx
```

### SSL 인증서 발급

**프로덕션 도메인:**
```bash
sudo certbot --nginx -d taba.asia -d www.taba.asia
```

**개발 도메인:**
```bash
sudo certbot --nginx -d dev.taba.asia
```

### 자동 갱신 확인

```bash
sudo certbot renew --dry-run
```

## 포트 확인

설정이 제대로 적용되었는지 확인:

```bash
# 프로덕션 포트 확인
curl http://localhost:8080/api/v1/actuator/health

# 개발 포트 확인
curl http://localhost:8081/api/v1/actuator/health

# Nginx를 통한 접근 확인
curl https://www.taba.asia/api/v1/actuator/health
curl https://dev.taba.asia/api/v1/actuator/health
```

## 문제 해결

### Nginx 로그 확인

```bash
# 프로덕션 로그
sudo tail -f /var/log/nginx/taba-prod-error.log

# 개발 로그
sudo tail -f /var/log/nginx/taba-dev-error.log

# 전체 에러 로그
sudo tail -f /var/log/nginx/error.log
```

### 포트 충돌 확인

```bash
sudo netstat -tlnp | grep -E "(8080|8081)"
# 또는
sudo ss -tlnp | grep -E "(8080|8081)"
```

### Nginx 설정 다시 로드 (재시작 없이)

```bash
sudo nginx -s reload
```

## 방화벽 설정 (UFW 사용 시)

```bash
# HTTP/HTTPS 포트 허용
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# 상태 확인
sudo ufw status
```

## 참고사항

- 프로덕션과 개발 환경이 동시에 실행되므로 각각 다른 포트를 사용합니다.
- Nginx는 외부 요청을 받아 적절한 백엔드로 프록시합니다.
- SSL 인증서는 각 도메인별로 발급받아야 합니다.
- 포트 변경 시 Nginx 설정도 함께 업데이트해야 합니다.

