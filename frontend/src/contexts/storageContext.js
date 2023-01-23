import { createContext, useContext } from 'react';

const StorageContext = createContext(null);

export const StorageProvider = ({ children, storage }) => {
  return (
    <StorageContext.Provider value={storage}>
      {children}
    </StorageContext.Provider>
  );
};

export const useStorage = () => {
  const storage = useContext(StorageContext);
  if (!storage) throw Error('Must be in a <StorageProvider />');

  return storage;
};
