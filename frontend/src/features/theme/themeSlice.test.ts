import { beforeEach, describe, expect, it } from 'vitest';

import { getInitialThemeMode, themeSlice, themeToggled, THEME_STORAGE_KEY } from './themeSlice';

describe('themeSlice', () => {
  it('toggles light to dark and back', () => {
    const dark = themeSlice.reducer({ mode: 'light' }, themeToggled());
    expect(dark.mode).toBe('dark');

    const light = themeSlice.reducer(dark, themeToggled());
    expect(light.mode).toBe('light');
  });
});

describe('getInitialThemeMode', () => {
  beforeEach(() => window.localStorage.clear());

  it('prefers the stored choice', () => {
    window.localStorage.setItem(THEME_STORAGE_KEY, 'dark');
    expect(getInitialThemeMode()).toBe('dark');
  });

  it('ignores invalid stored values and falls back to the OS preference', () => {
    window.localStorage.setItem(THEME_STORAGE_KEY, 'neon');
    // The test environment's matchMedia stub reports "no match" => light.
    expect(getInitialThemeMode()).toBe('light');
  });
});
