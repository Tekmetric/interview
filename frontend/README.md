# Tech Interview Project

## Steps to get started:

#### Fork the repository and clone it locally

- https://github.com/Tekmetric/interview.git

#### Let's install the project locally

`yarn install`

#### Let's start the project locally

json-server is used to mock a backend server. The server is running on port 3000 and the client is running on port 5173.

To start client: `yarn client`
To start server: `yarn server`

#### Configure the project

You can create .env.development.local file in the root of the project and add the following:

```
API_TARGET=http://localhost:3000
NETWORK_DELAY=2000
```

### Goals

1. Fetch Data from the backend Crud API you created or from a public API
2. Display data from API onto your page (Table, List, etc.)
3. Apply a styling solution of your choice to make your page look different (CSS, SASS, CSS-in-JS)
4. Have fun

### Submitting your coding exercise

Once you have finished the coding exercise please create a PR into Tekmetric/interview
