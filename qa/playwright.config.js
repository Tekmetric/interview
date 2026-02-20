const { defineConfig } = require('@playwright/test');
const { env } = require('./config/env');

module.exports = defineConfig({
  testDir: './tests',
  fullyParallel: true,
  timeout: 45_000,
  expect: {
    timeout: 8_000
  },
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 2 : undefined,
  reporter: [
    ['list'],
    ['html', { open: 'never' }]
  ],
  use: {
    baseURL: env.uiBaseUrl,
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    viewport: { width: 1440, height: 900 }
  }
});
