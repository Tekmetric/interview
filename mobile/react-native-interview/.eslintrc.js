module.exports = {
  extends: ['expo', 'prettier'],
  plugins: ['prettier', '@typescript-eslint'],
  rules: {
    'prettier/prettier': 'error',
    'import/no-unresolved': 'off', // Note: disabled for now as there is a misconfiguration between @ imports and eslint
  },
};
