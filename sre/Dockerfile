# =============================================================================
# Multi-stage Dockerfile for Tekmetric Backend Service
# =============================================================================
# Build: docker build -t tekmetric-backend:local -f sre/Dockerfile .
# Run:   docker run -p 8080:8080 tekmetric-backend:local
# =============================================================================

# -----------------------------------------------------------------------------
# Stage 1: Build
# -----------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-8 AS builder

WORKDIR /app

# Copy pom.xml first to leverage Docker layer caching for dependencies
COPY backend/pom.xml .

# Download dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy source code
COPY backend/src ./src

# Build the application (skip tests for faster builds in CI)
RUN mvn package -DskipTests -B

# -----------------------------------------------------------------------------
# Stage 2: Runtime
# -----------------------------------------------------------------------------
# Using amazoncorretto for better multi-arch support (AMD64 + ARM64)
FROM amazoncorretto:8-alpine

WORKDIR /app

# Install curl for healthcheck (minimal addition to Alpine)
RUN apk add --no-cache curl

# Create non-root user for security
RUN addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -D -s /bin/sh appuser

# Copy the built JAR from builder stage
COPY --from=builder /app/target/interview-1.0-SNAPSHOT.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8080

# Health check using Spring Boot Actuator
# Note: Spring Boot 2.2.x uses /actuator/health (liveness/readiness added in 2.3+)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM options for containerized environments
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
