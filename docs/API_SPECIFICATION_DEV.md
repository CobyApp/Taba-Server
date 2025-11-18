# Taba API 명세서 (개발 환경)

## 기본 정보

- **개발 환경 Base URL**: `https://dev.taba.asia/api/v1`
- **인증 방식**: JWT Bearer Token
- **Content-Type**: `application/json`
- **API 버전**: v1

> **참고**: 이 문서는 개발 환경용 API 명세서입니다. 프로덕션 환경은 [API_SPECIFICATION.md](API_SPECIFICATION.md)를 참고하세요.

## 공통 응답 형식

모든 API는 다음 형식으로 응답합니다:

```json
{
  "success": true,
  "data": { ... },
  "message": "성공 메시지 (선택사항)"
}
```

에러 응답:

```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "에러 메시지"
  }
}
```

## 인증

대부분의 API는 JWT 토큰 인증이 필요합니다. 요청 헤더에 다음을 포함하세요:

```
Authorization: Bearer {token}
```

---

## 1. 인증 API (`/auth`)

### 1.1 회원가입

**POST** `/auth/signup`

**인증**: 불필요

**Content-Type**: `multipart/form-data`

**Request**:
- `email`: 이메일 (필수)
- `password`: 비밀번호 (필수, 최소 8자)
- `nickname`: 닉네임 (필수, 2-50자)
- `agreeTerms`: 이용약관 동의 (필수, boolean)
- `agreePrivacy`: 개인정보처리방침 동의 (필수, boolean)
- `profileImage`: 프로필 이미지 파일 (선택사항)

**Request Body 예시** (multipart/form-data):
```
email: user@example.com
password: password123
nickname: 사용자
agreeTerms: true
agreePrivacy: true
profileImage: [파일]
```

**Response** (201 Created):
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "user": {
        "id": "uuid",
        "email": "user@example.com",
        "nickname": "사용자"
      }
  },
  "message": "회원가입이 완료되었습니다."
}
```

### 1.2 로그인

**POST** `/auth/login`

**인증**: 불필요

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": "uuid",
      "email": "user@example.com",
      "nickname": "사용자"
    }
  },
  "message": "로그인에 성공했습니다."
}
```

---

## 2. 사용자 API (`/users`)

### 2.1 내 프로필 조회

**GET** `/users/me`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "email": "user@example.com",
    "nickname": "사용자",
    "profileImageUrl": "https://dev.taba.asia/api/v1/files/{fileId}",
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

### 2.2 프로필 수정

**PATCH** `/users/me`

**인증**: 필요

**Content-Type**: `multipart/form-data`

**Request**:
- `nickname`: 닉네임 (선택사항, 2-50자)
- `profileImage`: 프로필 이미지 파일 (선택사항)

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "email": "user@example.com",
    "nickname": "수정된 닉네임",
    "profileImageUrl": "http://localhost:8080/api/v1/files/{fileId}"
  },
  "message": "프로필이 수정되었습니다."
}
```

---

## 3. 친구 API (`/friends`)

### 3.1 친구 목록 조회

**GET** `/friends`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "friends": [
      {
        "id": "uuid",
        "nickname": "친구1",
        "profileImageUrl": "https://dev.taba.asia/api/v1/files/{fileId}",
        "createdAt": "2024-01-01T00:00:00"
      }
    ]
  }
}
```

### 3.2 친구 추가 (초대 코드 사용)

**POST** `/friends`

**인증**: 필요

**Request Body**:
```json
{
  "inviteCode": "ABC123"
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "data": {
    "friend": {
      "id": "uuid",
      "nickname": "친구1",
      "profileImageUrl": "http://localhost:8080/api/v1/files/{fileId}"
    }
  },
  "message": "친구가 추가되었습니다."
}
```

### 3.3 친구 삭제

**DELETE** `/friends/{friendId}`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "message": "친구가 삭제되었습니다."
}
```

### 3.4 친구 편지 목록 조회

**GET** `/friends/{friendId}/letters`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "letters": [
      {
        "id": "uuid",
        "content": "편지 내용",
        "imageUrl": "https://dev.taba.asia/api/v1/files/{fileId}",
        "sentAt": "2024-01-01T00:00:00",
        "readAt": "2024-01-01T01:00:00"
      }
    ]
  }
}
```

