import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), "");

  return {
    plugins: [react()],
    define: {
      "import.meta.env.API_TARGET": JSON.stringify(env.API_TARGET),
      "import.meta.env.NETWORK_DELAY": JSON.stringify(env.NETWORK_DELAY),
    },
  };
});
