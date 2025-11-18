# 무중단 배포 가이드

## 개요

Blue-Green 배포 방식을 사용하여 서비스 중단 없이 배포합니다.

**작동 방식**:
1. 새 이미지 빌드
2. 임시 포트(8081)에서 새 인스턴스 시작
3. 헬스체크 확인 (최대 180초, 연속 3번 성공)
4. 기존 인스턴스 Graceful shutdown (30초 대기)
5. 새 인스턴스를 메인 포트(8080)로 전환
6. 최종 헬스체크 확인

## 사용 방법

### GitHub Actions 자동 배포 (권장)
```bash
git push origin main  # 자동으로 무중단 배포 실행
```

### 수동 배포
```bash
ssh user@server
cd ~/taba_backend
./zero-downtime-deploy.sh ~/taba_backend
```

## 설정

### Graceful Shutdown
Spring Boot의 Graceful shutdown이 활성화되어 있어, 종료 시 진행 중인 요청을 완료한 후 종료합니다 (최대 30초 대기).

```yaml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

### 헬스체크
헬스체크는 `/api/v1/actuator/health` 엔드포인트를 사용합니다.

## 배포 상태 확인

### 배포 로그 확인
```bash
# GitHub Actions 로그에서 확인
# 또는 서버에서
docker-compose logs -f backend
```

### 서비스 상태 확인
```bash
docker-compose ps
```

---

## 문제 해결

### 배포 실패 시
- 배포 스크립트가 자동으로 롤백합니다
- 기존 인스턴스는 그대로 유지됩니다
- 로그 확인: `docker-compose logs backend`

### 포트 충돌
```bash
sudo netstat -tlnp | grep 8081
```
