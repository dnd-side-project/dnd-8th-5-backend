# Sentry 에러 알림 적용 계획

## Context

Modutime 프로젝트에 프로덕션 에러 모니터링이 없어, 서버 에러 발생 시 즉각적인 인지가 불가능하다. Sentry를 도입하여 5xx 서버 에러를 실시간으로 감지하고 알림을 받을 수 있도록 한다.

**현재 상태:**
- Spring Boot 2.7.8 / Java 17
- `GlobalControllerAdvice`에서 4xx 에러만 처리 (5xx catch-all 핸들러 없음)
- OAuth2 보안 예외는 별도 `SecurityErrorCodeResponseHandler`에서 처리
- 프로덕션 로그는 INFO 레벨만 설정
- Actuator는 `/health`만 노출

## 호환성 검토 결과

| 항목 | 결과 |
|------|------|
| Spring Boot 2.7.8 호환 | `sentry-spring-boot-starter` 7.x 사용 (Jakarta 아님) |
| Java 17 호환 | SDK가 Java 8+ 타겟, 문제 없음 |
| 기존 `@ControllerAdvice` 충돌 | `exception-resolver-order` 설정으로 해결 |
| OAuth2 보안 필터 | 보안 예외는 필터링으로 Sentry 전송 제외 |
| Free tier 적합성 | 월 5,000 이벤트, 소규모 서비스에 충분 |

## 적용 계획

### Step 1: 의존성 추가

**파일:** `build.gradle`

```groovy
implementation 'io.sentry:sentry-spring-boot-starter:7.22.0'
```

### Step 2: 설정 파일 추가

**새 파일:** `src/main/resources/application-sentry.yaml`

```yaml
# local 프로필 - Sentry 비활성화
sentry:
  dsn:
---
# prod 프로필 - Sentry 활성화
spring:
  config:
    activate:
      on-profile: prod
sentry:
  dsn: ${SENTRY_DSN}
  environment: production
  release: ${SENTRY_RELEASE:modutime@unknown}
  traces-sample-rate: 0.1
  send-default-pii: false
  in-app-includes: com.dnd.modutime
  exception-resolver-order: -2147483647
  ignored-exceptions-for-type:
    - com.dnd.modutime.exception.NotFoundException
    - com.dnd.modutime.exception.InvalidPasswordException
    - org.springframework.security.access.AccessDeniedException
    - org.springframework.web.bind.MethodArgumentNotValidException
```

**수정 파일:** `src/main/resources/application.yaml` — profiles.include에 `sentry` 추가

```yaml
spring:
  profiles:
    active: local
    include:
      - db
      - oauth2
      - host
      - security
      - logging
      - sentry    # 추가
```

### Step 3: GlobalControllerAdvice에 5xx 핸들러 추가

**파일:** `src/main/java/com/dnd/modutime/advice/GlobalControllerAdvice.java`

5xx 서버 에러를 명시적으로 Sentry에 전송하는 catch-all 핸들러 추가:

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ExceptionResponse> handleServerError(Exception exception) {
    Sentry.captureException(exception);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ExceptionResponse("서버 내부 오류가 발생하였습니다."));
}
```

### Step 4: BeforeSendCallback으로 노이즈 필터링

**새 파일:** `src/main/java/com/dnd/modutime/config/SentryConfig.java`

```java
@Configuration
public class SentryConfig {

    @Bean
    public SentryOptions.BeforeSendCallback beforeSendCallback() {
        return (event, hint) -> {
            var ex = event.getThrowable();
            // 4xx 클라이언트 에러는 전송하지 않음
            if (ex instanceof IllegalArgumentException
                    || ex instanceof NotFoundException
                    || ex instanceof InvalidPasswordException) {
                return null;
            }
            return event;
        };
    }
}
```

### Step 5: 프로덕션 환경 변수 설정

배포 환경(Docker/서버)에 환경 변수 추가:

```
SENTRY_DSN=https://<key>@o<org>.ingest.sentry.io/<project>
SENTRY_RELEASE=modutime@<version>
```

### Step 6: Sentry 대시보드 알림 규칙 설정 (수동)

Sentry 웹 UI에서:
1. **Alert 1:** 새 이슈 생성 시 → 이메일 알림 (Free tier)
2. **Alert 2:** 10분 내 동일 에러 10회 이상 → 긴급 알림
3. (선택) Team 플랜 시 Slack 채널 연동

## 수정 대상 파일 요약

| 파일 | 변경 내용 |
|------|-----------|
| `build.gradle` | sentry-spring-boot-starter 의존성 추가 |
| `src/main/resources/application.yaml` | profiles.include에 sentry 추가 |
| `src/main/resources/application-sentry.yaml` | **신규** - Sentry 설정 (local/prod 분리) |
| `src/main/java/.../advice/GlobalControllerAdvice.java` | 5xx catch-all 핸들러 추가 |
| `src/main/java/.../config/SentryConfig.java` | **신규** - BeforeSendCallback 필터 |

## 비용 및 제한사항

- **Free tier:** 월 5,000 이벤트, 이메일 알림만 가능, 30일 보관
- **Team 플랜 ($26/월):** Slack 연동, 무제한 사용자
- 노이즈 필터링으로 4xx 에러를 제외하면 Free tier로 충분할 가능성 높음

## 검증 방법

1. 로컬에서 `./gradlew test` — 기존 테스트 전체 통과 확인
2. 로컬에서 DSN 비워둔 상태로 앱 기동 → Sentry 비활성화 확인 (에러 없이 정상 동작)
3. 테스트용 DSN 설정 후 의도적으로 500 에러 유발 → Sentry 대시보드에 이벤트 수신 확인
4. 4xx 에러(잘못된 요청) 유발 → Sentry에 전송되지 않음 확인
