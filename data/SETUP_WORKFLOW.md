# Local development

## Requirements

- docker
- [minikube](https://minikube.sigs.k8s.io/docs/start): A tool for running Kubernetes locally.
- [ctlptl](https://github.com/tilt-dev/ctlptl) (optional): A utility for setting up and managing a local Kubernetes
  cluster.
- [kubectl](https://kubernetes.io/docs/reference/kubectl/): The command-line tool for interacting with your Kubernetes
  cluster.
- [Tilt](https://docs.tilt.dev/install.html): A development tool designed to simplify and speed up the local k8s
  development
- [Argo CLI](https://github.com/argoproj/argo-workflows/releases/): The command-line interface for Argo Workflows
- [mc client](https://min.io/docs/minio/linux/reference/minio-mc.html) (optional):  Provides an alternative to UNIX
  commands like ls, cat, cp, mirror, and diff with support for both filesystems and Amazon S3-compatible cloud storage
  services.

## Environment setup

```
ctlptl create cluster minikube --registry=ctlptl-registry (one time only)
```

## Start k8s cluster and Argo Workflows
```
docker stop ctlptl-registry
minikube start
docker start ctlptl-registry

cd solution-argo-workflows
tilt up # this will install argo workflows in the minikube cluster and expose the ports for the Argo UI and MinIO

# Check Argo Workflows UI at https://localhost:2746/
```

## Run project

```
# Submit workflow (can be done via UI also)
cd solution-argo-workflow
argo submit -n argo neo-workflow.yaml -p api_key=DEMO_KEY -p end_page=2
argo submit -n argo neo-workflow.yaml -p api_key=DEMO_KEY -p end_page=9

# check workflow run in the UI (https://localhost:2746/) and wait completion
```

## Verify bucket contents (optional)

```
wget https://dl.min.io/client/mc/release/linux-amd64/mc && chmod +x mc

./mc alias set minio http://localhost:9000 admin password

./mc ls minio/my-bucket/metrics/
# [2025-02-26 11:09:06 EET]     5B STANDARD near_misses_count
# [2025-02-26 11:08:56 EET]  13KiB STANDARD yearly_approaches.json

./mc cat  minio/my-bucket/metrics/near_misses_count
# 1061
```

Resulted outputs can also be viewed in the Workflow run UI directly.
