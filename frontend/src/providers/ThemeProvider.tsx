import { createContext, useContext, useEffect, useMemo, useState } from "react";

type ThemeValue = "light" | "dark" | null; // null => follow system

type ThemeContextValue = {
  theme: ThemeValue;
  resolvedDark: boolean; // effective dark mode after resolving system
  setTheme: (theme: ThemeValue) => void;
  cycleTheme: () => void;
};

const ThemeContext = createContext<ThemeContextValue | undefined>(undefined);

function getSystemPrefersDark() {
  return (
    typeof window !== "undefined" &&
    window.matchMedia &&
    window.matchMedia("(prefers-color-scheme: dark)").matches
  );
}

function applyThemeToDocument(theme: ThemeValue) {
  const root = document.documentElement;
  const shouldDark =
    theme === "dark" || (theme == null && getSystemPrefersDark());
  root.classList.toggle("dark", shouldDark);
}

export function ThemeProvider({ children }: { children: React.ReactNode }) {
  const [theme, setThemeState] = useState<ThemeValue>(
    () => localStorage.getItem("theme") as ThemeValue,
  );

  // Apply theme on mount and whenever it changes
  useEffect(() => {
    applyThemeToDocument(theme);
    if (theme) localStorage.setItem("theme", theme);
    else localStorage.removeItem("theme");
  }, [theme]);

  // Re-apply when system preference changes and we follow system
  useEffect(() => {
    const media = window.matchMedia("(prefers-color-scheme: dark)");
    const handler = () => {
      if (theme == null) applyThemeToDocument(null);
    };
    media.addEventListener("change", handler);
    return () => media.removeEventListener("change", handler);
  }, [theme]);

  const resolvedDark = useMemo(
    () => theme === "dark" || (theme == null && getSystemPrefersDark()),
    [theme],
  );

  const setTheme = (value: ThemeValue) => setThemeState(value);

  const cycleTheme = () => {
    setThemeState((prev) => {
      if (prev == null) return getSystemPrefersDark() ? "light" : "dark"; // flip from system
      if (prev === "dark") return "light";
      return "dark";
    });
  };

  const value = useMemo<ThemeContextValue>(
    () => ({ theme, resolvedDark, setTheme, cycleTheme }),
    [theme, resolvedDark],
  );

  return (
    <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>
  );
}

export function useTheme() {
  const ctx = useContext(ThemeContext);
  if (!ctx) throw new Error("useTheme must be used within a ThemeProvider");
  return ctx;
}
