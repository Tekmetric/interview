# Tekmetric SRE take-home. One-command local environment.
#
#   make up          Spin up k3d cluster, install Argo CD, sync everything.
#   make demo        Open Argo, Grafana, Vault, and the app in a browser.
#   make down        Tear down the cluster.
#
# Targets assume: docker, k3d, kubectl, helm installed. `make doctor`
# checks for the prereqs and prints hints if anything's missing.

SHELL := /bin/bash
.SHELLFLAGS := -eu -o pipefail -c
.ONESHELL:

CLUSTER       := sre-demo
ARGOCD_NS     := argocd
ARGOCD_VER    := v2.12.4
HOSTS_ENTRY   := 127.0.0.1 app.sre-demo.local argocd.sre-demo.local grafana.sre-demo.local vault.sre-demo.local prometheus.sre-demo.local alertmanager.sre-demo.local
ROOT_APP      := gitops/bootstrap/root-app.yaml
CHART_DIR     := chart

# Colors for output legibility in terminal.
C_GREEN := \033[0;32m
C_YELL  := \033[0;33m
C_RED   := \033[0;31m
C_OFF   := \033[0m

.PHONY: help
help: ## Show this help.
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  $(C_GREEN)%-22s$(C_OFF) %s\n", $$1, $$2}' $(MAKEFILE_LIST)

.PHONY: doctor
doctor: ## Check for required CLIs (docker, k3d, kubectl, helm).
	@for bin in docker k3d kubectl helm; do \
	  if ! command -v $$bin >/dev/null 2>&1; then \
	    echo -e "$(C_RED)missing:$(C_OFF) $$bin"; exit 1; \
	  else \
	    printf "  ok   %-8s %s\n" "$$bin" "$$($$bin version --short 2>/dev/null | head -1 || $$bin --version 2>/dev/null | head -1)"; \
	  fi; \
	done

.PHONY: hosts
hosts: ## Ensure /etc/hosts has the demo entries (requires sudo).
	@if ! grep -q "sre-demo.local" /etc/hosts; then \
	  echo -e "$(C_YELL)Adding hosts entry (needs sudo)...$(C_OFF)"; \
	  echo "$(HOSTS_ENTRY)" | sudo tee -a /etc/hosts > /dev/null; \
	else \
	  echo "hosts entry already present"; \
	fi

.PHONY: cluster
cluster: doctor ## Create the k3d cluster (Traefik + servicelb disabled).
	@if k3d cluster list | grep -q "^$(CLUSTER) "; then \
	  echo "cluster already exists"; \
	else \
	  k3d cluster create $(CLUSTER) \
	    --agents 2 \
	    --k3s-arg "--disable=traefik@server:0" \
	    --k3s-arg "--disable=servicelb@server:0" \
	    --port "80:80@loadbalancer" \
	    --port "443:443@loadbalancer" \
	    --wait; \
	fi

.PHONY: argocd
argocd: ## Install Argo CD into the cluster.
	@kubectl create namespace $(ARGOCD_NS) --dry-run=client -o yaml | kubectl apply -f -
	kubectl apply -n $(ARGOCD_NS) -f https://raw.githubusercontent.com/argoproj/argo-cd/$(ARGOCD_VER)/manifests/install.yaml
	@echo "waiting for Argo CD to be ready..."
	kubectl rollout status -n $(ARGOCD_NS) deploy/argocd-server --timeout=5m
	kubectl rollout status -n $(ARGOCD_NS) deploy/argocd-repo-server --timeout=5m
	kubectl rollout status -n $(ARGOCD_NS) statefulset/argocd-application-controller --timeout=5m || true
	# Argo CD TLS terminates at ingress-nginx, so server runs insecure.
	kubectl patch configmap argocd-cmd-params-cm -n $(ARGOCD_NS) --type merge \
	  -p '{"data":{"server.insecure":"true"}}'
	kubectl rollout restart deploy/argocd-server -n $(ARGOCD_NS)
	kubectl rollout status  deploy/argocd-server -n $(ARGOCD_NS) --timeout=3m

.PHONY: argocd-ingress
argocd-ingress: ## Ingress for the Argo CD UI (TLS via cert-manager).
	@kubectl apply -f scripts/argocd-ingress.yaml

.PHONY: root-app
root-app: ## Apply the root "app of apps" that manages everything else.
	kubectl apply -f $(ROOT_APP)

.PHONY: vault-init
vault-init: ## Write demo secrets to Vault and create ESO's auth token Secret.
	@bash scripts/vault-init.sh

.PHONY: up
up: hosts cluster argocd argocd-ingress root-app ## Full local bring-up.
	@echo -e "$(C_GREEN)cluster up.$(C_OFF) Argo is syncing. Run 'make status' to watch, or 'make demo' to open URLs."
	@$(MAKE) -s status

.PHONY: status
status: ## Show Argo CD application status.
	@kubectl get applications -n $(ARGOCD_NS) -o custom-columns='NAME:.metadata.name,SYNC:.status.sync.status,HEALTH:.status.health.status,MESSAGE:.status.operationState.message' || true

.PHONY: demo
demo: ## Open all the demo URLs in a browser.
	@echo -e "$(C_GREEN)Argo CD:$(C_OFF)      https://argocd.sre-demo.local"
	@echo -e "$(C_GREEN)Grafana:$(C_OFF)      https://grafana.sre-demo.local  (admin / admin)"
	@echo -e "$(C_GREEN)Prometheus:$(C_OFF)   https://prometheus.sre-demo.local"
	@echo -e "$(C_GREEN)Alertmanager:$(C_OFF) https://alertmanager.sre-demo.local"
	@echo -e "$(C_GREEN)Vault:$(C_OFF)        https://vault.sre-demo.local    (token: root)"
	@echo -e "$(C_GREEN)App:$(C_OFF)          https://app.sre-demo.local/api/welcome"
	@echo ""
	@echo -e "$(C_GREEN)Argo initial admin password:$(C_OFF)"
	@kubectl -n $(ARGOCD_NS) get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" 2>/dev/null | base64 -d || true
	@echo ""

.PHONY: lint
lint: ## helm lint + kubeconform on rendered manifests.
	helm lint $(CHART_DIR) --strict
	helm template tekmetric-backend $(CHART_DIR) \
	  | kubeconform -strict -ignore-missing-schemas -

.PHONY: test
test: ## Run helm-unittest specs.
	@if ! helm plugin list | grep -q unittest; then \
	  helm plugin install https://github.com/helm-unittest/helm-unittest --version 0.5.2; \
	fi
	helm unittest $(CHART_DIR)

.PHONY: template
template: ## Render the chart to stdout for inspection.
	helm template tekmetric-backend $(CHART_DIR)

.PHONY: down
down: ## Delete the k3d cluster.
	k3d cluster delete $(CLUSTER)

.PHONY: trust-ca
trust-ca: ## Add the demo CA to the macOS keychain (kills browser warnings).
	@bash scripts/trust-ca.sh
