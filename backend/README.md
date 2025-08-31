# Java Spring Boot Vehicles API

#### Prerequisites

- Java 17+

#### Build and run your app

- `./gradlew bootRun`

#### Test that your app is running

- `curl -X GET   http://localhost:8080/actuator/health`

#### API Endpoints

Use Swagger UI to explore and test the API endpoints:

- Swagger UI: http://localhost:8080/swagger-ui/index.html

#### Generate Vehicle Data

To generate vehicle data you can run the following script in your terminal:

```bash
python3 ./scripts/generate_vehicle_data.py
```

Optional argument: `--num <NUMBER_OF_VEHICLES>` (default is 100)

This will create a data.sql file in the `backend/src/main/resources` directory. Spring Boot will automatically load this
data into the H2 in-memory database when the application starts.

#### H2 Configuration

- Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

### Submitting your coding exercise

Once you have finished the coding exercise please create a PR into Tekmetric/interview
