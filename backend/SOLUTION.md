# Solution — Repair Order Management API

## Build & Run

### Maven
```bash
mvn package && java -jar target/interview-1.0-SNAPSHOT.jar
```

### Docker (Cloud Native Buildpacks)
```bash
mvn spring-boot:build-image
docker run -p 8080:8080 interview:1.0-SNAPSHOT
```

## API Overview

Base URL: `http://localhost:8080/api`

### Entities
```
Customer (1) ──── (N) RepairOrder (1) ──── (N) LineItem
```

Each entity supports full CRUD with pagination on list endpoints.

API request examples are available in [`http/api.http`](http/api.http) (IntelliJ HTTP Client / VS Code REST Client).

## Testing

Tests are split across two source sets:

| Source | Plugin | Command | What runs |
|--------|--------|---------|-----------|
| `src/test/` | Surefire | `mvn test` | Unit tests (`*Test.java`) |
| `src/it/` | Failsafe | `mvn verify` | Slice + integration tests (`*IT.java`) |

### Test levels
- **Unit tests** (`src/test/`) — service logic with mocked dependencies
- **Slice tests** (`src/it/`) — `@WebMvcTest` (controllers), `@DataJpaTest` (repositories)
- **Integration tests** (`src/it/`) — `@SpringBootTest` full stack

### Coverage
JaCoCo collects coverage from both surefire and failsafe runs and produces a merged report.

```bash
mvn verify        # runs all tests + generates merged coverage report
```

## Architecture Decision Records

- [ADR-001: Layered Architecture over Hexagonal](docs/adr/001-layered-architecture-with-entity-as-domain.md)
- [ADR-002: DTOs and Validation at Controller Boundary](docs/adr/002-dtos-and-validation-at-controller-boundary.md)
- [ADR-003: Error Handling Strategy](docs/adr/003-error-handling-strategy.md)
- [ADR-004: Testing Strategy](docs/adr/004-testing-strategy.md)

## H2 Console

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`
