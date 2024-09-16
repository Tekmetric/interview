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

# Vinyl App

This is an application that's supposed to be an inventory management app for my own vinyl collection.

### Prerequisites
- Maven
- Java 17
- Docker
- A working Internet connection

### Build & Run

- `mvn clean build`
- `docker build -t vinyl-app .`
- `docker-compose up -d`
- Test data could be found in the `data.sql` file, but it's automatically loaded into the H2 database on boot.

#### Once everything is up and running, the main links will be:
  - Swagger: http://localhost:8080/swagger-ui/index.html
  - H2 Console: http://localhost:8080/h2-console
  - Grafana: http://localhost:3000/
    - username: admin
    - pass: password

I've tried to provide documentation on API level in Swagger, but if any question arise, I'll be more than happy to clarify.


### Main flows

- Create a vinyl record to be added to the collection
- Update a vinyl record (these two go hand in hand - as I've thought of a FE app that would 
  first create the entity, then perform a getById to enter its "details" page, where the user can do all the changes required)
- Delete a vinyl record
- Get a list of records, a lightweight representation of them, perfectly suited for a list view with cards (paginated)
- Get a vinyl record's details

