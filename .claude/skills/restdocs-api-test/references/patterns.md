# REST Docs 테스트 패턴 레퍼런스

## 목차

1. [POST with Request Body](#1-post-with-request-body)
2. [GET with Path Parameters](#2-get-with-path-parameters)
3. [GET with Query Parameters + Pagination](#3-get-with-query-parameters--pagination)
4. [Cookie 기반 인증](#4-cookie-기반-인증)
5. [커스텀 Argument Resolver (@AuthenticationPrincipal)](#5-커스텀-argument-resolver)
6. [DELETE with Request Body](#6-delete-with-request-body)
7. [PageResponse 공통 필드](#7-pageresponse-공통-필드)

---

## 1. POST with Request Body

POST 요청 + JSON body + 응답이 있는 패턴. 가장 빈번한 패턴.

```java
var requestFields = new FieldDescriptor[]{
        fieldWithPath("title").type(STRING).description("방 제목"),
        fieldWithPath("roomDates").type(ARRAY).description("방 날짜 목록"),
        fieldWithPath("roomDates[].availableDate").type(STRING).description("날짜"),
        fieldWithPath("headcount").type(NUMBER).description("예상 인원"),
};

var responseFields = new FieldDescriptor[]{
        fieldWithPath("roomUuid").type(STRING).description("방 UUID"),
};

//language=JSON
var requestLiteral = """
        {
          "title": "모두의 회의",
          "roomDates": [{ "availableDate": "2025-07-16" }],
          "headcount": 5
        }
        """;

var response = TestJsonUtils.readValue(responseLiteral, RoomCreationResponse.class);
when(roomService.create(any())).thenReturn(response);

MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
        .perform(
                post("/guest/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestLiteral)
        )
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(MockMvcRestDocumentation.document(operationIdentifier,
                DocumentUtils.getDocumentRequest(),
                DocumentUtils.getDocumentResponse(),
                PayloadDocumentation.requestFields(requestFields),
                PayloadDocumentation.responseFields(responseFields)
        ))
        .andDo(MockMvcRestDocumentationWrapper.document(operationIdentifier,
                DocumentUtils.getDocumentRequest(),
                DocumentUtils.getDocumentResponse(),
                ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                                .description("Guest용 방 생성 API")
                                .tag("Room-Guest")
                                .requestFields(requestFields)
                                .responseFields(responseFields)
                                .build())
        ))
;
```

## 2. GET with Path Parameters

Path variable이 있는 GET 요청.

```java
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.RequestDocumentation;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

var pathParameters = new ParameterDescriptor[]{
        parameterWithName("roomUuid").description("방 UUID")
};

var responseFields = new FieldDescriptor[]{
        fieldWithPath("title").type(STRING).description("방 제목"),
        fieldWithPath("deadLine").type(STRING).description("마감 일시").optional(),
};

MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
        .perform(
                get("/guest/api/v2/room/{roomUuid}", "test-room-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
        )
        // ... andDo 블록에서:
        // Spring REST Docs:
        RequestDocumentation.pathParameters(pathParameters),
        PayloadDocumentation.responseFields(responseFields)
        // OpenAPI ResourceSnippetParameters:
        .responseFields(responseFields)
        .pathParameters(pathParameters)
```

## 3. GET with Query Parameters + Pagination

`.param()`으로 쿼리 파라미터 전달, `RequestDocumentation.requestParameters()`로 문서화.

```java
var queryParameters = new ParameterDescriptor[]{
        parameterWithName("page").description("페이지"),
        parameterWithName("size").description("페이지당 조회크기"),
        parameterWithName("sorted").description("정렬 조건").optional(),
        parameterWithName("participantNames").description("참여자 이름 목록").optional(),
};

MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
        .perform(
                get("/api/v1/room/{roomUuid}/adjustment-results", "test-room-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "1")
                        .param("size", "5")
                        .param("participantNames", "채민", "동호")
        )
        // ... andDo 블록에서:
        // Spring REST Docs:
        RequestDocumentation.pathParameters(pathParameters),
        RequestDocumentation.requestParameters(queryParameters),
        PayloadDocumentation.responseFields(responseFields)
        // OpenAPI ResourceSnippetParameters:
        .responseFields(responseFields)
        .pathParameters(pathParameters)
        .requestParameters(queryParameters)
```

## 4. Cookie 기반 인증

`@CookieValue` 파라미터가 있는 엔드포인트. REST Docs 2.x에서는 `requestCookies()` 미지원.

```java
import javax.servlet.http.Cookie;  // jakarta 아님!

MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
        .perform(
                post("/oauth2/reissue-token")
                        .cookie(new Cookie("refreshToken", "mock-refresh-token"))
        )
        // requestHeaders("Cookie") 사용 불가 - SnippetException 발생
        // 대신 adoc에 텍스트로 설명:
        // "* 요청 시 `Cookie: refreshToken={리프레시 토큰}` 헤더를 포함해야 합니다."
```

## 5. 커스텀 Argument Resolver

`@AuthenticationPrincipal`이나 커스텀 어노테이션이 있는 컨트롤러는 `MockMvcFactory` 대신 직접 MockMvc를 구성해야 한다.

```java
import org.springframework.core.MethodParameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

var documentationConfigurer = documentationConfiguration(contextProvider);
documentationConfigurer.uris().withScheme("https").withHost(LOCALHOST).withPort(443);

MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter,
                                          ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest,
                                          WebDataBinderFactory binderFactory) {
                return mockOAuth2User;  // 테스트용 mock 객체
            }
        })
        .apply(documentationConfigurer)
        .build();

// 이후 mockMvc.perform(...) 으로 사용 (MockMvcFactory 대신)
```

Mock OAuth2User 생성:

```java
private OAuth2User createMockOAuth2User() {
    var user = new User("테스트유저", "test@example.com", "profile.jpg", "thumb.jpg", OAuth2Provider.KAKAO);
    return new OAuth2User(user, Collections.singletonMap("id", "12345"), "id");
}
```

## 6. DELETE with Request Body

DELETE 요청에 JSON body가 있는 패턴 (비표준이지만 프로젝트에서 사용).

```java
MockMvcFactory.getRestDocsMockMvc(contextProvider, LOCALHOST, controller)
        .perform(
                delete("/guest/api/room/{roomUuid}/participants", "test-room-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestLiteral)
        )
```

## 7. PageResponse 공통 필드

페이지네이션 응답(`PageResponse<T>`)에 포함되는 공통 필드. content 내부 필드는 엔드포인트별로 다름.

```java
var responseFields = new FieldDescriptor[]{
        // 공통 페이지네이션 필드
        fieldWithPath("pageRequest").type(OBJECT).description("페이징 요청정보"),
        fieldWithPath("pageRequest.page").type(NUMBER).description("페이징 요청 페이지번호"),
        fieldWithPath("pageRequest.size").type(NUMBER).description("페이징 요청 페이징크기(=limit)"),
        fieldWithPath("pageRequest.offset").type(NUMBER).description("페이징 요청 오프셋"),
        fieldWithPath("total").type(NUMBER).description("전체 데이터 수"),
        fieldWithPath("isFirst").type(BOOLEAN).description("첫 번째 페이지 여부"),
        fieldWithPath("isLast").type(BOOLEAN).description("마지막 페이지 여부"),
        fieldWithPath("hasContent").type(BOOLEAN).description("컨텐츠 존재여부"),
        fieldWithPath("hasNext").type(BOOLEAN).description("다음페이지 존재여부"),
        fieldWithPath("hasPrevious").type(BOOLEAN).description("이전페이지 존재여부"),
        fieldWithPath("totalPages").type(NUMBER).description("전체 페이지 수"),

        // content 배열 (엔드포인트별로 다름)
        fieldWithPath("content").type(ARRAY).description("컨텐츠 목록"),
        fieldWithPath("content[].id").type(NUMBER).description("..."),
        // ...
};
```

Mock 응답 생성 시 `TypeReference` 사용:

```java
Mockito.when(service.search(any(), any()))
        .thenReturn(TestJsonUtils.readValue(responseLiteral,
                new TypeReference<PageResponse<SomeResponseDto>>() {}));
```
