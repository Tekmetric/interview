import { Provider } from 'react-redux';
import React from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App';
import { ToastProvider } from './components/toast/ToastProvider';
import { store } from './store';
import { ThemeProvider } from './theme/ThemeProvider';

const container = document.getElementById('root');
if (!container) {
  throw new Error('Root element #root not found');
}

document.getElementById('app-shell')?.removeAttribute('aria-busy');

const root = createRoot(container);
root.render(
  <React.StrictMode>
    <ThemeProvider>
      <Provider store={store}>
        <ToastProvider>
          <App />
        </ToastProvider>
      </Provider>
    </ThemeProvider>
  </React.StrictMode>
);
