export VERSION ?= $(shell git describe --always)
export GIT_SHA ?= $(shell git rev-parse HEAD)
export GIT_SHORTSHA ?= $(shell git rev-parse --short HEAD)
export GIT_BRANCH ?= $(shell git branch --show-current)

.PHONY: image-dry-run image-build image-build-push image-local dist

default: dist

image-dry-run:
	docker buildx bake --print

image-build:
	docker buildx bake

image-build-push:
	docker buildx bake --push

image-local:
	docker buildx bake local --load

dist:
	docker buildx bake dist

dist-clean:
	@rm -rf ./dist/*.jar
