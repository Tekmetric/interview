# build
FROM maven:3.9-eclipse-temurin-8 AS build

WORKDIR /app

COPY backend/pom.xml .
COPY backend/src ./src

RUN mvn dependency:go-offline -B
RUN mvn package -DskipTests -B

# copy app to runtime
FROM openjdk:8-jre-slim

WORKDIR /app
COPY --from=build /app/target/interview-1.0-SNAPSHOT.jar app.jar

# configure app user
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]