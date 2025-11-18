#!/bin/bash

# 목업 데이터 리셋 스크립트
# 사용법: ./scripts/reset-mock-data.sh

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}목업 데이터 리셋 스크립트${NC}"
echo "================================"

# MySQL 설정 확인
read -p "MySQL 사용자명 (기본값: root): " DB_USER
DB_USER=${DB_USER:-root}

read -sp "MySQL 비밀번호: " DB_PASSWORD
echo ""

read -p "데이터베이스명 (기본값: taba): " DB_NAME
DB_NAME=${DB_NAME:-taba}

# MySQL 연결 테스트
echo -e "${YELLOW}[1/3] MySQL 연결 확인...${NC}"
if ! mysql -u "$DB_USER" -p"$DB_PASSWORD" -e "SELECT 1" 2>/dev/null; then
    echo -e "${RED}MySQL 연결에 실패했습니다. 사용자명과 비밀번호를 확인하세요.${NC}"
    exit 1
fi

# 데이터베이스 존재 확인
if ! mysql -u "$DB_USER" -p"$DB_PASSWORD" -e "USE $DB_NAME" 2>/dev/null; then
    echo -e "${YELLOW}데이터베이스 '$DB_NAME'가 존재하지 않습니다.${NC}"
    read -p "데이터베이스를 생성하시겠습니까? (y/n): " CREATE_DB
    if [ "$CREATE_DB" = "y" ] || [ "$CREATE_DB" = "Y" ]; then
        mysql -u "$DB_USER" -p"$DB_PASSWORD" -e "CREATE DATABASE $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}데이터베이스 '$DB_NAME'가 생성되었습니다.${NC}"
        else
            echo -e "${RED}데이터베이스 생성에 실패했습니다.${NC}"
            exit 1
        fi
    else
        echo -e "${RED}스크립트를 종료합니다.${NC}"
        exit 1
    fi
else
    echo -e "${GREEN}데이터베이스 '$DB_NAME' 확인 완료.${NC}"
fi

# 기존 데이터 삭제
echo -e "${YELLOW}[2/3] 기존 데이터 삭제...${NC}"
mysql -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" 2>/dev/null <<EOF
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

if [ $? -eq 0 ]; then
    echo -e "${GREEN}기존 데이터가 삭제되었습니다.${NC}"
else
    echo -e "${RED}데이터 삭제 중 오류가 발생했습니다.${NC}"
    exit 1
fi

# 목업 데이터 삽입
echo -e "${YELLOW}[3/3] 목업 데이터 삽입...${NC}"
MOCK_DATA_FILE="src/main/resources/db/mock-data.sql"

if [ ! -f "$MOCK_DATA_FILE" ]; then
    echo -e "${RED}목업 데이터 파일을 찾을 수 없습니다: $MOCK_DATA_FILE${NC}"
    exit 1
fi

mysql -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" 2>/dev/null < "$MOCK_DATA_FILE"

if [ $? -eq 0 ]; then
    echo -e "${GREEN}목업 데이터가 성공적으로 삽입되었습니다!${NC}"
    echo ""
    echo -e "${GREEN}테스트 계정:${NC}"
    echo "  - alice@example.com / password123"
    echo "  - bob@example.com / password123"
    echo "  - charlie@example.com / password123"
    echo ""
    echo -e "${GREEN}데이터 확인:${NC}"
    mysql -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" 2>/dev/null -e "
        SELECT COUNT(*) AS user_count FROM users;
        SELECT COUNT(*) AS friendship_count FROM friendships;
        SELECT COUNT(*) AS letter_count FROM letters;
        SELECT COUNT(*) AS notification_count FROM notifications;
    " 2>/dev/null
else
    echo -e "${RED}목업 데이터 삽입 중 오류가 발생했습니다.${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}완료!${NC}"

