# Design Decisions

This document explains the reasoning behind each major design choice in the implementation.

---

## Dockerfile

### Multi-stage build

The `backend/Dockerfile` uses two FROM stages. The first (`maven:3.9-eclipse-temurin-8`) compiles the JAR and downloads the OpenTelemetry agent. The second (`eclipse-temurin:8-jre-jammy`) copies only those two artifacts into a clean JRE image.

The result is a runtime image that contains no Maven, no JDK toolchain, and no build cache — just the JRE, the JAR, and the OTel agent. This matters for two reasons:

- **Attack surface.** Build tools and compilers have their own CVEs. Excluding them from the runtime image removes those vulnerabilities entirely, rather than patching them.
- **Image size.** A JRE-only image is roughly 3× smaller than a JDK image, which speeds up ECR pulls and reduces cold-start time.

### eclipse-temurin over openjdk

`eclipse-temurin` (Adoptium) is the recommended replacement for the deprecated `openjdk` Docker images. The `8-jre-jammy` tag uses Ubuntu 22.04 LTS as the base, which receives regular security patches. A slim Debian base would be slightly smaller, but Ubuntu LTS gives more predictable patch cadence in practice.

### OpenTelemetry Java agent baked in

Rather than mounting the agent at runtime or injecting it via an init container, the agent JAR is downloaded during the build stage and copied into the image. This makes the container self-contained — the same image works in Docker locally, in k3s, and in EKS without any additional configuration at the orchestration layer.

`JAVA_TOOL_OPTIONS` is set to activate the agent automatically on any `java` invocation, so the application code requires zero changes. The OTel SDK endpoint is set to `localhost:4317` by default, pointing at the sidecar collector.

---

## CI/CD Pipeline

### One Workflow

The main advantage of a single file workflow is that job dependencies are explicit and visible in one place. The dependency graph at the top of the file makes the intent clear:

```
changes ──┬── trivy-scan ── build-push ──┐
          │                               ├── terraform-dev-deploy  (PR only)
          └── helm-validate ─────────────┘
```

If this workflow was broken into four files there is no way to express that `terraform-dev-deploy` should wait for both `build-push` and `helm-validate`. Each file would trigger independently.

### Path-based change detection

`dorny/paths-filter` detects which paths changed in a given push or PR. The three output signals (`backend`, `helm`, `terraform`) control which downstream jobs run. A documentation-only commit does not trigger a Docker build. A Helm-only change does not trigger Trivy scanning.

On `workflow_dispatch` all filters are forced to `true`, so a manual trigger always runs everything.

### OIDC authentication for AWS

The pipeline uses GitHub's OIDC token to assume an IAM role rather than storing long-lived `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY` secrets. The benefits:

- No credentials to rotate or accidentally expose in logs.
- The IAM trust policy can be scoped to specific repository and branch combinations, so a fork cannot assume the role.
- This is the current AWS-recommended approach for CI systems.

### Two-phase Trivy scanning

Trivy runs twice:

1. **Filesystem scan** (before build) — scans the source tree and dependency manifests (`pom.xml`). This catches known CVEs in libraries declared in the dependency tree even before the image exists. Results are uploaded to the GitHub Security tab as SARIF.
2. **Container image scan** (after push) — scans the built and pushed image. This catches CVEs introduced by the base image layers themselves, which the filesystem scan cannot see.

Scans never fail the build by default (`exit-code: 0`). A manual `workflow_dispatch` input can promote them to hard failures when needed. The reasoning: blocking every PR on a third-party base image CVE that has no fix yet would cause more developer friction than security value. The results are surfaced visibly on every PR so they are not ignored.

### Image tagging strategy

| Context | Tag format | Rationale |
|---------|-----------|-----------|
| Pull request | `pr-{N}-{short-sha}` | Uniquely identifies the build; can be deployed to a review environment |
| Merge to main | `{short-sha}-{date}` + `latest` | Short SHA gives traceability; date makes scanning chronologically sortable; `latest` supports GitOps tools that watch for the canonical tag |

`latest` is only pushed on merges to main, never on PRs, so it always refers to a production-ready build.

### Registry layer caching

The `build-push` job uses ECR's `latest` tag as a cache source. On a cache hit the layer download happens from ECR (same region as the runner), which is typically faster than rebuilding from scratch. This is a meaningful saving for the Maven dependency download step in particular.

### Terraform dev deploy on every PR

Every PR automatically plans and applies Terraform against the dev environment. The plan and apply output are posted as a PR comment. This ensures infrastructure drift is caught early — a developer cannot merge an application change that would break the infra it depends on without seeing the Terraform effect first.

---

## Helm Chart

### Argo Rollouts canary instead of a standard Deployment

The chart defaults to an Argo Rollouts `Rollout` resource rather than a `Deployment`. The canary strategy sends 20% of traffic to the new version, pauses 30 seconds, then 50%, pauses again, then promotes to 100%.

