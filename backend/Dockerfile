# syntax=docker/dockerfile:1

# ---------- Build stage ----------
FROM maven:3.9.6-eclipse-temurin-8 AS build
WORKDIR /workspace

# Leverage dependency layer caching
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests package


# ---------- Runtime stage ----------
FROM eclipse-temurin:8-jre
WORKDIR /app

# Copy the built jar from the builder image
COPY --from=build /workspace/target/interview-1.0-SNAPSHOT.jar /app/app.jar

# Allow optional JVM flags at runtime (e.g., -Xms256m -Xmx512m)
ENV JAVA_OPTS=""

# Spring Boot default port
EXPOSE 8080

# Run as non-root user for better security
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]


