import { DarkTheme, DefaultTheme, Theme } from '@react-navigation/native';
import { createContext, useState, useContext, useMemo } from 'react';
import { type ColorSchemeName, useColorScheme } from 'react-native';

type ThemeContextType = {
  theme: Theme;
  mode: ColorSchemeName;
  toggleMode: () => void;
};

const ThemeContext = createContext<ThemeContextType | null>(null);

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [mode, setMode] = useState<ColorSchemeName>(useColorScheme() ?? 'light');

  const theme = useMemo(() => {
    return mode === 'dark' ? DarkTheme : DefaultTheme;
  }, [mode]);

  const toggleMode = () => {
    setMode((prevMode) => (prevMode === 'light' ? 'dark' : 'light'));
  };

  const value = useMemo(
    () => ({
      theme,
      mode,
      toggleMode,
    }),
    [theme, mode],
  );

  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>;
};

export const useTheme = (): ThemeContextType => {
  const context = useContext(ThemeContext);

  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
};
