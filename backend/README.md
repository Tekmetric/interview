# Java Spring Boot API Coding Exercise

## Steps to get started:

#### Prerequisites
- Maven
- Java 1.8 (or higher, update version in pom.xml if needed)

#### Fork the repository and clone it locally
- https://github.com/Tekmetric/interview.git

#### Import project into IDE
- Project root is located in `backend` folder

#### Build and run your app
- `mvn package && java -jar target/interview-1.0-SNAPSHOT.jar`

#### Test that your app is running
- `curl -X GET   http://localhost:8080/api/welcome`

#### After finishing the goals listed below create a PR

### Goals
1. Design a CRUD API with data store using Spring Boot and in memory H2 database (pre-configured, see below)
2. API should include one object with create, read, update, and delete operations. Read should include fetching a single item and list of items.
3. Provide SQL create scripts for your object(s) in resources/data.sql
4. Demo API functionality using API client tool

### Considerations
This is an open ended exercise for you to showcase what you know! We encourage you to think about best practices for structuring your code and handling different scenarios. Feel free to include additional improvements that you believe are important.

#### H2 Configuration
- Console: http://localhost:8080/h2-console 
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

### Submitting your coding exercise
Once you have finished the coding exercise please create a PR into Tekmetric/interview


# Tekmetric Estimate Builder API

This is a Spring Boot CRUD API for a simplified auto repair estimate builder. It models estimates, work orders, and seeded parts so a technician can create an estimate first, then add labor and parts as work orders are diagnosed.

## Tech Stack

- Java 26
- Spring Boot 4.0.6
- Spring Web
- Spring Data JPA
- Jakarta Bean Validation
- H2 in-memory database
- Lombok
- Springdoc OpenAPI and Swagger UI
- JUnit, Mockito, and MockMvc

## JVM Setup

This project targets Homebrew OpenJDK. If `java -version` does not work, expose the Homebrew JDK in your shell:

```zsh
export JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
```

The repository includes shared IntelliJ run configurations:

- `Run Application`
- `Run Tests`

## Run The App

```zsh
mvn -Dmaven.repo.local=.m2/repository spring-boot:run
```

Health check:

```zsh
curl http://localhost:8080/api/health
```

Swagger UI:

- http://localhost:8080/swagger-ui.html

OpenAPI JSON:

- http://localhost:8080/v3/api-docs

## Run Tests

```zsh
mvn -Dmaven.repo.local=.m2/repository test
```

## H2 Console

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

The schema and seed data live in `src/main/resources/database/data.sql`.

## Application Properties

- `spring.jpa.hibernate.ddl-auto=none`: Hibernate will not create or update tables; the SQL script owns schema setup.
- `spring.sql.init.mode=always`: Spring runs SQL initialization every time the in-memory database starts.
- `spring.sql.init.schema-locations=classpath:database/data.sql`: points Spring at the schema/seed file.
- `spring.sql.init.continue-on-error=false`: startup fails if the SQL script has a problem.
- `springdoc.api-docs.path=/v3/api-docs`: serves the generated OpenAPI JSON document.
- `springdoc.swagger-ui.path=/swagger-ui.html`: serves the Swagger UI page.
- `spring.jpa.defer-datasource-initialization=false`: SQL initialization runs during normal datasource startup.
- `spring.h2.console.enabled=true`: enables the browser-based H2 console for local debugging.

In SQL, `CONSTRAINT` names a database rule such as a foreign key, uniqueness rule, enum-like check, or positive-value check. Named constraints make schema intent and database errors easier to understand.

## Domain Rules

- Parts are seeded reference data and include `price`.
- Work orders include `summary`, `notes`, labor rate, labor time, status, and needed parts.
- A work order can require multiple quantities of the same part through `quantity`; duplicate part IDs in a request are rejected.
- Work order `totalCost` is calculated as `(laborTime * laborRate) + sum(part.price * quantity)`.
- Estimate `totalCost` and `totalTime` include only work orders that are not `REFUSED`.
- Estimate responses include work-order summaries, but not nested part details.
- Work order detail responses include parts needed.
- Work order lists sort `REFUSED` work orders to the bottom.
- API requests and responses use DTOs, not raw JPA entities.

## Endpoints

### Health

```http
GET /api/health
```

### Parts

Parts are read-only in this exercise.

```http
GET /api/parts
```

### Work Orders

```http
POST /api/work-orders
GET /api/work-orders/{id}
GET /api/work-orders?page=0&size=20&status=PENDING
PUT /api/work-orders/{id}
DELETE /api/work-orders/{id}
```

### Estimates

```http
POST /api/estimates
POST /api/estimates/{estimateId}/work-orders
GET /api/estimates/{id}
GET /api/estimates?page=0&size=20&customerId={uuid}&status=PENDING
PUT /api/estimates/{id}
DELETE /api/estimates/{id}
```

Estimate creation intentionally starts without work orders. Customer and vehicle identity are set when the estimate is
created; later updates only accept `status` and `workOrderIds`, and append work orders instead of replacing the existing
list.

## Example Demo Flow

List seeded parts:

```zsh
curl http://localhost:8080/api/parts
```

Create an empty estimate:

```zsh
curl -X POST http://localhost:8080/api/estimates \
  -H 'Content-Type: application/json' \
  -d '{
    "customerId": "12121212-1212-1212-1212-121212121212",
    "vehicleId": "99999999-9999-9999-9999-999999999999"
  }'
```

Create a standalone work order:

```zsh
curl -X POST http://localhost:8080/api/work-orders \
  -H 'Content-Type: application/json' \
  -d '{
    "vehicleId": "99999999-9999-9999-9999-999999999999",
    "status": "PENDING",
    "summary": "Replace front brake pads",
    "notes": "Pads are worn below recommended thickness.",
    "laborRate": 100.00,
    "laborTime": 2.00,
    "partsNeeded": [
      { "partId": "11111111-1111-1111-1111-111111111111", "quantity": 2 }
    ]
  }'
```

Create and attach a new work order to an estimate:

```zsh
curl -X POST http://localhost:8080/api/estimates/{estimateId}/work-orders \
  -H 'Content-Type: application/json' \
  -d '{
    "vehicleId": "99999999-9999-9999-9999-999999999999",
    "status": "PENDING",
    "summary": "Replace front brake pads",
    "notes": "Pads are worn below recommended thickness.",
    "laborRate": 100.00,
    "laborTime": 2.00,
    "partsNeeded": [
      { "partId": "11111111-1111-1111-1111-111111111111", "quantity": 2 }
    ]
  }'
```

Filter estimates:

```zsh
curl 'http://localhost:8080/api/estimates?customerId=12121212-1212-1212-1212-121212121212&status=PENDING&page=0&size=10'
```

## Error Handling And Debugging

Known failures return a consistent error shape with a timestamp, status, message, and field-level validation details when available. Validation covers required IDs, enum values, positive labor values, part quantities, pagination bounds, missing resources, and invalid references.

Create, update, and delete flows add relevant IDs to MDC, such as `estimateId`, `workOrderId`, and `partId`, so request logs are easier to correlate during debugging.
