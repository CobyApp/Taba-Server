-- ============================================
-- Taba 목업 데이터 생성 스크립트
-- ============================================
-- 로컬 개발 및 테스트 환경용
--
-- ⚠️ 중요: 프로덕션 환경에서는 절대 실행하지 마세요!
--          개발/테스트 환경에서만 사용하세요.
--
-- ============================================
-- 사용 방법
-- ============================================
--
-- 개발 환경:
--   mysql -h 서버IP -P 3307 -u [DB_USERNAME_DEV] -p [DB_NAME_DEV] < mock-data.sql
--   예: mysql -h 192.168.0.3 -P 3307 -u taba_user_dev -p taba_dev < mock-data.sql
--
-- 프로덕션 환경 (절대 실행 금지):
--   mysql -h 서버IP -P 3306 -u [DB_USERNAME_PROD] -p [DB_NAME_PROD] < mock-data.sql
--
-- 로컬 환경:
--   mysql -u root -p [데이터베이스명] < src/main/resources/db/mock-data.sql
--   예: mysql -u root -p taba < src/main/resources/db/mock-data.sql
--
-- ============================================
-- 환경별 설정
-- ============================================
-- - 개발 환경: ${DB_NAME_DEV} (예: taba_dev), 포트: 3307
-- - 프로덕션 환경: ${DB_NAME_PROD} (예: taba_prod), 포트: 3306
--
-- ============================================
-- 생성되는 데이터
-- ============================================
-- 1. 사용자: 8명 (앨리스, 밥, 찰리, 다이애나, 이브, 프랭크, 그레이스, 헨리)
-- 2. 친구 관계: 5쌍 (양방향)
-- 3. 편지: 77개 공개 편지 + 8개 직접 전송 편지 + 1개 친구 전용 편지
-- 4. 편지 이미지: 여러 편지에 첨부
-- 5. 편지 수신자: 공개 편지 읽은 사용자 기록
-- 6. 알림: 편지 수신 알림
--
-- 비밀번호: 모든 사용자의 비밀번호는 "password123" (BCrypt 해시)

-- ============================================
-- 기존 데이터 삭제 (외래키 제약조건 고려한 순서)
-- ============================================
SET FOREIGN_KEY_CHECKS = 0;

-- 외래키를 참조하는 테이블부터 삭제
TRUNCATE TABLE letter_recipients;
TRUNCATE TABLE letter_images;
TRUNCATE TABLE letter_reports;
TRUNCATE TABLE notifications;
TRUNCATE TABLE password_reset_tokens;
TRUNCATE TABLE invite_codes;

