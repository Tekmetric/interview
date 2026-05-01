# filepath: backend/Dockerfile
# Build stage
FROM maven:3.8-openjdk-8 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Download OpenTelemetry Java agent
RUN mkdir -p /opt/opentelemetry \
    && curl -L -o /opt/opentelemetry/opentelemetry-javaagent.jar \
    "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.32.0/opentelemetry-javaagent.jar"

# Runtime stage
FROM openjdk:8-jre-slim
WORKDIR /app

# Install curl for health checks and copy agent
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/list/*
COPY --from=build /opt/opentelemetry /opt/opentelemetry
COPY --from=build /app/target/*.jar app.jar

# Environment variables for OpenTelemetry
ENV OTEL_SERVICE_NAME=interview-backend
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
ENV JAVA_TOOL_OPTIONS="-javaagent:/opt/opentelemetry/opentelemetry-javaagent.jar"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]