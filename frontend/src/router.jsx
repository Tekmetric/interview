import { createBrowserRouter } from 'react-router-dom';
import RootLayout from './layouts/RootLayout';
import ErrorBoundary from './layouts/ErrorBoundary';
import SearchPage from './pages/SearchPage';
import CollectionPage from './pages/CollectionPage';
import NotFoundPage from './pages/NotFoundPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <RootLayout />,
    errorElement: <ErrorBoundary />,
    children: [
      { index: true, element: <SearchPage /> },
      { path: 'collection', element: <CollectionPage /> },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
]);
