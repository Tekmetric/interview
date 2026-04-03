# 🏃‍♂️ Athlete Management API

A REST API built with Spring Boot for managing athlete information with advanced features including filtering, pagination, sorting, rate limiting, and comprehensive testing.

## ✨ Features

### 🎯 Core Functionality
- **Complete CRUD Operations** - Create, Read, Update, Delete athletes
- **Advanced Filtering** - Filter by nationality, discipline, and name search
- **Pagination & Sorting** - Efficient data retrieval with customizable page size and multi-field sorting
- **Input Validation** - Bean validation with detailed error messages
- **Clean Architecture** - Proper separation of concerns (Controller → Service → Repository)

### 🚀 Performance & Quality
- **Database Indexes** - Strategic indexes on nationality, discipline, and composite duplicate check
- **Rate Limiting** - Global configurable rate limiting (1000 req/min) with Bucket4j
- **Request Logging** - Structured logging for all API requests
- **Virtual Threads** - Enabled virtual threads for improved concurrency
- **CORS Support** - Configurable cross-origin resource sharing

### 📚 API Documentation
- **OpenAPI 3.0/Swagger** - Interactive API documentation at `/swagger-ui.html`
- **Actuator Endpoints** - Health checks and monitoring
- **Postman Collection** - Complete API demo collection with automated tests

### 🧪 Testing & Code Quality
- **60 Comprehensive Tests** - Unit tests, integration tests, and API tests
- **Code Formatting** - Spotless with Palantir Java Format
- **CI/CD Pipeline** - Minimal CI with Github Actions

## 🚀 Quick Start

### Prerequisites
- **Java 21** or higher
- **Maven 3.6+**
- **(Optional) Node.js + npm** - For Newman API testing

### Run the Application

```bash
# Navigate to backend directory
cd backend

# Run with Maven wrapper
./mvnw spring-boot:run

# Or build and run JAR
./mvnw clean package
java -jar target/interview-1.0-SNAPSHOT.jar
```

The API will be available at **`http://localhost:8080`**

## 📖 API Documentation

### Swagger UI
**URL:** http://localhost:8080/swagger-ui.html

Interactive API documentation with:
- All endpoints and parameters
- Request/response examples
- Try-it-out functionality

### API Endpoints

**Base URL:** `http://localhost:8080/api/v1/athletes`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/` | Get paginated list of athletes with filtering and sorting |
| `GET` | `/{id}` | Get athlete by ID |
| `POST` | `/` | Create new athlete |
| `PUT` | `/{id}` | Update existing athlete |
| `DELETE` | `/{id}` | Delete athlete |

### Query Parameters

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `page` | int | Page number (0-based) | `0` |
| `size` | int | Page size (1-100) | `10` |
| `sortBy` | string | Sort field (id, firstName, lastName, nationality, discipline) | `lastName` |
| `direction` | string | Sort direction (ASC, DESC) | `ASC` |
| `nationality` | string | Filter by nationality (partial match) | `Jamaica` |
| `discipline` | string | Filter by discipline (partial match) | `100m` |
| `search` | string | Search by first or last name (partial match) | `Bolt` |

### Example Requests

```bash
# Pagination
GET /api/v1/athletes?page=0&size=5

# Filtering
GET /api/v1/athletes?nationality=Jamaica&discipline=100m

# Searching
GET /api/v1/athletes?search=Bolt

# Sorting
GET /api/v1/athletes?sortBy=lastName&direction=DESC

# Combined
GET /api/v1/athletes?nationality=USA&sortBy=lastName&size=20
```

### Response Format

**Successful Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "firstName": "Usain",
      "lastName": "Bolt",
      "birthDate": "1986-08-21",
      "nationality": "Jamaica",
      "discipline": "100m",
      "personalBest": "9.58s",
      "bio": "Olympic gold medalist..."
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 25,
  "totalPages": 3,
  "first": true,
  "last": false
}
```

**Error Response (400/404/409):**
```json
{
  "timestamp": "2025-10-16T12:00:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Athlete with ID 999 not found."
}
```

**Validation Error Response (400):**
```json
{
  "timestamp": "2025-10-16T12:00:00.000Z",
  "status": 400,
  "errors": {
    "firstName": "First name is required",
    "birthDate": "Birth date is required"
  }
}
```

## 🧪 Testing

### Run All Tests

```bash
./mvnw test
```

**Test Coverage:**
- ✅ 60 comprehensive tests
- ✅ Unit tests for services, repositories, DTOs, and configurations
- ✅ Integration tests for controller endpoints

### Test with Postman/Newman

```bash
# Install Newman (if not already installed)
npm install -g newman

# Run the Postman collection
newman run Athlete-API.postman_collection.json
```

