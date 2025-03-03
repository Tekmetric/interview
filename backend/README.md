# Animal Shelter Management System

This is a Spring Boot application that provides a REST API for managing an animal shelter. It includes functionality for managing animals, employees, and veterinarians.

## Business Requirements and Domain Model

### Core Entities and Relationships

1. **Animals**
   - Each animal has basic information (name, species, breed, date of birth)
   - Can be assigned to one responsible employee
   - Can be associated with multiple veterinarians

2. **Employees**
   - Handle day-to-day care of animals
   - Can be responsible for multiple animals
   - Have contact information and job titles

3. **Veterinarians**
   - Provide medical care to animals
   - Can be associated with multiple animals
   - Have specializations and contact information

### Business Rules and Assumptions

1. **Animal Management**
   - Animals can exist in the system without an assigned employee or veterinarian
   - When an animal is deleted, its relationships with employees and vets are automatically removed
   - Animals can be filtered by name, date of birth range, and responsible employee

2. **Employee Management**
   - Currently no limit on the number of animals an employee can be responsible for
   - Employees can be reassigned or removed without affecting animal records

3. **Veterinarian Management**
   - Vets can be associated with any number of animals
   - Vet specializations are free-text fields for flexibility

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Development Tools

### IDE Setup
- The project is optimized for IntelliJ IDEA
- Recommended plugins:
  - Lombok plugin for annotation processing
  - Spring Boot plugin for enhanced Spring support

## Getting Started

1. Clone the repository
2. Navigate to the backend directory:
   ```bash
   cd backend
   ```
3. Build and run the project (Option 1 - using Maven):
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   
   Or build and run with Java (Option 2):
   ```bash
   mvn package && java -jar target/interview-1.0-SNAPSHOT.jar
   ```

4. Verify the application is running:
   ```bash
   curl -X GET http://localhost:8080/api/animals
   ```

The application will start on `http://localhost:8080`

## Database

The application uses an H2 in-memory database for development and testing purposes. You can access the H2 console at:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

Note: For production deployment, this should be migrated to a proper database system like PostgreSQL to support scalability, concurrent access, and data persistence.

### Database Optimizations
- Indexes have been added for common query patterns (e.g., animal name searches)
   - Note that, depending on our use cases, we might want to change the "full" match query on animal name for a "prefix" match, as that'd make the index way more effective
- Note: Some N+1 query issues exist in "find all" methods due to pagination requirements
  - Future optimization could implement batch fetching strategies to avoid this, but that's outside of the scope of the current project

## API Documentation

Swagger UI is available at: `http://localhost:8080/swagger-ui.html`
OpenAPI documentation: `http://localhost:8080/api-docs`

### Pagination and Sorting
All list endpoints support the following parameters:
- `page` - Page number (0-based, defaults to 0)
- `size` - Number of items per page (defaults to 10)
- `sort` - Sort field and direction (format: `field,direction`, e.g., `name,asc`)
  - Multiple sort criteria can be specified (e.g., `name,asc&sort=dateOfBirth,desc`)
  - Direction can be `asc` or `desc`

## Available Endpoints

### Animals
- GET `/api/animals` - List all animals. Supports:
  - Pagination parameters (`page`, `size`, `sort`)
  - Filtering:
    - `name` - Filter by animal name
    - `startDate` - Filter by date of birth range start (format: YYYY-MM-DD)
    - `endDate` - Filter by date of birth range end (format: YYYY-MM-DD)
    - `employeeId` - Filter by responsible employee
  - Example: `/api/animals?name=Max&page=0&size=10&sort=name,asc`
- GET `/api/animals/{id}` - Get animal by ID
- POST `/api/animals` - Create new animal
- PUT `/api/animals/{id}` - Update animal (full update)
- DELETE `/api/animals/{id}` - Delete animal
- GET `/api/animals/{id}/vets` - List veterinarians for an animal (paginated)

### Employees
- GET `/api/employees` - List all employees. Supports:
  - Pagination parameters (`page`, `size`, `sort`)
  - Example: `/api/employees?page=0&size=20&sort=name,asc`
- GET `/api/employees/{id}` - Get employee by ID
- POST `/api/employees` - Create new employee
- PUT `/api/employees/{id}` - Update employee
- DELETE `/api/employees/{id}` - Delete employee

### Veterinarians
- GET `/api/vets` - List all veterinarians. Supports:
  - Pagination parameters (`page`, `size`, `sort`)
  - Example: `/api/vets?page=0&size=15&sort=specialization,desc`
- GET `/api/vets/{id}` - Get veterinarian by ID
- POST `/api/vets` - Create new veterinarian
- PUT `/api/vets/{id}` - Update veterinarian
- DELETE `/api/vets/{id}` - Delete veterinarian

## Caching Strategy

The application implements caching for frequently accessed data:
- Individual entities cached by ID
- List/search results cached with composite keys
- Cache eviction on entity modifications

## Error Handling

The application includes global exception handling for:
- Resource not found
- Validation errors
- Database constraints
- General server errors

## Testing

To run the tests:
```bash
mvn test
```

To run tests with coverage report:
```bash
mvn clean test jacoco:report
```

The test suite includes:
- Unit tests for services
- Integration tests for controllers
- Database persistence tests
- Caching behavior tests

### Test Coverage
JaCoCo is configured to enforce minimum coverage thresholds:
- Line coverage: 80%
- Branch coverage: 80%

