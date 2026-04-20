# ADR-004: Testing Strategy — Testing Trophy over Test Pyramid

## Context
We need a testing approach that gives confidence in correctness without overinvesting in test infrastructure for a small CRUD application. We follow the **testing trophy** model rather than the traditional test pyramid — emphasizing component tests over a heavy base of unit tests, complemented by static analysis (Checkstyle, SpotBugs) as the foundation.

## Decision
We combine static analysis with three levels of tests:
- **Unit tests (`src/test/`, `*Test.java`):** Service logic with mocked dependencies. Fast, isolated, focused on business rules. Run via `maven-surefire-plugin`.
- **Component tests (`src/it/`, `*ComponentTest.java`):** `@WebMvcTest` for controllers (HTTP layer, serialization, error handling) and `@DataJpaTest` for repositories (queries, entity mappings). These load only the relevant Spring context slice. Run via `maven-failsafe-plugin`.
- **Integration tests (`src/it/`, `*IT.java`):** `@SpringBootTest` with full context, hitting actual endpoints end-to-end against the H2 database. Run via `maven-failsafe-plugin`.

**Testing conventions:**
- BDD style with `// Given`, `// When`, `// Then` comments in every test
- Mockito BDD: `given(...).willReturn(...)`, `org.mockito.BDDMockito.then(...).should()` (fully qualified to avoid AssertJ clash)
- AssertJ BDD: `then(result).isEqualTo(...)`, `thenThrownBy(...)` from `BDDAssertions`
- Soft assertions: `BDDSoftAssertions.thenSoftly(softly -> { ... })` when a test has multiple assertions — reports all failures at once
- Instancio for test data generation
- Nested test classes per method under test
- Test method names follow `givenX_whenY_thenZ` structure
- `@DisplayName` on test classes and methods for human-readable output
- `MockMvc` for component tests — no real network, tests the Spring MVC layer in-process
- `RestTestClient` for integration tests — calls through the real network stack

## Alternatives Considered
- **Integration tests only (`@SpringBootTest`):** simplest setup, tests the full stack. But slow, hard to pinpoint failures, and doesn't validate layers in isolation.
- **Unit tests only (mocked everything):** fast but misses Spring wiring issues — serialization not tested, JPA mappings not verified.
- **Unit + integration (no component tests):** covers both ends but leaves a gap — controller serialization and repository query correctness are only caught by the slow integration tests.

## Consequences
- **Static analysis as the foundation:** Checkstyle and SpotBugs catch style violations and common bugs before any test runs — the base of the trophy.
- **Component tests carry the most weight:** they validate real Spring behavior (serialization, JPA mappings) without the cost of a full application context. This is where most confidence comes from.
- **Unit tests are targeted, not exhaustive:** we unit-test meaningful business logic, not trivial delegation.
- **Integration tests are the safety net:** they prove the full stack works together but are not the primary debugging tool.
