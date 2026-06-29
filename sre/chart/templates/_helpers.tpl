{{/*
Expand the name of the chart.
*/}}
{{- define "tekmetric-backend.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Fully qualified app name. Truncated at 63 chars because some k8s name fields
are limited to this.
*/}}
{{- define "tekmetric-backend.fullname" -}}
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

{{- define "tekmetric-backend.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Standard labels applied to every resource. Matches the Kubernetes
recommended labels (app.kubernetes.io/*).
*/}}
{{- define "tekmetric-backend.labels" -}}
helm.sh/chart: {{ include "tekmetric-backend.chart" . }}
{{ include "tekmetric-backend.selectorLabels" . }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: tekmetric
{{- end }}

{{/*
Selector labels. Stable subset of labels used for matchLabels in
Deployment, Service, NetworkPolicy, PDB, etc. Must not change across
releases or Deployment selector immutability will block upgrades.
*/}}
{{- define "tekmetric-backend.selectorLabels" -}}
app.kubernetes.io/name: {{ include "tekmetric-backend.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{- define "tekmetric-backend.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "tekmetric-backend.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}
