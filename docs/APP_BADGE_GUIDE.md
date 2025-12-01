# 앱 뱃지 구현 가이드

## 개요

앱 뱃지는 읽지 않은 알림 개수를 표시하며, iOS와 Android 모두 지원합니다.

## 백엔드 API

### 1. 뱃지 동기화
**POST** `/notifications/badge/sync`

앱이 포그라운드로 올라오거나 알림 목록 화면 진입 시 호출하여 뱃지를 현재 읽지 않은 알림 개수로 동기화합니다.

**Response**:
```json
{
  "success": true,
  "data": {
    "unreadCount": 3
  }
}
```

### 2. 읽지 않은 알림 개수 조회
**GET** `/notifications/unread-count`

현재 읽지 않은 알림 개수를 조회합니다.

## 앱에서 구현해야 할 사항

### iOS (Swift)

#### 1. 앱 포그라운드 진입 시 뱃지 동기화

```swift
// AppDelegate.swift 또는 SceneDelegate.swift
func applicationDidBecomeActive(_ application: UIApplication) {
    // 뱃지 동기화 API 호출
    syncBadge()
}

func sceneDidBecomeActive(_ scene: UIScene) {
    // 뱃지 동기화 API 호출
    syncBadge()
}

func syncBadge() {
    guard let token = UserDefaults.standard.string(forKey: "authToken") else { return }
    
    let url = URL(string: "https://dev.taba.asia/api/v1/notifications/badge/sync")!
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
    
    URLSession.shared.dataTask(with: request) { data, response, error in
        if let data = data,
           let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
           let dataDict = json["data"] as? [String: Any],
           let unreadCount = dataDict["unreadCount"] as? Int {
            DispatchQueue.main.async {
                // iOS 뱃지 업데이트
                UIApplication.shared.applicationIconBadgeNumber = unreadCount
            }
        }
    }.resume()
}
```

#### 2. 알림 목록 화면 진입 시 뱃지 동기화

```swift
// NotificationListViewController.swift
override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    syncBadge()
}
```

#### 3. FCM 푸시 알림 수신 시 뱃지 업데이트

```swift
// AppDelegate.swift
func userNotificationCenter(_ center: UNUserNotificationCenter, 
                          willPresent notification: UNNotification,
                          withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
    // 포그라운드에서 알림 수신 시
    let userInfo = notification.request.content.userInfo
    
    // data payload에서 badge 값 추출
    if let badgeString = userInfo["badge"] as? String,
       let badge = Int(badgeString) {
        DispatchQueue.main.async {
            UIApplication.shared.applicationIconBadgeNumber = badge
        }
    }
    
    completionHandler([.alert, .sound, .badge])
}

func userNotificationCenter(_ center: UNUserNotificationCenter,
                          didReceive response: UNNotificationResponse,
                          withCompletionHandler completionHandler: @escaping () -> Void) {
    // 백그라운드에서 알림 탭 시
    let userInfo = response.notification.request.content.userInfo
    
    // data payload에서 badge 값 추출
    if let badgeString = userInfo["badge"] as? String,
       let badge = Int(badgeString) {
        DispatchQueue.main.async {
            UIApplication.shared.applicationIconBadgeNumber = badge
        }
    }
    
    completionHandler()
}
```

#### 4. FCM 메시지 수신 처리 (Firebase Messaging)

```swift
// AppDelegate.swift
extension AppDelegate: MessagingDelegate {
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        // FCM 토큰을 서버에 등록
        if let token = fcmToken {
            registerFCMToken(token)
        }
    }
    
    func messaging(_ messaging: Messaging, didReceiveMessage remoteMessage: MessagingRemoteMessage) {
        // 포그라운드에서 메시지 수신 시
        let userInfo = remoteMessage.appData
        
        // 뱃지 업데이트만 하는 경우 (type: "badge_update")
        if let type = userInfo["type"] as? String, type == "badge_update",
           let badgeString = userInfo["badge"] as? String,
           let badge = Int(badgeString) {
            DispatchQueue.main.async {
                UIApplication.shared.applicationIconBadgeNumber = badge
            }
        }
    }
}
```

### Android (Kotlin)

#### 1. 앱 포그라운드 진입 시 뱃지 동기화

