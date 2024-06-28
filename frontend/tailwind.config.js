module.exports = {
  purge: ['./src/**/*.{js,jsx,ts,tsx}', './public/index.html'],
  darkMode: false,
  theme: {
    extend: {
      colors: {
        black: '#000000',
        darkGray: '#1A1A1A',
        darkerGray: '#333333',
        gray: '#4D4D4D',
        mediumGray: '#666666',
        lightGray: '#808080',
        lighterGray: '#999999',
        veryLightGray: '#B3B3B3',
        veryVeryLightGray: '#CCCCCC',
        nearWhite: '#E6E6E6',
        white: '#FFFFFF',
        tekOrange: '#f0572a',
      },
    },
  },
  variants: {
    extend: {},
  },
  plugins: [],
};
