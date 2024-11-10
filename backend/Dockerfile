# Stage 1: Build the application using Maven and package it
FROM maven:3-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven configuration and source code
COPY pom.xml /app
COPY src /app/src

# Build the application with Maven
RUN mvn clean package

# Extract the packaged JAR to analyze dependencies
RUN jar xf target/interview-1.0-SNAPSHOT.jar

# Use jdeps to identify the required modules for the application
RUN jdeps --ignore-missing-deps -q \
    --recursive \
    --multi-release 17 \
    --print-module-deps \
    --class-path 'BOOT-INF/lib/*' \
    target/interview-1.0-SNAPSHOT.jar > deps.info

# Create a custom Java runtime image with only the required modules
RUN jlink \
    --add-modules $(cat deps.info) \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output /app/runtime

# Stage 2: Use a minimal image for running the application
FROM debian:bookworm-slim

# Create a non-root user and group for security purposes
RUN groupadd -r appuser -g 1001 && useradd --no-log-init -r -g appuser appuser -u 1001

# Copy the custom runtime and the application JAR from the build stage
COPY --from=build /app/runtime /app/runtime
COPY --from=build /app/target/*.jar /app/application.jar

# Ensure that the /app directory and its contents are owned by the non-root user
RUN chown -R appuser:appuser /app

# Expose the port used by the application
EXPOSE 8080

# Run the application as the non-root user for better security
USER appuser

# Start the application with the custom runtime and packaged JAR
CMD ["/app/runtime/bin/java", "-jar", "/app/application.jar"]
