#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MINIKUBE_VERSION="${MINIKUBE_VERSION:-latest}"
MINIKUBE_MEMORY="${MINIKUBE_MEMORY:-8192}"
HELM_VERSION="${HELM_VERSION:-v3.14.0}"
HELM_CHARTS_DIR="${SCRIPT_DIR}/helm"
BACKEND_DIR="${SCRIPT_DIR}/../backend"
BACKEND_IMAGE="interview-backend:latest"
ISTIO_NAMESPACE="istio-system"
GATEWAY_NAMESPACE="istio-ingress"
CERT_MANAGER_NAMESPACE="cert-manager"
ESO_NAMESPACE="eso"
OTEL_OPERATOR_NAMESPACE="opentelemetry-operator-system"
ARGOCD_NAMESPACE="argocd"
ARGO_ROLLOUTS_NAMESPACE="argo-rollouts"
OBSERVABILITY_NAMESPACE="observability-stack"
Git_REPO_URL="git@github.com:joetechholmes/interview.git"

info() {
  echo "[INFO] $*"
}

die() {
  echo "[ERROR] $*" >&2
  exit 1
}

command_exists() {
  command -v "$1" >/dev/null 2>&1
}

download_file() {
  local url="$1"
  local dest="$2"
  if command_exists curl; then
    curl -fsSL "$url" -o "$dest"
  elif command_exists wget; then
    wget -qO "$dest" "$url"
  else
    die "curl or wget is required to download files"
  fi
}

install_binary() {
  local url="$1"
  local dest="$2"
  local tmpfile
  tmpfile="$(mktemp)"
  download_file "$url" "$tmpfile"
  if [[ "$dest" != *".exe" ]]; then
    chmod +x "$tmpfile"
  fi
  if [[ "$EUID" -ne 0 ]] && command_exists sudo; then
    sudo install -m 0755 "$tmpfile" "$dest"
  else
    install -m 0755 "$tmpfile" "$dest"
  fi
  rm -f "$tmpfile"
  info "Installed $(basename "$dest") to $dest"
}

detect_platform() {
  local uname_out
  uname_out="$(uname -s)"
  case "$uname_out" in
    Linux*) os=linux ;;
    Darwin*) os=darwin ;;
    CYGWIN*|MINGW*|MSYS*|Windows_NT*) os=windows ;;
    *) die "Unsupported OS: $uname_out" ;;
  esac

  local arch_out
  arch_out="$(uname -m)"
  case "$arch_out" in
    x86_64|amd64) arch=amd64 ;;
    arm64|aarch64) arch=arm64 ;;
    *) die "Unsupported architecture: $arch_out" ;;
  esac

  if [[ "$os" == windows ]]; then
    ext=".exe"
  else
    ext=""
  fi
}

ensure_minikube() {
  if command_exists minikube; then
    info "Minikube already installed: $(minikube version | head -n1)"
    return
  fi

  detect_platform
  local minikube_url="https://storage.googleapis.com/minikube/releases/${MINIKUBE_VERSION}/minikube-${os}-${arch}${ext}"
  local install_path="/usr/local/bin/minikube${ext}"

  info "Installing minikube ${MINIKUBE_VERSION} for ${os}/${arch}"
  install_binary "$minikube_url" "$install_path"
}

ensure_helm() {
  if command_exists helm; then
    info "Helm already installed: $(helm version --short)"
    return
  fi

  detect_platform
  local helm_archive="helm-${HELM_VERSION}-${os}-${arch}.tar.gz"
  local helm_url="https://get.helm.sh/${helm_archive}"
  local tmpdir
  tmpdir="$(mktemp -d)"
  pushd "$tmpdir" >/dev/null
  download_file "$helm_url" "$helm_archive"
  tar -xzf "$helm_archive"
  install_binary "$tmpdir/${os}-${arch}/helm" "/usr/local/bin/helm${ext}"
  popd >/dev/null
  rm -rf "$tmpdir"
}

ensure_kubectl() {
  if command_exists kubectl; then
    info "kubectl already installed: $(kubectl version --client)"
    return
  fi

  detect_platform
  if [[ "$os" == windows ]]; then
    local kubectl_url="https://dl.k8s.io/release/stable.txt"
    local kubectl_version
    kubectl_version="$(curl -fsSL "$kubectl_url")"
    kubectl_url="https://dl.k8s.io/release/${kubectl_version}/bin/windows/${arch}/kubectl.exe"
    install_binary "$kubectl_url" "/usr/local/bin/kubectl.exe"
  else
    local kubectl_version
    kubectl_version="$(curl -fsSL https://dl.k8s.io/release/stable.txt)"
    local kubectl_url="https://dl.k8s.io/release/${kubectl_version}/bin/${os}/${arch}/kubectl"
    install_binary "$kubectl_url" "/usr/local/bin/kubectl"
  fi
}

