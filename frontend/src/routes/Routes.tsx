import React, { FC } from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router';

import MainPage from '../containers/MainPage/MainPage';
import AnimeListing from '../containers/AnimeListing/AnimeListing';
import NotFound from '../containers/NotFound/NotFound';

export const Router: FC = () => {
  const router = createBrowserRouter([
    {
      path: '/all-animes',
      element: <MainPage />,
    },
    {
      path: '/anime/:id',
      element: <AnimeListing />,
    },
    {
      path: '/',
      element: <MainPage />,
    },
    {
      path: '*',
      element: <NotFound />,
    },
  ]);
  return <RouterProvider router={router} />;
};

export default Router;