---

## 4. 편지 API (`/letters`)

### 4.1 편지 작성

**POST** `/letters`

**인증**: 필요

**Content-Type**: `multipart/form-data`

**Request**:
- `recipientId`: 수신자 ID (필수)
- `content`: 편지 내용 (필수)
- `image`: 이미지 파일 (선택사항)
- `scheduledAt`: 예약 발송 시간 (선택사항, ISO 8601 형식)

**Response** (201 Created):
```json
{
  "success": true,
  "data": {
    "letter": {
      "id": "uuid",
      "content": "편지 내용",
      "imageUrl": "https://dev.taba.asia/api/v1/files/{fileId}",
      "sentAt": "2024-01-01T00:00:00"
    }
  },
  "message": "편지가 작성되었습니다."
}
```

### 4.2 편지 조회

**GET** `/letters/{letterId}`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "letter": {
      "id": "uuid",
      "content": "편지 내용",
      "imageUrl": "https://dev.taba.asia/api/v1/files/{fileId}",
      "sentAt": "2024-01-01T00:00:00",
      "readAt": "2024-01-01T01:00:00"
    }
  }
}
```

### 4.3 공개 편지 목록 조회

**GET** `/letters/public`

**인증**: 선택사항

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "letters": [
      {
        "id": "uuid",
        "content": "편지 내용",
        "imageUrl": "https://dev.taba.asia/api/v1/files/{fileId}",
        "sentAt": "2024-01-01T00:00:00",
        "author": {
          "id": "uuid",
          "nickname": "작성자"
        }
      }
    ]
  }
}
```

---

## 5. 초대 코드 API (`/invite-codes`)

### 5.1 초대 코드 생성

**POST** `/invite-codes`

**인증**: 필요

**Response** (201 Created):
```json
{
  "success": true,
  "data": {
    "code": "ABC123",
    "expiresAt": "2024-01-01T00:03:00"
  },
  "message": "초대 코드가 생성되었습니다."
}
```

### 5.2 초대 코드 조회

**GET** `/invite-codes/me`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "code": "ABC123",
    "expiresAt": "2024-01-01T00:03:00"
  }
}
```

---

## 6. 알림 API (`/notifications`)

### 6.1 알림 목록 조회

**GET** `/notifications`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "notifications": [
      {
        "id": "uuid",
        "type": "LETTER_RECEIVED",
        "message": "새 편지를 받았습니다.",
        "read": false,
        "createdAt": "2024-01-01T00:00:00"
      }
    ]
  }
}
```

### 6.2 알림 읽음 처리

**PATCH** `/notifications/{notificationId}/read`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "message": "알림이 읽음 처리되었습니다."
}
```

---

## 7. 파일 API (`/files`)

### 7.1 파일 업로드

**POST** `/files`

**인증**: 필요

**Content-Type**: `multipart/form-data`

**Request**:
- `file`: 파일 (필수, 이미지 파일만 가능)

**Response** (201 Created):
```json
{
  "success": true,
  "data": {
    "fileId": "uuid",
    "url": "https://dev.taba.asia/api/v1/files/{fileId}"
  }
}
```

### 7.2 파일 조회

**GET** `/files/{fileId}`

**인증**: 필요

**Response**: 이미지 파일 반환

---

## Swagger UI

개발 환경에서 다음 URL에서 인터랙티브 API 문서를 확인할 수 있습니다:

- **개발 서버**: https://dev.taba.asia/api/v1/swagger-ui/index.html

---

## 참고사항

### 초대 코드
- 유효 시간: **3분**
- 형식: 정확히 **6자리** 숫자+영문 조합 (예: `A1B2C3`, `9X7Y2Z`, `ABC123`)
- 대문자 영문(A-Z)과 숫자(0-9) 조합만 사용

### 편지 답장 시 자동 친구 추가
- 친구가 아닌 사용자에게 답장을 보내면 자동으로 양방향 친구 관계가 생성됩니다

