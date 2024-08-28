import React, { lazy, Suspense } from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import { Home } from './routes/Home';
import { NotFound404 } from './routes/NotFound';

// lazy loading of route components to be added here
const Dashboard = lazy(() => {
  return import('./routes/Dashboard').then(({ Dashboard }) => ({ default: Dashboard }));
});

const router = createBrowserRouter([
  {
    path: '/',
    errorElement: <NotFound404 />,
    element: <Home />,
    children: [
      {
        path: '/',
        element: (
          <Suspense>
            <Navigate to="/dashboard" replace />
          </Suspense>
        )
      },
      {
        path: '/dashboard',
        element: (
          <Suspense>
            <Dashboard />
          </Suspense>
        )
      }
    ]
  }
]);

export default router;
