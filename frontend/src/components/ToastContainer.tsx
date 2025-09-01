import React from 'react';
import { createPortal } from 'react-dom';

import { Toast, type ToastProps } from './Toast';

export interface ToastContainerProps {
  toasts: ToastProps[];
  onRemove: (id: string) => void;
}

export const ToastContainer: React.FC<ToastContainerProps> = ({ toasts, onRemove }) => {
  if (toasts.length === 0) {
    return null;
  }

  const toastContainer = (
    <div
      className='pointer-events-none fixed inset-0 z-50 flex items-end justify-end p-6 sm:items-start sm:justify-end'
      aria-live='assertive'
      aria-label='Notifications'
    >
      <div className='flex w-full flex-col items-center space-y-4 sm:items-end'>
        {toasts.map(toast => (
          <div
            key={toast.id}
            className='transform transition-all duration-300 ease-in-out'
            style={{
              animation: 'slideInRight 0.3s ease-out',
            }}
          >
            <Toast {...toast} onClose={onRemove} />
          </div>
        ))}
      </div>
    </div>
  );

  // Render toasts in a portal to ensure they appear above all other content
  return createPortal(toastContainer, document.body);
};

export default ToastContainer;
