#!/bin/bash

# MySQL 설정 스크립트
# 사용법: ./scripts/setup-mysql.sh

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}MySQL 설정 스크립트${NC}"
echo "================================"
echo ""

# MySQL 설치 확인
echo -e "${YELLOW}[1/4] MySQL 설치 확인...${NC}"
if ! command -v mysql &> /dev/null; then
    echo -e "${RED}MySQL이 설치되어 있지 않습니다.${NC}"
    echo -e "${YELLOW}설치 방법:${NC}"
    echo "  macOS: brew install mysql"
    echo "  Linux: sudo apt-get install mysql-server"
    exit 1
fi

MYSQL_VERSION=$(mysql --version 2>/dev/null | awk '{print $5}' | cut -d',' -f1 || echo "unknown")
echo -e "${GREEN}MySQL 버전: $MYSQL_VERSION${NC}"
echo ""

# MySQL 서비스 상태 확인
echo -e "${YELLOW}[2/4] MySQL 서비스 상태 확인...${NC}"
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    if brew services list 2>/dev/null | grep -q mysql; then
        if brew services list 2>/dev/null | grep mysql | grep -q "started"; then
            echo -e "${GREEN}MySQL 서비스가 실행 중입니다.${NC}"
        else
            echo -e "${YELLOW}MySQL 서비스가 중지되어 있습니다. 시작하시겠습니까? (y/n)${NC}"
            read -p "> " START_MYSQL
            if [ "$START_MYSQL" = "y" ] || [ "$START_MYSQL" = "Y" ]; then
                brew services start mysql
                echo -e "${GREEN}MySQL 서비스가 시작되었습니다.${NC}"
                sleep 2
            else
                echo -e "${RED}MySQL 서비스를 시작해야 합니다.${NC}"
                exit 1
            fi
        fi
    else
        echo -e "${YELLOW}MySQL 서비스가 설치되어 있지 않습니다.${NC}"
        echo -e "${YELLOW}Homebrew로 설치하시겠습니까? (y/n)${NC}"
        read -p "> " INSTALL_MYSQL
        if [ "$INSTALL_MYSQL" = "y" ] || [ "$INSTALL_MYSQL" = "Y" ]; then
            brew install mysql
            brew services start mysql
            echo -e "${GREEN}MySQL이 설치되고 시작되었습니다.${NC}"
            sleep 2
        else
            echo -e "${RED}MySQL을 설치해야 합니다.${NC}"
            exit 1
        fi
    fi
else
    # Linux
    if systemctl is-active --quiet mysql || systemctl is-active --quiet mysqld; then
        echo -e "${GREEN}MySQL 서비스가 실행 중입니다.${NC}"
    else
        echo -e "${YELLOW}MySQL 서비스가 중지되어 있습니다. 시작하시겠습니까? (y/n)${NC}"
        read -p "> " START_MYSQL
        if [ "$START_MYSQL" = "y" ] || [ "$START_MYSQL" = "Y" ]; then
            sudo systemctl start mysql || sudo systemctl start mysqld
            echo -e "${GREEN}MySQL 서비스가 시작되었습니다.${NC}"
            sleep 2
        else
            echo -e "${RED}MySQL 서비스를 시작해야 합니다.${NC}"
            exit 1
        fi
    fi
fi

echo ""

# MySQL 비밀번호 설정
echo -e "${YELLOW}[3/4] MySQL 비밀번호 설정...${NC}"
echo -e "${BLUE}현재 MySQL root 비밀번호를 입력하세요 (없으면 Enter):${NC}"
read -sp "> " CURRENT_PASSWORD
echo ""

echo -e "${BLUE}새로운 MySQL root 비밀번호를 입력하세요:${NC}"
read -sp "> " NEW_PASSWORD
echo ""

if [ -z "$NEW_PASSWORD" ]; then
    echo -e "${RED}비밀번호는 필수입니다.${NC}"
    exit 1
fi

echo -e "${BLUE}비밀번호 확인:${NC}"
read -sp "> " NEW_PASSWORD_CONFIRM
echo ""

if [ "$NEW_PASSWORD" != "$NEW_PASSWORD_CONFIRM" ]; then
    echo -e "${RED}비밀번호가 일치하지 않습니다.${NC}"
    exit 1
fi

# MySQL 비밀번호 변경 시도
if [ -z "$CURRENT_PASSWORD" ]; then
    # 비밀번호가 없는 경우
    mysql -u root <<EOF 2>/dev/null || true
ALTER USER 'root'@'localhost' IDENTIFIED BY '$NEW_PASSWORD';
FLUSH PRIVILEGES;
EOF
else
    # 기존 비밀번호가 있는 경우
    mysql -u root -p"$CURRENT_PASSWORD" <<EOF 2>/dev/null || true
ALTER USER 'root'@'localhost' IDENTIFIED BY '$NEW_PASSWORD';
FLUSH PRIVILEGES;
EOF
fi

# 비밀번호 변경 확인
if mysql -u root -p"$NEW_PASSWORD" -e "SELECT 1" 2>/dev/null; then
    echo -e "${GREEN}MySQL 비밀번호가 성공적으로 변경되었습니다!${NC}"
else
    echo -e "${RED}비밀번호 변경에 실패했습니다.${NC}"
    echo -e "${YELLOW}수동으로 설정하세요:${NC}"
    echo "  mysql -u root -p"
    echo "  ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_password';"
    echo "  FLUSH PRIVILEGES;"
    exit 1
fi

echo ""

# ~/.my.cnf 파일 생성
echo -e "${YELLOW}[4/4] MySQL 설정 파일 생성...${NC}"
read -p "~/.my.cnf 파일을 생성하시겠습니까? (y/n): " CREATE_CONFIG
if [ "$CREATE_CONFIG" = "y" ] || [ "$CREATE_CONFIG" = "Y" ]; then
    cat > ~/.my.cnf <<EOF
[client]
user=root
password=$NEW_PASSWORD
EOF
    chmod 600 ~/.my.cnf
    echo -e "${GREEN}~/.my.cnf 파일이 생성되었습니다.${NC}"
    echo -e "${YELLOW}주의: 이 파일은 보안상 중요하므로 공유하지 마세요.${NC}"
else
    echo -e "${YELLOW}설정 파일 생성을 건너뜁니다.${NC}"
fi

echo ""
echo -e "${GREEN}완료!${NC}"
echo ""
echo -e "${BLUE}다음 단계:${NC}"
echo "  1. 환경 변수 설정: export DB_PASSWORD=$NEW_PASSWORD"
echo "  2. 데이터베이스 생성: mysql -u root -p$NEW_PASSWORD -e \"CREATE DATABASE taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\""
echo "  3. 목업 데이터 삽입: ./scripts/quick-reset.sh 또는 ./scripts/reset-mock-data.sh"

