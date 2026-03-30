ARG MAVEN_IMAGE=maven:3.9.9-eclipse-temurin-8
ARG JAVA_IMAGE=eclipse-temurin:8-jre

FROM ${MAVEN_IMAGE} AS build
WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn package

FROM ${JAVA_IMAGE} AS backend

WORKDIR /app
ENV CATALINA_HOME=/app

RUN groupadd -g 1001 tomcat \
    && useradd -u 1001 -g 1001 -m -s /bin/sh tomcat \
    && mkdir -p "$CATALINA_HOME/tmp" \
    && chown -R 1001:1001 $CATALINA_HOME

COPY --from=build --chown=1001:1001 /app/target/interview-1.0-SNAPSHOT.jar /app/app.jar

USER tomcat
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]