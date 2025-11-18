#!/bin/bash

# 무중단 배포 스크립트
# Blue-Green 배포 방식 사용
# 
# 작동 방식:
# 1. 새 컨테이너를 임시 포트(8081)에서 시작
# 2. 새 컨테이너 헬스체크 확인
# 3. 기존 컨테이너 중지 (Graceful shutdown)
# 4. 새 컨테이너를 메인 포트(8080)로 재시작

set -e  # 에러 발생 시 스크립트 중단

# 색상 출력
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 변수 설정
PROJECT_DIR="${1:-~/taba_backend}"
COMPOSE_FILE="${PROJECT_DIR}/docker-compose.yml"
COMPOSE_PROD_FILE="${PROJECT_DIR}/docker-compose.prod.yml"
CURRENT_SERVICE="backend"
EXTERNAL_PORT="${EXTERNAL_PORT:-8080}"
TEMP_PORT=$((EXTERNAL_PORT + 1))  # 임시 포트 (예: 8081)
HEALTH_CHECK_TIMEOUT=180  # 헬스체크 타임아웃 (초)
HEALTH_CHECK_INTERVAL=5   # 헬스체크 간격 (초)

echo -e "${GREEN}🚀 무중단 배포 시작${NC}"

cd "$PROJECT_DIR" || exit 1

# 1. 환경 변수 확인
echo -e "${YELLOW}🔍 환경 변수 확인...${NC}"
if [ -z "$DB_PASSWORD" ] || [ -z "$JWT_SECRET" ]; then
    echo -e "${RED}❌ 필수 환경 변수가 설정되지 않았습니다.${NC}"
    echo -e "${RED}   DB_PASSWORD, JWT_SECRET이 필요합니다.${NC}"
    exit 1
fi

# 2. 필요한 이미지 pull (MySQL, Redis - 서버에서 이미지가 없을 경우 대비)
echo -e "${YELLOW}📥 필요한 이미지 pull 중 (MySQL, Redis)...${NC}"
docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" pull mysql redis || {
    echo -e "${YELLOW}⚠️  이미지 pull 실패 (이미 존재하거나 네트워크 문제일 수 있음)${NC}"
    echo -e "${YELLOW}   계속 진행합니다...${NC}"
}

# 3. 새 이미지 빌드 (clean 빌드로 오래된 클래스 파일 제거)
echo -e "${YELLOW}📦 새 이미지 빌드 중...${NC}"
echo -e "${YELLOW}   (clean 빌드로 오래된 클래스 파일 제거)${NC}"
echo -e "${YELLOW}   빌드 로그를 확인하세요 (시간이 오래 걸릴 수 있습니다)...${NC}"
if ! docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" build --no-cache --progress=plain "$CURRENT_SERVICE"; then
    echo -e "${RED}❌ 이미지 빌드 실패!${NC}"
    echo -e "${YELLOW}로그를 확인하세요:${NC}"
    docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" logs "$CURRENT_SERVICE" || true
    exit 1
fi
echo -e "${GREEN}✅ 이미지 빌드 완료${NC}"

# 4. 임시 포트로 새 컨테이너 시작
echo -e "${YELLOW}🔄 새 인스턴스 시작 중 (임시 포트 ${TEMP_PORT})...${NC}"

# 환경 변수 확실히 export (docker-compose가 읽을 수 있도록)
export DB_HOST=${DB_HOST:-mysql}
export DB_PORT=${DB_PORT:-3306}
export DB_NAME=${DB_NAME:-taba}
export DB_USERNAME=${DB_USERNAME:-taba_user}
export DB_PASSWORD=${DB_PASSWORD:-}
export REDIS_HOST=${REDIS_HOST:-redis}
export REDIS_PORT=${REDIS_PORT:-6379}
export REDIS_PASSWORD=${REDIS_PASSWORD:-}
export JWT_SECRET=${JWT_SECRET:-}
export JWT_EXPIRATION=${JWT_EXPIRATION:-604800000}
export SERVER_PORT=${SERVER_PORT:-8080}
export SERVER_URL=${SERVER_URL:-}
export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-prod}
export FILE_UPLOAD_DIR=${FILE_UPLOAD_DIR:-/app/uploads}

