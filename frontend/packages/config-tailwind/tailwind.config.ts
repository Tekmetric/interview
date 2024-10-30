import type { Config } from 'tailwindcss'

// We want each package to be responsible for its own content.
const config: Omit<Config, 'content'> = {
  prefix: 'tek-',
  theme: {
    extend: {
      fontFamily: {
        sans: ['var(--font-lato)']
      }
    }
  },
  plugins: []
}
export default config
