# 데이터베이스 설정 가이드

## MySQL 설치

### macOS
```bash
brew install mysql
brew services start mysql
```

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql
```

### Windows
- MySQL 공식 사이트에서 설치: https://dev.mysql.com/downloads/mysql/

## 데이터베이스 생성

MySQL에 접속하여 데이터베이스를 생성합니다:

```bash
mysql -u root -p
```

```sql
CREATE DATABASE taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

또는 스크립트 실행:

```bash
mysql -u root -p < src/main/resources/db/init.sql
```

## 연결 설정

### 환경 변수 설정

`.env` 파일을 생성하거나 환경 변수를 설정합니다:

```bash
# 데이터베이스 설정
DB_HOST=localhost
DB_PORT=3306
DB_NAME=taba
DB_USERNAME=root
DB_PASSWORD=your_password
```

### application.yml 설정

기본 설정은 이미 구성되어 있습니다:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/taba?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
```

## 스키마 생성

### 개발 환경

`application-dev.yml`을 사용하면 자동으로 테이블이 생성됩니다:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

애플리케이션 실행:

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### 프로덕션 환경

프로덕션에서는 `ddl-auto: validate`를 사용하여 스키마 검증만 수행합니다.

마이그레이션 도구 사용을 권장합니다:
- Flyway
- Liquibase

## 연결 테스트

애플리케이션을 실행하면 자동으로 데이터베이스에 연결됩니다.

로그에서 다음 메시지를 확인할 수 있습니다:

```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

## 문제 해결

### 연결 실패 시

1. MySQL 서비스가 실행 중인지 확인:
```bash
# macOS/Linux
brew services list
# 또는
sudo systemctl status mysql
```

2. 포트 확인:
```bash
lsof -i :3306
```

3. 사용자 권한 확인:
```sql
SHOW GRANTS FOR 'root'@'localhost';
```

### 타임존 오류

`serverTimezone=Asia/Seoul`이 설정되어 있으므로 문제가 없어야 합니다.

### 문자 인코딩

`utf8mb4`를 사용하여 이모지 등 4바이트 문자를 지원합니다.

## 백업 및 복원

### 백업
```bash
mysqldump -u root -p taba > taba_backup.sql
```

### 복원
```bash
mysql -u root -p taba < taba_backup.sql
```

