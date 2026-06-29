# Architecture & Design Decisions

A production-leaning deployment of the Spring Boot backend in `../backend`. Containerized with Docker, deployed via a Helm chart managed by Argo CD, observed with the kube-prometheus-stack, secrets backed by HashiCorp Vault via the External Secrets Operator, edge TLS via cert-manager, all exposed through ingress-nginx. CI builds images, scans them, pushes to GHCR, and triggers GitOps sync by bumping the chart values.

See [`QUICKSTART.md`](./QUICKSTART.md) for how to run it.

---

## Architecture

```
            ┌──────────────────────────┐
push to  →  │  GitHub Actions          │  build, Trivy scan, push to GHCR,
main        │  .github/workflows/ci.yml│  bump chart values.yaml image tag,
            └───────────┬──────────────┘  commit back with [skip ci]
                        │
                        ▼ (git is source of truth)
                 ┌──────────────┐
                 │   Argo CD    │  watches this repo, auto-syncs, self-heals
                 └──────┬───────┘
                        │
         ┌──────────────┴──────────────────────────────────────┐
         │ app-of-apps (gitops/bootstrap/root-app.yaml)        │
         │                                                     │
         │  wave 0:  cert-manager, ingress-nginx, vault        │
         │  wave 1:  cert-manager-issuers, external-secrets,   │
         │           kube-prometheus-stack                     │
         │  wave 2:  external-secrets ClusterSecretStore       │
         │  wave 3:  tekmetric-backend (this chart)            │
         └─────────────────────────────────────────────────────┘
```

### Why this shape

- **Argo CD app-of-apps pattern.** One root Application deploys the other child Applications. Adding a new platform component means one YAML file; the root app picks it up. Mirrors how real platform teams run multi-component environments.
- **Sync waves.** cert-manager has to install CRDs before a `ClusterIssuer` can be created. ESO has to install its CRDs before a `ClusterSecretStore` exists. Vault has to be up before ESO can authenticate. Waves enforce ordering declaratively.
- **k3d with Traefik disabled.** k3s/k3d ship Traefik by default, installed outside Argo's view. Having a critical ingress component unmanaged by git breaks the "git is source of truth" invariant. Disabled at cluster creation; ingress-nginx is installed via Argo instead.

---

## Production-readiness checklist (what the Helm chart does)

| Concern | What the chart does |
|--------|----------------------|
| Image hygiene | Multi-stage build, non-root user, Eclipse Temurin JRE, pinned tags (no `latest`) |
| Security context | `runAsNonRoot`, `readOnlyRootFilesystem`, `allowPrivilegeEscalation=false`, all caps dropped, `RuntimeDefault` seccomp |
| Network isolation | `NetworkPolicy` default-deny, explicit allow for ingress-nginx → :8080 and Prometheus → :8081 |
| Probes | `startupProbe` buys JVM cold-start time, `livenessProbe` restarts only on sustained failure, `readinessProbe` pulls from LB fast |
| Graceful shutdown | `preStop` sleep + `terminationGracePeriodSeconds=30` so in-flight requests drain before SIGTERM is honored |
| Autoscaling | HPA on CPU and memory, stabilization windows tuned to prevent flapping |
| Disruption protection | PDB with `minAvailable: 1` (dev) / `2` (prod) prevents voluntary evictions from draining the service |
| Scheduling HA | Pod anti-affinity (soft in dev, hard in prod), `topologySpreadConstraints` across zones in prod |
| Config management | `ConfigMap` for env, checksum annotation rolls pods when config changes |
| Secrets | `ExternalSecret` pulls from Vault via ESO, never in git, refreshed every 1m |
| Observability | `ServiceMonitor` (Prometheus Operator), `PrometheusRule` with multi-window burn-rate alerting, Grafana dashboard auto-loaded via ConfigMap sidecar label |
| TLS | cert-manager `ClusterIssuer` (self-signed CA for demo, `letsencrypt-prod` in `values-prod.yaml`) |
| Resource limits | Requests and limits set; JVM `MaxRAMPercentage=75` respects the container memory limit |

---

## CI / CD

**PR workflow** (`.github/workflows/pr.yml`):
- hadolint on Dockerfile
- `helm lint --strict`
- `kubeconform` on rendered manifests
- `helm-unittest` on chart specs
- Docker build
- Trivy scan (fail on CRITICAL/HIGH)

