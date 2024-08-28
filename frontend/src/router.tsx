import React, { Suspense } from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import { Home } from './routes/Home';
import { NotFound404 } from './routes/NotFound';

// lazy loading of route components to be added here


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
    ]
  }
]);

export default router;
