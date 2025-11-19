-- 데이터베이스 초기화 스크립트
-- MySQL 컨테이너 시작 시 자동 실행 (/docker-entrypoint-initdb.d/)
-- 또는 MySQL에서 직접 실행 가능
--
-- ⚠️ 주의사항:
-- - 이 스크립트는 MySQL 컨테이너가 처음 생성될 때만 실행됩니다 (볼륨이 없을 때)
-- - docker-compose의 MYSQL_DATABASE 환경 변수로 데이터베이스가 자동 생성됩니다
-- - 실제 배포에서는 deploy.yml의 ensure_mysql_user 함수가 데이터베이스와 사용자를 생성합니다
--
-- 환경별 데이터베이스 이름:
-- - 개발 환경: ${DB_NAME_DEV} (예: taba_dev)
-- - 프로덕션 환경: ${DB_NAME_PROD} (예: taba_prod)
--
-- 데이터베이스 생성 (MYSQL_DATABASE 환경 변수가 이미 설정되어 있으면 자동 생성됨)
-- 이 부분은 docker-compose의 MYSQL_DATABASE 환경 변수로 처리되므로 주석 처리
-- CREATE DATABASE IF NOT EXISTS taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 사용자 생성 및 권한 부여는 deploy.yml의 ensure_mysql_user 함수에서 처리됩니다
-- CREATE USER IF NOT EXISTS 'taba_user'@'%' IDENTIFIED BY 'taba_password';
-- GRANT ALL PRIVILEGES ON taba.* TO 'taba_user'@'%';
-- FLUSH PRIVILEGES;

-- 테이블은 JPA 엔티티를 기반으로 자동 생성됩니다.
-- 개발 환경: ddl-auto: update (자동 스키마 생성/업데이트)
-- 프로덕션 환경: ddl-auto: validate (스키마 검증만)

