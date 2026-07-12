import { useEffect, useState } from 'react';

// Returns `value`, but only after it has stopped changing for `delayMs`.
// The effect cleanup cancels the pending timer whenever a newer value
// arrives (and on unmount), so only the last value in a burst is published.
export function useDebouncedValue<T>(value: T, delayMs: number): T {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const timer = window.setTimeout(() => setDebouncedValue(value), delayMs);
    return () => window.clearTimeout(timer);
  }, [value, delayMs]);

  return debouncedValue;
}