start_minikube() {
  if command_exists minikube && minikube status >/dev/null 2>&1; then
    info "Minikube cluster is already running"
    return
  fi

  info "Starting minikube"
  minikube start --driver=docker --memory="${MINIKUBE_MEMORY}"
}

install_istio() {
  info "Installing Istio into namespace '${ISTIO_NAMESPACE}' via Helm chart"
  helm dependency update "${HELM_CHARTS_DIR}/istio-chart"
  helm upgrade --install istio "${HELM_CHARTS_DIR}/istio-chart" \
    --namespace "${ISTIO_NAMESPACE}" \
    --create-namespace \
    --wait \
    --timeout 10m
}

install_gateway() {
  info "Installing Istio ingress gateway into namespace '${GATEWAY_NAMESPACE}' via Helm chart"
  helm dependency update "${HELM_CHARTS_DIR}/gateway-chart"
  helm upgrade --install gateway "${HELM_CHARTS_DIR}/gateway-chart" \
    --namespace "${GATEWAY_NAMESPACE}" \
    --create-namespace \
    --wait \
    --timeout 5m
}

install_cert_manager() {
  info "Installing cert-manager into namespace '${CERT_MANAGER_NAMESPACE}' via Helm chart"
  helm dependency update "${HELM_CHARTS_DIR}/cert-manager-chart"
  helm upgrade --install cert-manager "${HELM_CHARTS_DIR}/cert-manager-chart" \
    --namespace "${CERT_MANAGER_NAMESPACE}" \
    --create-namespace \
    --wait \
    --timeout 10m
}

build_and_load_interview_backend() {
  command_exists docker || die "docker is required to build the interview-backend image"
  info "Building interview-backend Docker image: ${BACKEND_IMAGE}"
  docker build -t "${BACKEND_IMAGE}" "${BACKEND_DIR}"
  info "Loading interview-backend image into minikube"
  minikube image load "${BACKEND_IMAGE}"
}

install_argo_rollouts() {
  info "Installing Argo Rollouts into namespace '${ARGO_ROLLOUTS_NAMESPACE}' via Helm chart"
  helm dependency update "${HELM_CHARTS_DIR}/argo-rollouts-chart"
  helm upgrade --install argo-rollouts "${HELM_CHARTS_DIR}/argo-rollouts-chart" \
    --namespace "${ARGO_ROLLOUTS_NAMESPACE}" \
    --create-namespace \
    --wait \
    --timeout 10m
}

configure_argocd_repo() {
  local ssh_key_path="${HOME}/.ssh/id_ed25519"
  [[ -f "${ssh_key_path}" ]] || ssh_key_path="${HOME}/.ssh/id_rsa"
  [[ -f "${ssh_key_path}" ]] || die "No SSH key found at ~/.ssh/id_ed25519 or ~/.ssh/id_rsa — required for ArgoCD to clone the repo"

  info "Configuring ArgoCD repository credentials from ${ssh_key_path}"
  kubectl create secret generic argocd-repo-interview \
    --namespace "${ARGOCD_NAMESPACE}" \
    --from-literal=type=git \
    --from-literal=url=${Git_REPO_URL} \
    --from-file=sshPrivateKey="${ssh_key_path}" \
    --dry-run=client -o yaml \
    | kubectl apply -f -

  kubectl label secret argocd-repo-interview \
    --namespace "${ARGOCD_NAMESPACE}" \
    argocd.argoproj.io/secret-type=repository \
    --overwrite
}

install_argocd() {
  local git_branch
  git_branch="$(git -C "${SCRIPT_DIR}" rev-parse --abbrev-ref HEAD)"
  info "Installing Argo CD into namespace '${ARGOCD_NAMESPACE}' via Helm chart (targeting branch: ${git_branch})"
  helm dependency update "${HELM_CHARTS_DIR}/argocd-chart"
  helm upgrade --install argocd "${HELM_CHARTS_DIR}/argocd-chart" \
    --namespace "${ARGOCD_NAMESPACE}" \
    --create-namespace \
    --set "apps.interviewBackend.targetRevision=${git_branch}" \
    --wait \
    --timeout 10m
}

install_opentelemetry_operator() {
  info "Installing OpenTelemetry Operator into namespace '${OTEL_OPERATOR_NAMESPACE}' via Helm chart"
  helm dependency update "${HELM_CHARTS_DIR}/opentelemetry-operator-chart"
  helm upgrade --install opentelemetry-operator "${HELM_CHARTS_DIR}/opentelemetry-operator-chart" \
    --namespace "${OTEL_OPERATOR_NAMESPACE}" \
    --create-namespace \
    --skip-schema-validation \
    --wait \
    --timeout 10m
}

