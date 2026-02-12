import React from 'react';
import { createRoot } from 'react-dom/client';
import { Provider } from 'react-redux';
import { store } from './store/store';
import './index.css';
import './i18n'; // Initialize i18n
import App from './App';
import { GranularErrorBoundary } from './components/errors/GranularErrorBoundary/GranularErrorBoundary';
import { initMonitoring, captureError } from './lib/monitoring';

// Initialize Sentry monitoring (only runs in production)
initMonitoring();

const container = document.getElementById('root');
if (!container) throw new Error('Failed to find the root element');

const root = createRoot(container);

root.render(
  <Provider store={store}>
    <GranularErrorBoundary
      componentName="App"
      severity="critical"
      onError={(error, errorInfo) => {
        // Send to Sentry
        captureError(error, {
          componentStack: errorInfo.componentStack,
          severity: 'critical',
        });
      }}
    >
      <App />
    </GranularErrorBoundary>
  </Provider>
);
