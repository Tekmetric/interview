import { DependencyList, EffectCallback, useEffect, useMemo, useRef } from 'react';
import { debounce } from '@mui/material';

/**
 * Debounced effect optimized for not recreating the debounce method again and again for every dependency change
 * @param effect - function that needs to run
 * @param deps - dependencies array
 * @param debounceInterval - debounce time interval - default set to 500ms
 */
const useDebouncedEffect = (
  effect: EffectCallback,
  deps: DependencyList,
  debounceInterval: number = 500,
) => {
  const effectRef = useRef<EffectCallback>();
  effectRef.current = effect;

  const debouncedEffect = useMemo(() => {
    return debounce(() => {
      effectRef.current?.();
    }, debounceInterval);
  }, []);

  useEffect(() => debouncedEffect, [deps]);
};

export default useDebouncedEffect;
