/// <reference types="vitest/config" />
import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    setupFiles: './src/test/setup.ts',
    css: false,
    // Unit tests are *.test.* under src; e2e specs (*.spec.*) belong to Playwright.
    include: ['src/**/*.test.{ts,tsx}'],
  },
});
