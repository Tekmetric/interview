{{/*
Expand the name of the chart.
*/}}
{{- define "chart.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "chart.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "chart.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "chart.labels" -}}
helm.sh/chart: {{ include "chart.chart" . }}
{{ include "chart.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "chart.selectorLabels" -}}
app.kubernetes.io/name: {{ include "chart.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "chart.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "chart.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Create the full image reference
*/}}
{{- define "chart.image" -}}
{{- printf "%s:%s" .Values.image.repository (.Values.image.tag | default .Chart.AppVersion) }}
{{- end }}

{{/*
Get the service port
*/}}
{{- define "chart.servicePort" -}}
{{- .Values.service.port }}
{{- end }}

{{/*
Common pod metadata (annotations and labels)
*/}}
{{- define "chart.podMetadata" -}}
{{- with .Values.podAnnotations }}
annotations:
{{- toYaml . | nindent 2 }}
{{- end }}
labels:
{{- include "chart.selectorLabels" . | nindent 2 }}
{{- end }}

{{/*
Common pod spec (imagePullSecrets, serviceAccountName, securityContext)
*/}}
{{- define "chart.podSpec" -}}
{{- with .Values.imagePullSecrets }}
imagePullSecrets:
{{- toYaml . | nindent 2 }}
{{- end }}
serviceAccountName: {{ include "chart.serviceAccountName" . }}
securityContext:
{{- toYaml .Values.podSecurityContext | nindent 2 }}
{{- end }}

{{/*
Common container spec (securityContext, image, imagePullPolicy)
*/}}
{{- define "chart.containerSpec" -}}
securityContext:
{{- toYaml .Values.securityContext | nindent 2 }}
image: {{ include "chart.image" . }}
imagePullPolicy: {{ .Values.image.pullPolicy }}
{{- end }}

{{/*
Container ports
*/}}
{{- define "chart.containerPorts" -}}
ports:
  - name: http
    containerPort: {{ include "chart.servicePort" . }}
    protocol: TCP
{{- end }}

{{/*
Liveness probe
*/}}
{{- define "chart.livenessProbe" -}}
livenessProbe:
  httpGet:
    path: /
    port: http
{{- end }}

{{/*
Readiness probe
*/}}
{{- define "chart.readinessProbe" -}}
readinessProbe:
  httpGet:
    path: /
    port: http
{{- end }}

{{/*
Common probes (liveness + readiness)
*/}}
{{- define "chart.probes" -}}
{{- include "chart.livenessProbe" . }}
{{- include "chart.readinessProbe" . }}
{{- end }}

{{/*
Pod scheduling (nodeSelector, affinity, tolerations)
*/}}
{{- define "chart.podScheduling" -}}
{{- with .Values.nodeSelector }}
nodeSelector:
{{- toYaml . | nindent 2 }}
{{- end }}
{{- with .Values.affinity }}
affinity:
{{- toYaml . | nindent 2 }}
{{- end }}
{{- with .Values.tolerations }}
tolerations:
{{- toYaml . | nindent 2 }}
{{- end }}
{{- end }}

{{/*
Common metadata block
*/}}
{{- define "chart.metadata" -}}
name: {{ include "chart.fullname" . }}
labels:
{{- include "chart.labels" . | nindent 2 }}
{{- end }}

{{/*
Ingress API version based on Kubernetes version
*/}}
{{- define "chart.ingress.apiVersion" -}}
{{- if semverCompare ">=1.19-0" .Capabilities.KubeVersion.GitVersion -}}
networking.k8s.io/v1
{{- else if semverCompare ">=1.14-0" .Capabilities.KubeVersion.GitVersion -}}
networking.k8s.io/v1beta1
{{- else -}}
extensions/v1beta1
{{- end }}
{{- end }}

{{/*
Ingress backend configuration based on Kubernetes version
*/}}
{{- define "chart.ingress.backend" -}}
{{- $fullName := include "chart.fullname" . -}}
{{- $svcPort := include "chart.servicePort" . -}}
{{- if semverCompare ">=1.19-0" $.Capabilities.KubeVersion.GitVersion }}
service:
  name: {{ $fullName }}
  port:
    number: {{ $svcPort }}
{{- else }}
serviceName: {{ $fullName }}
servicePort: {{ $svcPort }}
{{- end }}
{{- end }}

{{/*
Ingress className handling for older Kubernetes versions
*/}}
{{- define "chart.ingress.className" -}}
{{- if and .Values.ingress.className (not (semverCompare ">=1.18-0" .Capabilities.KubeVersion.GitVersion)) }}
  {{- if not (hasKey .Values.ingress.annotations "kubernetes.io/ingress.class") }}
  {{- $_ := set .Values.ingress.annotations "kubernetes.io/ingress.class" .Values.ingress.className}}
  {{- end }}
{{- end }}
{{- end }}

{{/*
HPA scale target reference
*/}}
{{- define "chart.hpaScaleTargetRef" -}}
scaleTargetRef:
  apiVersion: apps/v1
  kind: Deployment
  name: {{ include "chart.fullname" . }}
{{- end }}

{{/*
HPA metrics for CPU
*/}}
{{- define "chart.hpaCpuMetric" -}}
{{- if .Values.autoscaling.targetCPUUtilizationPercentage }}
- type: Resource
  resource:
    name: cpu
    targetAverageUtilization: {{ .Values.autoscaling.targetCPUUtilizationPercentage }}
{{- end }}
{{- end }}

{{/*
HPA metrics for Memory
*/}}
{{- define "chart.hpaMemoryMetric" -}}
{{- if .Values.autoscaling.targetMemoryUtilizationPercentage }}
- type: Resource
  resource:
    name: memory
    targetAverageUtilization: {{ .Values.autoscaling.targetMemoryUtilizationPercentage }}
{{- end }}
{{- end }}

{{/*
HPA metrics (CPU + Memory)
*/}}
{{- define "chart.hpaMetrics" -}}
{{- include "chart.hpaCpuMetric" . }}
{{- include "chart.hpaMemoryMetric" . }}
{{- end }}

{{/*
Service selector
*/}}
{{- define "chart.serviceSelector" -}}
selector:
{{- include "chart.selectorLabels" . | nindent 2 }}
{{- end }}

{{/*
Deployment selector
*/}}
{{- define "chart.deploymentSelector" -}}
selector:
  matchLabels:
{{- include "chart.selectorLabels" . | nindent 4 }}
{{- end }}
