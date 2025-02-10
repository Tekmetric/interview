import antfu from '@antfu/eslint-config';

export default antfu({
  react: true,
  lessOpinionated: true,
  stylistic: {
    semi: true
  },
  overrides: {
    stylistic: {
      'style/comma-dangle': ['error', 'never']
    }
  }
});
