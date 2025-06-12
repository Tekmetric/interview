FROM maven:3-ibm-semeru-21-jammy AS builder
COPY ./pom.xml pom.xml
COPY ./src src/
RUN mvn package -DskipTests

FROM maven:3-ibm-semeru-21-jammy
RUN apt-get -y autoremove && \
  apt-get -y autoclean && \
  rm -rf /var/lib/apt/lists/* && \
  useradd -m -u 1000 tekmetric && \
  mkdir -p /app && chown -R tekmetric:tekmetric /app

ARG JAR_FILE=target/*.jar
ARG SERVER_PORT=8080
ENV JAVA_OPTS=""

USER tekmetric
WORKDIR /app
COPY --from=builder --chown=tekmetric:tekmetric ${JAR_FILE} app.jar

EXPOSE $SERVER_PORT
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar app.jar" ]
