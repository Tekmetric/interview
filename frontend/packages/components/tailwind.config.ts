import sharedConfig from '@tekmetric/tailwind-config'
import type { Config } from 'tailwindcss'

const config: Pick<Config, 'prefix' | 'presets' | 'content'> = {
  content: ['./src/**/*.{ts,tsx}'],
  presets: [sharedConfig]
}

export default config
