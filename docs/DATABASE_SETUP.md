# 데이터베이스 설정

## MySQL 설치

### macOS
```bash
brew install mysql
brew services start mysql
```

### Linux
```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
```

## 데이터베이스 생성

```bash
mysql -u root -p
CREATE DATABASE taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

## 연결 설정

### 환경 변수
```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=taba
export DB_USERNAME=root
export DB_PASSWORD=your_password
```

또는 `application.yml`에서 직접 설정 가능합니다.

## 스키마 생성

개발 환경 (`dev` 프로파일)에서는 자동으로 테이블이 생성됩니다:

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

프로덕션 환경에서는 `validate` 모드를 사용하여 스키마 검증만 수행합니다.

## 백업 및 복원

### 백업
```bash
mysqldump -u root -p taba > taba_backup.sql
```

### 복원
```bash
mysql -u root -p taba < taba_backup.sql
```
