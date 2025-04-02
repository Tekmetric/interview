import { ComponentPropsWithoutRef, forwardRef, ReactNode } from 'react';
import { twMerge } from 'tailwind-merge';

interface TextInputProps extends ComponentPropsWithoutRef<'input'> {
  name: string;
  label: string;
  autocomplete?: 'on' | 'off';
  inputClassName?: string;
  labelClassName?: string;
  icon?: ReactNode;
}

const TextInput = forwardRef<HTMLInputElement, TextInputProps>(
  (
    { className, icon, inputClassName, label, labelClassName, ...props },
    ref
  ) => {
    return (
      <div className={twMerge('relative', className)}>
        <div className="absolute top-0 h-12 text-gray-400">{icon}</div>
        <input
          type="text"
          {...props}
          ref={ref}
          placeholder=" "
          autoComplete={props.autocomplete ?? 'off'}
          id={props.name}
          className={twMerge(
            'peer h-12 w-full rounded-lg border border-gray-300 bg-white px-4 transition-colors focus:border-2 focus:border-green-500 focus:py-[15px] focus:outline-none',
            icon ? 'pl-[48px]' : '',
            inputClassName
          )}
        />
        <label
          htmlFor={props.name}
          className={twMerge(
            'absolute top-[-8px] left-[14px] cursor-text rounded-full bg-white px-1 text-xs text-gray-400 transition-all peer-placeholder-shown:top-[14px] peer-placeholder-shown:text-[16px] peer-placeholder-shown:text-gray-400 peer-focus:top-[-8px] peer-focus:left-[14px] peer-focus:text-xs peer-focus:text-green-500',
            icon
              ? `peer-placeholder-shown:left-[48px]`
              : `peer-placeholder-shown:left-[14px]`,
            labelClassName
          )}
        >
          {label}
        </label>
      </div>
    );
  }
);

TextInput.displayName = 'TextInput';

export default TextInput;