```kotlin
// MainActivity.kt 또는 Application.kt
class MainActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        syncBadge()
    }
    
    private fun syncBadge() {
        val token = getAuthToken() ?: return
        
        val url = "https://dev.taba.asia/api/v1/notifications/badge/sync"
        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create(null, ByteArray(0)))
            .addHeader("Authorization", "Bearer $token")
            .build()
        
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body()?.string() ?: "")
                val unreadCount = json.getJSONObject("data").getInt("unreadCount")
                
                runOnUiThread {
                    // Android 뱃지 업데이트
                    updateBadge(unreadCount)
                }
            }
            
            override fun onFailure(call: Call, e: IOException) {
                // 에러 처리
            }
        })
    }
    
    private fun updateBadge(count: Int) {
        // ShortcutBadger 라이브러리 사용 예시
        ShortcutBadger.applyCount(this, count)
        
        // 또는 NotificationManager를 사용한 방법
        // createNotificationChannel()
        // val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // notificationManager.cancelAll() // 기존 알림 제거
    }
}
```

#### 2. 알림 목록 화면 진입 시 뱃지 동기화

```kotlin
// NotificationListActivity.kt
override fun onResume() {
    super.onResume()
    syncBadge()
}
```

#### 3. FCM 메시지 수신 처리

```kotlin
// MyFirebaseMessagingService.kt
class MyFirebaseMessagingService : FirebaseMessagingService() {
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 포그라운드에서 메시지 수신 시
        val data = remoteMessage.data
        
        // 뱃지 업데이트만 하는 경우
        if (data["type"] == "badge_update") {
            val badge = data["badge"]?.toIntOrNull() ?: 0
            updateBadge(badge)
        } else {
            // 일반 알림 처리
            handleNotification(remoteMessage)
        }
    }
    
    override fun onNewToken(token: String) {
        // FCM 토큰 갱신 시 서버에 등록
        registerFCMToken(token)
    }
    
    private fun updateBadge(count: Int) {
        // ShortcutBadger 라이브러리 사용
        ShortcutBadger.applyCount(this, count)
    }
}
```

#### 4. Android 뱃지 라이브러리 추가

**build.gradle (Module: app)**
```gradle
dependencies {
    // ShortcutBadger 라이브러리 (뱃지 표시용)
    implementation 'me.leolin:ShortcutBadger:1.1.22@aar'
    
    // 또는 NotificationManager 사용
    // Android 기본 API 사용 가능
}
```

#### 5. 알림 채널 생성 (Android 8.0 이상)

```kotlin
// NotificationHelper.kt
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "taba_notifications",
            "Taba 알림",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Taba 앱 알림 채널"
            enableVibration(true)
            setShowBadge(true)
        }
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
```

## 주요 구현 포인트

### 1. 뱃지 동기화 시점
- ✅ 앱이 포그라운드로 올라올 때 (`onResume`, `applicationDidBecomeActive`)
- ✅ 알림 목록 화면 진입 시 (`viewWillAppear`, `onResume`)
- ✅ FCM 푸시 알림 수신 시 (자동 처리)

### 2. FCM Data Payload 처리
- **일반 알림**: `notificationId`, `category`, `relatedId`, `deepLink`, `badge` 포함
- **뱃지 업데이트만**: `type: "badge_update"`, `badge` 포함

### 3. 뱃지 숫자 업데이트
- **iOS**: `UIApplication.shared.applicationIconBadgeNumber = count`
- **Android**: `ShortcutBadger.applyCount(context, count)` 또는 NotificationManager 사용

### 4. 에러 처리
- 네트워크 에러 시 재시도 로직 구현
- FCM 토큰 만료 시 재등록
- 뱃지 업데이트 실패 시 로그 기록

## 테스트 체크리스트

- [ ] 앱 포그라운드 진입 시 뱃지가 올바르게 동기화되는가?
- [ ] 알림 목록 화면 진입 시 뱃지가 올바르게 동기화되는가?
- [ ] 새 알림 수신 시 뱃지가 증가하는가?
- [ ] 알림 읽음 처리 시 뱃지가 감소하는가?
- [ ] 모든 알림 읽음 처리 시 뱃지가 0이 되는가?
- [ ] 알림 삭제 시 뱃지가 올바르게 업데이트되는가?
- [ ] 백그라운드에서 알림 수신 시 뱃지가 업데이트되는가?
- [ ] 포그라운드에서 알림 수신 시 뱃지가 업데이트되는가?

## 참고사항

1. **iOS**: 뱃지는 APNs를 통해 자동으로 설정되지만, 앱에서도 명시적으로 업데이트하는 것을 권장합니다.
2. **Android**: 뱃지는 앱에서 직접 처리해야 하며, ShortcutBadger 라이브러리를 사용하는 것이 가장 호환성이 좋습니다.
3. **네트워크 최적화**: 뱃지 동기화는 사용자 경험에 중요하지만, 너무 자주 호출하지 않도록 주의하세요.
4. **오프라인 처리**: 네트워크가 없을 때는 로컬에 저장된 읽지 않은 알림 개수를 사용할 수 있습니다.

