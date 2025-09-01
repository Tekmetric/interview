import { ExclamationTriangleIcon as WarningIcon } from '@heroicons/react/24/outline';
import React, { useCallback, useEffect, useRef } from 'react';

import { TableData } from '../types';

interface ConfirmationModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  title: string;
  message: string;
  user?: TableData | null;
  confirmText?: string;
  cancelText?: string;
  isDestructive?: boolean;
}

export const ConfirmationModal: React.FC<ConfirmationModalProps> = React.memo(
  ({
    isOpen,
    onClose,
    onConfirm,
    title,
    message,
    user,
    confirmText = 'Confirm',
    cancelText = 'Cancel',
    isDestructive = false,
  }) => {
    const modalRef = useRef<HTMLDivElement>(null);
    const confirmButtonRef = useRef<HTMLButtonElement>(null);
    // Disable body scroll when modal is open
    useEffect(() => {
      if (isOpen) {
        // Store current styles
        const originalOverflow = document.body.style.overflow;
        const originalPaddingRight = document.body.style.paddingRight;

        // Calculate scrollbar width
        const scrollbarWidth = window.innerWidth - document.documentElement.clientWidth;

        // Disable scroll and compensate for scrollbar width
        document.body.style.overflow = 'hidden';
        document.body.style.paddingRight = `${scrollbarWidth}px`;

        // Focus management
        setTimeout(() => {
          confirmButtonRef.current?.focus();
        }, 100);

        return () => {
          // Restore original styles when modal closes
          document.body.style.overflow = originalOverflow;
          document.body.style.paddingRight = originalPaddingRight;
        };
      }
    }, [isOpen]);

    const handleKeyDown = useCallback(
      (e: KeyboardEvent) => {
        if (e.key === 'Escape') {
          onClose();
        }
      },
      [onClose]
    );

    useEffect(() => {
      if (isOpen) {
        document.addEventListener('keydown', handleKeyDown);
        return () => document.removeEventListener('keydown', handleKeyDown);
      }
    }, [isOpen, handleKeyDown]);

    const handleBackdropClick = (e: React.MouseEvent) => {
      if (e.target === e.currentTarget) {
        onClose();
      }
    };

    if (!isOpen) {
      return null;
    }

    return (
      <div
        className='fixed inset-0 z-50 !m-0 flex items-center justify-center bg-gray-600/50 backdrop-blur-sm'
        onClick={handleBackdropClick}
        role='dialog'
        aria-modal='true'
        aria-labelledby='modal-title'
        aria-describedby='modal-description'
      >
        <div
          ref={modalRef}
          className='w-full max-w-md rounded-md border bg-white p-5 shadow-lg dark:border-gray-700 dark:bg-gray-800'
          onClick={e => e.stopPropagation()}
        >
          <div className='mb-4 flex items-start'>
            {isDestructive && (
              <WarningIcon className='mr-3 size-6 shrink-0 text-red-600' aria-hidden='true' />
            )}
            <div>
              <h3 id='modal-title' className='text-lg font-medium text-gray-900 dark:text-white'>
                {title}
              </h3>
              <p id='modal-description' className='mt-2 text-sm text-gray-500 dark:text-gray-400'>
                {message}
              </p>
              {user && (
                <div className='mt-3 rounded-md bg-gray-50 p-3 text-sm dark:bg-gray-700'>
                  <div className='font-medium text-gray-900 dark:text-white'>{user.name}</div>
                  <div className='text-gray-500 dark:text-gray-400'>{user.email}</div>
                  <div className='text-gray-500 dark:text-gray-400'>
                    {user.company && `${user.company} • `}
                    Status: {user.status}
                  </div>
                </div>
              )}
            </div>
          </div>

          <div className='flex justify-end space-x-3'>
            <button
              type='button'
              onClick={onClose}
              className='btn-secondary'
              aria-label='Cancel action'
            >
              {cancelText}
            </button>
            <button
              ref={confirmButtonRef}
              type='button'
              onClick={onConfirm}
              className={isDestructive ? 'btn-danger' : 'btn-primary'}
              aria-label={`${confirmText} action`}
            >
              {confirmText}
            </button>
          </div>
        </div>
      </div>
    );
  }
);

ConfirmationModal.displayName = 'ConfirmationModal';
