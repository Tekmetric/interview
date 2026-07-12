# Event Management System API

## Overview

A comprehensive event management REST API built with Spring Boot 3.2.0 and Java 21. This application provides a robust platform for managing events (concerts, sports games, tech conferences), venues, performers, and user accounts with advanced features including JWT authentication, Redis caching, and OpenSearch-powered full-text search.

## Key Features

### Event Management
- **CRUD Operations**: Create, read, update, and delete events with full validation
- **Event Search**: OpenSearch integration for fast, full-text search across event names, descriptions, and locations
- **Cursor-Based Pagination**: Efficient pagination for large datasets
- **Event Filtering**: Filter events by location, date ranges, and upcoming events
- **Venue & Performer Association**: Link events with venues and multiple performers

### Authentication & Authorization
- **JWT-based Authentication**: Secure token-based authentication system
- **Role-Based Access Control (RBAC)**: Admin and user roles with different permissions
- **Password Encryption**: BCrypt password hashing for secure credential storage
- **Protected Endpoints**: Admin-only operations for creating, updating, and deleting events

### Performance Optimization
- **Redis Caching**: Distributed caching layer for frequently accessed data (10-minute TTL)
- **OpenSearch Integration**: Lightning-fast search capabilities with pagination
- **Optimized Queries**: JPA-based data access with efficient query design
- **Connection Pooling**: Optimized database connection management

### API Documentation
- **Swagger/OpenAPI 3.0**: Interactive API documentation and testing interface
- **Detailed Request/Response Examples**: Complete API specifications
- **API Versioning**: RESTful API design with proper versioning

### Data Persistence
- **H2 In-Memory Database**: Fast development and testing environment
- **JPA/Hibernate**: Object-relational mapping with automatic schema generation
- **UUID Primary Keys**: Distributed-system-ready unique identifiers
- **Automatic Timestamps**: Created and updated timestamps on all entities

### Testing
- **Comprehensive Unit Tests**: 29 passing unit tests covering service layer
- **Mockito Integration**: Mock-based testing for isolated component testing
- **Test Coverage**: EventService (12 tests), AccountService (17 tests)

## Data Schema

The application uses a relational database schema with the following entities:

### Event
The core entity representing events (concerts, sports, tech events).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | Primary Key, NOT NULL | Unique event identifier |
| name | VARCHAR(255) | NOT NULL | Event name |
| description | VARCHAR(1000) | NULL | Detailed event description |
| event_date | TIMESTAMP | NOT NULL | Date and time of the event |
| location | VARCHAR(255) | NOT NULL | Event location/address |
| venue_id | UUID | Foreign Key | Reference to Venue table |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NULL | Last update timestamp |

**Relationships:**
- Many-to-One with Venue
- Many-to-Many with Performer (via event_performer join table)

### Account
User accounts with authentication credentials and role-based permissions.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | Primary Key, NOT NULL | Unique account identifier |
| first_name | VARCHAR(255) | NOT NULL | User's first name |
| last_name | VARCHAR(255) | NOT NULL | User's last name |
| email | VARCHAR(255) | UNIQUE, NOT NULL | User's email (login) |
| password | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| phone | VARCHAR(255) | NULL | Contact phone number |
| created_at | TIMESTAMP | NOT NULL | Account creation timestamp |
| updated_at | TIMESTAMP | NULL | Last update timestamp |

**Relationships:**
- Many-to-Many with Role (via account_role join table)

### Venue
Physical locations where events take place.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | Primary Key, NOT NULL | Unique venue identifier |
| name | VARCHAR(255) | NOT NULL | Venue name |
| address | VARCHAR(255) | NOT NULL | Street address |
| city | VARCHAR(255) | NOT NULL | City |
| state | VARCHAR(255) | NOT NULL | State/Province |
| zip_code | VARCHAR(255) | NULL | Postal code |
| capacity | INTEGER | NULL | Maximum capacity |
| created_at | TIMESTAMP | NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NULL | Last update timestamp |

**Relationships:**
- One-to-Many with Event

### Performer
Artists, bands, speakers, or teams performing at events.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | Primary Key, NOT NULL | Unique performer identifier |
| name | VARCHAR(255) | NOT NULL | Performer name |
| genre | VARCHAR(255) | NULL | Genre/Category |
| bio | VARCHAR(2000) | NULL | Biography/Description |
| created_at | TIMESTAMP | NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NULL | Last update timestamp |

