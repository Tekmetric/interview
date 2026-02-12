# ----- Build stage -------
ARG MAVEN_VERSION=3.9.8
ARG JDK_DIST=eclipse-temurin
ARG JDK_VERSION=21

# Build stage (multi‑arch)
FROM --platform=$BUILDPLATFORM maven:${MAVEN_VERSION}-${JDK_DIST}-${JDK_VERSION}-jammy AS build

# Work‑around for OpenJDK “SIGILL on AArch64 when SVE is unavailable” (JDK‑8345296)
# See: https://bugs.openjdk.org/browse/JDK-8345296
ENV JAVA_TOOL_OPTIONS="-XX:UseSVE=0"

WORKDIR /build

# 1. Prime the Maven cache using only pom.xml. This speeds up subsequent rebuilds
COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline

# 2. Copy the rest of the source tree and the code‑quality configuration that Checkstyle expects, then compile.
COPY src ./src
COPY config ./config
RUN mvn -B -ntp -DskipTests package

# ---- Runtime stage ------
FROM --platform=$TARGETPLATFORM eclipse-temurin:${JDK_VERSION}-jre-jammy

WORKDIR /app

# copy fat‑jar built in the previous stage
COPY --from=build /build/target/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
