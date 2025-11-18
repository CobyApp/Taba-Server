# MySQL 설정 가이드

MySQL 비밀번호 설정 및 연결 문제 해결 가이드입니다.

## 빠른 시작

### 자동 설정 (권장)

```bash
./scripts/setup-mysql.sh
```

### 수동 설정

```bash
# 1. MySQL 접속 (비밀번호 없이)
mysql -u root

# 2. 비밀번호 설정
ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_password';
FLUSH PRIVILEGES;
EXIT;

# 3. 비밀번호로 접속 테스트
mysql -u root -p
```

---

## 문제 해결

### 1. MySQL 접속 불가

**증상**: `mysql: command not found`

**해결**:
```bash
# macOS
brew install mysql
brew services start mysql

# Linux
sudo apt-get install mysql-server
sudo systemctl start mysql
```

---

### 2. 비밀번호를 잊어버린 경우

#### macOS (Homebrew)

```bash
# 1. MySQL 서비스 중지
brew services stop mysql

# 2. 안전 모드로 시작
mysqld_safe --skip-grant-tables &

# 3. MySQL 접속 (비밀번호 없이)
mysql -u root

# 4. 비밀번호 재설정
USE mysql;
ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
FLUSH PRIVILEGES;
EXIT;

# 5. MySQL 재시작
brew services restart mysql
```

#### Linux

```bash
# 1. MySQL 서비스 중지
sudo systemctl stop mysql

# 2. 안전 모드로 시작
sudo mysqld_safe --skip-grant-tables &

# 3. MySQL 접속
sudo mysql -u root

# 4. 비밀번호 재설정
USE mysql;
ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
FLUSH PRIVILEGES;
EXIT;

# 5. MySQL 재시작
sudo systemctl start mysql
```

---

### 3. "Access denied" 오류

**증상**: `ERROR 1045 (28000): Access denied for user 'root'@'localhost'`

**해결 방법**:

1. **비밀번호 확인**:
```bash
# 비밀번호 입력 시 대소문자, 특수문자 확인
mysql -u root -p
```

2. **비밀번호 재설정** (위의 "비밀번호를 잊어버린 경우" 참고)

3. **사용자 확인**:
```sql
SELECT user, host FROM mysql.user WHERE user='root';
```

---

### 4. MySQL 서비스가 시작되지 않음

#### macOS

```bash
# 서비스 상태 확인
brew services list | grep mysql

# 서비스 시작
brew services start mysql

# 로그 확인
tail -f /usr/local/var/mysql/*.err
```

#### Linux

```bash
# 서비스 상태 확인
sudo systemctl status mysql

# 서비스 시작
sudo systemctl start mysql

# 로그 확인
sudo journalctl -u mysql -f
```

---

### 5. 포트 충돌

**증상**: `ERROR 2002 (HY000): Can't connect to local MySQL server`

**해결**:
```bash
# 포트 사용 확인
lsof -i :3306

# 다른 프로세스가 사용 중이면 종료
kill -9 <PID>

# MySQL 재시작
brew services restart mysql  # macOS
sudo systemctl restart mysql  # Linux
```

---

## ~/.my.cnf 파일 설정

비밀번호를 매번 입력하지 않으려면 설정 파일을 사용하세요:

```bash
# 설정 파일 생성
cat > ~/.my.cnf <<EOF
[client]
user=root
password=your_password
EOF

# 권한 설정 (보안상 중요)
chmod 600 ~/.my.cnf
```

**주의**: 이 파일은 보안상 중요하므로:
- Git에 커밋하지 마세요
- 다른 사람과 공유하지 마세요
- 권한을 600으로 설정하세요

---

## 환경 변수 설정

애플리케이션에서 사용할 환경 변수:

```bash
# ~/.zshrc 또는 ~/.bashrc에 추가
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=$(openssl rand -hex 32)

# 적용
source ~/.zshrc  # 또는 source ~/.bashrc
```

---

## 데이터베이스 생성

```bash
# MySQL 접속
mysql -u root -p

# 데이터베이스 생성
CREATE DATABASE taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 사용자 생성 (선택사항)
CREATE USER 'taba_user'@'localhost' IDENTIFIED BY 'taba_password';
GRANT ALL PRIVILEGES ON taba.* TO 'taba_user'@'localhost';
FLUSH PRIVILEGES;

# 종료
EXIT;
```

---

## 연결 테스트

```bash
# 1. MySQL 접속 테스트
mysql -u root -p -e "SELECT 1;"

# 2. 데이터베이스 확인
mysql -u root -p -e "SHOW DATABASES;"

# 3. 테이블 확인
mysql -u root -p taba -e "SHOW TABLES;"
```

---

## 추가 리소스

- [MySQL 공식 문서](https://dev.mysql.com/doc/)
- [로컬 테스트 가이드](LOCAL_TESTING.md)

