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
    "nickname": "사용자",
    "avatarUrl": "http://localhost:8080/api/v1/uploads/avatar.jpg",
    "joinedAt": "2024-01-01T00:00:00",
    "friendCount": 10,
    "sentLetters": 25
  }
}
```

### 2.2 프로필 수정

**PUT** `/users/{userId}`

**인증**: 필요 (본인만 수정 가능)

**Content-Type**: `multipart/form-data`

**Request**:
- `nickname`: 닉네임 (선택사항)
- `avatarUrl`: 기존 아바타 URL (선택사항, profileImage가 있으면 무시됨)
- `profileImage`: 프로필 이미지 파일 (선택사항)

**참고**: `profileImage`가 제공되면 자동으로 업로드되어 `avatarUrl`이 업데이트됩니다. `avatarUrl`과 `profileImage`를 동시에 제공하면 `profileImage`가 우선됩니다.

**Request Body 예시** (multipart/form-data):
```
nickname: 새 닉네임
profileImage: [파일]
```

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "email": "user@example.com",
    "nickname": "새 닉네임",
    "avatarUrl": "http://localhost:8080/api/v1/uploads/new-avatar.jpg",
    "joinedAt": "2024-01-01T00:00:00",
    "friendCount": 10,
    "sentLetters": 25
  }
}
```

### 2.3 FCM 토큰 등록

**PUT** `/users/{userId}/fcm-token`

**인증**: 필요 (본인만 수정 가능)

**Request Body**:
```json
{
  "fcmToken": "클라이언트에서 받은 FCM 토큰"
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

**참고**:
- FCM 토큰은 푸시 알림 발송에 사용됩니다
- 편지 수신 시 자동으로 푸시 알림이 발송됩니다
- 푸시 알림이 비활성화되어 있으면(`pushNotificationEnabled = false`) 푸시는 발송되지 않지만 알림은 저장됩니다

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

**필드 설명**:
- `attachedImages`: 이미지 URL 배열 (선택사항). 이미지는 먼저 `/files/upload` API로 업로드한 후 반환된 URL을 사용합니다. 순서대로 저장되며, 조회 시에도 동일한 순서로 반환됩니다.
- `scheduledAt`: 예약 발송 시간 (선택사항). 미지정 시 즉시 발송됩니다.
- `recipientId`: 수신자 ID (선택사항). `DIRECT` 편지인 경우 필수입니다.

**호환성**:
- 알 수 없는 필드(예: `flowerType`)는 자동으로 무시됩니다
- 기존 앱에서 보내는 추가 필드는 에러를 발생시키지 않습니다

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
    "visibility": "PUBLIC",
    "isAnonymous": false,
    "sentAt": "2024-01-01T00:00:00",
    "views": 0,
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
        "views": 50,
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
    "sender": {
      "id": "sender-uuid",
      "nickname": "작성자"
    },
    "visibility": "PUBLIC",
    "isAnonymous": false,
    "sentAt": "2024-01-01T00:00:00",
    "views": 51,
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

**참고**:
- `template.fontFamily`: 편지에 사용된 폰트 이름 (편지 표시 시 적용)
- `template.fontSize`: 폰트 크기
- `template.background`: 배경 색상
- `template.textColor`: 텍스트 색상

### 3.4 편지 답장 (자동 친구 추가)

**POST** `/letters/{letterId}/reply`

**인증**: 필요

**Request Body**:
```json
{
  "title": "답장 제목",
  "content": "답장 내용",
  "preview": "답장 미리보기",
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

### 3.5 편지 신고

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

### 3.6 편지 삭제

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
  "inviteCode": "A1B2C3"
}
```

**초대 코드 형식**:
- 정확히 **6자리** 숫자+영문 조합
- 대문자 영문(A-Z)과 숫자(0-9)만 사용
- 예: `A1B2C3`, `9X7Y2Z`, `ABC123`
- 대소문자 구분 없음 (자동으로 대문자로 변환됨)

**Response** (201 Created):
```json
{
  "success": true,
  "data": null,
  "message": "친구가 추가되었습니다."
}
```

**에러 코드**:
- `INVALID_INVITE_CODE`: 유효하지 않은 초대 코드 (형식 불일치, 존재하지 않음, 또는 6자리가 아님)
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
      "nickname": "친구",
      "avatarUrl": "...",
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

### 4.4 친구별 편지 목록 조회

**GET** `/friends/{friendId}/letters?page=0&size=20&sort=sentAt,desc`

**인증**: 필요

**설명**: 특정 친구와 주고받은 편지 목록을 조회합니다. Letter 테이블을 직접 조회하여 양방향 편지를 가져옵니다.

**Path Parameters**:
- `friendId`: 친구의 사용자 ID

**Query Parameters**:
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20)
- `sort`: 정렬 기준 (기본값: `sentAt,desc`)
  - 정렬 필드: `sentAt` (현재는 `sentAt`만 지원)
  - 정렬 방향: `asc`, `desc`

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "letter-uuid",
        "letter": {
          "id": "letter-uuid",
          "title": "편지 제목",
          "preview": "미리보기",
          "fontFamily": "Arial"
        },
        "sentAt": "2024-01-01T00:00:00",
        "sentByMe": false,
        "isRead": true,
        "fontFamily": "Arial"
      }
    ],
    "totalElements": 50,
    "totalPages": 3,
    "size": 20,
    "number": 0
  }
}
```