**Relationships:**
- Many-to-Many with Event (via event_performer join table)

### Role
User roles for access control (e.g., ADMIN, USER).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | Primary Key, NOT NULL | Unique role identifier |
| name | VARCHAR(255) | UNIQUE, NOT NULL | Role name (e.g., ROLE_ADMIN) |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |

**Relationships:**
- Many-to-Many with Account (via account_role join table)

### Join Tables

**event_performer** (Many-to-Many)
- event_id (UUID, Foreign Key → Event)
- performer_id (UUID, Foreign Key → Performer)

**account_role** (Many-to-Many)
- account_id (UUID, Foreign Key → Account)
- role_id (UUID, Foreign Key → Role)

### Entity Relationship Diagram (ERD)

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│    Account   │───M:M─┤account_role │─M:M───│     Role     │
└──────────────┘       └──────────────┘       └──────────────┘

┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│    Event     │───M:M─┤event_performer│─M:M──│  Performer   │
└──────┬───────┘       └──────────────┘       └──────────────┘
       │
       │ M:1
       │
       ▼
┌──────────────┐
│    Venue     │
└──────────────┘
```

## Steps to get started:

#### Prerequisites
- Maven
- Java 21 (updated from Java 1.8)
- Docker and Docker Compose (for containerized deployment)

#### Fork the repository and clone it locally
- https://github.com/Tekmetric/interview.git

#### Import project into IDE
- Project root is located in `backend` folder

## Running the Application

### Option 1: Using Docker (Recommended)

This option runs the application with all dependencies (Redis, OpenSearch) in containers.

#### Build and start all services:
```bash
docker-compose up --build -d
```

This command will:
- Build the Spring Boot application with Java 21
- Start Redis (cache)
- Start OpenSearch (search engine)
- Start the Spring Boot application

#### Check service status:
```bash
docker-compose ps
```

#### View logs:
```bash
docker-compose logs -f          # All services
docker-compose logs -f app      # Just the Spring Boot app
```

#### Stop all services:
```bash
docker-compose down
```

#### Access the application:
- API: http://localhost:8080
- OpenSearch: http://localhost:9200
- Redis: localhost:6379

### Option 2: Running Locally with Maven

**Note:** This requires Redis and OpenSearch to be running locally or accessible.

#### Build and run your app:
```bash
mvn clean package -DskipTests
java -jar target/interview-1.0-SNAPSHOT.jar
```

Or simply:
```bash
mvn package && java -jar target/interview-1.0-SNAPSHOT.jar
```

## Testing the Application

#### Test that your app is running:
```bash
curl -X GET http://localhost:8080/api/welcome
```

#### Access Swagger UI:
- http://localhost:8080/swagger-ui.html

#### Access H2 Console:
- Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

## Running Unit Tests

The application includes comprehensive unit tests for the service layer.

#### Run all tests:
```bash
mvn test
```

#### Run tests with coverage report:
```bash
mvn clean test
```

#### Test Summary:
- **Total Tests**: 29
- **EventServiceTest**: 12 tests covering CRUD operations, search, and date filtering
- **AccountServiceTest**: 17 tests covering account management, authentication, and validation

All tests use Mockito for mocking dependencies and JUnit 5 for test execution.

## Technology Stack

- **Java 21** with Spring Boot 3.2.0
- **Spring Security** with JWT authentication
- **H2 Database** (in-memory)
- **Redis** for caching
- **OpenSearch** for search functionality
- **Swagger/OpenAPI** for API documentation
- **Structurizr** for architecture documentation (C4 model)

## Architecture Documentation

This project uses **Structurizr** to document the system architecture using the C4 model (Context, Containers, Components, Code). Architecture diagrams are available in two formats:

1. **DSL Format** - Text-based architecture definition in `workspace.dsl`
2. **Java API** - Programmatic architecture definition in `src/main/java/com/interview/config/ArchitectureDiagram.java`

### Architecture Source Files

- **`workspace.dsl`** - Human-readable text file defining the architecture using Structurizr DSL
  - Easy to edit and maintain
  - Can be used with Structurizr Lite or Structurizr CLI
  - Version control friendly

- **`ArchitectureDiagram.java`** - Java class that programmatically generates diagrams
  - Useful for CI/CD integration
  - Exports to PlantUML, Mermaid, and JSON formats

### Viewing Architecture Diagrams

The architecture diagrams are located in the `diagrams/` directory:

- **PlantUML format** (`.puml` files) - Can be viewed in:
  - PlantUML online editor: https://www.plantuml.com/plantuml/uml/
  - VS Code with PlantUML extension
  - IntelliJ IDEA with PlantUML integration

- **Mermaid format** (`.mmd` files) - Can be viewed in:
  - GitHub (renders automatically in markdown)
  - Mermaid Live Editor: https://mermaid.live/
  - VS Code with Mermaid extension

- **JSON format** (`workspace.json`) - Can be viewed in:
  - Structurizr Lite: https://structurizr.com/help/lite
  - Structurizr online workspace

### Architecture Diagram Types

1. **System Context Diagram** - Shows the big picture: the system and its users
   - Files: `diagrams/system-context.puml`, `diagrams/system-context.mmd`

2. **Container Diagram** - Shows high-level technology choices and how they communicate
   - Files: `diagrams/containers.puml`, `diagrams/containers.mmd`
   - Includes: Spring Boot App, H2 Database, Redis Cache, OpenSearch, Swagger UI

3. **Component Diagram** - Shows internal structure of the Spring Boot application
   - Files: `diagrams/components.puml`, `diagrams/components.mmd`
   - Includes: Controllers, Services, Repositories, Security Filters

### Working with the DSL File

The `workspace.dsl` file is the easiest way to view and edit the architecture. You can use it with:

#### Option 1: Structurizr Lite (Recommended)

Structurizr Lite is a Docker-based viewer that runs locally:

```bash
# Run Structurizr Lite using Docker
docker run -it --rm -p 8080:8080 -v $(pwd):/usr/local/structurizr structurizr/lite

