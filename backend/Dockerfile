FROM adoptopenjdk/openjdk11:ubi
VOLUME /tmp
ADD target/interview-*.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT java $JAVA_OPTS -jar /app.jar ${JVM_OPTS}