# Java Spring Boot API Coding Exercise

## Steps to get started:

#### Prerequisites
- Java 21 or higher
- Gradle (wrapper included)

#### Fork the repository and clone it locally
- https://github.com/Tekmetric/interview.git

#### Import project into IDE
- Project root is located in `backend` folder

#### Start the database
- Ensure Docker & Docker Compose are installed.
- From the project root, run:
    docker-compose up -d

#### Build and run your app
- `./gradlew build`
- `java -jar build/libs/interview-1.1-SNAPSHOT.jar`
- (or run `./gradlew bootRun`)

#### Test that your app is running
- `curl -X GET http://localhost:8080/api/shops?page=0&size=10`

#### After finishing the goals listed below create a PR

### Goals
1. Design a CRUD API with data store using Spring Boot and PostgreSQL (configured via Docker Compose, see below)
2. API should include one object with create, read, update, and delete operations. Read should include fetching a single item and list of items.
3. Provide SQL create scripts for your object(s) in resources/data.sql
4. Demo API functionality using API client tool

### Considerations
This is an open ended exercise for you to showcase what you know! We encourage you to think about best practices for structuring your code and handling different scenarios. Feel free to include additional improvements that you believe are important.

#### PostgreSQL Configuration
- JDBC URL: jdbc:postgresql://localhost:5432/interview
- Username: postgres
- Password: postgres

### Submitting your coding exercise
Once you have finished the coding exercise please create a PR into Tekmetric/interview

### Changes
1. Added a Shop entity, repository, service, and controller.
2. Added a ShopDto which interacts with the resource in order to mitigate any issues regarding managed entity
3. Added lombok, mapstruct and ShopMapper to convert between Shop and ShopDto
4. Added an ExceptionHandler to handle exceptions globally
5. Upgraded to Java 21 and latest library versions
6. Tried to do a Domain-Driven design, but not many features implemented that can use. We can discuss about it
7. Added OpenAPI
8. Authentication and Authorization are not implemented, but should be taken into account usually
9. Added a unit (Spock based) and an integration test
10. Replaced Maven with Gradle
11. Added flyway for migrations
12. Added logging mechanism
13. Observability, monitoring and load testing are not implemented, but to be taken into a production-level application 
