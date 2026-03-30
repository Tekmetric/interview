# Backend Build and Deployment

## Prerequisites

- Docker is installed and configured.
- Helm is installed.
- Kubectl is installed.
- You have valid AWS credentials configured (e.g., via `aws configure` or environment variables).
- You have access to the EKS cluster, and `kubectl` is configured to communicate with it (e.g., via `aws eks update-kubeconfig`).

## Build Docker Image

```bash
docker build --platform linux/amd64 \
    -t ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/backend:${IMAGE_TAG} .
```

## Deploy with Helm

```bash
helm upgrade --install backend ./charts/backend \
  --namespace backend \
  --create-namespace \
  --set image.repository=${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/backend \
  --set image.tag=${IMAGE_TAG} \
  --wait --timeout 5m \
  --atomic
```

## Post-deployment Validation

Forward a local port to the application:
```bash
kubectl port-forward deployment/backend 8080:8080 -n backend
```
In another terminal, test the endpoint:
```bash
curl http://127.0.0.1:8080/api/welcome
```
You should receive a successful response. If not, check the pod logs:
```bash
kubectl logs -n backend deployment/backend
```