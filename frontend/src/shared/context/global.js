import { createContext, useState } from 'react';

export const GlobalContext = createContext({
  globalState: {},
  setGlobalState: _newState => {},
});

export const GlobalState = ({ children }) => {
  const [state, setState] = useState({});

  return (
    <GlobalContext.Provider
      value={{
        globalState: state,
        setGlobalState: newState => {
          setState(prev => ({
            ...prev,
            ...newState,
          }));
        },
      }}
    >
      {children}
    </GlobalContext.Provider>
  );
};
