import type { ButtonHTMLAttributes, PropsWithChildren } from 'react'

import { ButtonClassNames } from './styles'
import type { ButtonSize, ButtonWidth } from './types'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  width?: ButtonWidth
  size?: ButtonSize
}

export const Button = ({
  children,
  type = 'button',
  width = 'default',
  size = 'medium',
  ...rest
}: PropsWithChildren<ButtonProps>): JSX.Element => (
  <button {...rest} type={type} className={ButtonClassNames({ width, size })}>
    {children}
  </button>
)
