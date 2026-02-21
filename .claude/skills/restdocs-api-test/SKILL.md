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
3. `.adoc` 파일 생성
4. `src/docs/asciidoc/index.adoc`에 include 추가
5. `./gradlew apiDocsTest` 실행 후 검증

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

요청 본문 → `request-fields.adoc` 추가, path parameter → `path-parameters.adoc` 추가, query parameter → `request-parameters.adoc` 추가, Authorization 헤더 → `request-headers.adoc` 추가.

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
