# Stage 1: build with Maven (no wrapper required)
FROM maven:3.9.11-eclipse-temurin-21 AS builder
WORKDIR /workspace

# copy pom first to leverage Docker cache for dependencies
# (repo root context is used by the workflow, so reference the backend subdirectory)
COPY pom.xml ./

# copy the backend source code
COPY src ./src

# build the project (skip tests to speed up the image build in CI)
RUN mvn -B -DskipTests package

# Stage 2: runtime image
FROM eclipse-temurin:21-jre-jammy AS runtime
ARG JAR_FILE=/workspace/target/interview-1.0-SNAPSHOT.jar

# create non-root user
RUN useradd -m appuser || true
WORKDIR /app

# copy artifact from builder
COPY --from=builder ${JAR_FILE} app.jar
RUN chown appuser:appuser app.jar || true

USER appuser
EXPOSE 8080
ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]