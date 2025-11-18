# 로컬 테스트 가이드

로컬 환경에서 애플리케이션을 실행하고 테스트하기 위한 가이드입니다.

## 목차

1. [환경 설정](#환경-설정)
2. [데이터베이스 설정](#데이터베이스-설정)
3. [목업 데이터 삽입](#목업-데이터-삽입)
4. [애플리케이션 실행](#애플리케이션-실행)
5. [테스트 계정](#테스트-계정)
6. [API 테스트](#api-테스트)

---

## 환경 설정

### 1. 필수 요구사항

- Java 17 이상
- MySQL 8.0 이상
- Gradle 8.5 이상 (또는 Gradle Wrapper 사용)

### 2. MySQL 설정

#### 방법 1: 자동 설정 스크립트 (권장)

```bash
# MySQL 설치, 서비스 시작, 비밀번호 설정을 자동으로 수행
./scripts/setup-mysql.sh
```

스크립트는 다음을 수행합니다:
1. MySQL 설치 확인
2. MySQL 서비스 시작
3. MySQL root 비밀번호 설정
4. ~/.my.cnf 파일 생성 (선택사항)

#### 방법 2: 수동 설정

**MySQL 설치** (macOS):
```bash
brew install mysql
brew services start mysql
```

**MySQL 비밀번호 설정**:
```bash
# MySQL 접속 (처음에는 비밀번호 없음)
mysql -u root

# MySQL 콘솔에서 실행
ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_password';
FLUSH PRIVILEGES;
EXIT;
```

**~/.my.cnf 파일 생성** (선택사항, 비밀번호 입력 없이 사용):
```bash
cat > ~/.my.cnf <<EOF
[client]
user=root
password=your_password
EOF
chmod 600 ~/.my.cnf
```

### 3. 환경 변수 설정

로컬 개발 환경에서는 다음 환경 변수를 설정합니다:

```bash
# MySQL 설정
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=taba
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password

# JWT 설정
export JWT_SECRET=$(openssl rand -hex 32)

# 서버 설정
export SERVER_PORT=8080
export SERVER_URL=http://localhost:8080/api/v1

# Redis 설정 (선택사항)
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=
```

또는 `.bashrc` 또는 `.zshrc`에 추가:

```bash
# ~/.bashrc 또는 ~/.zshrc에 추가
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=$(openssl rand -hex 32)
```

---

## 데이터베이스 설정

### 1. MySQL 데이터베이스 생성

```bash
# MySQL 접속
mysql -u root -p

# 데이터베이스 생성
CREATE DATABASE taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 사용자 생성 및 권한 부여 (선택사항)
CREATE USER IF NOT EXISTS 'taba_user'@'localhost' IDENTIFIED BY 'taba_password';
GRANT ALL PRIVILEGES ON taba.* TO 'taba_user'@'localhost';
FLUSH PRIVILEGES;

# 종료
EXIT;
```

### 2. 테이블 자동 생성

애플리케이션을 실행하면 `application-dev.yml`의 `ddl-auto: update` 설정에 의해 테이블이 자동으로 생성됩니다.

또는 수동으로 스키마 실행:

```bash
mysql -u root -p taba < src/main/resources/db/init.sql
```

---

## 목업 데이터 삽입

### 방법 1: 빠른 스크립트 사용 (권장, 비밀번호 입력 없음)

**전제조건**: `~/.my.cnf` 파일에 MySQL 자격증명 설정

```bash
# ~/.my.cnf 파일 생성 (선택사항)
cat > ~/.my.cnf <<EOF
[client]
user=root
password=your_mysql_password
EOF
chmod 600 ~/.my.cnf

# 빠른 리셋 스크립트 실행
./scripts/quick-reset.sh
```

### 방법 2: 대화형 스크립트 사용

```bash
# 리셋 스크립트 실행 (비밀번호 입력 필요)
./scripts/reset-mock-data.sh
```

스크립트는 다음을 수행합니다:
1. MySQL 연결 확인
2. 데이터베이스 존재 확인 (없으면 생성)
3. 기존 데이터 삭제
4. 목업 데이터 삽입
5. 삽입된 데이터 확인

### 방법 3: 수동 SQL 실행

```bash
# MySQL에 직접 실행
mysql -u root -p taba < src/main/resources/db/mock-data.sql
```

또는 MySQL 클라이언트에서:

```bash
mysql -u root -p taba
```

```sql
source src/main/resources/db/mock-data.sql
```

### 방법 4: 기존 데이터 삭제 후 재삽입

기존 데이터가 있어서 오류가 발생하는 경우:

```bash
# 1. 기존 데이터 삭제
mysql -u root -p taba <<EOF
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE letter_recipients;
TRUNCATE TABLE letter_images;
TRUNCATE TABLE letter_reports;
TRUNCATE TABLE letters;
TRUNCATE TABLE friendships;
TRUNCATE TABLE invite_codes;
TRUNCATE TABLE notifications;
TRUNCATE TABLE password_reset_tokens;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;
EOF

# 2. 목업 데이터 삽입
mysql -u root -p taba < src/main/resources/db/mock-data.sql
```

### 2. 데이터 확인

```sql
-- 사용자 확인
SELECT id, email, username, nickname FROM users;

-- 친구 관계 확인
SELECT u1.nickname AS user, u2.nickname AS friend 
FROM friendships f
JOIN users u1 ON f.user_id = u1.id
JOIN users u2 ON f.friend_id = u2.id;

-- 편지 확인
SELECT l.id, u1.nickname AS sender, u2.nickname AS recipient, l.title, l.visibility
FROM letters l
JOIN users u1 ON l.sender_id = u1.id
LEFT JOIN users u2 ON l.recipient_id = u2.id;
```

---

## 애플리케이션 실행

### 1. Gradle로 실행

```bash
# 개발 프로파일로 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
```

또는:

```bash
# 빌드 후 실행
./gradlew build
java -jar build/libs/taba-backend-1.0.0.jar --spring.profiles.active=dev
```

### 2. IDE에서 실행

1. `TabaApplication.java`를 열기
2. Run Configuration 설정:
   - VM options: 없음
   - Program arguments: `--spring.profiles.active=dev`
   - Environment variables: 위에서 설정한 환경 변수들

### 3. 실행 확인

애플리케이션이 정상적으로 실행되면:

- 콘솔에 "Started TabaApplication" 메시지 출력
- http://localhost:8080/api/v1/swagger-ui/index.html 접속 가능
- http://localhost:8080/api/v1/actuator/health 접속 시 `{"status":"UP"}` 응답

---

## 테스트 계정

목업 데이터에 포함된 테스트 계정:

| 이메일 | 비밀번호 | 닉네임 | 설명 |
|--------|---------|--------|------|
| alice@example.com | password123 | 앨리스 | 친구 2명, 편지 여러 개 |
| bob@example.com | password123 | 밥 | 친구 2명, 편지 여러 개 |
| charlie@example.com | password123 | 찰리 | 친구 1명, 편지 여러 개 |
| diana@example.com | password123 | 다이애나 | 친구 1명, 편지 1개 |
| eve@example.com | password123 | 이브 | 친구 없음 |
| frank@example.com | password123 | 프랭크 | 친구 없음 |
| grace@example.com | password123 | 그레이스 | 친구 없음 |
| henry@example.com | password123 | 헨리 | 친구 없음 |

**모든 계정의 비밀번호는 `password123`입니다.**

---

## API 테스트

### 1. Swagger UI 사용

애플리케이션 실행 후:

http://localhost:8080/api/v1/swagger-ui/index.html

1. `/auth/login` 엔드포인트로 로그인
2. 응답에서 `token` 복사
3. 우측 상단 "Authorize" 버튼 클릭
4. `Bearer {token}` 형식으로 입력
5. 다른 API 테스트

### 2. cURL 사용

#### 로그인

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "password123"
  }'
```

응답에서 `token`을 복사합니다.

#### 친구 목록 조회

```bash
curl -X GET http://localhost:8080/api/v1/bouquets \
  -H "Authorization: Bearer {token}"
```

#### 편지 목록 조회 (친구별)

```bash
# 밥의 ID를 friendId로 사용
curl -X GET "http://localhost:8080/api/v1/bouquets/22222222-2222-2222-2222-222222222222/letters?page=0&size=20" \
  -H "Authorization: Bearer {token}"
```

#### 공개 편지 목록 조회

```bash
curl -X GET "http://localhost:8080/api/v1/letters/public?page=0&size=20" \
  -H "Authorization: Bearer {token}"
```

### 3. Postman 사용

1. **Collection 생성**
   - Base URL: `http://localhost:8080/api/v1`
   - Authorization: Bearer Token (변수로 설정)

2. **환경 변수 설정**
   - `base_url`: `http://localhost:8080/api/v1`
   - `token`: 로그인 후 자동 설정

3. **요청 예시**
   - `POST {{base_url}}/auth/login`
   - `GET {{base_url}}/bouquets` (Header: `Authorization: Bearer {{token}}`)

---

## 목업 데이터 구조

### 사용자 관계

```
앨리스 (alice@example.com)
├── 친구: 밥, 찰리
├── 보낸 편지: 공개 1개, 밥에게 2개, 찰리에게 1개
└── 받은 편지: 밥으로부터 1개 (읽음), 찰리로부터 1개 (미읽음)

밥 (bob@example.com)
├── 친구: 앨리스, 다이애나
├── 보낸 편지: 공개 1개, 앨리스에게 1개, 다이애나에게 1개
└── 받은 편지: 앨리스로부터 1개 (읽음)

찰리 (charlie@example.com)
├── 친구: 앨리스
├── 보낸 편지: 공개 1개, 앨리스에게 1개
└── 받은 편지: 앨리스로부터 1개 (미읽음)

다이애나 (diana@example.com)
├── 친구: 밥
└── 받은 편지: 밥으로부터 1개 (읽음)
```

### 편지 종류

1. **공개 편지 (PUBLIC)**
   - 앨리스: "안녕하세요!" (2일 전)
   - 밥: "좋은 아침입니다" (1일 전)
   - 찰리: "행복한 하루" (3시간 전, 익명)

2. **직접 전송 편지 (DIRECT)**
   - 앨리스 → 밥: "밥에게" (1일 전)
   - 밥 → 앨리스: "앨리스에게" (12시간 전, 읽음)
   - 앨리스 → 찰리: "찰리에게" (6시간 전)
   - 찰리 → 앨리스: "앨리스에게" (2시간 전, 미읽음)
   - 밥 → 다이애나: "다이애나에게" (1시간 전, 읽음)

---

## 문제 해결

### 1. 데이터베이스 연결 오류

```
Error: Access denied for user 'root'@'localhost'
```

**해결 방법**:
- MySQL 비밀번호 확인
- 환경 변수 `DB_PASSWORD` 설정 확인
- MySQL 사용자 권한 확인

### 2. 테이블이 생성되지 않음

```
Error: Table 'taba.users' doesn't exist
```

**해결 방법**:
- `application-dev.yml`에서 `ddl-auto: update` 확인
- 데이터베이스가 올바르게 생성되었는지 확인
- 애플리케이션 재시작

### 3. 목업 데이터 삽입 실패

```
Error: Duplicate entry for key 'PRIMARY'
```

**해결 방법**:
- 기존 데이터 삭제 후 재삽입
- 또는 `TRUNCATE` 문 사용 (주의: 모든 데이터 삭제)

```sql
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE letter_recipients;
TRUNCATE TABLE letter_images;
TRUNCATE TABLE letters;
TRUNCATE TABLE friendships;
TRUNCATE TABLE invite_codes;
TRUNCATE TABLE notifications;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;
```

### 4. JWT 토큰 오류

```
Error: Invalid JWT token
```

**해결 방법**:
- 환경 변수 `JWT_SECRET` 확인
- 토큰이 만료되지 않았는지 확인
- 로그인 후 새 토큰 발급

---

## 추가 리소스

- [API 명세서](API_SPECIFICATION.md)
- [프로젝트 가이드](PROJECT_GUIDE.md)
- [빠른 시작](QUICK_START.md)

---

## 참고사항

- 목업 데이터는 테스트용이며, 실제 프로덕션 환경에서는 사용하지 마세요.
- 비밀번호는 모두 `password123`으로 설정되어 있으므로, 보안에 주의하세요.
- UUID는 고정값을 사용하여 테스트 시 일관성을 유지합니다.