The key reasons for this over a standard `Deployment` rolling update:

- **Traffic-weighted, not replica-weighted.** Kubernetes rolling updates shift traffic by changing the number of pods. At low replica counts (e.g. 2) you can only do 0% or 50% — there is no intermediate. Argo Rollouts routes traffic at the Istio VirtualService layer, so you get true percentage-based control regardless of replica count.
- **Pause at each step.** The pauses allow time for error rate and latency dashboards to be checked before committing the rollout. They can also be driven by Argo Analysis with automated metric gates.
- **One flag to fall back.** `rollout.enabled: false` switches to a plain Deployment, which is useful for local debugging.

### Istio service mesh

Istio is used rather than a simpler Ingress controller for three reasons:

1. **mTLS between services.** `PeerAuthentication` with `STRICT` mode means all in-cluster traffic is mutually authenticated. No service can receive unencrypted traffic or impersonate another.
2. **Fine-grained traffic control.** `VirtualService` and `DestinationRule` give per-route timeout, retry, and canary weight configuration that a standard Ingress resource cannot express.
3. **Telemetry at the mesh layer.** Istio emits L7 metrics (request rate, error rate, latency) and propagates trace context for every service-to-service call without any changes to application code.

Traefik (the k3s default) is disabled in the bootstrap script so it does not conflict with Istio's ingress gateway.

### OpenTelemetry Collector sidecar

Each pod gets an OTel Collector sidecar injected by the OpenTelemetry Operator. The Java agent sends telemetry to `localhost:4317` (the sidecar), and the sidecar fans it out to three backends over separate pipelines:

- Traces → Tempo (OTLP gRPC)
- Metrics → Alloy/Prometheus (OTLP gRPC)
- Logs → Loki (OTLP HTTP)

The indirection through the collector is intentional. If the backend endpoint changes, only the collector config changes — the application image does not need to be rebuilt. The collector also handles batching and memory limiting, preventing a misbehaving application from overwhelming the backend.

### External Secrets Operator

`ExternalSecret` resources are defined in the chart but disabled by default (`externalSecrets.enabled: false`). When enabled, ESO pulls values from AWS Secrets Manager and materialises them as Kubernetes `Secret` objects with a configurable refresh interval.

The alternative would be to store secrets in the Helm values files or as Kubernetes secrets in the git repository. Neither is acceptable — plaintext secrets in git is a well-known anti-pattern, and base64-encoded secrets in a `Secret` manifest are equally readable.

### Pod security hardening

Every pod gets the following by default:

```yaml
runAsNonRoot: true
runAsUser: 1000
readOnlyRootFilesystem: true
capabilities:
  drop: [ALL]
allowPrivilegeEscalation: false
seccompProfile:
  type: RuntimeDefault
```

`readOnlyRootFilesystem` requires a writable `emptyDir` volume mounted at `/tmp` for the JVM's temp directory. This is a deliberate tradeoff — it eliminates an entire class of container escape techniques at the cost of one extra volume mount.

`seccompProfile: RuntimeDefault` applies the container runtime's default syscall filter, blocking obscure syscalls that normal JVM processes never need.

### ConfigMap checksum annotation

The deployment template adds an annotation containing the SHA256 of the ConfigMap data:

```yaml
checksum/config: {{ include (print $.Template.BasePath "/configmap.yaml") . | sha256sum }}
```

When a ConfigMap value changes, `helm upgrade` recomputes the checksum. Kubernetes sees the pod template has changed and triggers a rolling restart. Without this, a ConfigMap update would not restart any pods, and the application would continue reading the stale mounted config until manually restarted.

### Three values files

| File | Target | Key differences |
|------|--------|----------------|
| `values.yaml` | Base defaults | ECR registry, canary rollout, Istio enabled |
| `values-k3s.yaml` | Local k3d dev | No registry, `pullPolicy: Never`, resource limits |
| `values-eks.yaml` | AWS EKS production | External Secrets enabled, ECR pull secrets |

This avoids maintaining separate chart copies for different environments. The base chart is always the source of truth; environment-specific overlays are layered on top with `-f`.

---

## Observability

### LGTM stack (Loki, Grafana, Tempo, Prometheus/Alloy)

All four components are open-source, have native OTLP support, and are designed to be deployed together. Grafana has first-class data source integrations for all three backends, making it straightforward to correlate a trace from Tempo with logs in Loki and a metric spike in Prometheus within a single dashboard.

The `k8s-monitoring` Helm chart (Grafana Labs) wraps the Alloy collector and automatically scrapes cluster-level metrics (node CPU/memory, kube-state-metrics, pod logs) without manual configuration.

### Three observability signals for the application

The OTel Java agent provides all three signals automatically:

