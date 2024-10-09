# Cats API v1.1

## Usage
Run the following command from this directory to build and run the application:

`mvn package && java -jar target/interview-1.1-SNAPSHOT.jar`


## Documentation
While the application is running, access to this API's documentation can be found at the following link.

http://localhost:8080/swagger-ui/index.html#/cat-resource

Use the `Try it out` features to interact with a locally hosted sandbox instance of this API.


## Data Source
Data is persisted in a runtime database. While the application is running, access to the database console can be found at the following link.

http://localhost:8080/h2-console

After gaining access to the console, use the configuration below to connect.

```
JDBC URL: jdbc:h2:mem:testdb
User Name: sa
Password: password
```


## Notable Additions
- Data source initialization on start up
- Exception handling
- API documentation