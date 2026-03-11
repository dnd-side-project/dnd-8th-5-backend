# FCM 알림 시스템 아키텍처 설계

## Context

Modutime은 모임 시간 조율 서비스로, 참여자가 가용시간을 등록하면 최적 시간을 산출한다. 현재 참여자가 가용시간을 등록해도 다른 참여자에게 알림이 가지 않아, 수동으로 확인해야 하는 불편함이 있다.

Firebase Cloud Messaging(FCM) 기반 푸시 알림을 도입하여, **OAuth 사용자가 가용시간을 등록하면 같은 방의 다른 참여자에게 알림**을 보내는 시스템을 설계한다.

**범위:**
- 알림 대상: OAuth(카카오) 로그인 사용자만
- 알림 시나리오: 가용시간 등록/수정 알림 1가지
- 알림 이력 저장 및 조회 (읽음 상태 관리 포함)
- 단일 API 서버 구성 (메시지 큐 불필요)

---

## 1. 도메인 모델

### DeviceToken 엔티티

```
Table: device_token
──────────────────────────────────────────
id            BIGINT PK AUTO_INCREMENT
token         VARCHAR(512) NOT NULL UNIQUE   -- FCM 등록 토큰
user_id       BIGINT NOT NULL               -- FK → users.id
device_info   VARCHAR(50)                   -- "Android", "iOS", "Web" (선택)
created_at    DATETIME(6)
modified_at   DATETIME(6)

INDEX(user_id)
```

**설계 근거:**
- OAuth 사용자만 지원하므로 `user_id`만 필요 (nullable `participant_id` 불필요)
- 한 사용자가 여러 디바이스를 가질 수 있으므로 1:N 관계
- `token`에 UNIQUE 제약 → 같은 디바이스 중복 등록 방지
- JPA Auditing으로 `created_at`, `modified_at` 자동 관리

### Notification 엔티티 (알림 이력)

```
Table: notification
──────────────────────────────────────────
id                BIGINT PK AUTO_INCREMENT
type              VARCHAR(50) NOT NULL        -- NotificationType enum
title             VARCHAR(200) NOT NULL       -- 알림 제목
message           VARCHAR(500) NOT NULL       -- 알림 본문
room_uuid         VARCHAR(50)                 -- 관련 방 UUID (딥링크용)
recipient_id      BIGINT NOT NULL             -- FK → users.id (수신자)
sender_name       VARCHAR(50)                 -- 발송 트리거한 참여자 이름
is_read           BOOLEAN NOT NULL DEFAULT FALSE  -- 읽음 여부
read_at           DATETIME(6)                 -- 읽은 시각
created_at        DATETIME(6)

INDEX(recipient_id, is_read)
INDEX(recipient_id, created_at DESC)
```

**설계 근거:**
- `recipient_id`로 사용자별 알림 목록 조회 (OAuth 사용자만 대상이므로 user_id 기반)
- `is_read` + `read_at`으로 읽음 상태 관리 → 클라이언트 알림 뱃지 카운트 지원
- `room_uuid` 포함 → 알림 탭에서 방으로 바로 이동 가능
- `sender_name`은 비정규화 → 알림 표시 시 Participant 조회 불필요
- `(recipient_id, is_read)` 복합 인덱스 → 읽지 않은 알림 카운트 쿼리 최적화
- `(recipient_id, created_at DESC)` 인덱스 → 알림 목록 페이징 최적화

### NotificationType 열거형

```java
public enum NotificationType {
    가용시간_등록
}
```

향후 시나리오 추가 시 열거값만 추가하면 된다.

---

## 2. 패키지 구조

기존 vertical slice 패턴을 따른다:

