FROM ubuntu AS build

RUN apt-get update && apt-get install -y \
    java-common \
    maven \
    && rm -rf /var/lib/apt/lists/*

COPY . /app
WORKDIR /app

RUN mvn package

FROM openjdk:26-jdk

# Make app user
RUN adduser -U app

COPY --from=build /app/target/interview-1.0-SNAPSHOT.jar /app/interview-1.0-SNAPSHOT.jar
RUN chown -R app:app /app

USER app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/interview-1.0-SNAPSHOT.jar"]
