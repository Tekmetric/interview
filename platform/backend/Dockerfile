FROM eclipse-temurin:11-jre

LABEL maintainer="Tekmetric Site Reliability <sre@tekmetric.com>"

ENV DATABASE_URL="jdbc:h2:mem:testdb"
ENV DATABASE_USER="sa"
ENV DATABASE_PASS="password"

COPY backend/target/interview-1.0-SNAPSHOT.jar /app/interview-1.0-SNAPSHOT.jar

RUN useradd javaguy -d /app \
  && chown -R javaguy:javaguy /app

WORKDIR /app

USER javaguy

EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=5s CMD curl http://localhost:8080/api/welcome || exit 1

CMD ["java", "-jar", "interview-1.0-SNAPSHOT.jar"]