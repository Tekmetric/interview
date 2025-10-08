import React, { FC } from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router';

import MainPage from '../containers/MainPage/MainPage';
import AnimeListing from '../containers/AnimeListing/AnimeListing';
import NotFound from '../containers/NotFound/NotFound';
import { ROUTES } from './Routes.const';

export const Router: FC = () => {
  const router = createBrowserRouter([
    {
      path: ROUTES.ALL_ANIME,
      element: <MainPage />,
    },
    {
      path: ROUTES.ANIME_LISTING,
      element: <AnimeListing />,
    },
    {
      path: ROUTES.HOME,
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
