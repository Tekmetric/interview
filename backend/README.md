# Job Posting API — Tekmetric Interview Exercise

A production-quality Spring Boot REST API for managing job vacancies. 

## Tech Stack

| Layer | Library                      |
|-------|------------------------------|
| Framework | Spring Boot 4.0.5            |
| ORM | Spring Data JPA + Hibernate  |
| Migrations | NONE                         |
| Mapping | MapStruct                    |
| Boilerplate | Lombok                       |
| Docs | Springdoc / Scalar           |
| Errors | RFC 7807    |
| Observability | Spring Actuator              |
| DB | H2 (in-memory)               |
| Tests | JUnit 5 + MockMvc + JavaFaker |

---

## Quickstart

### Prerequisites
- Java 21+
- Maven 3.6+

#### Build and run your app
```bash 
mvn package && java -jar target/interview-1.0-SNAPSHOT.jar
```

#### Run tests
```bash
mvn test
```

---

## Useful URLs (after startup)

| URL | Description |
|-----|-------------|
| http://localhost:8080/scalar | Interactive API docs |
| http://localhost:8080/v3/api-docs | Raw OpenAPI JSON |
| http://localhost:8080/h2-console | H2 database browser |
| http://localhost:8080/actuator/health | Health check |
| http://localhost:8080/actuator/info | App info |

H2 console credentials: **JDBC URL** `jdbc:h2:mem:testdb` · **User** `sa` · **Password** `password`

---

## API Endpoints

### CRUD
| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/job-postings` | Create (defaults to DRAFT) |
| `GET` | `/api/job-postings/{id}` | Get by ID |
| `GET` | `/api/job-postings` | Paginated list with filters |
| `PUT` | `/api/job-postings/{id}` | Full update |
| `DELETE` | `/api/job-postings/{id}` | Delete |


### Filters (all optional, combinable)
```
GET /api/job-postings?remote=true
GET /api/job-postings?location=Houston
GET /api/job-postings?titleContains=engineer
GET /api/job-postings?remote=true&titleContains=devops&page=0&size=10&sort=postedAt,desc
```
---

## Demo API functionality using API client tool

The project includes a pre-configured api-demo.http file for rapid testing.

Run the App:

```bash 
mvn spring-boot:run
```

or
```bash 
mvn package && java -jar target/interview-1.0-SNAPSHOT.jar
```


Execute Requests: Open [api-demo.http](./api-demo.http) in IntelliJ Ultimate or VS Code (with REST Client extension).

Interactive UI: Visit http://localhost:8080/scalar to test endpoints directly in the browser.


##  Roadmap & Next Steps
To evolve this service into a production-grade microservice, the following improvements are prioritized:

- PostgreSQL Migration: Move from H2 to a persistent, standalone PostgreSQL database.
- Liquibase: Implement database versioning to manage schema changes safely.
- Docker & Docker Compose: Containerize the application and its dependencies (DB, Redis) for consistent deployments.
- Testcontainers: Integrate Testcontainers into the CI/CD pipeline to run integration tests against a real PostgreSQL instance.
- Spring Security: Add JWT-based authentication to protect sensitive write/delete operations.
- Redis Caching: Implement a caching layer
- Micrometer
