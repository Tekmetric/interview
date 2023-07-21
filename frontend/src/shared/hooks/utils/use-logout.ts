import { useCallback, useContext } from 'react';
import { GlobalContext } from '../../context/global';

export const useLogout = (key: string) => {
  const { setGlobalState } = useContext(GlobalContext);

  const logout = useCallback(() => {
    try {
      localStorage.removeItem(key);
      setGlobalState({
        token: '',
      });
    } catch (err) {
      console.error(err);
    }
  }, [setGlobalState, key]);

  return { logout };
};
