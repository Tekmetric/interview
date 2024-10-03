import plugin from 'tailwindcss/plugin';
import { default as beeqPreset, TYPOGRAPHY_DEFAULT } from '@beeq/tailwindcss';
import type { Config } from 'tailwindcss';

export default {
  content: ['./index.html', './src/**/*.{js,jsx,ts,tsx}'],
  presets: [beeqPreset],
  theme: {
    extend: {
      animation: {
        'logo-spin': 'logo-spin 20s linear infinite',
      },
      keyframes: {
        'logo-spin': {
          '0%': { transform: 'rotate(0deg)' },
          '100%': { transform: 'rotate(360deg)' },
        },
      },
    },
  },
  plugins: [
    plugin(function ({ addBase }) {
      // Use the default typography styles
      addBase({ ...TYPOGRAPHY_DEFAULT });
    }),
  ],
  corePlugins: {
    preflight: false,
  },
} satisfies Config;
