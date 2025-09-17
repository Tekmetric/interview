# Backend Docker Build & Run

This document explains how to build and run the Spring Boot backend as a Docker container without modifying the application code.

## Prerequisites
- Docker 20.10+
- Internet access to pull Maven and JRE base images

## Build the image
From the `backend` directory:

```bash
# Build with a descriptive tag
docker build -t interview-backend:latest .
```

The Dockerfile uses a multi-stage build to compile the app with Maven and produce a runtime image based on Temurin JRE 8.

## Run the container

```bash
# Default run on port 8080
docker run --rm -p 8080:8080 --name interview-backend interview-backend:latest
```

Optionally pass JVM flags with `JAVA_OPTS`:

```bash
docker run --rm -p 8080:8080 -e JAVA_OPTS="-Xms256m -Xmx512m" interview-backend:latest
```

## Test the API

```bash
curl -s http://localhost:8080/api/welcome
```

You should see:

```
Welcome to the interview project!
```

## Clean up

```bash
docker rm -f interview-backend 2>/dev/null || true
```

## Notes
- The image runs as a non-root user.
- Build layers are cached; subsequent builds will be faster if `pom.xml` dependencies don’t change.
