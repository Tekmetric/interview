# Frontend Application


## Features

- **Prometheus Metrics**: Built-in metrics endpoint (`/metrics`) for monitoring HTTP requests and server performance
- **Health Checks**: Lightweight health check endpoint (`/healthz`) for Kubernetes readiness/liveness probes

## Getting Started

#### Fork the repository and clone it locally
- https://github.com/Tekmetric/interview.git

#### Install dependencies
```bash
npm install
```

#### Start the development server
```bash
npm start
```

This will start the Express server on port 3001 and proxy to the React dev server on port 3000.

#### Alternative start commands
- `npm run start:react` - Start only the React dev server (port 3000)
- `npm run start:dev` - Start both React dev server and Express server concurrently
- `npm run build` - Build the production bundle
- `npm run start:react:prod` - Build and start in production mode

## Development Goals

1. Fetch Data from the backend CRUD API you created or from a public API
2. Display data from API onto your page (Table, List, etc.)
3. Apply a styling solution of your choice to make your page look different (CSS, SASS, CSS-in-JS)
4. Have fun

## Submitting your coding exercise

Once you have finished the coding exercise please create a PR into Tekmetric/interview