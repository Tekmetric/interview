import { lazy, Suspense } from 'react';
import { createBrowserRouter } from 'react-router-dom';
import RootLayout from './layouts/RootLayout';
import ErrorBoundary from './layouts/ErrorBoundary';
import SearchPage from './pages/SearchPage';
import CollectionPage from './pages/CollectionPage';
import NotFoundPage from './pages/NotFoundPage';
import Spinner from './components/Spinner';

// Code-split: the essay page is only fetched when visited.
const HowIBuiltThisPage = lazy(() => import('./pages/HowIBuiltThisPage'));

export const router = createBrowserRouter([
  {
    path: '/',
    element: <RootLayout />,
    errorElement: <ErrorBoundary />,
    children: [
      { index: true, element: <SearchPage /> },
      { path: 'collection', element: <CollectionPage /> },
      {
        path: 'how-i-built-this',
        element: (
          <Suspense fallback={<Spinner labelKey="spinner.loadingPage" />}>
            <HowIBuiltThisPage />
          </Suspense>
        ),
      },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
]);
