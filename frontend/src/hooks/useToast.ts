import { useCallback, useState } from 'react';

import { ToastProps, ToastType } from '../components/Toast';

interface UseToastOptions {
  duration?: number;
}

interface ToastInput {
  type: ToastType;
  title: string;
  message?: string;
  duration?: number;
}

export const useToast = (options: UseToastOptions = {}) => {
  const [toasts, setToasts] = useState<ToastProps[]>([]);

  const removeToast = useCallback((id: string) => {
    setToasts(prev => prev.filter(toast => toast.id !== id));
  }, []);

  const addToast = useCallback(
    (input: ToastInput) => {
      const id = `toast-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
      const duration = input.duration ?? options.duration ?? 5000;

      const newToast: ToastProps = {
        id,
        type: input.type,
        title: input.title,
        message: input.message,
        duration,
        onClose: removeToast,
      };

      setToasts(prev => [...prev, newToast]);
      return id;
    },
    [options.duration, removeToast]
  );

  const clearAllToasts = useCallback(() => {
    setToasts([]);
  }, []);

  // Convenience methods for different toast types
  const success = useCallback(
    (title: string, message?: string, duration?: number) => {
      return addToast({ type: 'success', title, message, duration });
    },
    [addToast]
  );

  const error = useCallback(
    (title: string, message?: string, duration?: number) => {
      return addToast({ type: 'error', title, message, duration });
    },
    [addToast]
  );

  const warning = useCallback(
    (title: string, message?: string, duration?: number) => {
      return addToast({ type: 'warning', title, message, duration });
    },
    [addToast]
  );

  const info = useCallback(
    (title: string, message?: string, duration?: number) => {
      return addToast({ type: 'info', title, message, duration });
    },
    [addToast]
  );

  return {
    toasts,
    addToast,
    removeToast,
    clearAllToasts,
    success,
    error,
    warning,
    info,
  };
};
