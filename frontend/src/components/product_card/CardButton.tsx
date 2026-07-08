import type { ButtonHTMLAttributes } from 'react';

const baseClassName =
  'cursor-pointer rounded px-3 py-2 text-[0.9rem] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600';

const variantClassNames = {
  primary: 'mt-auto border border-transparent bg-blue-600 text-white',
  secondary: 'border border-neutral-300 bg-white text-neutral-700',
  notify: 'mt-auto border border-blue-600 bg-white text-blue-600',
} as const;

export type CardButtonVariant = keyof typeof variantClassNames;

interface CardButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: CardButtonVariant;
}

export function CardButton({
  variant = 'primary',
  className,
  type = 'button',
  ...props
}: CardButtonProps) {
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
