import { createBrowserRouter, RouterProvider } from 'react-router';
import { lazy, Suspense } from 'react';

import ProductsSkeleton from '../components/ProductsGrid/ProductsSkeleton.tsx';
import ProductSkeleton from '../components/ProductDetail/ProductSkeleton.tsx';
import CartSkeleton from '../components/CartDetail/CartSkeleton.tsx';
import NotFoundPage from './NotFoundPage/NotFoundPage.tsx';
import Layout from '../layouts/Layout.tsx';

const ProductsPage = lazy(() => import('./ProductsPage/ProductsPage.tsx'));
const ProductPage = lazy(() => import('./ProductPage/ProductPage.tsx'));
const CartPage = lazy(() => import('./CartPage/CartPage.tsx'));

const AppRoutes = () => {
  const router = createBrowserRouter([
    {
      path: '/',
      element: <Layout />,
      children: [
        {
          index: true,
          element: (
            <Suspense fallback={<ProductsSkeleton />}>
              <ProductsPage />
            </Suspense>
          ),
        },
        {
          path: '/product/:id',
          element: (
            <Suspense fallback={<ProductSkeleton />}>
              <ProductPage />
            </Suspense>
          ),
        },
        {
          path: '/cart',
          element: (
            <Suspense fallback={<CartSkeleton />}>
              <CartPage />
            </Suspense>
          ),
        },
        {
          path: '*',
          element: <NotFoundPage />,
        },
      ],
    },
  ]);

  return <RouterProvider router={router} />;
};

export default AppRoutes;
