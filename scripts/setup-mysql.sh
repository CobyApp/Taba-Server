#!/bin/bash

# MySQL 설정 스크립트
# 사용법: ./scripts/setup-mysql.sh

set -e

DB_USER=${DB_USER:-root}
DB_NAME=${DB_NAME:-taba}

echo "=== MySQL 설정 ==="

# MySQL 설치 확인
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL이 설치되어 있지 않습니다."
    echo "macOS: brew install mysql"
    echo "Linux: sudo apt install mysql-server"
    exit 1
fi

# MySQL 서비스 확인
if [[ "$OSTYPE" == "darwin"* ]]; then
    if ! brew services list 2>/dev/null | grep mysql | grep -q "started"; then
        echo "⚠️  MySQL 서비스가 중지되어 있습니다."
        read -p "시작하시겠습니까? (y/n): " START
        if [ "$START" = "y" ] || [ "$START" = "Y" ]; then
            brew services start mysql
            sleep 2
        else
            exit 1
        fi
    fi
else
    if ! systemctl is-active --quiet mysql && ! systemctl is-active --quiet mysqld; then
        echo "⚠️  MySQL 서비스가 중지되어 있습니다."
        read -p "시작하시겠습니까? (y/n): " START
        if [ "$START" = "y" ] || [ "$START" = "Y" ]; then
            sudo systemctl start mysql || sudo systemctl start mysqld
            sleep 2
        else
            exit 1
        fi
    fi
fi

# 비밀번호 설정
echo ""
echo "현재 MySQL root 비밀번호를 입력하세요 (없으면 Enter):"
read -sp "> " CURRENT_PASSWORD
echo ""

echo "새로운 MySQL root 비밀번호를 입력하세요:"
read -sp "> " NEW_PASSWORD
echo ""

if [ -z "$NEW_PASSWORD" ]; then
    echo "❌ 비밀번호는 필수입니다."
    exit 1
fi

# 비밀번호 변경
if [ -z "$CURRENT_PASSWORD" ]; then
    mysql -u "$DB_USER" <<EOF 2>/dev/null || true
ALTER USER 'root'@'localhost' IDENTIFIED BY '$NEW_PASSWORD';
FLUSH PRIVILEGES;
EOF
else
    mysql -u "$DB_USER" -p"$CURRENT_PASSWORD" <<EOF 2>/dev/null || true
ALTER USER 'root'@'localhost' IDENTIFIED BY '$NEW_PASSWORD';
FLUSH PRIVILEGES;
EOF
fi

# 확인
if mysql -u "$DB_USER" -p"$NEW_PASSWORD" -e "SELECT 1" 2>/dev/null; then
    echo "✅ MySQL 비밀번호 변경 완료!"
else
    echo "❌ 비밀번호 변경 실패"
    exit 1
fi

# 데이터베이스 생성
echo ""
echo "데이터베이스 '$DB_NAME' 생성 중..."
mysql -u "$DB_USER" -p"$NEW_PASSWORD" <<EOF 2>/dev/null || true
CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EOF

echo ""
echo "✅ 완료!"
echo ""
echo "다음 단계:"
echo "  export DB_PASSWORD=$NEW_PASSWORD"
echo "  ./gradlew bootRun --args='--spring.profiles.active=dev'"
