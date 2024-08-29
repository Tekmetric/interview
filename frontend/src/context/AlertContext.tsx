import React from 'react';
import { createContext, useState, ReactNode, useContext } from 'react';

export enum AlertTypes {
  SUCCESS = 'success',
  ERROR = 'error'
}

type AlertType = `${AlertTypes}`;

interface AlertState {
  message: string;
  type: AlertType;
}

interface AlertContextProps extends AlertState {
  setAlert: (message: string, type?: AlertType) => void;
}

const initialState: AlertState = {
  message: '',
  type: AlertTypes.SUCCESS
};

const AlertContext = createContext<AlertContextProps>({
  ...initialState,
  setAlert: () => {}
});

interface AlertProviderProps {
  children: ReactNode;
}

export function AlertProvider({ children }: AlertProviderProps) {
  const [message, setMessage] = useState<string>('');
  const [type, setType] = useState<AlertType>(AlertTypes.SUCCESS);

  function setAlert(message: string, type?: AlertType) {
    setMessage(message);
    setType(type || AlertTypes.SUCCESS);
  }

  return (
    <AlertContext.Provider
      value={{
        message,
        type,
        setAlert
      }}
    >
      {children}
    </AlertContext.Provider>
  );
}

export const useAlert = () => useContext(AlertContext);
