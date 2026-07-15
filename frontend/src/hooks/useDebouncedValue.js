import { useCallback, useEffect, useRef, useState } from 'react';

// Returns [debounced, flush]. flush() settles to the latest value immediately
// (e.g. on Enter), cancelling the pending wait.
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

  return [debounced, flush];
}
