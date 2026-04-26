---
name: e2e-test
description: |
  Modutime 프로젝트에서 새/수정된 HTTP 엔드포인트를 실제 부팅된 애플리케이션을 상대로
  end-to-end로 검증하는 스킬. 단위/통합 테스트로는 잡히지 않는 응답 헤더(`Set-Cookie`),
  실제 SQL 동작, Caffeine 캐시 / Spring Security 필터 같은 런타임 측면을 확인할 때 사용한다.

  반드시 트리거: "e2e 테스트", "실제로 띄워서 확인", "엔드포인트 e2e", "직접 띄워서 검증",
  "수동 검증 계획 세워줘", "API 동작 확인", "런타임 검증", "real run", "live test",
  "응답 헤더 직접 확인", "쿠키가 실제로 떨어지는지" 등.

  엔드포인트를 새로 추가하거나 기존 엔드포인트를 의미 있게 수정한 PR을 검증할 때마다
  사용한다 — 사용자가 "테스트는 통과했는데 진짜로 동작하나?"라고 물을 때 또는 그렇게
  물을 만한 변경(필터/세션/쿠키/캐시/SQL 부수효과)이 있을 때는 명시적으로 요청하지
  않더라도 본 스킬을 적극 제안한다.
---

# Modutime 엔드포인트 e2e 검증 스킬

## 무엇을, 왜

Spring REST Docs 단위 테스트와 `@SpringBootTest` 통합 테스트는 컨트롤러 시그니처와 컨텍스트 로딩까지 검증해주지만, 다음은 **실제 부팅된 앱**으로만 잡힌다:

- HTTP 응답 헤더 (`Set-Cookie`의 `Max-Age`, `SameSite`, `Secure` 등)
- Spring Security 필터 체인 통과 후의 최종 응답
- Hibernate가 실제로 발행한 SQL과 바인딩된 파라미터
- Caffeine 같은 in-memory 캐시의 hit/miss 동작
- OAuth 흐름에서 발급된 JWT의 형태와 클레임

본 스킬은 그 검증을 효율적이고 빠짐없이 수행하기 위한 절차다. 이번 한 번만 손으로 따라하고 끝나는 게 아니라, 새 엔드포인트가 추가되거나 인증/세션/쿠키/캐시 관련 동작이 바뀔 때마다 이 흐름을 재사용한다.

## 0. 사전 조건 점검

### JDK 17 강제

프로젝트는 Lombok 1.18.20을 쓰는데, 시스템 기본 JDK가 17보다 높으면(예: 25) Lombok이 javac 내부 API를 깨뜨려 `NoSuchFieldException: com.sun.tools.javac.code.TypeTag :: UNKNOWN`이 난다. **항상 Corretto 17로 명시적으로 환경변수를 잡고 시작한다.**

```bash
export JAVA_HOME=/Users/dio.kim/Library/Java/JavaVirtualMachines/corretto-17.0.17/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
java -version   # "17" 시작 확인
```

설치된 JDK가 다른 경로/벤더면 `/usr/libexec/java_home -V`로 17 경로를 찾아 `JAVA_HOME`만 바꾼다.

### 카카오 OAuth (인증 필요한 엔드포인트면)

- `KAKAO_CLIENT_ID` 환경변수 필요 (카카오 dev console > 내 애플리케이션 > 앱 키 > **REST API 키**)
- 카카오 dev console > Kakao Login > Redirect URI에 **`http://localhost:8080/oauth2/kakao/callback`** 등록되어 있어야 함

검증 대상이 게스트 경로(`/guest/**`)나 permitAll 경로뿐이면 이 단계는 건너뛴다. permitAll 매처는 `OAuth2SecurityConfig#permitAllMatchers()`에서 확인한다.

### 작업 디렉토리

워크트리에서 작업 중이면 워크트리 경로에서 그대로 명령을 실행한다. `cd` 하지 말고 절대경로로 진행해도 된다. (예: `cd /Users/dio.kim/Documents/private/repository/modutime/.claude/worktrees/<branch>`)

## 1. 검증 계획 수립

