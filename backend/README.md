# CarShop — Spring Boot REST API

CarShop is a RESTful API for managing a car dealership inventory. It supports full CRUD operations on car entities, JWT-based authentication, role-based access control (ADMIN / EMPLOYEE), paginated and filtered listing, structured logging, and a full observability stack (Prometheus + Loki + Grafana).

---

## Prerequisites

- Java 21
- Maven 3.x
- Docker + Docker Compose *(optional — only needed for the observability stack)*

---

## Running Locally

### Maven CLI

Verify you are using Java 21 before building:

```bash
java -version
```

If the version is not 21, point `JAVA_HOME` to your Java 21 installation before running Maven:

```bash
export JAVA_HOME=/path/to/java21
export PATH=$JAVA_HOME/bin:$PATH
```

Then build and run:

```bash
# 1. Build the project (skipping tests for speed)
mvn clean package -DskipTests

# 2. Run the packaged jar
java -jar target/interview-1.0-SNAPSHOT.jar
```

The app starts on **http://localhost:8080**.

Alternatively, build and run in one step (no jar produced):

```bash
mvn spring-boot:run
```

### Observability Stack (Docker)

```bash
docker compose up -d
```

---

## Running Tests

```bash
mvn test
```

---

## Useful URLs

| URL | Description |
|-----|-------------|
| http://localhost:8080/swagger-ui/index.html | Swagger UI — interactive API docs & testing |
| http://localhost:8080/h2-console | H2 in-memory database console |
| http://localhost:8080/actuator/health | Application health check |
| http://localhost:8080/actuator/prometheus | Prometheus metrics endpoint |
| http://localhost:3000 | Grafana — metrics & log dashboards *(admin / admin)* |
| http://localhost:9090 | Prometheus — raw metrics explorer |

**H2 Console connection:** JDBC URL `jdbc:h2:mem:testdb` · user `sa` · password `password`

---

## Project Structure

```
src/main/java/com/interview/
├── CarShopApplication.java        # Entry point
├── api/                           # OpenAPI interfaces
├── config/                        # Spring Security + OpenAPI configuration
├── controller/                    # HTTP handlers
├── service/                       # Business logic
├── repository/                    # Repository classes
├── mapper/                        # Entity to/from DTO conversion
├── model/                         # JPA entities
├── dto/                           # Request/Response records (CarRequest, CarResponse, etc.)
├── security/                      # JWT generation and request filter
├── validator/                     # Business-rule validation
└── exception/                     # Exception handling

src/main/resources/
├── application.yml                # App configuration (DB, actuator, JWT, logging)
├── logback-spring.xml             # Logging config (console + Loki appender)
└── database/
    ├── schema.sql                 # Table definition
    └── data.sql                   # 5 seed cars pre-loaded on every startup

observability/
├── prometheus/prometheus.yml      # Scrape config targeting host.docker.internal:8080
└── grafana/provisioning/          # Auto-provisioned Prometheus + Loki datasources

docker-compose.yml                 # Prometheus + Loki + Grafana stack
```

---

## Authentication & Authorization

### In-memory users

| Username | Password | Role | Allowed operations |
|----------|----------|------|--------------------|
| `admin` | `adm123` | ADMIN | GET, POST, PUT, DELETE |
| `employee` | `emp123` | EMPLOYEE | GET, PUT |

### How it works

1. Call `POST /login` with your credentials → receive a JWT token (valid 24 hours)
2. Pass the token in every subsequent request via the `Authorization: Bearer <token>` header
3. Unauthenticated requests → **401 Unauthorized**
4. Authenticated but wrong role → **403 Forbidden**

### Open endpoints (no token required)

`/login` · `/actuator/**` · `/swagger-ui/**` · `/v3/api-docs/**` · `/h2-console/**`

---

## API Reference

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/login` | Authenticate and receive a JWT token | Public |
| GET | `/carshop/v1/cars` | List cars with optional filtering and pagination | ADMIN, EMPLOYEE |
| GET | `/carshop/v1/cars/{id}` | Get a single car by ID | ADMIN, EMPLOYEE |
| POST | `/carshop/v1/cars` | Create a new car | ADMIN |
| PUT | `/carshop/v1/cars/{id}` | Update an existing car | ADMIN, EMPLOYEE |
| DELETE | `/carshop/v1/cars/{id}` | Delete a car | ADMIN |

---

## Using Swagger UI with JWT

1. Open **http://localhost:8080/swagger-ui/index.html**
2. Expand **Authentication → POST /login** → click **Try it out** → enter credentials → **Execute**
3. Copy the `token` value from the response body
4. Click the **Authorize** 🔓 button (top-right of the page)
5. Paste the token *(without the `Bearer ` prefix)* into the `bearerAuth` field → **Authorize** → **Close**
6. All subsequent requests in Swagger UI will automatically include the `Authorization` header

---

## Main Technologies

| Tool / Library | Purpose |
|----------------|---------|
| Spring Boot | Application framework |
| Spring Security | Authentication & authorization |
| Spring Data JPA | Database access and dynamic query specifications |
| H2 | In-memory relational database |
| JJWT | JWT token generation and validation |
| Springdoc OpenAPI | Swagger UI + OpenAPI 3 spec generation |
| Micrometer + Prometheus | Application metrics collection |
| Loki4j | Log shipping to Loki |
| Lombok | Boilerplate reduction |
| JUnit 5 + Mockito | Unit and integration testing |
| Prometheus | Metrics aggregation (Docker) |
| Loki | Log aggregation (Docker) |
| Grafana | Metrics & log visualization (Docker) |

---

### Improvements
- Addeng performance testing capabilities using JMeter or Gatling
- Introducing CI Pipeline for test running
- Adding support for db schema migration (Flyway or Liquidbase)
