# Site Reliability Engineering Interview Guidelines

Welcome to the Site Reliability Engineer take-home coding challenge! 

This project is designed to evaluate your skills in `productionizing applications`, `managing container orchestration`, and ensuring the `reliability` and `observability` of services in a `Kubernetes production environment`.

## Overview:

Your goal is to create a containerized deployment strategy using Docker and deploy the application(s) on Kubernetes (K8s) with Helm charts.

Make sure you to include a minimal CI/CD pipeline to help you out.

The application to be containerized is bundled in this repository:
- [Backend Service](../backend/README.md) - Backend Application (BE)

This assignment will give us insight into your technical expertise, problem-solving skills, and ability to work with modern infrastructure tools.

#### Fork the repository and clone it locally
- https://github.com/Tekmetric/interview.git

#### Import project into IDE
- Project root is located in `sre` folder

#### After finishing the goals listed below create a PR

- if you've forked the repository, create a PR from your fork to `Tekmetric/interview` repository
# Goals
1. Create a Dockerfile to containerize the application.
2. Create a minimal CI/CD pipeline to demonstrate building and pushing Docker images to a container registry (e.g., Docker Hub, AWS ECR, etc.).
3. Develop a Helm chart to deploy the application on a K8s or K3s cluster.
   1. Make sure that whatever you implement in the Helm chart is `as close as possible to production-ready`.
4. Add relevant observability
5. Provide clear instructions on how to build and deploy the application, including any prerequisites.

# Considerations
This is an open-ended exercise for you to showcase what you know!

### Submitting your coding exercise
Once you have finished the coding exercise please create a PR into Tekmetric/interview

### Excited to see what you build! 🚀

For the interview, make sure you have the following ready:
- An IDE with your submission code
- A running K8s or K3s cluster (local or cloud-based) to demonstrate your Helm chart deployment
- Make sure you can explain your design choices and answer any questions related to the exercise.
- You will have only 10 minutes to showcase your submission during the interview, so please be concise and focus on the key aspects of your implementation.

---

# Deployment Instructions

## Build

```bash
podman build -t ghcr.io/hoppr-gg/interview/backend:latest -f Dockerfile .
```

Run locally to test:
```bash
podman run -p 8080:8080 ghcr.io/hoppr-gg/interview/backend:latest
```

## Deploy

Apply the ArgoCD application:
```bash
kubectl apply -f argocd-application.yaml
```

ArgoCD will sync the Helm chart from `charts/backend/` and deploy to the `tekmetric` namespace.

To deploy manually with Helm:
```bash
helm upgrade --install backend charts/backend -n tekmetric --create-namespace
```

## Validate

Check pods are running:
```bash
kubectl get pods -n tekmetric -l app=backend
```

Health check:
```bash
kubectl exec -n tekmetric deploy/backend -- curl -s localhost:8080/actuator/health
```

Welcome endpoint:
```bash
kubectl exec -n tekmetric deploy/backend -- curl -s localhost:8080/api/welcome
```

Prometheus metrics:
```bash
kubectl exec -n tekmetric deploy/backend -- curl -s localhost:8080/actuator/prometheus | head
```
