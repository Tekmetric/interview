import type { ReactElement } from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render } from '@testing-library/react';

export function renderWithQueryClient(ui?: any) {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false
      }
    }
  });

  const { rerender, ...result } = render(
    <QueryClientProvider client={ queryClient }>{ui}</QueryClientProvider>
  );

  return {
    ...result,
    rerender: (rerenderUi: ReactElement) =>
      rerender(
        <QueryClientProvider client={ queryClient }>{rerenderUi}</QueryClientProvider>
      )
  };
}