**Main workflow** (`.github/workflows/ci.yml`):
- All of the above
- Push image to `ghcr.io/schlotech/tekmetric-backend:sha-<short>` (and `v*.*.*-sha-<short>` on tags)
- Trivy SARIF upload to GitHub Security tab
- `sed` bump of `chart/values.yaml` and `chart/Chart.yaml`, commit back with `[skip ci]`
- Argo CD detects the commit and auto-syncs. No manual `helm upgrade` anywhere.

### Image tag strategy

- **Main pushes:** `sha-<7 char>`. Every commit uniquely addressable, immutable.
- **Release tags (`v*.*.*`):** `vX.Y.Z-sha-<7 char>`. Human-readable version plus traceable build.
- **Never used:** `latest`, `main`, or any mutable tag in Argo-managed `values.yaml`. Mutable tags defeat GitOps (the cluster can drift from git).

---

## Backend changes (minimal, additive)

The exercise says "productionize this app." I made two small, non-functional changes to `../backend`:

1. **`pom.xml`:** added `spring-boot-starter-actuator` and `micrometer-registry-prometheus`. This gets us `/actuator/health` for probes and `/actuator/prometheus` for metrics scraping. Without these, the app has no production observability surface.
2. **`application.properties`:** configured actuator exposure (`health,info,prometheus`), moved management to port 8081 so it can be network-isolated, and made `spring.h2.console.enabled` env-overridable so prod values disable it.

**Rationale:** a Spring Boot app without health endpoints or metrics isn't something you can operate in production. The changes are additive (no code modifications, no behavioral changes for the existing `/api/welcome` endpoint) and the production values file disables the H2 console entirely. A senior SRE owns the production outcome, not a narrow slice of the repo boundary.

### Spring Boot 2.2.1 caveat

The app is on Spring Boot 2.2.1 (2019). Two consequences worth calling out:
- **Dedicated liveness/readiness endpoints** (`/actuator/health/liveness`, `/actuator/health/readiness`) were introduced in 2.3.0. On 2.2.1, all three probes hit `/actuator/health`. The different probe **thresholds** still give different semantics: readiness pulls from LB in ~10s, liveness restarts only after ~60s of sustained failure, startup gives the JVM ~5min to boot. Upgrading Spring Boot to 2.7+ (or 3.x) would be a separate PR.
- **Jar layering** (`spring-boot:build-image` / `layertools`) was added in 2.3. The Dockerfile uses a simple single-layer jar copy. Fine for this app size; I'd switch to layered builds on 2.3+.

---

## Observability

**Metrics:**
- Micrometer exposes JVM (`jvm_memory_used_bytes`, `jvm_gc_pause_seconds`), HTTP (`http_server_requests_seconds_*`), and system metrics at `/actuator/prometheus` on port 8081.
- `ServiceMonitor` tells Prometheus Operator how to scrape them.
- Pre-built Grafana dashboard (request rate, error rate, p50/p95/p99 latency, JVM heap, GC pauses) ships as a ConfigMap with the `grafana_dashboard=1` label that the Grafana sidecar auto-discovers.

**Alerting (SLO-based, not threshold-based):**
- `TekmetricBackendHighErrorRateFast`: fires at `>2%` 5xx rate over 5min, labeled `severity: critical`. Tuned for fast burn: at this rate, monthly error budget is gone in less than a day.
- `TekmetricBackendHighLatencyP95`: fires if p95 `>1s` for 10min.
- `TekmetricBackendPodNotReady`: fires if any pod is non-ready for 5min.

Burn-rate alerts page on user impact, not arbitrary thresholds. In a real environment I'd add slow-burn counterparts (5% budget consumption over 6h) to catch chronic degradation that fast-burn misses.

---

## Secrets

**Demo chain:** dev-mode Vault → External Secrets Operator → k8s Secret → `envFrom`.

