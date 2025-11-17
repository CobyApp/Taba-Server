-- 데이터베이스 초기화 스크립트
-- MySQL에서 직접 실행하거나 애플리케이션 시작 시 자동 실행

-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 사용자 생성 및 권한 부여 (선택사항)
-- CREATE USER IF NOT EXISTS 'taba_user'@'localhost' IDENTIFIED BY 'taba_password';
-- GRANT ALL PRIVILEGES ON taba.* TO 'taba_user'@'localhost';
-- FLUSH PRIVILEGES;

-- 데이터베이스 사용
USE taba;

-- 테이블은 JPA 엔티티를 기반으로 자동 생성됩니다.
-- 개발 환경: ddl-auto: update
-- 프로덕션 환경: ddl-auto: validate (스키마 검증만)

