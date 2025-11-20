# Taba API 명세서

## 기본 정보

- **개발 환경 Base URL**: `https://dev.taba.asia/api/v1`
- **프로덕션 Base URL**: `https://www.taba.asia/api/v1`
- **인증 방식**: JWT Bearer Token
- **Content-Type**: `application/json`
- **API 버전**: v1

> **참고**: 이 문서는 개발/프로덕션 환경 공통 API 명세입니다.  
> 예시 URL은 개발 환경(`dev.taba.asia`)을 기준으로 작성되었으며, 프로덕션 환경에서는 `www.taba.asia`로 변경하여 사용하세요.

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

**GET** `/users/{userId}`

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

**PUT** `/users/{userId}`

**인증**: 필요

**Content-Type**: `multipart/form-data`

**Request**:
- `nickname`: 닉네임 (선택사항, 2-50자)
- `avatarUrl`: 아바타 URL (선택사항)
- `profileImage`: 프로필 이미지 파일 (선택사항)

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "email": "user@example.com",
    "nickname": "수정된 닉네임",
    "profileImageUrl": "https://dev.taba.asia/api/v1/files/{fileId}"
  },
  "message": "프로필이 수정되었습니다."
}
```

### 2.3 FCM 토큰 업데이트

**PUT** `/users/{userId}/fcm-token`

**인증**: 필요

**Request Body**:
```json
{
  "fcmToken": "fcm_token_string"
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": null,
  "message": "FCM 토큰이 등록되었습니다."
}
```

### 2.4 회원탈퇴

**DELETE** `/users/{userId}`

**인증**: 필요

**참고사항**:
- 본인만 탈퇴할 수 있습니다.
- 회원탈퇴 시 사용자 정보는 소프트 삭제됩니다.
- 탈퇴 후 해당 계정으로 로그인할 수 없습니다.

**Response** (200 OK):
```json
{
  "success": true,
  "data": null,
  "message": "회원탈퇴가 완료되었습니다."
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

**POST** `/friends/invite`

**인증**: 필요

**Request Body**:
```json
{
  "inviteCode": "ABC123"
}
```

**참고사항**:
- 올바르지 않은 초대 코드인 경우: `INVALID_INVITE_CODE` 에러 반환
- 만료된 초대 코드인 경우: `INVITE_CODE_EXPIRED` 에러 반환
- 이미 사용된 초대 코드인 경우: `INVITE_CODE_ALREADY_USED` 에러 반환
- 자신의 초대 코드를 사용한 경우: 본인 정보와 함께 "본인은 친구 추가할 수 없습니다." 메시지 반환 (201 Created)
- 이미 친구인 경우: 친구 정보와 함께 "이미 친구입니다." 메시지 반환 (201 Created)
- 새로운 친구인 경우: 친구 정보와 함께 "친구가 추가되었습니다." 메시지 반환 (201 Created)

**Response** (201 Created) - 새로운 친구 추가 성공:
```json
{
  "success": true,
  "data": {
    "friend": {
      "id": "uuid",
      "email": "friend@example.com",
      "nickname": "친구1",
      "avatarUrl": "https://dev.taba.asia/api/v1/files/{fileId}",
      "joinedAt": "2024-01-01T00:00:00",
      "friendCount": 5,
      "sentLetters": 10
    },
    "alreadyFriends": false,
    "isOwnCode": false
  },
  "message": "친구가 추가되었습니다."
}
```

**Response** (201 Created) - 자신의 초대 코드를 사용한 경우:
```json
{
  "success": true,
  "data": {
    "friend": {
      "id": "uuid",
      "email": "user@example.com",
      "nickname": "본인",
      "avatarUrl": "https://dev.taba.asia/api/v1/files/{fileId}",
      "joinedAt": "2024-01-01T00:00:00",
      "friendCount": 5,
      "sentLetters": 10
    },
    "alreadyFriends": false,
    "isOwnCode": true
  },
  "message": "본인은 친구 추가할 수 없습니다."
}
```

**Response** (201 Created) - 이미 친구인 경우:
```json
{
  "success": true,
  "data": {
    "friend": {
      "id": "uuid",
      "email": "friend@example.com",
      "nickname": "친구1",
      "avatarUrl": "https://dev.taba.asia/api/v1/files/{fileId}",
      "joinedAt": "2024-01-01T00:00:00",
      "friendCount": 5,
      "sentLetters": 10
    },
    "alreadyFriends": true,
    "isOwnCode": false
  },
  "message": "이미 친구입니다."
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

**Content-Type**: `application/json`

**Request Body**:
```json
{
  "title": "편지 제목",
  "content": "편지 내용",
  "preview": "편지 미리보기",
  "visibility": "PUBLIC",
  "template": {
    "background": "#1D1433",
    "textColor": "#FFFFFF",
    "fontFamily": "Jua",
    "fontSize": 16.0
  },
  "attachedImages": ["https://dev.taba.asia/api/v1/files/{fileId}"],
  "scheduledAt": "2024-01-01T12:00:00",
  "recipientId": "uuid",
  "language": "ko"
}
```

**참고사항**:
- `visibility`: `PUBLIC`, `FRIENDS`, `DIRECT`, `PRIVATE` 중 하나 (필수)
- **익명 기능은 제거되었습니다.** 모든 편지는 작성자 정보가 표시됩니다.
- `recipientId`: 직접 전송(`DIRECT`) 편지인 경우 필수, 공개 편지인 경우 선택사항
- `language`: 편지 언어 (선택사항). `ko` (한국어), `en` (영어), `ja` (일본어) 중 하나

**Response** (201 Created):
```json
{
  "success": true,
  "data": {
    "letter": {
      "id": "uuid",
      "title": "편지 제목",
      "content": "편지 내용",
      "preview": "편지 미리보기",
      "sender": {
        "id": "uuid",
        "nickname": "작성자",
        "avatarUrl": "https://dev.taba.asia/api/v1/files/{fileId}"
      },
      "visibility": "PUBLIC",
      "sentAt": "2024-01-01T00:00:00",
      "views": 0,
      "attachedImages": ["https://dev.taba.asia/api/v1/files/{fileId}"],
      "template": {
        "background": "#1D1433",
        "textColor": "#FFFFFF",
        "fontFamily": "Jua",
        "fontSize": 16.0
      },
      "language": "ko"
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

**Query Parameters**:
- `languages` (선택사항): 언어 필터링. 여러 언어를 선택하려면 같은 파라미터를 여러 번 사용하세요.
  - 예: `?languages=ko&languages=en` (한국어와 영어)
  - 가능한 값: `ko` (한국어), `en` (영어), `ja` (일본어)
  - 생략 시 모든 언어의 편지를 조회합니다.

**참고사항**:
- 모든 편지는 작성자 정보가 표시됩니다 (익명 기능 제거)
- 로그인한 사용자의 경우 자신이 작성한 편지는 목록에서 제외됩니다
- 언어 필터링은 여러 언어를 동시에 선택할 수 있습니다 (예: 한국어와 영어)

**Request 예시**:
```
GET /letters/public?languages=ko&languages=en&page=0&size=20
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "letters": [
      {
        "id": "uuid",
        "title": "편지 제목",
        "content": "편지 내용",
        "preview": "편지 미리보기",
        "sentAt": "2024-01-01T00:00:00",
        "views": 10,
        "sender": {
          "id": "uuid",
          "nickname": "작성자",
          "profileImageUrl": "https://dev.taba.asia/api/v1/files/{fileId}"
        },
        "attachedImages": ["https://dev.taba.asia/api/v1/files/{fileId}"],
        "template": {
          "background": "#1D1433",
          "textColor": "#FFFFFF",
          "fontFamily": "Jua",
          "fontSize": 16.0
        },
        "language": "ko"
      }
    ]
  }
}
```

### 4.4 편지 답장

**POST** `/letters/{letterId}/reply`

**인증**: 필요

**Request Body**:
```json
{
  "title": "답장 제목",
  "content": "답장 내용",
  "preview": "답장 미리보기",
  "template": {
    "background": "#1D1433",
    "textColor": "#FFFFFF",
    "fontFamily": "Jua",
    "fontSize": 16.0
  },
  "attachedImages": ["https://dev.taba.asia/api/v1/files/{fileId}"],
  "scheduledAt": "2024-01-01T12:00:00"
}
```

**참고사항**:
- `recipientId`는 필요하지 않습니다. 원본 편지의 작성자가 자동으로 수신자가 됩니다.
- 공개 편지, 친구 전용 편지, 직접 전송 편지 모두에 답장 가능합니다.
- 답장은 항상 `DIRECT` 타입으로 생성됩니다.
- 친구가 아닌 사용자에게 답장을 보내면 자동으로 양방향 친구 관계가 생성됩니다.
- 자기 자신에게 답장할 수 없습니다.

**Response** (201 Created):
```json
{
  "success": true,
  "data": {
    "letter": {
      "id": "uuid",
      "title": "답장 제목",
      "content": "답장 내용",
      "preview": "답장 미리보기",
      "sentAt": "2024-01-01T00:00:00"
    }
  },
  "message": "답장이 전송되었습니다. 친구가 자동으로 추가되었습니다."
}
```

---

## 5. 초대 코드 API (`/invite-codes`)

### 5.1 초대 코드 생성

**POST** `/invite-codes/generate`

**인증**: 필요

**참고사항**:
- 기존에 유효한 초대 코드가 있으면 새로 생성하지 않고 기존 코드를 반환합니다.
- 초대 코드는 생성 후 3분간 유효합니다.

**Response** (201 Created):
```json
{
  "success": true,
  "data": {
    "code": "ABC123",
    "expiresAt": "2024-01-01T00:03:00",
    "remainingMinutes": 3
  },
  "message": "초대 코드가 생성되었습니다."
}
```

### 5.2 현재 초대 코드 조회

**GET** `/invite-codes/current`

**인증**: 필요

**참고사항**:
- 현재 유효한 초대 코드가 없거나 만료된 경우 `null`을 반환합니다.

**Response** (200 OK) - 유효한 코드가 있는 경우:
```json
{
  "success": true,
  "data": {
    "code": "ABC123",
    "expiresAt": "2024-01-01T00:03:00",
    "remainingMinutes": 2
  }
}
```

**Response** (200 OK) - 유효한 코드가 없는 경우:
```json
{
  "success": true,
  "data": null
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

다음 URL에서 인터랙티브 API 문서를 확인할 수 있습니다:

- **개발 환경**: https://dev.taba.asia/api/v1/swagger-ui/index.html
- **프로덕션 환경**: https://www.taba.asia/api/v1/swagger-ui/index.html

---

## 참고사항

### 초대 코드
- 유효 시간: **3분**
- 형식: 정확히 **6자리** 숫자+영문 조합 (예: `A1B2C3`, `9X7Y2Z`, `ABC123`)
- 대문자 영문(A-Z)과 숫자(0-9) 조합만 사용

### 편지 답장 시 자동 친구 추가
- 친구가 아닌 사용자에게 답장을 보내면 자동으로 양방향 친구 관계가 생성됩니다

