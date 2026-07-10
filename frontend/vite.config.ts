import { reactRouter } from "@react-router/dev/vite";
import tailwindcss from "@tailwindcss/vite";
import { defineConfig } from "vitest/config";
import tsconfigPaths from "vite-tsconfig-paths";

export default defineConfig({
  plugins: [tailwindcss(), process.env.VITEST ? null : reactRouter(), tsconfigPaths()].filter(Boolean),
  server: {
    port: 3000,
    open: true
  },
  test: {
    globals: true,
    environment: "jsdom",
    include: ["tests/**/*.{test,spec}.{ts,tsx}"],
    setupFiles: [],
    exclude: ["node_modules", "dist", ".idea", ".git", ".cache"]
  }
});
