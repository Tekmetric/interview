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

### Implementation description

This backend provides a Spring Boot REST API that implements CRUD operations for a single domain object (`RepairOrder`) persisted in an in-memory H2 database. The API supports:
- `POST /api/repair-orders` (create)
- `GET /api/repair-orders/{id}` and `GET /api/repair-orders` (read single + list)
- `PUT /api/repair-orders/{id}` (update)
- `DELETE /api/repair-orders/{id}` (delete)

## Technologies used
- Spring Boot (upgraded to `4.0.3`) with Spring Web, Spring Data JPA, Validation, Spring Security, Actuator, and Springdoc OpenAPI
- Java (`25`)
- Persistence: Hibernate/JPA + H2 (`ddl-auto` disabled so schema is not generated implicitly)
- Schema migrations: Flyway (versioned SQL migrations under `src/main/resources/db/migration`)
- Seed/test data: `src/main/resources/database/data.sql`

## Upgrades performed
- Updated the project baseline (Spring Boot + Java) to match 2026-era dependency and framework practices.
- Introduced Flyway-managed database schema migrations.
- Added OpenAPI/Swagger documentation for the secured API.
- Added production-minded cross-cutting concerns:
  - DTOs + mapping to keep API contracts separate from the persistence model
  - Bean Validation at the API boundary
  - Global exception handling producing a consistent error payload (`ApiErrorDto`)
  - Security with HTTP Basic and role-based method authorization (`@EnableMethodSecurity` + `@PreAuthorize`)
  - Optimistic concurrency via `@Version` to prevent lost updates
  - Request boundary logging with correlation id (MDC)
  - Rate limiting on `/api/repair-orders/**`

## Architecture principles applied
- Layered design: `Resource/Controller` (HTTP) -> `Service` (use cases/transactions) -> `Repository` (data access) -> JPA `Entity`
- Stable API contracts: request/response DTOs (instead of exposing entities)
- Consistent client experience: uniform JSON error responses for validation, not-found, conflicts, and security errors
- Security-by-default: minimal public surface area, and explicit role controls for sensitive endpoints

## Postman collection (manual API testing)
A Postman collection is included to test the full `/api/repair-orders` flow end-to-end:
- Collection file: `backend/repair-order.postman_collection.json`

To run it:
1. Open Postman.
2. `Import` the collection JSON file "repair-order.postman_collection.json".
3Run the request group named `Scenario Runner (Admin Create -> User Checks -> Admin Delete)`:
   - Click `Run` (Collection Runner) and set iterations to `1`.
   - The runner uses `postman.setNextRequest(...)` to execute steps in sequence.
