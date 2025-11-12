build: ## Build the docker image
	docker build -f Dockerfile ./../backend -t interview-svc

run: ## Run the service in docker (port 8080 mapped to local)
	docker run --rm -p 8080:8080 interview-svc

.PHONY: help

help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