See [API-DEMO-README.md](./API-DEMO-README.md) for detailed API demo instructions.

## 🗄️ Database

### H2 In-Memory Database

**Configuration:**
- **Console:** http://localhost:8080/h2-console
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **Username:** `sa`
- **Password:** `password`

**Sample Data:**
- 25 diverse athletes from various disciplines
- Includes sprinters, distance runners, and field event athletes
- Covers multiple nationalities and eras

**Database Indexes:**
- `idx_athlete_nationality` - Optimizes nationality filtering
- `idx_athlete_discipline` - Optimizes discipline filtering
- `idx_athlete_duplicate_check` - Composite index for duplicate detection

## 🏗️ Architecture

### Project Structure

```
src/main/java/com/interview/
├── AthleteApplication.java          # Spring Boot application entry point
├── config/                          # Configuration classes
│   ├── CorsConfig.java             # CORS configuration
│   ├── RateLimitConfig.java        # Rate limiting setup
│   ├── RateLimitFilter.java        # Rate limit filter
│   ├── RateLimitProperties.java    # Rate limit properties
│   └── RequestLoggingFilter.java   # Request logging
├── controller/                      # REST controllers
│   ├── AthleteController.java      # Main API endpoints
│   └── advice/
│       └── GlobalExceptionHandler.java  # Centralized exception handling
├── dto/                            # Data Transfer Objects
│   ├── AthleteMapper.java          # DTO ↔ Entity conversion
│   ├── AthleteRequest.java         # Create/Update request DTO
│   ├── AthleteResponse.java        # API response DTO
│   └── PagedResponse.java          # Pagination wrapper
├── exception/                       # Custom exceptions
│   ├── AthleteNotFoundException.java
│   └── DuplicateAthleteException.java
├── model/                          # Domain entities
│   └── Athlete.java                # Athlete entity
├── repository/                      # Data access layer
│   ├── AthleteRepository.java      # JPA repository
│   └── AthleteSpecification.java   # Dynamic query specifications
└── service/                        # Business logic layer
    ├── AthleteService.java         # Service interface
    └── AthleteServiceImpl.java     # Service implementation
```

### Clean Architecture Principles

1. **Separation of Concerns**
   - Controllers handle HTTP/validation
   - Services handle business logic
   - Repositories handle data access

2. **Single Responsibility**
   - Each class has one clear purpose
   - DTOs separate from entities
   - Validation at API boundary

3. **Dependency Inversion**
   - Service interface + implementation
   - Dependency injection throughout

## 🔧 Configuration

### Application Properties

```properties
# CORS - Configure allowed origin for frontend
cors.allowed-origin=http://localhost:3000

# Rate Limiting - Adjust capacity and refill rate
rate-limit.capacity=1000
rate-limit.refill-amount=1000
rate-limit.refill-duration=PT1M
```

### Monitoring Endpoints

- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information

## 🛠️ Development

### Code Formatting

```bash
# Check formatting
./mvnw spotless:check

# Apply formatting
./mvnw spotless:apply
```

### Build

```bash
# Clean build
./mvnw clean package

# Skip tests
./mvnw clean package -DskipTests
```

### Docker Support

```bash
# Build Docker image
docker build -t athlete-api .

# Run container
docker-compose up
```

## 📊 Technology Stack

- **Java 21** - Modern Java with virtual threads
- **Spring Boot 3.5.6** - Latest Spring Boot framework
- **Spring Data JPA** - Data access with Hibernate
- **H2 Database** - In-memory database as required
- **SpringDoc OpenAPI** - API documentation (Swagger)
- **Bucket4j** - Rate limiting
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework
- **AssertJ** - Fluent assertions
- **Spotless** - Code formatting

## 🚀 Future Enhancements & Production Considerations

Key improvements for production deployment:

### 🔐 Security
- Add Spring Security with JWT-based authentication
- Require authentication for CREATE/UPDATE/DELETE operations
- Implement per-user rate limiting

### 🗄️ Database
- Migrate from H2 to PostgreSQL or MySQL
- Add Flyway for database schema migrations

### 📊 Monitoring
- Add Prometheus metrics endpoint
- Set up Grafana dashboards for monitoring
- Implement structured logging for better observability

### 🐳 Deployment
- Create Helm charts for Kubernetes deployment
- Add health check endpoints for liveness/readiness probes
- Configure environment-specific properties

## 🔗 Additional Resources

- [Original Task Description](./ORIGINAL-TASK-DESCRIPTION.md) - Original coding exercise requirements
- [API Demo Guide](./API-DEMO-README.md) - Postman collection usage and API demonstration
- [Postman Collection](./Athlete-API.postman_collection.json) - Interactive API testing

---

**Built with ❤️ for the Tekmetric interview process**

