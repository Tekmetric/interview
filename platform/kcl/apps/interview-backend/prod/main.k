import pkg.app

backend: app.App {
    labels: { "app.kubernetes.io/instance" = "prod" }
    replicas: 2
    hostname: "interview.tekmetric.com"
}