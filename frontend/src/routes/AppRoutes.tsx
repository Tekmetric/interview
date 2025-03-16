import { createBrowserRouter, RouterProvider } from 'react-router';
import { lazy, Suspense } from 'react';

import ProductsSkeleton from '../components/ProductsGrid/ProductsSkeleton.tsx';
import ProductSkeleton from '../components/ProductDetail/ProductSkeleton.tsx';
import NotFoundSkeleton from '../components/NotFound/NotFoundSkeleton.tsx';
import CartSkeleton from '../components/CartDetail/CartSkeleton.tsx';
import Layout from '../layouts/Layout.tsx';

const ProductsPage = lazy(() => import('./ProductsPage/ProductsPage.tsx'));
const NotFoundPage = lazy(() => import('./NotFoundPage/NotFoundPage.tsx'));
const ProductPage = lazy(() => import('./ProductPage/ProductPage.tsx'));
const CartPage = lazy(() => import('./CartPage/CartPage.tsx'));

const routes = [
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
        element: (
          <Suspense fallback={<NotFoundSkeleton />}>
            <NotFoundPage />
          </Suspense>
        ),
      },
    ],
  },
];

const AppRoutes = () => {
  const router = createBrowserRouter(routes);

  return <RouterProvider router={router} />;
};

export default AppRoutes;