Coverage reports can be found at:
- HTML: `target/site/jacoco/index.html`
- XML: `target/site/jacoco/jacoco.xml`
- CSV: `target/site/jacoco/jacoco.csv`

## Code Style & Conventions

### Repository Pattern
- All data access classes are wrapped in interfaces and suffixed with "Repository"
- Repositories handle all database operations and data persistence logic
- Custom specifications are used for complex queries

### Unit Testing
Tests follow a clear structure with three main sections:
1. **Given** - Test setup and preconditions
2. **When** - Action or method being tested
3. **Then** - Assertions and verifications

Service tests follow these principles:
- Only the service under test is instantiated as a real object
- All dependencies (other services, repositories) are mocked
- Service instantiation is done at variable declaration level
- Commonly used variables are extracted as constants

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/interview/
│   │   │   ├── config/      # Configuration classes
│   │   │   ├── controller/  # REST controllers
│   │   │   ├── dto/         # Data Transfer Objects
│   │   │   ├── exception/   # Custom exceptions and handlers
│   │   │   ├── mapper/      # Object mappers
│   │   │   ├── model/       # Domain entities
│   │   │   ├── repository/  # Data access interfaces
│   │   │   └── service/     # Business logic interfaces and implementations
│   │   └── resources/       # Application properties and static resources
│   └── test/
│       └── java/com/interview/
│           ├── controller/  # Controller tests
│           ├── repository/  # Repository tests
│           └── service/     # Service tests
└── pom.xml                  # Project dependencies and build configuration
```

## Potential Improvements and Future Work

### Business Rules and Validation
1. **Employee Workload Management**
   - Add limits on animals per employee
   - Implement workload balancing logic
   - Add validation for employee qualifications vs animal needs

2. **Data Validation Enhancements**
   - Add more specific validation rules for each entity
   - Implement cross-entity validation rules
   - Add business rule validators

### Security Enhancements

1. **Input Validation**
   - Add input sanitization for free-text fields
   - Limits to number of items requested in pagination
   - Implement rate limiting for API endpoints
   - Add request size limits

2. **Authentication/Authorization**
   - Implement Spring Security
   - Add role-based access control
   - Implement JWT authentication
   - Add audit logging

### Technical Improvements

1. **API Enhancements**
   - Add API versioning
   - Implement PATCH endpoints for partial updates
   - Add bulk operation endpoints
   - Enhance error responses with more detail

2. **Database Infrastructure**
   - Migrate from H2 to PostgreSQL or similar production-grade database
   - Set up proper connection pooling
   - Implement database replication strategy
   - Configure automated backups

3. **Caching Optimizations**
   - Evaluate alternative caching frameworks for distributed scenarios
   - Fine-tune the cache (e.g. cache size limits and TTL)
   - Implement cache warming strategies
   - Add cache monitoring

4. **Performance Optimizations**
   - Address N+1 query issues in list endpoints
   - Optimize database indexes based on usage patterns
   - Add database query monitoring
   - Implement connection pooling tuning

5. **Infrastructure**
   - Add Dockerfile and docker-compose
   - Create Infrastructure as Code (IaC)
   - Add CI/CD pipeline configuration
   - Implement monitoring and alerting

6. **Observability**
   - Add health check endpoints
   - Implement metrics collection
   - Enhance logging

7. **Documentation**
   - Add more API examples
   - Include sequence diagrams
   - Add load testing results
   - Document deployment procedures

8. **Testing**
   - Add more edge case tests
   - Implement performance tests
   - Add security tests
   - Enhance integration test coverage

## System-Wide Considerations

While completely outside the scope of this exercise, here are key considerations that a production version of this system would need to address:

### Domain Evolution
- **Multi-Location Support**
  - Geographic data partitioning
  - Location-specific configurations
  - Cross-location animal transfer tracking

- **Integration Ecosystem**
  - Veterinary practice management systems
  - Pet adoption platforms
  - Microchip registries
  - Emergency animal services

### System Architecture
- **Event-Driven Patterns**
  - Status change notifications
  - Audit trail implementation
  - Event schema versioning

- **Data Management**
  - Separate databases for read-heavy operations (e.g., adoption listings) and write-heavy operations (e.g., medical records)
  - Historical data archival strategy
  - Cache distribution for multiple instances
  - Data retention policies

### Production Readiness
- **Observability**
  - Business-level SLAs (e.g., animal care metrics)
  - Technical SLOs (e.g., API response times)
  - Capacity planning and alerting
  - Audit logging for sensitive operations

## Design Decisions and Trade-offs

1. **REST API Design**
   - Used PUT for full updates instead of PATCH for simplicity
   - Since the PUT endpoints use the full DTO, they show as if they could update the entities' IDs, but they can't (and in most cases shouldn't). We could improve documentation and DTO usage around this
     - This also happens for the Vet entity and the DTO showing the animals relationship
   - Implemented pagination for all list endpoints
   - Used query parameters for filtering

2. **Caching Strategy**
   - Implemented simple in-memory caching
   - Cache invalidation on any related entity changes
   - No TTL implemented initially

3. **Database Design**
   - Used simple relationships for maintainability
   - Accepted some N+1 query issues for pagination support
   - Indexes added based on common query patterns
   - ManyToMany relationships use PERSIST and MERGE cascading to maintain referential integrity while preventing unintended deletions
