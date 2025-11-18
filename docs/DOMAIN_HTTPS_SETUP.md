# 도메인 연결 및 HTTPS 설정

## 1. 퍼블릭 IP 확인
```bash
curl ifconfig.me
```

## 2. DNS 설정
도메인 관리 패널에서 A 레코드 추가:
- 타입: A
- 호스트: @ (또는 www)
- 값: [퍼블릭 IPv4 주소]

## 3. Nginx 설정

### 설치
```bash
sudo apt update
sudo apt install -y nginx
```

### 설정 파일 생성
```bash
sudo nano /etc/nginx/sites-available/taba-backend
```

다음 내용 입력 (도메인을 실제 도메인으로 변경):
```nginx
server {
    listen 80;
    server_name example.com www.example.com;

    client_max_body_size 10M;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

### 활성화 및 재시작
```bash
sudo ln -sf /etc/nginx/sites-available/taba-backend /etc/nginx/sites-enabled/taba-backend
sudo nginx -t && sudo systemctl restart nginx
sudo ufw allow 80/tcp && sudo ufw allow 443/tcp
```

## 4. HTTPS 설정 (Let's Encrypt)

### Certbot 설치
```bash
sudo apt install -y certbot python3-certbot-nginx
```

### SSL 인증서 발급
```bash
sudo certbot --nginx -d example.com -d www.example.com
```

Certbot 실행 시:
1. 이메일 주소 입력
2. 이용약관 동의 (Y)
3. 이메일 수신 동의 (선택, N)
4. HTTP를 HTTPS로 리다이렉트 (2번 권장)

### 자동 갱신 확인
```bash
sudo certbot renew --dry-run
```

## 5. 확인
```bash
# DNS 전파 확인
nslookup example.com

# HTTP 확인
curl http://example.com/api/v1/actuator/health

# HTTPS 확인
curl https://example.com/api/v1/actuator/health
```

## 문제 해결

### Nginx 에러 로그
```bash
sudo tail -f /var/log/nginx/error.log
```

### 설정 테스트
```bash
sudo nginx -t
```

### 포트 확인
```bash
sudo netstat -tlnp | grep -E ':(80|443|8080)'
```
