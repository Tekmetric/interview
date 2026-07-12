import { render } from '@testing-library/react';
import type { ReactElement, ReactNode } from 'react';
import { MemoryRouter } from 'react-router';

import { AppProviders } from '../app/providers';
import { makeStore, type RootState } from '../app/store';

interface RenderWithProvidersOptions {
  preloadedState?: Partial<RootState>;
  route?: string;
}

// Renders a component inside the full provider stack (fresh store per test,
// memory router) and returns the store for asserting on state.
export function renderWithProviders(
  ui: ReactElement,
  { preloadedState, route = '/' }: RenderWithProvidersOptions = {},
) {
  const store = makeStore(preloadedState);

  function Wrapper({ children }: { children: ReactNode }) {
    return (
      <AppProviders store={store}>
        <MemoryRouter initialEntries={[route]}>{children}</MemoryRouter>
      </AppProviders>
    );
  }

  return { store, ...render(ui, { wrapper: Wrapper }) };
}
