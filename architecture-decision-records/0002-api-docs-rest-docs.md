# 2. api docs rest docs

> Date: 2025-08-03

---

## Status

> 상태: 제안됨(Proposed)|승인됨(Accepted)|반려됨(Rejected)|대체됨(Superseded)|사용중단됨(Deprecated)

Accepted

## 상황(Context)

> 이 결정이 필요하게 된 배경과 문제 상황을 설명합니다. 여기에는 결정을 내리게 된 기술적, 비즈니스적, 또는 규제적 조건들이 포함될 수 있습니다.

- 모두타임 프로젝트에서 API 문서화를 위해 RestDocs를 사용하기로 결정했습니다.
- 백엔드개발과 프론트개발이 비동기적으로 수행되며 API를 먼저 개발하여 반영하는 개발방식을 사용 중입니다.

## 결정(Decision)

> 선택한 솔루션에 대한 명확한 설명을 제공합니다. 여기에는 고려된 대안들과 그 대안들을 배제한 이유도 포함될 수 있습니다.

- 모두타임 프로젝트는 프로젝트 초기부터 TDD 를 적용하여 테스트 코드가 촘촘합니다.
- E2E API 테스트도 항상 작성하는 프로젝트입니다.
- API 테스트가 필수인 프로젝트 이므로 Spring RestDocs를 사용하여 API 문서를 작성합니다.

## 영향(Consequence)

> 결정을 내린 후 무슨 일이 벌어졌는지를 설명합니다(프로젝트나 조직에 미치는 영향을 평가). 결정(Decision) 후 (좋은 혹은 나쁜)모든 결과를 기술합니다.

- 앞으로 API 문서화는 RestDocs를 통해 자동으로 생성됩니다.
- 컨트롤러 테스트 코드를 필수로 작성해야 합니다.

## 참조(Reference)

> 기술결정과 관련된 참조정보를 기술합니다.

- [Spring RestDocs](https://spring.io/projects/spring-restdocs)
- https://helloworld.kurly.com/blog/spring-rest-docs-guide/
- https://www.openapis.org/