- `make vault-init` seeds `secret/backend` with `datasource_password` and `api_key`, and writes the Vault root token to a Kubernetes Secret in the `external-secrets` namespace (ESO's auth material).
- The `ClusterSecretStore` named `vault-backend` points ESO at Vault.
- The chart's `ExternalSecret` (enabled in `values-prod.yaml`) creates a real k8s Secret with a 1-minute refresh interval. Rotate a value in Vault, watch the Secret update. The Deployment has a `checksum/config` annotation on the ConfigMap so ConfigMap-driven env changes roll pods; secret rotation is handled by your operator of choice (e.g. Reloader).

**What's different in prod:**
- Vault runs HA with Raft storage and auto-unseal (AWS KMS / GCP KMS / Azure Key Vault).
- ESO authenticates via Kubernetes auth method (ServiceAccount token review) or AWS IAM auth, not a static token.
- The "chicken-and-egg" auth bootstrap Secret (ESO ↔ Vault) comes from Sealed Secrets or an IRSA-annotated ServiceAccount, not `kubectl create secret`.

---

## What I'd do differently in prod (short list)

- **Split repos.** App code in one repo, GitOps config in another. Prevents CI loops and gives the platform team control over deployment velocity independent of app PRs.
- **Progressive delivery.** Argo Rollouts for canary/blue-green on the backend. With one real service this is overkill; at 10+ services it's table stakes.
- **Service mesh.** Istio ambient mode for mTLS between services, L7 policy, richer tracing. Not worth it for one service.
- **Real CA.** cert-manager with Let's Encrypt (HTTP-01 behind a public ingress, or DNS-01 with Route53 / Cloud DNS IAM). `values-prod.yaml` already points at `letsencrypt-prod`.
- **SealedSecrets for bootstrap.** Remove the `kubectl create secret` step from `vault-init.sh` in favor of an encrypted `SealedSecret` in git.
- **SLO tracking dashboard.** A dedicated Grafana dashboard showing error budget consumption over the month, not just point-in-time rates.
- **Cosign signing + attestation.** Sign images with Sigstore keyless, attach SBOM and Trivy attestation, verify in cluster via Kyverno / policy-controller.
- **PodSecurity admission.** Enforce `restricted` at the namespace level.
- **Backup.** Velero on any namespace with persistent state.
- **Spring Boot upgrade.** To 2.7+ or 3.x for dedicated liveness/readiness endpoints, layered jars, updated JVM compatibility.

---

## Repository layout

```
interview/sre/
├── README.md                        # interview prompt (unmodified from Tekmetric)
├── Makefile                         # make up/down/demo/lint/test/...
├── Dockerfile                       # multi-stage, non-root, Temurin 8 JRE
├── .dockerignore
├── .github/workflows/
│   ├── pr.yml                       # PR validation
│   └── ci.yml                       # main: build + push + GitOps bump
├── chart/                           # tekmetric-backend Helm chart
│   ├── Chart.yaml
│   ├── values.yaml                  # dev defaults
│   ├── values-prod.yaml             # prod overrides
│   ├── templates/
│   │   ├── _helpers.tpl
│   │   ├── deployment.yaml
│   │   ├── service.yaml
│   │   ├── serviceaccount.yaml
│   │   ├── configmap.yaml
│   │   ├── ingress.yaml
│   │   ├── networkpolicy.yaml
│   │   ├── hpa.yaml
│   │   ├── pdb.yaml
│   │   ├── servicemonitor.yaml
│   │   ├── prometheusrule.yaml
│   │   ├── grafana-dashboard.yaml
│   │   └── externalsecret.yaml
│   └── tests/                       # helm-unittest specs
├── docs/
│   ├── ARCHITECTURE.md              # this file
│   └── QUICKSTART.md                # local bring-up + demo flow
├── gitops/
│   ├── bootstrap/
│   │   └── root-app.yaml            # the app-of-apps entrypoint
│   ├── apps/                        # one Application per platform component
│   │   ├── cert-manager.yaml
│   │   ├── cert-manager-issuers.yaml
│   │   ├── ingress-nginx.yaml
│   │   ├── vault.yaml
│   │   ├── external-secrets.yaml
│   │   ├── external-secrets-store.yaml
│   │   ├── monitoring.yaml
│   │   └── backend.yaml
│   └── manifests/                   # raw YAML referenced by some apps
│       ├── cert-manager/
│       │   └── selfsigned-ca.yaml
│       └── external-secrets/
│           └── clustersecretstore.yaml
└── scripts/
    ├── argocd-ingress.yaml          # Argo + Vault UI ingresses (imperative)
    ├── vault-init.sh                # seed demo secrets + ESO auth Secret
    └── trust-ca.sh                  # trust the demo CA in macOS keychain
```
