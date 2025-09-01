import React, {
  createContext,
  ReactNode,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';

import { Theme, ThemeContextType } from '../types';

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

interface ThemeProviderProps {
  children: ReactNode;
}

const THEME_STORAGE_KEY = 'user-theme-preference';

/**
 * Theme Provider component that manages theme state and provides theme context
 * Supports light, dark, and system themes with localStorage persistence
 */
export const ThemeProvider: React.FC<ThemeProviderProps> = React.memo(({ children }) => {
  const [theme, setThemeState] = useState<Theme>('system');
  const [actualTheme, setActualTheme] = useState<'light' | 'dark'>('light');

  /**
   * Gets the system theme preference
   */
  const getSystemTheme = useCallback((): 'light' | 'dark' => {
    if (typeof window !== 'undefined' && window.matchMedia) {
      return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }
    return 'light';
  }, []);

  /**
   * Gets the stored theme preference from localStorage
   */
  const getStoredTheme = useCallback((): Theme => {
    if (typeof window !== 'undefined') {
      const stored = localStorage.getItem(THEME_STORAGE_KEY);
      if (stored && ['light', 'dark', 'system'].includes(stored)) {
        return stored as Theme;
      }
    }
    return 'system';
  }, []);

  /**
   * Updates the actual theme based on the theme preference
   */
  const updateActualTheme = useCallback(
    (themePreference: Theme) => {
      let newActualTheme: 'light' | 'dark';

      if (themePreference === 'system') {
        newActualTheme = getSystemTheme();
      } else {
        newActualTheme = themePreference;
      }

      setActualTheme(newActualTheme);

      // Apply theme to document
      if (newActualTheme === 'dark') {
        document.documentElement.classList.add('dark');
      } else {
        document.documentElement.classList.remove('dark');
      }
    },
    [getSystemTheme]
  );

  /**
   * Sets the theme and persists to localStorage
   */
  const setTheme = useCallback(
    (newTheme: Theme) => {
      setThemeState(newTheme);
      localStorage.setItem(THEME_STORAGE_KEY, newTheme);
      updateActualTheme(newTheme);
    },
    [updateActualTheme]
  );

  // Initialize theme on mount
  useEffect(() => {
    const storedTheme = getStoredTheme();
    setThemeState(storedTheme);
    updateActualTheme(storedTheme);
  }, [getStoredTheme, updateActualTheme]);

  // Listen for system theme changes
  useEffect(() => {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    const handleSystemThemeChange = () => {
      if (theme === 'system') {
        updateActualTheme('system');
      }
    };

    mediaQuery.addEventListener('change', handleSystemThemeChange);

    return () => {
      mediaQuery.removeEventListener('change', handleSystemThemeChange);
    };
  }, [theme, updateActualTheme]);

  const value: ThemeContextType = useMemo(
    () => ({
      theme,
      actualTheme,
      setTheme,
    }),
    [theme, actualTheme, setTheme]
  );

  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>;
});

ThemeProvider.displayName = 'ThemeProvider';

/**
 * Hook to use theme context
 * Throws error if used outside ThemeProvider
 */
export const useTheme = (): ThemeContextType => {
  const context = useContext(ThemeContext);
  if (context === undefined) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
};
