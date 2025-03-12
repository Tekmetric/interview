import { createBrowserRouter, RouterProvider } from 'react-router';
import { lazy, Suspense } from 'react';
import Layout from '../layouts/Layout.tsx';

const ProductsPage = lazy(() => import('./ProductsPage/ProductsPage.tsx'));

const AppRoutes = () => {
  const router = createBrowserRouter([
    {
      path: '/',
      element: <Layout />,
      children: [
        {
          index: true,
          element: (
            <Suspense fallback={<div>Loading...</div>}>
              <ProductsPage />
            </Suspense>
          ),
        },
        {
          path: '/cart',
          element: <div>Cart</div>,
        },
      ],
    },
  ]);

  return <RouterProvider router={router} />;
};

export default AppRoutes;
