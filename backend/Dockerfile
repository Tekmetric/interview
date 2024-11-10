ARG JAVA_VERSION=21

FROM --platform=$BUILDPLATFORM maven:3.9.9-amazoncorretto-${JAVA_VERSION} AS build
WORKDIR /usr/src/tekmetric-interview

COPY .mvn pom.xml ./
ARG TEST_DEP_CHANGE
RUN --mount=id=tekmetric-interview-mvn-cache,type=cache,target=/root/.m2 \
    mvn dependency:go-offline

ARG VERSION
ARG TEST_SRC_CHANGE
RUN --mount=source=src,target=/usr/src/tekmetric-interview/src \
    --mount=id=tekmetric-interview-mvn-cache,type=cache,target=/root/.m2 \
    mvn package

FROM scratch AS jar
COPY --from=build /usr/src/tekmetric-interview/target/*.jar /

FROM openjdk:${JAVA_VERSION}-slim AS release
WORKDIR /app

RUN adduser --system --group spring
USER spring:spring

COPY --from=build /usr/src/tekmetric-interview/target/*.jar ./backend.jar

EXPOSE 8080

CMD ["java", "-jar", "backend.jar"]
