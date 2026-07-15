import { useCallback, useEffect, useState } from 'react';

export function useLocalStorage(key, initialValue) {
  const [value, setValue] = useState(() => {
    try {
      const stored = window.localStorage.getItem(key);
      return stored !== null ? JSON.parse(stored) : initialValue;
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
