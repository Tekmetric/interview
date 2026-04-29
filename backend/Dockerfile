FROM maven:3-eclipse-temurin-8-alpine AS builder

WORKDIR /var/app/current
COPY . /var/app/current

ENV MAVEN_OPTS="-Dhttps.protocols=TLSv1.2"

RUN mvn dependency:go-offline -B \
    && mvn package \
    && mv target/$(mvn help:evaluate -Dexpression=project.build.finalName -q -DforceStdout).jar target/app.jar



FROM eclipse-temurin:8-jre-alpine

WORKDIR /var/app/current

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /var/app/current/target/app.jar /var/app/current/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]