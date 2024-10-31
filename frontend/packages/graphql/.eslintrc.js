module.exports = {
  extends: ['@tekmetric/eslint-config/react.js'],
  ignorePatterns: ['src/__generated__/*'],
  overrides: [
    {
      files: ['*.ts', '*.tsx'],
      rules: {
        '@typescript-eslint/explicit-function-return-type': 'off'
      }
    }
  ]
}
