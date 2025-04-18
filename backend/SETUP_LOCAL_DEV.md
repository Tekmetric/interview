# Setup for local dev

## Requirements
- **JDK 21**
- **maven**
- **Intellij (not mandatory)**
- **docker & docker compose**

## How to run

- clone repository locally
- either import & run from IDE or run maven cmd: `mvn spring-boot:run` 
- running tests: `mvn verify`
- For metrics & tracing (optional): `docker-compose up`

#### H2 Configuration
- Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

### Prometheus
