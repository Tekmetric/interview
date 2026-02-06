// Test utilities for React Query testing
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render as rtlRender, RenderOptions } from '@testing-library/react';
import { ReactElement } from 'react';

// Create a custom render function that includes React Query provider
export function renderWithQueryClient(
  ui: ReactElement,
  {
    queryClient = new QueryClient({
      defaultOptions: {
        queries: {
          retry: false,
          staleTime: Infinity,
        },
        mutations: {
          retry: false,
        },
      },
    }),
    ...renderOptions
  }: { queryClient?: QueryClient } & Omit<RenderOptions, 'wrapper'> = {}
) {
  function Wrapper({ children }: { children: React.ReactNode }) {
    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
  }

  return rtlRender(ui, { wrapper: Wrapper, ...renderOptions });
}

export * from '@testing-library/react';
export { renderWithQueryClient as render };
