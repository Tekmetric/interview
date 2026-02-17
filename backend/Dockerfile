FROM maven:3.9.6-eclipse-temurin-8 AS build

WORKDIR /workspace

COPY pom.xml .

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp dependency:go-offline

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp clean package -DskipTests


FROM eclipse-temurin:8-jre-jammy

RUN groupadd --system --gid 10001 app \
  && useradd --system --uid 10001 --gid 10001 --no-create-home --shell /usr/sbin/nologin app

WORKDIR /app

COPY --from=build /workspace/target/*.jar /app/app.jar

RUN chown -R 10001:10001 /app

USER 10001:10001

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions \
-XX:+UseCGroupMemoryLimitForHeap \
-XX:MaxRAMFraction=2 \
-XX:+ExitOnOutOfMemoryError \
-Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]

