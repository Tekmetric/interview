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
- `curl -X GET   http://localhost:8080/api/users`

#### After finishing the goals listed below create a PR

### Goals
1. Design basic CRUD API with data store using Spring Boot and in memory H2 database (pre-configured, see below)
2. API should include one object with create, read, update, and delete operations. Read should include fetching a single item and list of items.
3. Provide SQL create scripts for your object(s) in resources/data.sql
4. Demo API functionality using API client tool

#### H2 Configuration
- Console: http://localhost:8080/h2-console 
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

### Submitting your coding exercise
Once you have finished the coding exercise please create a PR into Tekmetric/interview


# API Submission, by [Mihnea Lazar](https://github.com/lzrmihnea/) 

## API Endpoints: 
1. Retrieving all Users
- `curl -X GET   http://localhost:8080/api/users`
- `curl -X GET   http://localhost:8080/api/users?page=0&size=2&searchBy=Severus`

2. Retrieving single User
- `curl -X GET http://localhost:8080/api/users/3`

3. Create User
- `curl 'http://localhost:8080/api/users' \
-X 'PUT' \
--data-raw $' {\n    "lastname": "Potter",\n    "firstname": "Harry"\n  }' \
--compressed`

4. Update User with Document
- `curl 'http://localhost:8080/api/users' \
-X 'PUT' \
--data-raw $' {\n    "id": 3,\n    "lastname": "Potter",\n    "firstname": "Harry",\n    "documents": [\n      {\n        "name": "Avadakedavra spell"\n      }\n    ]\n  }' \
--compressed`

5. Delete User 
- `curl 'http://localhost:8080/api/users/3' \
-X 'DELETE' \
--compressed`

6. Monitoring 
- `curl -X GET   http://localhost:8080/actuator/health`
- `curl -X GET   http://localhost:8080/actuator/info`
- `curl -X GET   http://localhost:8080/actuator/prometheus`

7. Swagger
- `curl -X GET http://localhost:8080/swagger-ui`