# 임시 override 파일 생성 (모든 환경 변수 포함)
cat > "${PROJECT_DIR}/docker-compose.temp.yml" << EOF
version: '3.8'
services:
  backend-temp:
    extends:
      service: backend
    container_name: taba-backend-temp
    ports:
      - "${TEMP_PORT}:8080"
    environment:
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE}
      DB_HOST: ${DB_HOST}
      DB_PORT: ${DB_PORT}
      DB_NAME: ${DB_NAME}
      DB_USERNAME: ${DB_USERNAME}
      DB_PASSWORD: ${DB_PASSWORD}
      REDIS_HOST: ${REDIS_HOST}
      REDIS_PORT: ${REDIS_PORT}
      REDIS_PASSWORD: ${REDIS_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRATION: ${JWT_EXPIRATION}
      SERVER_PORT: 8080
      SERVER_URL: ${SERVER_URL}
      FILE_UPLOAD_DIR: ${FILE_UPLOAD_DIR}
EOF

# 임시 컨테이너 시작
echo -e "${YELLOW}   임시 컨테이너 시작 중...${NC}"
if ! docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" -f "${PROJECT_DIR}/docker-compose.temp.yml" up -d backend-temp; then
    echo -e "${RED}❌ 임시 컨테이너 시작 실패!${NC}"
    echo -e "${YELLOW}로그를 확인하세요:${NC}"
    docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" -f "${PROJECT_DIR}/docker-compose.temp.yml" logs backend-temp || true
    rm -f "${PROJECT_DIR}/docker-compose.temp.yml"
    exit 1
fi
echo -e "${GREEN}✅ 임시 컨테이너 시작 완료${NC}"

# 5. 새 컨테이너 헬스체크
echo -e "${YELLOW}🏥 새 인스턴스 헬스체크 중...${NC}"
elapsed=0
health_check_passed=false

while [ $elapsed -lt $HEALTH_CHECK_TIMEOUT ]; do
  # 컨테이너가 실행 중인지 확인
  if ! docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" -f "${PROJECT_DIR}/docker-compose.temp.yml" ps backend-temp | grep -q "Up"; then
    echo -e "${RED}❌ 임시 컨테이너가 중지되었습니다!${NC}"
    echo -e "${YELLOW}로그 확인:${NC}"
    docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" -f "${PROJECT_DIR}/docker-compose.temp.yml" logs --tail=50 backend-temp || true
    health_check_passed=false
    break
  fi
  
  if docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" -f "${PROJECT_DIR}/docker-compose.temp.yml" exec -T backend-temp wget --no-verbose --tries=1 --spider "http://localhost:8080/api/v1/actuator/health" 2>&1 > /dev/null; then
    echo -e "${GREEN}✅ 새 인스턴스 헬스체크 통과!${NC}"
    health_check_passed=true
    break
  fi
  
  echo -e "${YELLOW}⏳ 헬스체크 대기 중... (${elapsed}/${HEALTH_CHECK_TIMEOUT}초)${NC}"
  # 30초마다 로그 확인
  if [ $((elapsed % 30)) -eq 0 ] && [ $elapsed -gt 0 ]; then
    echo -e "${YELLOW}   현재 상태 확인 중...${NC}"
    docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" -f "${PROJECT_DIR}/docker-compose.temp.yml" logs --tail=20 backend-temp || true
  fi
  sleep $HEALTH_CHECK_INTERVAL
  elapsed=$((elapsed + HEALTH_CHECK_INTERVAL))
done

if [ "$health_check_passed" = false ]; then
  echo -e "${RED}❌ 새 인스턴스 헬스체크 실패. 배포 중단 및 롤백 중...${NC}"
  docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" -f "${PROJECT_DIR}/docker-compose.temp.yml" down backend-temp
  rm -f "${PROJECT_DIR}/docker-compose.temp.yml"
  exit 1
fi

# 6. 추가 헬스체크 (연속 3번 성공 확인)
echo -e "${YELLOW}🔍 추가 헬스체크 중 (연속 3번 확인)...${NC}"
health_check_count=0
for i in {1..3}; do
  sleep 2
  if docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" -f "${PROJECT_DIR}/docker-compose.temp.yml" exec -T backend-temp wget --no-verbose --tries=1 --spider "http://localhost:8080/api/v1/actuator/health" 2>&1 > /dev/null; then
    health_check_count=$((health_check_count + 1))
    echo -e "${GREEN}✅ 헬스체크 ${i}/3 성공${NC}"
  else
    echo -e "${RED}❌ 헬스체크 ${i}/3 실패${NC}"
    break
  fi
done

if [ $health_check_count -lt 3 ]; then
  echo -e "${RED}❌ 추가 헬스체크 실패. 배포 중단 및 롤백 중...${NC}"
  docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" -f "${PROJECT_DIR}/docker-compose.temp.yml" down backend-temp
  rm -f "${PROJECT_DIR}/docker-compose.temp.yml"
  exit 1
fi

# 7. 기존 컨테이너 중지 (Graceful shutdown)
echo -e "${YELLOW}🛑 기존 인스턴스 종료 중 (Graceful shutdown, 30초 대기)...${NC}"
if docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" ps "$CURRENT_SERVICE" 2>/dev/null | grep -q "Up"; then
  # Graceful shutdown을 위해 stop 사용 (기본 타임아웃 10초, 여기서는 30초로 설정)
  docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" stop -t 30 "$CURRENT_SERVICE" || true
  
  # 잠시 대기 (진행 중인 요청 완료 대기)
  sleep 5
  
  # 기존 컨테이너 완전히 제거
  docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" rm -f "$CURRENT_SERVICE" || true
  
  echo -e "${GREEN}✅ 기존 인스턴스 종료 완료${NC}"
else
  echo -e "${YELLOW}⚠️  기존 인스턴스가 실행 중이 아닙니다.${NC}"
fi

# 8. 임시 컨테이너 정리 및 메인 서비스 시작
echo -e "${YELLOW}🔄 새 인스턴스를 메인 포트로 전환 중...${NC}"

# 임시 컨테이너 중지 및 제거
docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" -f "${PROJECT_DIR}/docker-compose.temp.yml" stop backend-temp || true
docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" -f "${PROJECT_DIR}/docker-compose.temp.yml" rm -f backend-temp || true
rm -f "${PROJECT_DIR}/docker-compose.temp.yml"

# 메인 서비스 시작 (환경 변수는 이미 export되어 있음)
# MySQL, Redis가 실행 중이 아닌 경우 자동으로 시작됨
echo -e "${YELLOW}🔄 메인 서비스 시작 중 (MySQL, Redis 확인)...${NC}"
# MySQL, Redis 컨테이너가 없거나 중지된 경우 시작
docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" up -d mysql redis || {
    echo -e "${YELLOW}⚠️  MySQL/Redis 시작 실패 (이미 실행 중이거나 오류)${NC}"
}
docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" up -d "$CURRENT_SERVICE"

