import React from 'react';
import { twMerge } from 'tailwind-merge';

interface FormFieldProps {
  label: string;
  type?: 'text' | 'email' | 'tel' | 'select';
  value: string;
  onChange: (value: string) => void;
  onBlur?: () => void;
  error?: string;
  placeholder?: string;
  required?: boolean;
  options?: { value: string; label: string }[];
  className?: string;
  disabled?: boolean;
}

export const FormField: React.FC<FormFieldProps> = ({
  label,
  type = 'text',
  value,
  onChange,
  onBlur,
  error,
  placeholder,
  required = false,
  options,
  className = '',
  disabled = false,
}) => {
  const baseInputClasses = `form-input ${
    error ? 'border-red-500 focus:ring-red-500 dark:border-red-400' : ''
  } ${disabled ? 'bg-gray-100 dark:bg-gray-600 cursor-not-allowed' : ''}`;

  const fieldId = `${label.toLowerCase().replace(/\s+/g, '-')}-${Math.random().toString(36).substr(2, 9)}`;

  return (
    <div className={twMerge('space-y-1', className)}>
      <label htmlFor={fieldId} className='form-label'>
        {label}
        {required && (
          <span className='ml-1 status-error' aria-label='required'>
            *
          </span>
        )}
      </label>

      {type === 'select' && options ? (
        <select
          id={fieldId}
          value={value}
          onChange={e => !disabled && onChange(e.target.value)}
          onBlur={onBlur}
          disabled={disabled}
          className={baseInputClasses}
          aria-invalid={!!error}
          aria-describedby={error ? `${fieldId}-error` : undefined}
          required={required}
        >
          {options.map(option => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
      ) : (
        <input
          id={fieldId}
          type={type}
          value={value}
          onChange={e => !disabled && onChange(e.target.value)}
          onBlur={onBlur}
          placeholder={placeholder}
          disabled={disabled}
          className={baseInputClasses}
          aria-invalid={!!error}
          aria-describedby={error ? `${fieldId}-error` : undefined}
          required={required}
        />
      )}

      {error && (
        <p id={`${fieldId}-error`} className='form-error' role='alert' aria-live='polite'>
          {error}
        </p>
      )}
    </div>
  );
};
