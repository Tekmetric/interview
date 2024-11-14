import pkg.app

backend: app.App {

    name = "interview"
    pathPrefix = "/api"

    labels = {
        "app.kubernetes.io/name" = "interview-backend"
    }

    annotations = { 
        "app.kubernetes.io/version" = option("tag")
    }

    containers = {
        "backend" = {
            image = { 
                name: "tekmetric/interview-backend"
                tag: option("tag")
            }
            ports = [{
                name = "http"
                containerPort = 8080
            }]

            resources.requests = {
                cpu = 1
                memory = 512Mi
            }
            resources.limits = resources.requests
        }
    }

    service = {
        $type = "ClusterIP"
        ports = [
            {
                name: "http"
                port: 80
                targetPort = "http"
            }
        ]

    }
}
