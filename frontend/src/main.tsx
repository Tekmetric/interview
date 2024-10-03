import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { setBasePath } from '@beeq/core/dist/components';

import App from './App.tsx';
import './index.css';

setBasePath('icons/svg');

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
