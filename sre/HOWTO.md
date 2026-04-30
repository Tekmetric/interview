# How to Build the Backend Docker Container

## Prerequisites

- Docker installed on your machine
- Access to the `backend/` directory

## Build the Docker Image

From the project root, run:

```bash
cd backend
docker build -t interview-backend .
```

## Run the Container

```bash
docker run -p 8080:8080 interview-backend
```

## Image Details

| Property | Value |
|----------|-------|
| Base image | OpenJDK 8 (slim) |
| Exposed port | 8080 |
| Framework | Spring Boot 2.2.1 |
| Database | H2 (in-memory) |

## Verify Running Container

```bash
curl http://localhost:8080
```

## Optional: Tag and Push to Registry

```bash
# Tag the image
docker tag interview-backend:latest your-registry/interview-backend:latest

# Push to registry
docker push your-registry/interview-backend:latest
```