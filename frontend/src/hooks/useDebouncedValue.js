import { useCallback, useEffect, useRef, useState } from 'react';

// Returns [debounced, flush, sync]. flush() settles to the latest value
// immediately (e.g. on Enter). sync(next) settles to a specific value now,
// bypassing the wait — used when the value is set programmatically (e.g. from a
// back/forward navigation) and shouldn't lag behind.
export function useDebouncedValue(value, delay = 300) {
  const [debounced, setDebounced] = useState(value);
  const timeoutRef = useRef(null);

  useEffect(() => {
    timeoutRef.current = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(timeoutRef.current);
  }, [value, delay]);

  const flush = useCallback(() => {
    clearTimeout(timeoutRef.current);
    setDebounced(value);
  }, [value]);

  const sync = useCallback((next) => {
    clearTimeout(timeoutRef.current);
    setDebounced(next);
  }, []);

  return [debounced, flush, sync];
}
