import React, { forwardRef, TextareaHTMLAttributes } from 'react';
import { FieldError } from 'react-hook-form';

type TextAreaProps = {
  label: string;
  id: string;
  error?: FieldError;
  required?: boolean;
} & TextareaHTMLAttributes<HTMLTextAreaElement>;

export const TextArea = forwardRef<HTMLTextAreaElement, TextAreaProps>(
  ({ label, id, error, required = false, className = '', rows = 3, ...props }, ref) => {
    return (
      <div>
        <label htmlFor={id} className="block text-sm font-medium text-gray-700">
          {label} {required && '*'}
        </label>
        <textarea
          ref={ref}
          id={id}
          rows={rows}
          className={`mt-1 block w-full rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm ${
            error ? 'border-red-500' : 'border-gray-300'
          } ${className}`}
          {...props}
        />
        {error && <p className="mt-1 text-sm text-red-600">{error.message}</p>}
      </div>
    );
  }
);

TextArea.displayName = 'TextArea';
