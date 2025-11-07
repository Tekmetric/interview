# Only run on Mac OS
UNAME:= $(shell uname)
ifneq ($(UNAME),Darwin)
$(error ERROR: This Makefile must be run on Mac OS)
endif

export DOCKER_DEFAULT_PLATFORM=linux/amd64

APP:= backend
VERSION:= 0.0.1

.PHONY: all
all: deps deploy links

.PHONY: build
build: ## Build image then upload to cluster
	@echo ""
	@echo "-----------"
	@echo "Build Image"
	@echo "-----------"
	@docker build ../$(APP)/ -t $(APP):$(VERSION)
	@k3d image import $(APP):$(VERSION) --cluster $(APP)

.PHONY: clean
clean: ## Delete k3d cluster
	@echo "---------------------------"
	@echo "k3d: Delete $(APP) cluster"
	@echo "---------------------------"
	@k3d cluster delete $(APP)

.PHONY: deploy
deploy: deploy-k3d deploy-prometheus deploy-app ## Deploy infrastructure and applications

.PHONY: deploy-app
deploy-app: build
	@echo ""
	@echo "-------------------"
	@echo "Application: Deploy"
	@echo "-------------------"
	@if $$(helm list -n $(APP) | grep $(APP) &>/dev/null); then \
		echo "INFO: Application already deployed!"; \
		helm list -n $(APP) | grep $(APP); \
	else \
		helm upgrade $(APP) charts/app \
			--create-namespace \
			--install \
			--namespace $(APP) \
			--values ../$(APP)/values.yaml; \
		kubectl rollout status -n $(APP) deployment/$(APP) || echo "ERROR: The deployment of $(APP) has FAILED"; \
	fi

.PHONY: deploy-k3d
deploy-k3d:
	@echo ""
	@echo "---------------------------"
	@echo "k3d: Create $(APP) Cluster"
	@echo "---------------------------"
	@if $$(k3d cluster list $(APP) &>/dev/null); then \
		echo "INFO: The $(APP) cluster already exists!"; \
		k3d cluster list $(APP); \
	else \
		k3d cluster create $(APP) \
			-p "8080:8081@loadbalancer" \
			-p "8000:8000@loadbalancer" \
			-p "9090:9090@loadbalancer"; \
	fi

.PHONY: deploy-prometheus
deploy-prometheus:
	@echo ""
	@echo "------------------"
	@echo "Prometheus: Deploy"
	@echo "------------------"
	@if ! $$(helm repo list | grep prometheus-community &>/dev/null); then \
		helm repo add prometheus-community https://prometheus-community.github.io/helm-charts; \
		helm repo update; \
	fi
	@if $$(helm list -n prometheus-system | grep prometheus &>/dev/null); then \
		echo "INFO: Prometheus already deployed!"; \
		helm list -n prometheus-system | grep prometheus; \
	else \
		helm upgrade prometheus prometheus-community/kube-prometheus-stack \
			--create-namespace \
			--install \
			--namespace prometheus-system \
			--values prometheus-values.yaml; \
	fi

.PHONY: deps
deps: deps-banner deps-homebrew deps-docker deps-helm deps-k3d deps-kubectl ## Install required dependencies

.PHONY:deps-banner
deps-banner:
	@echo "----------------"
	@echo "Dependency Check"
	@echo "----------------"

.PHONY: deps-docker
deps-docker:
	@if ! command -v docker&>/dev/null; then \
		brew install --cask docker &>/dev/null; \
	fi
	@echo "- DOCKER: $$(docker version --format '{{.Client.Version}}')"

.PHONY: deps-helm
deps-helm:
	@if ! command -v helm &>/dev/null; then \
		brew install helm 1>/dev/null; \
	fi
	@echo "- HELM: $$(helm version --template='{{.Version}}')"

.PHONY: deps-homebrew
deps-homebrew:
	@if ! command -v brew &>/dev/null; then \
		/bin/bash -c "$$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)" &>/dev/null; \
	fi
	@echo "- HOMEBREW: $$(brew --version | awk '{print $$NF}')"

.PHONY: deps-k3d
deps-k3d:
	@if ! command -v k3d &>/dev/null; then \
		brew install k3d &>/dev/null; \
	fi
	@echo "- K3D: $$(k3d version | grep 'k3d' | awk '{print $$NF}')"

.PHONY: deps-kubectl
deps-kubectl:
	@if ! command -v kubectl &>/dev/null; then \
		brew install kubectl &>/dev/null; \
	fi
	@echo "- KUBECTL: $$(kubectl version --client | grep 'Client' | awk '{print $$NF}')"

.PHONY: help
help: ## Show help for each of the Makefile recipes.
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

.PHONY: links
links: ## List URLs for installed applications
	@echo ""
	@echo "-----------------"
	@echo "Application Links"
	@echo "-----------------"
	@echo "- APP[$(APP)] -- http://localhost:8080/api/welcome"
	@echo "- GRAFANA -- http://localhost:8000/login"
	@echo "- PROMETHEUS -- http://localhost:9090/"
	@echo ""
	@kubectl --namespace prometheus-system get secrets prometheus-grafana -o jsonpath="{.data.admin-password}" | base64 -d | pbcopy
	@echo "INFO: The password for Grafana's admin user has been copied to your clipboard"
