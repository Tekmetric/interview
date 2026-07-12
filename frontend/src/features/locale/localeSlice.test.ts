import { beforeEach, describe, expect, it } from 'vitest';

import { getInitialLocale, localeChanged, localeSlice, LOCALE_STORAGE_KEY } from './localeSlice';

describe('localeSlice', () => {
  it('changes the locale', () => {
    const state = localeSlice.reducer({ locale: 'en-US' }, localeChanged('ro-RO'));
    expect(state.locale).toBe('ro-RO');
  });
});

describe('getInitialLocale', () => {
  beforeEach(() => window.localStorage.clear());

  it('prefers the stored choice', () => {
    window.localStorage.setItem(LOCALE_STORAGE_KEY, 'ro-RO');
    expect(getInitialLocale()).toBe('ro-RO');
  });

  it('ignores unsupported stored values and falls back to the browser language', () => {
    window.localStorage.setItem(LOCALE_STORAGE_KEY, 'fr-FR');
    // jsdom reports navigator.language as en-US.
    expect(getInitialLocale()).toBe('en-US');
  });
});
