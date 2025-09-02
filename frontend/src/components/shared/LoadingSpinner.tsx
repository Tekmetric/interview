import React from 'react';

interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg';
  text?: string;
}

export const LoadingSpinner: React.FC<LoadingSpinnerProps> = React.memo(
  ({ size = 'md', text = 'Loading...' }) => {
    const sizeClasses = {
      sm: 'size-6',
      md: 'size-12',
      lg: 'size-16',
    };

    return (
      <div className='flex items-center justify-center py-12' role='status' aria-live='polite'>
        <div className='relative'>
          <div
            className={`${sizeClasses[size]} animate-spin rounded-full border-b-2 border-blue-600`}
            aria-hidden='true'
          ></div>
          <div
            className={`absolute inset-0 ${sizeClasses[size]} animate-ping rounded-full border-t-2 border-blue-200`}
            aria-hidden='true'
          ></div>
        </div>
        <span className='ml-3 text-gray-600 dark:text-gray-400'>{text}</span>
      </div>
    );
  }
);

LoadingSpinner.displayName = 'LoadingSpinner';
