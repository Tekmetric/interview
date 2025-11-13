# Project Setup

ensure-deps: ## Install deps (ctlptl) via brew
	brew install ctlptl helm
	helm repo add gitea-charts https://dl.gitea.com/charts/
	helm repo add traefik https://traefik.github.io/charts
	helm repo update
# Cluster manipulation

cluster-up: ## Use ctlptl to bring up a local cluster with registry
	ctlptl apply -f cluster.yaml

cluster-down: ## Delete kind cluster w/ ctlptl
	ctlptl delete -f cluster.yaml

# Container Image build/push
build: ## Build the docker image
	docker build -f Dockerfile ./../backend -t localhost:62265/interview-svc

push: ## Push image to local registry
	docker push localhost:62265/interview-svc

delete-image:
	docker rmi localhost:62265/interview-svc

# Gitops Bootstrap
bootstrap-level0:
	-kubectl create ns gitea
	-helm upgrade -i gitea gitea-charts/gitea --set namespace=gitea
	-helm upgrade -i traefik traefik/traefik -f manifests/bootstrap/traefik-values.yaml

bootstrap-remote:
	./scripts/setup-remote.sh

deploy: ## Deploy the service to the local cluster
	kubectl apply -f manifests/

# Misc

run-docker: ## Run the service in docker (port 8080 mapped to local)
	docker run --rm -p 8080:8080 interview-svc

# Top-level convenience targets

up: ## Create cluster, build image, deploy manifests
up: cluster-up build deploy

down: ## Delete cluster, delete local image
down: cluster-down delete-image


.PHONY: help delete-image down run-docker deploy push build cluster-down cluster-up

help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
