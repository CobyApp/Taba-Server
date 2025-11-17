# Taba API 명세서

## 기본 정보

- **Base URL**: `http://localhost:8080/api/v1`
- **인증 방식**: JWT Bearer Token
- **Content-Type**: `application/json`
- **API 버전**: v1

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

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123",
  "nickname": "사용자",
  "agreeTerms": true,
  "agreePrivacy": true
}
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
      "username": "user1234567890",
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
      "username": "user1234567890",
      "nickname": "사용자"
    }
  }
}
```

### 1.3 비밀번호 찾기

**POST** `/auth/forgot-password`

**인증**: 불필요

**Request Body**:
```json
{
  "email": "user@example.com"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": null,
  "message": "비밀번호 재설정 링크가 이메일로 전송되었습니다."
}
```

### 1.4 비밀번호 재설정

**POST** `/auth/reset-password`

**인증**: 불필요

**Request Body**:
```json
{
  "token": "reset-token-from-email",
  "newPassword": "newpassword123"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": null,
  "message": "비밀번호가 재설정되었습니다."
}
```

### 1.5 비밀번호 변경

**PUT** `/auth/change-password`

**인증**: 필요

**Request Body**:
```json
{
  "currentPassword": "oldpassword123",
  "newPassword": "newpassword123"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": null,
  "message": "비밀번호가 변경되었습니다."
}
```

### 1.6 로그아웃

**POST** `/auth/logout`

**인증**: 필요

**Request Headers**:
```
Authorization: Bearer {token}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": null,
  "message": "로그아웃되었습니다."
}
```

---

## 2. 사용자 API (`/users`)

### 2.1 프로필 조회

**GET** `/users/{userId}`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "email": "user@example.com",
    "username": "user1234567890",
    "nickname": "사용자",
    "avatarUrl": "http://localhost:8080/api/v1/uploads/avatar.jpg",
    "statusMessage": "상태 메시지",
    "joinedAt": "2024-01-01T00:00:00",
    "friendCount": 10,
    "sentLetters": 25
  }
}
```

### 2.2 프로필 수정

**PUT** `/users/{userId}`

**인증**: 필요 (본인만 수정 가능)

**Request Body**:
```json
{
  "nickname": "새 닉네임",
  "statusMessage": "새 상태 메시지",
  "avatarUrl": "http://localhost:8080/api/v1/uploads/new-avatar.jpg"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "email": "user@example.com",
    "username": "user1234567890",
    "nickname": "새 닉네임",
    "avatarUrl": "http://localhost:8080/api/v1/uploads/new-avatar.jpg",
    "statusMessage": "새 상태 메시지",
    "joinedAt": "2024-01-01T00:00:00",
    "friendCount": 10,
    "sentLetters": 25
  }
}
```

---

## 3. 편지 API (`/letters`)

### 3.1 편지 작성

**POST** `/letters`

**인증**: 필요

**Request Body**:
```json
{
  "title": "편지 제목",
  "content": "편지 내용",
  "preview": "미리보기 텍스트",
  "flowerType": "ROSE",
  "visibility": "PUBLIC",
  "isAnonymous": false,
  "template": {
    "background": "pink",
    "textColor": "black",
    "fontFamily": "Arial",
    "fontSize": 14.0
  },
  "attachedImages": [
    "http://localhost:8080/api/v1/uploads/image1.jpg",
    "http://localhost:8080/api/v1/uploads/image2.jpg"
  ],
  "scheduledAt": "2024-12-25T10:00:00",
  "recipientId": "recipient-uuid"
}
```

**FlowerType**: `ROSE`, `TULIP`, `SAKURA`, `SUNFLOWER`, `DAISY`, `LAVENDER`

**Visibility**: `PUBLIC`, `FRIENDS`, `DIRECT`, `PRIVATE`

**Response** (201 Created):
```json
{
  "success": true,
  "data": {
    "id": "letter-uuid",
    "title": "편지 제목",
    "content": "편지 내용",
    "preview": "미리보기 텍스트",
    "sender": {
      "id": "sender-uuid",
      "nickname": "작성자"
    },
    "flowerType": "ROSE",
    "visibility": "PUBLIC",
    "isAnonymous": false,
    "sentAt": "2024-01-01T00:00:00",
    "likes": 0,
    "views": 0,
    "savedCount": 0,
    "isLiked": false,
    "isSaved": false,
    "attachedImages": [
      "http://localhost:8080/api/v1/uploads/image1.jpg"
    ],
    "template": {
      "background": "pink",
      "textColor": "black",
      "fontFamily": "Arial",
      "fontSize": 14.0
    }
  }
}
```

