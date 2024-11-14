## Prerequisites

You need to have:
- GNU `make` command
- recent `docker` version with `buildx` cli plugin.
- `kubectl` for k8s cluster interaction.
- `k3d` for testing deployments in local environment (optional)

## Building docker image
export `REPO` and `IMAGE` env vars (optionaly). `VERSION` - if not specified it will be populated from git tag, or short sha of the HEAD commit as a failback.

To build and push multiarch docker image to the specified registry, run `make image-build-push`
To build docker image for local architecture, run `make image-local`
To build .jar using docker (without any java sdk or maven installed on the system) run `VERSION=1.0-alpha1 make dist` and check content of the `./dist` directory
