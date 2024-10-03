import { defineConfig } from 'vite';
import { viteStaticCopy } from 'vite-plugin-static-copy';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react(),
    /**
     * Icon SVG files get served under the URL:
     * http(s)://<base_url>/icons/svg/
     */
    viteStaticCopy({
      targets: [
        {
          src: './node_modules/@beeq/core/dist/beeq/svg/*',
          dest: 'icons/svg',
        },
      ],
    }),
  ],
});