**참고**:
- `id`: 편지 ID (Letter 테이블의 ID)
- `sentByMe`: 현재 사용자가 보낸 편지인지 여부 (`boolean`)
- `isRead`: 내가 받은 편지인 경우 읽음 상태 (`boolean`), 내가 보낸 편지는 `null`
- `fontFamily`: 편지에 사용된 폰트 이름 (편지 표시용, 최상위 레벨)
- `letter.fontFamily`: 편지 요약 정보의 폰트 이름 (`LetterSummaryDto` 내부)
- 편지는 `sentAt DESC` 순으로 정렬됩니다 (ORDER BY 절에 명시됨)

---

## 5. 초대 코드 API (`/invite-codes`)

### 5.1 초대 코드 생성

**POST** `/invite-codes/generate`

**인증**: 필요

**Response** (201 Created):
```json
{
  "success": true,
  "data": {
    "code": "A1B2C3",
    "expiresAt": "2024-01-01T00:03:00",
    "remainingMinutes": 3
  }
}
```

**참고**: 
- 기존 활성 코드가 있으면 새로 생성하지 않고 기존 코드 반환
- 코드 유효 시간: **3분**
- 형식: 정확히 **6자리** 숫자+영문 조합 (예: `A1B2C3`, `9X7Y2Z`)
- 대문자 영문(A-Z)과 숫자(0-9)만 사용
- 항상 6자리로 고정 생성됨

### 5.2 현재 초대 코드 조회

**GET** `/invite-codes/current`

