import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

import { readStorage } from '../../utils/storage';

export const SUPPORTED_LOCALES = ['en-US', 'ro-RO'] as const;
export type AppLocale = (typeof SUPPORTED_LOCALES)[number];

export const LOCALE_STORAGE_KEY = 'rm-wiki:locale';

export interface LocaleState {
  locale: AppLocale;
}

function isAppLocale(value: string | null): value is AppLocale {
  return SUPPORTED_LOCALES.includes(value as AppLocale);
}

// Explicit user choice wins; otherwise match the browser language.
export function getInitialLocale(): AppLocale {
  const stored = readStorage(LOCALE_STORAGE_KEY);
  if (isAppLocale(stored)) {
    return stored;
  }
  return navigator.language.startsWith('ro') ? 'ro-RO' : 'en-US';
}

export const localeSlice = createSlice({
  name: 'locale',
  initialState: (): LocaleState => ({ locale: getInitialLocale() }),
  reducers: {
    localeChanged(state, action: PayloadAction<AppLocale>) {
      state.locale = action.payload;
    },
  },
});

export const { localeChanged } = localeSlice.actions;