# Then open in your browser
open http://localhost:8080
```

The tool will automatically load `workspace.dsl` and render interactive diagrams.

#### Option 2: Structurizr CLI

Install the [Structurizr CLI](https://github.com/structurizr/cli) and export diagrams:

```bash
# Install Structurizr CLI (requires Docker)
docker pull structurizr/cli

# Export to PlantUML
docker run -v $(pwd):/usr/local/structurizr structurizr/cli export -workspace workspace.dsl -format plantuml

# Export to Mermaid
docker run -v $(pwd):/usr/local/structurizr structurizr/cli export -workspace workspace.dsl -format mermaid
```

### Regenerating Architecture Diagrams (Java API Method)

To regenerate the architecture diagrams using the Java API after making changes:

```bash
# Compile the project
mvn clean compile -DskipTests

# Run the ArchitectureDiagram class
mvn exec:java -Dexec.mainClass="com.interview.config.ArchitectureDiagram" -Dexec.classpathScope=compile
```

The diagrams will be regenerated in the `diagrams/` directory.

### Which Approach to Use?

- **Use DSL (`workspace.dsl`)** for:
  - Quick architecture updates
  - Team collaboration (easier to review in pull requests)
  - Visual exploration with Structurizr Lite

- **Use Java API (`ArchitectureDiagram.java`)** for:
  - CI/CD pipeline integration
  - Programmatic diagram generation
  - When you need PlantUML/Mermaid exports

### Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    Event Management System                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌───────────────┐      ┌────────────────┐     ┌─────────────┐ │
│  │   Swagger UI  │────▶ │ Spring Boot    │────▶│   Redis     │ │
│  │  (OpenAPI)    │      │   Application  │     │   Cache     │ │
│  └───────────────┘      └────────┬───────┘     └─────────────┘ │
│                                  │                               │
│                         ┌────────┼────────┐                     │
│                         ▼        ▼        ▼                     │
│                    ┌────────┐┌────────┐┌──────────┐            │
│                    │   H2   ││OpenSearch││Security│            │
│                    │Database││  Engine  ││ (JWT)  │            │
│                    └────────┘└────────┘└──────────┘            │
└─────────────────────────────────────────────────────────────────┘
```

## Project Structure

- Event Management API with CRUD operations
- JWT-based authentication
- Redis caching layer
- OpenSearch integration
- Docker containerization
