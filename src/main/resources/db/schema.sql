-- Taba 데이터베이스 스키마 초기화 스크립트
-- MySQL 8.0 이상 버전 사용

-- 데이터베이스 생성 (필요시)
-- CREATE DATABASE IF NOT EXISTS taba CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE taba;

-- 주의: 이 스키마는 JPA 엔티티를 기반으로 작성되었습니다.
-- 개발 환경에서는 application-dev.yml의 ddl-auto: update를 사용하세요.
-- 프로덕션 환경에서는 Flyway나 Liquibase 같은 마이그레이션 도구 사용을 권장합니다.

-- ============================================
-- 주요 테이블 구조
-- ============================================

-- users: 사용자 정보
-- - id (VARCHAR(36), PK)
-- - email (VARCHAR(255), UNIQUE)
-- - username (VARCHAR(50), UNIQUE)
-- - password (VARCHAR(255))
-- - nickname (VARCHAR(50))
-- - avatar_url (VARCHAR(500))
-- - status_message (VARCHAR(200))
-- - push_notification_enabled (BOOLEAN)
-- - language (VARCHAR(10))
-- - created_at, updated_at, deleted_at (TIMESTAMP)

-- letters: 편지 정보
-- - id (VARCHAR(36), PK)
-- - sender_id (VARCHAR(36), FK -> users.id)
-- - recipient_id (VARCHAR(36), FK -> users.id, nullable)
-- - title (VARCHAR(200))
-- - content (TEXT)
-- - preview (VARCHAR(500))
-- - flower_type (VARCHAR(20), ENUM)
-- - visibility (VARCHAR(20), ENUM: PUBLIC, FRIENDS, DIRECT, PRIVATE)
-- - is_anonymous (BOOLEAN)
-- - template_background (VARCHAR(50))
-- - template_text_color (VARCHAR(50))
-- - template_font_family (VARCHAR(100))
-- - template_font_size (DECIMAL(5,2))
-- - scheduled_at (TIMESTAMP, nullable)
-- - sent_at (TIMESTAMP, nullable)
-- - views (INTEGER, default 0)
-- - is_read (BOOLEAN, default false) -- 읽음 상태 (recipient 기준)
-- - read_at (TIMESTAMP, nullable) -- 읽은 시간
-- - created_at, updated_at, deleted_at (TIMESTAMP)

-- friendships: 친구 관계
-- - id (VARCHAR(36), PK)
-- - user_id (VARCHAR(36), FK -> users.id)
-- - friend_id (VARCHAR(36), FK -> users.id)
-- - bouquet_name (VARCHAR(100))
-- - bloom_level (DECIMAL(3,2), default 0.0)
-- - trust_score (INTEGER, default 0)
-- - created_at, updated_at, deleted_at (TIMESTAMP)
-- - UNIQUE(user_id, friend_id)

-- letter_images: 편지 첨부 이미지
-- - id (VARCHAR(36), PK)
-- - letter_id (VARCHAR(36), FK -> letters.id)
-- - image_url (VARCHAR(500))
-- - image_order (INTEGER)

-- letter_reports: 편지 신고
-- - id (VARCHAR(36), PK)
-- - letter_id (VARCHAR(36), FK -> letters.id)
-- - reporter_id (VARCHAR(36), FK -> users.id)
-- - reason (VARCHAR(500))
-- - created_at (TIMESTAMP)

-- invite_codes: 초대 코드
-- - id (VARCHAR(36), PK)
-- - user_id (VARCHAR(36), FK -> users.id)
-- - code (VARCHAR(100), UNIQUE)
-- - expires_at (TIMESTAMP)
-- - used_by_user_id (VARCHAR(36), FK -> users.id, nullable)
-- - used_at (TIMESTAMP, nullable)
-- - created_at (TIMESTAMP)

-- notifications: 알림
-- - id (VARCHAR(36), PK)
-- - user_id (VARCHAR(36), FK -> users.id)
-- - title (VARCHAR(200))
-- - subtitle (VARCHAR(500))
-- - category (VARCHAR(50), ENUM)
-- - related_id (VARCHAR(36))
-- - is_read (BOOLEAN, default false)
-- - read_at (TIMESTAMP, nullable)
-- - created_at (TIMESTAMP)

-- password_reset_tokens: 비밀번호 재설정 토큰
-- - id (VARCHAR(36), PK)
-- - user_id (VARCHAR(36), FK -> users.id)
-- - token (VARCHAR(255), UNIQUE)
-- - expires_at (TIMESTAMP)
-- - used (BOOLEAN, default false)
-- - created_at (TIMESTAMP)

-- ============================================
-- 참고사항
-- ============================================
-- 1. 친구 간 편지 조회는 letters 테이블을 직접 조회합니다.
--    - sender_id와 recipient_id를 이용하여 양방향 조회
--    - visibility = 'DIRECT'인 편지만 조회
-- 2. 읽음 상태는 letters.is_read 필드로 관리됩니다.
--    - recipient 기준으로 읽음 상태 관리
