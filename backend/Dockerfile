# ---- build stage ----
    FROM maven:3.9-eclipse-temurin-8 AS build
    WORKDIR /app
    
    COPY pom.xml .
    RUN mvn -B -q -DskipTests dependency:go-offline
    
    COPY src ./src
    RUN mvn -B -DskipTests package
    
    # ---- runtime stage (pinned Alpine JRE 8) ----
    # Pinning to an Alpine variant helps reduce OS CVE surface vs Debian-based JRE images.
    FROM eclipse-temurin:8-jre-alpine-3.21
    
    # Create a non-root user/group (Alpine style)
    RUN addgroup -S appgroup \
     && adduser  -S -u 10001 -G appgroup appuser
    
    WORKDIR /app
    COPY --from=build /app/target/*.jar /app/app.jar
    
    # Java often needs /tmp writable (also plays well with readOnlyRootFilesystem + /tmp emptyDir in k8s)
    RUN mkdir -p /tmp \
     && chown -R 10001:appgroup /tmp /app
    
    USER 10001
    EXPOSE 8080
    ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
    
    ENTRYPOINT ["java","-jar","/app/app.jar"]
    