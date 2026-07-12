import '@testing-library/jest-dom/vitest';
import { cleanup } from '@testing-library/react';
import { afterAll, afterEach, beforeAll } from 'vitest';

import { server } from './msw/server';

// jsdom does not implement ResizeObserver, which the virtualized grid uses
// to derive its column count. A no-op keeps the initial measurement path.
window.ResizeObserver ??= class {
  observe() {}
  unobserve() {}
  disconnect() {}
};

// jsdom does not implement matchMedia, which the theme initializer relies on.
// The stub always reports "no match" => tests default to the light theme.
window.matchMedia ??= (query: string): MediaQueryList =>
  ({
    matches: false,
    media: query,
    onchange: null,
    addEventListener: () => {},
    removeEventListener: () => {},
    addListener: () => {},
    removeListener: () => {},
    dispatchEvent: () => false,
  }) as MediaQueryList;

// Fail loudly on any request a test did not explicitly mock.
beforeAll(() => server.listen({ onUnhandledRequest: 'error' }));
afterEach(() => {
  // Testing Library only auto-cleans when Vitest globals are enabled;
  // with explicit imports the unmount must be registered by hand.
  cleanup();
  server.resetHandlers();
  window.localStorage.clear();
});
afterAll(() => server.close());
