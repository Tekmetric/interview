import type { ButtonHTMLAttributes } from 'react';

const baseClassName =
  'cursor-pointer rounded px-3 py-2 text-sm focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600';

const variantClassNames = {
  primary: 'border border-transparent bg-blue-600 text-white',
  secondary: 'border border-neutral-300 bg-white text-neutral-700',
  tertiary: 'border border-blue-600 bg-white text-blue-600',
} as const;

export type ButtonVariant = keyof typeof variantClassNames;

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
}

export function Button({
  variant = 'primary',
  className,
  type = 'button',
  ...props
}: ButtonProps) {
  return (
    <button
      type={type}
      className={[baseClassName, variantClassNames[variant], className]
        .filter(Boolean)
        .join(' ')}
      {...props}
    />
  );
}