- **Metrics** — JVM heap usage, GC pause time, HTTP request counts, error rates, response time histograms.
- **Logs** — structured JSON logs enriched with `trace_id` and `span_id` so individual log lines can be linked directly to the trace that produced them.
- **Traces** — distributed traces across the full call graph, including Istio's sidecar spans and any downstream HTTP or database calls.

The correlation between signals is the core value proposition. When a latency alert fires, you can jump from the Prometheus metric to the specific slow trace in Tempo, then from the trace to the related log lines in Loki — without manually searching across three separate tools.

### Why a sidecar collector rather than a DaemonSet collector

A DaemonSet collector runs one collector process per node and receives telemetry from all pods on that node over the network. A sidecar runs one collector per pod.

For this application the sidecar model is preferred because:

- Each pod's collector pipeline is isolated. A spike in one pod's telemetry cannot cause back-pressure that affects neighbouring pods.
- The collector config can be customised per-application via Helm values without touching a shared DaemonSet config.
- The OpenTelemetry Operator manages sidecar injection automatically based on the `sidecar.opentelemetry.io/inject` pod annotation.

The tradeoff is higher memory usage (one collector process per pod). The collector is configured with a `memory_limiter` processor capped at 500 MiB to prevent runaway memory consumption.

---

## Local Development — `bootstrap-k3s.sh`

### k3d over kind or minikube

k3d runs k3s (a lightweight Kubernetes distribution) inside Docker containers. It was chosen because:

- It runs on any machine that has Docker, including WSL2, without requiring a separate hypervisor.
- The full cluster can be destroyed and recreated in under a minute with `k3d cluster delete` / `k3d cluster create`.
- `k3d image import` loads a locally-built Docker image directly into the cluster's containerd without a registry push, which is essential for the `pullPolicy: Never` local dev workflow.

Traefik is disabled at cluster creation (`--disable=traefik`) because Istio manages all ingress. Running both would cause port conflicts and confusion.

### Installation order

The bootstrap order matters because of CRD dependencies:

1. Istio CRDs must be present before any resource that uses `VirtualService`, `Gateway`, or `PeerAuthentication`.
2. cert-manager must be running before any `Certificate` or `Issuer` resource.
3. OTel Operator CRDs must be present before the `OpenTelemetryCollector` sidecar spec in the application chart.
4. Argo Rollouts CRDs must be present before any `Rollout` resource.
5. ArgoCD is installed last because it reads from the git repository, which requires all the above to be ready so that the app it syncs can actually deploy.

### GitOps with ArgoCD

ArgoCD watches the git repository and reconciles the cluster state to match the declared Helm values. If someone manually edits a resource in the cluster, ArgoCD detects the drift and reverts it on the next sync cycle. This makes the git repository the single source of truth for cluster state.

The bootstrap script configures ArgoCD's repository credentials from the local SSH key (`~/.ssh/id_ed25519` or `id_rsa`) and targets the current branch dynamically (`git rev-parse --abbrev-ref HEAD`), so developers can test their branch's chart changes end-to-end without merging first.

### `set -euo pipefail`

The script opens with `set -euo pipefail`. This causes the script to exit immediately on any unhandled error (`-e`), treat unset variables as errors (`-u`), and propagate pipe failures correctly (`-o pipefail`). Combined with the `die()` function, every error produces a clear `[ERROR]` message identifying exactly which step failed, rather than silently continuing and failing in a confusing way later.

---

## AWS Infrastructure (Terraform)

### EKS for production

EKS was chosen over self-managed Kubernetes because:

- The control plane (API server, etcd, scheduler) is managed by AWS. Upgrades, patches, and availability are handled without operator intervention.
- Native integrations with IAM, VPC networking, ALB, Route53, and ACM are available out of the box.
- IRSA (IAM Roles for Service Accounts) gives individual pods scoped AWS permissions without sharing node instance profile credentials.

### OIDC / IRSA

The EKS cluster is provisioned with an OIDC provider. This allows IAM roles to be bound to Kubernetes service accounts using trust policies that check the pod's projected service account token. A pod running with `serviceAccountName: interview-backend` can assume an IAM role that only allows, for example, `s3:GetObject` on a specific bucket — without any AWS credentials being stored in the pod or on the node.

The CI pipeline uses the same OIDC mechanism (GitHub Actions OIDC → IAM role) for consistency.

### Node sizing for dev

The dev environment uses `t3.xlarge` nodes (4 vCPU, 16 GB each). The full observability stack (Prometheus, Loki, Tempo, Grafana, Alloy, Istio, cert-manager, OTel Operator, ESO, Argo Rollouts, ArgoCD) consumes significant memory even at idle. `t3` instances are burstable, which is appropriate for a dev environment with intermittent load. Production would use `m6i` or `c6i` instances with predictable CPU performance.
