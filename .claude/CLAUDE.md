# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Modutime is a meeting time coordination service that helps groups find optimal meeting times by aggregating participant availability. The application is built with Spring Boot and follows clean architecture with event-driven patterns.

**Tech Stack:** Java 17, Spring Boot 2.7.8, Spring Data JPA, MySQL, Spring REST Docs

## Common Commands

### Development

```bash
# Run the application (Mac)
./run.sh

# If permission denied:
chmod +x ./run.sh
chmod +x ./install-docker.sh
```

### Testing

```bash
# Run all tests
./gradlew test

# Run API documentation tests only (generates REST Docs snippets)
./gradlew apiDocsTest

# Run a specific test class
./gradlew test --tests "com.dnd.modutime.core.room.RoomServiceTest"

# Run a specific test method
./gradlew test --tests "com.dnd.modutime.core.room.RoomServiceTest.방을_생성한다"
```

### Building

```bash
# Build without API docs
./gradlew build

# Build with API docs included in JAR
./gradlew bootJar -Pinclude-api-docs

# Generate API documentation (AsciiDoc)
./gradlew asciidoctor

# Generate OpenAPI 3.0 spec
./gradlew openapi3
```

## Code Architecture

### language
- Java 17
- use val or var where possible

### Domain Structure

The codebase follows **vertical slice architecture** with domains organized under `src/main/java/com/dnd/modutime/core/`:

