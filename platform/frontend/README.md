# Interview-UI
A brief guide on building and deploying the Interview-UI

## Table of Contents
1. [Requirements and Assumptions](#requirements-and-assumptions)
1. [Usage](#usage)
1. [Production](#production)
1. [To-Do](#to-do)

## Requirements and Assumptions
Your workstation must have the following installed and configured:
1. Node.js (18 or newer)
1. NPM
1. Docker (or Minikube; either works)
    1. If using Minikube, the required secrets for the image repository (tekmetric-dockerhub) and application exist and are correct.
    1. If using Minikube, you are already logged into the image repository at the command-line level on your workstation.

Furthermore, it is assumed that you have already properly configured the application to talk to the interview-api, either through the use of `.env` or within the source code itself, and that you are at least passingly familiar with the use of the Docker and Kubernetes command-line utilities and are comfortable running containers.

How to configure and manage these requirements is not within the scope of this document.

## Usage
### Developing in Docker
It is entirely possible to run the dev mode of the app within a container.  To do so, run the following commands from the root of the repository:
```
docker run --name ui-dev \
  -it \
  -p 3000:3000 \
  -v <path-to-the-repo>/frontend:/app \
  node:18 \
  /bin/bash
```

This will deposit your shell session inside the running container called `ui-dev`.  Once inside, you must run a few more commands to start the app and begin testing.
```
cd /app
npm install
npm start
```

At this point, you can switch to your browser and navigate to `http://localhost:3000`.  You should see the homepage of the app.

Since you're running in dev mode, you'll be able to get hot refreshes as you edit soruce files, which you will **not** get if you build the app as described below.

### Building the Application Locally
Change to the `frontend` directory.  This assumes you are beginning in `/platform/frontend`, where this README lives, not the root of the repository.
```
cd ../../frontend
```

Build the application.
```
npm install
npm build
```

Return to the _root_ directory and build the container image.  Replace `<MY_BRANCH>` in the command below with something easily recognizable (**not** "latest"!), such as a shortened branch name or your own initials.  Branch names are great since they help you keep track of what's in a given image.
```
cd ../
docker build -t tekmetric/interview-ui:<MY_BRANCH> -f platform/frontend/Dockerfile .
```

### Running in Docker
All you need to do is execute the following command:
```
docker run --name interview-ui -p 3000:3000 -d tekmetric/interview-ui:<MY_BRANCH>
```

Now, open your browser and navigate to `http://localhost:3000`.  You should see the UI welcome screen.  At `http://localhost:3000/metrics` you'll see the metrics as presented to Prometheus.

### Running in Minikube
If you wish to use Minikube, you must first push the image up to a public repository.  Unfortunately, Minikube cannot use images that exist on your machine already because it is essentially a VM with no storage connection.

Push the image to Dockerhub:
```
docker push tekmetric/interview-ui:<MY_BRANCH>
```

Make sure Minikube is started:
```
minikube start
```

Deploy the application:
```
kubectl apply -f platform/frontend/interview-ui.yml
```

Expose the service:
```
minikube service interview-ui --url
```

Copy the IP address and port returned by the previous command.  It should look something like `http://192.168.49.2:#####`.  

Load the URL you just copied into your browser, as such `http://192.168.49.2:32701/`.

## Production
Production deployments can be handled via the `interview-ui.yml` file, and should be automated through the CI/CD system.

## To-Do
1. Update the app to work under **at least** Node 18.  Because of how it's currently written, we have to enable insecure SSL settings during the build step.  These cannot be allowed to persist in production.
1. Update all libraries and dependencies.  At the time of this posting (2025-10-08), there were 31 critical vulnerabilities and 64 high reported by `npm install`.  Many of the required packages are deprecated or end-of-life.
1. Wrap the code with a Prometheus push library so that the app can be statically compiled and served through Nginx or another webserver instead of relying on `npm start` in production.  This may require a significant refactoring of the code, but will produce a smaller, faster, more secure container image.
1. Implement a strategy for loading environment variables at runtime so that we do not have to compile different images for each environment.