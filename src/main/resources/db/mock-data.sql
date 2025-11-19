-- 목업 데이터 생성 스크립트
-- 로컬 개발 및 테스트용
--
-- ⚠️ 사용 방법:
-- 개발 환경: mysql -h 서버IP -P 3307 -u [DB_USERNAME_DEV] -p [DB_NAME_DEV] < mock-data.sql
-- 프로덕션 환경: mysql -h 서버IP -P 3306 -u [DB_USERNAME_PROD] -p [DB_NAME_PROD] < mock-data.sql
-- 로컬: mysql -u root -p [데이터베이스명] < src/main/resources/db/mock-data.sql
--
-- 환경별 데이터베이스 이름:
-- - 개발 환경: ${DB_NAME_DEV} (예: taba_dev)
-- - 프로덕션 환경: ${DB_NAME_PROD} (예: taba_prod)
--
-- ⚠️ 주의: 프로덕션 환경에서는 이 스크립트를 실행하지 마세요!
-- 개발/테스트 환경에서만 사용하세요.
--
-- 실행 예시:
--   개발: mysql -h 서버IP -P 3307 -u taba_user_dev -p taba_dev < mock-data.sql
--   로컬: mysql -u root -p taba < mock-data.sql

-- 기존 데이터 삭제 (선택사항, 주의해서 사용)
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE letter_recipients;
-- TRUNCATE TABLE letter_images;
-- TRUNCATE TABLE letter_reports;
-- TRUNCATE TABLE letters;
-- TRUNCATE TABLE friendships;
-- TRUNCATE TABLE notifications;
-- TRUNCATE TABLE password_reset_tokens;
-- TRUNCATE TABLE users;
-- SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 1. 사용자 데이터 (Users)
-- ============================================
-- 비밀번호: 모두 "password123" (BCrypt 해시)
-- BCrypt 해시: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

