-- 목업 데이터 생성 스크립트
-- 로컬 개발 및 테스트용
-- 사용법: mysql -u root -p taba < src/main/resources/db/mock-data.sql

USE taba;

-- 기존 데이터 삭제 (선택사항, 주의해서 사용)
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE letter_recipients;
-- TRUNCATE TABLE letter_images;
-- TRUNCATE TABLE letter_reports;
-- TRUNCATE TABLE letters;
-- TRUNCATE TABLE friendships;
-- TRUNCATE TABLE invite_codes;
-- TRUNCATE TABLE notifications;
-- TRUNCATE TABLE password_reset_tokens;
-- TRUNCATE TABLE users;
-- SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 1. 사용자 데이터 (Users)
-- ============================================
-- 비밀번호: 모두 "password123" (BCrypt 해시)
-- BCrypt 해시: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

INSERT INTO users (id, email, password, username, nickname, avatar_url, status_message, language, push_notification_enabled, created_at, updated_at, deleted_at) VALUES
('11111111-1111-1111-1111-111111111111', 'alice@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'alice123', '앨리스', 'http://localhost:8080/api/v1/uploads/avatar1.jpg', '안녕하세요!', 'ko', TRUE, NOW(), NOW(), NULL),
('22222222-2222-2222-2222-222222222222', 'bob@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'bob456', '밥', 'http://localhost:8080/api/v1/uploads/avatar2.jpg', '반가워요', 'ko', TRUE, NOW(), NOW(), NULL),
('33333333-3333-3333-3333-333333333333', 'charlie@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'charlie789', '찰리', 'http://localhost:8080/api/v1/uploads/avatar3.jpg', '좋은 하루 되세요', 'ko', TRUE, NOW(), NOW(), NULL),
('44444444-4444-4444-4444-444444444444', 'diana@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'diana012', '다이애나', 'http://localhost:8080/api/v1/uploads/avatar4.jpg', '행복한 하루!', 'ko', TRUE, NOW(), NOW(), NULL),
('55555555-5555-5555-5555-555555555555', 'eve@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'eve345', '이브', 'http://localhost:8080/api/v1/uploads/avatar5.jpg', '즐거운 하루 되세요', 'ko', TRUE, NOW(), NOW(), NULL),
('66666666-6666-6666-6666-666666666666', 'frank@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'frank678', '프랭크', 'http://localhost:8080/api/v1/uploads/avatar6.jpg', '좋은 하루!', 'ko', TRUE, NOW(), NOW(), NULL),
('77777777-7777-7777-7777-777777777777', 'grace@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'grace901', '그레이스', 'http://localhost:8080/api/v1/uploads/avatar7.jpg', '행복한 하루 되세요', 'ko', TRUE, NOW(), NOW(), NULL),
('88888888-8888-8888-8888-888888888888', 'henry@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'henry234', '헨리', 'http://localhost:8080/api/v1/uploads/avatar8.jpg', '즐거운 하루!', 'ko', TRUE, NOW(), NOW(), NULL);

-- ============================================
-- 2. 친구 관계 (Friendships)
-- ============================================
-- 앨리스와 밥이 친구
INSERT INTO friendships (id, user_id, friend_id, bouquet_name, bloom_level, trust_score, created_at, updated_at, deleted_at) VALUES
('f1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', '밥의 꽃다발', 0.75, 50, NOW(), NOW(), NULL),
('f2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', '앨리스의 꽃다발', 0.75, 50, NOW(), NOW(), NULL);

-- 앨리스와 찰리가 친구
INSERT INTO friendships (id, user_id, friend_id, bouquet_name, bloom_level, trust_score, created_at, updated_at, deleted_at) VALUES
('f3333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', '찰리의 꽃다발', 0.50, 30, NOW(), NOW(), NULL),
('f4444444-4444-4444-4444-444444444444', '33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', '앨리스의 꽃다발', 0.50, 30, NOW(), NOW(), NULL);

-- 밥과 다이애나가 친구
INSERT INTO friendships (id, user_id, friend_id, bouquet_name, bloom_level, trust_score, created_at, updated_at, deleted_at) VALUES
('f5555555-5555-5555-5555-555555555555', '22222222-2222-2222-2222-222222222222', '44444444-4444-4444-4444-444444444444', '다이애나의 꽃다발', 0.90, 80, NOW(), NOW(), NULL),
('f6666666-6666-6666-6666-666666666666', '44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', '밥의 꽃다발', 0.90, 80, NOW(), NOW(), NULL);

-- ============================================
-- 3. 편지 데이터 (Letters)
-- ============================================

-- 공개 편지 1 (앨리스가 작성)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, flower_type, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', NULL, '안녕하세요!', '오늘 날씨가 정말 좋네요. 모두 행복한 하루 되세요!', '오늘 날씨가 정말 좋네요...', 'ROSE', 'PUBLIC', FALSE, 'pink', 'black', 'Arial', 14.0, NULL, NOW() - INTERVAL 2 DAY, 15, FALSE, NULL, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, NULL);

-- 공개 편지 2 (밥이 작성)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, flower_type, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', NULL, '좋은 아침입니다', '오늘도 화이팅! 모두 좋은 하루 되세요.', '오늘도 화이팅!...', 'SUNFLOWER', 'PUBLIC', FALSE, 'yellow', 'brown', 'Georgia', 16.0, NULL, NOW() - INTERVAL 1 DAY, 8, FALSE, NULL, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NULL);

-- 공개 편지 3 (찰리가 작성)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, flower_type, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l3333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', NULL, '행복한 하루', '모두 행복한 하루 되세요! 긍정적인 에너지가 가득하길 바랍니다.', '모두 행복한 하루 되세요!...', 'TULIP', 'PUBLIC', TRUE, 'purple', 'white', 'Comic Sans MS', 15.0, NULL, NOW() - INTERVAL 3 HOUR, 3, FALSE, NULL, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR, NULL);

-- 직접 전송 편지 1 (앨리스 -> 밥)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, flower_type, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l4444444-4444-4444-4444-444444444444', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', '밥에게', '밥아, 오늘 정말 고마웠어! 덕분에 좋은 하루였어.', '밥아, 오늘 정말 고마웠어!...', 'ROSE', 'DIRECT', FALSE, 'pink', 'black', 'Arial', 14.0, NULL, NOW() - INTERVAL 1 DAY, 0, FALSE, NULL, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NULL);

-- 직접 전송 편지 2 (밥 -> 앨리스)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, flower_type, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l5555555-5555-5555-5555-555555555555', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', '앨리스에게', '앨리스야, 고마워! 나도 좋은 하루였어.', '앨리스야, 고마워!...', 'TULIP', 'DIRECT', FALSE, 'blue', 'white', 'Verdana', 14.0, NULL, NOW() - INTERVAL 12 HOUR, 0, TRUE, NOW() - INTERVAL 11 HOUR, NOW() - INTERVAL 12 HOUR, NOW() - INTERVAL 11 HOUR, NULL);

-- 직접 전송 편지 3 (앨리스 -> 찰리)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, flower_type, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l6666666-6666-6666-6666-666666666666', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', '찰리에게', '찰리야, 오늘 만나서 반가웠어! 다음에 또 만나자.', '찰리야, 오늘 만나서 반가웠어!...', 'SAKURA', 'DIRECT', FALSE, 'pink', 'black', 'Arial', 14.0, NULL, NOW() - INTERVAL 6 HOUR, 0, FALSE, NULL, NOW() - INTERVAL 6 HOUR, NOW() - INTERVAL 6 HOUR, NULL);

-- 직접 전송 편지 4 (찰리 -> 앨리스, 미읽음)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, flower_type, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l7777777-7777-7777-7777-777777777777', '33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', '앨리스에게', '앨리스야, 나도 반가웠어! 다음에 또 만나자.', '앨리스야, 나도 반가웠어!...', 'DAISY', 'DIRECT', FALSE, 'white', 'black', 'Times New Roman', 14.0, NULL, NOW() - INTERVAL 2 HOUR, 0, FALSE, NULL, NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR, NULL);

-- 직접 전송 편지 5 (밥 -> 다이애나)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, flower_type, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l8888888-8888-8888-8888-888888888888', '22222222-2222-2222-2222-222222222222', '44444444-4444-4444-4444-444444444444', '다이애나에게', '다이애나야, 오늘 정말 고마웠어! 사랑해!', '다이애나야, 오늘 정말 고마웠어!...', 'ROSE', 'DIRECT', FALSE, 'red', 'white', 'Arial', 16.0, NULL, NOW() - INTERVAL 1 HOUR, 0, TRUE, NOW() - INTERVAL 30 MINUTE, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 30 MINUTE, NULL);

-- ============================================
-- 4. 편지 이미지 (Letter Images)
-- ============================================

INSERT INTO letter_images (id, letter_id, image_url, image_order, created_at, updated_at, deleted_at) VALUES
('i1111111-1111-1111-1111-111111111111', 'l1111111-1111-1111-1111-111111111111', 'http://localhost:8080/api/v1/uploads/letter1-image1.jpg', 0, NOW(), NOW(), NULL),
('i2222222-2222-2222-2222-222222222222', 'l2222222-2222-2222-2222-222222222222', 'http://localhost:8080/api/v1/uploads/letter2-image1.jpg', 0, NOW(), NOW(), NULL),
('i3333333-3333-3333-3333-333333333333', 'l2222222-2222-2222-2222-222222222222', 'http://localhost:8080/api/v1/uploads/letter2-image2.jpg', 1, NOW(), NOW(), NULL);

-- ============================================
-- 5. 편지 수신자 (Letter Recipients) - 공개 편지 읽은 사용자
-- ============================================

-- 밥이 앨리스의 공개 편지를 읽음
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('r1111111-1111-1111-1111-111111111111', 'l1111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', TRUE, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NULL);

-- 찰리가 앨리스의 공개 편지를 읽음
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('r2222222-2222-2222-2222-222222222222', 'l1111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', TRUE, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NULL);

-- 다이애나가 밥의 공개 편지를 읽음 (미읽음 상태)
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('r3333333-3333-3333-3333-333333333333', 'l2222222-2222-2222-2222-222222222222', '44444444-4444-4444-4444-444444444444', FALSE, NULL, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NULL);

-- ============================================
-- 6. 초대 코드 (Invite Codes)
-- ============================================

INSERT INTO invite_codes (id, user_id, code, expires_at, used_by_user_id, used_at, created_at, updated_at, deleted_at) VALUES
('c1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'alice123-123456', NOW() + INTERVAL 3 MINUTE, NULL, NULL, NOW(), NOW(), NULL),
('c2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'bob456-789012', NOW() + INTERVAL 3 MINUTE, NULL, NULL, NOW(), NOW(), NULL),
('c3333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 'charlie789-345678', NOW() + INTERVAL 3 MINUTE, NULL, NULL, NOW(), NOW(), NULL);

-- ============================================
-- 7. 알림 (Notifications)
-- ============================================

-- 앨리스에게 알림 (편지 받음)
INSERT INTO notifications (id, user_id, title, subtitle, category, related_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('n1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '새 편지 도착', '밥으로부터 편지가 도착했습니다.', 'LETTER', 'l5555555-5555-5555-5555-555555555555', TRUE, NOW() - INTERVAL 11 HOUR, NOW() - INTERVAL 12 HOUR, NOW() - INTERVAL 11 HOUR, NULL),
('n2222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', '새 편지 도착', '찰리로부터 편지가 도착했습니다.', 'LETTER', 'l7777777-7777-7777-7777-777777777777', FALSE, NULL, NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR, NULL);

-- 밥에게 알림 (편지 받음)
INSERT INTO notifications (id, user_id, title, subtitle, category, related_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('n3333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', '새 편지 도착', '앨리스로부터 편지가 도착했습니다.', 'LETTER', 'l4444444-4444-4444-4444-444444444444', TRUE, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NULL);

-- 다이애나에게 알림 (편지 받음)
INSERT INTO notifications (id, user_id, title, subtitle, category, related_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('n4444444-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444444', '새 편지 도착', '밥으로부터 편지가 도착했습니다.', 'LETTER', 'l8888888-8888-8888-8888-888888888888', TRUE, NOW() - INTERVAL 30 MINUTE, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 30 MINUTE, NULL);

-- ============================================
-- 완료 메시지
-- ============================================
SELECT 'Mock data inserted successfully!' AS message;
SELECT COUNT(*) AS user_count FROM users;
SELECT COUNT(*) AS friendship_count FROM friendships;
SELECT COUNT(*) AS letter_count FROM letters;
SELECT COUNT(*) AS notification_count FROM notifications;

