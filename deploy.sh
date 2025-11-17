#!/bin/bash

# Taba Backend 배포 스크립트
# 사용법: ./deploy.sh [서버주소] [포트]
# 예: ./deploy.sh coby@cobyserver.iptime.org 8080

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 기본값 설정
SERVER=${1:-coby@cobyserver.iptime.org}
EXTERNAL_PORT=${2:-8080}
REMOTE_DIR="~/taba_backend"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Taba Backend 배포 스크립트${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "서버: ${YELLOW}${SERVER}${NC}"
echo -e "포트: ${YELLOW}${EXTERNAL_PORT}${NC}"
echo -e "원격 디렉토리: ${YELLOW}${REMOTE_DIR}${NC}"
echo ""

# 1. 로컬에서 빌드 확인
echo -e "${YELLOW}[1/6] 로컬 빌드 확인...${NC}"
if [ ! -f "build/libs/taba-backend-1.0.0.jar" ]; then
    echo -e "${YELLOW}JAR 파일이 없습니다. 빌드를 시작합니다...${NC}"
    ./gradlew clean build -x test
fi

# 2. 원격 서버 연결 확인
echo -e "${YELLOW}[2/6] 원격 서버 연결 확인...${NC}"
ssh -o ConnectTimeout=5 ${SERVER} "echo '연결 성공'" || {
    echo -e "${RED}서버 연결 실패: ${SERVER}${NC}"
    exit 1
}

# 3. 원격 서버에 디렉토리 생성
echo -e "${YELLOW}[3/6] 원격 서버 디렉토리 준비...${NC}"
ssh ${SERVER} "mkdir -p ${REMOTE_DIR}"

# 4. 필요한 파일 전송
echo -e "${YELLOW}[4/6] 파일 전송 중...${NC}"
scp docker-compose.yml docker-compose.prod.yml Dockerfile ${SERVER}:${REMOTE_DIR}/
scp -r src/main/resources/db ${SERVER}:${REMOTE_DIR}/

# 5. 환경 변수 확인
echo -e "${YELLOW}[5/6] 환경 변수 확인...${NC}"
if [ -z "$DB_PASSWORD" ] || [ -z "$JWT_SECRET" ] || [ -z "$SERVER_URL" ]; then
    echo -e "${RED}환경 변수가 설정되지 않았습니다!${NC}"
    echo -e "${YELLOW}다음 환경 변수를 설정하세요:${NC}"
    echo -e "  export DB_NAME=taba"
    echo -e "  export DB_USERNAME=taba_user"
    echo -e "  export DB_PASSWORD=your_password"
    echo -e "  export JWT_SECRET=\$(openssl rand -hex 32)"
    echo -e "  export SERVER_URL=http://cobyserver.iptime.org:${EXTERNAL_PORT}/api/v1"
    echo -e "  export REDIS_PASSWORD=" # 선택사항
    exit 1
fi

# 6. 원격 서버에서 Docker Compose 실행 (환경변수 주입)
echo -e "${YELLOW}[6/6] Docker 컨테이너 시작...${NC}"
ssh ${SERVER} bash << EOF
    cd ${REMOTE_DIR}
    export DB_HOST=mysql
    export DB_PORT=3306
    export DB_NAME=${DB_NAME:-taba}
    export DB_USERNAME=${DB_USERNAME:-taba_user}
    export DB_PASSWORD=${DB_PASSWORD}
    export REDIS_HOST=redis
    export REDIS_PORT=6379
    export REDIS_PASSWORD=${REDIS_PASSWORD:-}
    export JWT_SECRET=${JWT_SECRET}
    export JWT_EXPIRATION=${JWT_EXPIRATION:-604800000}
    export SERVER_PORT=8080
    export SERVER_URL=${SERVER_URL}
    export EXTERNAL_PORT=${EXTERNAL_PORT}
    export SPRING_PROFILES_ACTIVE=prod
    export FILE_UPLOAD_DIR=/app/uploads
    docker-compose -f docker-compose.yml -f docker-compose.prod.yml pull || true
    docker-compose -f docker-compose.yml -f docker-compose.prod.yml build --no-cache
    docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
EOF

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}배포 완료!${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "서버 주소: ${YELLOW}http://cobyserver.iptime.org:${EXTERNAL_PORT}/api/v1${NC}"
echo -e "Health Check: ${YELLOW}http://cobyserver.iptime.org:${EXTERNAL_PORT}/api/v1/actuator/health${NC}"
echo -e "Swagger UI: ${YELLOW}http://cobyserver.iptime.org:${EXTERNAL_PORT}/api/v1/swagger-ui/index.html${NC}"
echo ""
echo -e "${YELLOW}중요 사항:${NC}"
echo -e "1. 라우터에서 포트포워드 설정이 필요합니다:"
echo -e "   - 외부 포트: ${EXTERNAL_PORT} -> 내부 IP: 192.168.0.3:${EXTERNAL_PORT} (TCP)"
echo -e "2. 로그 확인:"
echo -e "   ssh ${SERVER} 'cd ${REMOTE_DIR} && docker-compose logs -f backend'"
echo ""

