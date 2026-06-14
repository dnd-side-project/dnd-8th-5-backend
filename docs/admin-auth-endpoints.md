# 어드민 인증 API 핸드오프

Modutime 어드민 전용 인증 API 명세입니다. 어드민 클라이언트(콘솔/도구) 개발 시 참고하세요.

- 모든 어드민 API는 `/admin/**` 경로에 위치하며, **일반 사용자(카카오 OAuth) 인증과 완전히 분리**되어 있습니다.
- 인증 방식: JWT **access token + refresh token** (기존 서비스 로그인과 동일한 토큰 구조).
- 에러 응답은 서비스 공통 포맷 `ErrorResponse`를 사용합니다: `{ "code": "...", "message": "...", "status": 4xx }`

---

## 1. 로그인 — `POST /admin/login`

username + 비밀번호를 검증하고 access/refresh 토큰을 **JSON 으로** 발급합니다. (쿠키 사용 안 함)

**요청**
```http
POST /admin/login
Content-Type: application/json

{
  "username": "superadmin",
  "password": "1234"
}
```

**응답 `200 OK`**
```json
{
  "accessToken": "eyJ0eXBlIjoiSldUI...",
  "accessTokenExpirationTime": "2026-06-14T06:57:36.829",
  "refreshToken": "eyJ0eXBlIjoiSldUI...",
  "refreshTokenExpirationTime": "2026-06-28T06:42:36.829"
}
```

**실패**
| 상황 | status | code |
|---|---|---|
| 아이디 없음 / 비밀번호 불일치 | 401 | `BAD_CREDENTIALS` |
| username 또는 password 누락(빈 값) | 400 | `MT400` |

```bash
curl -X POST http://{host}/admin/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"superadmin","password":"1234"}'
```

---

## 2. 토큰 재발급 — `POST /admin/reissue-token`

refresh token으로 새로운 access token을 발급합니다. (refresh token 회전 없음 — 기존 refresh token 계속 사용)

refresh token은 **두 경로** 중 하나로 전달합니다. 둘 다 주면 **바디가 우선**합니다.
- 쿠키: `Cookie: refreshToken={리프레시 토큰}`
- JSON 바디: `{ "refreshToken": "..." }`

**요청 (바디)**
```http
POST /admin/reissue-token
Content-Type: application/json

{ "refreshToken": "eyJ0eXBlIjoiSldUI..." }
```

**응답 `200 OK`**
```json
{
  "accessToken": "eyJ0eXBlIjoiSldUI...",
  "accessTokenExpirationTime": "2026-06-14T06:57:37.063"
}
```

**실패**
| 상황 | status | code |
|---|---|---|
| refreshToken 누락(쿠키/바디 모두 없음) | 401 | `MISSING_COOKIE` |
| 유효하지 않은 refreshToken | 401 | `INVALID_TOKEN` |
| refreshToken 만료 | 401 | `REFRESH_TOKEN_EXPIRED` |

---

## 3. 보호된 어드민 API 호출 방법

`/admin/login`, `/admin/reissue-token` 을 **제외한 모든 `/admin/**` 엔드포인트**는 어드민 access token 인증이 필요합니다.

```http
GET /admin/{any-admin-endpoint}
Authorization: Bearer {accessToken}
```

**인증 실패**
| 상황 | status | code |
|---|---|---|
| Authorization 헤더 없음/형식 오류(Bearer 아님) | 401 | `INVALID_AUTHORIZATION_HEADER` |
| 위조/손상된 토큰, 어드민 토큰 아님 | 401 | `INVALID_TOKEN` |
| access token 만료 | 401 | `ACCESS_TOKEN_EXPIRED` |

> 만료(`ACCESS_TOKEN_EXPIRED`) 응답을 받으면 `POST /admin/reissue-token` 으로 access token을 재발급받아 재시도하세요.

---

## 4. 토큰 특성 / 격리

- 알고리즘: HS512. access token에 `user_type=admin` 클레임이 포함됩니다.
- 유효기간(서비스 토큰과 동일): **access 15분 / refresh 2주**.
- **토큰 격리**: 어드민 토큰은 `/admin/**` 에서만 유효합니다. 어드민 토큰으로 일반 사용자 API(`/api/**`)를 호출하면 401, 반대로 카카오/게스트 토큰으로 `/admin/**` 을 호출해도 401 입니다.
- 어드민 권한은 `ROLE_ADMIN` 으로 부여됩니다. (principal = username)

---

## 5. 계정 발급

- **로컬**: 부팅 시 `superadmin / 1234` 계정이 자동 생성됩니다(테스트용, local 프로파일 전용).
- **운영(prod)**: `ddl-auto: none` 이므로 테이블 생성 + 계정 등록을 **수동 SQL**로 수행합니다. 비밀번호는 현재 **평문 비교**입니다(추후 BCrypt 전환 예정).

```sql
CREATE TABLE admins (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(512) NULL,
    token_expiration_time DATETIME(6) NULL,
    created_by VARCHAR(50) NULL, created_at DATETIME(6) NULL,
    modified_by VARCHAR(50) NULL, modified_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uniqueAdminUsername UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO admins (username, password, created_by, modified_by, created_at, modified_at)
VALUES ('superadmin', '<비밀번호>', 'system', 'system', NOW(6), NOW(6));
```

---

## 6. 참고

- 생성되는 정식 API 문서(REST Docs HTML / OpenAPI 3.0): 태그 `Admin-Auth`. 빌드 명령 `./gradlew asciidoctor` (HTML), `./gradlew openapi3` (YAML).
- 현재 제공 범위: 로그인 + 토큰 재발급. (로그아웃은 미포함)
