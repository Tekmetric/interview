<!-- # Platform Engineering Interview Guidelines -->
<!---->
<!-- Welcome to the Platform Engineer (Site Reliability Engineer) take-home coding challenge!  -->
<!---->
<!-- This project is designed to evaluate your skills in productionizing applications, managing container orchestration, and ensuring the reliability and observability of services in a Kubernetes environment. -->
<!---->
<!-- ## Overview: -->
<!---->
<!-- In this repository, we have included two distinct projects: -->
<!-- - [Backend Service](../backend/README.md) - Backend Application (BE) -->
<!-- - [Frontend Application](../frontend/README.md) - Frontend Application (FE) -->
<!---->
<!-- Your task is to choose either one of these projects and productionize them. -->
<!---->
<!-- The goal is to create a containerized deployment strategy using Docker and deploy the application(s) on Kubernetes (K8s) with Helm charts or other K8s resources. -->
<!---->
<!-- This assignment will give us insight into your technical expertise, problem-solving skills, and ability to work with modern infrastructure tools. -->
<!---->
<!-- #### Fork the repository and clone it locally -->
<!-- - https://github.com/Tekmetric/interview.git -->
<!---->
<!-- #### Import project into IDE -->
<!-- - Project root is located in `platform` folder -->
<!---->
<!-- #### After finishing the goals listed below create a PR -->
<!---->
<!-- ### Goals -->
<!-- 1. Dockerization: Create Dockerfiles to containerize the chosen application(s). -->
<!-- Kubernetes Deployment: -->
<!-- 2. Develop Helm charts or standard Kubernetes manifests to deploy the application(s) on a K8s cluster. -->
<!-- 3. Application Enhancements: Ensure each application has appropriate health checks (e.g., liveness and readiness probes). -->
<!-- Add relevant metrics (e.g., Prometheus instrumentation) to monitor application performance. -->
<!-- 4. Documentation: Provide clear instructions on how to build and deploy the application(s), including any prerequisites. -->
<!---->
<!-- ### Considerations -->
<!-- This is an open-ended exercise for you to showcase what you know! -->
<!---->
<!-- ### Submitting your coding exercise -->
<!-- Once you have finished the coding exercise please create a PR into Tekmetric/interview -->
<!---->
<!-- ### Excited to see what you build! 🚀 -->

# Interview Backend Deployment

## Overview

This document outlines the process for building and deploying the Backend Application(BE) and provides additional context and insight for the process.

## Requirements

This implementation requires Mac OS to be run. Attempting to run it on another environment will result in an error. As part of the deployment process, the following packages will be verified -- if they do not exist, then they will be installed as part of the process:

- Docker
- Helm
- Homebrew
- k3d
- Kubectl

## Deployment

The deployment of the BE is orchestrated using `make`, run from this directory(`platform`). For more information about the available options, run `make help`:

```
build                Build image then export to cluster
clean                Delete k3d cluster
deploy               Deploy infrastructure and applications
deps                 Install required dependencies
help                 Show help for each of the Makefile recipes.
links                List URLs for installed applications
```

Any of the above tasks can be run directly by prefixing them with the `make` command, e.g.: `make build`. Running `make` directly will perform the following tasks:

- `deps` -- Verify dependencies listed in Requirements
    - For each dependency:
        - Check to see if the command is available
        - Install the dependency if it does not exist
        - Output the installed version
- `deploy` -- Deploy the infrastructure and desired applications
    - Infrastructure:
        - `k3d` -- Creates a new cluster(`backend`), if it doesn't exist, and set a load balancer with locally accessible ports
    - Applications:
        - `prometheus` & `grafana`
            - Add `prometheus-community` Helm Chart repo if it doesn't exist
            - Install `prometheus-community` Helm Chart if it doesn't exist
                - Uses `prometheus-values.yaml` file in `platform` directory
        - `backend`
            - `build` -- Builds `backend` image using `Dockerfile` in `backend` directory and uploads image to the `k3d` cluster
            - Install local `app` Helm Chart if it doesn't exist
                - Uses `values.yaml` file in `backend` directory
- `links` -- Print a list of URLs for the installed applications
    - **NOTE**: For `grafana` the username is `admin` and the password is copied to your clipboard.

## Cleanup

The `k3d` cluster can be completely removed by running `make clean`.
