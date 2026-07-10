import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useLayoutEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react';

type ResolvedTheme = 'light' | 'dark';

interface ThemeContextValue {
  useOppositeScheme: boolean;
  setUseOppositeScheme: (value: boolean) => void;
  resolvedTheme: ResolvedTheme;
}

const ThemeContext = createContext<ThemeContextValue | null>(null);

function getSystemTheme(): ResolvedTheme {
  return window.matchMedia('(prefers-color-scheme: dark)').matches
    ? 'dark'
    : 'light';
}

function resolveTheme(
  systemTheme: ResolvedTheme,
  useOppositeScheme: boolean,
): ResolvedTheme {
  if (!useOppositeScheme) {
    return systemTheme;
  }

  return systemTheme === 'dark' ? 'light' : 'dark';
}

function applyTheme(theme: ResolvedTheme) {
  const root = document.documentElement;
  root.classList.toggle('dark', theme === 'dark');
  root.style.colorScheme = theme;
}

export function ThemeProvider({ children }: { children: ReactNode }) {
  const [systemTheme, setSystemTheme] = useState<ResolvedTheme>(getSystemTheme);
  const [useOppositeScheme, setUseOppositeScheme] = useState(false);

  const resolvedTheme = useMemo(
    () => resolveTheme(systemTheme, useOppositeScheme),
    [systemTheme, useOppositeScheme],
  );

  useEffect(() => {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');

    function handleChange(event: MediaQueryListEvent) {
      setSystemTheme(event.matches ? 'dark' : 'light');
    }

    mediaQuery.addEventListener('change', handleChange);

    return () => {
      mediaQuery.removeEventListener('change', handleChange);
    };
  }, []);

  useLayoutEffect(() => {
    applyTheme(resolvedTheme);
  }, [resolvedTheme]);

  const handleSetUseOppositeScheme = useCallback((value: boolean) => {
    setUseOppositeScheme(value);
  }, []);

  const value = useMemo(
    () => ({
      useOppositeScheme,
      setUseOppositeScheme: handleSetUseOppositeScheme,
      resolvedTheme,
    }),
    [useOppositeScheme, handleSetUseOppositeScheme, resolvedTheme],
  );

  return (
    <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>
  );
}

export function useTheme() {
  const context = useContext(ThemeContext);

  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }

  return context;
}
