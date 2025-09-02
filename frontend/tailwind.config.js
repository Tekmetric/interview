/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
    "./public/index.html",
  ],
  darkMode: 'class', // Enable class-based dark mode
  plugins: [],
  // Optimize for production
  future: {
    hoverOnlyWhenSupported: true,
  },
}