### 3.2 공개 편지 목록 조회 (씨앗)

**GET** `/letters/public?page=0&size=20`

**인증**: 선택사항 (로그인 시 자신이 작성한 편지 제외)

**Query Parameters**:
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20)

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "letter-uuid",
        "title": "편지 제목",
        "preview": "미리보기",
        "sender": { ... },
        "flowerType": "ROSE",
        "likes": 10,
        "views": 50,
        "isLiked": false,
        "isSaved": false,
        "attachedImages": [ ... ],
        "template": { ... }
      }
    ],
    "totalElements": 100,
    "totalPages": 5,
    "size": 20,
    "number": 0
  }
}
```

### 3.3 편지 상세 조회

**GET** `/letters/{letterId}`

**인증**: 필요 (편지 접근 권한에 따라)

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "id": "letter-uuid",
    "title": "편지 제목",
    "content": "편지 전체 내용",
    "preview": "미리보기",
    "sender": { ... },
    "flowerType": "ROSE",
    "visibility": "PUBLIC",
    "isAnonymous": false,
    "sentAt": "2024-01-01T00:00:00",
    "likes": 10,
    "views": 51,
    "savedCount": 5,
    "isLiked": true,
    "isSaved": false,
    "attachedImages": [ ... ],
    "template": { ... }
  }
}
```

### 3.4 편지 좋아요 토글

**POST** `/letters/{letterId}/like`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "id": "letter-uuid",
    "likes": 11,
    "isLiked": true,
    ...
  }
}
```

### 3.5 편지 저장 토글

**POST** `/letters/{letterId}/save`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "id": "letter-uuid",
    "savedCount": 6,
    "isSaved": true,
    ...
  }
}
```

### 3.6 편지 답장 (자동 친구 추가)

**POST** `/letters/{letterId}/reply`

**인증**: 필요

**Request Body**:
```json
{
  "title": "답장 제목",
  "content": "답장 내용",
  "preview": "답장 미리보기",
  "flowerType": "ROSE",
  "isAnonymous": false,
  "template": {
    "background": "blue",
    "textColor": "white",
    "fontFamily": "Arial",
    "fontSize": 16.0
  },
  "attachedImages": []
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "data": {
    "id": "reply-letter-uuid",
    "title": "답장 제목",
    ...
  },
  "message": "답장이 전송되었습니다. 친구가 자동으로 추가되었습니다."
}
```

**참고**: 친구가 아닌 경우 자동으로 양방향 친구 관계가 생성됩니다.

### 3.7 편지 신고

**POST** `/letters/{letterId}/report`

**인증**: 필요

**Request Body**:
```json
{
  "reason": "부적절한 내용"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": null,
  "message": "신고가 접수되었습니다."
}
```

### 3.8 편지 삭제

**DELETE** `/letters/{letterId}`

**인증**: 필요 (작성자만 삭제 가능)

**Response** (200 OK):
```json
{
  "success": true,
  "data": null,
  "message": "편지가 삭제되었습니다."
}
```

---

## 4. 친구 API (`/friends`)

### 4.1 친구 추가 (초대 코드)

**POST** `/friends/invite`

**인증**: 필요

**Request Body**:
```json
{
  "inviteCode": "user123-456789"
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "data": null,
  "message": "친구가 추가되었습니다."
}
```

**에러 코드**:
- `INVALID_INVITE_CODE`: 유효하지 않은 초대 코드
- `INVITE_CODE_EXPIRED`: 만료된 초대 코드
- `INVITE_CODE_ALREADY_USED`: 이미 사용된 초대 코드
- `CANNOT_USE_OWN_INVITE_CODE`: 자신의 초대 코드는 사용 불가
- `ALREADY_FRIENDS`: 이미 친구 관계

### 4.2 친구 목록 조회

