# ----------- Build Stage -----------
FROM eclipse-temurin:21-jdk-noble AS builder

WORKDIR /app

# Copy Maven wrapper and configuration
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Prepare dependencies (cached unless pom.xml changes)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy application source code
COPY src src

# Package the Spring Boot app
RUN ./mvnw package -DskipTests

# Extract layers from the fat jar
RUN mkdir extracted && cd extracted && java -Djarmode=layertools -jar ../target/*.jar extract

# ----------- Runtime Stage -----------
FROM eclipse-temurin:21-jre-noble

ENV JDK_JAVA_OPTIONS="-XX:MaxRAMPercentage=80 -Djava.io.tmpdir=/tmp"
EXPOSE 8080

# Optional health check if actuator is enabled
HEALTHCHECK --interval=10s --retries=3 --start-period=30s --timeout=2s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

WORKDIR /app

# Copy layered jar contents from builder stage
COPY --chown=1000:1000 --from=builder /app/extracted/dependencies/ ./
COPY --chown=1000:1000 --from=builder /app/extracted/snapshot-dependencies/ ./
COPY --chown=1000:1000 --from=builder /app/extracted/spring-boot-loader/ ./
COPY --chown=1000:1000 --from=builder /app/extracted/application/ ./

# Use non-root user
USER 1000:1000

# Launch the application using Spring Boot layered loader
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]