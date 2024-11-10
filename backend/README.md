# Java Spring Boot API Coding Exercise

## Steps to get started:

#### Prerequisites
- Maven
- Java 1.8 (or higher, update version in pom.xml if needed)

#### Fork the repository and clone it locally
- https://github.com/Tekmetric/interview.git

#### Import project into IDE
- Project root is located in `backend` folder

#### Build and run your app
- `mvn package && java -jar target/interview-1.0-SNAPSHOT.jar`

#### Test that your app is running
- `curl -X GET   http://localhost:8080/api/welcome`

#### After finishing the goals listed below create a PR

### Goals
1. Design a CRUD API with data store using Spring Boot and in memory H2 database (pre-configured, see below)
2. API should include one object with create, read, update, and delete operations. Read should include fetching a single item and list of items.
3. Provide SQL create scripts for your object(s) in resources/data.sql
4. Demo API functionality using API client tool

### Considerations
This is an open ended exercise for you to showcase what you know! We encourage you to think about best practices for structuring your code and handling different scenarios. Feel free to include additional improvements that you believe are important.

#### H2 Configuration
- Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

### Submitting your coding exercise
Once you have finished the coding exercise please create a PR into Tekmetric/interview

### Build and push image

Ensure the following tools are installed and accessible from your command line:

- Make
- Helm
- Docker CLI

Use the provided Makefile in the root directory to build and push the Docker image:

- For building the image: `make build`
- For pushing the image: `make push`

### Run locally using docker

To run the application locally using Docker, execute:

- `make run`

### Deploying to Kubernetes

You can deploy the application to a Kubernetes cluster using Helm:

- `make helm-deploy`

Note:
For the `Service Monitor` and `Ingress` features, ensure Prometheus and NGINX are installed and running in the cluster.

### Makefile Overview

The Makefile in this project offers simple commands to build, push, and deploy your app with Docker and Helm. It makes routine tasks easier and lets you customize them with variables.

Available Variables

Here are the key variables you can override when running make:

```
IMAGE_NAME: The name of the Docker image to build. Default: backend
IMAGE_TAG: The tag for the Docker image. Default: the latest Git SHA
DOCKER_REGISTRY: The Docker registry to push the image to. Default: sidineycrescencio
DOCKERFILE_PATH: Path to the Dockerfile used for building. Default: ./backend/Dockerfile
CONTEXT_PATH: The build context path for Docker. Default: ./backend
HELM_RELEASE: The Helm release name for deployment. Default: backend
HELM_CHART_PATH: Path to the Helm chart for deployment. Default: ./platform/helm/backend
NAMESPACE: The Kubernetes namespace for deployment. Default: backend
```

Examples of Overriding Variables

You can override any variable when running make by passing it as an argument:

Override the IMAGE_TAG:

`make build IMAGE_TAG=custom-tag`

Change the Docker Registry:

`make push DOCKER_REGISTRY=registry.example.com`

Deploy to a Different Namespace:

`make helm-deploy NAMESPACE=custom-namespace`
