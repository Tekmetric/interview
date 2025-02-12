import antfu from '@antfu/eslint-config';

export default antfu({
  react: true,
  lessOpinionated: true,
  stylistic: {
    blockSpacing: true,
    semi: true
  },
  overrides: {
    stylistic: {
      'style/comma-dangle': ['error', 'never'],
      'style/jsx-curly-spacing': ['error', { when: 'always' }],
      'style/jsx-one-expression-per-line': ['off'],
      'style/object-curly-spacing': ['error', 'always'],
      'style/quotes': ['error', 'single', { avoidEscape: true }],
      'style/template-curly-spacing': ['error', 'always']
    }
  },
  ignores: ['src/routeTree.gen.ts']
});
