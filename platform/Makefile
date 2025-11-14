# Project Setup

ensure-deps: ## Install deps (ctlptl) via brew
	brew install ctlptl helm
	helm repo add gitea-charts https://dl.gitea.com/charts/
	helm repo add traefik https://traefik.github.io/charts
	helm repo add external-dns https://kubernetes-sigs.github.io/external-dns/
	helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
	helm repo add sloth https://slok.github.io/sloth
	helm repo update

# Local Cluster manipulation

cluster-up: ## Use ctlptl to bring up a local cluster with registry
	ctlptl apply -f cluster.yaml

cluster-down: ## Delete kind cluster w/ ctlptl
	ctlptl delete -f cluster.yaml

# Container Image build/push
build: ## Build the docker image
	docker build --platform linux/amd64 -f Dockerfile ./../backend \
		-t localhost:62265/interview-svc \
		-t registry.loosh.cloud/interview-svc:latest

push-local: ## Push image to local registry
	docker push localhost:62265/interview-svc

push:
	docker push registry.loosh.cloud/interview-svc

delete-image:
	docker rmi localhost:62265/interview-svc
	docker rmi registry.loosh.cloud/interview-svc

install-remote-traefik: ## Install traefik to remote cluster
	-helm upgrade -i traefik traefik/traefik -f manifests/remote/traefik-values.yaml

install-remote-dns: ## Install external dns to remote cluster
	helm upgrade -i external-dns external-dns/external-dns \
	    -n kube-system \
	    -f manifests/remote/values/external-dns-values.yaml
	./scripts/create-do-secret.sh

install-remote-monitoring: ## Install Prometheus and Grafana to remote cluster
	helm upgrade -i kube-prometheus-stack prometheus-community/kube-prometheus-stack \
	    -n monitoring --create-namespace \
	    -f manifests/remote/values/kube-prometheus-stack-values.yaml

install-remote-sloth: ## Install Sloth SLO operator to remote cluster
	helm template sloth sloth/sloth \
	    -n monitoring \
	    --include-crds | kubectl apply -f -

deploy-local: ## Deploy the service to the local cluster
	kubectl apply -f manifests/local

deploy: ## Deploy the service to the remote cluster
	kubectl apply -f manifests/remote

# Misc

run-docker: ## Run the service in docker (port 8080 mapped to local)
	docker run --rm -p 8080:8080 interview-svc

grafana-admin-pass: ## Get the admin password for grafana
	kubectl -n monitoring get secrets kube-prometheus-stack-grafana -o jsonpath="{.data.admin-password}" | base64 -d ; echo

# Top-level convenience targets

up: ## Create local cluster, build image, deploy manifests
up: cluster-up build deploy-local

down: ## Delete local cluster, delete local image
down: cluster-down delete-image


.PHONY: help delete-image down run-docker deploy push build cluster-down cluster-up

help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
