export VERSION ?= $(shell git describe --always)
export GIT_SHA ?= $(shell git rev-parse HEAD)
export GIT_SHORTSHA ?= $(shell git rev-parse --short HEAD)
export GIT_BRANCH ?= $(shell git branch --show-current)
export K3D_CLUSTER ?= tekmetric-interview
export KCL_VERSION ?= 0.10.9
export REPO ?= tekmetric
export IMAGE ?= interview-backend

.PHONY: image-dry-run image-build image-build-push image-local dist k3d-export-kubeconfig k3d-cluster-info

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

k3d-bootstrap:
	k3d cluster create "${K3D_CLUSTER}" --config k3d-default.yaml
	touch k3d-bootstrap

k3d-kubeconfig:
	@echo "use: export KUBECONFIG=`k3d kubeconfig write tekmetric-interview`"

k3d-cluster-info:
	KUBECONFIG=`k3d kubeconfig write ${K3D_CLUSTER}` kubectl cluster-info

k3d-destroy:
	k3d cluster delete "${K3D_CLUSTER}"
	rm -f k3d-bootstrap

k3d-image-load: k3d-bootstrap image-local
	k3d image load -c ${K3D_CLUSTER} ${REPO}/${IMAGE}:${VERSION}

deploy: k3d-image-load
	docker run --rm -it  -v ${PWD}/../platform/kcl:/work -w /work kcllang/kcl:v${KCL_VERSION} \
		kcl run -D tag=${VERSION} \
			apps/interview-backend/base/base.k \
			apps/interview-backend/prod/main.k \
			pkg/render-k8s.k | vals eval -f - | KUBECONFIG=`k3d kubeconfig write ${K3D_CLUSTER}` kubectl apply -f -

deploy-dry-run:
	docker run --rm -it  -v ${PWD}/../platform/kcl:/work -w /work kcllang/kcl:v${KCL_VERSION} \
		kcl run -D tag="${VERSION}" \
		apps/interview-backend/base/base.k  \
		apps/interview-backend/prod/main.k  \
		pkg/render-k8s.k

backend-api-test:
	curl --resolve interview.tekmetric.com:8888:127.0.0.1 -XGET http://interview.tekmetric.com:8888/api/welcome
