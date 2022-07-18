import dev from './dev';
import prod from './prod';
import test from './test';

const config = (() => {
  switch (process.env.NODE_ENV) {
    case 'development':
      return dev;
    case 'production':
      return prod;
    case 'test':
      return test;
    default:
      return dev;
  }
})();

export default config;
