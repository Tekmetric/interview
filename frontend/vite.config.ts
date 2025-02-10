/// <reference types="vitest" />

// @ts-expect-error https://github.com/tailwindlabs/tailwindcss/discussions/16250
import tailwindcss from '@tailwindcss/vite';
import { TanStackRouterVite } from '@tanstack/router-plugin/vite';
import react from '@vitejs/plugin-react-swc';
import tsconfigPaths from 'vite-tsconfig-paths';
import { defineConfig } from 'vitest/config';

// https://vitejs.dev/config/
export default defineConfig({
  base: '/',
  plugins: [tsconfigPaths(), TanStackRouterVite({ autoCodeSplitting: true }), tailwindcss(), react()],
  server: {
    port: 3000,
    open: true
  },
  test: {
    globals: true,
    environment: 'happy-dom',
    setupFiles: ['./src/setupTests.ts'],
    css: true,
    reporters: ['verbose'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      include: ['src/**/*'],
      exclude: []
    }
  }
});
