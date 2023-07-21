import { useCallback, useContext, useEffect, useState } from 'react';
import { GlobalContext, GlobalStateType } from '../../context/global';

const useReadStoredValue = (key: string) => {
  const { globalState } = useContext(GlobalContext);

  const readValue = useCallback(() => {
    try {
      const globalToken = globalState[key as keyof GlobalStateType];
      const storageToken = localStorage.getItem(key);

      const token = globalToken || storageToken;

      return token;
    } catch (error) {
      console.warn(`Error reading stored value for key "${key}":`, error);
      return null;
    }
  }, [key, globalState]);

  const [storedValue, setStoredValue] = useState(readValue);

  useEffect(() => {
    setStoredValue(readValue());
  }, [globalState]);

  return storedValue;
};

export default useReadStoredValue;
