module.exports = {
  extends: ['@tekmetric/eslint-config/next.js', 'plugin:jest/recommended'],
  rules: {
    '@next/next/no-html-link-for-pages': 'off'
  },
  plugins: ['jest']
}
