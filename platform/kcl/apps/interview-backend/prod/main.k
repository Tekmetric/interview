import pkg.app

backend: app.App {
    labels: { "app.kubernetes.io/instance" = "prod" }
    replicas: 3
    hostname: "interview.tekmetric.com"
    antiaffinity_enabled: True
}
