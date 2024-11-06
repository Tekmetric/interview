# Tech Interview Project

## Steps to get started

#### Fork the repository and clone it locally

- <https://github.com/Tekmetric/interview.git>

#### Let's install the project locally

`npm install`

#### Let's start the project locally

`npm start`

### Goals

1. Fetch Data from the backend Crud API you created or from a public API
2. Display data from API onto your page (Table, List, etc.)
3. Apply a styling solution of your choice to make your page look different (CSS, SASS, CSS-in-JS)
4. Have fun

### Submitting your coding exercise

Once you have finished the coding exercise please create a PR into Tekmetric/interview

### Building and testing docker image

Upon code push to master branch github action is ran that will build docker image and push it to ghcr.io registry.

#### Building and running image locally

- in backend directory run `docker build .` this will build image which you will need to run with `docker run --expose 3000 -p 3000:3000 $imageid`
- you can also build and run image using `docker compose up frontend`

#### Running the container in kubernetes

If you want to run app in kubernetes make sure you have correct context set and `helm` installed. Once you verified that helm is installed you can just run `helm install frontend .` in this folder

