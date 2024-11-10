## Prerequisites

You need to have GNU `make` command available at your build/deploy machine. You will also need recent `docker` version with `buildx` client plugin.

## Building docker image
export `REPO` and `IMAGE` env vars (mandatory). Rest are optional, like `VERSION` - if not specified it will be populated from git tag, or short sha of the HEAD commit as a failback.

To build and push multiarch docker image to the specified registry, run `REPO=tekmetric IMAGE=interview-backend make image-build-push`
To build docker image for local architecture, run `REPO=tekmetric IMAGE=interview-backend make image-local`
To build .jar using docker (without any java sdk or maven installed on the system) run `VERSION=1.0-alpha1 make dist` and check content of the `./dist` directory
