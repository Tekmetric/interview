IMAGE_NAME = ghcr.io/gregops312/interview
IMAGE_TAG = $(shell git rev-parse --short HEAD)
GITHUB_ACTOR = gregops312

##
## Backend
##
.PHONY: backend-build
backend-build:
	cd backend && \
	docker build -t $(IMAGE_NAME):latest .

.PHONY: backend-deploy
backend-deploy: #backend-publish
	kubectl apply -f backend/backend.yaml

.PHONY: backend-publish
backend-publish: backend-build #ghcr-login
	docker push $(IMAGE_NAME):latest

.PHONY: backend-run
backend-run: backend-build
	docker run \
		-it \
		--rm \
		--name interview-backend \
		-p 8080:8080 \
		$(IMAGE_NAME):latest

# .PHONY: ghcr-login
# ghcr-login:
# 	echo $$GH_TOKEN | docker login ghcr.io -u $$GITHUB_ACTOR --password

##
## Clean
##
.PHONY: clean
clean:
	kubectl delete -f backend/backend.yaml || true && \
	kubectl delete -f k8s/monitoring.yaml || true

##
## Setup Monitoring
##
.PHONY: setup-monitoring
setup-monitoring:
	kubectl apply -f k8s/monitoring.yaml