install_external_secrets_operator() {
  info "Installing External Secrets Operator into namespace '${ESO_NAMESPACE}' via Helm chart"
  helm dependency update "${HELM_CHARTS_DIR}/external-secrets-chart"
  helm upgrade --install external-secrets "${HELM_CHARTS_DIR}/external-secrets-chart" \
    --namespace "${ESO_NAMESPACE}" \
    --create-namespace \
    --wait \
    --timeout 10m
}

install_observability_stack() {
  info "Installing observability stack (Mimir, Loki, Tempo, Grafana, k8s-monitoring) via Helm chart"
  helm dependency update "${HELM_CHARTS_DIR}/observability-stack"
  helm upgrade --install observability-stack "${HELM_CHARTS_DIR}/observability-stack" \
    --namespace "${OBSERVABILITY_NAMESPACE}" \
    --create-namespace \
    --wait \
    --timeout 20m
}

smoke_test_interview_backend() {
  local namespace="interview-backend"
  local rollout="interview-backend"
  local service="interview-backend"
  local port=18080

  info "Waiting for ArgoCD to sync and create interview-backend workload"
  local attempts=0
  local use_rollout=false
  until kubectl get rollout.argoproj.io "${rollout}" -n "${namespace}" >/dev/null 2>&1 || \
        kubectl get deployment "${rollout}" -n "${namespace}" >/dev/null 2>&1; do
    attempts=$((attempts + 1))
    [[ ${attempts} -ge 60 ]] && die "Timed out waiting for interview-backend workload to be created by ArgoCD"
    sleep 5
  done

  if kubectl get rollout.argoproj.io "${rollout}" -n "${namespace}" >/dev/null 2>&1; then
    use_rollout=true
  fi

  if [[ "${use_rollout}" == "true" ]]; then
    info "Waiting for interview-backend rollout to be available"
    kubectl wait rollout.argoproj.io/"${rollout}" \
      --namespace "${namespace}" \
      --for=condition=Available \
      --timeout=5m
  else
    info "Waiting for interview-backend deployment to be available"
    kubectl wait deployment/"${rollout}" \
      --namespace "${namespace}" \
      --for=condition=available \
      --timeout=5m
  fi

  info "Running smoke test against GET /api/welcome"
  kubectl port-forward "svc/${service}" "${port}:8080" --namespace "${namespace}" &
  local pf_pid=$!
  sleep 3

  local response
  response=$(curl -sf "http://localhost:${port}/api/welcome" || true)
  kill "${pf_pid}" 2>/dev/null || true

  if [[ "${response}" == *"Welcome"* ]]; then
    info "Smoke test passed: ${response}"
  else
    die "Smoke test failed: unexpected response '${response}'"
  fi
}

up() {
  detect_platform
  ensure_minikube
  ensure_kubectl
  ensure_helm
  start_minikube
  build_and_load_interview_backend
  install_istio
  install_gateway
  install_cert_manager
  install_opentelemetry_operator
  install_external_secrets_operator
  install_argo_rollouts
  install_observability_stack
  install_argocd
  configure_argocd_repo
  smoke_test_interview_backend
  info "Bootstrap complete."
  info ""
  info "To access Grafana from a Windows browser (WSL2):"
  info ""
  info "  1. Run in a separate WSL2 terminal:"
  info "       kubectl port-forward svc/istio-ingress -n istio-ingress 8080:80"
  info ""
  info "  2. Add to Windows hosts file (C:\\Windows\\System32\\drivers\\etc\\hosts):"
  info "       127.0.0.1  grafana.local"
  info ""
  info "  3. Open: http://grafana.local:8080 (admin/admin)"
  info ""
  info "Note: minikube tunnel alone does not expose services to the Windows host"
  info "because it only creates routes inside the WSL2 network namespace."
}

teardown() {
  info "Deleting minikube cluster"
  minikube delete

  local kubeconfig="${KUBECONFIG:-${HOME}/.kube/config}"
  if [[ -f "$kubeconfig" ]]; then
    info "Removing minikube context and cluster from kubeconfig"
    kubectl config delete-context minikube 2>/dev/null || true
    kubectl config delete-cluster minikube 2>/dev/null || true
    kubectl config unset users.minikube 2>/dev/null || true
  fi

  info "Teardown complete"
}

usage() {
  echo "Usage: $(basename "$0") [--up | --teardown]"
  echo ""
  echo "  --up        Start minikube and install all Helm charts"
  echo "  --teardown  Delete the minikube cluster and remove it from kubeconfig"
  exit 1
}

case "${1:-}" in
  --up)       up ;;
  --teardown) teardown ;;
  *)          usage ;;
esac
