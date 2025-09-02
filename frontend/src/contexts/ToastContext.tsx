import React, { createContext, useContext } from 'react';

import { ToastContainer } from '../components/ToastContainer';
import { useToast } from '../hooks/useToast';

interface ToastContextType {
  addSuccessToast: (title: string, message?: string, duration?: number) => string;
  addErrorToast: (title: string, message?: string, duration?: number) => string;
  removeToast: (id: string) => void;
}

const ToastContext = createContext<ToastContextType | undefined>(undefined);

export const useToastContext = (): ToastContextType => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToastContext must be used within a ToastProvider');
  }
  return context;
};

interface ToastProviderProps {
  children: React.ReactNode;
}

export const ToastProvider: React.FC<ToastProviderProps> = ({ children }) => {
  const { toasts, removeToast, addSuccessToast, addErrorToast } = useToast();

  const contextValue: ToastContextType = {
    addSuccessToast,
    addErrorToast,
    removeToast,
  };

  return (
    <ToastContext.Provider value={contextValue}>
      {children}
      <ToastContainer toasts={toasts} onRemove={removeToast} />
    </ToastContext.Provider>
  );
};