# 9. 최종 헬스체크
echo -e "${YELLOW}🔍 최종 헬스체크 중...${NC}"
sleep 5
final_check_passed=false

for i in {1..5}; do
  if docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" exec -T "$CURRENT_SERVICE" wget --no-verbose --tries=1 --spider "http://localhost:8080/api/v1/actuator/health" 2>&1 > /dev/null; then
    echo -e "${GREEN}✅ 최종 헬스체크 성공!${NC}"
    final_check_passed=true
    break
  fi
  
  echo -e "${YELLOW}⏳ 최종 헬스체크 재시도 중... (${i}/5)${NC}"
  sleep 3
done

if [ "$final_check_passed" = false ]; then
  echo -e "${RED}❌ 최종 헬스체크 실패!${NC}"
  echo -e "${YELLOW}⚠️  컨테이너는 실행 중이지만 헬스체크가 실패했습니다. 로그를 확인하세요.${NC}"
  docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" logs --tail=50 "$CURRENT_SERVICE"
  exit 1
fi

# 10. 오래된 이미지 정리 (선택사항)
echo -e "${YELLOW}🧹 오래된 이미지 정리 중...${NC}"
docker image prune -f

echo -e "${GREEN}✅ 무중단 배포 완료!${NC}"
echo -e "${GREEN}📍 서비스 상태:${NC}"
docker-compose -f "$COMPOSE_FILE" -f "$COMPOSE_PROD_FILE" ps

