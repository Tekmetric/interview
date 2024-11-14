import manifests

import .app

# Convert the `App` model into Kubernetes Deployment and Service Manifests
kubernetesRender = lambda a: app.App {
    # Construct the deployment manifest.
    deployment = {
        apiVersion = "apps/v1"
        kind = "Deployment"
        metadata.name = a.name
        metadata.labels = a.labels
        metadata.annotations = a.annotations
        spec = {
            replicas = a.replicas
            selector.matchLabels = a.labels
            template.metadata.labels = a.labels
            template.spec.containers = [
                {
                    name = cname
                    image = c.image.name + ":" + c.image.tag
                    command = c.command
                    args = c.args
        #             env = c.env
                    resources = c.resources
                    ports = c.ports
                    if c.spring_actuator_health_enabled:
                        startupProbe = {
                            httpGet = {
                                path: "/actuator/health/liveness"
                                port: "http"
                            }
                            failureThreshold: 10
                            periodSeconds: 5
                        }
                        readinessProbe = {
                            httpGet = {
                                path: "/actuator/health/readiness"
                                port: "http"
                            }
                            periodSeconds: 1
                            failureThreshold: 3
                        }
                        livenessProbe = {
                            httpGet = {
                                path: "/actuator/health/liveness"
                                port: "http"
                            }
                            periodSeconds: 5
                            failureThreshold: 1
                        }
                } for cname, c in a.containers
            ]
        }
    }
    # Construct the service manifest.
    service = {
        apiVersion = "v1"
        kind = "Service"
        metadata.name = a.name
        metadata.labels = a.labels
        spec = {
            type = a.service?.$type
            selector = a.labels
            ports = a.service?.ports
        }
    }

    ingressRoute = {
        apiVersion = "traefik.containo.us/v1alpha1"
        kind = "IngressRoute"
        metadata.name = a.name
        metadata.labels = a.labels
        spec.entryPoints = ["web"]
        spec.routes = [
            {
                kind = "Rule"
                match = "Host(`{}`) && PathPrefix(`{}`)".format(a.hostname, a.pathPrefix)
                services: [
                    {
                        kind: "Service"
                        name: a.name
                        port: "http"
                        scheme: "http"
                    }
                ]
            }
        ]

    }


    # Returns Kubernetes manifests
    [deployment, service, if a.hostname: ingressRoute]
}

manifests.yaml_stream(sum([kubernetesRender(a) for a in app.App.instances()], []))