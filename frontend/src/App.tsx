import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { useRef } from 'react'

import { ErrorBoundary } from './components/ErrorBoundary/ErrorBoundary'
import { MainPage } from './pages/MainPage/MainPage'

export const App = () => {
  const queryClientRef = useRef(new QueryClient())

  return (
    <ErrorBoundary>
      <QueryClientProvider client={queryClientRef.current}>
        <MainPage />
      </QueryClientProvider>
    </ErrorBoundary>
  )
}
