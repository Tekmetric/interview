# Repair Shop Management API

## Overview
This Spring Boot application provides a RESTful API for managing repair services in an automotive repair shop. It includes comprehensive CRUD operations for repair services with proper validation, error handling, and API documentation.

## Features
- **Complete CRUD Operations**: Create, read, update, and delete repair services
- **Pagination & Sorting**: API supports pagination and sorting for listing repair services
- **Data Validation**: Input validation with descriptive error messages
- **OAuth2 Authentication**: Secured with Auth0 for proper authentication and authorization
- **Swagger/OpenAPI Documentation**: Interactive API documentation with OAuth2 support
- **Global Exception Handling**: Consistent error responses across the API

## Prerequisites
- Java 21 or higher
- Maven 3.6 or higher
- Auth0 account (for authentication)

## Getting Started

### Clone the Repository
```bash
git clone https://github.com/emregozen/interview.git
cd tekmetric-interview/backend
```

### Configure Auth0
1. Create an API in your Auth0 dashboard
2. Set the API identifier (audience) in `application.yaml`
3. Configure a Single Page Application in Auth0 for Swagger UI
4. Add `http://localhost:8080/swagger-ui/oauth2-redirect.html` as an allowed callback URL

### Build and Run

#### Option 1: Using Maven
```bash
mvn clean package
java -jar target/interview-1.0-SNAPSHOT.jar
```

#### Option 2: Using Docker Compose
The project includes Docker configuration for easy deployment:

```bash
docker-compose up --build
```

### Access the API
- API Base URL: `http://localhost:8080/api/repair-services`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: `password`

## API Documentation
The API is documented using OpenAPI 3.0 (Swagger). You can access the interactive documentation at `http://localhost:8080/swagger-ui.html`.

### Authentication
The API uses OAuth2 with Auth0 for authentication. To use the API:

1. Click the `Authorize` button in Swagger UI
2. Log in with your Auth0 credentials
3. Use the obtained token for subsequent API calls

### Endpoints
- `GET /api/repair-services` - List all repair services (supports pagination and sorting)
- `GET /api/repair-services/{id}` - Get a specific repair service by ID
- `POST /api/repair-services` - Create a new repair service
- `PUT /api/repair-services/{id}` - Update an existing repair service
- `DELETE /api/repair-services/{id}` - Delete a repair service

## Project Structure
- `controller` - REST controllers defining API endpoints
- `service` - Business logic layer
- `repository` - Data access layer
- `dto` - Data Transfer Objects for API requests/responses
- `entity` - JPA entities representing database tables
- `exception` - Custom exceptions and global exception handler
- `configuration` - Application configuration classes

## Future Enhancements
- **WireMock Integration**: For testing service interactions without external dependencies
- **Kustomize Files**: Kubernetes deployment configuration for different environments
- **Caching Layer**: Implement Redis or Caffeine cache for frequently accessed data
- **API Versioning**: Support multiple API versions
- **Multi-tenancy Support**: Isolate data for different repair shops
