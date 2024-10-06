import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import { setBasePath } from '@beeq/core/dist/components';
import './index.css';

import { Products } from './pages';
import { Page } from './layout';

setBasePath('icons/svg');

const queryClient = new QueryClient();

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
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
    </QueryClientProvider>
  </StrictMode>,
);
