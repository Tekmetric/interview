# Multi-stage build
# Stage 1: Build the application with Maven
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the jar from builder stage
COPY --from=builder /app/target/interview-1.0-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java -jar -Dspring.redis.host=${SPRING_REDIS_HOST:-redis} -Dspring.redis.port=${SPRING_REDIS_PORT:-6379} -Dopensearch.uris=${OPENSEARCH_URIS:-http://opensearch:9200} app.jar"]
