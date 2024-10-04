import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { setBasePath } from '@beeq/core/dist/components';
import './index.css';

import { Products } from './pages';
import { Page } from './layout';

setBasePath('icons/svg');

const router = createBrowserRouter([
  {
    path: '/',
    element: <Page />,
    children: [
      {
        path: '',
        element: <Products />,
      },
      {
        path: '/:category',
        element: <Products />,
      },
    ],
  },
]);

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>,
);