```
com.dnd.modutime.core.notification/
├── domain/
│   ├── DeviceToken.java                  -- JPA 엔티티
│   ├── DeviceTokenRepository.java        -- 도메인 인터페이스
│   ├── Notification.java                 -- 알림 이력 JPA 엔티티
│   ├── NotificationRepository.java       -- 알림 이력 도메인 인터페이스
│   └── NotificationType.java            -- 알림 유형 enum
├── application/
│   ├── DeviceTokenService.java           -- 토큰 등록/해제
│   ├── NotificationService.java          -- 알림 발송 + 이력 저장 오케스트레이션
│   ├── NotificationQueryService.java     -- 알림 이력 조회 (읽음 처리 포함)
│   ├── NotificationEventHandler.java     -- 이벤트 구독 → 알림 트리거
│   ├── command/
│   │   └── DeviceTokenRegisterCommand.java
│   └── response/
│       ├── NotificationResponse.java     -- 알림 단건 응답 DTO
│       └── UnreadCountResponse.java      -- 읽지 않은 알림 수 응답 DTO
└── controller/
    ├── DeviceTokenController.java        -- 디바이스 토큰 REST API
    └── NotificationController.java       -- 알림 이력 조회/읽음 REST API

com.dnd.modutime.infrastructure/
├── fcm/
│   ├── FcmConfig.java                    -- FirebaseApp 초기화 @Configuration
│   ├── FcmNotificationSender.java        -- NotificationSender 구현체
│   └── NoOpNotificationSender.java       -- 테스트/로컬용 비활성 구현체
└── persistence/notification/
    ├── DeviceTokenJpaRepository.java     -- JPA 구현
    └── NotificationJpaRepository.java    -- 알림 이력 JPA 구현
```

**핵심 인터페이스 (도메인 계층):**

```java
// core/notification/domain/NotificationSender.java
public interface NotificationSender {
    SendResult send(List<String> tokens, String title, String body, Map<String, String> data);
}
```

`NotificationSender`는 도메인이 정의하고, `FcmNotificationSender`가 인프라에서 구현한다. 기존 `ParticipantRepository` ↔ `ParticipantJpaRepository` 패턴과 동일.

---

## 3. 이벤트 기반 알림 흐름

### 기존 이벤트 흐름 (변경 없음)

```
TimeBlock.replace()
  → TimeBlockReplaceEvent (roomUuid, participantName)
  → TimeTableEventHandler → TimeTable 갱신
  → TimeTableReplaceEvent
  → AdjustmentResultEventHandler → 최적 시간 재계산
```

### 추가되는 알림 흐름

```
TimeBlock.replace()
  → TimeBlockReplaceEvent
  → NotificationEventHandler (AFTER_COMMIT, @Async)
      ├── 1. roomUuid로 해당 방의 Participant 목록 조회
      ├── 2. 등록한 본인 제외
      ├── 3. userId가 있는 Participant만 필터 (OAuth 사용자)
      ├── 4. userId로 DeviceToken 목록 조회
      ├── 5. 수신 대상별 Notification 이력 저장 (bulk insert)
      └── 6. NotificationSender.send() 호출
```

### NotificationEventHandler 핵심 설계

```java
@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TimeBlockReplaceEvent event) {
        notificationService.가용시간_등록_알림(
            event.getRoomUuid(),
            event.getParticipantName()
        );
    }
}
```

**`@Async` + `AFTER_COMMIT` 조합 이유:**
- 기존 이벤트 핸들러들은 `REQUIRES_NEW`로 트랜잭션 체인을 형성 → 알림은 이 체인에 참여하면 안 됨
- `AFTER_COMMIT`: 가용시간 저장이 확정된 후에만 알림 발송
- `@Async`: HTTP 응답 지연 방지 (FCM 호출은 외부 네트워크 I/O)

---

## 4. API 설계

### 디바이스 토큰 등록

```
POST /api/v1/device-tokens
Authorization: Bearer {OAuth JWT}
Content-Type: application/json

{
  "token": "fcm-registration-token-string"
}

→ 201 Created (신규 등록)
→ 200 OK (이미 존재하는 토큰 → 멱등성)
```

### 디바이스 토큰 해제

```
DELETE /api/v1/device-tokens
Authorization: Bearer {OAuth JWT}
Content-Type: application/json

{
  "token": "fcm-registration-token-string"
}

→ 204 No Content
```

**보안:** `/api/**` 경로이므로 기존 `OAuth2TokenAuthenticationFilter`가 자동 적용. 별도 Security 설정 변경 불필요.

### 알림 이력 조회

