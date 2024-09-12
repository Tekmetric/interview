/** @type {import('tailwindcss').Config} */
module.exports = {
 content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'pantone-green': '#009B77',
      },
      backgroundImage: {
        'gradient-green-to-white': 'linear-gradient(to bottom, #009B77, #ffffff)',
      },
    },
  },
  plugins: [],
}

