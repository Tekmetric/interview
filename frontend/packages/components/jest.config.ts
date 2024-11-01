import path from 'path'

module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'jsdom', // or 'jsdom' if you're testing browser-based code
  transform: {
    '^.+\\.tsx?$': 'ts-jest'
  },
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json', 'node'],
  setupFilesAfterEnv: ['<rootDir>/jest-setup.ts'],
  // moduleDirectories: ['node_modules', '<rootDir>/../../node_modules'],
  // modulePaths: ['<rootDir>/node_modules', '<rootDir>/../../node_modules'],
  moduleDirectories: [
    'node_modules',
    path.resolve(__dirname, '../../node_modules')
  ],
  modulePaths: [path.resolve(__dirname, '../../node_modules')]
}
