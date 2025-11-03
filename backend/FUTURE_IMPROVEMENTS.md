# Enhancements and Improvements Proposal

## Project structure and organization notes

### 1. Upgrade to new tool versions 
Upgrade Java, Spring Boot, and dependencies to modern LTS versions to improve 
stability, security, and maintainability.

### 2. Replace JPA with lightweight ORM and use immutable domain model
JPA entities are mutable and often introduce hidden side effects that significantly 
increase code complexity and maintenance overhead as the project grows, especially 
in large-scale distributed systems.
A lightweight and more predictable ORM such as Spring JDBC Template or MyBatis can be used 
instead. These solutions make it easier to keep the domain model separated from the persistence 
layer without relying on additional DTOs or complex mapping logic.
Using immutable domain objects ensures better reliability, easier debugging, and fewer “hacky” 
workarounds in the long run.

### 3. Move API definition into separate module
Move API contracts into a separate module so clients and other services can reuse them as a dependency.

### 4. Use schema migration tools
Use tools like Flyway or Liquibase to manage database schema changes safely and consistently 
across environments.


## API improvements

### 1. Pagination
Use `skip`, `limit`, `hasNext` instead of `page`, `pageSize`, `totalPages` - it is more convenient and 
efficient, especially for infinite scrolling and virtualized tables or lists on browsers and mobile apps.
Using `totalPages` is a bad idea when dealing with a large number of rows or partitioned databases. 

### 2. Authentication and Authorization
In most cases, authentication and authorization should be handled by a higher-level service/layer 
responsible for user management and access policies. Our service should be adjusted to work properly 
with the chosen authentication system. 


## DB optimizations
1. Add partitioning / sharding (for example partitioning by YYYY/MM) to improve scalability.
2. Optimize DB indexes according to the most frequent query patterns.
3. Add caching based on system use cases.


## Test improvements

### 1. Use TimeService abstraction for current date-time
Instead of reading the current time directly from the system clock, introduce a time service abstraction.
This allows controlled time manipulation for testing purposes using different time service implementations.

### 2. Improve tests
Add common helpers for frequently used test scenarios and add more tests to cover the full invoice 
lifecycle and money-related calculations.


## Next features proposal
1. Add multi-currency support.
2. Add discounts handling.
3. Improve tax calculation logic.

