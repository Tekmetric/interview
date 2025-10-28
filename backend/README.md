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
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── interview
│   │   │           ├── controller
│   │   │           │   ├── LeagueController.java    # REST controller for handling HTTP requests.
│   │   │           │   └── TeamController.java      # REST controller for handling HTTP requests.
│   │   │           ├── model
│   │   │           │   ├── League.java              # JPA entity for the League table.
│   │   │           │   ├── NotFoundResponse.java
│   │   │           │   └── Team.java                # JPA entity for the Team table.
│   │   │           ├── repository
│   │   │           │   ├── LeagueRepository.java    # Spring Data JPA repository for data access.
│   │   │           │   └── TeamRepository.java      # Spring Data JPA repository for data access.
│   │   │           ├── service
│   │   │           │   ├── LeagueService.java       # Business logic for League management.
│   │   │           │   └── TeamService.java         # Business logic for Team management.
│   │   │           └── DemoApplication.java         # Main Spring Boot application class.
│   │   │           
│   │   └── resources
│   │       ├── application.properties               # Configuration for H2 database and other settings.
│   │       ├── database                             
│   │       │   └── database.sql                     # Script to pre-populate data on startup.
│   │       ├── interview.postman_collection.json    # Json postman collection.
│   │       └── apiCall.sh                           # Script for example curl commands.
│   └── test
│       └── java
│           └── com
│               └── interview
│                   └── controller
│                       └── ControllerTest.java    # Main test class.
├── pom.xml                                        # Maven project file with dependencies.
└── README.md

### Building the Project

Navigate to the project's root directory in your terminal and execute the following command:

```shell
  mvn clean install
```

### Running the Project

Navigate to the project's root directory in your terminal and execute the following command:

```shell
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

```shell
  mvn clean test
```
or to verify tests are successful
```shell
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
