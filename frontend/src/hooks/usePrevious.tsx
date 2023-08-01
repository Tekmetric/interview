import React, { useEffect, useRef } from 'react';
import { Product } from '../model/Product';

// custom hook for getting previous value
export function usePrevious(value: Product[]): Product[] | undefined {
  const ref: React.MutableRefObject<Product[] | undefined> = useRef();

  useEffect(() => {
    ref.current = value;
  }, [value]);

  return ref.current;
}
