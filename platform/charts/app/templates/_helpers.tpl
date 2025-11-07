{{/* Labels(Chart): Combines chart name and version for use in labels */}}
{{- define "app.chart" -}}
  {{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Labels(Common) */}}
{{- define "app.labels" -}}
  {{- printf "app.kubernetes.io/managed-by: %s" .Release.Service -}}
  {{- printf "helm.sh/chart: %s" (include "app.chart" .) | nindent 0 -}}
  {{- include "app.selectorLabels" . | nindent 0 -}}
{{- end -}}

{{/* Application Name: Defaults to the Release Name */}}
{{- define "app.name" -}}
  {{- default .Release.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Port Name: Combines the http prefix to the container port */}}
{{- define "app.portName" -}}
  {{- printf "http%v" .Values.deployment.containers.containerPort -}}
{{- end -}}

{{/* Labels(Selector) */}}
{{- define "app.selectorLabels" -}}
  {{- printf "app.kubernetes.io/instance: %s" .Release.Name -}}
  {{- printf "app.kubernetes.io/name: %s" (include "app.name" .) | nindent 0 -}}
{{- end -}}
