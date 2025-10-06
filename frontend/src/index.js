// Suppress Material-UI fade deprecation warning from third-party libraries
const originalWarn = console.warn;
console.warn = function(...args) {
  const message = args[0];
  if (typeof message === 'string' && message.includes('fade')) {
    return;
  }
  originalWarn.apply(console, args);
};

/* eslint-disable import/first */
import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import './i18n'; // Initialize i18n
import App from './App';
import ErrorBoundary from './components/ErrorBoundary';
import { ThemeProvider } from './contexts/ThemeContext';
/* eslint-enable import/first */

ReactDOM.render(
  <ErrorBoundary>
    <ThemeProvider>
      <App />
    </ThemeProvider>
  </ErrorBoundary>,
  document.getElementById('root')
);

 