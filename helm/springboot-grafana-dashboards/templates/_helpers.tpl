{{- define "springboot-grafana-dashboards.name" -}}
springboot-grafana-dashboards
{{- end -}}

{{- define "springboot-grafana-dashboards.fullname" -}}
{{- printf "%s" (include "springboot-grafana-dashboards.name" .) -}}
{{- end -}}