**인증**: 필요

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "code": "A1B2C3",
    "expiresAt": "2024-01-01T00:03:00",
    "remainingMinutes": 2
  }
}
```

**참고**: 활성 코드가 없거나 만료된 경우 `data`가 `null`일 수 있습니다.

---

## 6. 알림 API (`/notifications`)

### 6.1 알림 목록 조회

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

### 6.2 알림 읽음 처리

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

### 6.3 전체 알림 읽음 처리

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

### 6.4 알림 삭제

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

## 7. 파일 API (`/files`)

### 7.1 이미지 업로드

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
- `INVALID_INVITE_CODE`: 유효하지 않은 초대 코드 (존재하지 않음, 형식 불일치, 또는 6자리가 아님)
- `INVITE_CODE_EXPIRED`: 만료된 초대 코드
- `INVITE_CODE_ALREADY_USED`: 이미 사용된 초대 코드
- `CANNOT_USE_OWN_INVITE_CODE`: 자신의 초대 코드는 사용 불가
- `ALREADY_FRIENDS`: 이미 친구 관계
- `INVALID_REQUEST`: 잘못된 요청
- `VALIDATION_ERROR`: 입력값 검증 실패

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
- `NOT_FOUND`: 요청한 리소스를 찾을 수 없습니다

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

### 초대 코드
- 유효 시간: **3분**
- 형식: 정확히 **6자리** 숫자+영문 조합 (예: `A1B2C3`, `9X7Y2Z`, `ABC123`)
- 대문자 영문(A-Z)과 숫자(0-9) 조합만 사용
- 항상 6자리로 고정 생성됨 (길이 검증 포함)
- 친구 추가 시 초대 코드는 반드시 6자리여야 함 (형식 불일치 시 `INVALID_INVITE_CODE` 에러)
- 대소문자 구분 없음 (사용 시 자동으로 대문자로 변환)
- 기존 활성 코드가 있으면 새로 생성하지 않고 기존 코드 반환
- 만료되면 새 코드 생성 가능

### 편지 답장 시 자동 친구 추가
- 친구가 아닌 사용자에게 답장을 보내면 자동으로 양방향 친구 관계가 생성됩니다
- 이미 친구인 경우에는 친구 추가 없이 답장만 전송됩니다

### 공개 편지 목록
- 로그인한 사용자는 자신이 작성한 편지가 목록에 표시되지 않습니다
- 비로그인 사용자는 모든 공개 편지를 볼 수 있습니다

### 공개 편지 수신자 관리
- 공개 편지(`PUBLIC`)는 여러 사용자가 읽을 수 있습니다
- 공개 편지를 읽은 모든 사용자는 `letter_recipients` 테이블에 기록됩니다
- 각 사용자별로 읽음 상태(`isRead`)와 읽은 시간(`readAt`)이 관리됩니다
- 편지 상세 조회 시 자동으로 `LetterRecipient` 레코드가 생성/업데이트됩니다

### 친구 간 편지 조회
- 친구 간 주고받은 편지는 `Letter` 테이블을 직접 조회합니다
- `sender_id`와 `recipient_id`를 이용하여 양방향 편지를 조회합니다
- `visibility = 'DIRECT'`인 편지만 조회됩니다
- 읽음 상태는 `Letter.isRead` 필드로 관리됩니다 (recipient 기준)
- 편지 상세 조회 시 자동으로 읽음 처리됩니다

### 파일 업로드
- 모든 이미지는 로컬 `uploads/` 폴더에 저장됩니다
- 업로드된 파일은 `/uploads/{파일명}` 경로로 접근 가능합니다
- 인증 없이도 업로드된 파일에 접근할 수 있습니다

---

**문서 버전**: 1.4.0  
**최종 업데이트**: 2025-01-18

**주요 변경사항**:
- 회원가입 시 프로필 이미지 업로드 지원 (multipart/form-data)
- 프로필 수정 시 프로필 이미지 업로드 지원 (multipart/form-data)
- 편지 작성/답장 시 이미지 첨부 지원 (다중 이미지, 순서 보장)
- 공개 편지 복수 수신자 지원 (LetterRecipient 엔티티 추가)
- 공개 편지 읽음 상태 개별 관리 (사용자별 읽음 상태 추적)
- 꽃다발(bouquet) 기능 제거
- 꽃 종류(flowerType) 제거 (알 수 없는 필드 자동 무시 처리)
- username, statusMessage 필드 제거
- 초대 코드 형식: 정확히 6자리 숫자+영문 조합으로 고정 (길이 검증 추가)
- 초대 코드 사용 시 6자리 검증 추가
- FCM 푸시 알림 지원 추가
- 친구 API에 편지 목록 조회 추가 (`GET /friends/{friendId}/letters`)
- 친구별 편지 목록 조회 API에 ORDER BY 절 추가 (정렬 안정성 개선)
- 친구별 편지 목록 API 응답 형식 명확화 (`SharedFlowerDto` 구조)
- Spring Security 예외 처리 개선 (NoResourceFoundException 처리)
- JwtAuthenticationFilter 최적화 (permitAll 경로 처리 개선)

