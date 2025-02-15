/// <reference types="vite/client" />
/// <reference types="vite-plugin-svgr/client" />
/// <reference types="vitest/importMeta" />
/// <reference types="vitest/globals" />

interface ImportMetaEnv {
  readonly VITE_WMATA_API_KEY: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
