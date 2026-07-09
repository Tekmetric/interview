import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
  type ReactNode,
} from 'react';
import { usePrefersReducedMotion } from '../drawer/usePrefersReducedMotion';
import { Toast } from './Toast';

const TOAST_DURATION_MS = 4000;

interface ToastItem {
  id: number;
  message: string;
}

interface ToastContextValue {
  showToast: (message: string) => void;
}

const ToastContext = createContext<ToastContextValue | null>(null);

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<ToastItem[]>([]);
  const nextIdRef = useRef(0);
  const prefersReducedMotion = usePrefersReducedMotion();

  const dismissToast = useCallback((id: number) => {
    setToasts((current) => current.filter((toast) => toast.id !== id));
  }, []);

  const showToast = useCallback((message: string) => {
    const id = nextIdRef.current;
    nextIdRef.current += 1;
    setToasts((current) => [...current, { id, message }]);
  }, []);

  const value = useMemo(() => ({ showToast }), [showToast]);

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div
        aria-live="polite"
        className="pointer-events-none fixed bottom-4 right-4 z-50 flex w-full max-w-sm flex-col gap-2 p-4"
      >
        {toasts.map((toast) => (
          <ToastItemWithAutoDismiss
            key={toast.id}
            id={toast.id}
            message={toast.message}
            prefersReducedMotion={prefersReducedMotion}
            onDismiss={dismissToast}
          />
        ))}
      </div>
    </ToastContext.Provider>
  );
}

function ToastItemWithAutoDismiss({
  id,
  message,
  prefersReducedMotion,
  onDismiss,
}: {
  id: number;
  message: string;
  prefersReducedMotion: boolean;
  onDismiss: (id: number) => void;
}) {
  useEffect(() => {
    const timeoutId = window.setTimeout(() => {
      onDismiss(id);
    }, TOAST_DURATION_MS);

    return () => {
      window.clearTimeout(timeoutId);
    };
  }, [id, onDismiss]);

  return (
    <div
      className={[
        'pointer-events-auto',
        prefersReducedMotion ? '' : 'animate-[toast-in_200ms_ease-out]',
      ]
        .filter(Boolean)
        .join(' ')}
    >
      <Toast message={message} onDismiss={() => onDismiss(id)} />
    </div>
  );
}

export function useToast(): ToastContextValue {
  const context = useContext(ToastContext);

  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }

  return context;
}
