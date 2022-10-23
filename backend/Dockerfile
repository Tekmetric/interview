FROM openjdk:11.0.6-jre

COPY target/interview-1.0-SNAPSHOT.jar /

CMD java -jar\
    -Dserver.port=8080\
    interview-1.0-SNAPSHOT.jar

EXPOSE 8080
