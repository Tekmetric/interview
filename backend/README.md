# Application changes
## API Endpoints

The application provides the following REST API endpoints with Swagger documentation:

- `GET /api/version` - Returns the API version ("v1")
- `GET /api/welcome` - Returns a welcome message
- `POST /api/users` - Creates a new user. Requires a valid User object in the request body.
- `PUT /api/users` - Updates an existing user. Requires a valid User object in the request body.
- `GET /api/users` - Retrieves a list of all users.
- `GET /api/users/{id}` - Retrieves a specific user by ID.
- `DELETE /api/users/{id}` - Deletes a user by ID.

## Dockerization

The application can be containerized using Docker.

### Building the Docker Image

1. Build the application JAR:
   ```bash
   mvn package
   ```

2. Build the Docker image:
   ```bash
   docker build -t interview-app .
   ```

### Running the Docker Container

Run the container with:
```bash
docker run -p 8080:8080 interview-app
```

The application will be available at `http://localhost:8080`.

### Tests

- Added mocktest to test various endpoints
- Added testcontainer based Integrationtest using Mysql.

### Security Configuration

- Added WebSecurityConfig to permit all URLs for development purposes.


### Production Configuration
For production, JWT token validation will be implemented to secure the requests.

For production deployment, use the `prod` Spring profile to enable secure database connections via AWS Secrets Manager.

Run the application with:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

The prod profile configures the application to fetch database credentials from AWS Secrets Manager, ensuring sensitive information is not hardcoded. Make sure to set up the secret in AWS Secrets Manager with the name `prod/db` containing the database username and password in JSON format.

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
