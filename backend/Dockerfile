FROM public.ecr.aws/amazoncorretto/amazoncorretto:8-al2023-jre AS base

ARG JAVA_OPTS=""
ENV JAVA_OPTS="${JAVA_OPTS}"

FROM public.ecr.aws/amazoncorretto/amazoncorretto:8-al2023-jdk AS builder

WORKDIR /build

COPY . ./

RUN dnf install -y maven-amazon-corretto8 && \
    mvn clean package

FROM base AS final

WORKDIR /app

RUN adduser docker && chown docker:docker /app
COPY --from=builder --chown=docker:docker /build/target/interview-1.0-SNAPSHOT.jar ./interview.jar

USER docker

EXPOSE 8080

CMD ["java", "-jar", "interview.jar", "${JAVA_OPTS}"]
