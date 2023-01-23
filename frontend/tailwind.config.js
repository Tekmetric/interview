/** @type {import('tailwindcss').Config} */

module.exports = {
  content: [ "./src/**/*.{js,jsx,ts,tsx}", './public/*.html'],
  theme: {
    extend: { },
  },
  plugins: [require("daisyui")],

  daisyui: {
    themes: [
      {
        mytheme: {
          primary: "#ee5834",
          secondary: "#199ae5",
          accent: "#37cdbe",
          "base-100": "#ffffff",
          "--btn-text-case": "none",
        },
      },
    ],
  },
}