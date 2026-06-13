# Solution - David Schwartz

# Implementation Notes
- Backend containerization lives in `../backend/Dockerfile`.
- CI/CD pipeline is in `../.github/workflows/ci.yml` and pushes to Docker Hub.
- Helm chart is located at `./charts/backend`.
- Observability is provided via application logs to stdout and a health endpoint at `/actuator/health`.

# Prerequisites (Local Kubernetes via Docker Desktop)
- Docker Desktop with Kubernetes enabled
- NGINX Ingress Controller enabled (Docker Desktop: Settings -> Kubernetes -> Enable Kubernetes, then enable the built-in ingress or install NGINX ingress)
- kubectl
- Helm 3
- Docker Hub account (for pushing/pulling images)

# Build and Run (Local)
## Option A: Build locally and deploy (no registry push needed for Docker Desktop)
- From repository root:
- `docker build -t interview-backend:local ./backend`
- `helm install interview-backend ./sre/charts/backend --set image.repository=interview-backend --set image.tag=local`
- Add a hosts entry: `127.0.0.1 interview-backend.local`
- `curl http://interview-backend.local/api/welcome`
- `curl http://interview-backend.local/actuator/health`

## Option B: Push to Docker Hub and deploy
- `docker login`
- `docker build -t <dockerhub-username>/interview-backend:dev ./backend`
- `docker push <dockerhub-username>/interview-backend:dev`
- `helm install interview-backend ./sre/charts/backend --set image.repository=<dockerhub-username>/interview-backend --set image.tag=dev`
- Add a hosts entry: `127.0.0.1 interview-backend.local`
- `curl http://interview-backend.local/api/welcome`
- `curl http://interview-backend.local/actuator/health`

# CI/CD (GitHub Actions & Docker Hub)
- Set repository secrets:
    - `DOCKERHUB_USERNAME`
    - `DOCKERHUB_TOKEN`
- Prior to merging a PR a successful build is required via `build-and-push`
    - No DockerHub publish
- On push to `master`, the pipeline builds and pushes tags:
    - `sha-<git-sha>`
    - `latest`

