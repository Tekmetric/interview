import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useRef } from 'react';
import { MainPage } from './pages/MainPage/MainPage';
import { ErrorBoundary } from './components/ErrorBoundary/ErrorBoundary';

export const App = () => {
  const queryClientRef = useRef(new QueryClient())

  return <ErrorBoundary>
      <QueryClientProvider client={queryClientRef.current}>
      <MainPage />
    </QueryClientProvider>
  </ErrorBoundary>
}