본격적으로 부팅하기 전에 **무엇을 검증할지** 종이에 적어둔다. 빈 종이 상태로 부팅하면 "되는 것 같다" 수준의 손짓 검증으로 끝나기 쉽다. 다음 4가지 차원에서 변경된 엔드포인트가 가진 책임을 한 줄씩 적는다:

| 차원 | 질문 | 검증 수단 |
|---|---|---|
| HTTP 응답 | 상태 코드, 응답 본문, 응답 헤더(특히 `Set-Cookie`)가 의도대로 나오는가 | `curl -i` 출력 |
| DB 부수효과 | 어떤 테이블의 어떤 컬럼이 바뀌어야 하는가 | `application-db.yaml` local의 `show-sql: true` 로그 |
| 캐시/세션 | Caffeine 캐시, `JSESSIONID`, `OAuth2UserCache` 같은 in-memory 상태가 어떻게 변하는가 | DEBUG 로깅 + 동작 결과로 간접 |
| 거부 경로 | 인증 실패, 잘못된 입력, 후속 호출 거부가 의도대로 되는가 | 같은 엔드포인트에 의도적으로 부적합 요청 |

각 항목에 대해 "이 응답에서 이 줄이 보여야 한다", "이 SQL의 이 파라미터가 이 값이어야 한다" 수준까지 구체화하면 검증이 실패했을 때 원인을 빨리 좁힐 수 있다. 항목이 4개 미만이라면 실제로 변경의 영향이 크지 않은 것일 수 있고, 8개 이상이라면 두 PR로 쪼개는 걸 고민해도 된다.

거부 경로를 빠뜨리지 말 것. 작동 시나리오만 검증하면 회귀가 거부 경로에서 터질 때 못 잡는다.

## 2. 단위 테스트 선행 보강 (선택, 강력 권장)

런타임 검증은 결정적이지 않은 면이 있다(타이밍, 외부 OAuth, 사람 개입). 캐시 무효화나 메서드 호출 횟수 같은 결정적 검증은 Mockito 단위테스트로 먼저 잠그는 게 안전하다. e2e 절차를 시작하기 전에:

- 변경된 서비스가 외부 컴포넌트를 호출하는가? (e.g. `userCache.removeUserFromCache(...)`)
- 분기(예: 사용자 미존재 시) 동작이 명세되어 있는가?

있다면 `@ExtendWith(MockitoExtension.class)` 패턴(`OAuth2TokenProviderTest`, `OAuth2LogoutServiceTest` 참고)으로 1-2개 테스트를 먼저 추가하고 `./gradlew test` 통과 확인 후 push해 CI를 한 번 거치는 걸 권장한다. 이러면 e2e에서 캐시 검증을 "로그 기반 간접"으로 더 가볍게 가져갈 수 있다.

## 3. 앱 부팅

```bash
export JAVA_HOME=/Users/dio.kim/Library/Java/JavaVirtualMachines/corretto-17.0.17/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
export KAKAO_CLIENT_ID="<REST API 키>"   # OAuth 안 쓰면 생략 가능
./gradlew bootRun --args='--logging.level.com.dnd.modutime.core.auth.oauth.OAuth2UserService=DEBUG'
```

`bootRun`은 long-running이므로 백그라운드 실행 + 출력 파일 tail이 편하다. 검증 대상에 따라 `--args=`에 더 많은 DEBUG 로깅을 추가한다 (예: `--logging.level.com.dnd.modutime.core.<도메인>=DEBUG`).

부팅 완료 신호: 헬스체크. 폴링 루프로 기다린다.

```bash
until curl -s -f http://localhost:8080/aws -o /dev/null; do sleep 2; done && echo "App is healthy"
```

`/aws`는 `permitAllMatchers`에 등록된 헬스체크 엔드포인트다.

### 기본 환경 사실

