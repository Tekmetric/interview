# Java Spring Boot API Coding Exercise

## Solution

This is a simple CRUD API for the management of cars.

## API Documentation

The API is documented via Swagger docs. Once the application is running, visit the following URL for the API contract.

```
http://localhost:8080/swagger-ui/index.html
```

## Steps to get started:

#### Prerequisites
- Maven
- Java 21

### Goals
1. Design a CRUD API with data store using Spring Boot and in memory H2 database (pre-configured, see below)
2. API should include one object with create, read, update, and delete operations. Read should include fetching a single item and list of items.
3. Provide SQL create scripts for your object(s) in resources/data.sql
4. Demo API functionality using API client tool

### Considerations
This is an open-ended exercise for you to showcase what you know! We encourage you to think about best practices for structuring your code and handling different scenarios. Feel free to include additional improvements that you believe are important.

#### H2 Configuration
- Console: http://localhost:8080/h2-console 
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

### Submitting your coding exercise
Once you have finished the coding exercise please create a PR into Tekmetric/interview
