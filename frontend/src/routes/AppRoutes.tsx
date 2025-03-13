import { createBrowserRouter, RouterProvider } from 'react-router';
import { lazy, Suspense } from 'react';
import Layout from '../layouts/Layout.tsx';
import Loader from '../components/Loader/Loader.tsx';

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
            <Suspense fallback={<Loader />}>
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