| 항목 | 값 | 비고 |
|---|---|---|
| 기본 프로필 | `local` (`application.yaml`) | include: db / oauth2 / host / security / logging / server |
| DB | H2 in-memory (`jdbc:h2:mem:modutime`) | 외부 DB 인프라 불필요. 앱 종료 시 데이터 소실 |
| `secure-cookie` (local) | `false` | HTTP localhost에서 쿠키 동작 OK |
| 클라이언트 redirect | `http://localhost:3000` | 미실행이어도 무방, 토큰만 URL에서 추출 |
| 토큰 만료 | access 15분 / refresh 2주 | 검증을 15분 내 끝낼 것, 길어지면 재로그인 |
| `show-sql` | true | Hibernate SQL이 콘솔에 그대로 찍힘 |

## 4. 사전 데이터 시드

엔드포인트가 path/query 파라미터로 도메인 식별자(예: `roomUuid`)를 받으면 미리 만들어둔다. 카카오 OAuth 흐름에서 `roomUuid`는 state에 인코딩되므로 **로그인 전에** 방을 만들어 둬야 한다.

```bash
ROOM_UUID=$(curl -s -X POST http://localhost:8080/guest/api/room \
  -H "Content-Type: application/json" \
  -d '{
    "title": "e2e",
    "headCount": 2,
    "dates": ["2026-04-30"],
    "startTime": "10:00",
    "endTime": "18:00"
  }' | jq -r '.roomUuid')
echo "ROOM_UUID=${ROOM_UUID}"
```

응답 키는 `roomUuid` (`uuid`가 아님 — `RoomCreationResponse` 확인). 다른 도메인에도 비슷한 게스트 경로(`/guest/**` 또는 `permitAll` 매처)가 있는지 확인하고 시드 가능한 엔드포인트로 데이터를 미리 만든다.

## 5. 엔드포인트별 검증 흐름

### 5-A. OAuth 인증이 필요한 엔드포인트

#### 토큰 획득 (브라우저)

사용자에게 다음 흐름을 안내한다:

1. **시크릿 창**에서 `http://localhost:8080/oauth2/authorization/kakao?roomUuid=<ROOM_UUID>` 접속
2. 카카오 로그인 → 자동 redirect → 주소창 URL이 `http://localhost:3000/...?access_token=eyJ...&access_token_expiration_time=...&room_uuid=...`로 바뀜 (ERR_CONNECTION_REFUSED 페이지지만 URL은 살아있다)
3. 주소창에서 `access_token=` 뒤의 JWT 값을 복사
4. DevTools(F12) > Application > Cookies > `http://localhost:8080`에서 `refreshToken`, `refreshTokenExpireTime` 두 쿠키 존재/`Max-Age` 확인 (검증 1)
5. 같은 시점 콘솔에 `User kakao:<email> is cached` 로그가 떴는지 확인 (검증 1-C)

토큰을 셸 변수로 저장:

```bash
export ACCESS_TOKEN="eyJ..."
```

#### 엔드포인트 호출

`curl -i`로 응답 헤더까지 한 번에 본다:

```bash
curl -i -X POST http://localhost:8080/<path> \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '<payload>'
```

응답을 1번 단계에서 적어둔 검증 항목과 한 줄씩 대조한다.

### 5-B. 게스트(인증 불필요) 엔드포인트

permitAll 매처 또는 `/guest/**` 경로면 그냥 curl. 시크릿 창도 토큰도 필요 없다.

```bash
curl -i -X POST http://localhost:8080/guest/api/<path> \
  -H "Content-Type: application/json" \
  -d '<payload>'
```

## 6. 차원별 검증 패턴

### 6-1. HTTP 응답

`curl -i`의 첫 줄과 헤더를 전체 본다. `grep`으로 한두 줄만 보지 말 것 — 의도치 않은 헤더(예: 빠진 `Vary`, 잘못 추가된 `WWW-Authenticate`)가 회귀 신호일 수 있다.

쿠키가 핵심이면 발급 시 옵션과 비교한다. 예: 로그인 시 `httpOnly=true; secure=<env>; SameSite=Lax; path=/`로 발급된다면, 만료/삭제 시에도 동일 옵션 + `Max-Age=0`이어야 브라우저가 같은 쿠키로 인식해 제거한다.

