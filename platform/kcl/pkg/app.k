import units

type Unit = units.NumberMultiplier

schema App:
    name: str
    replicas: int = 1
    labels?: {str:str}
    annotations?: {str:str}
    containers?: {str: Container}
    service?: Service
    hostname?: str
    pathPrefix: str

schema Container:
    image: Image
    resources?: Resources
    command?: [str]
    args?: [str]
    ports?: [ContainerPort]
    spring_actuator_health_enabled?: bool = True

schema Image:
    name: str
    tag: str

schema Resources:
    requests?: Resource
    limits?: Resource

schema Resource:
    cpu?: int
    memory?: Unit

schema ContainerPort:
    name?: str
    protocol: "TCP" | "UDP" = "TCP"
    # port?: int
    containerPort: int

    check:
        1 <= containerPort <= 65535, "containerPort must be between 1 and 65535, inclusive"

schema Service:
    $type?: str
    ports: [Port]

schema Port:
    name?: str
    protocol: "TCP" | "UDP" = "TCP"
    port: int
    targetPort?: int | str
