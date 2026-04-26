---
name: restdocs-api-test
description: "Modutime 프로젝트의 Spring REST Docs API 테스트 및 AsciiDoc 문서 생성 스킬. 새 컨트롤러 엔드포인트에 대한 @ApiDocsTest 테스트 클래스와 .adoc 문서 파일을 프로젝트 패턴에 맞춰 생성한다. 사용 시점: (1) 새 API 엔드포인트의 REST Docs 테스트 작성, (2) 기존 API의 문서화 누락 보완, (3) 'REST Docs', 'API 문서', 'DocsTest', 'adoc 만들어', 'API 테스트 문서' 키워드 감지 시."
---

# REST Docs API Test 생성 가이드

## 핵심 규칙

- **Spring Boot 2.7.8** → `javax.servlet` 사용 (jakarta 아님)
- **듀얼 문서 생성** 필수: Spring REST Docs (AsciiDoc) + OpenAPI 3.0 (epages)
- Cookie 문서화 시 `requestCookies()` 미지원 → adoc 텍스트로 설명
- 테스트 실행: `./gradlew apiDocsTest --tests "{FQCN}"`

## 생성 워크플로우

1. 대상 컨트롤러 읽기 → 엔드포인트, 파라미터, 응답 DTO 파악
2. DocsTest 클래스 생성 (아래 템플릿 + [references/patterns.md](references/patterns.md) 참조)
3. **도메인 `.adoc` 파일 처리** — 두 갈래 분기. 새 엔드포인트가 어느 도메인 .adoc로 가야 하는지 먼저 확인하고, 그 파일이 이미 있는지부터 본다.
   - **새 도메인** → `src/docs/asciidoc/{domain-name}.adoc` 신규 생성 + `src/docs/asciidoc/index.adoc`에 `include::{domain-name}.adoc[]` 한 줄 추가
   - **기존 도메인** → 그 .adoc 파일을 열어서 새 엔드포인트 `=== {API 이름}` 섹션 + 스니펫 include 블록을 **반드시 직접 추가**한다. 이걸 빠뜨리면 테스트는 통과해도 최종 문서에 노출되지 않는다 (스니펫만 생기고 include되지 않은 dead snippet 상태가 됨). index.adoc은 이미 해당 도메인 파일을 include하고 있으므로 손대지 말 것.
4. `./gradlew apiDocsTest --tests "{FQCN}"` 실행 → 스니펫 생성 확인 (`build/generated-snippets/{operation-id}/`)
5. **`./gradlew asciidoctor` 실행 → 최종 HTML에 새 섹션이 들어갔는지 검증.** 빌드만 성공하는 걸로는 부족하다. asciidoctor는 include가 누락돼도 통과하므로 `grep "{operation-id}\|{API 한글 이름}" build/docs/asciidoc/index.html`로 실제 노출을 확인한다.

### 흔한 실수: dead snippet

스니펫은 생성됐는데 어느 .adoc에서도 include하지 않으면 `build/generated-snippets/`에는 파일이 쌓여도 사용자가 보는 문서에는 그 엔드포인트가 영영 나타나지 않는다. 새 엔드포인트의 DocsTest를 작성한 직후 항상 자문할 것: "이 `operation-id`를 include하는 .adoc 라인이 어디에 있나?" 답이 없으면 step 3을 빼먹은 것이다.

## 파일 위치 규칙

| 파일 | 경로 |
|------|------|
| DocsTest | `src/test/java/com/dnd/modutime/controller/{domain}/{Controller}DocsTest.java` |
| adoc | `src/docs/asciidoc/{domain-name}.adoc` |
| index | `src/docs/asciidoc/index.adoc` |
| snippets 출력 | `build/generated-snippets/{operationIdentifier}/` |

## operationIdentifier 네이밍

URL 패턴을 kebab-case로 변환. HTTP 메서드를 prefix로 포함:

- `POST /guest/api/room` → `guest-post-api-room`
- `GET /api/v1/room/{roomUuid}/adjustment-results` → `get-api-v1-room-room-uuid-adjustment-result`
- `POST /oauth2/reissue-token` → `oauth2-post-reissue-token`

## tag 네이밍