### 6-2. DB 부수효과 (간접, 권장)

H2 콘솔을 임시로 켜는 건 침습적이라 권장하지 않는다. 대신 `application-db.yaml` local에 이미 활성화된 `show-sql: true` + `format_sql: true` + `BasicBinder: TRACE`로 충분하다.

bootRun 출력 파일에서 호출 시각 근처를 찾는다:

```bash
# 백그라운드 task ID로 출력 파일 위치 알기
# 그 파일에서 시각/스레드로 좁혀 본다
awk '/<HH:MM:SS>/' <output-file> | grep -nE "Hibernate|update[[:space:]]+|insert[[:space:]]+into|delete[[:space:]]+from"
```

`Hibernate:` prefix 다음 멀티라인이 실제 실행된 SQL이고, 바로 뒤의 `BasicBinder` TRACE 라인들이 바인딩된 파라미터다. 의도한 컬럼이 의도한 값으로 들어갔는지 확인한다.

만약 더미 prefix(`Hibernate: create sequence`, `insert/update/delete persister 정의` 등)가 시작 시점에 잔뜩 보이면 실제 호출 SQL이 아니라 startup 스캔이다. 호출 시각 이후로 좁혀라.

### 6-3. 캐시/세션/내부

직접 메모리를 들여다볼 수 없으므로 DEBUG 로그로 간접 검증한다. 대표 패턴:

- `OAuth2UserCache`(Caffeine, 10분 TTL)는 `OAuth2UserService.loadUser()`에 있는 `User <username> is cached` 로그(`log.debug`)로 hit/miss 추적 가능. 캐시가 **있었다면** loadUser는 캐시 hit 분기를 타서 `is cached` 로그가 안 찍힌다 → 캐시 무효화 동작은 "로그아웃 직후 재로그인 시 다시 로그가 찍힌다"로 간접 검증.
- `JSESSIONID`는 응답 `Set-Cookie: JSESSIONID=; Max-Age=0`으로 SecurityConfig의 logout 설정 확인.

DEBUG가 더 필요하면 `--args='--logging.level.com.dnd.modutime.<package>=DEBUG'`를 추가해 부팅한다.

### 6-4. 거부 경로

작동 시나리오만 보지 말 것. 거부 경로는 회귀가 가장 자주 숨는 곳이다. 엔드포인트 종류별로 최소 3개는 챙긴다.

**인증 필요 엔드포인트**:
- `Authorization` 헤더 없이 호출 → 401
- 만료된 access token으로 호출 → 401 (`OAuth2TokenAuthenticationFilter` 검증)
- 잘못된 형식의 토큰(서명 깨짐) → 401
- 필요한 쿠키 없이 후속 엔드포인트 호출 → 컨트롤러가 던지는 예외(예: `MISSING_COOKIE`)
- 본인이 참여하지 않은 방에 접근 → 403 (`AccessDeniedException`은 `OAuth2AccessDeniedHandler`로 가서 `GlobalControllerAdvice`를 안 거침)
- 잘못된 `roomUuid`/`userId` → `404` 또는 도메인 에러

**게스트(`/guest/**`) / permitAll 엔드포인트**: 인증이 없으니 더더욱 입력 검증이 핵심.
- 필수 필드 누락(예: `title` 빠진 `POST /guest/api/room`) → 400
- 잘못된 형식(예: `dates: ["not-a-date"]`, `startTime: "25:00"`) → 400
- 음수/경계값(`headCount: 0` 또는 `-1`) → 400
- 존재하지 않는 식별자로 GET → 404
- 동일 식별자로 중복 요청(idempotency 깨질 수 있음) — 사양에 따라 다르지만 명시.

거부 응답의 본문 형태가 같이 검증 항목이다 (`message`, `code`, `status`). `GlobalControllerAdvice`가 어떤 예외를 잡고 어떤 코드로 매핑하는지 확인하고 그 매핑이 의도대로인지 확인.

## 7. 결과 정리

부팅 종료 전에 검증 결과를 표로 모은다. 사용자(또는 PR 리뷰어)에게 보고할 때 이 표가 산출물 그 자체다.

