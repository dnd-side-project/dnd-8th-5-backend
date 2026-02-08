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

### Testing Requirements

**TDD is mandatory** - write tests before implementation:

1. **API Documentation Tests** - Required for all controller endpoints
   - Tag with `@ApiDocsTest`
   - Generate REST Docs snippets
   - Located in `src/test/java/.../controller/`

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

The project generates two types of API documentation:

1. **Spring REST Docs** (AsciiDoc format)
   - Generated from controller tests tagged with `@ApiDocsTest`
   - Output: `build/docs/asciidoc/`
   - Included in JAR when building with `-Pinclude-api-docs`

2. **OpenAPI 3.0 Spec** (YAML format)
   - Generated via `openapi3` Gradle task
   - Uses `restdocs-api-spec` plugin

## Working with the Codebase

### Adding a New Feature

1. Identify the domain (Room, Participant, TimeBlock, etc.)
2. Start with domain model changes (entities, value objects)
3. Write domain unit tests first
4. Implement service layer logic
5. Add controller endpoint with `@ApiDocsTest`
6. If cross-aggregate coordination needed, use domain events

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

## Development Notes

- Room entities use UUID instead of DB IDs for public API (security/obfuscation)
- JPA Auditing is enabled - use `@EntityListeners(AuditingEntityListener.class)` for timestamps
- Timezone is set to 'Asia/Seoul' for tests
- UTF-8 encoding is enforced for all compilation and tests
