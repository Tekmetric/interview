# Testing

## Running tests
```bash
mvn test              # unit tests only (surefire)
mvn verify            # unit + integration tests (surefire + failsafe)
mvn verify -DskipTests  # integration tests only
```

## Test source sets

### Unit tests — `src/test/`
Fast, isolated tests with no full Spring context or database.

- `domain/` — value objects (Vin, PhoneNumber) — plain JUnit
- `api/controller/` — request validation — @WebMvcTest with mocked services
- `api/request/` — Bean Validation constraints — plain JUnit
- `api/mapper/` — DTO-to-domain mapping — plain JUnit on MapStruct-generated code
- `repository/mapper/` — domain-to-entity mapping — plain JUnit on MapStruct-generated code
- `repository/converter/` — JPA attribute converters (VinConverter, PhoneNumberConverter) — plain JUnit
- `service/` — business logic — Mockito mocks for repositories

### Integration tests — `src/it/`
* Separated source set via build-helper-maven-plugin, run by maven-failsafe-plugin.
* Real Spring context and H2 database.
* @Transactional so each test rolls back automatically.
* `api/controller/` — end-to-end API-to-database — @SpringBootTest + MockMvc + @Sql test data
* `repository/` — repository queries and JPA mappings — @DataJpaTest + @Sql test data

### Test data and fixtures
* SQL scripts in `src/it/resources/datasets/` loaded via @Sql.
* Each dataset has a corresponding fixture class in `src/it/java/com/interview/fixture/` with ID constants.
  * `vehicle-data.sql` → `VehicleDataFixture` (customer and vehicle IDs)
  * `work-order-data.sql` → `WorkOrderDataFixture` (work order, part, and labor IDs)
* Tests import fixture constants rather than declaring their own.

## Conventions

### Recursive comparisons
* Prefer AssertJ recursive comparison over field-by-field assertions to avoid missing coverage and keep tests concise.
* `ignoringFields(...)` for lazy-loaded associations or fields that can't be compared directly.

```java
assertThat(found).usingRecursiveComparison()
    .ignoringFields("customer")
    .isEqualTo(saved);
```

### Query count assertions
* Custom AssertJ assert in `src/it/java/com/interview/assertion/QueryAssert.java`.
* Verifies Hibernate query counts via session statistics. Catches N+1 queries and unexpected operations.
* Hibernate statistics enabled in `src/test/resources/application.yml`.

```java
assertThatQuery(statistics)
    .hasQueryCount(1)
    .hasNoOtherOperations();
```
