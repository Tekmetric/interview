# Variables
IMAGE ?= backend-service:dev
JMX_IMAGE ?= public.ecr.aws/u0n6l1t0/system/bitnami/jmx-exporter:0.17.2-debian-11-r29
CLUSTER ?= dev-cluster
NAMESPACE ?= backend
RELEASE ?= backend
CHART_PATH ?= platform/charts/backend
SERVICE_PORT ?= 8080

# Internal
KIND := kind
KUBECTL := kubectl
HELM := helm

.PHONY: all build cluster load install install-jmx verify port-forward test test-metrics cleanup uninstall delete-cluster redeploy

all: build cluster load install-jmx verify test test-metrics cleanup

build:
	cd backend && docker build -t $(IMAGE) .

cluster:
	$(KIND) get clusters | grep -q "^$(CLUSTER)$$" || $(KIND) create cluster --name $(CLUSTER) --wait 60s

load:
	$(KIND) load docker-image $(IMAGE) --name $(CLUSTER)
	docker pull $(JMX_IMAGE)
	$(KIND) load docker-image $(JMX_IMAGE) --name $(CLUSTER)

install:
	$(HELM) upgrade --install $(RELEASE) $(CHART_PATH) \
	  --namespace $(NAMESPACE) --create-namespace \
	  --set image.repository=$(word 1,$(subst :, ,$(IMAGE))) \
	  --set image.tag=$(word 2,$(subst :, ,$(IMAGE))) \
	  --set service.type=ClusterIP

install-jmx:
	$(HELM) upgrade --install $(RELEASE) $(CHART_PATH) \
	  --namespace $(NAMESPACE) --create-namespace \
	  --set image.repository=$(word 1,$(subst :, ,$(IMAGE))) \
	  --set image.tag=$(word 2,$(subst :, ,$(IMAGE))) \
	  --set service.type=ClusterIP \
	  --set jmxExporter.enabled=true \
	  --set jmxExporter.image=$(JMX_IMAGE) \
	  --set metrics.enabled=true \
	  --set metrics.port=9404 \
	  --set metrics.path=/metrics

verify:
	$(KUBECTL) -n $(NAMESPACE) rollout status deploy/$(RELEASE)
	$(KUBECTL) -n $(NAMESPACE) get deploy,po,svc -o wide | cat

port-forward:
	# Runs in foreground. Use another terminal to curl.
	$(KUBECTL) -n $(NAMESPACE) port-forward svc/$(RELEASE) 8080:$(SERVICE_PORT)

# Simple functional test via port-forward
# Requires a separate shell or GNU make -j to parallelize with port-forward
.test-curl:
	curl -fsS http://localhost:8080/api/welcome | grep -i "Welcome"

test: ## Port-forward in background, test, and kill port-forward
	-$(KUBECTL) -n $(NAMESPACE) port-forward svc/$(RELEASE) 8080:$(SERVICE_PORT) >/tmp/pf.$(RELEASE).log 2>&1 & echo $$! > /tmp/pf.$(RELEASE).pid; \
	sleep 2; \
	$(MAKE) --no-print-directory .test-curl; \
	kill `cat /tmp/pf.$(RELEASE).pid` || true; \
	rm -f /tmp/pf.$(RELEASE).pid /tmp/pf.$(RELEASE).log

# Verify JMX exporter metrics endpoint from sidecar
.test-metrics-curl:
	curl -fsS http://localhost:9404/metrics | head -n 20 | cat

test-metrics:
	POD=$$($(KUBECTL) -n $(NAMESPACE) get pod -l app.kubernetes.io/name=$(RELEASE) -o jsonpath='{.items[0].metadata.name}'); \
	$(KUBECTL) -n $(NAMESPACE) port-forward $$POD 9404:9404 >/tmp/pfmetrics.$(RELEASE).log 2>&1 & echo $$! > /tmp/pfmetrics.$(RELEASE).pid; \
	sleep 2; \
	$(MAKE) --no-print-directory .test-metrics-curl; \
	kill `cat /tmp/pfmetrics.$(RELEASE).pid` || true; \
	rm -f /tmp/pfmetrics.$(RELEASE).pid /tmp/pfmetrics.$(RELEASE).log

# Ensure cluster exists for redeploy, then do full install with JMX
redeploy: build cluster load install-jmx verify

uninstall:
	-$(HELM) -n $(NAMESPACE) uninstall $(RELEASE) || true

delete-cluster:
	-$(KIND) delete cluster --name $(CLUSTER) || true

cleanup: uninstall delete-cluster
