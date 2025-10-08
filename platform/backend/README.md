# Interview-API
A brief guide on building and deploying the Interview-API

## Table of Contents
1. [Requirements and Assumptions](#requirements-and-assumptions)
1. [Usage](#usage)
1. [Production](#production)
1. [To-Do](#to-do)

## Requirements and Assumptions
Your workstation must have the following installed and configured:
1. Java 11 JDK (or the equivalent)
1. Maven 
1. Docker (or Minikube; either works)
    1. If using Minikube, the required secrets for the image repository (tekmetric-dockerhub) and application exist and are correct.
    1. If using Minikube, you are already logged into the image repository at the command-line level on your workstation.

Furthermore, it is assumed that you are at least passingly familiar with the use of the Docker and Kubernetes command-line utilities and are comfortable running containers.

How to configure and manage these requirements is not within the scope of this document.

## Usage
### Building the Application Locally
Change to the `backend` directory.  This assumes you are beginning in `/platform/backend`, where this README lives, not the root of the repository.
```
cd ../../backend
```

Build the application.
```
mvn package
```

Return to the _root_ directory and build the container image.  Replace `<MY_BRANCH>` in the command below with something easily recognizable (**not** "latest"!), such as a shortened branch name or your own initials.  Branch names are great since they help you keep track of what's in a given image.
```
cd ../
docker build -t tekmetric/interview-api:<MY_BRANCH> -f platform/backend/Dockerfile .
```

### Running in Docker
All you need to do is execute the following command:
```
docker run --name interview-api -p 8080:8080 -d tekmetric/interview-api:<MY_BRANCH>
```

The default environment variables provided should work correctly for the configured H2 database.  If they are not correct, you can replace them with the correct values in the docker command as such:
```
docker run --name interview-api \
  -p 8080:8080 \
  -e DATABASE_URL='<NEW_VALUE>' \
  -e DATABASE_USER='<NEW_USER>' \
  -e DATABASE_PASS='<NEW_PASS>' \
  -d \
  tekmetric/interview-api:<MY_BRANCH>
```

Now, open your browser and navigate to `http://localhost:8080/api/welcome`

### Running in Minikube
If you wish to use Minikube, you must first push the image up to a public repository.  Unfortunately, Minikube cannot use images that exist on your machine already because it is essentially a VM with no storage connection.

Push the image to Dockerhub:
```
docker push tekmetric/interview-api:<MY_BRANCH>
```

Make sure Minikube is started:
```
minikube start
```

Edit the _local_ yaml (`interview-api.local.yml`) and update the values for DATABASE_URL, DATABASE_USER, and DATABASE_PASS (currently defined as <SET_ME> in the block from lines 24-30) with appropriate values for the application.  Save the changes.

Deploy the application:
```
kubectl apply -f platform/backend/interview-api.local.yml
```

Expose the service:
```
minikube service interview-api --url
```

Copy the IP address and port returned by the previous command.  It should look something like `http://192.168.49.2:#####`.  

Load the URL you just copied into your browser, as such `http://192.168.49.2:32700/api/welcome`.

## Production
Production deployments can be handled via the `interview-api.yml` file, and should be automated through the CI/CD system.

## To-Do
1. Provide a Docker compose file for faster local startup.
1. Update Java from 8 to at least 11, if not 17 (11 is reflected in changes to `pom.xml`)
1. Update Springboot and its components to newer versions.
1. Tune resource requests for CPU and memory to ensure timely startup without constraining other deployments.