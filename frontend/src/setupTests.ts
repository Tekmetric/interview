import "@testing-library/jest-dom";

// Add a global fetch mock if it doesn’t exist (JSDOM lacks it)
if (!global.fetch) {
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok: true,
      json: () => Promise.resolve({}),
    })
  ) as jest.Mock;
}