```
GET /api/v1/notifications?page=0&size=20
Authorization: Bearer {OAuth JWT}

→ 200 OK
{
  "content": [
    {
      "id": 1,
      "type": "AVAILABILITY_SUBMITTED",
      "title": "가용시간 등록",
      "message": "김철수님이 가용시간을 등록했습니다.",
      "roomUuid": "abc123",
      "senderName": "김철수",
      "isRead": false,
      "createdAt": "2026-03-10T14:30:00"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 45,
  "totalPages": 3
}
```

### 읽지 않은 알림 수 조회

```
GET /api/v1/notifications/unread-count
Authorization: Bearer {OAuth JWT}

→ 200 OK
{
  "count": 3
}
```

### 알림 읽음 처리

```
PATCH /api/v1/notifications/{notificationId}/read
Authorization: Bearer {OAuth JWT}

→ 204 No Content
```

### 알림 전체 읽음 처리

```
PATCH /api/v1/notifications/read-all
Authorization: Bearer {OAuth JWT}

→ 204 No Content
```

**설계 근거:**
- 페이징은 기존 프로젝트의 `PageRequest`/`PageResponse` 패턴 활용
- 개별 읽음 + 전체 읽음 모두 지원 → 클라이언트 UX 유연성 확보
- `unread-count`를 별도 API로 분리 → 알림 뱃지 갱신 시 목록 전체를 불러올 필요 없음
- 본인 알림만 조회/수정 가능하도록 서비스 계층에서 `userId` 검증

---

## 5. 알림 메시지 형식

FCM **Data Message** 사용 (클라이언트가 표시 제어):

```json
{
  "data": {
    "type": "AVAILABILITY_SUBMITTED",
    "roomUuid": "abc123",
    "roomTitle": "팀 회식 날짜 정하기",
    "participantName": "김철수",
    "message": "김철수님이 가용시간을 등록했습니다."
  }
}
```

- `roomUuid` 포함 → 클라이언트 딥링크 지원
- Notification Message가 아닌 Data Message → 앱이 포그라운드/백그라운드 모두에서 커스텀 처리 가능

---

## 6. FCM 인프라 설정

### Firebase Admin SDK 의존성

```groovy
// build.gradle
implementation 'com.google.firebase:firebase-admin:9.2.0'
```

### 설정 파일

```yaml
# application-fcm.yaml (신규)
fcm:
  enabled: ${FCM_ENABLED:false}
  service-account-path: ${FCM_SERVICE_ACCOUNT_PATH:}
```

```yaml
# application.yaml (수정)
spring:
  profiles:
    include:
      - db
      - oauth2
      - host
      - security
      - logging
      - fcm          # 추가
```

### FcmConfig

```java
@Configuration
@EnableAsync
public class FcmConfig {

    @Bean
    @ConditionalOnProperty(name = "fcm.enabled", havingValue = "true")
    public NotificationSender fcmNotificationSender(
            @Value("${fcm.service-account-path}") String path) {
        // FirebaseApp 초기화 + FcmNotificationSender 반환
    }

    @Bean
    @ConditionalOnProperty(name = "fcm.enabled", havingValue = "false", matchIfMissing = true)
    public NotificationSender noOpNotificationSender() {
        return new NoOpNotificationSender(); // 로컬/테스트용
    }
}
```

**서비스 계정 키 관리:**
- 프로덕션: 환경변수 `FCM_SERVICE_ACCOUNT_PATH`로 파일 경로 주입
- 로컬: `fcm.enabled=false` → `NoOpNotificationSender` 사용 (FCM 미호출)
- `.gitignore`에 서비스 계정 JSON 추가 필수

---

## 7. 에러 처리

| 상황 | 처리 방식 |
|------|-----------|
| FCM 토큰 만료 (`UNREGISTERED`) | 해당 `DeviceToken` 자동 삭제 |
| FCM 토큰 형식 오류 (`INVALID_ARGUMENT`) | 해당 `DeviceToken` 자동 삭제 |
| FCM 서비스 장애 | WARN 로그 기록, 재시도 없음 (best-effort) |
| 방에 알림 대상 없음 | 조용히 스킵 (정상 케이스) |

