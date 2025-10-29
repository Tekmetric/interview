# Kevin Hall Tekmetric Interview Backend

This project demonstrates API CRUD operations on an H2 database for League and Team tables.

## Technologies Used

*   **Java 21**
*   **Spring Boot 3.x**
*   **Maven**
*   **H2 Database**
*   **Open API**

## Getting Started

### Prerequisites

- Java21
- Maven

### Project Structure
```
project-root/
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── interview
│   │   │           ├── controller
│   │   │           │   ├── LeagueController.java           # REST controller for handling HTTP requests.
│   │   │           │   └── TeamController.java             # REST controller for handling HTTP requests.
│   │   │           ├── exception
│   │   │           │   ├── ConflictException.java          # Exception class for Conflict.
│   │   │           │   ├── GlobalExceptionHandler.java     # Global Exception Handler class.
│   │   │           │   ├── MissingRequiredException.java   # Exception class for Missing Requirements.
│   │   │           │   └── RowNotFoundException.java       # Exception class for Row Not Found.
│   │   │           ├── model
│   │   │           │   ├── dto    
│   │   │           │   │   └── TeamDTO.java                # Data Transfer Object for Team          
│   │   │           │   ├── League.java                     # JPA entity for the League table.
│   │   │           │   └── Team.java                       # JPA entity for the Team table.
│   │   │           ├── repository
│   │   │           │   ├── LeagueRepository.java           # Spring Data JPA repository for data access.
│   │   │           │   └── TeamRepository.java             # Spring Data JPA repository for data access.
│   │   │           ├── security
│   │   │           │   ├── SecurityConfig.java             # Configuration for authentication/security.
│   │   │           ├── service
│   │   │           │   ├── LeagueService.java              # Business logic for League management.
│   │   │           │   └── TeamService.java                # Business logic for Team management.
│   │   │           └── DemoApplication.java                # Main Spring Boot application class.
│   │   │           
│   │   └── resources
│   │       ├── application.properties                      # Configuration for H2 database and other settings.
│   │       ├── database                             
│   │       │   └── database.sql                            # Script to pre-populate data on startup.
│   │       ├── interview.postman_collection.json           # Json postman collection.
│   │       └── apiCall.sh                                  # Script for example curl commands.
│   └── test
│       └── java
│           └── com
│               └── interview
│                   └── ControllerTest.java                 # Main test class.
├── pom.xml                                                 # Maven project file with dependencies.
└── README.md
```

### Building the Project

Navigate to the project's root directory in your terminal and execute the following command:

```
  mvn clean install
```

### Running the Project

Navigate to the project's root directory in your terminal and execute the following command:

```
  mvn clean package && java -jar target/interview-1.0-SNAPSHOT.jar
```

#### Calling the API

The application will start running on http://localhost:8080. The endpoint base urls are http://localhost:8080/api/leagues and 
http://localhost:8080/api/teams.
Provided scripts/documents for calling the API are in src/main/resources/. The apiCall.sh is a script for calling the 
endpoints by CLI. The interview.postman_collection.json is a postman collection that can be imported as a base for 
these endpoints.

### Run Tests

Navigate to the project's root directory in your terminal and execute the following command:

```
  mvn clean test
```
or to verify tests are successful
```
  mvn clean verify
```

#### H2 Configuration
- Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password


#### Open API
- Swagger UI Doc: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

#### Actuator Endpoints
- [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) - GET - Shows application health information.
- [http://localhost:8080/actuator/info](http://localhost:8080/actuator/info) - GET - Displays arbitrary application information.
- [http://localhost:8080/actuator/beans](http://localhost:8080/actuator/beans) - GET - Displays a list of all Spring beans.
- [http://localhost:8080/actuator/conditions](http://localhost:8080/actuator/conditions) - GET - Shows auto-configuration conditions.
- [http://localhost:8080/actuator/configprops](http://localhost:8080/actuator/configprops) - GET - Displays @ConfigurationProperties.
- [http://localhost:8080/actuator/env](http://localhost:8080/actuator/env) - GET - Exposes properties from Spring's ConfigurableEnvironment.
- [http://localhost:8080/actuator/loggers](http://localhost:8080/actuator/loggers) - GET/POST - Shows and modifies logger configuration.
- [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics) - GET - Shows metrics information.
- [http://localhost:8080/actuator/mappings](http://localhost:8080/actuator/mappings) - GET - Displays @RequestMapping paths.
- [http://localhost:8080/actuator/threaddump](http://localhost:8080/actuator/threaddump) - GET - Performs a thread dump.
- [http://localhost:8080/actuator/shutdown](http://localhost:8080/actuator/shutdown) - POST - Allows graceful application shutdown (disabled by default).
