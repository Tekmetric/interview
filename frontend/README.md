# Tech Interview Project  - Red Panda project - Node js backend

## Steps to get started:


### Let's install the project locally
`yarn install`

### Let's start the project locally
`yarn dev`

#### Let's run unit tests locally
`yarn test`

### Goals
1. Fetch Data from the backend Crud API you created or from a public API
2. Display data from API onto your page (Table, List, etc.)
3. Apply a styling solution of your choice to make your page look different (CSS, SASS, CSS-in-JS)
4. Have fun

### Submitting your coding exercise
Once you have finished the coding exercise please create a PR into Tekmetric/interview

### Had my fun building an app for tracking red pandas
I might have gone a bit further away from the initial goal of this excercise, but I had some ideas I wanted to experiment with.

The code is not perfect, it was a lot to fit everything in 5 days of work so I sometimes rushed more than I would have liked to, because I did not want to exceed the inital 1 week estimation.
I wanted to also integrate Redux, it's currently a work in progress, keeps some basic user data when logged in.

The signin is not wired to the backend, just input whatever looks like a valid email and a password and you will log in :D.

I have written some UI unit test examples, but again, due to time constraints, I did not manage to test all the components.

### I also built a basic nodejs backed
Please find it in the /pandaAPI folder

#### To run the backend
`cd ./pandaApi/backend`

`npm i`

`node index.js`
