import {
  CheckCircleIcon,
  ExclamationTriangleIcon,
  XCircleIcon,
  XMarkIcon,
} from '@heroicons/react/24/outline';
import React, { useEffect, useRef } from 'react';

export type ToastType = 'success' | 'error' | 'warning' | 'info';

export interface ToastProps {
  id: string;
  type: ToastType;
  title: string;
  message?: string;
  duration?: number;
  onClose: (id: string) => void;
}

export const Toast: React.FC<ToastProps> = React.memo(
  ({ id, type, title, message, duration = 5000, onClose }) => {
    const timeoutRef = useRef<NodeJS.Timeout>();

    // Auto-close the toast after duration
    useEffect(() => {
      if (duration > 0) {
        timeoutRef.current = setTimeout(() => {
          onClose(id);
        }, duration);
      }

      return () => {
        if (timeoutRef.current) {
          clearTimeout(timeoutRef.current);
        }
      };
    }, [id, duration, onClose]);

    const handleClose = () => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
      onClose(id);
    };

    const getToastConfig = (type: ToastType) => {
      switch (type) {
        case 'success':
          return {
            icon: CheckCircleIcon,
            bgColor: 'bg-green-50 border border-green-200 dark:bg-green-900 dark:border-green-700',
            borderColor: '', // Combined with bgColor for better control
            iconColor: 'text-green-500 dark:text-green-400',
            titleColor: 'text-green-900 dark:text-green-100',
            messageColor: 'text-green-800 dark:text-green-200',
            buttonColor:
              'text-green-600 hover:text-green-700 dark:text-green-300 dark:hover:text-green-200',
          };
        case 'error':
          return {
            icon: XCircleIcon,
            bgColor: 'bg-red-50 border border-red-200 dark:bg-red-900 dark:border-red-700',
            borderColor: '', // Combined with bgColor for better control
            iconColor: 'text-red-500 dark:text-red-400',
            titleColor: 'text-red-900 dark:text-red-100',
            messageColor: 'text-red-800 dark:text-red-200',
            buttonColor:
              'text-red-600 hover:text-red-700 dark:text-red-300 dark:hover:text-red-200',
          };
        case 'warning':
          return {
            icon: ExclamationTriangleIcon,
            bgColor:
              'bg-yellow-50 border border-yellow-200 dark:bg-yellow-900 dark:border-yellow-700',
            borderColor: '', // Combined with bgColor for better control
            iconColor: 'text-yellow-600 dark:text-yellow-400',
            titleColor: 'text-yellow-900 dark:text-yellow-100',
            messageColor: 'text-yellow-800 dark:text-yellow-200',
            buttonColor:
              'text-yellow-700 hover:text-yellow-800 dark:text-yellow-300 dark:hover:text-yellow-200',
          };
        case 'info':
        default:
          return {
            icon: CheckCircleIcon,
            bgColor: 'bg-blue-50 border border-blue-200 dark:bg-blue-900 dark:border-blue-700',
            borderColor: '', // Combined with bgColor for better control
            iconColor: 'text-blue-500 dark:text-blue-400',
            titleColor: 'text-blue-900 dark:text-blue-100',
            messageColor: 'text-blue-800 dark:text-blue-200',
            buttonColor:
              'text-blue-600 hover:text-blue-700 dark:text-blue-300 dark:hover:text-blue-200',
          };
      }
    };

    const config = getToastConfig(type);
    const Icon = config.icon;

    return (
      <div
        className={`pointer-events-auto flex w-full max-w-sm rounded-lg p-4 shadow-xl transition-all duration-300 ease-in-out backdrop-blur-sm ${config.bgColor}`}
        role='alert'
        aria-live='polite'
        aria-atomic='true'
      >
        <div className='flex'>
          <div className='flex-shrink-0'>
            <Icon className={`h-5 w-5 ${config.iconColor}`} aria-hidden='true' />
          </div>
          <div className='ml-3 flex-1'>
            <h3 className={`text-sm font-medium ${config.titleColor}`}>{title}</h3>
            {message && <p className={`mt-1 text-sm ${config.messageColor}`}>{message}</p>}
          </div>
          <div className='ml-4 flex flex-shrink-0'>
            <button
              type='button'
              className={`inline-flex rounded-md p-1 ${config.buttonColor} transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-white dark:focus:ring-offset-gray-800 ${
                type === 'success'
                  ? 'focus:ring-green-500'
                  : type === 'error'
                    ? 'focus:ring-red-500'
                    : type === 'warning'
                      ? 'focus:ring-yellow-500'
                      : 'focus:ring-blue-500'
              }`}
              onClick={handleClose}
              aria-label='Close notification'
            >
              <span className='sr-only'>Close</span>
              <XMarkIcon className='h-5 w-5' aria-hidden='true' />
            </button>
          </div>
        </div>
      </div>
    );
  }
);

Toast.displayName = 'Toast';
