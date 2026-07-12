import { createSlice } from '@reduxjs/toolkit';

import type { ThemeMode } from '../../theme';
import { readStorage } from '../../utils/storage';

export const THEME_STORAGE_KEY = 'rm-wiki:theme';

export interface ThemeState {
  mode: ThemeMode;
}

// Explicit user choice wins; otherwise follow the OS preference.
export function getInitialThemeMode(): ThemeMode {
  const stored = readStorage(THEME_STORAGE_KEY);
  if (stored === 'light' || stored === 'dark') {
    return stored;
  }
  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
}

export const themeSlice = createSlice({
  name: 'theme',
  initialState: (): ThemeState => ({ mode: getInitialThemeMode() }),
  reducers: {
    themeToggled(state) {
      state.mode = state.mode === 'light' ? 'dark' : 'light';
    },
  },
});

export const { themeToggled } = themeSlice.actions;
