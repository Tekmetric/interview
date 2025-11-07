const express = require('express');
const { Registry, Counter, Gauge } = require('prom-client');
const path = require('path');
const fs = require('fs');

// Create a Registry to register the metrics
const register = new Registry();

// Add default metrics (CPU, memory, etc.)
const collectDefaultMetrics = require('prom-client').collectDefaultMetrics;
collectDefaultMetrics({ register });

// Custom metrics
const httpRequestTotal = new Counter({
  name: 'http_requests_total',
  help: 'Total number of HTTP requests',
  labelNames: ['method', 'route', 'status_code'],
  registers: [register],
});

const httpRequestInProgress = new Gauge({
  name: 'http_requests_in_progress',
  help: 'Number of HTTP requests currently in progress',
  labelNames: ['method', 'route'],
  registers: [register],
});

const app = express();

// Middleware to track request metrics
const metricsMiddleware = (req, res, next) => {
  // Normalize route path (remove query params, trailing slashes)
  let route = req.path;
  // Normalize route for metrics (group similar routes)
  if (route !== '/metrics' && route !== '/healthz' && route !== '/') {
    // For API-like routes, keep the pattern
    route = route.replace(/\/\d+/g, '/:id'); // Replace numeric IDs with :id
  }
  
  // Increment in-progress gauge
  httpRequestInProgress.inc({ method: req.method, route: req.path });

  // Override res.end to capture response metrics
  const originalEnd = res.end;
  res.end = function (...args) {
    const statusCode = res.statusCode || 200;
    
    // Record metrics with normalized route
    httpRequestTotal.inc({ method: req.method, route, status_code: statusCode });
    httpRequestInProgress.dec({ method: req.method, route: req.path });
    
    // Call original end
    originalEnd.apply(this, args);
  };
  
  next();
};

app.use(metricsMiddleware);

// Health check endpoint
app.get('/healthz', (req, res) => {
  res.status(200).end();
});

// Prometheus metrics endpoint
app.get('/metrics', async (req, res) => {
  try {
    res.set('Content-Type', register.contentType);
    const metrics = await register.metrics();
    res.end(metrics);
  } catch (ex) {
    res.status(500).end(ex);
  }
});

// Check if we're in production (serving built files)
const isProduction = process.env.NODE_ENV === 'production';
const buildPath = path.join(__dirname, 'build');

if (isProduction && fs.existsSync(buildPath)) {
  // Serve static files from the React app build folder
  app.use(express.static(buildPath));
  
  // Log and serve root path
  app.get('/', (req, res) => {
    console.log(`[${new Date().toISOString()}] GET / - IP: ${req.ip || req.connection.remoteAddress}`);
    res.sendFile(path.join(buildPath, 'index.html'));
  });
  
  // Serve React app for all other non-API routes (catch-all route)
  // Use app.use() instead of app.get() for Express 5 compatibility
  app.use((req, res) => {
    res.sendFile(path.join(buildPath, 'index.html'));
  });
} else {
  // Development mode - log root path access
  app.get('/', (req, res, next) => {
    console.log(`[${new Date().toISOString()}] GET / - IP: ${req.ip || req.connection.remoteAddress}`);
    next();
  });
}
const PORT = process.env.PORT || 3001;

const server = app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
  console.log(`Metrics endpoint: http://localhost:${PORT}/metrics`);
  console.log(`Health check: http://localhost:${PORT}/healthz`);
  
  if (!isProduction) {
    console.log('\nNote: Running in development mode. Make sure react-scripts dev server is running on port 3000.');
    console.log('Start it with: npm run start:react');
    console.log('Or run both together with: npm run start:dev');
  }
});
