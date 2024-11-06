FROM maven:latest AS builder
WORKDIR /app
COPY . .
RUN mvn package
FROM openjdk:24-slim-bullseye
WORKDIR /app
COPY --from=builder /app/target/interview-1.0-SNAPSHOT.jar .
CMD ["java", "-jar", "interview-1.0-SNAPSHOT.jar"]
