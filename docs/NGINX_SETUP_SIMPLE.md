# Nginx 설정 (SSL 인증서 없이)

SSL 인증서가 아직 발급되지 않은 경우 사용할 설정입니다.

## 설정 파일 내용

```bash
sudo nano /etc/nginx/sites-available/taba
```

아래 내용을 복사하여 붙여넣으세요:

```nginx
# 프로덕션 환경 (www.taba.asia, taba.asia)
server {
    listen 80;
    listen [::]:80;
    server_name www.taba.asia taba.asia;

    access_log /var/log/nginx/taba-prod-access.log;
    error_log /var/log/nginx/taba-prod-error.log;
    client_max_body_size 10M;

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

    access_log /var/log/nginx/taba-dev-access.log;
    error_log /var/log/nginx/taba-dev-error.log;
    client_max_body_size 10M;

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
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
}
```

## 적용 명령어

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

## SSL 인증서 발급 (나중에)

SSL 인증서를 발급받은 후, 위 설정 파일에 HTTPS 블록을 추가하거나 Certbot이 자동으로 추가해줍니다:

```bash
# Certbot 설치
sudo apt update
sudo apt install certbot python3-certbot-nginx

# 프로덕션 도메인
sudo certbot --nginx -d taba.asia -d www.taba.asia

# 개발 도메인
sudo certbot --nginx -d dev.taba.asia
```

Certbot이 자동으로 HTTPS 설정을 추가하고 HTTP를 HTTPS로 리다이렉트합니다.

