# === 1. Build stage ===
FROM eclipse-temurin:25-jdk-alpine-3.22 AS builder
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn

RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package -B

# === 2. Run stage ===
FROM eclipse-temurin:25-jre-alpine-3.22
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]