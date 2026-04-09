# Carl Gustas - SRE Interview Submission 

## Prerequisites
- Docker Desktop 4.65.0 (with built-in Kubernetes runing)
- kubectl / k9s
- Helm v4.0.0
- DockerHub credential set in environment variables

## Steps to build and deploy 
1. First build the Docker image by running `docker build -t interview-backend:latest backend/ `
2. Install the Helm chart `helm install interview-backend ./sre/helm/ --set image.repository=interview-backend --set image.tag=latest`
3. Create a local DNS override by running `echo "127.0.0.1 interview-backend.local" | sudo tee -a /etc/hosts`
4. Verify that the interview-backend and interview-backend-nginx-controller pods are both in a running state. `kubectl get pods -n default` . Proceed to next step once both are in a Running state. 
5. Verify that the interview-backend is responding by issuing `curl http://interview-backend.local/api/welcome -w "\n"`
6. Verify that the Spring Boot Actuator is responding with application metrics by issuing `curl http://interview-backend.local/actuator/health -w "\n"`


Thanks for your time and consideration while reviewing this project. 