import React, { forwardRef, SelectHTMLAttributes } from 'react';
import { FieldError } from 'react-hook-form';

type SelectProps = {
  label: string;
  id: string;
  error?: FieldError;
  required?: boolean;
  children: React.ReactNode;
} & SelectHTMLAttributes<HTMLSelectElement>;

export const Select = forwardRef<HTMLSelectElement, SelectProps>(
  ({ label, id, error, required = false, className = '', children, ...props }, ref) => {
    return (
      <div>
        <label htmlFor={id} className="block text-sm font-medium text-gray-700">
          {label} {required && '*'}
        </label>
        <select
          ref={ref}
          id={id}
          className={`mt-1 block w-full rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm ${
            error ? 'border-red-500' : 'border-gray-300'
          } ${className}`}
          {...props}
        >
          {children}
        </select>
        {error && <p className="mt-1 text-sm text-red-600">{error.message}</p>}
      </div>
    );
  }
);

Select.displayName = 'Select';
