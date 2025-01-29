import '@testing-library/jest-dom'

// Mock ResizeObserver
global.ResizeObserver = class {
  constructor(callback) {
    this.callback = callback
  }
  observe() {}
  unobserve() {}
  disconnect() {}
}
