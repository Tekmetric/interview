# Frontend Application - Deployment Guide

## Prerequisites:
- **Node.js** (version 16 or higher)
- **Yarn** (package manager)
- **Docker** (for containerization)
- **Minikube** (for local Kubernetes)
- **kubectl** (Kubernetes CLI)

---

## Running Frontend Application:
## 1. Run the App Locally (Development Mode)

1. **Clone the repository** and navigate to the frontend folder:

   ```bash
   git clone https://github.com/Tekmetric/interview.git
   cd interview/frontend

2. **Install dependencies:** 
    ```bash
    yarn install

3. **Start the development server:** 
    ```bash
    yarn start

4. **Open in your browser:** 
    http://localhost:3000


## 2. Run the App Using Docker (Local Container)
1. **Clone the repository** and navigate to the frontend folder:

   ```bash
   git clone https://github.com/Tekmetric/interview.git
   cd interview/frontend

2. **Build the Docker image:** 
    ```bash
    docker build -t interview-frontend:latest .

3. **Run the container:** 
    ```bash
    docker run -p 8080:80 interview-frontend:latest

4. **Access the app:** 
    http://localhost:8080

## 3. Deploy the App to Minikube (Local Kubernetes)
1. **Clone the repository:** and navigate to the frontend folder:

   ```bash
   git clone https://github.com/Tekmetric/interview.git
   cd interview/frontend

2. **Start Minkube:** 
    ```bash
    minikube start

3. **Build the image inside Minikube’s Docker environment:** 
    ```bash
    eval $(minikube docker-env)
    docker build -t interview-frontend:latest .

4. **Apply the Kubernetes manifests:** (deployment && service)
    ```bash
    kubectl apply -f minikube-k8s-manifests.yaml

5. **Access the application:** 
    ```bash
    minikube service interview-frontend-service --url
    example: http://127.0.0.1:59444


## 4. Run the App on Productions AWS-EKS (Production Mode)

### Prerequisites: (assuming app to be exposed to internal network only)
- **AWS EKS Cluster**: is installed, configured (at least two worker nodes in two AZs for HA) and cluster auto scalling is enabled
- **Service Mesh**: e.g istio is installed and configured with at least one gateway (internal)
- **AWS ALB**: ALB is created, porta 443 (terminate ssl at loadbalancer), cert/ssl is setup, which points to worker nodes
- **AWS Route53**: DNS cname/record is created and pointing to above ALB (frontend.internal.tekmetrics.com)
- **ArgoCD/GitOps**: ArgoCD is installed and configured with target EKS cluster, which maintains k8s manifests (depolyment, service, virtualService, HPA, serviceAccount)


### very simple basic CI/CD micro service repo:  
- developer creates feature branch
- developer change/updates code
- developer commits code
- pipeline auto-trigger to scan, run testcases, build and push image to target AWS-ECR
- open PR to develop, intergation test for all feature, review, approve and merge
- create release from develop, tag image in AWS-ECR
- push image to higher envs

### ArgoCD:  
- developer creates feature branch
- developer update manifest
- developer commits k8s manifest
- open PR, review, approve and merge
- ArgoCD will auto sync manifest with EKS Cluster