## OVERALL ASSUMPTIONS
Several basic assumptions must be made in order to properly complete this exercise.

1. There is a pipeline of some sort carrying out the CI/CD actions.
2. The applications have been compiled and thoroughly tested before the container build stage.  That would include static code analysis, dependency checks, unit tests, and any functional tests.  At this point, we can simply copy in the `.jar` (for backend) or the project directory (for frontend).
    a. The backend artifact (target/interview-1.0-SNAPSHOT.jar) should ultimately have a more stable name, otherwise the build processes will need to be updated as the versioning in `pom.xml` changes.  We certainly don't want to deploy `SNAPSHOT` to prod.
    b. Various health / status endpoints are written into the app(s) and available for use.
    b. Some of this is philosophical, for example whether the application should be *compiled* in the Dockerfile or not.  There are pros and cons to both approaches, and I choose to compile outside when possible.
3. Dockerhub is being used to store container images.
4. We have a working Kubernetes cluster.
    a. Ingress is already taken care of, so we only need to expose these services as ClusterIP.
        i. We _could_ choose to use a NodePort-type services for simplicity's sake, but that has ramifications for scalability.
        ii. Alternatively, we could use a LoadBalancer-type service to directly expose the apps to the public.
    b. Kubernetes secrets are being used.
        i. All requried secrets already exist and are provided in the production cluster.  Necessary secrets are referenced in the respective Dockerfiles.
5. Prometheus / Grafana / etc... are already running in the cluster and can quickly be updated to include these new services.

## BACKEND 
### Assumptions
See the overall assumptions section.

### Production-readiness 
#### Versioning 
Java is 1.8 w/ SpringBoot 2.2.  This is... old.  At a very minimum, the Java version should be updated to 11 for performance and security reasons.  However.  It would be better to upgrade Springboot to 2.7 (if you want or need to remain in the 2.x line) so that we could update to Java 17.  Java 21 is, unfortunately, not supported by Springboot 2.x, and moving to 3.x is likely a major undertaking.  Dependencies will need to be updated according to whichever version of Springboot is ultimately chosen.

For the purpose of this exam, I have updated Java to 11 and made the necessary changes in `pom.xml`.

#### Observability 
There are several ways to provide the desired observability and metrics.  To work with Prometheus, we'll need to add a few dependencies to the backend project and make sure they're properly configured.  At a minimum, we need to add the Springboot actuator to expose health endpoints and Micrometer to convert the metrics into things Prometheus will understand.  Then we must configure the correct endpoints in the BE via its `application.yml`.  Finally, we need to point Prometheus at the `/actuator/prometheus` endpoint of the API via its own configuration.

The most rudimentary changes to `pom.xml` and `application.yml` within the `backend` folder to demonstrate functionality have been made as part of this exercise.  Fully defining custom metrics should be a joint exercise between the SE and SRE teams.

#### Environment 
Few required environment variables are listed in the backend project.  The database URL, username, and password are all configured in the image _using local development values_.  This is simply to facilitate development work and does not reflect production values at all.  Because they are not secrets, it's relatively okay to bake them into the Dockerfile.

In production, variables can be supplied to the container/pod in a number of ways.  ENVs are the most straight-forward and, probably, common way of handling it, but there are security risks with ENVs due to their plain-text nature.  For this exercise, we're still using them to demonstrate Kubernetes secrets, but ideally we would write the app in such a way that pulls its own secrets in from an external vault, such as AWS Secrets Manager.

#### Docker Healthcheck 
Though intended to be run in Kubernetes in production, the container has a Docker-style healthcheck for local development purposes.

#### Kubernetes Checks 
- readiness:  /api//health/status - Intended to perform a few backend checks such as DB connectivity and overall app health.  On any failure, return a 500.
- liveness:   /api/welcome - The starting point mentioned in the backend README.  This should always load if the app is able to serve.
- startup:    /api/health/start - Does nothing but return 200.  This is used to on startup to ensure that Springboot is fully loaded and can serve traffic, and reduces the amount of fine-tuning necessary around the liveness and readiness checks.

## FRONTEND
### Assumptions
See the overall assumptions section.

### Production-readiness
#### Versioning
No version of Node is specified in the frontend folder, but given issues around SSL, it's likely to be prior to Node 17.  At this point, it should be refactored to work correctly under _at least_ Node 18, if not Node 22.

Accordingly, all dependencies should be updated as well due to the numerous critical- and high-severity vulnerabilities found, along with how many of the dependencies are deprecated or end-of-life.

#### Observability
In order to begin instrumenting the application, a small handful of extra packages need to be installed.  I've chosen `prom-client` and `express-prom-bundle`.  The implementation also requires `express-favicon`.  Once installed and configured (again, a rudimentary implementation is in the `frontend` folder), these expose a `/metrics` endpoint that we can easily point Prometheus at to scrape.  There are a myriad of packages that could be used; which one to go with ultimately depends on how well it integrates with the other dependencies.

However.  This is not idea for production use, as it requires we run the app with `npm start`.  Generally speaking, in production you want to deploy a statically-compiled app that's served by Nginx or another webserver.  That, unfortunately, requires wrapping the application with additional code to push metrics to Prometheus instead of allowing it to scrape, because the /metrics endpoint ceases functioning once the app is static.  For the purposes of this demonstration and non-production environments, the code works.

#### Environment
Presently there are no required environment variables.  In production we'd expect to see things like the backend API URL and related variables so that the end users can connect correctly.  Often this results in unique images per environment to account for different URLs.  It is possible to allow the application to read runtime environment variables by including another library (react-inject-env), and I recommend this approach so that the image only has to be built once no matter where or to whom it's deployed.

#### Docker Healthcheck
When run in Docker, the /metrics endpoint is used for a healthcheck.

#### Kubernetes Checks
- readiness:  /metrics - This is the endpoint created by the Prometheus instrumentation.
- liveness:   / - The landing page.
- startup:    / - The landing page.

We use '/' as both our startup and liveness probes since there is little to ensuring the running state of a web app.  For readiness we poke the newly-created `/metrics` endpoint that our instrumentation exposes.  As long as the Prometheus client can generate stats, this endpoint should return without errors.

If fully productionized with a statically-compiled application, the checks will need to be updated to reflect Nginx rather than `npm`.