# Quickstart

Local bring-up of the full stack: k3d cluster, Argo CD, platform components (cert-manager, ingress-nginx, Vault, ESO, kube-prometheus-stack), and the Spring Boot backend.

## Prerequisites

- Docker Desktop
- k3d(https://k3d.io) ≥ 5.7
- `kubectl` ≥ 1.27
- `helm` ≥ 3.15

```bash
make doctor   # verifies all four are installed and prints versions
```

## TL;DR

```bash
make up       # create k3d cluster, install Argo CD, apply the root app-of-apps
make demo     # print URLs + Argo admin password
```

First run takes ~3–4 minutes (image pulls + Argo sync waves). Subsequent runs are faster since images are cached.

## Demo surface

| URL | What |
|-----|------|
| `https://app.sre-demo.local/api/welcome` | The backend app |
| `https://argocd.sre-demo.local`          | Argo CD UI (auto-sync, health tree) |
| `https://grafana.sre-demo.local`         | Grafana (preloaded backend dashboard) |
| `https://prometheus.sre-demo.local`      | Prometheus UI |
| `https://alertmanager.sre-demo.local`    | Alertmanager |
| `https://vault.sre-demo.local`           | Vault UI (dev mode, token `root`) |

All hostnames resolve via `/etc/hosts` (added by `make up`, needs sudo once).

Certs are signed by a self-signed CA. Either click through the browser warning, or run `make trust-ca` once to add the CA to your macOS keychain and silence the warning.

## Useful Make targets

| Target | What it does |
|--------|--------------|
| `make doctor` | Check docker/k3d/kubectl/helm are installed |
| `make up` | Full bring-up: hosts → cluster → Argo → root app |
| `make down` | Delete the k3d cluster |
| `make status` | Show Argo Application sync/health status |
| `make demo` | Print demo URLs and Argo admin password |
| `make vault-init` | Seed demo secrets into Vault + create ESO auth Secret |
| `make lint` | `helm lint --strict` + `kubeconform` on rendered manifests |
| `make test` | Run `helm-unittest` specs (auto-installs plugin) |
| `make template` | Render the chart to stdout for inspection |
| `make trust-ca` | Add the demo CA to macOS keychain |

## Demo flow (10 min)

1. **`make up`** (run before interview to avoid pulling images live). Walk through what it's doing.
2. **Argo CD**: show all Applications Synced + Healthy, drill into the backend app's resource tree.
3. **Grafana**: backend dashboard with live request rate, JVM heap, p95 latency.
4. **Prometheus**: scrape target green; show one of the `PrometheusRule` alerts.
5. **Vault UI**: `secret/backend` contents; rotate a value; watch the `Secret` in the `backend` namespace refresh via ESO.
6. **App itself**: `curl https://app.sre-demo.local/api/welcome`.
7. **Walk the chart**: `values.yaml`, probes, `NetworkPolicy`, `ServiceMonitor`.
8. **Push a trivial commit**: watch CI build → push to GHCR → bump `values.yaml` → Argo sync → rolling update complete. ~2 minutes end to end.

## Troubleshooting

**`make up` hangs on Argo CD rollout** — usually Docker Desktop running low on resources. Argo + Vault + kube-prometheus-stack want ~4 GB RAM total. Bump Docker Desktop's memory allocation.

**Backend pod in `ImagePullBackOff`** — the chart's `values.yaml` has `image.tag: "sha-placeholder"` at rest. CI is what bumps that tag on push. For a pre-push local test, either build/load the image locally and update the tag, or rely on the full GitOps path (push to fork, let CI build).

**Browser "not secure" warnings** — expected; the cluster uses a self-signed CA. `make trust-ca` adds the CA to your macOS keychain; restart the browser to pick it up.

**`/etc/hosts` entries missing** — `make hosts` (called by `make up`) adds them with a single sudo prompt. Run it standalone if you skipped it.
