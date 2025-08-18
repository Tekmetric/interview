## Data Schema

### Customer
### Address
a customer can have multiple addresses

---

## Features & Tech Stacks

### CRUD APIs for Customer
- **Create a single customer with addresses**
    - Public API for registration (no JWT authentication required)
    - Business rule: no more than 3 users with the same last name in the database
    - Include `Location` header in response (REST convention)
- **Read all customers**
    - Pagination by page number and page size
    - Sort by email or last name
    - Generic fuzzy search using Spring Data JPA Specification
- **Read one customer by ID**
- **Update a customer** (addresses updated via a separate API)
    - Optimistic update using row version
- **Update password**
    - Action-based update by providing old and new passwords
- **Delete a customer by ID**
    - Only Admin role can delete a customer

### Authentication & Authorization
- **Login API**
    - Authenticate via email/password using Spring Security
    - Use stateless JWT for horizontal scalability
    - Returns JWT access token in response body
    - Returns JWT refresh token in `HttpOnly` header
- **Refresh API**
    - Pass in the JWT refresh token and receive a new JWT access token
- **Role-based authorization**
    - Admin and User have different permissions on APIs
- **Password hashing**
    - Passwords hashed with `BCryptPasswordEncoder`
    - Authentication via `AuthenticationManager`

### Data & Persistence
- `JPA / Hibernate` for ORM
- `H2` in-memory database
- `UUID` as primary key (for security & horizontal scalability)
- One-to-many relationship: one customer can have many addresses
- Avoid `N+1 problem
    - `@EntityGraph` to avoid N+1 on read
    - Use database cascade instead of JPA cascade to avoid N+1 on deletion
- Transactions
    - Customer creation and addresses insertion done in a single DB transaction

### DTO & Validation
- DTOs hide internal entities and support different request/response formats
- Generic page DTO for pagination
- Jakarta Bean Validation for field validation
- Custom validation annotations (e.g., `@Lowercase`)
- MapStruct used for DTO ↔ Entity mapping

### Configuration & Caching
- Redis for in-memory caching
- Cache warmup using `CommandLineRunner` on app startup
- Cache eviction after create/update/delete via Spring AOP
- Complex cache keys using SpEL (sort, page number, page size, etc.)

### Exception Handling
- Global-level and Controller-level exception handling via `@ExceptionHandler`

### Additional Tools & Practices
- Controller ↔ Service ↔ Repository
- Lombok to reduce boilerplate
- Read configs from `application.properties` via a Config class or `@Value`
- Dockerization
- Unit tests using Spring Test (MockMvc) and Mockito
- OpenAPI, Swagger UI

---

## Future Considerations

- Address entity CRUD
- Add logout feature to invalid the token
- Rate limiter
- API monitoring and pressure testing
- Internationalization
- Account lockout after failed login attempts
- Add 2FA support
- Audit trail
- API versioning
- Environment profiles
- CI/CD pipeline
- Use application.yml
---

## Build and Run

### Docker (Recommended)
- Run `docker compose up --build`
- To check redis `docker exec -it interview-redis redis-cli`

### Non-docker
- Run `docker run --name redis -p 6379:6379 -d redis:latest` (first time only)
  - `docker start redis`
  - `docker stop redis`
- Run `docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management` (first time only)
  - `docker start rabbitmq` 
  - `docker stop rabbitmq`
- Run `mvn clean package -DskipTests && java -jar target/interview-1.0-SNAPSHOT.jar`

### H2 Configuration
- Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

### Rabbit Management UI
- Console: http://localhost:15672/
- Username: guest
- Password: guest

### OpenAPI
- Swagger UI: http://localhost:8080/swagger-ui/index.html#/
- raw OpenAPI specification: http://localhost:8080/api-docs

### Run Test
- All tests: `mvn test`
- Single test: `mvn test -Dtest=CustomerControllerTest#shouldGetCustomersWithPaginationAndSorting`















