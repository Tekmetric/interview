// tailwind config is required for editor support
import sharedConfig from '@tekmetric/tailwind-config'
import type { Config } from 'tailwindcss'

const config: Pick<Config, 'content' | 'presets'> = {
  content: ['./src/app/**/*.{ts,tsx}'],
  presets: [sharedConfig]
}

export default config
