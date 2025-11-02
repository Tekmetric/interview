# Java Spring Boot API Coding Exercise

## Steps to get started:

#### Prerequisites
- Maven
- Java 21

#### Fork the repository and clone it locally
- https://github.com/Tekmetric/interview.git

#### Import project into IDE
- Project root is located in `backend` folder

#### Run the app
- Option A — Run directly with Maven (from the `backend` folder):
  - `mvn spring-boot:run`
- Option B — Build a JAR and run (from the `backend` folder):
  - `mvn clean package -DskipTests`
  - `java -jar target/interview-1.0-SNAPSHOT.jar`

#### Test that your app is running
- `curl -X GET http://localhost:8080/api/welcome`

#### API documentation (Swagger / OpenAPI)
- Swagger UI: http://localhost:8080/swagger-ui/index.html
  - Shortcut (redirects to the index page): http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml

#### Running tests
- From the `backend` folder:
  - Run all tests: `mvn test`
  - Run a single test class (example): `mvn -Dtest=com.interview.services.UserServiceTest test`
  - Run a single test method (example): `mvn -Dtest=com.interview.services.UserServiceTest#shouldCreateUser test`
- Test reports: `backend/target/surefire-reports/`
  - Look for `.txt` and `.xml` reports for each test class.

#### Local E2E / API smoke test (test-api.sh)
- This repo includes a simple bash script to exercise the main API endpoints locally.
- Prerequisite: Start the backend so it’s reachable at `http://localhost:8080` (see "Run the app" above).
- Run (macOS/Linux):
  - From the `backend` folder: `chmod +x test-api.sh && ./test-api.sh`
- Run (Windows):
  - Use Git Bash: `bash backend/test-api.sh`
  - Or run inside WSL from the `backend` folder: `bash test-api.sh`
- What the script does:
  - Attempts an invalid create (expects HTTP 400) to show validation.
  - Creates a user `johndoe` with password `SecurePass123$`.
  - Reads and updates the user using Basic Auth.
  - Creates, lists, updates, and soft-deletes a bank account via `/api/bank` endpoints.
- Notes:
  - Basic Auth credentials used by the script: `johndoe` / `SecurePass123$`.
  - The script expects the same API shapes implemented in this project; adjust if you change endpoints.

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
