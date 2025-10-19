FROM docker.io/maven:3.9-eclipse-temurin-21-alpine as build_image

WORKDIR /build

# this is done in a separate step so the dependencies layer can be reused, rather than downloading them all every time
# there's a code file change
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

COPY src ./src/
RUN --mount=type=cache,target=/root/.m2 mvn package

# normally I'd just end it with the above and COPY the jar in the runtime stage, but the spring docs recommend this
# because it avoids the overhead of the uber jar
RUN java -Djarmode=tools -jar target/interview-1.0-SNAPSHOT.jar extract --layers --destination extracted


FROM gcr.io/distroless/java21-debian12:nonroot as runtime_base

COPY --from=build_image /build/extracted/dependencies/ /app/
COPY --from=build_image /build/extracted/spring-boot-loader/ /app/
COPY --from=build_image /build/extracted/snapshot-dependencies/ /app/
COPY --from=build_image /build/extracted/application/ /app/

EXPOSE 8080
WORKDIR /app

# the java distroless image's entrypoint is already invoking java -jar, so we just provide the CMD
CMD ["interview-1.0-SNAPSHOT.jar"]