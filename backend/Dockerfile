FROM amazoncorretto:25.0.0-alpine3.22@sha256:807ea3c4000a052986cd1e7097a883f9cd7a6e527f73841f462e3d04851b8835

RUN addgroup -S tekmetric && adduser -S tekmetric -G tekmetric

WORKDIR /app

COPY --chown=tekmetric:tekmetric target/app.jar app.jar

USER tekmetric

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
CMD []