**GET** `/friends`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": [
    {
      "id": "friend-uuid",
      "email": "friend@example.com",
      "username": "friend123",
      "nickname": "친구",
      "avatarUrl": "...",
      "statusMessage": "...",
      "joinedAt": "2024-01-01T00:00:00",
      "friendCount": 5,
      "sentLetters": 10
    }
  ]
}
```

### 4.3 친구 삭제

**DELETE** `/friends/{friendId}`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": null,
  "message": "친구 관계가 삭제되었습니다."
}
```

---

## 5. 꽃다발 API (`/bouquets`)

### 5.1 꽃다발 목록 조회

**GET** `/bouquets`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": [
    {
      "friend": {
        "id": "friend-uuid",
        "nickname": "친구",
        ...
      },
      "bloomLevel": 0.75,
      "trustScore": 50,
      "bouquetName": "친구의 꽃다발",
      "unreadCount": 3
    }
  ]
}
```

### 5.2 친구별 편지 목록 조회

**GET** `/bouquets/{friendId}/letters?page=0&size=20`

**인증**: 필요

**Query Parameters**:
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20)

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "shared-flower-uuid",
        "letter": {
          "id": "letter-uuid",
          "title": "편지 제목",
          "preview": "미리보기"
        },
        "flowerType": "ROSE",
        "sentAt": "2024-01-01T00:00:00",
        "sentByMe": false,
        "isRead": true
      }
    ],
    "totalElements": 50,
    "totalPages": 3,
    "size": 20,
    "number": 0
  }
}
```

### 5.3 꽃다발 이름 변경

**PUT** `/bouquets/{friendId}/name`

**인증**: 필요

**Request Body**:
```json
{
  "bouquetName": "새 꽃다발 이름"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": null
}
```

---

## 6. 초대 코드 API (`/invite-codes`)

### 6.1 초대 코드 생성

**POST** `/invite-codes/generate`

**인증**: 필요

**Response** (201 Created):
```json
{
  "success": true,
  "data": {
    "code": "user123-456789",
    "expiresAt": "2024-01-01T00:03:00",
    "remainingMinutes": 3
  }
}
```

**참고**: 
- 기존 활성 코드가 있으면 새로 생성하지 않고 기존 코드 반환
- 코드 유효 시간: **3분**
- 형식: `{username}-{6자리 숫자}`

### 6.2 현재 초대 코드 조회

**GET** `/invite-codes/current`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "code": "user123-456789",
    "expiresAt": "2024-01-01T00:03:00",
    "remainingMinutes": 2
  }
}
```

**참고**: 활성 코드가 없거나 만료된 경우 `data`가 `null`일 수 있습니다.

---

## 7. 알림 API (`/notifications`)

### 7.1 알림 목록 조회

**GET** `/notifications?category=LETTER&page=0&size=20`

**인증**: 필요

**Query Parameters**:
- `category`: 알림 카테고리 (선택사항)
  - `LETTER`: 편지 관련
  - `FRIEND`: 친구 관련
  - `SYSTEM`: 시스템 알림
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20)

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "notification-uuid",
        "title": "새로운 편지가 도착했습니다",
        "subtitle": "편지 제목",
        "category": "LETTER",
        "relatedId": "letter-uuid",
        "isRead": false,
        "createdAt": "2024-01-01T00:00:00",
        "readAt": null
      }
    ],
    "totalElements": 10,
    "totalPages": 1,
    "size": 20,
    "number": 0
  }
}
```

### 7.2 알림 읽음 처리

**PUT** `/notifications/{notificationId}/read`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "id": "notification-uuid",
    "isRead": true,
    "readAt": "2024-01-01T00:00:00",
    ...
  }
}
```

### 7.3 전체 알림 읽음 처리

**PUT** `/notifications/read-all`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "readCount": 5,
    "message": "모든 알림이 읽음 처리되었습니다."
  }
}
```

### 7.4 알림 삭제

