import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";
import tsconfigPaths from "vite-tsconfig-paths";

export default defineConfig({
  plugins: [tsconfigPaths(), react()],

  test: {
    environment: "jsdom",
    include: ["**/*.spec.tsx"],
    globals: true,
    coverage: {
      reporter: ["text", "json", "html"],
      include: ["src/**/*"],
      exclude: ["**/*.spec.tsx", "**/*.d.ts", "**/*.mts", "**/*.config.*"],
    },
  },
});