`{Domain}` 또는 `{Domain}-{Context}` 형식: `Room-Guest`, `Auth-OAuth2`, `Participant`, `adjustment-result`

## 기본 테스트 구조

```java
package com.dnd.modutime.controller.{domain};

import com.dnd.modutime.annotation.ApiDocsTest;
import com.dnd.modutime.documentation.DocumentUtils;
import com.dnd.modutime.documentation.MockMvcFactory;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;

import static com.dnd.modutime.TestConstant.LOCALHOST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiDocsTest
public class {Controller}DocsTest {

    @Mock
    private {Service} service;

    @InjectMocks
    private {Controller} controller;

    @DisplayName("{API 설명}")
    @Test
    void test01(RestDocumentationContextProvider contextProvider) throws Exception {
        var operationIdentifier = "{operation-id}";
        var responseFields = new FieldDescriptor[]{ /* ... */ };

        when(service.method(any())).thenReturn(/* mock */);

        MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
                .perform(/* request builder */)
                .andExpect(status().isOk())
                .andDo(print())
                .andDo( // Spring REST Docs
                        MockMvcRestDocumentation.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse()
                                /* snippets: pathParameters, requestFields, responseFields 등 */
                        )
                )
                .andDo( // OpenAPI
                        MockMvcRestDocumentationWrapper.document(
                                operationIdentifier,
                                DocumentUtils.getDocumentRequest(),
                                DocumentUtils.getDocumentResponse(),
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .description("{API 설명}")
                                                .tag("{Tag}")
                                                /* .requestFields / .responseFields / .pathParameters 등 */
                                                .build())
                        )
                )
        ;
    }
}
```

## adoc 템플릿

새 도메인 파일을 만들 때는 전체 템플릿, 기존 도메인 파일에 엔드포인트를 추가할 때는 헤더(`[[Tag]]`, `==`)를 빼고 `=== {API 이름}` 블록만 이어 붙인다.

**전체 템플릿 (새 도메인 파일):**

```asciidoc
[[{Tag}]]
== {섹션 제목}

* {API 그룹 설명}

=== {API 이름}

* {API 상세 설명}

[discrete]
==== 요청

include::{snippets}/{operation-id}/curl-request.adoc[]
include::{snippets}/{operation-id}/http-request.adoc[]

[discrete]
==== 응답

include::{snippets}/{operation-id}/http-response.adoc[]
include::{snippets}/{operation-id}/response-fields.adoc[]
```

**엔드포인트 추가 블록 (기존 도메인 파일):** 위에서 `=== {API 이름}` 이하만 떼어 마지막 섹션 뒤에 append.

요청 본문 → `request-fields.adoc` 추가, path parameter → `path-parameters.adoc` 추가, query parameter → `request-parameters.adoc` 추가, Authorization 헤더 → `request-headers.adoc` 추가. 응답이 204 No Content처럼 본문이 없는 경우 `response-fields.adoc`은 빼고 `http-response.adoc`만 include.

## 필드 타입 매핑

| Java 타입 | JsonFieldType |
|-----------|---------------|
| String, LocalDateTime, LocalDate, enum | `STRING` |
| Long, Integer, int | `NUMBER` |
| List, 배열 | `ARRAY` |
| 중첩 객체 | `OBJECT` |
| boolean | `BOOLEAN` |

optional 필드는 반드시 `.optional()` 체이닝.

## 주의사항

- `@AuthenticationPrincipal` 컨트롤러는 `setCustomArgumentResolvers()`로 MockMvc 직접 구성 → [references/patterns.md](references/patterns.md) 참조
- `@RoomParticipant` 등 Bearer 토큰 인증이 필요한 엔드포인트는 `Authorization` 헤더를 문서화 → [references/patterns.md](references/patterns.md) 8번 참조
- `@CookieValue`는 `.cookie(new Cookie(...))` 사용, REST Docs `requestHeaders("Cookie")` 미작동 → adoc에 텍스트 설명
- `TestJsonUtils.readValue()`로 JSON literal → 응답 객체 변환 가능
- 한글 테스트 메서드명 가능 (예: `void 등록_유저_방_참여(...)`)
- 페이지네이션 응답은 `PageResponse` 공통 필드 문서화 필요 → [references/patterns.md](references/patterns.md) 참조
