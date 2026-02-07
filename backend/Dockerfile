FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY . .

# Build only the server-module (and its dependencies)
RUN mvn -pl server-module -am clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy AS runtime

WORKDIR /app

# Copy the built Spring Boot JAR from the build stage
COPY --from=build /workspace/server-module/target/*.jar app.jar

# Spring Boot default port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java","-jar","/app/app.jar"]
