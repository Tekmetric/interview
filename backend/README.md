# Customer Management API - Tekmetric Interview Project

A comprehensive Spring Boot REST API for managing customers, vehicles, and service packages with JWT authentication, built for the Tekmetric interview process.

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.6+
- Docker & Docker Compose (for production setup)

### Development Mode (H2 In-Memory Database)
```bash
# Clone and navigate to project
git clone https://github.com/AdrianCodesio/interview-tekmetric.git
cd backend

# Run from IntelliJ with 'dev' profile active
# Or via command line:
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Or package and run
mvn package
java -jar target/interview-1.0-SNAPSHOT.jar --spring.profiles.active=dev
```

### Production Mode with MySQL (Local Development)

Option 1: MySQL via Docker + Run from IDE
```bash
# Start only MySQL
docker-compose up -d mysql

# Run from IntelliJ with 'prod' profile active
# Or via command line:
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

Option 2: Full Docker Deployment
```bash
# Copy environment template
cp .env.example .env
# Edit .env with your secure passwords

# Start complete stack
docker-compose up -d

# View logs
docker-compose logs -f app
```

Access Points:

H2 Console: http://localhost:8080/h2-console

Swagger UI: http://localhost:8080/swagger-ui/index.html

Health Check: http://localhost:8080/actuator/health

## Project Overview

### What It Does
This REST API manages automotive service shop operations with three core entities:

- **Customers** - Basic customer information with optional profiles
- **Vehicles** - Customer vehicle inventory
- **Service Packages** - Subscription-based service offerings

### Core Features
- ✅ Complete CRUD operations for all entities
- ✅ JWT-based authentication with role-based access (ADMIN/USER) - *Demo implementation for showcasing auth/authorization patterns. Production should integrate with Keycloak / Auth0 / AWS Cognito / Azure AD / etc.*
- ✅ Advanced search and filtering with pagination
- ✅ Optimistic locking for concurrent updates
- ✅ Comprehensive validation and error handling
- ✅ Request correlation tracking
- ✅ OpenAPI/Swagger documentation

### API Endpoints

#### Authentication
- `POST /auth/login` - Authenticate and receive JWT token

#### Customers
- `GET /api/v1/customers` - List all customers (USER & ADMIN)
- `GET /api/v1/customers/paginated` - Paginated customer list (USER & ADMIN)
- `GET /api/v1/customers/{id}` - Get customer by ID (USER & ADMIN)
- `POST /api/v1/customers` - Create customer (ADMIN)
- `PUT /api/v1/customers/{id}` - Update customer (ADMIN)
- `DELETE /api/v1/customers/{id}` - Delete customer (ADMIN)

#### Vehicles
- `GET /api/v1/vehicles` - List all vehicles (USER & ADMIN)
- `GET /api/v1/vehicles/search` - Advanced vehicle search (USER & ADMIN)
- `GET /api/v1/vehicles/{id}` - Get vehicle by ID (USER & ADMIN)
- `POST /api/v1/vehicles` - Create vehicle (ADMIN)
- `PUT /api/v1/vehicles/{id}` - Update vehicle (ADMIN)
- `DELETE /api/v1/vehicles/{id}` - Delete vehicle (ADMIN)

#### Service Packages
- `GET /api/v1/service-packages` - List packages with status filter (USER & ADMIN)
- `GET /api/v1/service-packages/{id}` - Get package details (USER & ADMIN)
- `POST /api/v1/service-packages` - Create package (ADMIN)
- `PUT /api/v1/service-packages/{id}` - Update package (ADMIN)
- `PATCH /api/v1/service-packages/{id}/status` - Activate/deactivate (ADMIN)
- `POST /api/v1/service-packages/{id}/subscribe` - Subscribe customer (ADMIN)
- `DELETE /api/v1/service-packages/{id}/customers/{customerId}` - Unsubscribe (ADMIN)
- `GET /api/v1/service-packages/{id}/subscribers` - Get subscribers (ADMIN)

**Demo Credentials:**
- Admin: `admin` / `admin123` (full access)
- User: `user` / `user123` (read-only access)

### Documentation
- **API Documentation**: [Swagger UI](http://localhost:8080/swagger-ui/index.html)
- **Detailed Architecture Flow**: See `docs/Architecture Diagram.pdf`
- **Database Schema**: See `docs/Database Schema Documentation.pdf`

## Architecture Overview

### Technology Stack
- **Framework**: Spring Boot 3.5.4 with Java 21
- **Security**: Spring Security with JWT authentication
- **Data**: Spring Data JPA with Hibernate
- **Database**: H2 (dev) / MySQL 8.0 (prod)
- **Migration**: Flyway for schema versioning
- **Mapping**: MapStruct for DTO transformations
- **Validation**: Bean Validation with custom validators
- **Documentation**: OpenAPI 3 / Swagger
- **Testing**: JUnit 5, Mockito, Jacoco, Spring Boot Test
- **Quality**: Checkstyle with Google Java Style

### Layered Architecture

**Controller Layer**
- REST endpoints with OpenAPI documentation
- Request validation and security enforcement
- HTTP status code mapping

**Service Layer**
- Business logic and transaction management
- DTO mapping and data transformation

**Repository Layer**
- Data access with Spring Data JPA
- Dynamic queries using Specifications
- N+1 query prevention with EntityGraphs or Join Fetch

**Entity Layer**
- JPA entities with proper relationships
- Audit fields and optimistic locking
- Database constraints and validation

### Key Design Patterns
- **Repository Pattern** - Clean data access layer
- **DTO Pattern** - Separate internal/external representations
- **Specification Pattern** - Type-safe dynamic queries
- **Global Exception Handling** - Consistent error responses

## Database Design

### Schema Overview
- **customers** - Core customer data with optimistic locking
- **customer_profiles** - Extended customer information (1:1)
- **vehicles** - Vehicle inventory (N:1 to customers)
- **service_packages** - Service offerings with soft delete (active/inactive)
- **customer_service_packages** - Many-to-many subscription between customers and services

### Migration Strategy
- **Flyway** manages all schema changes with versioned SQL scripts
- **Development**: Auto-applies migrations on startup with sample data loading
- **Production**: Auto-applies migrations on startup with sample data loading
- **Testing**: Clean database state with sample data for each test

### Key Features
- **Audit Fields**: Created/updated timestamps and user tracking
- **Optimistic Locking**: Prevents concurrent update conflicts
- **Referential Integrity**: Proper foreign keys with cascade rules
- **Performance**: Strategic indexes on search columns

*For complete ERD and table specifications, see `docs/database-design.pdf`*

## Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Run integration tests only
mvn test -Dtest="*IntegrationTest"
```

