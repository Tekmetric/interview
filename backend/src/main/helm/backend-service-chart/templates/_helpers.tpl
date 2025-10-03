{{/*
Expand the name of the chart.
*/}}
{{- define "helm.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "helm.fullname" -}}
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
{{- define "helm.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "helm.labels" -}}
helm.sh/chart: {{ include "helm.chart" . }}
{{ include "helm.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | replace "+" "-" | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "helm.selectorLabels" -}}
app.kubernetes.io/name: {{ .Values.app.name | default (include "helm.name" .) }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "helm.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "helm.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Get the appropriate resources block.
It prefers a sizing profile but falls back to the top-level resources block.
*/}}
{{- define "helm.resources" -}}
{{- $profile := .Values.sizeProfile | default "custom" -}}
{{- $profiles := .Values.sizing.profiles | default dict -}}
{{- $customResources := .Values.resources | default dict -}}
{{- $selectedProfile := index $profiles $profile | default dict -}}
{{- toYaml (coalesce $selectedProfile $customResources "test: test") -}}
{{- end -}}

{{/*
Get the appropriate VPA resource policy.
It prefers a sizing profile but falls back to the top-level vpa policy block.
*/}}
{{- define "helm.vpaResourcePolicy" -}}
{{- $profile := .Values.sizing.profile | default "custom" -}}
{{- $profiles := .Values.sizing.profiles | default dict -}}
{{- $customPolicy := .Values.verticalPodAutoscaler.resourcePolicy.containerPolicies | default list -}}
{{- $selectedProfile := index $profiles $profile | default dict -}}
{{- $profilePolicy := dict -}}
{{- if $selectedProfile.requests -}}
{{- $_ := set $profilePolicy "minAllowed" $selectedProfile.requests -}}
{{- end -}}
{{- if $selectedProfile.limits -}}
{{- $_ := set $profilePolicy "maxAllowed" $selectedProfile.limits -}}
{{- end -}}
{{- if or $profilePolicy.minAllowed $profilePolicy.maxAllowed -}}
containerPolicies:
  - containerName: "*"
{{- if $profilePolicy.minAllowed }}
    minAllowed:
{{- toYaml $profilePolicy.minAllowed | nindent 6 }}
{{- end }}
{{- if $profilePolicy.maxAllowed }}
    maxAllowed:
{{- toYaml $profilePolicy.maxAllowed | nindent 6 }}
{{- end }}
{{- else -}}
{{- toYaml $customPolicy -}}
{{- end -}}
{{- end -}}

{{/*
Calculate JVM memory options based on container memory limits.
Works with sizing profiles and handles all Kubernetes memory formats.
*/}}
{{- define "helm.jvmMemoryOptions" -}}
{{- $resources := include "helm.resources" . | fromYaml -}}
{{- if and $resources.limits $resources.limits.memory -}}
{{- $memLimit := $resources.limits.memory -}}
{{- $memMi := 0 -}}
{{- if hasSuffix "Gi" $memLimit -}}
  {{- $memValue := trimSuffix "Gi" $memLimit | float64 -}}
  {{- $memMi = mul $memValue 1024 | int -}}
{{- else if hasSuffix "Mi" $memLimit -}}
  {{- $memValue := trimSuffix "Mi" $memLimit | float64 -}}
  {{- $memMi = $memValue | int -}}
{{- else if hasSuffix "G" $memLimit -}}
  {{- $memValue := trimSuffix "G" $memLimit | float64 -}}
  {{- $memMi = mul $memValue 1024 | int -}}
{{- else if hasSuffix "M" $memLimit -}}
  {{- $memValue := trimSuffix "M" $memLimit | float64 -}}
  {{- $memMi = $memValue | int -}}
{{- else if hasSuffix "Ki" $memLimit -}}
  {{- $memValue := trimSuffix "Ki" $memLimit | float64 -}}
  {{- $memMi = div $memValue 1024 | int -}}
{{- else -}}
  {{- /* Assume bytes if no suffix */ -}}
  {{- $memBytes := $memLimit | float64 -}}
  {{- $memMi = div $memBytes 1048576 | int -}}
{{- end -}}

{{- if gt $memMi 0 -}}
  {{- $percentage := int .Values.app.jvmMemoryPercentage | default 70 -}}
  {{- if or (lt $percentage 1) (gt $percentage 90) -}}
    {{- fail (printf "jvmMemoryPercentage must be an integer between 1 and 90, got %d" $percentage) -}}
  {{- end -}}
  {{- $heapSize := div (mul $memMi $percentage) 100 -}}
  {{- printf "- -Xmx%dM" $heapSize -}}
  {{- printf "\n- -Xms%dM" $heapSize -}}
{{- end -}}
{{- end -}}
{{- end -}}