# API Documentation Guide

Spring REST Docs + OpenAPI 3.0 으로 API 문서를 자동 생성한다. 이 문서는 신규/변경 엔드포인트를 추가할 때 빠뜨리기 쉬운 단계를 명시한다.

## 2단계 파이프라인 (중요)

REST Docs 문서화는 **두 단계**다. 두 단계 모두 갱신해야 사용자가 보는 HTML 에 노출된다.

```
@ApiDocsTest 클래스 실행
       │
       ▼  ./gradlew apiDocsTest
build/generated-snippets/{operationId}/*.adoc      ← 1단계: 스니펫 생성
       │
       ▼  ./gradlew asciidoctor
src/docs/asciidoc/{도메인}.adoc 의 include::{snippets}/{operationId}/... 라인이 끌어옴
       │
       ▼
build/docs/asciidoc/*.html                         ← 2단계: 사용자가 보는 문서
```

**자주 발생하는 실수:** 1단계만 하고 2단계를 빼먹는다. `apiDocsTest` 는 스니펫만 검증하고 통과하며, `asciidoctor` 는 "없는 스니펫을 include 하면" 실패하지만 "있는 스니펫을 include 안 한 경우" 는 침묵하기 때문에 CI 로도 안 잡힌다. 사례: PR #165 (commit `054ac3b`) — DocsTest 는 추가됐지만 `src/docs/asciidoc/auth-oauth2.adoc` include 가 빠져 두 신규 operation 이 HTML 에서 누락됨.

## 신규/변경 엔드포인트 추가 시 체크리스트

1. **컨트롤러에 `@ApiDocsTest` 추가**
   - 파일: `src/test/java/com/dnd/modutime/controller/{도메인}/{Controller}DocsTest.java`
   - `MockMvcFactory.getRestDocsMockMvc(...)` 로 호출
   - `MockMvcRestDocumentation.document(operationId, ...)` 와 `MockMvcRestDocumentationWrapper.document(...)` 를 함께 호출 (REST Docs + OpenAPI 동시 생성)
   - `operationId` 컨벤션: `{도메인}-{method}-{path-kebab}` 예: `oauth2-post-kakao-native-login`

2. **adoc include 추가**
   - 파일: `src/docs/asciidoc/{도메인}.adoc`
   - 같은 도메인 파일이 없으면 새로 만들고 `src/docs/asciidoc/index.adoc` 에 `include::{도메인}.adoc[]` 추가
   - 기본 패턴:
     ```adoc
     === {기능 이름}

     * 한 줄 설명
     * 실패 응답 (4xx/5xx) 목록 (있다면)

     [discrete]
     ==== 요청

     include::{snippets}/{operationId}/curl-request.adoc[]
     include::{snippets}/{operationId}/http-request.adoc[]
     include::{snippets}/{operationId}/path-parameters.adoc[]     // 있을 때만
     include::{snippets}/{operationId}/request-parameters.adoc[]  // 있을 때만
     include::{snippets}/{operationId}/request-headers.adoc[]     // 있을 때만
     include::{snippets}/{operationId}/request-fields.adoc[]      // JSON 바디일 때

     [discrete]
     ==== 응답

     include::{snippets}/{operationId}/http-response.adoc[]
     include::{snippets}/{operationId}/response-fields.adoc[]
     ```

3. **검증**
   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 17)
   ./gradlew apiDocsTest asciidoctor
   ```
   - `build/generated-snippets/{operationId}/` 에 9개 파일이 생겼는지
   - `build/docs/asciidoc/index.html` 에 새 섹션이 렌더링됐는지

## Gradle 태스크 레퍼런스

| 명령 | 역할 |
|---|---|
| `./gradlew apiDocsTest` | `@ApiDocsTest` 만 실행 → 스니펫 생성 |
| `./gradlew asciidoctor` | adoc → HTML 빌드 (`build/docs/asciidoc/`) |
| `./gradlew openapi3` | OpenAPI 3.0 YAML 생성 (`restdocs-api-spec` 플러그인) |
| `./gradlew bootJar -Pinclude-api-docs` | API 문서를 JAR 에 포함해서 빌드 |

## 출력 위치

- 스니펫 (중간 산출물): `build/generated-snippets/{operationId}/*.adoc`
- HTML (사용자 가시): `build/docs/asciidoc/{도메인}.html`, `index.html`
- OpenAPI YAML: `build/api-spec/openapi3.yaml`

## ADR

- 결정: API 문서화는 Spring REST Docs 를 사용한다 (컨트롤러 테스트가 강제됨)
- 자세한 내용: `architecture-decision-records/` 참고
