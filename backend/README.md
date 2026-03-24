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

## Future Enhancements

If I continued evolving this project beyond the exercise, these are the next additions I would prioritize and how I would approach them:

- Database migrations
  - Replace ad hoc schema setup in `data.sql` with Flyway or Liquibase so schema changes are versioned, reviewable, and repeatable. I would move table and sequence creation into numbered migrations and keep seed/demo data separate for local development and tests.

- Production database parity
  - Add a PostgreSQL profile and run persistence-focused integration tests against PostgreSQL with Testcontainers. That closes the gap between the in-memory H2 development setup and a more realistic production database.

- Better operational readiness
  - Expand Actuator usage with health, readiness, liveness, and build/info endpoints, then document which endpoints should be exposed in each environment. I would also disable development-only features such as the H2 console outside local use.

- Containerization
  - Add a `Dockerfile` and, if useful, a `docker-compose.yml` for local app plus database startup. That makes the project easier to run consistently locally.

- Environment configuration
  - Split configuration into local/test/prod profiles and push secrets or environment-specific values into environment variables. That keeps the app simple while showing a production-minded configuration model.

- API Versioning
  - If the API were to gain external consumers, I would introduce a clear versioning and deprecation strategy, most likely path-based versioning such as `/api/v1`. Alternatively, I would introduce a new GraphQL API and gradually migrate to it.

- Concurrency contract at the API boundary
  - The application already uses optimistic locking internally. A next step would be to expose that more explicitly to clients through a version field, then add integration tests that prove stale updates are rejected with `409 Conflict`.

- Security and rate limiting
  - If this moved beyond a coding exercise, I would add authentication/authorization with Spring Security and introduce basic rate limiting at the edge.

- Observability
  - Add request correlation, structured logs, and metrics that surface key behaviors such as request counts, error rates, and latency. That would pair well with Actuator and make the service easier to operate and troubleshoot.
