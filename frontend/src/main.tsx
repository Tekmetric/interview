import { StrictMode } from 'react';
import '@mui/material/styles/styled';
import { BrowserRouter as Router } from 'react-router-dom';
import { Provider } from 'react-redux';
import { createRoot } from 'react-dom/client';
import App from './App.tsx';
import './index.css';
import './utils/i18n.ts';
import store from './store';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Provider store={store}>
      <Router>
        <App />
      </Router>
    </Provider>
  </StrictMode>,
);
