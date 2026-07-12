import { darkPalette, lightPalette, type Palette } from './palettes';
import { tokens, type Tokens } from './tokens';

export type ThemeMode = 'light' | 'dark';

export interface AppTheme extends Tokens {
  name: ThemeMode;
  colors: Palette;
}

export const lightTheme: AppTheme = {
  ...tokens,
  name: 'light',
  colors: lightPalette,
};

export const darkTheme: AppTheme = {
  ...tokens,
  name: 'dark',
  colors: darkPalette,
};

export const themesByMode: Record<ThemeMode, AppTheme> = {
  light: lightTheme,
  dark: darkTheme,
};
