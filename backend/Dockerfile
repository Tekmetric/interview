FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -ntp -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN groupadd --system app && useradd --system --gid app --home /app app
COPY --from=build --chown=app:app /workspace/target/interview-*.jar /app/
USER app
# Allows `docker run --read-only`
VOLUME /tmp
EXPOSE 8080 8081
HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 \
    CMD wget -qO- http://localhost:8081/actuator/health/liveness || exit 1
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","exec java -XX:MaxRAMPercentage=75.0 $JAVA_OPTS -jar /app/interview-*.jar"]
