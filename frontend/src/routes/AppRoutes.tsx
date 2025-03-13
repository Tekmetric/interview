import { createBrowserRouter, RouterProvider } from 'react-router';
import { lazy, Suspense } from 'react';
import Layout from '../layouts/Layout.tsx';
import Loader from '../components/Loader/Loader.tsx';
import ProductSkeleton from '../components/ProductDetail/ProductSkeleton.tsx';

const ProductsPage = lazy(() => import('./ProductsPage/ProductsPage.tsx'));
const ProductPage = lazy(() => import('./ProductPage/ProductPage.tsx'));

const AppRoutes = () => {
  const router = createBrowserRouter([
    {
      path: '/',
      element: <Layout />,
      children: [
        {
          index: true,
          element: (
            <Suspense
              fallback={
                <div className="mt-[68px] flex h-full w-full items-center justify-center text-center">
                  <Loader />
                </div>
              }
            >
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
          element: <div>Cart</div>,
        },
      ],
    },
  ]);

  return <RouterProvider router={router} />;
};

export default AppRoutes;
