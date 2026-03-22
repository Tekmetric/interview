# Running the Test Suite

## Prerequisites

- Java 25
- Maven 3.x (`mvn -version`)

---

## Run Everything

```bash
mvn test
```

Runs all unit tests, integration tests, and Spock/Groovy mapper specs in one pass.
Surefire picks up `**/*Test.java` and `**/*Spec.groovy` automatically — no extra flags needed.

---

## Test Categories

### Unit Tests

Fast, no Spring context, all dependencies mocked with Mockito.

| Class | What it covers |
|-------|---------------|
| `CustomerServiceTest` | `CustomerService` business logic — create, find, update, delete; cache annotations; duplicate email handling |
| `CreditApplicationServiceTest` | `CreditApplicationService` — create with S3 upload and document persistence, presigned GET URLs on reads, `findByCustomerId` pagination and 404, status state machine, SQS event published after commit, `decidedAt` stamping |
| `SSNValidatorTest` | `@ValidSSN` — valid format, wrong format, null |
| `AdultAgeValidatorTest` | `@ValidAdultAge` — exactly 18, under 18, null |
| `CustomerTest` | `Customer` entity — UUID assigned at construction, two instances have distinct IDs, `equals`/`hashCode` stability |
| `CreditApplicationTest` | `CreditApplication` entity — default status, `@PrePersist submittedAt`, audit fields |
| `S3DocumentServiceImplTest` | `S3DocumentServiceImpl` — presigned PUT URL generation per document, presigned GET URL generation per persisted document, SDK invoked once per document |
| `NoOpS3DocumentServiceTest` | `NoOpS3DocumentService` — upload and download URL generation, URL contains object key, empty list returns empty |
| `NoOpSqsPublisherTest` | `NoOpSqsPublisher` — publish does not throw, logs correctly |
| `SqsPublisherImplTest` | `SqsPublisherImpl` — message sent to correct queue, message body contains application ID |

```bash
# Run only unit tests (fast, no Spring context)
mvn test -Dtest="CustomerServiceTest,CreditApplicationServiceTest,SSNValidatorTest,AdultAgeValidatorTest,CustomerTest,CreditApplicationTest"
```

---

### Integration Tests

Spin up a Spring context (full or slice) backed by H2. Slower but exercise wiring, caching, and persistence end-to-end.

| Class | Slice | What it covers |
|-------|-------|---------------|
| `CustomerServiceIntegrationTest` | `@SpringBootTest` | Cache population (`@Cacheable`), eviction (`@CacheEvict`), put-on-update (`@CachePut`) |
| `CreditApplicationServiceIntegrationTest` | `@SpringBootTest` | Same cache lifecycle for credit applications; S3/SQS beans mocked via `@MockitoBean` |
| `CustomerRepositoryIntegrationTest` | `@DataJpaTest` | JPA queries against H2 — save, find, pagination, duplicate email constraint |
| `CreditApplicationRepositoryIntegrationTest` | `@DataJpaTest` | `findByCustomerId` (paginated), status filter via JPA Specification, pagination |
| `CustomerControllerIntegrationTest` | `@WebMvcTest` | Full HTTP layer — 201 create, 400 validation errors, 404 not found, 409 duplicate email; JSON response structure |
| `CreditApplicationControllerIntegrationTest` | `@WebMvcTest` | 201 create (with `documentUploadUrls[]`), paginated `findByCustomer`, 400/404/409 scenarios, PATCH status transitions |

```bash
# Run only integration tests
mvn test -Dtest="*IntegrationTest"

# Run only repository slice tests
mvn test -Dtest="CustomerRepositoryIntegrationTest,CreditApplicationRepositoryIntegrationTest"

# Run only controller slice tests
mvn test -Dtest="CustomerControllerIntegrationTest,CreditApplicationControllerIntegrationTest"
```

---

### Spock / Groovy Mapper Specs

Groovy-based specs using the Spock Framework. Run through GMavenPlus and are included automatically in `mvn test`. They test MapStruct mapper logic in isolation — no Spring context.

| Spec | What it covers |
|------|---------------|
| `CustomerMapperSpec` | `CustomerMapper` — `toResponse` flattens address/employment fields, SSN masked to `***-**-XXXX`, `toEntity` ignores audit fields, `updateEntity` leaves `ssn`/`dateOfBirth` unchanged |
| `CreditApplicationMapperSpec` | `CreditApplicationMapper` — `toResponse` maps `customer.id` → `customerId` and builds `customerName`, `documentUploadUrls`/`documentDownloadUrls` excluded from mapper (set in service), `toEntity` ignores all server-managed fields including `documents` |

```bash
# Run only Spock specs
mvn test -Dtest="CustomerMapperSpec,CreditApplicationMapperSpec"
```

---

## Useful Flags

```bash
# Skip tests entirely (e.g. just build the jar)
mvn package -DskipTests

# Run a single test method
mvn test -Dtest="CreditApplicationServiceTest#create_validRequest_returnsDocumentUploadUrls"

# Show full stack traces on failure
mvn test -e

# Rerun flaky tests automatically (Surefire 3+)
mvn test -Dsurefire.rerunFailingTestsCount=2
```

---

## Test Output

Surefire reports are written to `target/surefire-reports/`. To view a summary after a failure:

```bash
cat target/surefire-reports/*.txt
```