```
| # | 검증 항목 | 결과 | 증거(파일/라인/응답) |
|---|---|---|---|
| 1 | ... | ✅ / ❌ | ... |
```

증거는 응답 라인, 출력 파일의 라인 번호, SQL 바인딩 파라미터 등 객관적 인용을 적는다. "잘 동작했다"만 적으면 다음 사람이 다시 처음부터 검증해야 한다.

## 8. 종료

```bash
# bootRun 백그라운드 task 종료
# (TaskStop 또는 Ctrl+C)
```

H2는 in-memory라 데이터는 자동으로 사라진다. 필요하면 push해서 PR에 결과 표를 코멘트로 첨부한다.

## 9. 실패 케이스 트리아지

| 증상 | 원인 후보 | 확인 |
|---|---|---|
| 컴파일 시 `NoSuchFieldException: TypeTag :: UNKNOWN` | JDK 25+에서 Lombok 1.18.20 호환 안 됨 | `java -version`이 17인지 |
| `KAKAO_CLIENT_ID` 미설정 부팅 실패 | 환경변수 누락 | `echo $KAKAO_CLIENT_ID` |
| 카카오 redirect 시 `KOE006`/`redirect_uri_mismatch` | 콘솔에 `http://localhost:8080/oauth2/kakao/callback` 미등록 | dev console > Kakao Login > Redirect URI |
| 응답에 `Set-Cookie: refreshToken=` 없음 | SuccessHandler 빈 시그니처 회귀 또는 핸들러 자체 빠짐 | `OAuth2SecurityConfig` 로그아웃 핸들러 빈 정의 |
| `401 unauthorized` (정상 호출인데) | access token 만료(15분) 또는 잘못 복사 | 토큰 다시 받기 |
| 첫 호출 시 `User ... is cached` 로그 안 찍힘 | DEBUG 로깅 설정 안 먹음 | `--args=` 따옴표/이스케이프 |
| 재로그인 시 `is cached` 안 찍힘 | 캐시가 살아있음 = 캐시 무효화 회귀 | 코드 변경분 점검 |
| H2가 expected 데이터 없다고 함 | 다른 SpringBootTest가 in-memory DB를 공유하지 않음(별 프로세스) | 헬퍼 endpoint나 `/guest/api/room` 등으로 시드 |
| update SQL이 안 찍힘 | 트랜잭션이 롤백됐거나 dirty checking이 안 됨 | `Initiating transaction commit` 로그 + entity가 매니지드 상태인지 |

## 10. 적용 범위 / 비범위

**적용 범위**
- 새 컨트롤러 메서드 추가 (`@PostMapping`/`@GetMapping`/...).
- 기존 엔드포인트의 응답 형태/헤더/쿠키/상태 코드 변경.
- 인증·세션·캐시·필터 체인 변경.
- 데이터 부수효과(Insert/Update/Delete)가 있는 변경.

**비범위**
- 운영(prod) 환경 검증 — `secure-cookie=true`나 prod-only 빈은 dev 배포 후에 가능.
- 로드/동시성 검증 — 본 스킬은 단일 사용자 흐름 위주.
- 외부 의존(카카오 dev console 자체) 검증.

## 참고

| 파일 | 위치 |
|---|---|
| `application.yaml` | `src/main/resources/` |
| `application-db.yaml` (`show-sql: true`) | 같은 곳 |
| `OAuth2SecurityConfig#permitAllMatchers()` | `core/auth/oauth/` |
| `OAuth2UserService` ("is cached" 로그) | 같은 곳 |
| `RoomGuestController` (`POST /guest/api/room`) | `core/room/controller/` |
| 기존 단위테스트 패턴 | `core/auth/oauth/facade/OAuth2TokenProviderTest.java`, `OAuth2LogoutServiceTest.java` |

## 함께 쓰면 좋은 스킬

- **restdocs-api-test**: 새 엔드포인트면 본 스킬 전에 또는 후에 ApiDocsTest를 추가해 OpenAPI/AsciiDoc까지 노출시킨다.
