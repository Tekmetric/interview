# ADR-004: Testing Strategy — Testing Trophy over Test Pyramid

## Context
We need a testing approach that gives confidence in correctness without overinvesting in test infrastructure for a small CRUD application. We follow the **testing trophy** model rather than the traditional test pyramid — emphasizing integration and slice tests over a heavy base of unit tests, complemented by static analysis (Checkstyle, SpotBugs) as the foundation.

## Decision
We combine static analysis with three levels of tests:
- **Unit tests:** Service logic with mocked dependencies (repository, etc.). Fast, isolated, focused on business rules.
- **Slice tests:** `@WebMvcTest` for controllers (HTTP layer, serialization, validation, error handling) and `@DataJpaTest` for repositories (queries, entity mappings, relationships). These load only the relevant Spring context slice.
- **Integration tests:** `@SpringBootTest` with full context, hitting actual endpoints end-to-end against the H2 database.

## Alternatives Considered
- **Integration tests only (`@SpringBootTest`):** simplest setup, tests the full stack. But slow, hard to pinpoint failures, and doesn't validate layers in isolation.
- **Unit tests only (mocked everything):** fast but misses Spring wiring issues — validation not triggered, serialization not tested, JPA mappings not verified.
- **Unit + integration (no slice tests):** covers both ends but leaves a gap — controller serialization and repository query correctness are only caught by the slow integration tests.

## Consequences
- **Static analysis as the foundation:** Checkstyle and SpotBugs catch style violations and common bugs before any test runs — the base of the trophy.
- **Slice tests carry the most weight:** they validate real Spring behavior (validation, serialization, JPA mappings) without the cost of a full application context. This is where most confidence comes from.
- **Unit tests are targeted, not exhaustive:** we unit-test meaningful business logic, not trivial delegation. Mocking everything for the sake of coverage is avoided.
- **Integration tests are the safety net:** they prove the full stack works together but are not the primary debugging tool.
- **More test files:** three test levels for three entities means more files, but each is small and focused.
