#!/bin/bash

# 목업 데이터 리셋 스크립트
# 사용법: ./scripts/quick-reset.sh
# 주의: ~/.my.cnf 파일에 MySQL 자격증명이 있어야 합니다.

set -e

DB_USER=${DB_USER:-root}
DB_NAME=${DB_NAME:-taba}
MOCK_DATA_FILE="src/main/resources/db/mock-data.sql"

echo "=== 목업 데이터 리셋 ==="

# MySQL 연결 확인
if ! mysql -u "$DB_USER" -e "SELECT 1" 2>/dev/null; then
    echo "❌ MySQL 연결 실패"
    echo "~/.my.cnf 파일을 설정하거나 비밀번호를 입력하세요."
    exit 1
fi

# 데이터베이스 생성
if ! mysql -u "$DB_USER" -e "USE $DB_NAME" 2>/dev/null; then
    echo "데이터베이스 '$DB_NAME' 생성 중..."
    mysql -u "$DB_USER" -e "CREATE DATABASE $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
fi

# 기존 데이터 삭제
echo "기존 데이터 삭제 중..."
mysql -u "$DB_USER" "$DB_NAME" <<EOF 2>/dev/null
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

# 목업 데이터 삽입
if [ ! -f "$MOCK_DATA_FILE" ]; then
    echo "❌ 목업 데이터 파일을 찾을 수 없습니다: $MOCK_DATA_FILE"
    exit 1
fi

echo "목업 데이터 삽입 중..."
mysql -u "$DB_USER" "$DB_NAME" < "$MOCK_DATA_FILE" 2>/dev/null

echo ""
echo "✅ 완료!"
echo ""
echo "테스트 계정:"
echo "  - alice@example.com / password123"
echo "  - bob@example.com / password123"
echo "  - charlie@example.com / password123"
