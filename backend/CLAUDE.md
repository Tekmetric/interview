# Tekmetric Interview - Backend

## Project Overview
Spring Boot CRUD API for a repair order management system. Interview take-home exercise (round 3 of 5, tech lead position).

## Tech Stack
- Java 25
- Spring Boot 4+ (latest LTS)
- H2 in-memory database
- Maven
- Lombok (where it makes sense, prefer records and plain Java)
- [OpenSpec](https://github.com/Fission-AI/OpenSpec) for spec-driven AI-assisted development

## Code Quality
- **Checkstyle** — code style enforcement
- **SpotBugs** — static bug analysis
- **JaCoCo** — test coverage reporting

## Build & Run
```bash
mvn package && java -jar target/interview-1.0-SNAPSHOT.jar
```

## Docker
- Use Spring Boot Maven plugin with Cloud Native Buildpacks (CNB) — no Dockerfile needed
- Build image: `mvn spring-boot:build-image`

## Architecture Decisions

### Layered Architecture (Option A)
- **By-layer** package structure: `controller/`, `service/`, `repository/`, `model/`
- Entity serves as the domain model (no separate domain POJO layer)
- DTOs at the API boundary only
- Pragmatic choice for scope — document awareness of hexagonal in ADRs

### Naming
- Controllers use `*Controller` (rename existing `WelcomeResource` to `WelcomeController`)
- API prefix: `/api/`

### DTOs
- Commands for writes (`CreateRepairOrderCommand`), Dto suffix for reads (`RepairOrderSummaryDto`)
- Separate summary/detail DTOs where appropriate (e.g. list vs get-by-id)
- Use Java records for DTOs (immutable, concise)
- Use Lombok for JPA entities (need mutability)
- `PageDto<T>` wraps paginated results — no Spring `Page`/`Pageable` in controllers
- MapStruct for entity ↔ DTO mapping (mappers live in `service/`)

### Data Model
```
Customer (1) ──── (N) RepairOrder (1) ──── (N) LineItem
```
- **Customer:** name, email, phone
- **RepairOrder:** description, status (enum), timestamps — belongs to Customer
- **LineItem:** description, unit price — belongs to RepairOrder

### Validation
- Bean Validation (`@Valid`, `@NotNull`, `@Size`) on command DTOs at the service level
- `@Validated` on service classes, `@Valid` on command parameters
- Service is the validation boundary — keeps controllers thin

### Error Handling
- `@ExceptionHandler` on controllers for domain-specific exceptions (e.g. `RepairOrderNotFoundException`)
- Global `@ControllerAdvice` for cross-cutting concerns (validation errors, unexpected exceptions)
- Structured JSON error responses

### Pagination
- Service accepts primitives (`page`, `size`, `sort`, `direction`) and returns `PageDto<T>`
- Spring `Page`/`Pageable` used internally in service — not exposed to controllers

### Testing Strategy
- **Unit tests (`src/test/`):** Service logic with mocked dependencies. Run via `maven-surefire-plugin` (`*Test.java`).
- **Slice + Integration tests (`src/it/`):** `@WebMvcTest`, `@DataJpaTest`, `@SpringBootTest`. Run via `maven-failsafe-plugin` (`*IT.java`).

### Documentation
- ADRs in `docs/adr/` as plain markdown (no doc tooling)
- Keep ADRs short (10-15 lines): title, context, decision, consequences
- Document tradeoffs and what you'd do differently at scale

### Logging
- Use Lombok `@Slf4j`
- **Controller** (app entry point): `log.info` — `"GET /api/resource [page={}, size={}]", page, size`
- **Service** (layer entry point): `log.debug` — `"Finding resource [id={}]", id`
- Message format: `"Message [var={}, var={}]"` — natural language, params grouped in brackets

## Git Conventions

### Conventional Commits
- `feat:` new feature
- `fix:` bug fix
- `docs:` documentation only
- `test:` adding or updating tests
- `refactor:` code change that neither fixes a bug nor adds a feature
- `chore:` build, config, tooling changes

### Conventional Branching
- `feature/` — new features (e.g. `feature/repair-order-crud`)
- `fix/` — bug fixes
- `docs/` — documentation changes
- `test/` — test additions
- `chore/` — tooling, config

## HTTP Client
- API request examples in `http/api.http` (IntelliJ / VS Code REST Client)

## Key Principles
- Pragmatism over purity — match architecture to scope
- Clean, readable code over clever abstractions
- Test coverage at every layer is the priority
- Show architectural awareness through ADRs, not extra layers
