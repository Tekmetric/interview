# Java Spring Boot Application for Managing Vehicles

## Steps to get started:

#### Prerequisites
- Maven
- Java 17 
- Docker (running on the machine)
- Docker Compose

#### Fork the repository and clone it locally
- https://github.com/Tekmetric/interview.git

#### Import project into IDE
- Project root is located in `backend` folder

#### Build and run your app
- `docker-compose -f keycloak/keycloak-docker.yml up -d` 
  - starts a keycloak server that is pre-configured with a realm named `tekmetric` and a client named `swagger-ui` to be used when logging in from swagger.
- `mvn package && java -jar target/interview-1.0-SNAPSHOT.jar`
** As part of the build process the application will run also integration tests which require the keycloak server to be up and running (in this case it will use a keycloak testcontainer)

#### Build docker image for the app (make sure the jar is built first and available in target folder)
- `docker build -t tekmetric/interview .` OR ` docker build -t tekmetric/interview . --platform="linux/amd64"` for M1 mac

#### Test that your app is running
- `curl -X GET   http://localhost:8080/actuator/health`

### Access the API documentation using swagger at location http://localhost:8080/api-docs/swagger-ui/index.html
In order to be able to access the secured endpoints you will need to obtain a JWT token from the keycloak server.
You can do this by logging in via swagger UI using the `Authorize` button on the right hand side and the `swagger-ui` client (at the moment you don't need any scopes).
Select `Authorize` button from the popup and this will redirect you to the keycloak login page. 

On the login page use the following credentials: username `testuser@tekmetric.com` and password `user123`.

#### Supported functionalities:
- Create a vehicle
- Update a vehicle
- Delete a vehicle
- Get vehicle details by id
- Get vehicles matching filtering criteria (id, type, vin, productionYear)

#### H2 Configuration
- Console: http://localhost:8080/h2-console 
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

### Submitting your coding exercise
Once you have finished the coding exercise please create a PR into Tekmetric/interview
