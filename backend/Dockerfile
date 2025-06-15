FROM --platform=linux/arm64 maven:3.9-amazoncorretto-21 AS build

WORKDIR /app

# Copy the Maven POM and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Create a smaller runtime image
FROM --platform=linux/arm64 amazoncorretto:21-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
