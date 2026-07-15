import { useCallback, useEffect, useState } from 'react';

// Persists state to localStorage. `legacyKey` lets a renamed key adopt (and clean
// up) a value stored under the old name once, so a rename doesn't orphan data.
export function useLocalStorage(key, initialValue, { legacyKey } = {}) {
  const [value, setValue] = useState(() => {
    try {
      const stored = window.localStorage.getItem(key);
      if (stored !== null) return JSON.parse(stored);
      if (legacyKey) {
        const legacy = window.localStorage.getItem(legacyKey);
        if (legacy !== null) {
          window.localStorage.removeItem(legacyKey);
          return JSON.parse(legacy);
        }
      }
      return initialValue;
    } catch {
      return initialValue;
    }
  });

  useEffect(() => {
    try {
      window.localStorage.setItem(key, JSON.stringify(value));
    } catch {
      // Storage full or disabled (e.g. private mode) — in-memory state still works.
    }
  }, [key, value]);

  const set = useCallback((next) => setValue(next), []);

  return [value, set];
}
