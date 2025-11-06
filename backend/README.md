# This branch is running Java 11. It and Maven are prerequisites.

### Build and run this app with the following, or from your IDE

- `mvn package && java -jar target/interview-1.0-SNAPSHOT.jar`

### To run tests - both unit and component

- `mvn test`

### To see and use all endpoints, view the swagger page at:

- Swagger: http://localhost:8080/swagger-ui/index.html
- Note: a header valjue of 'X-AUTH-KEY' is required for all api/v0/** endpoints.
- The correct value is "super-secret-demo-key" - enter this in the 'Authorize' button on the swagger page. or
- There is a postman collection: "tekmetric-BE-interview-project.postman_collection.json" that can be imported

### This app has two main DB tables - repair_orders and repair_order_lines, linked by repair order id.There are also audit tables for both.

### The main controller allows CRUD operations for repair_orders.

### A simple authorization Header for the api/v0/** endpoints was implemented. A JWT token would be more realistic for 'real' users.


# ORIGINAL INSTRUCTIONS FOR THIS EXERCISE ARE BELOW

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
2. API should include one object with create, read, update, and delete operations. Read should include fetching a single
   item and list of items.
3. Provide SQL create scripts for your object(s) in resources/data.sql
4. Demo API functionality using API client tool

### Considerations

This is an open ended exercise for you to showcase what you know! We encourage you to think about best practices for
structuring your code and handling different scenarios. Feel free to include additional improvements that you believe
are important.

#### H2 Configuration

- Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

### Submitting your coding exercise

Once you have finished the coding exercise please create a PR into Tekmetric/interview