**DELETE** `/notifications/{notificationId}`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": null,
  "message": "알림이 삭제되었습니다."
}
```

---

## 8. 파일 API (`/files`)

### 8.1 이미지 업로드

**POST** `/files/upload`

**인증**: 필요

**Content-Type**: `multipart/form-data`

**Request**:
```
file: [이미지 파일]
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "url": "http://localhost:8080/api/v1/uploads/uuid.jpg",
    "fileName": "original.jpg"
  }
}
```

**참고**:
- 파일은 로컬 `uploads/` 폴더에 저장됩니다
- 최대 파일 크기: 10MB
- 허용 파일 타입: `image/jpeg`, `image/png`, `image/gif`, `image/webp`
- 업로드된 파일은 `/uploads/{파일명}` 경로로 접근 가능합니다

---

## 9. 설정 API (`/settings`)

### 9.1 푸시 알림 설정 조회

**GET** `/settings/push-notification`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "enabled": true
  }
}
```

### 9.2 푸시 알림 설정 변경

**PUT** `/settings/push-notification`

**인증**: 필요

**Request Body**:
```json
{
  "enabled": false
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "enabled": false
  }
}
```

### 9.3 언어 설정 조회

**GET** `/settings/language`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "language": "ko"
  }
}
```

### 9.4 언어 설정 변경

**PUT** `/settings/language`

**인증**: 필요

**Request Body**:
```json
{
  "language": "en"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "language": "en"
  }
}
```

---

## 에러 코드

### 400 Bad Request
- `INVALID_EMAIL`: 잘못된 이메일 형식
- `EMAIL_ALREADY_EXISTS`: 이미 존재하는 이메일
- `INVALID_PASSWORD`: 잘못된 비밀번호 형식
- `INVALID_INVITE_CODE`: 유효하지 않은 초대 코드
- `INVITE_CODE_EXPIRED`: 만료된 초대 코드
- `INVITE_CODE_ALREADY_USED`: 이미 사용된 초대 코드
- `CANNOT_USE_OWN_INVITE_CODE`: 자신의 초대 코드는 사용 불가
- `ALREADY_FRIENDS`: 이미 친구 관계
- `INVALID_REQUEST`: 잘못된 요청

### 401 Unauthorized
- `UNAUTHORIZED`: 인증이 필요합니다
- `TOKEN_EXPIRED`: 토큰이 만료되었습니다
- `INVALID_TOKEN`: 유효하지 않은 토큰입니다
- `INVALID_CREDENTIALS`: 이메일 또는 비밀번호가 올바르지 않습니다

### 403 Forbidden
- `FORBIDDEN`: 권한이 없습니다
- `ACCESS_DENIED`: 접근이 거부되었습니다

### 404 Not Found
- `USER_NOT_FOUND`: 사용자를 찾을 수 없습니다
- `LETTER_NOT_FOUND`: 편지를 찾을 수 없습니다
- `FRIENDSHIP_NOT_FOUND`: 친구 관계를 찾을 수 없습니다

### 500 Internal Server Error
- `INTERNAL_SERVER_ERROR`: 서버 오류가 발생했습니다

---

## Swagger UI

애플리케이션 실행 후 다음 URL에서 인터랙티브 API 문서를 확인할 수 있습니다:

- **로컬**: http://localhost:8080/api/v1/swagger-ui/index.html
- **프로덕션**: https://api.taba.app/swagger-ui/index.html

Swagger UI에서:
1. "Authorize" 버튼 클릭
2. JWT 토큰 입력: `Bearer {your_token}`
3. 각 API의 "Try it out" 버튼으로 직접 테스트 가능

---

## 참고사항

### 친구 코드
- 유효 시간: **3분**
- 형식: `{username}-{6자리 숫자}` (예: `user123-456789`)
- 기존 활성 코드가 있으면 새로 생성하지 않고 기존 코드 반환
- 만료되면 새 코드 생성 가능

### 편지 답장 시 자동 친구 추가
- 친구가 아닌 사용자에게 답장을 보내면 자동으로 양방향 친구 관계가 생성됩니다
- 이미 친구인 경우에는 친구 추가 없이 답장만 전송됩니다

### 공개 편지 목록
- 로그인한 사용자는 자신이 작성한 편지가 목록에 표시되지 않습니다
- 비로그인 사용자는 모든 공개 편지를 볼 수 있습니다

### 파일 업로드
- 모든 이미지는 로컬 `uploads/` 폴더에 저장됩니다
- 업로드된 파일은 `/uploads/{파일명}` 경로로 접근 가능합니다
- 인증 없이도 업로드된 파일에 접근할 수 있습니다

---

**문서 버전**: 1.0.0  
**최종 업데이트**: 2024-11-17

