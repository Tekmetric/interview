FROM eclipse-temurin:21-jdk-alpine as build

WORKDIR /workspace/app

# Copy maven executable and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make the mvnw script executable
RUN chmod +x ./mvnw

# Download dependencies
# This layer will be cached unless pom.xml changes
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Production stage with smaller JRE image
FROM eclipse-temurin:21-jre-alpine

# Add a non-root user to run the app
RUN addgroup -S spring && adduser -S spring -G spring

VOLUME /tmp

# Copy the built application from the build stage
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Set proper ownership
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring

# Set environment variables
ENV JAVA_OPTS=""

# Run the application
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.interview.runningevents.RunningEventsApplication"]