# Customer Financing & Credit Application Service

A Spring Boot REST API for a car dealership's customer financing workflow. Covers customer management and credit application lifecycle (submission → review → approval/denial), with conditional AWS S3 document storage and SQS event publishing.

---

## Prerequisites

- Java 25
- Maven 3.x

---

## Build & Run

```bash
# Build and run the jar
mvn package -DskipTests && java -jar target/interview-1.0-SNAPSHOT.jar

# Or run directly via Maven
mvn spring-boot:run
```

The server starts on port `8080`.

---

## Authentication

All `/api/v1/**` endpoints require HTTP Basic Auth:

| Field    | Value      |
|----------|------------|
| Username | `api-user` |
| Password | `changeme` |

Credentials are configured in `application.properties` via `spring.security.user.name` / `spring.security.user.password`. Swagger UI, the H2 console, and actuator endpoints are public.

---

## Explore the API

**Swagger UI** (recommended starting point — click "Authorize" and enter credentials):
```
http://localhost:8080/swagger-ui/index.html
```

**OpenAPI JSON schema:**
```
http://localhost:8080/v3/api-docs
```

**H2 console** (in-memory database — resets on every restart):
```
http://localhost:8080/h2-console
JDBC URL:  jdbc:h2:mem:testdb
Username:  sa
Password:  password
```

---

## curl Scripts

Executable demo scripts are in `curl/customers/` and `curl/credit_applications/`. Make them executable once, then run them against a live server:

```bash
chmod +x curl/customers/*.sh curl/credit_applications/*.sh
```

Full usage and expected responses for every script are documented in [docs/curl-demo.md](docs/curl-demo.md).

---

## Running the Tests

```bash
mvn test
```

Runs all JUnit 5 unit tests, Spring integration tests (`@SpringBootTest`, `@WebMvcTest`, `@DataJpaTest`), and Spock/Groovy mapper specs in a single pass.

See [docs/testing.md](docs/testing.md) for a breakdown of every test class and how to run individual categories.

---

## Domain Overview

### Entities

| Entity | Key fields |
|--------|-----------|
| `Customer` | name, email (unique), SSN (masked in responses), date of birth (18+ required), employment details, annual income |
| `CreditApplication` | customer FK, requested loan amount, status, supporting documents |
| `SupportingDocument` | document type, S3 object key, optional file name hint |

### Application Status State Machine

```
SUBMITTED → UNDER_REVIEW → APPROVED
                         → DENIED
```

`APPROVED` and `DENIED` are terminal — no further transitions are permitted. Attempting an invalid transition returns `409 Conflict`.

### Custom Validation

| Annotation | Rule |
|-----------|------|
| `@ValidSSN` | Must match `XXX-XX-XXXX` format |
| `@ValidAdultAge` | Date of birth must be 18+ years ago |
| `@ValidLoanAmount` | Requested amount ≤ customer's annual income × 5 |

---

## AWS Integration (optional, disabled by default)

`aws.enabled=false` is the default. The application runs fully without AWS credentials — no-op stubs handle all S3 and SQS calls so every endpoint works locally.

To enable real AWS integration, update `application.properties`:

```properties
aws.enabled=true
spring.cloud.aws.sqs.enabled=true
aws.region=us-east-1
aws.sqs.queue-url=https://sqs.us-east-1.amazonaws.com/<account>/<queue>
aws.s3.bucket-name=<bucket>
```

### Document Upload Flow (S3)

Supporting documents use presigned S3 URLs — the app server never handles file bytes:

1. **Create** a credit application → `201` response includes `documentUploadUrls[]` — one presigned S3 `PUT` URL per document, valid **15 minutes**. The client uploads directly to S3.
2. **Read** any application → response includes `documentDownloadUrls[]` — fresh presigned S3 `GET` URLs valid **60 minutes**, regenerated on every request.

### SQS Events

When an application transitions to `UNDER_REVIEW`, a message is published to the configured SQS queue containing `applicationId` and `customerId`. Locally this is a no-op.

---

## Project Structure

```
src/main/java/com/interview/
├── controller/        # REST controllers (CustomerController, CreditApplicationController)
├── service/           # Business logic and state machine
├── persistence/       # JPA entities, repositories, enums
├── dto/               # Request/response DTOs with Bean Validation and @Schema annotations
├── mapper/            # MapStruct entity↔DTO mappers
├── validation/        # Custom constraint annotations and validators
├── exception/         # Sealed exception hierarchies (DealershipException, AwsException)
├── advice/            # GlobalExceptionHandler — ProblemDetail responses
├── aws/               # S3DocumentService, SqsPublisher — real and no-op implementations
└── config/            # Spring cache, JPA auditing, OpenAPI, security configuration

src/main/resources/
├── database/schema.sql   # DDL — table and index definitions
└── database/data.sql     # Seed data — 3 customers, 3 applications, 5 supporting documents

src/test/
├── java/              # JUnit 5 unit + integration tests
└── groovy/            # Spock mapper specs
```
