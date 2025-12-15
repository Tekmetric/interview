# Blogger Service

A multi-tenant RESTful microservice for managing blog entries built with Spring Boot and H2 in-memory database. 
This service demonstrates some enterprise patterns including soft-delete functionality, tenant isolation, and API documentation.

## Screencast

[![Short screencast of Postman interaction](https://img.youtube.com/)](src/test/resources/screencast.mp4)

## Features

- **Multi-Tenant Architecture**: Isolated blog entries per tenant based on the authorization context
- **CRUD Operations**: Complete create, read (single and pagination), update, and delete functionality for blog entries
- **Soft Delete**: Blog entries are marked as deleted rather than physically removed, enabling audit trails, billing reconciliation, and data recovery workflows
- **Basic Authentication**: Secured API endpoints with Spring Security framework
- **Request Logging & Metrics**: Built-in request/response logging and basic performance metrics via actuators and filters
- **Swagger Documentation**: Interactive API documentation and testing interface
- **In-Memory Database**: H2 database for quick setup and testing
- **Data Validation**: Basic input validation and standardized error handling

## Setup

### Prerequisites
- Maven
- Java 1.8 (or higher, update version in pom.xml if needed)

### Fork the repository and clone it locally
- https://github.com/Tekmetric/interview.git

### Import project into IDE
- Project root is located in `backend` folder

### Build and run your app
- `mvn package && java -jar target/interview-1.0-SNAPSHOT.jar`

### Verify the Application
- The application will start on `http://localhost:8080`

## API Documentation

### Swagger UI

Interactive API documentation is when loading the service at http://localhost:8080. It includes:
- API endpoint documentation
- Request/response schemas
- Interactive testing capability
- Authentication configuration

### Authentication & Multi-Tenancy

All API endpoints require Basic Authentication. An actually deployed service can and should switch out the Auth 
mechanism with something more robust. Locally, two users are registered:
- **Username**: `Bob` | **Password**: `foo`
- **Username**: `Alice` | **Password**: `bar`

Each user has isolated access to their own blog entries. Blog entries created by `Bob` are not visible to `Alice`, and vice versa.

### Endpoints

#### List Blog Entries (Active Only)
```bash
GET /api/blog-entries
```
Returns only active (non-deleted) blog entries for the authenticated user in a standardized page 
(e.g. query parameters control the size and offset of data)

#### Get Single Blog Entry
```bash
GET /api/blog-entries/{id}
```
Retrieves a specific blog entry by ID (must belong to the authenticated user).

#### Create Blog Entry
```bash
POST /api/blog-entries
Content-Type: application/json

{
  "title": "Why Brâncuși created The Kiss",
  "content": "We will never truly know.",
  "categories": ["ART"]
}
```
Creates a new blog entry for the authenticated user.

#### Update Blog Entry
```bash
PATCH /api/blog-entries/{id}
Content-Type: application/json

{
  "content": "We will never truly know, but some experts say it was on a bet.",
}
```
Note: Soft-deleted entries cannot be updated.

#### Delete Blog Entry
```bash
DELETE /api/blog-entries/{id}
```
Marks the entry as deleted without physically removing it from the database.

### Example Requests with Authentication

```bash
# Create a blog entry as Alice
curl -u 'Alice:bar' -X POST http://localhost:8080/api/blog-entries \
  -H "Content-Type: application/json" \
  -d '{"title":"Why did the chicken cross the road?","content":"To get to the other side"}'

# Get all entries as Alice
curl -u 'Alice:bar' -X GET http://localhost:8080/api/blog-entries

# Delete an entry as Alice
curl -u 'Alice:bar' -X DELETE http://localhost:8080/api/blog-entries/{id}
```

## Database Configuration

### H2 Console Access
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

### Data Initialization

The database schema is automatically initialized from `resources/data.sql` on application startup with sample data based
on the @Entity defined schema.

## Soft Delete Architecture

### Why Soft Delete?

The soft-delete pattern separates the **business lifecycle** (when users consider data "deleted") from the 
**data lifecycle** (when data is physically removed).  A more robust implementation that should be used in a deployed
service such as Spring Data can be used along with improved authentication, but this sample shows a simplified version of
what is possible. All blog entries are managed via a `@SoftDelete` annotation that filters on fetching and is updated on 
deleting. A separate process can later actually do the cleanup.

## Request Logging & Metrics

The service includes basic logging and metrics:

- **Request Logging**: All HTTP requests are logged with method, path, status code, and duration
- **Tenant Activity**: Tenant-specific action logging occurs along with a standardized error format when the service 
is unable to complete the request.

View logs in the console output when running the application.

## Multi-Tenancy Architecture

### Tenant Isolation

- Each authenticated user represents a tenant (username = tenant ID)
- Database queries automatically filter by tenant context via the table ids.
- Cross-tenant access is prevented at the repository level
- Tenant context is extracted from authentication principal

### Tenant Context Flow

1. Request arrives with Basic Auth credentials
2. Spring Security authenticates and sets principal
3. Controller layer extracts tenant ID from principal
4. Repository queries include tenant ID filter
5. Response contains only tenant-specific data

## Testing

### Using Swagger UI
1. Navigate to http://localhost:8080
2. Click "Authorize" and enter credentials (e.g., Alice/bar)
3. Try out different endpoints interactively

### Using cURL
See example commands in the "Example Requests with Authentication" section above.

### Using Postman/Insomnia
1. Import the Swagger JSON spec from http://localhost:8080/v2/api-docs
2. Configure Basic Authentication with tenant credentials
3. Test endpoints with different tenants to verify isolation

## Notes / Future Enhancements

- This service uses an in-memory database; all data is lost on restart
- Basic Authentication is for demonstration only and should not be used in production
- In production, consider using JWT tokens, OAuth2, or similar authentication mechanisms
- The soft-delete pattern shown here is simplified; production systems may need additional features like scheduled cleanup jobs, configurable retention periods, and admin override capabilities
- Multi-tenancy implementation is basic; enterprise solutions may require tenant-specific database schemas or separate database instances
- Request logging is basic; production systems should use structured logging and centralized log aggregation (e.g., ELK stack)
- Rate limiting can also be impleted with the auth layer to make the service more robust.