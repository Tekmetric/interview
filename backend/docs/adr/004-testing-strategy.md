# ADR-004: Testing Strategy — Unit, Slice, and Integration Tests

## Context
We need a testing approach that gives confidence in correctness without overinvesting in test infrastructure for a small CRUD application. We considered relying solely on integration tests (`@SpringBootTest`) for simplicity, or adding granular tests at each layer.

## Decision
We test at three levels:
- **Unit tests:** Service logic with mocked dependencies (repository, etc.). Fast, isolated, focused on business rules.
- **Slice tests:** `@WebMvcTest` for controllers (HTTP layer, serialization, validation, error handling) and `@DataJpaTest` for repositories (queries, entity mappings, relationships). These load only the relevant Spring context slice.
- **Integration tests:** `@SpringBootTest` with full context, hitting actual endpoints end-to-end against the H2 database.

## Alternatives Considered
- **Integration tests only (`@SpringBootTest`):** simplest setup, tests the full stack. But slow, hard to pinpoint failures, and doesn't validate layers in isolation.
- **Unit tests only (mocked everything):** fast but misses Spring wiring issues — validation not triggered, serialization not tested, JPA mappings not verified.
- **Unit + integration (no slice tests):** covers both ends but leaves a gap — controller serialization and repository query correctness are only caught by the slow integration tests.

## Consequences
- **Fast feedback loop:** unit and slice tests run in seconds, catching most issues before the heavier integration tests.
- **Each layer is tested in isolation:** a controller test failing points to the controller, not a buried service bug.
- **Slice tests validate Spring wiring** (validation, serialization, JPA mappings) that unit tests with mocks would miss.
- **Integration tests are the safety net** — they prove the full stack works together but are not the primary debugging tool.
- **More test files:** three test levels for three entities means more files, but each is small and focused.
