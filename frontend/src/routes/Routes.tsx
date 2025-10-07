import React, { FC } from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import MainPage from '../containers/MainPage/MainPage';

export const Router: FC = () => {
  const router = createBrowserRouter([
    {
      path: '/all-animes',
      element: <MainPage />,
    },
    {
      path: '/about',
      element: <div>About</div>,
    },
    {
      path: '/',
      element: <MainPage />,
    },
    {
      path: '*',
      element: <div>Not Found</div>,
    },
  ]);
  return <RouterProvider router={router} />;
};

export default Router;