### Test Coverage:

- **Unit Tests**: Services with mocked dependencies, custom validators
- **Repository Tests**: JPA repositories with H2 in-memory database (@DataJpaTest)
- **Integration Tests**: Full Spring context with complete request/response cycles

## API Testing with Postman

### Postman Collection
A comprehensive Postman collection is available for testing all API endpoints:

**Location**: `docs/Tekmetric.postman_collection.json`

## Configuration Profiles
### Development Profile (dev)

- **Database**: H2 in-memory
- **Use Case**: Local development
- **Configuration**: Flyway migrations

### Test Profile (test)

- **Database**: H2 in-memory
- **Use Case**: Automated testing
- **Configuration**: Clean state per test, sample data

## Production Profile (prod)

- **Database**: MySQL 8.0
- **Use Case**: Production deployment
- **Configuration**: Flyway migrations, connection pooling

### Monitoring & Observability

- **Health Checks**: /actuator/health
- **Correlation IDs**: Request tracing via `X-Correlation-ID` header (auto-generated if not provided by client) with automatic MDC population for log correlation
- **Structured Logging**: Correlation ID included in all log entries via logback pattern configuration
- **Metrics**: Spring Boot Actuator endpoints for application monitoring

## Development Notes

### Code Quality
- **Checkstyle**: Google Java Style via Checkstyle
- **Architecture**: Clean separation of concerns
- **Security**: Input sanitization, XSS prevention
- **Performance**: N+1 query prevention, connection pooling

### Checkstyle Command
```bash
mvn checkstyle:check
```

## Production Readiness Improvements
While this implementation demonstrates core functionality and best practices for the interview process, several enhancements would be required before production deployment:

### Security & Authentication

- **Secrets Management**: Replace secrets with AWS Secrets Manager, Azure Key Vault, or HashiCorp Vault
- **Identity Providers**: Replace demo JWT implementation with enterprise solutions:
  - **Keycloak** - Self-hosted, full-featured identity management
  - **Auth0** - Cloud-native identity platform with easy integration
  - **AWS Cognito** - Scalable user directory and authentication service
  - **Azure AD** - Enterprise identity and access management
- **HTTPS/TLS**: Configure SSL certificates and enforce HTTPS endpoints

### Performance & Scalability

- **Caching Layer**: Implement Redis/Caffeine for frequently accessed data
- **Rate Limiting**: Add API throttling via:
  - **Application Level**: Spring Security + Bucket4j library
  - **Infrastructure Level**: Kong Gateway, AWS API Gateway, or Azure API Management
- **Performance Testing**: Load testing with JMeter to identify bottlenecks

### Code Quality & Security

- **SonarQube Integration**: Continuous code quality analysis and technical debt monitoring
- **Snyk Security Scanning**: Automated dependency vulnerability detection and remediation

### Monitoring & Observability

- **Custom Metrics**: Prometheus for JVM metrics 
- **Dashboards**: Grafana visualization for system health and metrics
- **Centralized Logging**: ELK Stack (Elasticsearch, Logstash, Kibana) for log aggregation and analysis