알림은 **best-effort** 정책. 실패해도 핵심 기능(가용시간 저장)에 영향 없음. 이는 `@Async` + `AFTER_COMMIT`으로 보장됨.

---

## 8. 전체 아키텍처 다이어그램

```
┌──────────────────────────────────────────────────────────┐
│                      Client (App)                         │
│  ┌──────────┐ ┌──────────┐ ┌────────────┐ ┌───────────┐  │
│  │FCM Token │ │가용시간  │ │ 알림 이력  │ │ 푸시 수신 │  │
│  │등록 API  │ │등록 API  │ │ 조회 API   │ │ (FCM SDK) │  │
│  └────┬─────┘ └────┬─────┘ └─────┬──────┘ └─────▲─────┘  │
└───────┼────────────┼─────────────┼───────────────┼────────┘
        │            │             │               │
  ──────┼────────────┼─────────────┼───────────────┼────────
        ▼            ▼             ▼               │
┌──────────────────────────────────────────────────┼────────┐
│                Spring Boot API Server            │        │
│                                                  │        │
│  DeviceToken   TimeBlock     Notification                 │
│  Controller    Controller    Controller                   │
│     │              │              │                       │
│     ▼              ▼              ▼                       │
│  DeviceToken  TimeBlock     NotificationQuery             │
│  Service      .replace()    Service                       │
│  (등록/해제)       │         (조회/읽음처리)              │
│     │        TimeBlockReplaceEvent  │                     │
│     ▼            │          │       ▼                     │
│  DeviceToken  기존 핸들러  NotificationEventHandler       │
│   (DB)     (TimeTable →     (@Async, AFTER_COMMIT)       │
│            AdjustResult)         │                        │
│                                  ▼                        │
│                         NotificationService               │
│                          │       │       │                │
│                  Participant  DeviceToken  Notification    │
│                    조회        조회       이력 저장(DB)   │
│                                  │                        │
│                                  ▼                        │
│                          NotificationSender               │
│                            (인터페이스)                    │
│                                  │                        │
│                      ┌───────────┴────────┐               │
│                      ▼                    ▼               │
│            FcmNotificationSender  NoOpSender              │
│                 (prod)           (local)                   │
└──────────────────────┬────────────────────────────────────┘
                       │
                       ▼
               ┌──────────────┐
               │  Firebase    │
               │  Cloud       │──── 푸시 ────▶ Client
               │  Messaging   │
               └──────────────┘
```

---

## 9. 향후 확장 포인트

현재 범위에는 포함하지 않지만, 아키텍처가 지원하는 확장:

| 확장 | 변경 사항 |
|------|-----------|
| Guest 알림 지원 | `DeviceToken`에 nullable `participantId` 추가, Guest용 API 엔드포인트 추가 |
| 마감 임박 알림 | `@Scheduled` 스케줄러 + `RoomRepository.findByDeadLineBetween()` 쿼리 추가 |
| 전원 제출 알림 | `TimeBlockReplaceEvent` 핸들러에서 제출 완료 여부 체크 로직 추가 |
| 알림 설정 (On/Off) | `NotificationPreference` 엔티티 추가 |
| 알림 이력 삭제 | 개별/전체 삭제 API + soft delete 또는 hard delete 정책 결정 |
| 오래된 알림 정리 | `@Scheduled` 스케줄러로 N일 이상 지난 알림 자동 삭제 |

---

## 주요 파일 참조

| 파일 | 역할 |
|------|------|
| `core/timeblock/domain/TimeBlockReplaceEvent.java` | 구독할 이벤트 (roomUuid, participantName 보유) |
| `core/adjustresult/application/AdjustmentResultEventHandler.java` | `@TransactionalEventListener` 패턴 참조 |
| `core/participant/domain/Participant.java` | userId로 OAuth 사용자 식별 |
| `core/auth/oauth/OAuth2SecurityConfig.java` | 보안 설정 (변경 불필요 확인) |
| `infrastructure/persistence/participant/ParticipantJpaRepository.java` | Repository 어댑터 패턴 참조 |
| `build.gradle` | 의존성 추가 위치 |
