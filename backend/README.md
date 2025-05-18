# Java Spring Boot API Coding Exercise

## Steps to get started:

#### Prerequisites
- Maven
- Java 1.8 (or higher, update version in pom.xml if needed)

#### Fork the repository and clone it locally
- https://github.com/Tekmetric/interview.git

#### Import project into IDE
- Project root is located in `backend` folder

#### Build and run your app
- `mvn package && java -jar target/interview-1.0-SNAPSHOT.jar`

#### Test that your app is running
- `curl -X GET   http://localhost:8080/api/welcome`

#### After finishing the goals listed below create a PR

### Goals
1. Design a CRUD API with data store using Spring Boot and in memory H2 database (pre-configured, see below)
2. API should include one object with create, read, update, and delete operations. Read should include fetching a single item and list of items.
3. Provide SQL create scripts for your object(s) in resources/data.sql
4. Demo API functionality using API client tool

### Considerations
This is an open ended exercise for you to showcase what you know! We encourage you to think about best practices for structuring your code and handling different scenarios. Feel free to include additional improvements that you believe are important.

#### H2 Configuration
- Console: http://localhost:8080/h2-console 
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

### Submitting your coding exercise
Once you have finished the coding exercise please create a PR into Tekmetric/interview

### Description

The Vehicle REST API provides a standardized interface for performing CRUD (Create, Read, Update, Delete) operations on vehicle entities. This API enables client applications to manage vehicle data effectively in a RESTful manner, supporting JSON for data exchange.

### API Documentation
 - http://localhost:8080/swagger-ui/index.html

### Endpoints

### 1. Fetch multiple Vehicles

**GET** `/api/vehicles`

**Description:** List all vehicles (supports pagination and sorting).

**Response (JSON):**
```json
{
  "content": [
    {
      "id": 1,
      "type": "SEDAN",
      "fabricationYear": 2016,
      "make": "Toyota",
      "model": "Prius"
    },
    {
      "id": 2,
      "type": "SUV",
      "fabricationYear": 2022,
      "make": "Ford",
      "model": "Ranger"
    }
  ]
}
```

### 2. Fetch a Vehicle
**GET** `/api/vehicles/{vehicleId}`

**Description:** Get a specific vehicle by ID

**Response (JSON):**
```json
{
  "id": 1,
  "type": "SEDAN",
  "fabricationYear": 2016,
  "make": "Toyota",
  "model": "Prius"
}
```

### 3. Create a Vehicle
**POST** `/api/vehicles`

**Description:** Create a new vehicle

**Request Body (JSON):**
```json
{
  "type": "SEDAN",
  "fabricationYear": 2016,
  "make": "Toyota",
  "model": "Prius"
}
```

**Response (JSON):**
```json
{
  "id": 1,
  "type": "SEDAN",
  "fabricationYear": 2016,
  "make": "Toyota",
  "model": "Prius"
}
```

### 4. Update a Vehicle
**PUT** `/api/vehicles/{vehicleId}`

**Description:** Update an existing vehicle

**Request Body (JSON):**
```json
{
  "type": "SEDAN",
  "fabricationYear": 2016,
  "make": "Toyota",
  "model": "Prius"
}
```

**Response (JSON):**
```json
{
  "id": 1,
  "type": "SEDAN",
  "fabricationYear": 2016,
  "make": "Toyota",
  "model": "Prius"
}
```

### 4. Delete a Vehicle
**DELETE** `/api/vehicles/{vehicleId}`

**Description:** Delete a vehicle

## Project Structure
- `web` - web related packages
- `web.controller` - domain rest api controllers
- `web.errorhandling` - domain rest api error handler
- `web.model` - domain rest api models (requests/responses)
- `persistence` - persistence layer
- `persistence.repository` - data access repositories
- `persistence.entity` - JPA entities
- `model` - domain models
- `exception` - custom exceptions

## Tech Stack
- Java 21
- Spring Boot
- Spring Data
- SpringDoc OpenAPI
- H2 Database
- Lombok
- Spock with Groovy (for testing)




