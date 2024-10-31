import type { ButtonHTMLAttributes, PropsWithChildren } from 'react'

import { ButtonClassNames } from './styles'
import type { ButtonSize, ButtonVariant, ButtonWidth } from './types'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  width?: ButtonWidth
  size?: ButtonSize
  variant?: ButtonVariant
}

export const Button = ({
  children,
  type = 'button',
  width = 'default',
  size = 'medium',
  variant = 'primary',
  ...rest
}: PropsWithChildren<ButtonProps>): JSX.Element => (
  <button
    {...rest}
    type={type}
    className={ButtonClassNames({ width, size, variant })}
  >
    {children}
  </button>
)
