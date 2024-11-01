module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'jsdom', // or 'jsdom' if you're testing browser-based code
  transform: {
    '^.+\\.tsx?$': 'ts-jest'
  },
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json', 'node'],
  setupFilesAfterEnv: ['<rootDir>/jest-setup.ts']
}
