import pkg.app

backend: app.App {
    labels: { "app.kubernetes.io/instance" = "prod" }
    replicas: 3
    hostname: "interview.tekmetric.com"
    antiaffinity_enabled: True

    databaseConfig: {
        url: "jdbc:h2:mem:testdb"
        driverClassName: "org.h2.Driver"
        username: "sa"
        password: "ref+echo://password"
    }
}
