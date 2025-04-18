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

## Local testing using an API client
Bearer token is required for all endpoints with security enabled.
Valid tokens for both ADMIN and USER roles can be found in swagger ui at the top or in [Intellij HTTP Client env file](apiclient/http-client.env.json).

### Using Intellij HTTP Client
For testing with API client we can use the Intellij Rest client
- For Job API [apiclient/job-crud](apiclient/job-crud.http)
- For Task API [apiclient/task-crud](apiclient/task-crud.http)
- Make sure to select the `dev` environment inside Intellij.

### Using Swagger
Access swagger at http://localhost:8080/swagger-ui/index.html.

Using the swagger ui you can add authorization as specified at the top and also export curl requests if need be. 


#### H2 Configuration
- Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

### Prometheus
Using prometheus we can query the metrics of the service.

- Console: http://localhost:9090/

### Zipkin
Zipkin can be used to visualize the traces for the requests the service gets.
Currently configured to sample 100% of the requests.
- Console: http://localhost:9411/

### Grafana
Grafana is set up to visualize the metrics from prometheus and the traces from Zipkin

- Console: http://localhost:3000/

- We have imported a jvm dashboard to visualize the app metrics