-- 참조되는 테이블 삭제
TRUNCATE TABLE letters;
TRUNCATE TABLE friendships;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;

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
('faaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '66666666-6666-6666-6666-666666666666', '44444444-4444-4444-4444-444444444444', NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY, NULL);

-- ============================================
-- 3. 편지 데이터 (Letters)
-- ============================================

-- 공개 편지 1 (앨리스가 작성, 2일 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('l1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', NULL, '안녕하세요!', '오늘 날씨가 정말 좋네요. 모두 행복한 하루 되세요! 봄이 오는 것 같아서 기분이 좋아요. 🌸', '오늘 날씨가 정말 좋네요. 모두 행복한 하루 되세요! 봄이 오는 것 같아서 기분이 좋아요. 🌸', 'PUBLIC', FALSE, '#1D1433', '#FFFFFF', 'Jua', 16.0, NULL, NOW() - INTERVAL 2 DAY, 15, FALSE, NULL, 'ko', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, NULL);

-- 공개 편지 2 (밥이 작성, 1일 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('l2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', NULL, '좋은 아침입니다', '오늘도 화이팅! 모두 좋은 하루 되세요. 새로운 시작이 기대됩니다. 💪', '오늘도 화이팅! 모두 좋은 하루 되세요. 새로운 시작이 기대됩니다. 💪', 'PUBLIC', FALSE, '#2A1F00', '#FFFF00', 'Sunflower', 15.0, NULL, NOW() - INTERVAL 1 DAY, 8, FALSE, NULL, 'ko', NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NULL);

-- 공개 편지 3 (찰리가 작성, 3시간 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('l3333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', NULL, '행복한 하루', '모두 행복한 하루 되세요! 긍정적인 에너지가 가득하길 바랍니다. 작은 것에도 감사하는 마음을 가지세요. ✨', '모두 행복한 하루 되세요! 긍정적인 에너지가 가득하길 바랍니다. 작은 것에도 감사하는 마음을 가지세요. ✨', 'PUBLIC', FALSE, '#1A0016', '#FF00FF', 'Yeon Sung', 16.0, NULL, NOW() - INTERVAL 3 HOUR, 3, FALSE, NULL, 'ko', NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR, NULL);

-- 공개 편지 4 (다이애나가 작성, 5시간 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('l4444444-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444444', NULL, '따뜻한 하루', '오늘도 따뜻한 하루 되세요. 주변 사람들에게 따뜻한 말 한마디 전해보세요. 사랑과 평화가 함께하길. 💕', '오늘도 따뜻한 하루 되세요. 주변 사람들에게 따뜻한 말 한마디 전해보세요. 사랑과 평화가 함께하길. 💕', 'PUBLIC', FALSE, '#001133', '#FFFFFF', 'Poor Story', 15.0, NULL, NOW() - INTERVAL 5 HOUR, 5, FALSE, NULL, 'ko', NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR, NULL);

-- 공개 편지 5 (이브가 작성, 7일 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('lddddddd-dddd-dddd-dddd-dddddddddddd', '55555555-5555-5555-5555-555555555555', NULL, '새로운 시작', '새로운 한 주가 시작되었어요. 모두에게 좋은 일만 가득하길 바라요. 힘든 일이 있어도 포기하지 말고 함께 이겨내요! 🌱', '새로운 한 주가 시작되었어요. 모두에게 좋은 일만 가득하길 바라요. 힘든 일이 있어도 포기하지 말고 함께 이겨내요! 🌱', 'PUBLIC', FALSE, '#001100', '#00FF00', 'Dongle', 16.0, NULL, NOW() - INTERVAL 7 DAY, 22, FALSE, NULL, 'ko', NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 7 DAY, NULL);

-- 공개 편지 6 (이브가 작성, 4일 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('leeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '55555555-5555-5555-5555-555555555555', NULL, '감사한 마음', '오늘 하루도 감사한 마음으로 시작해요. 작은 것에도 감사할 수 있는 마음을 가지면 더 행복해질 수 있어요. 모두 행복하세요! 🙏', '오늘 하루도 감사한 마음으로 시작해요. 작은 것에도 감사할 수 있는 마음을 가지면 더 행복해질 수 있어요. 모두 행복하세요! 🙏', 'PUBLIC', FALSE, '#1E1A14', '#FFFFFF', 'Gamja Flower', 15.0, NULL, NOW() - INTERVAL 4 DAY, 18, FALSE, NULL, 'ko', NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY, NULL);

-- 공개 편지 7 (프랭크가 작성, 6일 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('lfffffff-ffff-ffff-ffff-ffffffffffff', '66666666-6666-6666-6666-666666666666', NULL, '힘내세요!', '모두 힘내세요! 어려운 일이 있어도 포기하지 마세요. 당신은 충분히 강하고 멋져요. 오늘도 화이팅! 💪', '모두 힘내세요! 어려운 일이 있어도 포기하지 마세요. 당신은 충분히 강하고 멋져요. 오늘도 화이팅! 💪', 'PUBLIC', FALSE, '#210014', '#FFFFFF', 'Hi Melody', 17.0, NULL, NOW() - INTERVAL 6 DAY, 31, FALSE, NULL, 'ko', NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 6 DAY, NULL);

-- 공개 편지 8 (프랭크가 작성, 10시간 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('lggggggg-gggg-gggg-gggg-gggggggggggg', '66666666-6666-6666-6666-666666666666', NULL, '밤하늘을 보며', '밤하늘을 보니 마음이 편안해져요. 별들이 반짝이는 모습이 정말 아름다워요. 오늘 하루 고생 많으셨어요. 잘 쉬세요. 🌙', '밤하늘을 보니 마음이 편안해져요. 별들이 반짝이는 모습이 정말 아름다워요. 오늘 하루 고생 많으셨어요. 잘 쉬세요. 🌙', 'PUBLIC', FALSE, '#0A0024', '#FFFFFF', 'Nanum Pen Script', 15.0, NULL, NOW() - INTERVAL 10 HOUR, 12, FALSE, NULL, 'ko', NOW() - INTERVAL 10 HOUR, NOW() - INTERVAL 10 HOUR, NULL);

-- 공개 편지 9 (그레이스가 작성, 8일 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('lhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', '77777777-7777-7777-7777-777777777777', NULL, '봄날의 기운', '봄이 오고 있어요. 따뜻한 바람이 불어오고 꽃들이 피기 시작했어요. 새로운 계절, 새로운 시작을 함께해요. 🌺', '봄이 오고 있어요. 따뜻한 바람이 불어오고 꽃들이 피기 시작했어요. 새로운 계절, 새로운 시작을 함께해요. 🌺', 'PUBLIC', FALSE, '#061A17', '#FFFFFF', 'Jua', 16.0, NULL, NOW() - INTERVAL 8 DAY, 27, FALSE, NULL, 'ko', NOW() - INTERVAL 8 DAY, NOW() - INTERVAL 8 DAY, NULL);

-- 공개 편지 10 (그레이스가 작성, 2시간 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('liiiiiii-iiii-iiii-iiii-iiiiiiiiiiii', '77777777-7777-7777-7777-777777777777', NULL, '오늘의 일기', '오늘 하루도 무사히 보내고 있어요. 작은 행복들을 모아서 큰 행복을 만들어가고 있어요. 모두도 행복한 하루 보내세요! 📝', '오늘 하루도 무사히 보내고 있어요. 작은 행복들을 모아서 큰 행복을 만들어가고 있어요. 모두도 행복한 하루 보내세요! 📝', 'PUBLIC', FALSE, '#000000', '#00CC00', 'Sunflower', 15.0, NULL, NOW() - INTERVAL 2 HOUR, 4, FALSE, NULL, 'ko', NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR, NULL);

-- 공개 편지 11 (헨리가 작성, 5일 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('ljjjjjjj-jjjj-jjjj-jjjj-jjjjjjjjjjjj', '88888888-8888-8888-8888-888888888888', NULL, '좋은 하루 되세요', '모두 좋은 하루 되세요! 오늘도 웃음 가득한 하루가 되길 바라요. 긍정적인 에너지가 가득하길! 😊', '모두 좋은 하루 되세요! 오늘도 웃음 가득한 하루가 되길 바라요. 긍정적인 에너지가 가득하길! 😊', 'PUBLIC', FALSE, '#1D1433', '#FFFFFF', 'Yeon Sung', 15.0, NULL, NOW() - INTERVAL 5 DAY, 19, FALSE, NULL, 'ko', NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY, NULL);

-- 공개 편지 12 (헨리가 작성, 1시간 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('lkkkkkkk-kkkk-kkkk-kkkk-kkkkkkkkkkkk', '88888888-8888-8888-8888-888888888888', NULL, '작은 위로', '힘든 하루를 보내고 계신가요? 괜찮아요. 당신은 충분히 잘하고 있어요. 작은 위로의 말을 전하고 싶어요. 🌟', '힘든 하루를 보내고 계신가요? 괜찮아요. 당신은 충분히 잘하고 있어요. 작은 위로의 말을 전하고 싶어요. 🌟', 'PUBLIC', FALSE, '#1A0016', '#FF00FF', 'Poor Story', 15.0, NULL, NOW() - INTERVAL 1 HOUR, 2, FALSE, NULL, 'ko', NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR, NULL);

-- 공개 편지 13 (앨리스가 작성, 3일 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('llllllll-llll-llll-llll-llllllllllll', '11111111-1111-1111-1111-111111111111', NULL, '주말의 아침', '주말 아침이에요. 느긋하게 커피 한 잔 마시며 하루를 시작해요. 모두 편안한 주말 보내세요! ☕', '주말 아침이에요. 느긋하게 커피 한 잔 마시며 하루를 시작해요. 모두 편안한 주말 보내세요! ☕', 'PUBLIC', FALSE, '#1E1A14', '#FFFFFF', 'Dongle', 16.0, NULL, NOW() - INTERVAL 3 DAY, 24, FALSE, NULL, 'ko', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY, NULL);

-- 공개 편지 14 (밥이 작성, 9시간 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('lmmmmmmm-mmmm-mmmm-mmmm-mmmmmmmmmmmm', '22222222-2222-2222-2222-222222222222', NULL, '저녁 노을', '저녁 노을이 정말 아름다워요. 하루를 마무리하며 내일을 기대해봐요. 모두 좋은 저녁 되세요! 🌅', '저녁 노을이 정말 아름다워요. 하루를 마무리하며 내일을 기대해봐요. 모두 좋은 저녁 되세요! 🌅', 'PUBLIC', FALSE, '#210014', '#FFFFFF', 'Gamja Flower', 15.0, NULL, NOW() - INTERVAL 9 HOUR, 14, FALSE, NULL, 'ko', NOW() - INTERVAL 9 HOUR, NOW() - INTERVAL 9 HOUR, NULL);

-- 공개 편지 15 (찰리가 작성, 12일 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('lnnnnnnn-nnnn-nnnn-nnnn-nnnnnnnnnnnn', '33333333-3333-3333-3333-333333333333', NULL, '새로운 도전', '새로운 도전을 시작해요. 두려워하지 말고 한 걸음씩 나아가요. 당신은 할 수 있어요! 화이팅! 🚀', '새로운 도전을 시작해요. 두려워하지 말고 한 걸음씩 나아가요. 당신은 할 수 있어요! 화이팅! 🚀', 'PUBLIC', FALSE, '#001133', '#FFFFFF', 'Hi Melody', 17.0, NULL, NOW() - INTERVAL 12 DAY, 35, FALSE, NULL, 'ko', NOW() - INTERVAL 12 DAY, NOW() - INTERVAL 12 DAY, NULL);

-- 공개 편지 16 (다이애나가 작성, 8시간 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('looooooo-oooo-oooo-oooo-oooooooooooo', '44444444-4444-4444-4444-444444444444', NULL, '따뜻한 마음', '따뜻한 마음으로 하루를 시작해요. 작은 친절이 큰 기쁨을 만들어요. 모두에게 따뜻한 하루가 되길 바라요. 💝', '따뜻한 마음으로 하루를 시작해요. 작은 친절이 큰 기쁨을 만들어요. 모두에게 따뜻한 하루가 되길 바라요. 💝', 'PUBLIC', FALSE, '#0A0024', '#FFFFFF', 'Nanum Pen Script', 15.0, NULL, NOW() - INTERVAL 8 HOUR, 11, FALSE, NULL, 'ko', NOW() - INTERVAL 8 HOUR, NOW() - INTERVAL 8 HOUR, NULL);

-- 공개 편지 17 (이브가 작성, 15분 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('lppppppp-pppp-pppp-pppp-pppppppppppp', '55555555-5555-5555-5555-555555555555', NULL, '지금 이 순간', '지금 이 순간을 소중하게 여겨요. 과거는 지나갔고 미래는 아직 오지 않았어요. 지금 이 순간이 가장 소중해요. ⏰', '지금 이 순간을 소중하게 여겨요. 과거는 지나갔고 미래는 아직 오지 않았어요. 지금 이 순간이 가장 소중해요. ⏰', 'PUBLIC', FALSE, '#061A17', '#FFFFFF', 'Jua', 16.0, NULL, NOW() - INTERVAL 15 MINUTE, 1, FALSE, NULL, 'ko', NOW() - INTERVAL 15 MINUTE, NOW() - INTERVAL 15 MINUTE, NULL);

-- 공개 편지 18 (프랭크가 작성, 11일 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('lqqqqqqq-qqqq-qqqq-qqqq-qqqqqqqqqqqq', '66666666-6666-6666-6666-666666666666', NULL, '함께하는 힘', '혼자서는 어려운 일도 함께하면 쉬워져요. 서로를 응원하고 지지하는 마음이 중요해요. 함께 힘내요! 🤝', '혼자서는 어려운 일도 함께하면 쉬워져요. 서로를 응원하고 지지하는 마음이 중요해요. 함께 힘내요! 🤝', 'PUBLIC', FALSE, '#001100', '#00FF00', 'Sunflower', 16.0, NULL, NOW() - INTERVAL 11 DAY, 28, FALSE, NULL, 'ko', NOW() - INTERVAL 11 DAY, NOW() - INTERVAL 11 DAY, NULL);

-- 공개 편지 19 (그레이스가 작성, 6시간 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('lrrrrrrr-rrrr-rrrr-rrrr-rrrrrrrrrrrr', '77777777-7777-7777-7777-777777777777', NULL, '작은 기쁨', '작은 기쁨을 찾아봐요. 아침 커피 한 잔, 따뜻한 햇살, 친구의 미소. 작은 것들이 모여 큰 행복이 되요. 😄', '작은 기쁨을 찾아봐요. 아침 커피 한 잔, 따뜻한 햇살, 친구의 미소. 작은 것들이 모여 큰 행복이 되요. 😄', 'PUBLIC', FALSE, '#2A1F00', '#FFFF00', 'Yeon Sung', 15.0, NULL, NOW() - INTERVAL 6 HOUR, 9, FALSE, NULL, 'ko', NOW() - INTERVAL 6 HOUR, NOW() - INTERVAL 6 HOUR, NULL);

-- 공개 편지 20 (헨리가 작성, 13일 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('lsssssss-ssss-ssss-ssss-ssssssssssss', '88888888-8888-8888-8888-888888888888', NULL, '꿈을 향해', '꿈을 향해 한 걸음씩 나아가요. 멀리 보이지 않아도 괜찮아요. 오늘 한 걸음이 내일의 큰 발걸음이 될 거예요. 🌠', '꿈을 향해 한 걸음씩 나아가요. 멀리 보이지 않아도 괜찮아요. 오늘 한 걸음이 내일의 큰 발걸음이 될 거예요. 🌠', 'PUBLIC', FALSE, '#1D1433', '#FFFFFF', 'Poor Story', 16.0, NULL, NOW() - INTERVAL 13 DAY, 42, FALSE, NULL, 'ko', NOW() - INTERVAL 13 DAY, NOW() - INTERVAL 13 DAY, NULL);

-- 공개 편지 21-77 (다양한 언어와 템플릿으로 추가)
-- 공개 편지 21 (한국어, 영어 폰트)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('lt000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', NULL, 'Good Morning!', 'Good morning everyone! Have a wonderful day filled with joy and happiness. Let''s make today amazing! 🌅', 'Good morning everyone! Have a wonderful day filled with joy and happiness. Let''s make today amazing! 🌅', 'PUBLIC', FALSE, '#0A0024', '#FFFFFF', 'Indie Flower', 16.0, NULL, NOW() - INTERVAL 14 DAY, 38, FALSE, NULL, 'en', NOW() - INTERVAL 14 DAY, NOW() - INTERVAL 14 DAY, NULL),
('lt000000-0000-0000-0000-000000000002', '22222222-2222-2222-2222-222222222222', NULL, 'Hello World', 'Hello from the other side! Hope you are doing well. Sending positive vibes your way! ✨', 'Hello from the other side! Hope you are doing well. Sending positive vibes your way! ✨', 'PUBLIC', FALSE, '#1E1A14', '#FFFFFF', 'Kalam', 15.0, NULL, NOW() - INTERVAL 16 DAY, 45, FALSE, NULL, 'en', NOW() - INTERVAL 16 DAY, NOW() - INTERVAL 16 DAY, NULL),
('lt000000-0000-0000-0000-000000000003', '33333333-3333-3333-3333-333333333333', NULL, 'Keep Going', 'Keep going! You are stronger than you think. Every step forward counts. You got this! 💪', 'Keep going! You are stronger than you think. Every step forward counts. You got this! 💪', 'PUBLIC', FALSE, '#061A17', '#FFFFFF', 'Patrick Hand', 16.0, NULL, NOW() - INTERVAL 18 DAY, 52, FALSE, NULL, 'en', NOW() - INTERVAL 18 DAY, NOW() - INTERVAL 18 DAY, NULL),
('lt000000-0000-0000-0000-000000000004', '44444444-4444-4444-4444-444444444444', NULL, 'Dream Big', 'Dream big and work hard. Your dreams are valid and achievable. Believe in yourself! 🌟', 'Dream big and work hard. Your dreams are valid and achievable. Believe in yourself! 🌟', 'PUBLIC', FALSE, '#1D1433', '#FFFFFF', 'Shadows Into Light', 15.0, NULL, NOW() - INTERVAL 20 DAY, 48, FALSE, NULL, 'en', NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 20 DAY, NULL),
('lt000000-0000-0000-0000-000000000005', '55555555-5555-5555-5555-555555555555', NULL, 'Stay Positive', 'Stay positive! Life is beautiful when you look at it with the right perspective. Smile more! 😊', 'Stay positive! Life is beautiful when you look at it with the right perspective. Smile more! 😊', 'PUBLIC', FALSE, '#001133', '#FFFFFF', 'Comic Neue', 16.0, NULL, NOW() - INTERVAL 22 DAY, 41, FALSE, NULL, 'en', NOW() - INTERVAL 22 DAY, NOW() - INTERVAL 22 DAY, NULL),
('lt000000-0000-0000-0000-000000000006', '66666666-6666-6666-6666-666666666666', NULL, 'Be Kind', 'Be kind to yourself and others. Small acts of kindness can change the world. Spread love! 💕', 'Be kind to yourself and others. Small acts of kindness can change the world. Spread love! 💕', 'PUBLIC', FALSE, '#210014', '#FFFFFF', 'Caveat', 17.0, NULL, NOW() - INTERVAL 24 DAY, 56, FALSE, NULL, 'en', NOW() - INTERVAL 24 DAY, NOW() - INTERVAL 24 DAY, NULL),
('lt000000-0000-0000-0000-000000000007', '77777777-7777-7777-7777-777777777777', NULL, 'Never Give Up', 'Never give up on your dreams. The journey might be tough, but the destination is worth it! 🚀', 'Never give up on your dreams. The journey might be tough, but the destination is worth it! 🚀', 'PUBLIC', FALSE, '#001100', '#00FF00', 'Dancing Script', 16.0, NULL, NOW() - INTERVAL 26 DAY, 63, FALSE, NULL, 'en', NOW() - INTERVAL 26 DAY, NOW() - INTERVAL 26 DAY, NULL),
('lt000000-0000-0000-0000-000000000008', '88888888-8888-8888-8888-888888888888', NULL, 'You Are Amazing', 'You are amazing just the way you are! Don''t let anyone tell you otherwise. Shine bright! ✨', 'You are amazing just the way you are! Don''t let anyone tell you otherwise. Shine bright! ✨', 'PUBLIC', FALSE, '#000000', '#00CC00', 'Permanent Marker', 15.0, NULL, NOW() - INTERVAL 28 DAY, 59, FALSE, NULL, 'en', NOW() - INTERVAL 28 DAY, NOW() - INTERVAL 28 DAY, NULL),
('lt000000-0000-0000-0000-000000000009', '11111111-1111-1111-1111-111111111111', NULL, 'おはようございます', 'おはようございます！今日も素晴らしい一日になりますように。笑顔で過ごしましょう！🌅', 'おはようございます！今日も素晴らしい一日になりますように。笑顔で過ごしましょう！🌅', 'PUBLIC', FALSE, '#1A0016', '#FF00FF', 'Yomogi', 16.0, NULL, NOW() - INTERVAL 30 DAY, 44, FALSE, NULL, 'ja', NOW() - INTERVAL 30 DAY, NOW() - INTERVAL 30 DAY, NULL),
('lt000000-0000-0000-0000-00000000000a', '22222222-2222-2222-2222-222222222222', NULL, '頑張って', '頑張って！あなたは思っているより強いです。一歩ずつ前進しましょう。応援しています！💪', '頑張って！あなたは思っているより強いです。一歩ずつ前進しましょう。応援しています！💪', 'PUBLIC', FALSE, '#2A1F00', '#FFFF00', 'Kosugi Maru', 15.0, NULL, NOW() - INTERVAL 32 DAY, 51, FALSE, NULL, 'ja', NOW() - INTERVAL 32 DAY, NOW() - INTERVAL 32 DAY, NULL),
('lt000000-0000-0000-0000-00000000000b', '33333333-3333-3333-3333-333333333333', NULL, 'ありがとう', 'ありがとうございます。あなたの優しさに感謝しています。素晴らしい一日を！🙏', 'ありがとうございます。あなたの優しさに感謝しています。素晴らしい一日を！🙏', 'PUBLIC', FALSE, '#0A0024', '#FFFFFF', 'M PLUS Rounded 1c', 16.0, NULL, NOW() - INTERVAL 34 DAY, 47, FALSE, NULL, 'ja', NOW() - INTERVAL 34 DAY, NOW() - INTERVAL 34 DAY, NULL),
('lt000000-0000-0000-0000-00000000000c', '44444444-4444-4444-4444-444444444444', NULL, '素敵な一日', '素敵な一日をお過ごしください。小さな幸せを見つけて、心温まる時間を過ごしましょう。💝', '素敵な一日をお過ごしください。小さな幸せを見つけて、心温まる時間を過ごしましょう。💝', 'PUBLIC', FALSE, '#1E1A14', '#FFFFFF', 'Comic Neue', 15.0, NULL, NOW() - INTERVAL 36 DAY, 53, FALSE, NULL, 'ja', NOW() - INTERVAL 36 DAY, NOW() - INTERVAL 36 DAY, NULL),
('lt000000-0000-0000-0000-00000000000d', '55555555-5555-5555-5555-555555555555', NULL, '夢を追いかけて', '夢を追いかけてください。遠くに見えなくても大丈夫です。今日の一歩が明日の大きな一歩になります。🌠', '夢を追いかけてください。遠くに見えなくても大丈夫です。今日の一歩が明日の大きな一歩になります。🌠', 'PUBLIC', FALSE, '#061A17', '#FFFFFF', 'Shippori Mincho', 16.0, NULL, NOW() - INTERVAL 38 DAY, 49, FALSE, NULL, 'ja', NOW() - INTERVAL 38 DAY, NOW() - INTERVAL 38 DAY, NULL),
('lt000000-0000-0000-0000-00000000000e', '66666666-6666-6666-6666-666666666666', NULL, '一緒に頑張ろう', '一緒に頑張りましょう！一人では難しいことも、一緒なら簡単になります。お互いを応援し合いましょう！🤝', '一緒に頑張りましょう！一人では難しいことも、一緒なら簡単になります。お互いを応援し合いましょう！🤝', 'PUBLIC', FALSE, '#1D1433', '#FFFFFF', 'Noto Sans JP', 15.0, NULL, NOW() - INTERVAL 40 DAY, 55, FALSE, NULL, 'ja', NOW() - INTERVAL 40 DAY, NOW() - INTERVAL 40 DAY, NULL),
('lt000000-0000-0000-0000-00000000000f', '77777777-7777-7777-7777-777777777777', NULL, '小さな喜び', '小さな喜びを見つけてみましょう。朝のコーヒー一杯、温かい日差し、友達の笑顔。小さなことが大きな幸せになります。😄', '小さな喜びを見つけてみましょう。朝のコーヒー一杯、温かい日差し、友達の笑顔。小さなことが大きな幸せになります。😄', 'PUBLIC', FALSE, '#001133', '#FFFFFF', 'Yomogi', 16.0, NULL, NOW() - INTERVAL 42 DAY, 58, FALSE, NULL, 'ja', NOW() - INTERVAL 42 DAY, NOW() - INTERVAL 42 DAY, NULL),
('lt000000-0000-0000-0000-000000000010', '88888888-8888-8888-8888-888888888888', NULL, '今日も一日', '今日も一日お疲れ様でした。ゆっくり休んで、明日も元気に過ごしましょう。良い夜を！🌙', '今日も一日お疲れ様でした。ゆっくり休んで、明日も元気に過ごしましょう。良い夜を！🌙', 'PUBLIC', FALSE, '#210014', '#FFFFFF', 'Kosugi Maru', 15.0, NULL, NOW() - INTERVAL 44 DAY, 61, FALSE, NULL, 'ja', NOW() - INTERVAL 44 DAY, NOW() - INTERVAL 44 DAY, NULL),
('lt000000-0000-0000-0000-000000000011', '11111111-1111-1111-1111-111111111111', NULL, '새로운 하루', '새로운 하루가 시작되었어요. 오늘도 멋진 하루 보내세요. 작은 행복을 찾아보세요. 🌸', '새로운 하루가 시작되었어요. 오늘도 멋진 하루 보내세요. 작은 행복을 찾아보세요. 🌸', 'PUBLIC', FALSE, '#001100', '#00FF00', 'Jua', 16.0, NULL, NOW() - INTERVAL 46 DAY, 64, FALSE, NULL, 'ko', NOW() - INTERVAL 46 DAY, NOW() - INTERVAL 46 DAY, NULL),
('lt000000-0000-0000-0000-000000000012', '22222222-2222-2222-2222-222222222222', NULL, 'Thank You', 'Thank you for being you! Your kindness makes the world a better place. Keep shining! 🌟', 'Thank you for being you! Your kindness makes the world a better place. Keep shining! 🌟', 'PUBLIC', FALSE, '#000000', '#00CC00', 'Indie Flower', 15.0, NULL, NOW() - INTERVAL 48 DAY, 67, FALSE, NULL, 'en', NOW() - INTERVAL 48 DAY, NOW() - INTERVAL 48 DAY, NULL),
('lt000000-0000-0000-0000-000000000013', '33333333-3333-3333-3333-333333333333', NULL, 'Make It Happen', 'Make it happen! Your future is created by what you do today. Take action and make your dreams come true! 🎯', 'Make it happen! Your future is created by what you do today. Take action and make your dreams come true! 🎯', 'PUBLIC', FALSE, '#1A0016', '#FF00FF', 'Kalam', 16.0, NULL, NOW() - INTERVAL 50 DAY, 70, FALSE, NULL, 'en', NOW() - INTERVAL 50 DAY, NOW() - INTERVAL 50 DAY, NULL),
('lt000000-0000-0000-0000-000000000014', '44444444-4444-4444-4444-444444444444', NULL, 'Believe in Yourself', 'Believe in yourself! You have the power to achieve anything you set your mind to. Trust the process! 💫', 'Believe in yourself! You have the power to achieve anything you set your mind to. Trust the process! 💫', 'PUBLIC', FALSE, '#2A1F00', '#FFFF00', 'Patrick Hand', 15.0, NULL, NOW() - INTERVAL 52 DAY, 73, FALSE, NULL, 'en', NOW() - INTERVAL 52 DAY, NOW() - INTERVAL 52 DAY, NULL),
('lt000000-0000-0000-0000-000000000015', '55555555-5555-5555-5555-555555555555', NULL, 'Spread Love', 'Spread love wherever you go. Let no one ever come to you without leaving happier. Share your light! 💖', 'Spread love wherever you go. Let no one ever come to you without leaving happier. Share your light! 💖', 'PUBLIC', FALSE, '#0A0024', '#FFFFFF', 'Shadows Into Light', 16.0, NULL, NOW() - INTERVAL 54 DAY, 76, FALSE, NULL, 'en', NOW() - INTERVAL 54 DAY, NOW() - INTERVAL 54 DAY, NULL),
('lt000000-0000-0000-0000-000000000016', '66666666-6666-6666-6666-666666666666', NULL, 'Stay Strong', 'Stay strong! Challenges are what make life interesting. Overcoming them is what makes life meaningful. 💪', 'Stay strong! Challenges are what make life interesting. Overcoming them is what makes life meaningful. 💪', 'PUBLIC', FALSE, '#1E1A14', '#FFFFFF', 'Comic Neue', 17.0, NULL, NOW() - INTERVAL 56 DAY, 79, FALSE, NULL, 'en', NOW() - INTERVAL 56 DAY, NOW() - INTERVAL 56 DAY, NULL),
('lt000000-0000-0000-0000-000000000017', '77777777-7777-7777-7777-777777777777', NULL, 'Enjoy Today', 'Enjoy today! Yesterday is history, tomorrow is a mystery, but today is a gift. That''s why it''s called present! 🎁', 'Enjoy today! Yesterday is history, tomorrow is a mystery, but today is a gift. That''s why it''s called present! 🎁', 'PUBLIC', FALSE, '#061A17', '#FFFFFF', 'Caveat', 16.0, NULL, NOW() - INTERVAL 58 DAY, 82, FALSE, NULL, 'en', NOW() - INTERVAL 58 DAY, NOW() - INTERVAL 58 DAY, NULL),
('lt000000-0000-0000-0000-000000000018', '88888888-8888-8888-8888-888888888888', NULL, 'Be Yourself', 'Be yourself! Everyone else is already taken. Your uniqueness is your superpower. Embrace it! 🦸', 'Be yourself! Everyone else is already taken. Your uniqueness is your superpower. Embrace it! 🦸', 'PUBLIC', FALSE, '#1D1433', '#FFFFFF', 'Dancing Script', 15.0, NULL, NOW() - INTERVAL 60 DAY, 85, FALSE, NULL, 'en', NOW() - INTERVAL 60 DAY, NOW() - INTERVAL 60 DAY, NULL),
('lt000000-0000-0000-0000-000000000019', '11111111-1111-1111-1111-111111111111', NULL, 'Follow Your Heart', 'Follow your heart, but take your brain with you. Listen to your intuition, but verify with logic. Balance is key! ⚖️', 'Follow your heart, but take your brain with you. Listen to your intuition, but verify with logic. Balance is key! ⚖️', 'PUBLIC', FALSE, '#001133', '#FFFFFF', 'Permanent Marker', 16.0, NULL, NOW() - INTERVAL 62 DAY, 88, FALSE, NULL, 'en', NOW() - INTERVAL 62 DAY, NOW() - INTERVAL 62 DAY, NULL),
('lt000000-0000-0000-0000-00000000001a', '22222222-2222-2222-2222-222222222222', NULL, 'Find Your Passion', 'Find your passion and pursue it relentlessly. When you love what you do, success follows naturally. Follow your dreams! 🌈', 'Find your passion and pursue it relentlessly. When you love what you do, success follows naturally. Follow your dreams! 🌈', 'PUBLIC', FALSE, '#210014', '#FFFFFF', 'Indie Flower', 15.0, NULL, NOW() - INTERVAL 64 DAY, 91, FALSE, NULL, 'en', NOW() - INTERVAL 64 DAY, NOW() - INTERVAL 64 DAY, NULL),
('lt000000-0000-0000-0000-00000000001b', '33333333-3333-3333-3333-333333333333', NULL, 'Take a Break', 'Take a break when you need it. Rest is not giving up, it''s preparing for the next challenge. Recharge and come back stronger! 🔋', 'Take a break when you need it. Rest is not giving up, it''s preparing for the next challenge. Recharge and come back stronger! 🔋', 'PUBLIC', FALSE, '#001100', '#00FF00', 'Kalam', 16.0, NULL, NOW() - INTERVAL 66 DAY, 94, FALSE, NULL, 'en', NOW() - INTERVAL 66 DAY, NOW() - INTERVAL 66 DAY, NULL),
('lt000000-0000-0000-0000-00000000001c', '44444444-4444-4444-4444-444444444444', NULL, 'Celebrate Small Wins', 'Celebrate small wins! Every step forward, no matter how small, is progress. Acknowledge your achievements! 🎉', 'Celebrate small wins! Every step forward, no matter how small, is progress. Acknowledge your achievements! 🎉', 'PUBLIC', FALSE, '#000000', '#00CC00', 'Patrick Hand', 17.0, NULL, NOW() - INTERVAL 68 DAY, 97, FALSE, NULL, 'en', NOW() - INTERVAL 68 DAY, NOW() - INTERVAL 68 DAY, NULL),
('lt000000-0000-0000-0000-00000000001d', '55555555-5555-5555-5555-555555555555', NULL, 'Learn Every Day', 'Learn something new every day. Knowledge is power, and curiosity is the key to unlocking endless possibilities. Stay curious! 📚', 'Learn something new every day. Knowledge is power, and curiosity is the key to unlocking endless possibilities. Stay curious! 📚', 'PUBLIC', FALSE, '#1A0016', '#FF00FF', 'Shadows Into Light', 16.0, NULL, NOW() - INTERVAL 70 DAY, 100, FALSE, NULL, 'en', NOW() - INTERVAL 70 DAY, NOW() - INTERVAL 70 DAY, NULL),
('lt000000-0000-0000-0000-00000000001e', '66666666-6666-6666-6666-666666666666', NULL, 'Connect with Others', 'Connect with others! Building relationships and sharing experiences makes life richer and more meaningful. Reach out! 🤗', 'Connect with others! Building relationships and sharing experiences makes life richer and more meaningful. Reach out! 🤗', 'PUBLIC', FALSE, '#2A1F00', '#FFFF00', 'Comic Neue', 15.0, NULL, NOW() - INTERVAL 72 DAY, 103, FALSE, NULL, 'en', NOW() - INTERVAL 72 DAY, NOW() - INTERVAL 72 DAY, NULL),
('lt000000-0000-0000-0000-00000000001f', '77777777-7777-7777-7777-777777777777', NULL, 'Practice Gratitude', 'Practice gratitude daily. When you focus on what you have, you attract more of what you want. Count your blessings! 🙏', 'Practice gratitude daily. When you focus on what you have, you attract more of what you want. Count your blessings! 🙏', 'PUBLIC', FALSE, '#0A0024', '#FFFFFF', 'Caveat', 16.0, NULL, NOW() - INTERVAL 74 DAY, 106, FALSE, NULL, 'en', NOW() - INTERVAL 74 DAY, NOW() - INTERVAL 74 DAY, NULL),
('lt000000-0000-0000-0000-000000000020', '88888888-8888-8888-8888-888888888888', NULL, 'Embrace Change', 'Embrace change! It''s the only constant in life. Adaptability is a superpower that helps you thrive in any situation. Grow! 🌱', 'Embrace change! It''s the only constant in life. Adaptability is a superpower that helps you thrive in any situation. Grow! 🌱', 'PUBLIC', FALSE, '#1E1A14', '#FFFFFF', 'Dancing Script', 15.0, NULL, NOW() - INTERVAL 76 DAY, 109, FALSE, NULL, 'en', NOW() - INTERVAL 76 DAY, NOW() - INTERVAL 76 DAY, NULL),
('lt000000-0000-0000-0000-000000000021', '11111111-1111-1111-1111-111111111111', NULL, '행복한 하루', '행복한 하루 되세요! 오늘도 웃음 가득한 하루가 되길 바라요. 작은 것에도 감사하는 마음을 가지세요. 😊', '행복한 하루 되세요! 오늘도 웃음 가득한 하루가 되길 바라요. 작은 것에도 감사하는 마음을 가지세요. 😊', 'PUBLIC', FALSE, '#061A17', '#FFFFFF', 'Sunflower', 16.0, NULL, NOW() - INTERVAL 78 DAY, 112, FALSE, NULL, 'ko', NOW() - INTERVAL 78 DAY, NOW() - INTERVAL 78 DAY, NULL),
('lt000000-0000-0000-0000-000000000022', '22222222-2222-2222-2222-222222222222', NULL, '좋은 하루 되세요', '좋은 하루 되세요! 오늘도 멋진 하루 보내시길 바라요. 긍정적인 에너지가 가득하길! ✨', '좋은 하루 되세요! 오늘도 멋진 하루 보내시길 바라요. 긍정적인 에너지가 가득하길! ✨', 'PUBLIC', FALSE, '#1D1433', '#FFFFFF', 'Yeon Sung', 15.0, NULL, NOW() - INTERVAL 80 DAY, 115, FALSE, NULL, 'ko', NOW() - INTERVAL 80 DAY, NOW() - INTERVAL 80 DAY, NULL),
('lt000000-0000-0000-0000-000000000023', '33333333-3333-3333-3333-333333333333', NULL, '힘내세요', '힘내세요! 어려운 일이 있어도 포기하지 마세요. 당신은 충분히 강하고 멋져요. 오늘도 화이팅! 💪', '힘내세요! 어려운 일이 있어도 포기하지 마세요. 당신은 충분히 강하고 멋져요. 오늘도 화이팅! 💪', 'PUBLIC', FALSE, '#001133', '#FFFFFF', 'Poor Story', 16.0, NULL, NOW() - INTERVAL 82 DAY, 118, FALSE, NULL, 'ko', NOW() - INTERVAL 82 DAY, NOW() - INTERVAL 82 DAY, NULL),
('lt000000-0000-0000-0000-000000000024', '44444444-4444-4444-4444-444444444444', NULL, '따뜻한 하루', '따뜻한 하루 되세요. 주변 사람들에게 따뜻한 말 한마디 전해보세요. 사랑과 평화가 함께하길. 💕', '따뜻한 하루 되세요. 주변 사람들에게 따뜻한 말 한마디 전해보세요. 사랑과 평화가 함께하길. 💕', 'PUBLIC', FALSE, '#210014', '#FFFFFF', 'Dongle', 15.0, NULL, NOW() - INTERVAL 84 DAY, 121, FALSE, NULL, 'ko', NOW() - INTERVAL 84 DAY, NOW() - INTERVAL 84 DAY, NULL),
('lt000000-0000-0000-0000-000000000025', '55555555-5555-5555-5555-555555555555', NULL, '새로운 시작', '새로운 시작이에요. 모두에게 좋은 일만 가득하길 바라요. 힘든 일이 있어도 포기하지 말고 함께 이겨내요! 🌱', '새로운 시작이에요. 모두에게 좋은 일만 가득하길 바라요. 힘든 일이 있어도 포기하지 말고 함께 이겨내요! 🌱', 'PUBLIC', FALSE, '#001100', '#00FF00', 'Gamja Flower', 16.0, NULL, NOW() - INTERVAL 86 DAY, 124, FALSE, NULL, 'ko', NOW() - INTERVAL 86 DAY, NOW() - INTERVAL 86 DAY, NULL),
('lt000000-0000-0000-0000-000000000026', '66666666-6666-6666-6666-666666666666', NULL, '감사한 마음', '감사한 마음으로 하루를 시작해요. 작은 것에도 감사할 수 있는 마음을 가지면 더 행복해질 수 있어요. 🙏', '감사한 마음으로 하루를 시작해요. 작은 것에도 감사할 수 있는 마음을 가지면 더 행복해질 수 있어요. 🙏', 'PUBLIC', FALSE, '#000000', '#00CC00', 'Hi Melody', 15.0, NULL, NOW() - INTERVAL 88 DAY, 127, FALSE, NULL, 'ko', NOW() - INTERVAL 88 DAY, NOW() - INTERVAL 88 DAY, NULL),
('lt000000-0000-0000-0000-000000000027', '77777777-7777-7777-7777-777777777777', NULL, '밤하늘을 보며', '밤하늘을 보니 마음이 편안해져요. 별들이 반짝이는 모습이 정말 아름다워요. 오늘 하루 고생 많으셨어요. 🌙', '밤하늘을 보니 마음이 편안해져요. 별들이 반짝이는 모습이 정말 아름다워요. 오늘 하루 고생 많으셨어요. 🌙', 'PUBLIC', FALSE, '#1A0016', '#FF00FF', 'Nanum Pen Script', 16.0, NULL, NOW() - INTERVAL 90 DAY, 130, FALSE, NULL, 'ko', NOW() - INTERVAL 90 DAY, NOW() - INTERVAL 90 DAY, NULL),
('lt000000-0000-0000-0000-000000000028', '88888888-8888-8888-8888-888888888888', NULL, '봄날의 기운', '봄이 오고 있어요. 따뜻한 바람이 불어오고 꽃들이 피기 시작했어요. 새로운 계절, 새로운 시작을 함께해요. 🌺', '봄이 오고 있어요. 따뜻한 바람이 불어오고 꽃들이 피기 시작했어요. 새로운 계절, 새로운 시작을 함께해요. 🌺', 'PUBLIC', FALSE, '#2A1F00', '#FFFF00', 'Jua', 15.0, NULL, NOW() - INTERVAL 92 DAY, 133, FALSE, NULL, 'ko', NOW() - INTERVAL 92 DAY, NOW() - INTERVAL 92 DAY, NULL),
('lt000000-0000-0000-0000-000000000029', '11111111-1111-1111-1111-111111111111', NULL, '오늘의 일기', '오늘 하루도 무사히 보내고 있어요. 작은 행복들을 모아서 큰 행복을 만들어가고 있어요. 모두도 행복한 하루 보내세요! 📝', '오늘 하루도 무사히 보내고 있어요. 작은 행복들을 모아서 큰 행복을 만들어가고 있어요. 모두도 행복한 하루 보내세요! 📝', 'PUBLIC', FALSE, '#0A0024', '#FFFFFF', 'Sunflower', 16.0, NULL, NOW() - INTERVAL 94 DAY, 136, FALSE, NULL, 'ko', NOW() - INTERVAL 94 DAY, NOW() - INTERVAL 94 DAY, NULL),
('lt000000-0000-0000-0000-00000000002a', '22222222-2222-2222-2222-222222222222', NULL, '주말의 아침', '주말 아침이에요. 느긋하게 커피 한 잔 마시며 하루를 시작해요. 모두 편안한 주말 보내세요! ☕', '주말 아침이에요. 느긋하게 커피 한 잔 마시며 하루를 시작해요. 모두 편안한 주말 보내세요! ☕', 'PUBLIC', FALSE, '#1E1A14', '#FFFFFF', 'Yeon Sung', 15.0, NULL, NOW() - INTERVAL 96 DAY, 139, FALSE, NULL, 'ko', NOW() - INTERVAL 96 DAY, NOW() - INTERVAL 96 DAY, NULL),
('lt000000-0000-0000-0000-00000000002b', '33333333-3333-3333-3333-333333333333', NULL, '저녁 노을', '저녁 노을이 정말 아름다워요. 하루를 마무리하며 내일을 기대해봐요. 모두 좋은 저녁 되세요! 🌅', '저녁 노을이 정말 아름다워요. 하루를 마무리하며 내일을 기대해봐요. 모두 좋은 저녁 되세요! 🌅', 'PUBLIC', FALSE, '#061A17', '#FFFFFF', 'Poor Story', 16.0, NULL, NOW() - INTERVAL 98 DAY, 142, FALSE, NULL, 'ko', NOW() - INTERVAL 98 DAY, NOW() - INTERVAL 98 DAY, NULL),
('lt000000-0000-0000-0000-00000000002c', '44444444-4444-4444-4444-444444444444', NULL, '새로운 도전', '새로운 도전을 시작해요. 두려워하지 말고 한 걸음씩 나아가요. 당신은 할 수 있어요! 화이팅! 🚀', '새로운 도전을 시작해요. 두려워하지 말고 한 걸음씩 나아가요. 당신은 할 수 있어요! 화이팅! 🚀', 'PUBLIC', FALSE, '#1D1433', '#FFFFFF', 'Dongle', 15.0, NULL, NOW() - INTERVAL 100 DAY, 145, FALSE, NULL, 'ko', NOW() - INTERVAL 100 DAY, NOW() - INTERVAL 100 DAY, NULL),
('lt000000-0000-0000-0000-00000000002d', '55555555-5555-5555-5555-555555555555', NULL, '따뜻한 마음', '따뜻한 마음으로 하루를 시작해요. 작은 친절이 큰 기쁨을 만들어요. 모두에게 따뜻한 하루가 되길 바라요. 💝', '따뜻한 마음으로 하루를 시작해요. 작은 친절이 큰 기쁨을 만들어요. 모두에게 따뜻한 하루가 되길 바라요. 💝', 'PUBLIC', FALSE, '#001133', '#FFFFFF', 'Gamja Flower', 16.0, NULL, NOW() - INTERVAL 102 DAY, 148, FALSE, NULL, 'ko', NOW() - INTERVAL 102 DAY, NOW() - INTERVAL 102 DAY, NULL),
('lt000000-0000-0000-0000-00000000002e', '66666666-6666-6666-6666-666666666666', NULL, '지금 이 순간', '지금 이 순간을 소중하게 여겨요. 과거는 지나갔고 미래는 아직 오지 않았어요. 지금 이 순간이 가장 소중해요. ⏰', '지금 이 순간을 소중하게 여겨요. 과거는 지나갔고 미래는 아직 오지 않았어요. 지금 이 순간이 가장 소중해요. ⏰', 'PUBLIC', FALSE, '#210014', '#FFFFFF', 'Hi Melody', 15.0, NULL, NOW() - INTERVAL 104 DAY, 151, FALSE, NULL, 'ko', NOW() - INTERVAL 104 DAY, NOW() - INTERVAL 104 DAY, NULL),
('lt000000-0000-0000-0000-00000000002f', '77777777-7777-7777-7777-777777777777', NULL, '함께하는 힘', '혼자서는 어려운 일도 함께하면 쉬워져요. 서로를 응원하고 지지하는 마음이 중요해요. 함께 힘내요! 🤝', '혼자서는 어려운 일도 함께하면 쉬워져요. 서로를 응원하고 지지하는 마음이 중요해요. 함께 힘내요! 🤝', 'PUBLIC', FALSE, '#001100', '#00FF00', 'Nanum Pen Script', 16.0, NULL, NOW() - INTERVAL 106 DAY, 154, FALSE, NULL, 'ko', NOW() - INTERVAL 106 DAY, NOW() - INTERVAL 106 DAY, NULL),
('lt000000-0000-0000-0000-000000000030', '88888888-8888-8888-8888-888888888888', NULL, '작은 기쁨', '작은 기쁨을 찾아봐요. 아침 커피 한 잔, 따뜻한 햇살, 친구의 미소. 작은 것들이 모여 큰 행복이 되요. 😄', '작은 기쁨을 찾아봐요. 아침 커피 한 잔, 따뜻한 햇살, 친구의 미소. 작은 것들이 모여 큰 행복이 되요. 😄', 'PUBLIC', FALSE, '#000000', '#00CC00', 'Jua', 15.0, NULL, NOW() - INTERVAL 108 DAY, 157, FALSE, NULL, 'ko', NOW() - INTERVAL 108 DAY, NOW() - INTERVAL 108 DAY, NULL),
('lt000000-0000-0000-0000-000000000031', '11111111-1111-1111-1111-111111111111', NULL, '꿈을 향해', '꿈을 향해 한 걸음씩 나아가요. 멀리 보이지 않아도 괜찮아요. 오늘 한 걸음이 내일의 큰 발걸음이 될 거예요. 🌠', '꿈을 향해 한 걸음씩 나아가요. 멀리 보이지 않아도 괜찮아요. 오늘 한 걸음이 내일의 큰 발걸음이 될 거예요. 🌠', 'PUBLIC', FALSE, '#1A0016', '#FF00FF', 'Sunflower', 16.0, NULL, NOW() - INTERVAL 110 DAY, 160, FALSE, NULL, 'ko', NOW() - INTERVAL 110 DAY, NOW() - INTERVAL 110 DAY, NULL),
('lt000000-0000-0000-0000-000000000032', '22222222-2222-2222-2222-222222222222', NULL, 'Love Yourself', 'Love yourself first! You cannot pour from an empty cup. Take care of yourself so you can take care of others. Self-care matters! 💆', 'Love yourself first! You cannot pour from an empty cup. Take care of yourself so you can take care of others. Self-care matters! 💆', 'PUBLIC', FALSE, '#2A1F00', '#FFFF00', 'Indie Flower', 15.0, NULL, NOW() - INTERVAL 112 DAY, 163, FALSE, NULL, 'en', NOW() - INTERVAL 112 DAY, NOW() - INTERVAL 112 DAY, NULL),
('lt000000-0000-0000-0000-000000000033', '33333333-3333-3333-3333-333333333333', NULL, 'Create Magic', 'Create magic in ordinary moments! Life is made of small moments, not grand gestures. Find beauty in simplicity! ✨', 'Create magic in ordinary moments! Life is made of small moments, not grand gestures. Find beauty in simplicity! ✨', 'PUBLIC', FALSE, '#0A0024', '#FFFFFF', 'Kalam', 16.0, NULL, NOW() - INTERVAL 114 DAY, 166, FALSE, NULL, 'en', NOW() - INTERVAL 114 DAY, NOW() - INTERVAL 114 DAY, NULL),
('lt000000-0000-0000-0000-000000000034', '44444444-4444-4444-4444-444444444444', NULL, 'Be Present', 'Be present in the moment! The past is gone, the future is uncertain, but the present is a gift. Live it fully! 🎁', 'Be present in the moment! The past is gone, the future is uncertain, but the present is a gift. Live it fully! 🎁', 'PUBLIC', FALSE, '#1E1A14', '#FFFFFF', 'Patrick Hand', 15.0, NULL, NOW() - INTERVAL 116 DAY, 169, FALSE, NULL, 'en', NOW() - INTERVAL 116 DAY, NOW() - INTERVAL 116 DAY, NULL),
('lt000000-0000-0000-0000-000000000035', '55555555-5555-5555-5555-555555555555', NULL, 'Inspire Others', 'Inspire others by being yourself! Your authenticity is your greatest gift. Share it with the world! 🌍', 'Inspire others by being yourself! Your authenticity is your greatest gift. Share it with the world! 🌍', 'PUBLIC', FALSE, '#061A17', '#FFFFFF', 'Shadows Into Light', 16.0, NULL, NOW() - INTERVAL 118 DAY, 172, FALSE, NULL, 'en', NOW() - INTERVAL 118 DAY, NOW() - INTERVAL 118 DAY, NULL),
('lt000000-0000-0000-0000-000000000036', '66666666-6666-6666-6666-666666666666', NULL, 'Choose Happiness', 'Choose happiness! It''s not about what happens to you, but how you react to it. Your attitude determines your altitude! 🎈', 'Choose happiness! It''s not about what happens to you, but how you react to it. Your attitude determines your altitude! 🎈', 'PUBLIC', FALSE, '#1D1433', '#FFFFFF', 'Comic Neue', 15.0, NULL, NOW() - INTERVAL 120 DAY, 175, FALSE, NULL, 'en', NOW() - INTERVAL 120 DAY, NOW() - INTERVAL 120 DAY, NULL),
('lt000000-0000-0000-0000-000000000037', '77777777-7777-7777-7777-777777777777', NULL, 'Make Memories', 'Make memories that last a lifetime! Experiences are more valuable than possessions. Collect moments, not things! 📸', 'Make memories that last a lifetime! Experiences are more valuable than possessions. Collect moments, not things! 📸', 'PUBLIC', FALSE, '#001133', '#FFFFFF', 'Caveat', 16.0, NULL, NOW() - INTERVAL 122 DAY, 178, FALSE, NULL, 'en', NOW() - INTERVAL 122 DAY, NOW() - INTERVAL 122 DAY, NULL),
('lt000000-0000-0000-0000-000000000038', '88888888-8888-8888-8888-888888888888', NULL, 'Stay Curious', 'Stay curious! Questions are more important than answers. Keep wondering, keep exploring, keep learning! 🔍', 'Stay curious! Questions are more important than answers. Keep wondering, keep exploring, keep learning! 🔍', 'PUBLIC', FALSE, '#210014', '#FFFFFF', 'Dancing Script', 15.0, NULL, NOW() - INTERVAL 124 DAY, 181, FALSE, NULL, 'en', NOW() - INTERVAL 124 DAY, NOW() - INTERVAL 124 DAY, NULL),
('lt000000-0000-0000-0000-000000000039', '11111111-1111-1111-1111-111111111111', NULL, 'Be Patient', 'Be patient with yourself! Growth takes time. Trust the process and enjoy the journey. You''re doing great! 🌱', 'Be patient with yourself! Growth takes time. Trust the process and enjoy the journey. You''re doing great! 🌱', 'PUBLIC', FALSE, '#001100', '#00FF00', 'Permanent Marker', 16.0, NULL, NOW() - INTERVAL 126 DAY, 184, FALSE, NULL, 'en', NOW() - INTERVAL 126 DAY, NOW() - INTERVAL 126 DAY, NULL),
('lt000000-0000-0000-0000-00000000003a', '22222222-2222-2222-2222-222222222222', NULL, 'Find Balance', 'Find balance in everything! Work hard, but also rest. Be serious, but also laugh. Life is about harmony! ⚖️', 'Find balance in everything! Work hard, but also rest. Be serious, but also laugh. Life is about harmony! ⚖️', 'PUBLIC', FALSE, '#000000', '#00CC00', 'Indie Flower', 15.0, NULL, NOW() - INTERVAL 128 DAY, 187, FALSE, NULL, 'en', NOW() - INTERVAL 128 DAY, NOW() - INTERVAL 128 DAY, NULL),
('lt000000-0000-0000-0000-00000000003b', '33333333-3333-3333-3333-333333333333', NULL, 'Share Your Light', 'Share your light with the world! Your unique perspective and talents can brighten someone''s day. Don''t hide your shine! 💡', 'Share your light with the world! Your unique perspective and talents can brighten someone''s day. Don''t hide your shine! 💡', 'PUBLIC', FALSE, '#1A0016', '#FF00FF', 'Kalam', 16.0, NULL, NOW() - INTERVAL 130 DAY, 190, FALSE, NULL, 'en', NOW() - INTERVAL 130 DAY, NOW() - INTERVAL 130 DAY, NULL),
('lt000000-0000-0000-0000-00000000003c', '44444444-4444-4444-4444-444444444444', NULL, 'Keep Moving Forward', 'Keep moving forward! Even small steps count. Progress, not perfection, is what matters. You''re on the right track! 🚶', 'Keep moving forward! Even small steps count. Progress, not perfection, is what matters. You''re on the right track! 🚶', 'PUBLIC', FALSE, '#2A1F00', '#FFFF00', 'Patrick Hand', 15.0, NULL, NOW() - INTERVAL 132 DAY, 193, FALSE, NULL, 'en', NOW() - INTERVAL 132 DAY, NOW() - INTERVAL 132 DAY, NULL),
('lt000000-0000-0000-0000-00000000003d', '55555555-5555-5555-5555-555555555555', NULL, 'Believe in Miracles', 'Believe in miracles! Sometimes the most beautiful things come from the most unexpected places. Stay open to possibilities! ✨', 'Believe in miracles! Sometimes the most beautiful things come from the most unexpected places. Stay open to possibilities! ✨', 'PUBLIC', FALSE, '#0A0024', '#FFFFFF', 'Shadows Into Light', 16.0, NULL, NOW() - INTERVAL 134 DAY, 196, FALSE, NULL, 'en', NOW() - INTERVAL 134 DAY, NOW() - INTERVAL 134 DAY, NULL),
('lt000000-0000-0000-0000-00000000003e', '66666666-6666-6666-6666-666666666666', NULL, 'Be Grateful', 'Be grateful for what you have! Gratitude turns what we have into enough. Appreciate the little things! 🙏', 'Be grateful for what you have! Gratitude turns what we have into enough. Appreciate the little things! 🙏', 'PUBLIC', FALSE, '#1E1A14', '#FFFFFF', 'Comic Neue', 15.0, NULL, NOW() - INTERVAL 136 DAY, 199, FALSE, NULL, 'en', NOW() - INTERVAL 136 DAY, NOW() - INTERVAL 136 DAY, NULL),
('lt000000-0000-0000-0000-00000000003f', '77777777-7777-7777-7777-777777777777', NULL, 'Dream Without Limits', 'Dream without limits! Your imagination is your only boundary. Think big, start small, but most importantly, start! 🌌', 'Dream without limits! Your imagination is your only boundary. Think big, start small, but most importantly, start! 🌌', 'PUBLIC', FALSE, '#061A17', '#FFFFFF', 'Caveat', 16.0, NULL, NOW() - INTERVAL 138 DAY, 202, FALSE, NULL, 'en', NOW() - INTERVAL 138 DAY, NOW() - INTERVAL 138 DAY, NULL),
('lt000000-0000-0000-0000-000000000040', '88888888-8888-8888-8888-888888888888', NULL, 'Stay True', 'Stay true to yourself! Authenticity attracts the right people and opportunities. Be genuine, be you! 💎', 'Stay true to yourself! Authenticity attracts the right people and opportunities. Be genuine, be you! 💎', 'PUBLIC', FALSE, '#1D1433', '#FFFFFF', 'Dancing Script', 15.0, NULL, NOW() - INTERVAL 140 DAY, 205, FALSE, NULL, 'en', NOW() - INTERVAL 140 DAY, NOW() - INTERVAL 140 DAY, NULL),
('lt000000-0000-0000-0000-000000000041', '11111111-1111-1111-1111-111111111111', NULL, 'Find Your Why', 'Find your why! When you know your purpose, everything else falls into place. Purpose gives meaning to your journey! 🎯', 'Find your why! When you know your purpose, everything else falls into place. Purpose gives meaning to your journey! 🎯', 'PUBLIC', FALSE, '#001133', '#FFFFFF', 'Permanent Marker', 16.0, NULL, NOW() - INTERVAL 142 DAY, 208, FALSE, NULL, 'en', NOW() - INTERVAL 142 DAY, NOW() - INTERVAL 142 DAY, NULL),
('lt000000-0000-0000-0000-000000000042', '22222222-2222-2222-2222-222222222222', NULL, 'Celebrate Life', 'Celebrate life every day! Each day is a new opportunity to create something beautiful. Make it count! 🎊', 'Celebrate life every day! Each day is a new opportunity to create something beautiful. Make it count! 🎊', 'PUBLIC', FALSE, '#210014', '#FFFFFF', 'Indie Flower', 15.0, NULL, NOW() - INTERVAL 144 DAY, 211, FALSE, NULL, 'en', NOW() - INTERVAL 144 DAY, NOW() - INTERVAL 144 DAY, NULL),
('lt000000-0000-0000-0000-000000000043', '33333333-3333-3333-3333-333333333333', NULL, 'Be the Change', 'Be the change you wish to see! Start with yourself, and watch how it inspires others. Lead by example! 🌟', 'Be the change you wish to see! Start with yourself, and watch how it inspires others. Lead by example! 🌟', 'PUBLIC', FALSE, '#001100', '#00FF00', 'Kalam', 16.0, NULL, NOW() - INTERVAL 146 DAY, 214, FALSE, NULL, 'en', NOW() - INTERVAL 146 DAY, NOW() - INTERVAL 146 DAY, NULL),
('lt000000-0000-0000-0000-000000000044', '44444444-4444-4444-4444-444444444444', NULL, 'Enjoy the Journey', 'Enjoy the journey, not just the destination! Life is about the experiences along the way. Savor every moment! 🗺️', 'Enjoy the journey, not just the destination! Life is about the experiences along the way. Savor every moment! 🗺️', 'PUBLIC', FALSE, '#000000', '#00CC00', 'Patrick Hand', 15.0, NULL, NOW() - INTERVAL 148 DAY, 217, FALSE, NULL, 'en', NOW() - INTERVAL 148 DAY, NOW() - INTERVAL 148 DAY, NULL),
('lt000000-0000-0000-0000-000000000045', '55555555-5555-5555-5555-555555555555', NULL, 'Rise Above', 'Rise above challenges! Difficulties are opportunities in disguise. They make you stronger and wiser. Keep rising! 📈', 'Rise above challenges! Difficulties are opportunities in disguise. They make you stronger and wiser. Keep rising! 📈', 'PUBLIC', FALSE, '#1A0016', '#FF00FF', 'Shadows Into Light', 16.0, NULL, NOW() - INTERVAL 150 DAY, 220, FALSE, NULL, 'en', NOW() - INTERVAL 150 DAY, NOW() - INTERVAL 150 DAY, NULL),
('lt000000-0000-0000-0000-000000000046', '66666666-6666-6666-6666-666666666666', NULL, 'Create Your Path', 'Create your own path! Don''t follow someone else''s journey. Your unique path is waiting for you to discover it! 🛤️', 'Create your own path! Don''t follow someone else''s journey. Your unique path is waiting for you to discover it! 🛤️', 'PUBLIC', FALSE, '#2A1F00', '#FFFF00', 'Comic Neue', 15.0, NULL, NOW() - INTERVAL 152 DAY, 223, FALSE, NULL, 'en', NOW() - INTERVAL 152 DAY, NOW() - INTERVAL 152 DAY, NULL),
('lt000000-0000-0000-0000-000000000047', '77777777-7777-7777-7777-777777777777', NULL, 'Stay Humble', 'Stay humble, but never stop dreaming! Humility keeps you grounded, while dreams keep you reaching for the stars! ⭐', 'Stay humble, but never stop dreaming! Humility keeps you grounded, while dreams keep you reaching for the stars! ⭐', 'PUBLIC', FALSE, '#0A0024', '#FFFFFF', 'Caveat', 16.0, NULL, NOW() - INTERVAL 154 DAY, 226, FALSE, NULL, 'en', NOW() - INTERVAL 154 DAY, NOW() - INTERVAL 154 DAY, NULL),
('lt000000-0000-0000-0000-000000000048', '88888888-8888-8888-8888-888888888888', NULL, 'Make It Count', 'Make every moment count! Time is precious, so use it wisely. Invest in things that matter most to you! ⏳', 'Make every moment count! Time is precious, so use it wisely. Invest in things that matter most to you! ⏳', 'PUBLIC', FALSE, '#1E1A14', '#FFFFFF', 'Dancing Script', 15.0, NULL, NOW() - INTERVAL 156 DAY, 229, FALSE, NULL, 'en', NOW() - INTERVAL 156 DAY, NOW() - INTERVAL 156 DAY, NULL),
('lt000000-0000-0000-0000-000000000049', '11111111-1111-1111-1111-111111111111', NULL, 'Find Your Tribe', 'Find your tribe! Surround yourself with people who lift you up and believe in your dreams. Together is better! 👥', 'Find your tribe! Surround yourself with people who lift you up and believe in your dreams. Together is better! 👥', 'PUBLIC', FALSE, '#061A17', '#FFFFFF', 'Permanent Marker', 16.0, NULL, NOW() - INTERVAL 158 DAY, 232, FALSE, NULL, 'en', NOW() - INTERVAL 158 DAY, NOW() - INTERVAL 158 DAY, NULL),
('lt000000-0000-0000-0000-00000000004a', '22222222-2222-2222-2222-222222222222', NULL, 'Be Fearless', 'Be fearless in pursuit of what sets your soul on fire! Fear is temporary, but regret lasts forever. Take the leap! 🔥', 'Be fearless in pursuit of what sets your soul on fire! Fear is temporary, but regret lasts forever. Take the leap! 🔥', 'PUBLIC', FALSE, '#1D1433', '#FFFFFF', 'Indie Flower', 15.0, NULL, NOW() - INTERVAL 160 DAY, 235, FALSE, NULL, 'en', NOW() - INTERVAL 160 DAY, NOW() - INTERVAL 160 DAY, NULL),
('lt000000-0000-0000-0000-00000000004b', '33333333-3333-3333-3333-333333333333', NULL, 'Stay Focused', 'Stay focused on your goals! Distractions are everywhere, but your determination will guide you. Keep your eyes on the prize! 🎯', 'Stay focused on your goals! Distractions are everywhere, but your determination will guide you. Keep your eyes on the prize! 🎯', 'PUBLIC', FALSE, '#001133', '#FFFFFF', 'Kalam', 16.0, NULL, NOW() - INTERVAL 162 DAY, 238, FALSE, NULL, 'en', NOW() - INTERVAL 162 DAY, NOW() - INTERVAL 162 DAY, NULL),
('lt000000-0000-0000-0000-00000000004c', '44444444-4444-4444-4444-444444444444', NULL, 'Trust Yourself', 'Trust yourself! You know more than you think you do. Your intuition is your inner wisdom. Listen to it! 🧠', 'Trust yourself! You know more than you think you do. Your intuition is your inner wisdom. Listen to it! 🧠', 'PUBLIC', FALSE, '#210014', '#FFFFFF', 'Patrick Hand', 15.0, NULL, NOW() - INTERVAL 164 DAY, 241, FALSE, NULL, 'en', NOW() - INTERVAL 164 DAY, NOW() - INTERVAL 164 DAY, NULL),
('lt000000-0000-0000-0000-00000000004d', '55555555-5555-5555-5555-555555555555', NULL, 'Keep Growing', 'Keep growing! Personal development is a lifelong journey. Every day is a chance to become a better version of yourself! 🌳', 'Keep growing! Personal development is a lifelong journey. Every day is a chance to become a better version of yourself! 🌳', 'PUBLIC', FALSE, '#001100', '#00FF00', 'Shadows Into Light', 16.0, NULL, NOW() - INTERVAL 166 DAY, 244, FALSE, NULL, 'en', NOW() - INTERVAL 166 DAY, NOW() - INTERVAL 166 DAY, NULL),
('lt000000-0000-0000-0000-00000000004e', '66666666-6666-6666-6666-666666666666', NULL, 'Be Kind Always', 'Be kind always! Kindness is a language that everyone understands. It costs nothing but means everything! 💝', 'Be kind always! Kindness is a language that everyone understands. It costs nothing but means everything! 💝', 'PUBLIC', FALSE, '#000000', '#00CC00', 'Comic Neue', 15.0, NULL, NOW() - INTERVAL 168 DAY, 247, FALSE, NULL, 'en', NOW() - INTERVAL 168 DAY, NOW() - INTERVAL 168 DAY, NULL),
('lt000000-0000-0000-0000-00000000004f', '77777777-7777-7777-7777-777777777777', NULL, 'Live Fully', 'Live fully in every moment! Don''t wait for the perfect time. The perfect time is now. Seize the day! ⚡', 'Live fully in every moment! Don''t wait for the perfect time. The perfect time is now. Seize the day! ⚡', 'PUBLIC', FALSE, '#1A0016', '#FF00FF', 'Caveat', 16.0, NULL, NOW() - INTERVAL 170 DAY, 250, FALSE, NULL, 'en', NOW() - INTERVAL 170 DAY, NOW() - INTERVAL 170 DAY, NULL),
('lt000000-0000-0000-0000-000000000050', '88888888-8888-8888-8888-888888888888', NULL, 'Stay Positive', 'Stay positive even when things get tough! Your mindset determines your reality. Choose optimism! 🌈', 'Stay positive even when things get tough! Your mindset determines your reality. Choose optimism! 🌈', 'PUBLIC', FALSE, '#2A1F00', '#FFFF00', 'Dancing Script', 15.0, NULL, NOW() - INTERVAL 172 DAY, 253, FALSE, NULL, 'en', NOW() - INTERVAL 172 DAY, NOW() - INTERVAL 172 DAY, NULL),
('lt000000-0000-0000-0000-000000000051', '11111111-1111-1111-1111-111111111111', NULL, 'Make a Difference', 'Make a difference in someone''s life today! Small acts of kindness can have a huge impact. Be the reason someone smiles! 😊', 'Make a difference in someone''s life today! Small acts of kindness can have a huge impact. Be the reason someone smiles! 😊', 'PUBLIC', FALSE, '#0A0024', '#FFFFFF', 'Permanent Marker', 16.0, NULL, NOW() - INTERVAL 174 DAY, 256, FALSE, NULL, 'en', NOW() - INTERVAL 174 DAY, NOW() - INTERVAL 174 DAY, NULL),
('lt000000-0000-0000-0000-000000000052', '22222222-2222-2222-2222-222222222222', NULL, 'Chase Excellence', 'Chase excellence, not perfection! Perfection is an illusion, but excellence is achievable. Do your best! 🏆', 'Chase excellence, not perfection! Perfection is an illusion, but excellence is achievable. Do your best! 🏆', 'PUBLIC', FALSE, '#1E1A14', '#FFFFFF', 'Indie Flower', 15.0, NULL, NOW() - INTERVAL 176 DAY, 259, FALSE, NULL, 'en', NOW() - INTERVAL 176 DAY, NOW() - INTERVAL 176 DAY, NULL),
('lt000000-0000-0000-0000-000000000053', '33333333-3333-3333-3333-333333333333', NULL, 'Be Grateful Daily', 'Be grateful daily! Gratitude is the key to happiness. Count your blessings, not your problems. Appreciate life! 🙏', 'Be grateful daily! Gratitude is the key to happiness. Count your blessings, not your problems. Appreciate life! 🙏', 'PUBLIC', FALSE, '#061A17', '#FFFFFF', 'Kalam', 16.0, NULL, NOW() - INTERVAL 178 DAY, 262, FALSE, NULL, 'en', NOW() - INTERVAL 178 DAY, NOW() - INTERVAL 178 DAY, NULL),
('lt000000-0000-0000-0000-000000000054', '44444444-4444-4444-4444-444444444444', NULL, 'Find Your Voice', 'Find your voice and use it! Your perspective matters. Share your thoughts, ideas, and experiences. Speak up! 🗣️', 'Find your voice and use it! Your perspective matters. Share your thoughts, ideas, and experiences. Speak up! 🗣️', 'PUBLIC', FALSE, '#1D1433', '#FFFFFF', 'Patrick Hand', 15.0, NULL, NOW() - INTERVAL 180 DAY, 265, FALSE, NULL, 'en', NOW() - INTERVAL 180 DAY, NOW() - INTERVAL 180 DAY, NULL),
('lt000000-0000-0000-0000-000000000055', '55555555-5555-5555-5555-555555555555', NULL, 'Stay Committed', 'Stay committed to your goals! Consistency beats intensity. Small daily actions lead to big results. Keep going! 🎯', 'Stay committed to your goals! Consistency beats intensity. Small daily actions lead to big results. Keep going! 🎯', 'PUBLIC', FALSE, '#001133', '#FFFFFF', 'Shadows Into Light', 16.0, NULL, NOW() - INTERVAL 182 DAY, 268, FALSE, NULL, 'en', NOW() - INTERVAL 182 DAY, NOW() - INTERVAL 182 DAY, NULL),
('lt000000-0000-0000-0000-000000000056', '66666666-6666-6666-6666-666666666666', NULL, 'Embrace Failure', 'Embrace failure as a teacher! Every mistake is a lesson. Learn from it, grow from it, and move forward stronger! 📚', 'Embrace failure as a teacher! Every mistake is a lesson. Learn from it, grow from it, and move forward stronger! 📚', 'PUBLIC', FALSE, '#210014', '#FFFFFF', 'Comic Neue', 15.0, NULL, NOW() - INTERVAL 184 DAY, 271, FALSE, NULL, 'en', NOW() - INTERVAL 184 DAY, NOW() - INTERVAL 184 DAY, NULL),
('lt000000-0000-0000-0000-000000000057', '77777777-7777-7777-7777-777777777777', NULL, 'Create Your Legacy', 'Create your legacy! What you do today will be remembered tomorrow. Make it something worth remembering! 🏛️', 'Create your legacy! What you do today will be remembered tomorrow. Make it something worth remembering! 🏛️', 'PUBLIC', FALSE, '#001100', '#00FF00', 'Caveat', 16.0, NULL, NOW() - INTERVAL 186 DAY, 274, FALSE, NULL, 'en', NOW() - INTERVAL 186 DAY, NOW() - INTERVAL 186 DAY, NULL),
('lt000000-0000-0000-0000-000000000058', '88888888-8888-8888-8888-888888888888', NULL, 'Stay Inspired', 'Stay inspired! Inspiration is everywhere if you look for it. Find what motivates you and let it drive you forward! 💫', 'Stay inspired! Inspiration is everywhere if you look for it. Find what motivates you and let it drive you forward! 💫', 'PUBLIC', FALSE, '#000000', '#00CC00', 'Dancing Script', 15.0, NULL, NOW() - INTERVAL 188 DAY, 277, FALSE, NULL, 'en', NOW() - INTERVAL 188 DAY, NOW() - INTERVAL 188 DAY, NULL),
('lt000000-0000-0000-0000-000000000059', '11111111-1111-1111-1111-111111111111', NULL, 'Be the Light', 'Be the light in someone''s darkness! Your kindness can be someone''s hope. Shine bright and help others shine too! 💡', 'Be the light in someone''s darkness! Your kindness can be someone''s hope. Shine bright and help others shine too! 💡', 'PUBLIC', FALSE, '#1A0016', '#FF00FF', 'Permanent Marker', 16.0, NULL, NOW() - INTERVAL 190 DAY, 280, FALSE, NULL, 'en', NOW() - INTERVAL 190 DAY, NOW() - INTERVAL 190 DAY, NULL),
('lt000000-0000-0000-0000-00000000005a', '22222222-2222-2222-2222-222222222222', NULL, 'Keep Learning', 'Keep learning every day! Knowledge is power, and curiosity is the engine of achievement. Stay hungry for knowledge! 📖', 'Keep learning every day! Knowledge is power, and curiosity is the engine of achievement. Stay hungry for knowledge! 📖', 'PUBLIC', FALSE, '#2A1F00', '#FFFF00', 'Indie Flower', 15.0, NULL, NOW() - INTERVAL 192 DAY, 283, FALSE, NULL, 'en', NOW() - INTERVAL 192 DAY, NOW() - INTERVAL 192 DAY, NULL),
('lt000000-0000-0000-0000-00000000005b', '33333333-3333-3333-3333-333333333333', NULL, 'Stay Grounded', 'Stay grounded but reach for the stars! Humility and ambition can coexist. Be humble in your approach, ambitious in your goals! 🌟', 'Stay grounded but reach for the stars! Humility and ambition can coexist. Be humble in your approach, ambitious in your goals! 🌟', 'PUBLIC', FALSE, '#0A0024', '#FFFFFF', 'Kalam', 16.0, NULL, NOW() - INTERVAL 194 DAY, 286, FALSE, NULL, 'en', NOW() - INTERVAL 194 DAY, NOW() - INTERVAL 194 DAY, NULL),
('lt000000-0000-0000-0000-00000000005c', '44444444-4444-4444-4444-444444444444', NULL, 'Make It Happen', 'Make it happen! Don''t wait for opportunities, create them. Your future is in your hands. Take action now! 🚀', 'Make it happen! Don''t wait for opportunities, create them. Your future is in your hands. Take action now! 🚀', 'PUBLIC', FALSE, '#1E1A14', '#FFFFFF', 'Patrick Hand', 15.0, NULL, NOW() - INTERVAL 196 DAY, 289, FALSE, NULL, 'en', NOW() - INTERVAL 196 DAY, NOW() - INTERVAL 196 DAY, NULL),
('lt000000-0000-0000-0000-00000000005d', '55555555-5555-5555-5555-555555555555', NULL, 'Believe in Magic', 'Believe in magic! Not the kind in fairy tales, but the magic of hard work, dedication, and never giving up. Create your own magic! ✨', 'Believe in magic! Not the kind in fairy tales, but the magic of hard work, dedication, and never giving up. Create your own magic! ✨', 'PUBLIC', FALSE, '#061A17', '#FFFFFF', 'Shadows Into Light', 16.0, NULL, NOW() - INTERVAL 198 DAY, 292, FALSE, NULL, 'en', NOW() - INTERVAL 198 DAY, NOW() - INTERVAL 198 DAY, NULL),
('lt000000-0000-0000-0000-00000000005e', '66666666-6666-6666-6666-666666666666', NULL, 'Stay True to You', 'Stay true to yourself! Don''t change who you are to fit in. The right people will love you for who you are. Be authentic! 💎', 'Stay true to yourself! Don''t change who you are to fit in. The right people will love you for who you are. Be authentic! 💎', 'PUBLIC', FALSE, '#1D1433', '#FFFFFF', 'Comic Neue', 15.0, NULL, NOW() - INTERVAL 200 DAY, 295, FALSE, NULL, 'en', NOW() - INTERVAL 200 DAY, NOW() - INTERVAL 200 DAY, NULL),
('lt000000-0000-0000-0000-00000000005f', '77777777-7777-7777-7777-777777777777', NULL, 'Find Your Passion', 'Find your passion and let it consume you! When you love what you do, work becomes play. Follow your heart! ❤️', 'Find your passion and let it consume you! When you love what you do, work becomes play. Follow your heart! ❤️', 'PUBLIC', FALSE, '#001133', '#FFFFFF', 'Caveat', 16.0, NULL, NOW() - INTERVAL 202 DAY, 298, FALSE, NULL, 'en', NOW() - INTERVAL 202 DAY, NOW() - INTERVAL 202 DAY, NULL),
('lt000000-0000-0000-0000-000000000060', '88888888-8888-8888-8888-888888888888', NULL, 'Keep Shining', 'Keep shining! Your light is unique and beautiful. Don''t dim it for anyone. Let it shine bright and inspire others! 🌟', 'Keep shining! Your light is unique and beautiful. Don''t dim it for anyone. Let it shine bright and inspire others! 🌟', 'PUBLIC', FALSE, '#210014', '#FFFFFF', 'Dancing Script', 15.0, NULL, NOW() - INTERVAL 204 DAY, 301, FALSE, NULL, 'en', NOW() - INTERVAL 204 DAY, NOW() - INTERVAL 204 DAY, NULL),
('lt000000-0000-0000-0000-000000000061', '11111111-1111-1111-1111-111111111111', NULL, 'Stay Motivated', 'Stay motivated! Motivation comes and goes, but discipline keeps you going. Build habits that support your goals! 💪', 'Stay motivated! Motivation comes and goes, but discipline keeps you going. Build habits that support your goals! 💪', 'PUBLIC', FALSE, '#001100', '#00FF00', 'Permanent Marker', 16.0, NULL, NOW() - INTERVAL 206 DAY, 304, FALSE, NULL, 'en', NOW() - INTERVAL 206 DAY, NOW() - INTERVAL 206 DAY, NULL),
('lt000000-0000-0000-0000-000000000062', '22222222-2222-2222-2222-222222222222', NULL, 'Be Present Now', 'Be present now! The past is gone, the future is uncertain. All we have is this moment. Make it count! ⏰', 'Be present now! The past is gone, the future is uncertain. All we have is this moment. Make it count! ⏰', 'PUBLIC', FALSE, '#000000', '#00CC00', 'Indie Flower', 15.0, NULL, NOW() - INTERVAL 208 DAY, 307, FALSE, NULL, 'en', NOW() - INTERVAL 208 DAY, NOW() - INTERVAL 208 DAY, NULL),
('lt000000-0000-0000-0000-000000000063', '33333333-3333-3333-3333-333333333333', NULL, 'Create Joy', 'Create joy in your life! Happiness is not something you find, it''s something you create. Choose joy every day! 😄', 'Create joy in your life! Happiness is not something you find, it''s something you create. Choose joy every day! 😄', 'PUBLIC', FALSE, '#1A0016', '#FF00FF', 'Kalam', 16.0, NULL, NOW() - INTERVAL 210 DAY, 310, FALSE, NULL, 'en', NOW() - INTERVAL 210 DAY, NOW() - INTERVAL 210 DAY, NULL),
('lt000000-0000-0000-0000-000000000064', '44444444-4444-4444-4444-444444444444', NULL, 'Stay Strong', 'Stay strong! Life will test you, but you are stronger than any challenge. Keep fighting, keep believing! 🛡️', 'Stay strong! Life will test you, but you are stronger than any challenge. Keep fighting, keep believing! 🛡️', 'PUBLIC', FALSE, '#2A1F00', '#FFFF00', 'Patrick Hand', 15.0, NULL, NOW() - INTERVAL 212 DAY, 313, FALSE, NULL, 'en', NOW() - INTERVAL 212 DAY, NOW() - INTERVAL 212 DAY, NULL),
('lt000000-0000-0000-0000-000000000065', '55555555-5555-5555-5555-555555555555', NULL, 'Find Balance', 'Find balance in life! Work hard, but also rest. Be serious, but also laugh. Life is about harmony and balance! ⚖️', 'Find balance in life! Work hard, but also rest. Be serious, but also laugh. Life is about harmony and balance! ⚖️', 'PUBLIC', FALSE, '#0A0024', '#FFFFFF', 'Shadows Into Light', 16.0, NULL, NOW() - INTERVAL 214 DAY, 316, FALSE, NULL, 'en', NOW() - INTERVAL 214 DAY, NOW() - INTERVAL 214 DAY, NULL),
('lt000000-0000-0000-0000-000000000066', '66666666-6666-6666-6666-666666666666', NULL, 'Keep Dreaming', 'Keep dreaming big! Dreams are the seeds of reality. Water them with action and watch them grow into achievements! 🌱', 'Keep dreaming big! Dreams are the seeds of reality. Water them with action and watch them grow into achievements! 🌱', 'PUBLIC', FALSE, '#1E1A14', '#FFFFFF', 'Comic Neue', 15.0, NULL, NOW() - INTERVAL 216 DAY, 319, FALSE, NULL, 'en', NOW() - INTERVAL 216 DAY, NOW() - INTERVAL 216 DAY, NULL),
('lt000000-0000-0000-0000-000000000067', '77777777-7777-7777-7777-777777777777', NULL, 'Be Grateful', 'Be grateful for everything! Gratitude transforms ordinary days into thanksgivings. Appreciate what you have! 🙏', 'Be grateful for everything! Gratitude transforms ordinary days into thanksgivings. Appreciate what you have! 🙏', 'PUBLIC', FALSE, '#061A17', '#FFFFFF', 'Caveat', 16.0, NULL, NOW() - INTERVAL 218 DAY, 322, FALSE, NULL, 'en', NOW() - INTERVAL 218 DAY, NOW() - INTERVAL 218 DAY, NULL),
('lt000000-0000-0000-0000-000000000068', '88888888-8888-8888-8888-888888888888', NULL, 'Stay Curious', 'Stay curious about life! Questions lead to discoveries. Keep wondering, keep exploring, keep learning new things! 🔍', 'Stay curious about life! Questions lead to discoveries. Keep wondering, keep exploring, keep learning new things! 🔍', 'PUBLIC', FALSE, '#1D1433', '#FFFFFF', 'Dancing Script', 15.0, NULL, NOW() - INTERVAL 220 DAY, 325, FALSE, NULL, 'en', NOW() - INTERVAL 220 DAY, NOW() - INTERVAL 220 DAY, NULL),
('lt000000-0000-0000-0000-000000000069', '11111111-1111-1111-1111-111111111111', NULL, 'Make Memories', 'Make memories that last! Experiences are more valuable than things. Collect moments, not possessions. Live fully! 📸', 'Make memories that last! Experiences are more valuable than things. Collect moments, not possessions. Live fully! 📸', 'PUBLIC', FALSE, '#001133', '#FFFFFF', 'Permanent Marker', 16.0, NULL, NOW() - INTERVAL 222 DAY, 328, FALSE, NULL, 'en', NOW() - INTERVAL 222 DAY, NOW() - INTERVAL 222 DAY, NULL),
('lt000000-0000-0000-0000-00000000006a', '22222222-2222-2222-2222-222222222222', NULL, 'Stay Positive', 'Stay positive! Your attitude determines your altitude. Choose optimism, and watch how it transforms your life! 🌈', 'Stay positive! Your attitude determines your altitude. Choose optimism, and watch how it transforms your life! 🌈', 'PUBLIC', FALSE, '#210014', '#FFFFFF', 'Indie Flower', 15.0, NULL, NOW() - INTERVAL 224 DAY, 331, FALSE, NULL, 'en', NOW() - INTERVAL 224 DAY, NOW() - INTERVAL 224 DAY, NULL),
('lt000000-0000-0000-0000-00000000006b', '33333333-3333-3333-3333-333333333333', NULL, 'Be Kind', 'Be kind to everyone! Kindness is free, but it''s worth everything. Spread love and watch it come back to you! 💝', 'Be kind to everyone! Kindness is free, but it''s worth everything. Spread love and watch it come back to you! 💝', 'PUBLIC', FALSE, '#001100', '#00FF00', 'Kalam', 16.0, NULL, NOW() - INTERVAL 226 DAY, 334, FALSE, NULL, 'en', NOW() - INTERVAL 226 DAY, NOW() - INTERVAL 226 DAY, NULL),
('lt000000-0000-0000-0000-00000000006c', '44444444-4444-4444-4444-444444444444', NULL, 'Keep Going', 'Keep going! The journey might be long, but every step brings you closer to your destination. Don''t stop now! 🚶', 'Keep going! The journey might be long, but every step brings you closer to your destination. Don''t stop now! 🚶', 'PUBLIC', FALSE, '#000000', '#00CC00', 'Patrick Hand', 15.0, NULL, NOW() - INTERVAL 228 DAY, 337, FALSE, NULL, 'en', NOW() - INTERVAL 228 DAY, NOW() - INTERVAL 228 DAY, NULL),
('lt000000-0000-0000-0000-00000000006d', '55555555-5555-5555-5555-555555555555', NULL, 'Believe in You', 'Believe in yourself! You are capable of amazing things. Trust your abilities and watch yourself soar! 🦅', 'Believe in yourself! You are capable of amazing things. Trust your abilities and watch yourself soar! 🦅', 'PUBLIC', FALSE, '#1A0016', '#FF00FF', 'Shadows Into Light', 16.0, NULL, NOW() - INTERVAL 230 DAY, 340, FALSE, NULL, 'en', NOW() - INTERVAL 230 DAY, NOW() - INTERVAL 230 DAY, NULL),
('lt000000-0000-0000-0000-00000000006e', '66666666-6666-6666-6666-666666666666', NULL, 'Stay Humble', 'Stay humble but never stop reaching! Humility keeps you grounded while ambition keeps you growing. Balance is key! ⚖️', 'Stay humble but never stop reaching! Humility keeps you grounded while ambition keeps you growing. Balance is key! ⚖️', 'PUBLIC', FALSE, '#2A1F00', '#FFFF00', 'Comic Neue', 15.0, NULL, NOW() - INTERVAL 232 DAY, 343, FALSE, NULL, 'en', NOW() - INTERVAL 232 DAY, NOW() - INTERVAL 232 DAY, NULL),
('lt000000-0000-0000-0000-00000000006f', '77777777-7777-7777-7777-777777777777', NULL, 'Create Impact', 'Create impact! Your actions matter. Make choices that positively affect others. Leave the world better than you found it! 🌍', 'Create impact! Your actions matter. Make choices that positively affect others. Leave the world better than you found it! 🌍', 'PUBLIC', FALSE, '#0A0024', '#FFFFFF', 'Caveat', 16.0, NULL, NOW() - INTERVAL 234 DAY, 346, FALSE, NULL, 'en', NOW() - INTERVAL 234 DAY, NOW() - INTERVAL 234 DAY, NULL),
('lt000000-0000-0000-0000-000000000070', '88888888-8888-8888-8888-888888888888', NULL, 'Stay True', 'Stay true to your values! Don''t compromise on what matters most to you. Your integrity is your greatest asset! 💎', 'Stay true to your values! Don''t compromise on what matters most to you. Your integrity is your greatest asset! 💎', 'PUBLIC', FALSE, '#1E1A14', '#FFFFFF', 'Dancing Script', 15.0, NULL, NOW() - INTERVAL 236 DAY, 349, FALSE, NULL, 'en', NOW() - INTERVAL 236 DAY, NOW() - INTERVAL 236 DAY, NULL),
('lt000000-0000-0000-0000-000000000071', '11111111-1111-1111-1111-111111111111', NULL, 'Find Your Why', 'Find your why! When you know why you''re doing something, the how becomes easier. Purpose drives passion! 🎯', 'Find your why! When you know why you''re doing something, the how becomes easier. Purpose drives passion! 🎯', 'PUBLIC', FALSE, '#061A17', '#FFFFFF', 'Permanent Marker', 16.0, NULL, NOW() - INTERVAL 238 DAY, 352, FALSE, NULL, 'en', NOW() - INTERVAL 238 DAY, NOW() - INTERVAL 238 DAY, NULL),
('lt000000-0000-0000-0000-000000000072', '22222222-2222-2222-2222-222222222222', NULL, 'Stay Focused', 'Stay focused on what matters! Distractions are everywhere, but your goals deserve your full attention. Keep your eyes on the prize! 👀', 'Stay focused on what matters! Distractions are everywhere, but your goals deserve your full attention. Keep your eyes on the prize! 👀', 'PUBLIC', FALSE, '#1D1433', '#FFFFFF', 'Indie Flower', 15.0, NULL, NOW() - INTERVAL 240 DAY, 355, FALSE, NULL, 'en', NOW() - INTERVAL 240 DAY, NOW() - INTERVAL 240 DAY, NULL),
('lt000000-0000-0000-0000-000000000073', '33333333-3333-3333-3333-333333333333', NULL, 'Be Grateful', 'Be grateful for today! Each day is a gift. Appreciate the little things and find joy in simple moments. Life is beautiful! 🌸', 'Be grateful for today! Each day is a gift. Appreciate the little things and find joy in simple moments. Life is beautiful! 🌸', 'PUBLIC', FALSE, '#001133', '#FFFFFF', 'Kalam', 16.0, NULL, NOW() - INTERVAL 242 DAY, 358, FALSE, NULL, 'en', NOW() - INTERVAL 242 DAY, NOW() - INTERVAL 242 DAY, NULL),
('lt000000-0000-0000-0000-000000000074', '44444444-4444-4444-4444-444444444444', NULL, 'Keep Growing', 'Keep growing every day! Personal development never stops. Every day is a chance to become better than yesterday! 🌳', 'Keep growing every day! Personal development never stops. Every day is a chance to become better than yesterday! 🌳', 'PUBLIC', FALSE, '#210014', '#FFFFFF', 'Patrick Hand', 15.0, NULL, NOW() - INTERVAL 244 DAY, 361, FALSE, NULL, 'en', NOW() - INTERVAL 244 DAY, NOW() - INTERVAL 244 DAY, NULL),
('lt000000-0000-0000-0000-000000000075', '55555555-5555-5555-5555-555555555555', NULL, 'Stay Motivated', 'Stay motivated! Motivation is like a muscle - the more you use it, the stronger it gets. Keep pushing forward! 💪', 'Stay motivated! Motivation is like a muscle - the more you use it, the stronger it gets. Keep pushing forward! 💪', 'PUBLIC', FALSE, '#001100', '#00FF00', 'Shadows Into Light', 16.0, NULL, NOW() - INTERVAL 246 DAY, 364, FALSE, NULL, 'en', NOW() - INTERVAL 246 DAY, NOW() - INTERVAL 246 DAY, NULL),
('lt000000-0000-0000-0000-000000000076', '66666666-6666-6666-6666-666666666666', NULL, 'Make It Count', 'Make every day count! Time is precious, so use it wisely. Invest in things that bring you joy and fulfillment! ⏳', 'Make every day count! Time is precious, so use it wisely. Invest in things that bring you joy and fulfillment! ⏳', 'PUBLIC', FALSE, '#000000', '#00CC00', 'Comic Neue', 15.0, NULL, NOW() - INTERVAL 248 DAY, 367, FALSE, NULL, 'en', NOW() - INTERVAL 248 DAY, NOW() - INTERVAL 248 DAY, NULL),
('lt000000-0000-0000-0000-000000000077', '77777777-7777-7777-7777-777777777777', NULL, 'Stay Inspired', 'Stay inspired! Inspiration is everywhere - in nature, in people, in experiences. Keep your eyes open and your heart ready! 💫', 'Stay inspired! Inspiration is everywhere - in nature, in people, in experiences. Keep your eyes open and your heart ready! 💫', 'PUBLIC', FALSE, '#1A0016', '#FF00FF', 'Caveat', 16.0, NULL, NOW() - INTERVAL 250 DAY, 370, FALSE, NULL, 'en', NOW() - INTERVAL 250 DAY, NOW() - INTERVAL 250 DAY, NULL);

-- 직접 전송 편지 1 (앨리스 -> 밥, 1일 전 발송, 미읽음)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('l5555555-5555-5555-5555-555555555555', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', '밥에게', '밥아, 오늘 정말 고마웠어! 덕분에 좋은 하루였어. 다음에 또 만나자. 항상 고마워! 😊', '밥아, 오늘 정말 고마웠어! 덕분에 좋은 하루였어. 다음에 또 만나자. 항상 고마워! 😊', 'DIRECT', FALSE, 'pink', 'black', 'Arial', 14.0, NULL, NOW() - INTERVAL 1 DAY, 0, FALSE, NULL, 'ko', NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NULL);

-- 직접 전송 편지 2 (밥 -> 앨리스, 12시간 전 발송, 읽음)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('l6666666-6666-6666-6666-666666666666', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', '앨리스에게', '앨리스야, 고마워! 나도 좋은 하루였어. 네가 있어서 더 즐거웠어. 다음에 또 만나자! 🌟', '앨리스야, 고마워! 나도 좋은 하루였어. 네가 있어서 더 즐거웠어. 다음에 또 만나자! 🌟', 'DIRECT', FALSE, 'blue', 'white', 'Verdana', 14.0, NULL, NOW() - INTERVAL 12 HOUR, 0, TRUE, NOW() - INTERVAL 11 HOUR, 'ko', NOW() - INTERVAL 12 HOUR, NOW() - INTERVAL 11 HOUR, NULL);

-- 직접 전송 편지 3 (앨리스 -> 찰리, 6시간 전 발송, 미읽음)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('l7777777-7777-7777-7777-777777777777', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', '찰리에게', '찰리야, 오늘 만나서 반가웠어! 다음에 또 만나자. 재밌는 이야기 더 나누고 싶어. 🎉', '찰리야, 오늘 만나서 반가웠어! 다음에 또 만나자. 재밌는 이야기 더 나누고 싶어. 🎉', 'DIRECT', FALSE, 'pink', 'black', 'Arial', 14.0, NULL, NOW() - INTERVAL 6 HOUR, 0, FALSE, NULL, 'ko', NOW() - INTERVAL 6 HOUR, NOW() - INTERVAL 6 HOUR, NULL);

-- 직접 전송 편지 4 (찰리 -> 앨리스, 2시간 전 발송, 미읽음)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('l8888888-8888-8888-8888-888888888888', '33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', '앨리스에게', '앨리스야, 나도 반가웠어! 다음에 또 만나자. 오늘 정말 즐거웠어. 고마워! 😄', '앨리스야, 나도 반가웠어! 다음에 또 만나자. 오늘 정말 즐거웠어. 고마워! 😄', 'DIRECT', FALSE, 'white', 'black', 'Times New Roman', 14.0, NULL, NOW() - INTERVAL 2 HOUR, 0, FALSE, NULL, 'ko', NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR, NULL);

-- 직접 전송 편지 5 (밥 -> 다이애나, 1시간 전 발송, 읽음)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('l9999999-9999-9999-9999-999999999999', '22222222-2222-2222-2222-222222222222', '44444444-4444-4444-4444-444444444444', '다이애나에게', '다이애나야, 오늘 정말 고마웠어! 사랑해! 항상 네가 있어서 행복해. 💖', '다이애나야, 오늘 정말 고마웠어! 사랑해! 항상 네가 있어서 행복해. 💖', 'DIRECT', FALSE, 'red', 'white', 'Arial', 16.0, NULL, NOW() - INTERVAL 1 HOUR, 0, TRUE, NOW() - INTERVAL 30 MINUTE, 'ko', NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 30 MINUTE, NULL);

-- 직접 전송 편지 6 (다이애나 -> 밥, 30분 전 발송, 읽음)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('laaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', '밥에게', '밥아, 나도 고마워! 너도 항상 고마워. 우리 함께라서 더 행복해. ❤️', '밥아, 나도 고마워! 너도 항상 고마워. 우리 함께라서 더 행복해. ❤️', 'DIRECT', FALSE, 'purple', 'white', 'Georgia', 15.0, NULL, NOW() - INTERVAL 30 MINUTE, 0, TRUE, NOW() - INTERVAL 20 MINUTE, 'ko', NOW() - INTERVAL 30 MINUTE, NOW() - INTERVAL 20 MINUTE, NULL);

-- 직접 전송 편지 7 (이브 -> 찰리, 4시간 전 발송, 읽음)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('lbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '55555555-5555-5555-5555-555555555555', '33333333-3333-3333-3333-333333333333', '찰리에게', '찰리야, 오늘 만나서 정말 좋았어! 다음에 또 만나자. 재밌는 시간이었어. 🎊', '찰리야, 오늘 만나서 정말 좋았어! 다음에 또 만나자. 재밌는 시간이었어. 🎊', 'DIRECT', FALSE, 'green', 'black', 'Courier New', 14.0, NULL, NOW() - INTERVAL 4 HOUR, 0, TRUE, NOW() - INTERVAL 3 HOUR, 'ko', NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 3 HOUR, NULL);

-- 친구 전용 편지 1 (프랭크 -> 다이애나, 친구만 볼 수 있음, 3일 전 발송)
INSERT INTO letters (id, sender_id, recipient_id, title, content, preview, visibility, is_anonymous, template_background, template_text_color, template_font_family, template_font_size, scheduled_at, sent_at, views, is_read, read_at, language, created_at, updated_at, deleted_at) VALUES
('lccccccc-cccc-cccc-cccc-cccccccccccc', '66666666-6666-6666-6666-666666666666', NULL, '친구들에게', '친구들에게 전하는 편지입니다. 모두 건강하고 행복하길 바라요. 함께라서 행복해요! 🌈', '친구들에게 전하는 편지입니다. 모두 건강하고 행복하길 바라요. 함께라서 행복해요! 🌈', 'FRIENDS', FALSE, 'orange', 'black', 'Arial', 14.0, NULL, NOW() - INTERVAL 3 DAY, 2, FALSE, NULL, 'ko', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY, NULL);

-- ============================================
-- 4. 편지 이미지 (Letter Images)
-- ============================================

-- 공개 편지 1에 이미지 1개
INSERT INTO letter_images (id, letter_id, image_url, image_order) VALUES
('i1111111-1111-1111-1111-111111111111', 'l1111111-1111-1111-1111-111111111111', 'https://example.com/images/letter1-image1.jpg', 0);

-- 공개 편지 2에 이미지 2개
INSERT INTO letter_images (id, letter_id, image_url, image_order) VALUES
('i2222222-2222-2222-2222-222222222222', 'l2222222-2222-2222-2222-222222222222', 'https://example.com/images/letter2-image1.jpg', 0),
('i3333333-3333-3333-3333-333333333333', 'l2222222-2222-2222-2222-222222222222', 'https://example.com/images/letter2-image2.jpg', 1);

-- 직접 전송 편지 5에 이미지 1개
INSERT INTO letter_images (id, letter_id, image_url, image_order) VALUES
('i4444444-4444-4444-4444-444444444444', 'l9999999-9999-9999-9999-999999999999', 'https://example.com/images/letter5-image1.jpg', 0);

-- 공개 편지 5 (이브)에 이미지 1개
INSERT INTO letter_images (id, letter_id, image_url, image_order) VALUES
('i5555555-5555-5555-5555-555555555555', 'lddddddd-dddd-dddd-dddd-dddddddddddd', 'https://example.com/images/public-letter5-image1.jpg', 0);

-- 공개 편지 7 (프랭크)에 이미지 2개
INSERT INTO letter_images (id, letter_id, image_url, image_order) VALUES
('i6666666-6666-6666-6666-666666666666', 'lfffffff-ffff-ffff-ffff-ffffffffffff', 'https://example.com/images/public-letter7-image1.jpg', 0),
('i7777777-7777-7777-7777-777777777777', 'lfffffff-ffff-ffff-ffff-ffffffffffff', 'https://example.com/images/public-letter7-image2.jpg', 1);

-- 공개 편지 9 (그레이스)에 이미지 1개
INSERT INTO letter_images (id, letter_id, image_url, image_order) VALUES
('i8888888-8888-8888-8888-888888888888', 'lhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', 'https://example.com/images/public-letter9-image1.jpg', 0);

-- 공개 편지 13 (앨리스)에 이미지 1개
INSERT INTO letter_images (id, letter_id, image_url, image_order) VALUES
('i9999999-9999-9999-9999-999999999999', 'llllllll-llll-llll-llll-llllllllllll', 'https://example.com/images/public-letter13-image1.jpg', 0);

-- 공개 편지 15 (찰리)에 이미지 1개
INSERT INTO letter_images (id, letter_id, image_url, image_order) VALUES
('iaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'lnnnnnnn-nnnn-nnnn-nnnn-nnnnnnnnnnnn', 'https://example.com/images/public-letter15-image1.jpg', 0);

-- 공개 편지 17 (이브)에 이미지 1개
INSERT INTO letter_images (id, letter_id, image_url, image_order) VALUES
('ibbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'lppppppp-pppp-pppp-pppp-pppppppppppp', 'https://example.com/images/public-letter17-image1.jpg', 0);

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

-- 공개 편지 5 (이브) 읽은 사용자들
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('r6666666-6666-6666-6666-666666666666', 'lddddddd-dddd-dddd-dddd-dddddddddddd', '11111111-1111-1111-1111-111111111111', TRUE, NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 6 DAY, NULL),
('r7777777-7777-7777-7777-777777777777', 'lddddddd-dddd-dddd-dddd-dddddddddddd', '22222222-2222-2222-2222-222222222222', TRUE, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY, NULL),
('r8888888-8888-8888-8888-888888888888', 'lddddddd-dddd-dddd-dddd-dddddddddddd', '33333333-3333-3333-3333-333333333333', FALSE, NULL, NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY, NULL);

-- 공개 편지 6 (이브, 익명) 읽은 사용자들
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('r9999999-9999-9999-9999-999999999999', 'leeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '44444444-4444-4444-4444-444444444444', TRUE, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY, NULL),
('raaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'leeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '66666666-6666-6666-6666-666666666666', TRUE, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, NULL);

-- 공개 편지 7 (프랭크) 읽은 사용자들
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('rbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'lfffffff-ffff-ffff-ffff-ffffffffffff', '11111111-1111-1111-1111-111111111111', TRUE, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY, NULL),
('rccccccc-cccc-cccc-cccc-cccccccccccc', 'lfffffff-ffff-ffff-ffff-ffffffffffff', '22222222-2222-2222-2222-222222222222', TRUE, NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY, NULL),
('rddddddd-dddd-dddd-dddd-dddddddddddd', 'lfffffff-ffff-ffff-ffff-ffffffffffff', '33333333-3333-3333-3333-333333333333', TRUE, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY, NULL),
('reeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'lfffffff-ffff-ffff-ffff-ffffffffffff', '44444444-4444-4444-4444-444444444444', FALSE, NULL, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, NULL);

-- 공개 편지 8 (프랭크, 익명) 읽은 사용자들
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('rfffffff-ffff-ffff-ffff-ffffffffffff', 'lggggggg-gggg-gggg-gggg-gggggggggggg', '55555555-5555-5555-5555-555555555555', TRUE, NOW() - INTERVAL 8 HOUR, NOW() - INTERVAL 8 HOUR, NOW() - INTERVAL 8 HOUR, NULL),
('rggggggg-gggg-gggg-gggg-gggggggggggg', 'lggggggg-gggg-gggg-gggg-gggggggggggg', '77777777-7777-7777-7777-777777777777', TRUE, NOW() - INTERVAL 7 HOUR, NOW() - INTERVAL 7 HOUR, NOW() - INTERVAL 7 HOUR, NULL);

-- 공개 편지 9 (그레이스) 읽은 사용자들
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('rhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', 'lhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', '11111111-1111-1111-1111-111111111111', TRUE, NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 7 DAY, NULL),
('riiiiiii-iiii-iiii-iiii-iiiiiiiiiiii', 'lhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', '33333333-3333-3333-3333-333333333333', TRUE, NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 6 DAY, NULL),
('rjjjjjjj-jjjj-jjjj-jjjj-jjjjjjjjjjjj', 'lhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', '55555555-5555-5555-5555-555555555555', FALSE, NULL, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY, NULL);

-- 공개 편지 10 (그레이스) 읽은 사용자들
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('rkkkkkkk-kkkk-kkkk-kkkk-kkkkkkkkkkkk', 'liiiiiii-iiii-iiii-iiii-iiiiiiiiiiii', '22222222-2222-2222-2222-222222222222', TRUE, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR, NULL),
('rlllllll-llll-llll-llll-llllllllllll', 'liiiiiii-iiii-iiii-iiii-iiiiiiiiiiii', '44444444-4444-4444-4444-444444444444', FALSE, NULL, NOW() - INTERVAL 30 MINUTE, NOW() - INTERVAL 30 MINUTE, NULL);

-- 공개 편지 11 (헨리) 읽은 사용자들
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('rmmmmmmm-mmmm-mmmm-mmmm-mmmmmmmmmmmm', 'ljjjjjjj-jjjj-jjjj-jjjj-jjjjjjjjjjjj', '11111111-1111-1111-1111-111111111111', TRUE, NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY, NULL),
('rnnnnnnn-nnnn-nnnn-nnnn-nnnnnnnnnnnn', 'ljjjjjjj-jjjj-jjjj-jjjj-jjjjjjjjjjjj', '33333333-3333-3333-3333-333333333333', TRUE, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY, NULL),
('rooooooo-oooo-oooo-oooo-oooooooooooo', 'ljjjjjjj-jjjj-jjjj-jjjj-jjjjjjjjjjjj', '66666666-6666-6666-6666-666666666666', FALSE, NULL, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, NULL);

-- 공개 편지 12 (헨리, 익명) 읽은 사용자들
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('rppppppp-pppp-pppp-pppp-pppppppppppp', 'lkkkkkkk-kkkk-kkkk-kkkk-kkkkkkkkkkkk', '22222222-2222-2222-2222-222222222222', TRUE, NOW() - INTERVAL 50 MINUTE, NOW() - INTERVAL 50 MINUTE, NOW() - INTERVAL 50 MINUTE, NULL);

-- 공개 편지 13 (앨리스) 읽은 사용자들
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('rqqqqqqq-qqqq-qqqq-qqqq-qqqqqqqqqqqq', 'llllllll-llll-llll-llll-llllllllllll', '22222222-2222-2222-2222-222222222222', TRUE, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, NULL),
('rrrrrrrr-rrrr-rrrr-rrrr-rrrrrrrrrrrr', 'llllllll-llll-llll-llll-llllllllllll', '44444444-4444-4444-4444-444444444444', TRUE, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NULL),
('rsssssss-ssss-ssss-ssss-ssssssssssss', 'llllllll-llll-llll-llll-llllllllllll', '55555555-5555-5555-5555-555555555555', FALSE, NULL, NOW() - INTERVAL 12 HOUR, NOW() - INTERVAL 12 HOUR, NULL);

-- 공개 편지 14 (밥, 익명) 읽은 사용자들
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('rttttttt-tttt-tttt-tttt-tttttttttttt', 'lmmmmmmm-mmmm-mmmm-mmmm-mmmmmmmmmmmm', '33333333-3333-3333-3333-333333333333', TRUE, NOW() - INTERVAL 8 HOUR, NOW() - INTERVAL 8 HOUR, NOW() - INTERVAL 8 HOUR, NULL),
('ruuuuuuu-uuuu-uuuu-uuuu-uuuuuuuuuuuu', 'lmmmmmmm-mmmm-mmmm-mmmm-mmmmmmmmmmmm', '55555555-5555-5555-5555-555555555555', TRUE, NOW() - INTERVAL 7 HOUR, NOW() - INTERVAL 7 HOUR, NOW() - INTERVAL 7 HOUR, NULL),
('rvvvvvvv-vvvv-vvvv-vvvv-vvvvvvvvvvvv', 'lmmmmmmm-mmmm-mmmm-mmmm-mmmmmmmmmmmm', '77777777-7777-7777-7777-777777777777', FALSE, NULL, NOW() - INTERVAL 6 HOUR, NOW() - INTERVAL 6 HOUR, NULL);

-- 공개 편지 15 (찰리) 읽은 사용자들
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('rwwwwwww-wwww-wwww-wwww-wwwwwwwwwwww', 'lnnnnnnn-nnnn-nnnn-nnnn-nnnnnnnnnnnn', '11111111-1111-1111-1111-111111111111', TRUE, NOW() - INTERVAL 11 DAY, NOW() - INTERVAL 11 DAY, NOW() - INTERVAL 11 DAY, NULL),
('rxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx', 'lnnnnnnn-nnnn-nnnn-nnnn-nnnnnnnnnnnn', '22222222-2222-2222-2222-222222222222', TRUE, NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 10 DAY, NULL),
('ryyyyyyy-yyyy-yyyy-yyyy-yyyyyyyyyyyy', 'lnnnnnnn-nnnn-nnnn-nnnn-nnnnnnnnnnnn', '44444444-4444-4444-4444-444444444444', TRUE, NOW() - INTERVAL 9 DAY, NOW() - INTERVAL 9 DAY, NOW() - INTERVAL 9 DAY, NULL),
('rzzzzzzz-zzzz-zzzz-zzzz-zzzzzzzzzzzz', 'lnnnnnnn-nnnn-nnnn-nnnn-nnnnnnnnnnnn', '55555555-5555-5555-5555-555555555555', FALSE, NULL, NOW() - INTERVAL 8 DAY, NOW() - INTERVAL 8 DAY, NULL);

-- 공개 편지 16 (다이애나, 익명) 읽은 사용자들
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('r111111a-1111-1111-1111-111111111111', 'looooooo-oooo-oooo-oooo-oooooooooooo', '22222222-2222-2222-2222-222222222222', TRUE, NOW() - INTERVAL 7 HOUR, NOW() - INTERVAL 7 HOUR, NOW() - INTERVAL 7 HOUR, NULL),
('r222222a-2222-2222-2222-222222222222', 'looooooo-oooo-oooo-oooo-oooooooooooo', '33333333-3333-3333-3333-333333333333', TRUE, NOW() - INTERVAL 6 HOUR, NOW() - INTERVAL 6 HOUR, NOW() - INTERVAL 6 HOUR, NULL);

-- 공개 편지 17 (이브) 읽은 사용자들
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('r333333a-3333-3333-3333-333333333333', 'lppppppp-pppp-pppp-pppp-pppppppppppp', '11111111-1111-1111-1111-111111111111', FALSE, NULL, NOW() - INTERVAL 10 MINUTE, NOW() - INTERVAL 10 MINUTE, NULL);

-- 공개 편지 18 (프랭크) 읽은 사용자들
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('r444444a-4444-4444-4444-444444444444', 'lqqqqqqq-qqqq-qqqq-qqqq-qqqqqqqqqqqq', '11111111-1111-1111-1111-111111111111', TRUE, NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 10 DAY, NULL),
('r555555a-5555-5555-5555-555555555555', 'lqqqqqqq-qqqq-qqqq-qqqq-qqqqqqqqqqqq', '33333333-3333-3333-3333-333333333333', TRUE, NOW() - INTERVAL 9 DAY, NOW() - INTERVAL 9 DAY, NOW() - INTERVAL 9 DAY, NULL),
('r666666a-6666-6666-6666-666666666666', 'lqqqqqqq-qqqq-qqqq-qqqq-qqqqqqqqqqqq', '77777777-7777-7777-7777-777777777777', FALSE, NULL, NOW() - INTERVAL 8 DAY, NOW() - INTERVAL 8 DAY, NULL);

-- 공개 편지 19 (그레이스, 익명) 읽은 사용자들
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('r777777a-7777-7777-7777-777777777777', 'lrrrrrrr-rrrr-rrrr-rrrr-rrrrrrrrrrrr', '22222222-2222-2222-2222-222222222222', TRUE, NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR, NULL),
('r888888a-8888-8888-8888-888888888888', 'lrrrrrrr-rrrr-rrrr-rrrr-rrrrrrrrrrrr', '44444444-4444-4444-4444-444444444444', TRUE, NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 4 HOUR, NULL);

-- 공개 편지 20 (헨리) 읽은 사용자들
INSERT INTO letter_recipients (id, letter_id, user_id, is_read, read_at, created_at, updated_at, deleted_at) VALUES
('r999999a-9999-9999-9999-999999999999', 'lsssssss-ssss-ssss-ssss-ssssssssssss', '11111111-1111-1111-1111-111111111111', TRUE, NOW() - INTERVAL 12 DAY, NOW() - INTERVAL 12 DAY, NOW() - INTERVAL 12 DAY, NULL),
('raaaaaab-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'lsssssss-ssss-ssss-ssss-ssssssssssss', '33333333-3333-3333-3333-333333333333', TRUE, NOW() - INTERVAL 11 DAY, NOW() - INTERVAL 11 DAY, NOW() - INTERVAL 11 DAY, NULL),
('rbbbbbbc-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'lsssssss-ssss-ssss-ssss-ssssssssssss', '55555555-5555-5555-5555-555555555555', TRUE, NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 10 DAY, NULL),
('rccccccd-cccc-cccc-cccc-cccccccccccc', 'lsssssss-ssss-ssss-ssss-ssssssssssss', '66666666-6666-6666-6666-666666666666', FALSE, NULL, NOW() - INTERVAL 9 DAY, NOW() - INTERVAL 9 DAY, NULL);

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
-- 완료 메시지 및 데이터 확인
-- ============================================
SELECT 'Mock data inserted successfully!' AS message;
SELECT 'users' AS table_name, COUNT(*) AS record_count FROM users
UNION ALL
SELECT 'friendships', COUNT(*) FROM friendships
UNION ALL
SELECT 'letters', COUNT(*) FROM letters
UNION ALL
SELECT 'letter_images', COUNT(*) FROM letter_images
UNION ALL
SELECT 'letter_recipients', COUNT(*) FROM letter_recipients
UNION ALL
SELECT 'letter_reports', COUNT(*) FROM letter_reports
UNION ALL
SELECT 'invite_codes', COUNT(*) FROM invite_codes
UNION ALL
SELECT 'notifications', COUNT(*) FROM notifications
UNION ALL
SELECT 'password_reset_tokens', COUNT(*) FROM password_reset_tokens;
