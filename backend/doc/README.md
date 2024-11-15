## Prerequisites

You need to have:
- GNU `make` command
- recent `docker` version with `buildx` cli plugin.
- `kubectl` for k8s cluster interaction.
- [k3d](https://k3d.io/v5.7.4/#installation) for testing deployments in local environment
- [helmfile/vals](https://github.com/helmfile/vals/releases) for populating secrets in k8s manifests
- `curl` to check that the backend api is working

## Building docker image
export `REPO` and `IMAGE` env vars (optionaly). `VERSION` - if not specified it will be populated from git tag, or short sha of the HEAD commit as a failback.

- To build and push multiarch docker image to the specified registry, run `make image-build-push`
- To build docker image for local architecture, run `make image-local`
- To build .jar using docker (without any java sdk or maven installed on the system) run `VERSION=1.0-alpha1 make dist` and check content of the `./dist` directory

## Deployment in local env using k3d
- To bootstrap local k3d cluster with 3 nodes and 1 control plane node run `make k3d-bootstrap`
- To destroy k3d cluster run `make k3d-destroy`
- To interact with the cluster run `make k3d-kubeconfig` for instructions.
- To test KUBECONFIG is correct try running `make k3d-cluster-info`
- To generate k8s manifests to stdout without secrets interpolation run `make deploy-dry-run`
- To deploy app to k3d cluster run `make deploy` - it will trigger image build, image load to k3d cluster, run `kcl` to produce k8s manifests, pipe that to `vals` to interpolate secrets and finally pipe the result to `kubectl apply`
- To check backend api is reachable on k3d cluster run `make backend-api-test` - it will use curl to fetch `/api/welcome` and display the response to stdout.
