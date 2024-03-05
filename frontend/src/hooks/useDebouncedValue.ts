import { useState, useEffect } from 'react';

export const useDebouncedValue = <T>(inputValue: T) => {
  const [debouncedValue, setDebouncedValue] = useState<T>(inputValue);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(inputValue);
    }, 500);

    return () => {
      clearTimeout(handler);
    };
  }, [inputValue]);

  return debouncedValue;
};
