{{- define "interview-backend.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "interview-backend.fullname" -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- printf "%s" $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "interview-backend.labels" -}}
app.kubernetes.io/name: {{ include "interview-backend.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}

{{- define "interview-backend.selectorLabels" -}}
app.kubernetes.io/name: {{ include "interview-backend.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}
