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



## Implementation Details

### [design.md](design.md)
Low level design (LLD) is covered by design.md file. LLD design covers most of the architectural designs / decisions for the project. 

### Important URLS
**Swagger UI:** http://localhost:8080/swagger-ui.html

**Raw OpenAPI JSON:** http://localhost:8080/v3/api-docs

**Health check:** http://localhost:8080/actuator/health

**Metrics:** http://localhost:8080/actuator/metrics

**Info:** http://localhost:8080/actuator/info

**H2DB:** http://localhost:8080/h2-console 


### Environment Setup
- git, mvn, IDE of your choice ( Must Have)
    - brew install mvn
    - brew install git
    - Java 21 or Java 17 

- Install docker desktop (Optional)
    - brew install --cask docker

- Run Spring boot application
    - mvn clean test -U
    - mvn compile
    - mvn package && java -jar target/interview-1.0-SNAPSHOT.jar
    - mvn javadoc:javadoc (for java doc generation in /target/site/apidocs/index.html folder)
    - test open : http://localhost:8080/api/welcome
- Run docker container
    - mvn clean package -DskipTests
    - docker build -t interview-app .
    - docker run -p 8080:8080 interview-app
    - test open : http://localhost:8080/api/welcome
- Curl testing
``` bash
curl -X GET "http://localhost:8080/api/v1/users" \
  -H "Authorization: Basic dXNlcjpwYXNzd29yZA==" \
  -H "Content-Type: application/json"

curl -X POST "http://localhost:8080/api/v1/users" \
  -H "Authorization: Basic dXNlcjpwYXNzd29yZA==" \
  -H "Content-Type: application/json" \
  -d '{"name":"Meena Sriram","email":"test@example.com","age":35}'

curl -X GET "http://localhost:8080/api/v1/users/1" \
  -H "Authorization: Basic dXNlcjpwYXNzd29yZA==" \
  -H "Content-Type: application/json"

```
- Browser testing
    - http://localhost:8080/api/v1/users -> http://localhost:8080/login -> user:user , password:password

- Postman testing
    For Authorization select `Basic Auth` and use Bod with `raw` `JSON`




