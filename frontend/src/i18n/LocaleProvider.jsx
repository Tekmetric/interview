import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
} from 'react';
import { useLocalStorage } from '../hooks/useLocalStorage';
import en from './catalogs/en.json';
import es from './catalogs/es.json';
import fr from './catalogs/fr.json';
import pt from './catalogs/pt.json';
import ja from './catalogs/ja.json';

const CATALOGS = { en, es, fr, pt, ja };

// Labels are endonyms (each language names itself), the accessible convention.
export const LOCALES = [
  { code: 'en', label: 'English' },
  { code: 'es', label: 'Español' },
  { code: 'fr', label: 'Français' },
  { code: 'pt', label: 'Português' },
  { code: 'ja', label: '日本語' },
];

const LocaleContext = createContext(null);

function getInitialLocale() {
  const lang = (navigator.language || 'en').slice(0, 2).toLowerCase();
  return CATALOGS[lang] ? lang : 'en';
}

function interpolate(template, vars) {
  if (!vars) return template;
  return template.replace(/\{(\w+)\}/g, (match, key) =>
    key in vars ? String(vars[key]) : match
  );
}

export function LocaleProvider({ children }) {
  const [locale, setLocale] = useLocalStorage('bsc:locale', getInitialLocale());

  // Keep <html lang> current for screen readers and the :lang(ja) font fallback.
  useEffect(() => {
    document.documentElement.lang = locale;
  }, [locale]);

  // Falls back to English then the raw key, so a missing string is visible
  // rather than blank. A numeric `vars.count` selects the plural variant
  // (key_one / key_other) and is formatted for the locale.
  const t = useCallback(
    (key, vars) => {
      let lookupKey = key;
      let finalVars = vars;

      if (vars && typeof vars.count === 'number') {
        const category = new Intl.PluralRules(locale).select(vars.count);
        const variant = [`${key}_${category}`, `${key}_other`].find(
          (k) => CATALOGS[locale]?.[k] != null || en[k] != null
        );
        if (variant) lookupKey = variant;
        finalVars = {
          ...vars,
          count: new Intl.NumberFormat(locale).format(vars.count),
        };
      }

      const message =
        CATALOGS[locale]?.[lookupKey] ??
        en[lookupKey] ??
        CATALOGS[locale]?.[key] ??
        en[key] ??
        key;
      return interpolate(message, finalVars);
    },
    [locale]
  );

  const value = useMemo(
    () => ({ locale, setLocale, t, locales: LOCALES }),
    [locale, setLocale, t]
  );

  return (
    <LocaleContext.Provider value={value}>{children}</LocaleContext.Provider>
  );
}

export function useTranslation() {
  const ctx = useContext(LocaleContext);
  if (!ctx) {
    throw new Error('useTranslation must be used within a LocaleProvider');
  }
  return ctx;
}
