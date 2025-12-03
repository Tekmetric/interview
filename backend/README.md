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

## Submission notes
This submission implements the `Person` object as defined:

```java
public record Person(
        UUID id,
        Email email,
        String firstName,
        String lastName,
        String phoneNumber,
        Address address
) {}
```

The application serves the following endpoints:

| Path                 | Method | Description                          | Body                  | Param(s)                  | Header(s) |
|----------------------|--------|--------------------------------------|-----------------------|---------------------------|-----------|
| `/api/persons`       | GET    | Return page of Persons               |                       | `"pageNumber"`, `"limit"` |           |
|                      | POST   | Create new Person                    | `CreatePersonCommand` |                           |           |
|                      | PUT    | Create new or update existing Person | `UpsertPersonCommand` |                           |           |
| `/api/persons/{id}`  | GET    | Return Person by ID                  |                       |                           |           |
|                      | PATCH  | Update Person                        | `UpdatePersonCommand` |                           |           |
|                      | DELETE | Delete Person                        |                       |                           |           |
| `/api/persons/email` | GET    | Return Person by email               |                       |                           | `"Email"` |

An OpenAPI schema is automatically generated and can be retrieved [here](http://localhost:8080/v3/api-docs) when the application is run, and the Swagger UI can be accessed [here](http://localhost:8080/swagger-ui/index.html).

The data is stored in the `person` table in the H2 database described above. This data can be accessed directly through the console (http://localhost:8080/h2-console).

With more time, I would have liked to improve the following areas:

1. Address and phone numbers could do with better validation, ideally with country-specific rules.
2. I had planned to make `Person.address` and `Person.phoneNumber` nullable, but didn't want to spend time debugging the serialization issues around the update endpoint.

Additionally, in a more complex service, there would be more of the following:
1. Logging. I included examples in `PersonService.update` and `.upsert` since those have divergent paths.
2. Error handling. Coming from Kotlin, I'm used to using monads for error handling, but I would probably stick with exceptions in a Java codebase.
3. Javadocs. I prefer to only include docs that are explanatory rather than just repeating the name of the property/method, but that's a team preference.
4. Unit tests. Since almost all the functionality is covered by the E2E suite, the few unit tests that exist are fine for this submission, but ideally unit tests would cover every possible case, with E2E tests focused on the integrations.
