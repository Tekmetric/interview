# syntax=docker/dockerfile:1.7

# ---- Build stage ----------------------------------------------------------
# Maven image pinned by digest would be ideal; using tag for readability.
FROM maven:3.9-eclipse-temurin-8 AS build

WORKDIR /workspace

# Copy pom first so dependency resolution layer caches when only code changes.
COPY backend/pom.xml pom.xml
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q dependency:go-offline

COPY backend/src src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q package -DskipTests \
 && cp target/interview-*.jar app.jar

# ---- Runtime stage --------------------------------------------------------
# Eclipse Temurin 8 JRE on Ubuntu Jammy. Small, patched, widely trusted.
FROM eclipse-temurin:8-jre-jammy

# Non-root user baked in at build time. Never run the JVM as root.
RUN groupadd --system --gid 1000 app \
 && useradd  --system --uid 1000 --gid app --home-dir /app --shell /sbin/nologin app \
 && mkdir -p /app \
 && chown -R app:app /app

WORKDIR /app
COPY --from=build --chown=app:app /workspace/app.jar app.jar

USER app:app

# 8080 = application traffic, 8081 = actuator (management) traffic.
# Split ports let us apply tighter NetworkPolicy to 8081.
EXPOSE 8080 8081

# Sensible JVM defaults for a containerized workload. Can be overridden
# via Helm values. MaxRAMPercentage honors cgroup memory limits.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["java","-jar","/app/app.jar"]
