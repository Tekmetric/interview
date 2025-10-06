import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import './i18n'; // Initialize i18n
import App from './App';
import ErrorBoundary from './components/ErrorBoundary';
import { ThemeProvider } from './contexts/ThemeContext';

ReactDOM.render(
  <ErrorBoundary>
    <ThemeProvider>
      <App />
    </ThemeProvider>
  </ErrorBoundary>,
  document.getElementById('root')
);

 