/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        main: '#252B37',
        lightGrey: "#F5F5F5",
        grey: '#BCBCBC'
      },
      fontFamily: {
        sans: ['ui-sans-serif', 'system-ui'],
        roboto: ['Roboto', 'sans-serif']
      }
    }
  },
  plugins: []
};