INSERT INTO users (id, email, password, nickname, avatar_url, language, push_notification_enabled, fcm_token, created_at, updated_at, deleted_at) VALUES
('11111111-1111-1111-1111-111111111111', 'alice@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '앨리스', 'https://example.com/avatars/alice.jpg', 'ko', TRUE, NULL, NOW() - INTERVAL 30 DAY, NOW() - INTERVAL 1 DAY, NULL),
('22222222-2222-2222-2222-222222222222', 'bob@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '밥', 'https://example.com/avatars/bob.jpg', 'ko', TRUE, NULL, NOW() - INTERVAL 25 DAY, NOW() - INTERVAL 1 DAY, NULL),
('33333333-3333-3333-3333-333333333333', 'charlie@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '찰리', 'https://example.com/avatars/charlie.jpg', 'ko', TRUE, NULL, NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 1 DAY, NULL),
('44444444-4444-4444-4444-444444444444', 'diana@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '다이애나', 'https://example.com/avatars/diana.jpg', 'ko', TRUE, NULL, NOW() - INTERVAL 15 DAY, NOW() - INTERVAL 1 DAY, NULL),
('55555555-5555-5555-5555-555555555555', 'eve@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '이브', 'https://example.com/avatars/eve.jpg', 'ko', TRUE, NULL, NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 1 DAY, NULL),
('66666666-6666-6666-6666-666666666666', 'frank@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '프랭크', 'https://example.com/avatars/frank.jpg', 'ko', TRUE, NULL, NOW() - INTERVAL 8 DAY, NOW() - INTERVAL 1 DAY, NULL),
('77777777-7777-7777-7777-777777777777', 'grace@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '그레이스', 'https://example.com/avatars/grace.jpg', 'ko', TRUE, NULL, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 1 DAY, NULL),
('88888888-8888-8888-8888-888888888888', 'henry@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '헨리', 'https://example.com/avatars/henry.jpg', 'ko', TRUE, NULL, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 1 DAY, NULL);

-- ============================================
-- 2. 친구 관계 (Friendships)
-- ============================================
-- 양방향 친구 관계 (양쪽 모두 INSERT 필요)

-- 앨리스와 밥이 친구
INSERT INTO friendships (id, user_id, friend_id, created_at, updated_at, deleted_at) VALUES
('f1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 20 DAY, NULL),
('f2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 20 DAY, NULL);

-- 앨리스와 찰리가 친구
INSERT INTO friendships (id, user_id, friend_id, created_at, updated_at, deleted_at) VALUES
('f3333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', NOW() - INTERVAL 15 DAY, NOW() - INTERVAL 15 DAY, NULL),
('f4444444-4444-4444-4444-444444444444', '33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', NOW() - INTERVAL 15 DAY, NOW() - INTERVAL 15 DAY, NULL);

-- 밥과 다이애나가 친구
INSERT INTO friendships (id, user_id, friend_id, created_at, updated_at, deleted_at) VALUES
('f5555555-5555-5555-5555-555555555555', '22222222-2222-2222-2222-222222222222', '44444444-4444-4444-4444-444444444444', NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 10 DAY, NULL),
('f6666666-6666-6666-6666-666666666666', '44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 10 DAY, NULL);

-- 찰리와 이브가 친구
INSERT INTO friendships (id, user_id, friend_id, created_at, updated_at, deleted_at) VALUES
('f7777777-7777-7777-7777-777777777777', '33333333-3333-3333-3333-333333333333', '55555555-5555-5555-5555-555555555555', NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 7 DAY, NULL),
('f8888888-8888-8888-8888-888888888888', '55555555-5555-5555-5555-555555555555', '33333333-3333-3333-3333-333333333333', NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 7 DAY, NULL);

-- 다이애나와 프랭크가 친구
INSERT INTO friendships (id, user_id, friend_id, created_at, updated_at, deleted_at) VALUES
('f9999999-9999-9999-9999-999999999999', '44444444-4444-4444-4444-444444444444', '66666666-6666-6666-6666-666666666666', NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY, NULL),
('faaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '66666666-6666-6666-6666-666666666666', '44444444-4444-4444-4444-444444444444', NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY, NULL);

-- ============================================
-- 3. 편지 데이터 (Letters)
-- ============================================

-- 공개 편지 1 (앨리스가 작성, 2일 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', NULL, '안녕하세요!', '오늘 날씨가 정말 좋네요. 모두 행복한 하루 되세요! 봄이 오는 것 같아서 기분이 좋아요. 🌸', '오늘 날씨가 정말 좋네요. 모두 행복한 하루 되세요! 봄이 오는 것 같아서 기분이 좋아요. 🌸', 'PUBLIC', FALSE, 'pink', 'black', 'Arial', 14.0, NULL, NOW() - INTERVAL 2 DAY, 15, FALSE, NULL, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, NULL);

-- 공개 편지 2 (밥이 작성, 1일 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', NULL, '좋은 아침입니다', '오늘도 화이팅! 모두 좋은 하루 되세요. 새로운 시작이 기대됩니다. 💪', '오늘도 화이팅! 모두 좋은 하루 되세요. 새로운 시작이 기대됩니다. 💪', 'PUBLIC', FALSE, 'yellow', 'brown', 'Georgia', 16.0, NULL, NOW() - INTERVAL 1 DAY, 8, FALSE, NULL, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NULL);

-- 공개 편지 3 (찰리가 작성, 익명, 3시간 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l3333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', NULL, '행복한 하루', '모두 행복한 하루 되세요! 긍정적인 에너지가 가득하길 바랍니다. 작은 것에도 감사하는 마음을 가지세요. ✨', '모두 행복한 하루 되세요! 긍정적인 에너지가 가득하길 바랍니다. 작은 것에도 감사하는 마음을 가지세요. ✨', 'PUBLIC', TRUE, 'purple', 'white', 'Comic Sans MS', 15.0, NULL, NOW() - INTERVAL 3 HOUR, 3, FALSE, NULL, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR, NULL);

-- 공개 편지 4 (다이애나가 작성, 5시간 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l4444444-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444444', NULL, '따뜻한 하루', '오늘도 따뜻한 하루 되세요. 주변 사람들에게 따뜻한 말 한마디 전해보세요. 사랑과 평화가 함께하길. 💕', '오늘도 따뜻한 하루 되세요. 주변 사람들에게 따뜻한 말 한마디 전해보세요. 사랑과 평화가 함께하길. 💕', 'PUBLIC', FALSE, 'blue', 'white', 'Verdana', 14.0, NULL, NOW() - INTERVAL 5 HOUR, 5, FALSE, NULL, NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR, NULL);

-- 직접 전송 편지 1 (앨리스 -> 밥, 1일 전 발송, 미읽음)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l5555555-5555-5555-5555-555555555555', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', '밥에게', '밥아, 오늘 정말 고마웠어! 덕분에 좋은 하루였어. 다음에 또 만나자. 항상 고마워! 😊', '밥아, 오늘 정말 고마웠어! 덕분에 좋은 하루였어. 다음에 또 만나자. 항상 고마워! 😊', 'DIRECT', FALSE, 'pink', 'black', 'Arial', 14.0, NULL, NOW() - INTERVAL 1 DAY, 0, FALSE, NULL, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NULL);

-- 직접 전송 편지 2 (밥 -> 앨리스, 12시간 전 발송, 읽음)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l6666666-6666-6666-6666-666666666666', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', '앨리스에게', '앨리스야, 고마워! 나도 좋은 하루였어. 네가 있어서 더 즐거웠어. 다음에 또 만나자! 🌟', '앨리스야, 고마워! 나도 좋은 하루였어. 네가 있어서 더 즐거웠어. 다음에 또 만나자! 🌟', 'DIRECT', FALSE, 'blue', 'white', 'Verdana', 14.0, NULL, NOW() - INTERVAL 12 HOUR, 0, TRUE, NOW() - INTERVAL 11 HOUR, NOW() - INTERVAL 12 HOUR, NOW() - INTERVAL 11 HOUR, NULL);

-- 직접 전송 편지 3 (앨리스 -> 찰리, 6시간 전 발송, 미읽음)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l7777777-7777-7777-7777-777777777777', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', '찰리에게', '찰리야, 오늘 만나서 반가웠어! 다음에 또 만나자. 재밌는 이야기 더 나누고 싶어. 🎉', '찰리야, 오늘 만나서 반가웠어! 다음에 또 만나자. 재밌는 이야기 더 나누고 싶어. 🎉', 'DIRECT', FALSE, 'pink', 'black', 'Arial', 14.0, NULL, NOW() - INTERVAL 6 HOUR, 0, FALSE, NULL, NOW() - INTERVAL 6 HOUR, NOW() - INTERVAL 6 HOUR, NULL);

-- 직접 전송 편지 4 (찰리 -> 앨리스, 2시간 전 발송, 미읽음)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l8888888-8888-8888-8888-888888888888', '33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', '앨리스에게', '앨리스야, 나도 반가웠어! 다음에 또 만나자. 오늘 정말 즐거웠어. 고마워! 😄', '앨리스야, 나도 반가웠어! 다음에 또 만나자. 오늘 정말 즐거웠어. 고마워! 😄', 'DIRECT', FALSE, 'white', 'black', 'Times New Roman', 14.0, NULL, NOW() - INTERVAL 2 HOUR, 0, FALSE, NULL, NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR, NULL);

-- 직접 전송 편지 5 (밥 -> 다이애나, 1시간 전 발송, 읽음)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('l9999999-9999-9999-9999-999999999999', '22222222-2222-2222-2222-222222222222', '44444444-4444-4444-4444-444444444444', '다이애나에게', '다이애나야, 오늘 정말 고마웠어! 사랑해! 항상 네가 있어서 행복해. 💖', '다이애나야, 오늘 정말 고마웠어! 사랑해! 항상 네가 있어서 행복해. 💖', 'DIRECT', FALSE, 'red', 'white', 'Arial', 16.0, NULL, NOW() - INTERVAL 1 HOUR, 0, TRUE, NOW() - INTERVAL 30 MINUTE, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 30 MINUTE, NULL);

-- 직접 전송 편지 6 (다이애나 -> 밥, 30분 전 발송, 읽음)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('laaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', '밥에게', '밥아, 나도 고마워! 너도 항상 고마워. 우리 함께라서 더 행복해. ❤️', '밥아, 나도 고마워! 너도 항상 고마워. 우리 함께라서 더 행복해. ❤️', 'DIRECT', FALSE, 'purple', 'white', 'Georgia', 15.0, NULL, NOW() - INTERVAL 30 MINUTE, 0, TRUE, NOW() - INTERVAL 20 MINUTE, NOW() - INTERVAL 30 MINUTE, NOW() - INTERVAL 20 MINUTE, NULL);

-- 직접 전송 편지 7 (이브 -> 찰리, 4시간 전 발송, 읽음)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('lbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '55555555-5555-5555-5555-555555555555', '33333333-3333-3333-3333-333333333333', '찰리에게', '찰리야, 오늘 만나서 정말 좋았어! 다음에 또 만나자. 재밌는 시간이었어. 🎊', '찰리야, 오늘 만나서 정말 좋았어! 다음에 또 만나자. 재밌는 시간이었어. 🎊', 'DIRECT', FALSE, 'green', 'black', 'Courier New', 14.0, NULL, NOW() - INTERVAL 4 HOUR, 0, TRUE, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 3 HOUR, NULL);

-- 친구 전용 편지 1 (프랭크 -> 다이애나, 친구만 볼 수 있음, 3일 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('lccccccc-cccc-cccc-cccc-cccccccccccc', '66666666-6666-6666-6666-666666666666', NULL, '친구들에게', '친구들에게 전하는 편지입니다. 모두 건강하고 행복하길 바라요. 함께라서 행복해요! 🌈', '친구들에게 전하는 편지입니다. 모두 건강하고 행복하길 바라요. 함께라서 행복해요! 🌈', 'FRIENDS', FALSE, 'orange', 'black', 'Arial', 14.0, NULL, NOW() - INTERVAL 3 DAY, 2, FALSE, NULL, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY, NULL);

-- ============================================
-- 4. 편지 이미지 (Letter Images)
-- ============================================

-- 공개 편지 1에 이미지 1개
INSERT INTO letter_images (id, letter_id, image_url, image_order, created_at, updated_at, deleted_at) VALUES
('i1111111-1111-1111-1111-111111111111', 'l1111111-1111-1111-1111-111111111111', 'https://example.com/images/letter1-image1.jpg', 0, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, NULL);

-- 공개 편지 2에 이미지 2개
INSERT INTO letter_images (id, letter_id, image_url, image_order, created_at, updated_at, deleted_at) VALUES
('i2222222-2222-2222-2222-222222222222', 'l2222222-2222-2222-2222-222222222222', 'https://example.com/images/letter2-image1.jpg', 0, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NULL),
('i3333333-3333-3333-3333-333333333333', 'l2222222-2222-2222-2222-222222222222', 'https://example.com/images/letter2-image2.jpg', 1, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NULL);

-- 직접 전송 편지 5에 이미지 1개
INSERT INTO letter_images (id, letter_id, image_url, image_order, created_at, updated_at, deleted_at) VALUES
('i4444444-4444-4444-4444-444444444444', 'l9999999-9999-9999-9999-999999999999', 'https://example.com/images/letter5-image1.jpg', 0, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR, NULL);

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

-- 이브가 찰리의 공개 편지를 읽음
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('r4444444-4444-4444-4444-444444444444', 'l3333333-3333-3333-3333-333333333333', '55555555-5555-5555-5555-555555555555', TRUE, NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR, NULL);

-- 프랭크가 다이애나의 공개 편지를 읽음
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('r5555555-5555-5555-5555-555555555555', 'l4444444-4444-4444-4444-444444444444', '66666666-6666-6666-6666-666666666666', TRUE, NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 4 HOUR, NULL);

-- ============================================
-- 6. 알림 (Notifications)
-- ============================================

-- 앨리스에게 알림 (편지 받음)
INSERT INTO notifications (id, user_id, title, subtitle, category, related_id, is_read, read_at, created_at) VALUES
('n1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '새 편지 도착', '밥으로부터 편지가 도착했습니다.', 'LETTER', 'l6666666-6666-6666-6666-666666666666', TRUE, NOW() - INTERVAL 11 HOUR, NOW() - INTERVAL 12 HOUR),
('n2222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', '새 편지 도착', '찰리로부터 편지가 도착했습니다.', 'LETTER', 'l8888888-8888-8888-8888-888888888888', FALSE, NULL, NOW() - INTERVAL 2 HOUR);

-- 밥에게 알림 (편지 받음)
INSERT INTO notifications (id, user_id, title, subtitle, category, related_id, is_read, read_at, created_at) VALUES
('n3333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', '새 편지 도착', '앨리스로부터 편지가 도착했습니다.', 'LETTER', 'l5555555-5555-5555-5555-555555555555', TRUE, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY),
('n4444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', '새 편지 도착', '다이애나로부터 편지가 도착했습니다.', 'LETTER', 'laaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', TRUE, NOW() - INTERVAL 20 MINUTE, NOW() - INTERVAL 30 MINUTE);

-- 다이애나에게 알림 (편지 받음)
INSERT INTO notifications (id, user_id, title, subtitle, category, related_id, is_read, read_at, created_at) VALUES
('n5555555-5555-5555-5555-555555555555', '44444444-4444-4444-4444-444444444444', '새 편지 도착', '밥으로부터 편지가 도착했습니다.', 'LETTER', 'l9999999-9999-9999-9999-999999999999', TRUE, NOW() - INTERVAL 30 MINUTE, NOW() - INTERVAL 1 HOUR);

-- 찰리에게 알림 (편지 받음)
INSERT INTO notifications (id, user_id, title, subtitle, category, related_id, is_read, read_at, created_at) VALUES
('n6666666-6666-6666-6666-666666666666', '33333333-3333-3333-3333-333333333333', '새 편지 도착', '앨리스로부터 편지가 도착했습니다.', 'LETTER', 'l7777777-7777-7777-7777-777777777777', FALSE, NULL, NOW() - INTERVAL 6 HOUR),
('n7777777-7777-7777-7777-777777777777', '33333333-3333-3333-3333-333333333333', '새 편지 도착', '이브로부터 편지가 도착했습니다.', 'LETTER', 'lbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', TRUE, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 4 HOUR);

-- 이브에게 알림 (편지 받음)
INSERT INTO notifications (id, user_id, title, subtitle, category, related_id, is_read, read_at, created_at) VALUES
('n8888888-8888-8888-8888-888888888888', '55555555-5555-5555-5555-555555555555', '새 편지 도착', '찰리로부터 편지가 도착했습니다.', 'LETTER', 'lccccccc-cccc-cccc-cccc-cccccccccccc', FALSE, NULL, NOW() - INTERVAL 3 DAY);

-- ============================================
-- 완료 메시지
-- ============================================
SELECT 'Mock data inserted successfully!' AS message;
SELECT COUNT(*) AS user_count FROM users;
SELECT COUNT(*) AS friendship_count FROM friendships;
SELECT COUNT(*) AS letter_count FROM letters;
SELECT COUNT(*) AS letter_image_count FROM letter_images;
SELECT COUNT(*) AS letter_recipient_count FROM letter_recipients;
SELECT COUNT(*) AS notification_count FROM notifications;
