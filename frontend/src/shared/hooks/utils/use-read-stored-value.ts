import { useCallback, useContext, useEffect, useState } from 'react';
import { GlobalContext, GlobalStateType } from '../../context/global';

type Value<T> = T | null;

function useReadStoredValue<T>(key: string): Value<T> {
  const { globalState } = useContext(GlobalContext);

  const readValue = useCallback((): Value<T> => {
    try {
      const globalToken = globalState[key as keyof GlobalStateType];
      const storageToken = localStorage.getItem(key);

      const token = globalToken || storageToken;

      return token as Value<T>;
    } catch (error) {
      console.warn(`Error reading stored value for key "${key}":`, error);
      return null;
    }
  }, [key, globalState]);

  const [storedValue, setStoredValue] = useState<Value<T>>(readValue);

  useEffect(() => {
    setStoredValue(readValue());
  }, [globalState]);

  return storedValue;
}

export default useReadStoredValue;