- **room/** - Meeting room aggregate (Room, RoomDate)
- **participant/** - Participant management and authentication
- **timeblock/** - Individual participant availability submissions
- **timetable/** - Room-level aggregated availability (query-optimized view)
- **adjustresult/** - Precomputed optimal meeting time candidates

Each domain follows clean architecture layers:
- `domain/` - Entities and value objects
- `application/` - Services, commands, responses
- `controller/` - REST endpoints
- `repository/` - Repository interfaces
- `util/` - Domain-specific utilities (factories, converters, sorters)

### Core Domain Flow

The application maintains two parallel data models for performance:

1. **TimeTable** (query-optimized): Aggregated view of who is available when
2. **AdjustmentResult** (precomputed cache): Calculated optimal meeting times

**Data Flow:**
```
Participant submits availability
  → TimeBlock updated (publishes TimeBlockReplaceEvent)
  → TimeTable synced via event handler
  → TimeTable publishes TimeTableReplaceEvent
  → AdjustmentResult recalculated via event handler
```

### Event-Driven Architecture

The application uses Spring Domain Events with `@TransactionalEventListener` for cross-aggregate communication:

- `TimeBlockEventHandler` - Syncs TimeTable when TimeBlocks change
- `TimeTableEventHandler` - Handles participant lifecycle events
- `AdjustmentResultEventHandler` - Recalculates results when TimeTable changes

Event handlers use `@Transactional(propagation = Propagation.REQUIRES_NEW)` for separate transactions.

### Key Design Patterns

**Strategy Pattern with Factories:**
- `AdjustmentResultExecutorFactory` - Chooses between cached results vs on-the-fly computation
- `CandidateDateTimesSorterFactory` - Provides sorting strategies (FastFirst, LongFirst)
- `CandidateDateTimeConvertorFactory` - Handles date-only vs date+time room modes

**Repository Pattern:**
- Domain layer defines interfaces (e.g., `ParticipantRepository`)
- Infrastructure layer provides JPA implementations (`ParticipantJpaRepository`)

### Important Abstractions

- **TimeProvider** - Testable time abstraction (avoid `LocalDateTime.now()` in domain code)
- **TSID** - Used for generating unique UUIDs for Rooms (distributed ID generation)
- **DisplayableEnum** - Base for enums that need JSON serialization
- **PageRequest/PageResponse** - Custom pagination wrappers

## Coding Standards

### Language and Comments

- Use Korean for variable names, method names, and comments
- Write Javadoc in Korean for complex methods
- Acceptance test methods use Korean DSL (e.g., `방_생성()`, `시간을_등록한다()`)

### Error Response Format

모든 HTTP 에러 응답은 `com.dnd.modutime.core.common.ErrorResponse` 단일 포맷을 사용한다.

스키마:
```json
{ "code": "MT4xx | ErrorCode.name()", "message": "...", "status": 4xx }
```

- 일반 컨트롤러 예외는 `GlobalControllerAdvice`가 처리하며, 응답 바디로 `ExceptionResponse` 같은 다른 DTO를 새로 만들지 않는다.
- 신규 `@ExceptionHandler`를 추가할 때:
  - `core.common.ErrorCode`에 적절한 항목이 있으면 그것을 매핑한다. 없으면 enum에 새 항목을 추가한 뒤 매핑한다.
  - `ErrorResponse.from(ErrorCode, message, status)` 팩토리를 사용한다. 직접 `new ErrorResponse(...)` 호출은 Security 필터 계층(`SecurityErrorCodeResponseHandler`, `OAuth2LogoutFilter`)으로 제한한다.
  - 메시지는 예외의 `getMessage()`를 우선 사용하고, 비어있을 때만 `ErrorCode.getDescription()`로 fallback한다.
- Security 필터/핸들러에서 직접 에러를 직렬화할 때도 같은 `ErrorResponse` 포맷을 유지한다. 새로운 에러 응답 DTO를 만들지 않는다.
- `BindException`은 첫 번째 fieldError의 `defaultMessage`만 message로 노출한다 (다중 필드 에러 처리는 별도 정책 결정 필요).

### Testing Requirements

**TDD is mandatory** - write tests before implementation:

1. **API Documentation Tests** - Required for all controller endpoints
   - Tag with `@ApiDocsTest`, located in `src/test/java/.../controller/`
   - **주의:** 스니펫 생성만으로는 사용자 HTML에 노출되지 않는다. `src/docs/asciidoc/{도메인}.adoc`에 `include::{snippets}/...`까지 추가해야 한다.
   - 상세 단계 + 체크리스트: [docs/api-documentation.md](docs/api-documentation.md)

2. **Acceptance Tests** - End-to-end API tests
   - Extend `AcceptanceSupporter` base class
   - Use Korean method names for readability
   - Located in `src/test/java/.../acceptance/`

3. **Unit Tests** - Domain logic tests
   - Test entities and value objects
   - Located in `src/test/java/.../domain/`

4. **Integration Tests** - Service layer tests with database
   - Use `@SpringBootTest`
   - Located in `src/test/java/.../integration/`

### Test Tag System

Tests use JUnit 5 tags for organization:
- `@ApiDocsTest` - API documentation tests (run by `apiDocsTest` task)
- `@Tag("unit")` - Unit tests
- `@Tag("integration")` - Integration tests
- `@Tag("exclude")` - Excluded from standard test runs

## Architecture Decision Records

The project uses ADR (Architecture Decision Records) stored in `architecture-decision-records/`:

**Key Decisions:**
- Spring REST Docs is used for API documentation (mandatory controller tests)
- TDD approach with comprehensive test coverage

To create new ADR:
```bash
brew install adr-tools
adr new {decision-name}
```

## API Documentation

API 문서화는 **Spring REST Docs (AsciiDoc → HTML)** 와 **OpenAPI 3.0 (YAML)** 두 가지를 동시에 생성한다. 두 산출물 모두 `@ApiDocsTest` 한 곳에서 파생되지만, REST Docs HTML 노출에는 `src/docs/asciidoc/*.adoc` 의 include 갱신이 추가로 필요하다.

전체 파이프라인, 신규 엔드포인트 체크리스트, 자주 발생하는 실수 사례는 [docs/api-documentation.md](docs/api-documentation.md) 를 참고한다.

## Working with the Codebase

### Adding a New Feature

1. Identify the domain (Room, Participant, TimeBlock, etc.)
2. Start with domain model changes (entities, value objects)
3. Write domain unit tests first
4. Implement service layer logic
5. Add controller endpoint with `@ApiDocsTest` (스니펫 생성)
6. **`src/docs/asciidoc/{도메인}.adoc` 에 새 operationId의 include 블록 추가** — 빼먹기 쉬움, [docs/api-documentation.md](docs/api-documentation.md) 체크리스트 참고
7. If cross-aggregate coordination needed, use domain events

### Understanding Time Adjustment Logic

The core scheduling algorithm works as follows:

1. **TimeTable Structure:**
   - TimeTable → DateInfo (per date) → TimeInfo (per time slot) → TimeInfoParticipantName
   - Contains all participant availability aggregated at room level

2. **CandidateDateTime Generation:**
   - `AdjustmentResultReplaceService` converts TimeTable → CandidateDateTime
   - Merges consecutive time slots into time ranges
   - Filters by participant intersection

3. **Sorting Strategies:**
   - `FastFirstSorter` - Earliest times first (soonest available slots)
   - `LongFirstSorter` - Longest duration first (maximum time windows)

### Room Modes

Rooms support two scheduling modes:

- **Date-only mode** - Participants mark entire days as available
- **Date+time mode** - Participants specify specific time ranges

The convertor factory selects appropriate strategy based on room configuration.

### API Versioning

- V1 API: Returns top 5 results (hardcoded limit)
- V2 API: Supports pagination with custom `PageRequest`/`PageResponse`

## Important Files

- `application.yaml` / `application-db.yaml` - Configuration (profile-based)
- `build.gradle` - Build configuration with REST Docs setup
- `.junie/guidelines.md` - Additional coding guidelines
- `architecture-decision-records/` - Architecture decisions

## Deployment

### Blue-Green 배포

운영 배포는 GitHub Actions(`deploy-prod.yml`)를 통한 블루-그린 방식으로 수행된다.

**흐름:** Green EC2 생성 → 앱 배포 → 헬스체크(/aws) → NLB 타겟 그룹 전환 → Blue EC2 종료

- Launch Template(`lt-01450247ef5b97f69`)으로 Green EC2를 임시 생성
- 배포 실패 시 Green만 정리되고 Blue는 유지됨 (자동 롤백)
- 배포 완료 후 EC2는 항상 1대만 유지 (비용 최적화)

### Graceful Shutdown

- `application-server.yaml`에 prod 프로파일로 설정
- `server.shutdown=graceful` + `timeout-per-shutdown-phase=30s`
- SIGTERM 수신 시 진행 중인 요청을 최대 30초 대기 후 종료

### Database Migration SQL

- 필요한 마이그레이션/DDL SQL은 **레포 루트 `db/migrations/`** 에 둔다 (`src/main/resources/` 아래 X).
- 파일명 규칙: `V<YYYYMMDD>_<UPPER_SNAKE 설명>.sql`. 같은 날 2개 이상이면 `V<YYYYMMDD>_2_...` 처럼 순번 접미사.
- 상단에 한 줄 한글 요약 주석을 남긴다.
- Flyway/Liquibase 의존성은 설정되어 있지 않다 → 이 파일들은 prod(`ddl-auto=none`)에 **수동 적용**되는 스크립트 모음이다. local/test는 H2 `ddl-auto`로 엔티티에서 자동 생성된다.

## Development Notes

- Room entities use UUID instead of DB IDs for public API (security/obfuscation)
- JPA Auditing is enabled - use `@EntityListeners(AuditingEntityListener.class)` for timestamps
- Timezone is set to 'Asia/Seoul' for tests
- UTF-8 encoding is enforced for all compilation and tests
