# 무중단 배포 가이드

## 📋 개요

이 프로젝트는 **Blue-Green 배포** 방식을 사용하여 무중단 배포를 구현합니다.

### 작동 방식

1. **새 이미지 빌드**: 최신 코드로 새 Docker 이미지 빌드
2. **임시 포트에서 시작**: 새 컨테이너를 임시 포트(8081)에서 시작
3. **헬스체크 확인**: 새 인스턴스가 정상 작동하는지 확인
4. **기존 인스턴스 종료**: Graceful shutdown으로 기존 인스턴스 종료
5. **메인 포트로 전환**: 새 인스턴스를 메인 포트(8080)로 재시작
6. **최종 헬스체크**: 배포 완료 확인

## 🚀 사용 방법

### GitHub Actions 자동 배포 (권장)

`main` 브랜치에 푸시하면 자동으로 무중단 배포가 실행됩니다.

```bash
git push origin main
```

### 수동 배포

서버에서 직접 배포 스크립트를 실행할 수 있습니다.

```bash
ssh user@cobyserver.iptime.org
cd ~/taba_backend
./zero-downtime-deploy.sh ~/taba_backend
```

## ⚙️ 설정

### Graceful Shutdown

Spring Boot의 Graceful shutdown이 활성화되어 있어, 종료 시 진행 중인 요청을 완료한 후 종료합니다.

**설정 위치**: `src/main/resources/application.yml`, `application-prod.yml`

```yaml
server:
  shutdown: graceful  # Graceful shutdown 활성화

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s  # 종료 타임아웃 (진행 중인 요청 완료 대기 시간)
```

### 헬스체크

헬스체크는 `/api/v1/actuator/health` 엔드포인트를 사용합니다.

**스크립트 설정** (`scripts/zero-downtime-deploy.sh`):

```bash
HEALTH_CHECK_TIMEOUT=180  # 헬스체크 타임아웃 (초)
HEALTH_CHECK_INTERVAL=5   # 헬스체크 간격 (초)
```

## 🔍 배포 프로세스 상세

### 1. 새 이미지 빌드

```bash
docker-compose build --no-cache backend
```

### 2. 임시 포트에서 새 인스턴스 시작

새 컨테이너를 포트 8081에서 시작합니다.

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml -f docker-compose.temp.yml up -d backend-temp
```

### 3. 헬스체크

새 인스턴스가 정상 작동하는지 확인:

- **초기 헬스체크**: 최대 180초 대기
- **추가 헬스체크**: 연속 3번 성공 확인

### 4. 기존 인스턴스 종료

Graceful shutdown으로 기존 인스턴스 종료:

```bash
docker-compose stop -t 30 backend  # 30초 대기
docker-compose rm -f backend       # 컨테이너 제거
```

### 5. 메인 포트로 전환

임시 컨테이너를 정리하고 메인 서비스를 시작:

```bash
docker-compose -f docker-compose.temp.yml down backend-temp
docker-compose up -d backend
```

### 6. 최종 헬스체크

메인 포트(8080)에서 새 인스턴스가 정상 작동하는지 확인합니다.

## 📊 배포 상태 확인

### 배포 로그 확인

```bash
# GitHub Actions 로그
# GitHub 저장소 > Actions 탭에서 확인

# 서버 로그
ssh user@cobyserver.iptime.org
cd ~/taba_backend
docker-compose logs -f backend
```

### 서비스 상태 확인

```bash
docker-compose ps
```

### 헬스체크 직접 확인

```bash
curl http://localhost:8080/api/v1/actuator/health
```

예상 응답:

```json
{
  "success": true,
  "data": {
    "status": "UP"
  },
  "message": "Service is running"
}
```

## ⚠️ 주의사항

### 포트 충돌

임시 포트(8081)가 이미 사용 중인 경우 배포가 실패할 수 있습니다. 

**해결 방법**:
```bash
# 포트 사용 확인
sudo lsof -i :8081

# 사용 중인 프로세스 종료
sudo kill -9 <PID>
```

### 헬스체크 실패

헬스체크가 실패하면 배포가 중단되고 롤백됩니다.

**확인 사항**:
1. 데이터베이스 연결 확인
2. Redis 연결 확인 (선택사항)
3. 환경 변수 확인
4. 애플리케이션 로그 확인

### 메모리 부족

배포 중 두 개의 컨테이너가 동시에 실행될 수 있으므로 메모리가 부족할 수 있습니다.

**해결 방법**:
- 서버 메모리 확인: `free -h`
- `docker-compose.prod.yml`의 리소스 제한 조정

## 🔄 롤백 방법

배포가 실패하면 자동으로 롤백됩니다. 수동 롤백이 필요한 경우:

### 방법 1: 이전 이미지 사용

```bash
cd ~/taba_backend

# 기존 컨테이너 중지
docker-compose down backend

# 이전 이미지로 컨테이너 시작
docker-compose up -d backend
```

### 방법 2: 특정 이미지 태그로 롤백

```bash
# 사용 가능한 이미지 확인
docker images | grep taba_backend

# 특정 이미지로 컨테이너 시작
docker run -d --name taba-backend \
  -p 8080:8080 \
  --network taba_backend_taba-network \
  <IMAGE_ID>
```

## 📝 트러블슈팅

### 배포 스크립트 권한 오류

```bash
chmod +x scripts/zero-downtime-deploy.sh
```

### Docker Compose 버전 확인

```bash
docker-compose --version
# 권장: 1.29.0 이상
```

### 임시 파일 정리

```bash
cd ~/taba_backend
rm -f docker-compose.temp.yml
rm -f docker-compose.deploy.yml
```

### 디스크 공간 확인

```bash
docker system df
docker system prune -a  # 오래된 이미지/컨테이너 정리
```

## 🎯 배포 성공 확인

배포가 성공적으로 완료되면:

1. ✅ **GitHub Actions**: 모든 단계가 성공적으로 완료
2. ✅ **헬스체크**: `/api/v1/actuator/health`가 200 OK 반환
3. ✅ **API 응답**: 실제 API 엔드포인트가 정상 작동
4. ✅ **Swagger UI**: API 문서가 정상 표시

## 📚 참고 자료

- [Spring Boot Graceful Shutdown](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.graceful-shutdown)
- [Docker Compose Health Checks](https://docs.docker.com/compose/compose-file/compose-file-v3/#healthcheck)
- [Blue-Green Deployment](https://martinfowler.com/bliki/BlueGreenDeployment.html)

