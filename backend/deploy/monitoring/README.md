# 📊 Tekmetric Backend ServiceMonitor

This file defines a **Prometheus Operator ServiceMonitor** resource for scraping metrics from the Tekmetric backend service. It enables Prometheus to automatically discover and collect metrics from the backend’s `/actuator/prometheus` endpoint.

---

## 🧠 What Is a ServiceMonitor?

A **ServiceMonitor** is a custom resource (CRD) provided by the [Prometheus Operator](https://github.com/prometheus-operator/prometheus-operator).  
It tells Prometheus **what services to monitor** and **how to scrape their metrics**.  

Without a ServiceMonitor, Prometheus would need manual configuration (via static scrape configs).  
With a ServiceMonitor, Prometheus dynamically discovers targets in Kubernetes based on **labels**.

---

**Installed automatically when deploying your backend via Helm**

---

## ⚙️ Configuration Overview

```yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: tekmetric-backend-monitor
  labels:
    release: prometheus
spec:
  selector:
    matchLabels:
      app: tekmetric-backend
  namespaceSelector:
    any: true
  endpoints:
    - port: http
      path: /actuator/prometheus
      interval: 15s
      honorLabels: true
```