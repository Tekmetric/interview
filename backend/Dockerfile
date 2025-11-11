# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean install

# Stage 2: Create the final image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/interview-1.0-SNAPSHOT.jar .
EXPOSE 8080
CMD ["java", "-jar", "interview-1.0-SNAPSHOT.jar"]
