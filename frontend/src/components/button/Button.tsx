import type { ButtonHTMLAttributes } from 'react';

const baseClassName =
  'cursor-pointer rounded px-3 py-2 text-sm focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-cta-focus disabled:cursor-not-allowed';

const variantClassNames = {
  primary:
    'border border-transparent bg-cta text-on-cta hover:bg-cta-hover disabled:bg-disabled-bg disabled:text-disabled-text',
  secondary:
    'border border-border-input bg-elevated text-text-secondary hover:bg-hover disabled:border-border disabled:bg-disabled-bg disabled:text-disabled-text',
  tertiary:
    'border border-cta bg-elevated text-cta hover:bg-hover disabled:border-border-input disabled:text-disabled-text',
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
