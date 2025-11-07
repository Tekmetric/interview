FROM maven:3.9.11-amazoncorretto-8 AS builder
COPY pom.xml .
COPY src ./src
RUN mvn package

FROM amazoncorretto:8-alpine
RUN addgroup -S --gid 2000 apps && adduser -S -G apps --uid 1000 backend
WORKDIR /app
RUN chown -R backend:apps /app
USER backend
COPY --from=builder target/interview-1.0-SNAPSHOT.jar ./app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
