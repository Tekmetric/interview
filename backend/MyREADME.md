# Shop management system
Web application for managing shop operations built with Spring Boot 4.

## Prerequisites
Maven and Java 25. No external database required (in-memory H2).

## Install
mvn clean install
Optionally skip all tests (unit + integration): `mvn clean install -DskipTests -DskipITs`

## Run
mvn spring-boot:run
mvn spring-boot:stop

## API browser
http://localhost:8080/swagger-ui.html

## More details
See `docs/` directory for architecture, dependencies, and dev-tools documentation.
