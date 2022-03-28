# Tekmetric Interview - BE Assignment

## Summary
This project provides basic CRUD operations with LIST API and supports paging, sorting, and filtering.
For the sake of simplicity, only the Inventory entity was created and used in this project.

API DOC is created by the Swagger tool. You can check the API doc with the given URL below.
**API_DOC URL:** http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

Postman Collection "be_postman_collection.json" is added under the backend directory.

You can test API endpoints via FE project or POSTMAN.

## How to Run Application
with maven: `./mvnw spring-boot:run`

with Docker:
You can run the application via docker.
Docker file is under the root(backend) directory. You can run the given commands below.
`./mvnw package . `
`docker build -t tekmetric-be . `
`docker run -p 8080:8080 tekmetric-be`


## Test Data
Test data is loaded via liquabase `load_data.xml` script and test data are in the `/resources/database/data` directory. You can change the test data according to your needs.
In the test data there are;
- 120 inventories with ids from 1001 to 1120


### How To Run Application Test
There are 9 integration tests implemented. The test coverage is about %65

You can run the application tests with maven command `./mvnw test`


### Design Consideration

**1.** I have created a BaseEntity with id, createdAt, and updatedAt fields. This entity is a base entity for all entities.
I have taken the advantage of **Lombok** project to reduce the verbosity and used the defined annotations(@Getter, @Setter, @Data, etc)
Since the JPA implementation of Hibernate works with the Proxy classes and the Lombok implementation of the equals() and hashcode() methods are not compatible with Hibernate, I have overridden them not used Lombok annotations for equals() and hashcode() methods.
I have preferred the optimistic lock mechanism and added **@Version** annotation.

**2.** I have created an Inventory entity with the required fields and also added the deleted_At field to make it **softdeleteable**.

**3. ** I have created **DTOs** for request and response payloads. I have used **REST API** with required DTOs. For the fields validations, I have used javax validation like @NotNull. I have used Jackson annotations like @JsonInclude and @JsonProperty and made only the required fields updatable.

**4.** I have preferred to use **Spring Data JPA Repository** for the basic queries which are extended from PagingAndSortingRepository. For the requirement of dynamic queries because of search and filters, I have decided to use **Spring Data JPA Specification.** I could also use **Criteria API** or **QueryDSL** but Spring Data JPA Specification has built-in methods for this operation and also no need to bind EntityManagerFactory. I think Spring Data JPA Specification is a clear solution for the dynamic queries.

**5.** I have created a base exception class (**TekmetricDomainException**) with **code** and **message** fields. There are 6 domain exceptions and all of them are extended from this base class.
I have created a global configuration class for exceptions and handled all exceptions with this configuration before sending them to the client. While handling the exceptions, for all domain exceptions and IllegalArgumentExceptions, the status code **400** is set, for all other exceptions the status code **404** is set.

**6. ** I have used **liquibase** as a migration tool. DB-related SQL queries are in the data.sql file as expected. I have also created a changeset with the name **load_data.xml** to load test data from the test CSV files and import them to DB. This file is under the resources/database/data folder. I have preferred to use a CSV file to make changes on the test data easy.

**7.**  I have made the project dockerize to run it easily.
