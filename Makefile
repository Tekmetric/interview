# Variables
IMAGE_NAME ?= backend
IMAGE_TAG ?= $(shell git rev-parse HEAD)
DOCKER_REGISTRY ?= sidineycrescencio
DOCKERFILE_PATH ?= ./backend/Dockerfile
CONTEXT_PATH ?= ./backend
HELM_RELEASE ?= backend
HELM_CHART_PATH ?= ./platform/helm/backend
NAMESPACE ?= backend

# Build the Docker image with a custom Dockerfile path
build:
	docker build -t $(DOCKER_REGISTRY)/$(IMAGE_NAME):$(IMAGE_TAG) -f $(DOCKERFILE_PATH) $(CONTEXT_PATH)

# Push the Docker image to the registry
push:
	docker push $(DOCKER_REGISTRY)/$(IMAGE_NAME):$(IMAGE_TAG)

# Run locally the container
run:
	docker run -d -p 8080:8080 $(DOCKER_REGISTRY)/$(IMAGE_NAME):$(IMAGE_TAG)

# Run Helm upgrade (or install if it doesn't exist)
helm-deploy:
	helm upgrade --install $(HELM_RELEASE) $(HELM_CHART_PATH) --create-namespace --namespace $(NAMESPACE) --set image.repository=$(DOCKER_REGISTRY)/$(IMAGE_NAME) --set image.tag=$(IMAGE_TAG)

help:
	@echo "	Available targets:"
	@echo "	build	Build the Docker image"
	@echo "	push	Push the Docker image to the registry"
	@echo "	run	Run locally the container"
	@echo "	helm-deploy	Deploy to Kubernetes"
