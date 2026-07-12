import { defineConfig, devices } from '@playwright/test';

// E2e runs against the production build (vite preview), so it exercises the
// real bundles. Happy paths hit the live Rick and Morty API — public, no key.
export default defineConfig({
  testDir: './e2e',
  // Serial on purpose: parallel workers burst-requesting the public API get
  // rate limited, which shows up as flaky ErrorStates. The suite is small.
  workers: 1,
  retries: process.env.CI ? 2 : 0,
  reporter: 'list',
  use: {
    baseURL: 'http://localhost:4173',
    trace: 'on-first-retry',
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
  webServer: {
    command: 'npm run build && npm run preview',
    url: 'http://localhost:4173',
    reuseExistingServer: !process.env.CI,
  },
});
