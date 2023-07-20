import { createContext, useState, ReactNode, Dispatch, SetStateAction } from 'react';

type GlobalStateType = {
  token: string;
};

type GlobalContextType = {
  globalState: GlobalStateType;
  setGlobalState: Dispatch<SetStateAction<GlobalStateType>>;
};

export const GlobalContext = createContext<GlobalContextType>({
  globalState: { token: '' },
  setGlobalState: _newState => { },
});

type GlobalStateProps = {
  children: ReactNode | ReactNode[];
};

export const GlobalState = ({ children }: GlobalStateProps) => {
  const [state, setState] = useState<GlobalStateType>({ token: '' });

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
