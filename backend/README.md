# Java Spring Boot API Coding Exercise

## Steps to get started:

#### Prerequisites
- Maven
- Java 1.8 (or higher, update version in pom.xml if needed)

#### Fork the repository and clone it locally
- https://github.com/Tekmetric/interview.git

#### Import project into IDE
- Project root is located in `backend` folder

#### Testing

The service can be started using `mvn spring-boot:run`. 
Add `-Dspring-boot.run.profiles=local` to make the database persistent.
To delete the database create by the `local` profile, use `rm -rf ./target/db`.

Once started, the API documentation can be accessed at http://localhost:8080/swagger-ui/index.html.
Example requests are also available [here](examples.rest) using IntelliJ's HTTP client. 

A [data.sql](./src/main/resources/data.sql) is provided with some example records. 
Note that the H2 database only supports a single connection when used in memory/file mode. 
The example script can be applied by using H2 file before running service, or by using the [H2 console](http://localhost:8080/h2-console).
